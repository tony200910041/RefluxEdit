/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package myjava.gui.option;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.*;
import exec.*;
import myjava.gui.*;
import static exec.SourceManager.*;

public class LookAndFeelTab extends OptionTab
{
	//Look and Feel
	private JComboBox<UIManager.LookAndFeelInfo> lafBox = new JComboBox<>(UIManager.getInstalledLookAndFeels());
	private String originalLAFSetting = getConfig0("LAF");
	//ribbon
	private JCheckBox ribbon = new MyCheckBox("Use Ribbon UI", RefluxEdit.isRibbon);
	private boolean originalRibbonSetting = RefluxEdit.isRibbon;
	private boolean originalMenuBarSetting = getBoolean0("apple.laf.useScreenMenuBar");
	private JCheckBox nativeMenuBar = new MyCheckBox("Use native menu bar", originalMenuBarSetting);
	//file chooser
	private JRadioButton swingFileChooser = new MyRadioButton("Swing",false);
	private JRadioButton systemFileChooser = new MyRadioButton("System",false);
	private MyButtonGroup fileChooserGroup = new MyButtonGroup(swingFileChooser,systemFileChooser);
	//constructor
	public LookAndFeelTab()
	{
		super(new FlowLayout(FlowLayout.LEFT), "Look and Feel");
		//Look and Feel, ribbon
		JPanel in = new JPanel(new GridLayout(2,1,0,0));
		JPanel laf = MyPanel.wrap(new MyLabel("Look and Feel: "),lafBox,new MyLabel("   "),ribbon,nativeMenuBar);
		laf.setBorder(new TitledBorder("Look and Feel"));
		in.add(laf);
		//iterate through comobobox to select
		if (originalLAFSetting != null)
		{
			switch (originalLAFSetting)
			{
				case "Default":
				originalLAFSetting = UIManager.getCrossPlatformLookAndFeelClassName();
				break;
				
				case "System":
				originalLAFSetting = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
				break;
				
				case "Nimbus":
				originalLAFSetting = "javax.swing.plaf.nimbus.NimbusLookAndFeel";
				break;
			}
		}
		LookAndFeel currentLAF = UIManager.getLookAndFeel();
		if (currentLAF != null)
		{
			String name = currentLAF.getName();
			for (int i=0; i<lafBox.getItemCount(); i++)
			{
				if (lafBox.getItemAt(i).getName().equals(name))
				{
					lafBox.setSelectedIndex(i);
					break;
				}
			}
		}
		lafBox.setBackground(Color.WHITE);
		lafBox.setRenderer(new DefaultListCellRenderer()
		{
			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus)
			{
				JLabel label = (JLabel)super.getListCellRendererComponent(list,value,index,isSelected,cellHasFocus);
				if (value instanceof UIManager.LookAndFeelInfo)
				{
					UIManager.LookAndFeelInfo info = (UIManager.LookAndFeelInfo)value;
					label.setText(info.getName());
				}
				return label;
			}
		});
		//file chooser
		JPanel chooser = MyPanel.wrap(swingFileChooser,systemFileChooser);
		chooser.setBorder(new TitledBorder("File chooser"));
		in.add(chooser);
		this.add(in);
		(("Java").equals(getConfig0("ChooserStyle"))?swingFileChooser:systemFileChooser).setSelected(true);
	}
	
	@Override
	public void onExit()
	{
		//Look and Feel
		String lafValue = ((UIManager.LookAndFeelInfo)(lafBox.getSelectedItem())).getClassName();
		setConfig("LAF", lafValue);
		//ribbon
		boolean newRibbon = ribbon.isSelected();
		setConfig("isRibbon", newRibbon+"");
		//menubar
		boolean newMenuBar = nativeMenuBar.isSelected();
		setConfig("apple.laf.useScreenMenuBar", newMenuBar+"");
		//file chooser
		setConfig("ChooserStyle",swingFileChooser.isSelected()?"Java":"System");
		//finally
		if ((newRibbon != originalRibbonSetting)||(!lafValue.equals(originalLAFSetting))||(originalMenuBarSetting != newMenuBar))
		{
			JOptionPane.showMessageDialog(parent, "The Look and Feel will be changed after restart.", "Done", JOptionPane.INFORMATION_MESSAGE);
		}
	}
}
