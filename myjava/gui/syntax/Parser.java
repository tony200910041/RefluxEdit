/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package myjava.gui.syntax;

import javax.swing.*;
import javax.swing.text.*;
import java.util.*;
import java.util.concurrent.*;
import myjava.gui.*;
import myjava.util.*;
import static myjava.util.Utilities.*;

public class Parser
{
	private Language lang;
	private Set<Token> tokenSet = new ConcurrentSkipListSet<>();
	private String[] keywords, primitives, operators, lineCommentStarts, preprocessStarts, commentStarts, commentEnds;
	private char[] stringDelimiters, charDelimiters, escapeCharacters;
	private volatile boolean isParsing = false;
	private Set<Parser.ParseListener> parseListenerSet = Collections.newSetFromMap(new ConcurrentHashMap<Parser.ParseListener,Boolean>());
	public Parser(Language lang)
	{
		super();
		this.setLanguage(lang);
	}
	
	@Override
	public boolean equals(Object o)
	{
		if (o instanceof Parser)
		{
			Parser parser = (Parser)o;
			return (parser.lang == this.lang)&&(parser.tokenSet.equals(this.tokenSet));
		}
		else return false;
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(lang,tokenSet);
	}
	
	public final void setLanguage(Language lang)
	{
		this.lang = lang;
		this.keywords = lang.getKeywordList();
		this.primitives = lang.getPrimitiveList();
		this.operators = lang.getOperatorList();
		this.lineCommentStarts = lang.getLineCommentStart();
		this.preprocessStarts = lang.getPreprocessStart();
		this.stringDelimiters = lang.getStringDelimiter();
		this.charDelimiters = lang.getCharDelimiter();
		this.commentStarts = lang.getCommentStart();
		this.commentEnds = lang.getCommentEnd();
		this.escapeCharacters = lang.getEscapeCharacterList();
	}
	
	public Language getLanguage()
	{
		return this.lang;
	}
	
	public void addParseListener(Parser.ParseListener listener)
	{
		this.parseListenerSet.add(listener);
	}
	
