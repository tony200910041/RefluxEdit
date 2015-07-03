/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package myjava.gui.common;

import java.awt.*;
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
	boolean isWindows = LAF.contains("windows");
	boolean isNimbus = LAF.contains("nimbus");
	/*
	 * ui constants
	 */
	Font f13 = LAF.contains("windows")?(new Font("PMingLiU", Font.PLAIN, 12)):(new Font("Microsoft Jhenghei", Font.PLAIN, 13));
	LineBorder bord1 = new LineBorder(Color.BLACK, 1);
	LineBorder bord2 = new LineBorder(Color.LIGHT_GRAY, 1);
	BevelBorder raisedBorder = new BevelBorder(BevelBorder.RAISED);
}
