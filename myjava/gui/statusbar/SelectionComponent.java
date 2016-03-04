/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package myjava.gui.statusbar;

import javax.swing.*;
import javax.swing.text.*;
import myjava.gui.*;

public class SelectionComponent extends StatusComponent
{
	private JLabel label = new MyLabel();
	public SelectionComponent(Tab tab)
	{
		super(tab);
		this.setComponent(label);
		this.update();
	}
	
	@Override
	public void update()
	{
		try
		{
			JTextArea textArea = this.tab.getTextArea();
			int caret = textArea.getCaretPosition();
			int line = textArea.getLineOfOffset(caret);
			int start = Utilities.getRowStart(textArea,caret);
			label.setText("Line " + line + "  Column: " + (caret-start));
			if (textArea.getSelectedText() != null)
			{
				label.setToolTipText(" selection length: " + (textArea.getSelectionEnd()-textArea.getSelectionStart()));
			}
			else
			{
				label.setToolTipText(null);
			}
		}
		catch (BadLocationException ex)
		{
			throw new InternalError(ex.getMessage());
		}
	}
	
	@Override
	public void postCaretUpdate()
	{
		this.update();
	}
}
