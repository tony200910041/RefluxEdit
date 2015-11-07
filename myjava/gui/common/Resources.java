/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package myjava.gui.common;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

public interface Resources
{
	/**
	 * declares resources for convenience
	 */
	/*
	 * used across all components
	 * use name f13 for compatibility
	 * all are public static final 
	 * laf:
	 */
	String LAF = UIManager.getLookAndFeel().getName().toLowerCase();
	boolean isMetal = LAF.contains("metal");
	boolean isWindows = LAF.contains("windows");
	boolean isNimbus = LAF.contains("nimbus");
	/*
	 * "ctrl"/"meta"(command) key
	 */
	String osName = System.getProperty("os.name").toLowerCase();
	boolean isMac = osName.contains("mac");
	int OS_CTRL_MASK = isMac?ActionEvent.META_MASK:ActionEvent.CTRL_MASK;
	/*
	 * ui constants
	 */
	Font f13 = LAF.contains("windows")?(new JButton().getFont().deriveFont(12f)):(new Font(osName.contains("win")?"Microsoft Jhenghei":(isMac?new JButton().getFont().getName():"Arial"), Font.PLAIN, 13));
	LineBorder bord1 = new LineBorder(Color.BLACK, 1);
	LineBorder bord2 = new LineBorder(Color.LIGHT_GRAY, 1);
	BevelBorder raisedBorder = new BevelBorder(BevelBorder.RAISED);
}
