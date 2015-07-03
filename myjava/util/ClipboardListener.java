/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package myjava.util;

import java.awt.*;
import java.awt.datatransfer.*;

public abstract class ClipboardListener extends Thread implements ClipboardOwner
{
	private static final Clipboard clipBoard = Toolkit.getDefaultToolkit().getSystemClipboard();
	private Transferable t;
	public ClipboardListener()
	{
		super();
		this.setDaemon(true);
	}
	
	@Override
	public void run()
	{
		//initialization
		this.t = this.getContents();
		this.getOwnership();
		while (true)
		{
			try
			{
				this.sleep(Integer.MAX_VALUE);
			}
			catch (InterruptedException ex)
			{
				//pass
			}
		}
	}
	
	@Override
	public void lostOwnership(Clipboard c, Transferable t)
	{
		try
		{
			this.sleep(200);
			this.t = this.getContents();
			this.getOwnership();
			this.clipboardChanged(this.t);
		}
		catch (InterruptedException ex)
		{
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	protected abstract void clipboardChanged(Transferable t);
	
	private Transferable getContents()
	{
		return clipBoard.getContents(this);
	}
	
	private void getOwnership()
	{
		clipBoard.setContents(this.t, this);
	}
}
