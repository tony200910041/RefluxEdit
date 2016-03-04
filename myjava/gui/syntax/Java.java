/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package myjava.gui.syntax;

public class Java extends Language
{
	public Java()
	{
		super("Java");
	}
	
	@Override
	public String[] getKeywordList()
	{
		return new String[]{"abstract","assert","break","byte","case",
							"catch","class","const","continue","default",
							"do","else","enum","extends","final",
							"finally","for","goto","if","implements",
							"import","instanceof","interface","native","new",
							"package","private","protected","public","return",
							"static","strictfp","super","switch","synchronized",
							"this","throw","throws","transient","try",
							"volatile","while","true","false","null"};
	}
	
	@Override
	public String[] getPrimitiveList()
	{
		return new String[]{"boolean","byte","short","char","int","long","float","double","void"};
	}
	
	@Override
	public char[] getStringDelimiter()
	{
		return new char[]{'\"'};
	}
	
	@Override
	public char[] getCharDelimiter()
	{
		return new char[]{'\''};
	}
	
	@Override
	public String[] getLineCommentStart()
	{
		return new String[]{"//"};
	}
	
	@Override
	public String[] getPreprocessStart()
	{
		return new String[]{"@"};
	}
	
	@Override
	public String[] getCommentStart()
	{
		return new String[]{"/*"};
	}
	
	@Override
	public String[] getCommentEnd()
	{
		return new String[]{"*/"};
	}
	
	@Override
	public String[] getOperatorList()
	{
		return new String[]{"++","--","^","&","|","<<",">>","[]"};
	}
	
	@Override
	public boolean isNumber(String s)
	{
		if (hasSuffix(s))
		{
			s = s.substring(0,s.length()-1);
		}
		if (hasSuffix(s))
		{
			return false;
		}
		try
		{
			Double.parseDouble(s);
			return true;
		}
		catch (Exception ex)
		{
			return false;
		}
	}
	
	@Override
	public char[] getEscapeCharacterList()
	{
		return new char[]{'\\'};
	}
	
	private boolean hasSuffix(String s)
	{
		String lowerS = s.toLowerCase();
		return lowerS.endsWith("f")||lowerS.endsWith("d")||lowerS.endsWith("l");
	}
	
	private boolean isPlainInteger(String s)
	{
		return s.matches("[0-9]+");
	}
}
