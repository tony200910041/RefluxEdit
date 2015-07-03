package MyJava;

public class MyFileFilter implements java.io.FileFilter
{
	@Override
	public boolean accept(java.io.File f)
	{
		return true;
	}
	
	public String getDescription()
	{
		return null;
	}
}
