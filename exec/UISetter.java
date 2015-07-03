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
		UIManager.put("OptionPane.messageFont", f13);
		UIManager.put("OptionPane.buttonFont", f13);
		UIManager.put("OptionPane.okButtonText", "OK");
		UIManager.put("OptionPane.yesButtonText", "YES");
		UIManager.put("OptionPane.noButtonText", "NO");
		UIManager.put("ComboBox.background", Color.WHITE);
		UIManager.put("MenuItem.acceleratorForeground", new Color(34,131,132));
		UIManager.put("PopupMenu.border", bord1);
		UIManager.put("Separator.foreground", Color.BLACK);
		UIManager.put("ComboBox.font", f13);
		UIManager.put("TextField.font", f13);
		UIManager.put("Label.font", f13);
		UIManager.put("TabbedPane.font", f13);
		UIManager.put("RadioButton.font", f13);
		UIManager.put("CheckBox.font", f13);
		UIManager.put("Button.font", f13);
		UIManager.put("TitledBorder.font", f13);
		UIManager.put("Spinner.font", f13);
		UIManager.put("PopupMenu.background", Color.WHITE);
		UIManager.put("ToolTip.border", bord1);
		UIManager.put("ToolTip.background", Color.WHITE);
		ToolTipManager.sharedInstance().setInitialDelay(100);
		ToolTipManager.sharedInstance().setDismissDelay(6500);
	}
}
