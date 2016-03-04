/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

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
	/**
	 * Creates a new FileWatcher which listens to changes of the given file.
	 * 
	 * @param  file  a file whose changes have to be listened
	 * @see  WatchService
	 * @see  WatchKey
	 */
	public FileWatcher(File file)
	{
		super();
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
	
	/**
	 * Returns a WatchKey object representing the registration of the file specified in this watcher.
	 * Note that an InternalError is thrown when the registration fails.
	 * 
	 * @return  a WatchKey representing the registration of the file specified in the watcher.
	 */
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
	
	/**
	 * Enables or disables the watcher. The watcher will not generate WatchEvents if it is disabled.
	 * 
	 * @param  If true, the watcher is enabled. If false, the watcher is disabled.
	 */
	public void setEnabled(boolean isEnabled)
	{
		this.isEnabled = isEnabled;
	}
	
	/**
	 * Loop continuously in the background to detect file changes.
	 * This method should not be called directly.
	 */
	@Override
	public void run()
	{
		while (true)
		{
			//infinite loop
			WatchKey k;
			try
			{
				k = service.take();
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
	
	/**
	 * Closes the watcher. Upon return this watch becomes invalid.
	 * This method has no effect if this watcher has already been closed.
	 */
	@Override
	public void close()
	{
		this.key.cancel();
	}
	
	/**
	 * Determines whether this watcher is valid.
	 * A FileWatcher is valid upon creation and remains until it is closed. 
	 * 
	 * @return  true if this watcher is valid; false if not
	 */
	public boolean isValid()
	{
		return this.key.isValid();
	}
	
	//called when file is changed
	public abstract void fileChanged(WatchEvent<?> ev);
}
