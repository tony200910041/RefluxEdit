package myjava.io;

/**
 * file association detector
 */

import java.io.*;
import java.util.*;
import java.nio.file.*;
import myjava.gui.*;
import myjava.io.*;

public abstract class FADetector
{
	/*
	 * must separate into three files
	 * otherwise there are conflicts when detecting file changes
	 */
	private static final File FILE_INSTANCE = new File(getFAFilePath(),"REFLUXEDIT_DO_NOT_REMOVE_1");
	private static final File FILE_PATH = new File(getFAFilePath(),"REFLUXEDIT_DO_NOT_REMOVE_2");
	private static final File FILE_RESPONSE = new File(getFAFilePath(),"REFLUXEDIT_DO_NOT_REMOVE_3");
	static
	{
		if (!FILE_INSTANCE.exists())
		{
			try (PrintWriter writer = new PrintWriter(FILE_INSTANCE))
			{
			}
			catch (IOException ex)
			{
			}
		}
		if (!FILE_PATH.exists())
		{
			try (PrintWriter writer = new PrintWriter(FILE_PATH))
			{
			}
			catch (IOException ex)
			{
			}
		}
		if (!FILE_RESPONSE.exists())
		{
			try (PrintWriter writer = new PrintWriter(FILE_RESPONSE))
			{
			}
			catch (IOException ex)
			{
			}
		}
	}
	/*
	 * lock
	 */
	private Object respondLock = new Object();
	private volatile boolean isWaiting = false;
	/*
	 * 
	 */
	public FADetector()
	{
		super();
		FileWatcher responseWatcher = new FileWatcher(FILE_RESPONSE)
		{
			@Override
			public void fileChanged(WatchEvent ev)
			{
				synchronized(respondLock)
				{
					if (isWaiting)
					{
						respondLock.notify();
					}
					else
					{
						respond();
					}
				}
			}
		};
		FileWatcher pathWatcher = new FileWatcher(FILE_PATH)
		{
			@Override
			public void fileChanged(WatchEvent ev)
			{
				FADetector.this.hasNewFile();
			}
		};
	}
	
	public boolean hasInstance()
	{
		return ("true").equals(read(FILE_INSTANCE));
	}
	
	public void setInstance(boolean has)
	{
		write(FILE_INSTANCE, String.valueOf(has));
	}
	
	public void respond()
	{
		write(FILE_RESPONSE, "true");
	}
	
	public void requestResponse()
	{
		write(FILE_RESPONSE, "false");
	}
	
	public boolean getResponse()
	{
		return ("true").equals(read(FILE_RESPONSE));
	}
	
	public boolean awaitResponse(long timeout)
	{
		final Thread current = Thread.currentThread();
		Timer timer = new Timer();
		timer.schedule(new TimerTask()
		{
			@Override
			public void run()
			{
				current.interrupt();
			}
		}, timeout);
		synchronized(respondLock)
		{
			try
			{
				this.requestResponse();
				isWaiting = true;
				while (!getResponse())
				{
					respondLock.wait();
				}
				isWaiting = false;
				return true;
			}
			catch (InterruptedException ex)
			{
				isWaiting = false;
				return false;
			}
		}
	}
	
	public void setFile(File file)
	{
		write(FILE_PATH, file.getPath());
	}
	
	public File getFile()
	{
		try
		{
			Thread.currentThread().sleep(400); //ensure saved
		}
		catch (Exception ex)
		{
			//pass
		}
		String s = read(FILE_PATH);
		if (s != null)
		{
			return new File(s);
		}
		else return null;
	}
	
	public abstract void hasNewFile();
	
	private static String getFAFilePath()
	{
		try
		{			
			return (new File(FADetector.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath())).getParentFile().getPath();
		}
		catch (Exception ex)
		{
			throw new Error(ex);
		}
	}
	
	private static void write(File file, String text)
	{
		file.delete();
		try (PrintWriter writer = new PrintWriter(file,"UTF-8"))
		{
			writer.println("DO NOT REMOVE OR MODIFY: RefluxEdit auto-generated file used for file association detection");
			writer.println(text);
		}
		catch (Exception ex)
		{
			//wait and try again
			try
			{
				Thread.currentThread().sleep(400);
			}
			catch (InterruptedException ex2)
			{
			}
			finally
			{
				write(file, text);
			}
		}
	}
	
	private static String read(File file)
	{
		try (BufferedReader reader = new BufferedReader(new FileReader(file)))
		{
			reader.readLine(); //discard the first line
			return reader.readLine();
		}
		catch (Exception ex)
		{
			//wait and try again
			try
			{
				Thread.currentThread().sleep(400);
			}
			catch (InterruptedException ex2)
			{
			}
			finally
			{
				return read(file);
			}
		}
	}
}
