/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package myjava.gui.syntax;

import java.awt.*;
import java.util.*;

public class Token implements Comparable<Token>
{
	private int off;
	private int len;
	private Type type;
	protected Token(Type type, int off, int len)
	{
		super();
		this.off = off;
		if (len < 0)
		{
			throw new IllegalArgumentException("Token length " + len + " < 0");
		}
		this.len = len;
		this.type = type;
	}
	
	public int off()
	{
		return this.off;
	}
	
	public int length()
	{
		return this.len;
	}
	
	public Type getType()
	{
		return this.type;
	}
	
	public void moveOff(int x)
	{
		this.off += x;
	}
	
	public void moveLength(int x)
	{
		this.len += x;
	}
	
	@Override
	public int compareTo(Token token)
	{
		return this.off-token.off;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if (o instanceof Token)
		{
			Token t = (Token)o;
			return (t.off == this.off)&&(t.len == this.len)&&(t.type == this.type);
		}
		else return false;
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(this.off,this.len,this.type);
	}
	
	@Override
	public String toString()
	{
		return this.type.toString() + ": [off=" + this.off + ", len=" + this.len + "]";
	}
	
	public static enum Type
	{
		KEYWORD, PRIMITIVE, NUMBER, OPERATOR, STRING, CHARACTER, SINGLE_LINE_COMMENT, MULTI_LINE_COMMENT, PREPROCESS, MATCHED_BRACKET, UNMATCHED_BRACKET
	}
}