	public void replaceOnNewThread(final int pos, final int len, final int newLength, final StringView view)
	{
		this.isParsing = true;
		Thread thread = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				replace0(pos,len,newLength,view);
			}
		});
		thread.start();	
	}
	
	public void replace(int pos, int len, int newLength, StringView view)
	{
		this.isParsing = true;
		replace0(pos,len,newLength,view);
	}
	
	private synchronized void replace0(int pos, int len, int newLength, StringView view)
	{
		try
		{
			StringView s = view.getText(pos,newLength);
			/*
			 * we assume that the start/end of the string is at the first /last character of a line
			 * deal with token initially after "len"
			 */
			for (Token token: new TreeSet<>(tokenSet))
			{
				if ((token.off() >= pos)&&(token.off()+token.length() <= pos+len))
				{
					tokenSet.remove(token);
				}
				else if (token.off() >= (pos+len))
				{
					token.moveOff(newLength-len);
				}
			}
			/* 
			 * check whether the replace section lies inside comments entirely;
			 * if so, do nothing but comment length simply shifts
			 */
			for (Token token: tokenSet)
			{
				if (token.getType() == Token.Type.MULTI_LINE_COMMENT)
				{
					if ((token.off() <= pos+minLength(commentStarts))&&(token.off()+token.length()-minLength(commentEnds) >= pos+len))
					{
						token.moveLength(-(pos+len-newLength));
						return;
					}
				}
			}
			out:
			for (int i=pos; i<pos+newLength; i++)
			{
				char c = s.charAt(i-pos);
				if (Character.isDigit(c))
				{
					if ((i == 0)||(!isLetter(view.getText(i-1,1).charAt(0))))
					{
						int lineEnd = view.getLineEndOffset(view.getLineOfOffset(i));
						for (int j=i+1; j<=lineEnd; j++)
						{
							StringView line = view.getText(i,j-i);
							if (!lang.isNumber(line.toString()))
							{
								/*
								 * i.e. included a non-number at j-1
								 * check if the end is linked to a letter or a digit; if so, just continue; if not, parse as number
								 */
								if (isLetterOrDigit(view.getText(j-1,1).charAt(0)))
								{
									//find next symbol, then continue
									while (j < lineEnd)
									{
										if (!isSymbol(view.getText(j,1).charAt(0)))
										{
											j++;
										}
										else break;
									}
									//character at j is a symbol, or lineEnd
									i = j-1;
									continue out;
								}
								else
								{
									tokenSet.add(new Token(Token.Type.NUMBER,i,j-1-i));
									i = j-1;
									continue out;
								}
							}
							if (j == lineEnd)
							{
								tokenSet.add(new Token(Token.Type.NUMBER,i,lineEnd-i));
								i = lineEnd;
								continue out;
							}
						}
					}
					else
					{
						//letter linked to digit, find next symbol
						while (i < pos+newLength)
						{
							if (!isLetterOrDigit(view.getText(i,1).charAt(0)))
							{
								i++;
							}
						}
						//i is a letter or a digit
						i--;
						continue out;
					}
				}
				else if (isLetter(c))
				{
					for (int j=i+1; j<=pos+newLength; j++)
					{
						if ((j == pos+newLength)||(!isLetterOrDigit(s.charAt(j-pos))))
						{
							StringView fragment = s.substring(i-pos,j-pos);
							if (equal(fragment,primitives))
							{
								tokenSet.add(new Token(Token.Type.PRIMITIVE,i,j-i));
								i = j-1;
								continue out;
							}
							if (equal(fragment,keywords))
							{
								tokenSet.add(new Token(Token.Type.KEYWORD,i,j-i));
								i = j-1;
								continue out;
							}
							i = j-1;
							continue out;
						}
					}
				}
				else if (c != '\n')
				{
					//check for operator
					if (isSymbol(c))
					{
						for (int j=i; j<=pos+newLength; j++)
						{
							StringView fragment = s.substring(i-pos,j-pos);
							if (equal(fragment,operators))
							{
								tokenSet.add(new Token(Token.Type.OPERATOR,i,j-i));
								i = j-1;
								continue out;
							}
						}
					}
					int lineEnd = view.getLineEndOffset(view.getLineOfOffset(i));
					//check for multi-line comment start
					multiLineCommentStart:
					for (String commentStart: commentStarts)
					{
						if (view.getText(i,lineEnd-i).startsWith(commentStart))
						{
							//look for next commentEnd
							for (int start=i+commentStart.length(); start<view.length(); start++)
							{
								StringView lineLocal = view.getText(start,view.getLineEndOffset(view.getLineOfOffset(start))-start);
								for (String commentEnd: commentEnds)
								{
									if (lineLocal.startsWith(commentEnd))
									{
										int tokenStart = i;
										int tokenLength = start+commentEnd.length()-i;
										remove(tokenStart,tokenStart+tokenLength);
										tokenSet.add(new Token(Token.Type.MULTI_LINE_COMMENT,tokenStart,tokenLength));
										i = start+commentEnd.length()-1;
										continue out;
									}
								}
							}
							break multiLineCommentStart;
						}
					}
					//check for multi-line comment end
					multiLineCommentEnd:
					for (String commentEnd: commentEnds)
					{
						if (view.getText(i,lineEnd-i).startsWith(commentEnd))
						{
							//look for last commentEnd, then from there, look for next commentStart
							StringView beforeText = view.getText(0,i);
							int lastCommentEnd = -1;
							inFor:
							for (String commentEnd2: commentEnds)
							{
								lastCommentEnd = beforeText.lastIndexOf(new StringView(commentEnd2));
								if (lastCommentEnd != -1)
								{
									break inFor;
								}
							}
							lastCommentEnd = Math.max(0,lastCommentEnd);
							StringView afterText = view.getText(lastCommentEnd,i-lastCommentEnd);
							int startIndex = -1;
							for (String commentStart: commentStarts)
							{
								startIndex = afterText.indexOf(new StringView(commentStart));
								if (startIndex != -1)
								{
									startIndex += lastCommentEnd;
									break;
								}
							}
							if (startIndex != -1)
							{
								int tokenStart = startIndex;
								int tokenLength = i+commentEnd.length()-startIndex;
								remove(tokenStart,tokenStart+tokenLength);
								tokenSet.add(new Token(Token.Type.MULTI_LINE_COMMENT,tokenStart,tokenLength));
								break multiLineCommentEnd;
							}
						}
					}
					//check for single-line comment
					for (String lineCommentStart: lineCommentStarts)
					{
						if (view.getText(i,lineEnd-i).startsWith(lineCommentStart))
						{
							remove(i,lineEnd);
							tokenSet.add(new Token(Token.Type.SINGLE_LINE_COMMENT,i,lineEnd-i));
							i = lineEnd-1;
							continue out;
						}
					}
					//check for preprocess comment
					for (String preprocessStart: preprocessStarts)
					{
						if (view.getText(i,lineEnd-i).startsWith(preprocessStart))
						{
							remove(i,lineEnd);
							tokenSet.add(new Token(Token.Type.PREPROCESS,i,lineEnd-i));
							i = lineEnd-1;
							continue out;
						}
					}
					//check for string delimiter
					if (equal(c,stringDelimiters))
					{
						//look for next string delimiter
						//remove escaped ones (replace them with arbitrary characters)
						//String line = view.getText(i+1,lineEnd-(i+1)).replace("\\"+c,"  ");
						StringView line = new StringView(removeEscapeCharacter(view.getText(i+1,lineEnd-(i+1)).toString(), c));
						int index = line.indexOf(new StringView(Character.toString(c)));
						if (index > -1)
						{
							//we have to +2 to include the open delimiters
							tokenSet.add(new Token(Token.Type.STRING,i,index+2));
							i += index+1;
							continue out;
						}
						else
						{
							//string not closed, so skip the whole line
							i = lineEnd;
							continue out;
						}
					}
					if (equal(c,charDelimiters))
					{
						//a character's length must equal 1 or 6 (in '\u0000' format) (0: arbitrary digit)
						//so first check that line is in the form '?'
						//charAt(lineEnd-1) is the close delimiter with possible largest offset
						//charAt(lineEnd-3) is the open delimiter with possible largest offset
						if (i <= lineEnd-3)
						{
							StringView line = view.getText(i,lineEnd-i);
							if (line.charAt(2) == c)
							{
								char charContent = line.charAt(1);
								if (!equal(charContent,escapeCharacters))
								{
									tokenSet.add(new Token(Token.Type.CHARACTER,i,3));
									i += 2;
									continue out;
								}
							}
							else
							{
								//then check for escape character
								char maybeEscape = line.charAt(1);
								if (equal(maybeEscape,escapeCharacters))
								{
									//look for next matching escape character
									for (int j=3; j<lineEnd-i; j++)
									{
										//charAt(i) is open delimiter
										//charAt(i+1) is escape character
										//charAt(i+2) must be something "escaped"
										//so start looping at (i+3)
										char maybeClose = line.charAt(j);
										if (maybeClose == c)
										{
											//found
											tokenSet.add(new Token(Token.Type.CHARACTER,i,j+1));
											i += j;
											continue out;
										}
									}
								}
							}
							//char not closed, or closed improperly, so skip the whole line
							i = lineEnd;
							continue out;
						}
						else
						{
							//char not closed, so skip the whole line
							i = lineEnd;
							continue out;
						}
					}
				}
			}
		}
		catch (Exception ex)
		{
			throw new InternalError(ex.getMessage());
		}
		finally
		{
			this.isParsing = false;
			for (ParseListener parseListener: parseListenerSet)
			{
				parseListener.parseFinished();
			}
			parseListenerSet.clear();
			MainPanel.getSelectedTab().getLayer().repaint();
		}
	}
	
	public Set<Token> getTokenSet(int pos, int len)
	{
		Set<Token> returnSet = new TreeSet<>();
		for (Token token: tokenSet)
		{
			int off = token.off();
			int length = token.length();
			if (isBetween(off,pos,pos+len)||isBetween(off+length,pos,pos+len))
			{
				returnSet.add(token);
			}
		}
		return returnSet;
	}
	
	public Set<Token> getTokenSet()
	{
		return new TreeSet<>(tokenSet);
	}
	
	public boolean isParsing()
	{
		return this.isParsing;
	}
	
	public Token getCommentAt(int off)
	{
		return this.getTokenAt(off,Token.Type.MULTI_LINE_COMMENT);
	}
	
	public Token getTokenAt(int off, Token.Type... type)
	{
		for (Token token: tokenSet)
		{
			if (equal(token.getType(),type))
			{
				int tokenOff = token.off();
				int tokenLength = token.length();
				if (isBetween(off,tokenOff,tokenOff+tokenLength))
				{
					return token;
				}
			}
		}
		return null;
	}
	
	public boolean isContained(int off, Token.Type... type)
	{
		return getTokenAt(off,type) != null;
	}
	
	private void remove(int from, int to)
	{
		for (Iterator<Token> it = tokenSet.iterator(); it.hasNext(); )
		{
			Token token = it.next();
			int off = token.off();
			int len = token.length();
			if (isBetween(off+len,from,to)||isBetween(off,from,to))
			{
				it.remove();
			}
		}
	}
	
	private static boolean isLetter(char c)
	{
		return (Character.isLetter(c)&&(c != ' '))||(c == '_');
	}
	
	private static boolean isDigit(char c)
	{
		return ("0123456789").contains(Character.toString(c));
	}
	
	private static boolean isPlainNaturalNumber(String s)
	{
		for (char c: s.toCharArray())
		{
			if (!isDigit(c)) return false;
		}
		return true;
	}
	
	private static boolean isLetterOrDigit(char c)
	{
		return isLetter(c)||isDigit(c);
	}
	
	private static boolean isSymbol(char c)
	{
		return (!Character.isLetter(c))&&(!Character.isDigit(c))&&(c != ' ')&&(c != '\t')&&(c != '\n')&&(c != '\r');
	}
	
	private static boolean equal(Token.Type t, Token.Type[] types)
	{
		for (Token.Type type: types)
		{
			if (t.equals(type)) return true;
		}
		return false;
	}
	
	private static boolean equal(StringView s, CharSequence[] strs)
	{
		for (CharSequence str: strs)
		{
			if (s.contentEquals(str)) return true;
		}
		return false;
	}
	
	private static boolean equal(char c, char[] chars)
	{
		for (char _c: chars)
		{
			if (c == _c) return true;
		}
		return false;
	}
	
	private static int maxLength(CharSequence[] tokens)
	{
		int maxLength = -1;
		for (CharSequence token: tokens)
		{
			maxLength = Math.max(maxLength,token.length());
		}
		return maxLength;
	}
	
	private static int minLength(CharSequence[] tokens)
	{
		if (tokens.length == 0) return -1;
		else
		{
			int minLength = tokens[0].length();
			for (int i=1; i<tokens.length; i++)
			{
				minLength = Math.min(tokens[i].length(),minLength);
			}
			return minLength;
		}
	}
	
	private boolean isBetween(int x, int start, int end)
	{
		int smaller = Math.min(start,end);
		int larger = Math.max(start,end);
		return (start <= x)&&(x < end);
	}
	
	private String removeEscapeCharacter(String s, char c)
	{
		return removeEscapeCharacter(s,new char[]{c});
	}
	
	private String removeEscapeCharacter(String s, char[] _c)
	{
		for (char c: _c)
		{
			s = s.replace("\\"+c,"aa"); //just replaced by arbitrary characters, len=2
		}
		return s;
	}
	
	private String removeEscapeCharacter(String s)
	{
		return removeEscapeCharacter(s,this.escapeCharacters);
	}
	
	private String removeCommentDelimiterInString(String s)
	{
		Map<Integer,Integer> indices = indexOf(s,this.commentStarts);
		indices.putAll(indexOf(s,this.commentEnds));
		for (Map.Entry<Integer,Integer> entry: indices.entrySet())
		{
			int off = entry.getKey();
			int len = entry.getValue();
			if (isBetweenString(s,off))
			{
				s = s.substring(0,off) + createString(len) + s.substring(off+len);
			}
		}
		return s;
	}
	
	private boolean isBetweenString(String s, int off)
	{
		int start = Math.max(0,s.substring(0,off).lastIndexOf("\n"));
		String line = removeEscapeCharacter(s.substring(start,off));
		int countTotal = 0;
		for (char stringDelimiter: stringDelimiters)
		{
			countTotal += count(line,Character.toString(stringDelimiter),false,true);
		}
		return isOdd(countTotal);
	}
	
	private Map<Integer,Integer> indexOf(String s, String[] _find)
	{
		//(key=index, value=len)
		Map<Integer,Integer> map = new HashMap<>();
		for (String find: _find)
		{
			int findStringLength = find.length();
			for (Integer i: indexOf(s,find))
			{
				map.put(i,findStringLength);
			}
		}
		return map;
	}
	
	private List<Integer> indexOf(String s, String find)
	{
		List<Integer> list = new ArrayList<>();
		int removed = 0;
		int index;
		while ((index = s.indexOf(find)) != -1)
		{
			list.add(removed+index);
			int len = index+find.length();
			s = s.substring(len);
			removed += len;
		}
		return list;
	}
	
	private String createString(int len)
	{
		char[] c = new char[len];
		Arrays.fill(c,'a');
		return new String(c);
	}
	
	private boolean isOdd(int x)
	{
		return x%2 == 1;
	}
	
	public static interface ParseListener
	{
		void parseFinished();
	}
}
