package myjava.io;

import java.io.*;
import java.util.*;
import java.nio.file.*;
import myjava.gui.*;

public abstract class FileWatcher implements Runnable, AutoCloseable
{
	protected WatchService service;
	protected WatchKey key;
	private File file;
	private long lastModified;
	/*
	 * enabled
	 */
	private boolean isEnabled = true;
	/*
	 * constructor
	 */
	public FileWatcher(File file)
	{
		this.file = file;
		this.lastModified = file.lastModified();
		try
		{
			this.service = FileSystems.getDefault().newWatchService();
			this.key = this.getKey();
		}
		catch (Throwable ex)
		{
			//fail sliently
			throw new Error(ex);
		}
		Thread thread = new Thread(this);
		thread.setDaemon(true);
		thread.start();
	}
	
	protected WatchKey getKey()
	{
		try
		{
			return this.file.getParentFile().toPath().register(service, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
		}
		catch (Throwable ex)
		{
			//fail sliently
			throw new InternalError();
		}
	}
	
	public void setEnabled(boolean isEnabled)
	{
		this.isEnabled = isEnabled;
	}
	
	@Override
	public void run()
	{
		while (true)
		{
			//infinite loop
			WatchKey k;
			try
			{
				k = ((WatchService)service).take();
			}
			catch (InterruptedException ex)
			{
				ExceptionDialog.exception(ex);
				continue;
			}
			if (this.isEnabled)
			{
				outFor:
				for (WatchEvent<?> event: k.pollEvents())
				{
					File changed = ((Path)(event.context())).toFile();
					if (this.isEnabled)
					{
						if (changed.getName().equals(this.file.getName()))
						{
							long modified = this.file.lastModified();
							if (modified != this.lastModified)
							{
								if (this.isEnabled)
								{
									this.lastModified = modified;
									fileChanged(event);
								}
							}
						}
					}
					/*
					 * temporary solution
					 * for triggering both properties and fa
					 * actually deal with first event only
					 */
					break outFor;
				}
			}
			//must reset key at the end
			k.reset();
		}
	}
	
	@Override
	public void close()
	{
		this.key.cancel();
	}
	
	public boolean isValid()
	{
		return this.key.isValid();
	}
	
	//called when file is changed
	public abstract void fileChanged(WatchEvent ev);
}
