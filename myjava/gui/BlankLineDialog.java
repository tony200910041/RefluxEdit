/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package myjava.gui;

import java.awt.*;
import javax.swing.*;

public class BlankLineDialog
{
	public static Boolean showDialog(Window parent)
	{
		MyCheckBox checkBox = new MyCheckBox("Also delete lines containing whitespaces or tabs only", true);
		String[] options = new String[]{"Start", "Cancel"};
		int chosen = JOptionPane.showOptionDialog(parent, new JComponent[]{new MyLabel("Delete blank lines?"), checkBox}, "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
		return chosen == JOptionPane.YES_OPTION?Boolean.valueOf(checkBox.isSelected()):null;
	}
}
