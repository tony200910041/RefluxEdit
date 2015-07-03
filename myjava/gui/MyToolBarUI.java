/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package myjava.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.plaf.basic.*;

public class MyToolBarUI extends BasicToolBarUI
{
	private boolean forceStopFloating = false;
	MyToolBarUI()
	{
		super();
	}
	
	@Override
	public boolean isFloating()
	{
		if (forceStopFloating) return false;
		else return super.isFloating();
	}
	
	public void stopFloating()
	{
		this.forceStopFloating = true;
	}
}
