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
