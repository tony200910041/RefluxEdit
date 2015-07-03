package myjava.gui;

import javax.swing.*;
import java.io.*;

public class FileLabel extends MyLabel
{
	public FileLabel()
	{
		super(" ");
	}
	
	public void setFile(File file)
	{
		if (file != null)
		{
			String path = file.getPath();
			if (path.length() > 50)
			{
				path = path.substring(0,25) + "..." + path.substring(path.length()-25);
			}
			this.setText("Current file: " + path);
		}
		else
		{
			this.setText(" ");
		}
	}
}
