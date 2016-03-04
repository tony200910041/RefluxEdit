/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package myjava.gui.syntax;

import java.util.*;

public abstract class Language
{
	private static final Language JAVA = new Java();
	private static final Language _C = new C();
	private static final Language CPP = new Cpp();
	private String name;
	protected Language(String name)
	{
		super();
		this.name = name;
	}
	
	public String getName()
	{
		return this.name;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if (o instanceof Language)
		{
			return this.name.equals(((Language)o).name);
		}
		else return false;
	}
	
	@Override
	public int hashCode()
	{
		return this.name.hashCode();
	}
	
	public abstract String[] getKeywordList();
	public abstract String[] getPrimitiveList();
	public abstract String[] getOperatorList();
	public abstract char[] getStringDelimiter();
	public abstract char[] getCharDelimiter();
	public abstract String[] getLineCommentStart();
	public abstract String[] getPreprocessStart();
	public abstract String[] getCommentStart();
	public abstract String[] getCommentEnd();
	public abstract char[] getEscapeCharacterList();
	public abstract boolean isNumber(String s);
	public boolean isNumber(CharSequence s)
	{
		return isNumber(s.toString());
	}
	
	public static Language forName(String s)
	{
		switch (s.toLowerCase())
		{
			case "java":
			return JAVA;
			
			case "c":
			return _C;
			
			case "cpp":
			return CPP;
			
			default:
			return null;
		}
	}
}
