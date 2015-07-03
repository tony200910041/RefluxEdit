package myjava.util;

public class MutableBoolean
{
	private boolean b;
	public MutableBoolean(boolean b)
	{
		this.b = b;
	}
	
	public void set(boolean b)
	{
		this.b = b;
	}
	
	public boolean get()
	{
		return this.b;
	}
	
	public String toString()
	{
		return b+"";
	}
}
