package myjava.util;

import java.awt.*;
import javax.swing.*;
import java.io.*;
import java.net.*;
import exec.*;
import myjava.gui.*;

public class VersionChecker implements VersionConstants
{
	public static void showUpdateDialog(final Frame parent, boolean showUpToDate)
	{
		/*
		 * Uses information in VersionConstants to compare with the latest version information
		 */
		BufferedReader reader = null;
		try
		{
			//load information
			URL update = new URL("http://refluxedit.sourceforge.net/update/newversion.txt");
			reader = new BufferedReader(new InputStreamReader(update.openStream()));
			StringBuilder builder = new StringBuilder();
			String buffer;
			while ((buffer=reader.readLine()) != null)
			{
				builder.append(buffer+"\n");
			}
			final String[] fragment = builder.toString().split("\n");
			//compare
			double newVersionNumber = getVersionNumber(fragment);
			double currentVersionNumber = getVersionNumber(new String[]{VERSION_NO, BETA_STRING, BETA_NO, REV_NO});
			if (newVersionNumber > currentVersionNumber)
			{
				StringBuilder des = new StringBuilder();
				for (int i=5; i<fragment.length; i++)
				{
					des.append(fragment[i]+"\n");
				}
				final String description = des.toString();
				SwingUtilities.invokeLater(new Runnable()
				{
					/*
					 * show new version information
					 */
					@Override
					public void run()
					{
						int option = JOptionPane.showConfirmDialog(parent, "There is a new version of RefluxEdit."
														+ "\nVersion: " + fragment[0] + (fragment[1].equals("final")?"":(fragment[1] + fragment[2]
														+ (Byte.parseByte(fragment[3])==0?"":("rev"+fragment[3]))))
														+ "\nPublished: " + fragment[4]
														+ "\n\n" + description
														+ "\nWould you like to download it?", "Update found", JOptionPane.YES_NO_OPTION);
						if (option == JOptionPane.YES_OPTION)
						{
							try
							{
								Desktop.getDesktop().browse(new URI("https://sourceforge.net/projects/refluxedit/"));
							}
							catch (Exception ex)
							{
							}
						}
					}
				});
			}
			else if (showUpToDate)
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					/*
					 * show up-to-date
					 */
					@Override
					public void run()
					{
						JOptionPane.showMessageDialog(parent, "Your RefluxEdit is up to date.", "Check update", JOptionPane.INFORMATION_MESSAGE);
					}
				});
			}
		}
		catch (Exception ex)
		{
			ExceptionDialog.exception(ex);
		}
		finally
		{
			try
			{
				if (reader != null) reader.close();
			}
			catch (IOException ex)
			{
			}
		}
	}
						
	private static double getVersionNumber(String[] fragment)
	{
		//fragments like {"2","1","1"}
		double version_no = Double.parseDouble(fragment[0]);
		version_no+=getTestNumber(fragment[1])/10.0;
		version_no+=(fragment[2].isEmpty()?0:Double.parseDouble(fragment[2]))/100;
		version_no+=(fragment[3].isEmpty()?0:Double.parseDouble(fragment[3]))/1000;
		return version_no;
	}
	
	private static byte getTestNumber(String str)
	{
		byte beta_version;
		switch (str)
		{
			case "alpha":
			beta_version=0;
			break;
			
			case "beta":
			beta_version=1;
			break;
			
			case "final":
			case "":
			default:
			beta_version=2;
			break;
		}
		return beta_version;
	}
}
