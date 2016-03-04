/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
 
package myjava.util;

import java.util.*;
import java.io.*;
import exec.*;
import myjava.gui.option.*;
import com.apple.eawt.*;
import com.apple.mrj.*;

@SuppressWarnings("deprecation")
public class ControllerImpl implements MRJAboutHandler, MRJQuitHandler, MRJPrefsHandler, OpenFilesHandler
{
	@Override
	@SuppressWarnings("deprecation")
	public void handleAbout()
	{
		(new MyListener(16)).actionPerformed(null);
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public void handlePrefs()
	{
		OptionDialog.showDialog(RefluxEdit.getInstance());
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public void handleQuit()
	{
		RefluxEdit.getInstance().confirmClose();
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public void openFiles(AppEvent.OpenFilesEvent ev)
	{
		List<File> fileList = ev.getFiles();
		for (File file: fileList)
		{
			try
			{
				myjava.gui.MainPanel.openFileAndWait(file);
			}
			catch (Exception ex)
			{
				//pass
			}
		}
	}
}
