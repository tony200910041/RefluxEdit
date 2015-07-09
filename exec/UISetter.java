/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package exec;

import java.awt.*;
import javax.swing.*;
import myjava.gui.common.*;

public final class UISetter implements Resources
{
	/*
	 * amend Swing defaults
	 */
	public static void initialize()
	{
		UIDefaults defaults = UIManager.getLookAndFeelDefaults();
		defaults.put("OptionPane.messageFont", f13);
		defaults.put("OptionPane.buttonFont", f13);
		defaults.put("OptionPane.okButtonText", "OK");
		defaults.put("OptionPane.yesButtonText", "YES");
		defaults.put("OptionPane.noButtonText", "NO");
		defaults.put("ComboBox.background", Color.WHITE);
		defaults.put("MenuItem.acceleratorForeground", new Color(34,131,132));
		defaults.put("PopupMenu.border", bord1);
		defaults.put("Separator.foreground", Color.BLACK);
		defaults.put("ComboBox.font", f13);
		defaults.put("TextField.font", f13);
		defaults.put("Label.font", f13);
		defaults.put("TabbedPane.font", f13);
		defaults.put("RadioButton.font", f13);
		defaults.put("CheckBox.font", f13);
		defaults.put("Button.font", f13);
		defaults.put("TitledBorder.font", f13);
		defaults.put("Spinner.font", f13);
		defaults.put("PopupMenu.background", Color.WHITE);
		defaults.put("ToolTip.border", bord1);
		defaults.put("ToolTip.background", Color.WHITE);
		defaults.put("TabbedPane.focus", new Color(0,0,0,0));
		ToolTipManager.sharedInstance().setInitialDelay(100);
		ToolTipManager.sharedInstance().setDismissDelay(6500);
	}
	
	public static int getLookAndFeelTabHeight()
	{
		if (isWindows) return 14;
		else if (isNimbus) return 18;
		else return 18;
	}
}
