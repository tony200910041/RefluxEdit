/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package myjava.gui;

import javax.swing.plaf.basic.*;

public class StopFloatingToolBarUI extends BasicToolBarUI
{
	public StopFloatingToolBarUI()
	{
		super();
	}
	
	@Override
	public boolean isFloating()
	{
		return false;
	}
}
