/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package myjava.gui.syntax;

public class Cpp extends Language
{
	public Cpp()
	{
		super("C++");
	}
	
	@Override
	public String[] getKeywordList()
	{
		return new String[]{"alignas","alignof","and","and_eq","asm",
							"auto","bitand","bitor","break","case",
							"catch","class","compl","concept","const",
							"constexpr","const_cast","continue","decltype",
							"default","delete","do","dymanic_cast","else",
							"enum","explicit","export","extern","false",
							"for","friend","goto","if","inline",
							"mutable","namespace","new","noexcept","not",
							"not_eq","nullptr","operator","or","or_eq",
							"private","protected","public","register","reinterpret_cast",
							"requires","return","sizeof","static","static_assert",
							"static_cast","struct","switch","template","this",
							"thread_local","throw","true","try","typedef",
							"typeid","typename","union","using","virtual",
							"volatile","while","xor","xor_eq",
							"override","final"};
	}
	
	@Override
	public String[] getPrimitiveList()
	{
		return new String[]{"bool","char","double","float","int","long","short","signed","unsigned","void","wchat_t"};
	}
	
	@Override
	public String[] getOperatorList()
	{
		return new String[]{"++","--","==","<<",">>","&","|","[","]","->"};
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
		return new String[]{"#"};
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
	public char[] getEscapeCharacterList()
	{
		return new char[]{'\\'};
	}
	
	@Override
	public boolean isNumber(String s)
	{
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
}
