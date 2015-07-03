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
	 */
	String LAF = UIManager.getLookAndFeel().getName().toLowerCase();
	Font f13 = LAF.contains("windows")?(new Font("PMingLiU", Font.PLAIN, 12)):(new Font("Microsoft Jhenghei", Font.PLAIN, 13));
	LineBorder bord1 = new LineBorder(Color.BLACK, 1);
	LineBorder bord2 = new LineBorder(Color.LIGHT_GRAY, 1);
	BevelBorder raisedBorder = new BevelBorder(BevelBorder.RAISED);
}
