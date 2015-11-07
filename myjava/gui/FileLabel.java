/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

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
			this.setToolTipText(path);
			if (path.length() > 50)
			{
				path = path.substring(0,25) + "..." + path.substring(path.length()-25);
			}
			this.setText("Current file: " + path);
		}
		else
		{
			this.setText(" ");
			this.setToolTipText(null);
		}
	}
}
