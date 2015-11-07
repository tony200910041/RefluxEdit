/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package myjava.gui;

import javax.swing.*;

public final class ExceptionDialog
{
	private static JFrame parent = null;
	public static void setGlobalParent(JFrame parent)
	{
		ExceptionDialog.parent = parent;
	}
	
	public static void error(String str)
	{
		JOptionPane.showMessageDialog(parent,"Error!\n" + str, "Error", JOptionPane.ERROR_MESSAGE);
	}
	
	public static void exception(Throwable ex)
	{
		error("Exception type: " + ex.getClass().getName() + "\nException message: " + ex.getMessage());
	}
}
