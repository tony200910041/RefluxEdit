/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package myjava.util;

public class StringView implements CharSequence
{
	private String s;
	private int off, len;
	public StringView(String s)
	{
		super();
		this.s = s;
		this.off = 0;
		this.len = s.length();
	}
	
	public StringView(String s, int off, int len)
	{
		super();
		if (off<0)
		{
			throw new IllegalArgumentException("off = " + off + " < 0");
		}
		else if ((off+len)>s.length())
		{
			throw new IllegalArgumentException("off + len = " + (off+len) + " > " + s.length());
		}
		else if (len<0)
		{
			throw new IllegalArgumentException("len = " + len + " < 0");
		}
		this.s = s;
		this.off = off;
		this.len = len;
	}
	
	@Override
	public char charAt(int x)
	{
		if (x < 0)
		{
			throw new IllegalArgumentException("offset " + x + " < 0");
		}
		else if (x >= this.len)
		{
			throw new IllegalArgumentException("offset " + x + " >= " + this.len);
		}
		else
		{
			return this.s.charAt(x+this.off);
		}
	}
	
	@Override
	public int length()
	{
		return this.len;
	}
	
	@Override
	public StringView subSequence(int start, int end)
	{
		return new StringView(this.s,off+start,end-start);
	}
	
	@Override
	public String toString()
	{
		return this.s.substring(this.off,this.off+this.len);
	}
	
	@Override
	public boolean equals(Object o)
	{
		return (o instanceof StringView)?contentEquals((StringView)o):false;
	}
	
	public boolean contentEquals(CharSequence seq)
	{
		if (seq.length() != this.length())
		{
			return false;
		}
		else
		{
			for (int i=0; i<this.length(); i++)
			{
				if (this.charAt(i) != seq.charAt(i))
				{
					return false;
				}
			}
			return true;
		}
	}
	
	public StringView substring(int start)
	{
		return this.subSequence(start,this.length());
	}
	
	public StringView substring(int start, int end)
	{
		return this.subSequence(start,end);
	}
	
	public StringView getText(int start, int length)
	{
		return new StringView(this.s,this.off+start,length);
	}
	
	public boolean startsWith(CharSequence seq)
	{
		if (this.length() < seq.length())
		{
			return false;
		}
		else
		{
			for (int i=0; i<seq.length(); i++)
			{
				if (this.charAt(i) != seq.charAt(i)) return false;
			}
			return true;
		}
	}
	
	public int indexOf(StringView view)
	{
		int viewLength = view.length();
		for (int i=0; i<=this.length()-viewLength; i++)
		{
			if (this.getText(i,viewLength).contentEquals(view))
			{
				return i;
			}
		}
		return -1;
	}
	
	public int indexOf(StringView s, int fromIndex)
	{
		int index = new StringView(this.s,fromIndex,this.length()-fromIndex).indexOf(s);
		if (index == -1) return -1;
		else return fromIndex + index;
	}
	
	public int lastIndexOf(StringView view)
	{
		int viewLength = view.length();
		for (int i=this.len-viewLength; i>=0; i--)
		{
			if (this.getText(i,viewLength).contentEquals(view))
			{
				return i;
			}
		}
		return -1;
	}
	
	public int lastIndexOf(StringView s, int fromIndex)
	{
		int index = new StringView(this.s,fromIndex,this.length()-fromIndex).lastIndexOf(s);
		if (index == -1) return -1;
		else return fromIndex + index;
	}
	
	public int getLineOfOffset(int off)
	{
		String before = this.subSequence(0,off).toString();
		return (off-before.replace("\n","").length());
	}
	
	public int getLineStartOffset(int line)
	{
		//i.e. the offset just behind the (line)th "\n"
		if (line == 0) return 0;
		else
		{
			int count = 0;
			int offset = 0;
			while (count != line)
			{
				if (this.charAt(offset)=='\n') count++;
				offset++;
				if (offset > this.len) throw new IllegalArgumentException("invalid line number " + line);
			}
			return offset;
		}
	}
	
	public int getLineEndOffset(int line)
	{
		//i.e. the offset just behind the (line+1)th "\n"
		int count = 0;
		int offset = 0;
		while (count < (line+1))
		{
			if (this.charAt(offset)=='\n') count++;
			offset++;
			if (offset == this.len)
			{
				return offset;
			}
		}
		return offset-1;
	}
}
