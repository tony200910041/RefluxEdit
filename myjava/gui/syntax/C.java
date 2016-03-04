/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package myjava.gui.syntax;

public class C extends Language
{
	public C()
	{
		super("C");
	}
	
	@Override
	public String[] getKeywordList()
	{
		return new String[]{"auto","break","case","const","continue",
							"default","do","else","enum","extern",
							"for","goto","if","register","return",
							"sizeof","static","struct","switch","typedef",
							"union","volatile","while","true","false","NULL"};
	}
	
	@Override
	public String[] getPrimitiveList()
	{
		return new String[]{"char","short","int","long","float","double","signed","unsigned","void"};
	}
	
	@Override
	public String[] getOperatorList()
	{
		return new String[]{"printf","scanf","++","--","<<",">>","&","|"};
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
