/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package myjava.gui.syntax;

import java.util.*;

public enum Bracket
{
	OPEN_ROUND("("), CLOSE_ROUND(")"), OPEN_SQUARE("["), CLOSE_SQUARE("]"), OPEN_CURLY("{"), CLOSE_CURLY("}");
	private static final NavigableSet<Bracket> orderedSet = new TreeSet<>(EnumSet.allOf(Bracket.class));
	private String value;
	Bracket(String value)
	{
		this.value = value;
	}
	
	public String getValue()
	{
		return this.value;
	}
	
	public boolean isOpen()
	{
		return this.ordinal()%2==0;
	}
	
	public Bracket opposite()
	{
		return this.isOpen()?orderedSet.higher(this):orderedSet.lower(this);
	}
	
	public static Bracket of(CharSequence s)
	{
		for (Bracket bracket: Bracket.values())
		{
			if (bracket.getValue().contentEquals(s))
			{
				return bracket;
			}
		}
		return null;
	}
	
	public static boolean isBracket(CharSequence s)
	{
		return of(s) != null;
	}
}
