/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package myjava.gui.statusbar;

import java.awt.*;
import javax.swing.*;
import java.util.*;
import java.lang.reflect.*;
import myjava.gui.*;
import static exec.SourceManager.*;

public class StatusBar extends JPanel implements Comparator<StatusComponent>
{
	private Tab tab;
	private java.util.SortedSet<StatusComponent> components;
	public StatusBar(Tab tab)
	{
		super(new GridBagLayout());
		this.tab = tab;
		this.components = new TreeSet<>(this);
	}
	
	public void update()
	{
		components.clear();
		this.rearrange();
	}
	
	public void rearrange()
	{
		try
		{
			this.removeAll();
		}
		catch (Exception ex)
		{
			//pass
		}
		this.updateSet();
		this.addComponents();
		this.revalidate();
		this.repaint();
	}
	
	private void updateSet()
	{
		//add component to list
		display(components,CountComponent.class,getBoolean0("statusBar.showCount"),tab);
		display(components,SelectionComponent.class,getBoolean0("statusBar.showCaretLocation"),tab);
		display(components,FileComponent.class,tab.getFile()!=null,tab);
	}
	
	private void addComponents()
	{
		//add component to statusBar
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(0,2,0,2);
		c.gridy = 0;
		c.weightx = 0;
		c.gridx = 0;
		this.add(Box.createRigidArea(new Dimension(15,20)),c);
		for (StatusComponent comp: components)
		{
			c.gridx++;
			if (comp.fillHorizontal())
			{
				c.weightx = 1;
				c.fill = GridBagConstraints.HORIZONTAL;
			}
			else
			{
				c.weightx = 0;
				c.fill = GridBagConstraints.NONE;
			}
			this.add(comp,c);
		}
		c.gridx++;
		c.weightx = 0;
		c.fill = GridBagConstraints.NONE;
		this.add(Box.createRigidArea(new Dimension(15,20)),c);
	}
	
	public java.util.SortedSet<StatusComponent> getComponentList()
	{
		return this.components;
	}
	
	@Override
	public int compare(StatusComponent s1, StatusComponent s2)
	{
		java.util.List<? extends Class<?>> classList = Arrays.asList(CountComponent.class,SelectionComponent.class,FileComponent.class);
		return classList.indexOf(s1.getClass())-classList.indexOf(s2.getClass());
	}
	
	public void updateAll()
	{
		for (StatusComponent comp: this.components)
		{
			comp.update();
		}
	}
	
	public void postCaretUpdate()
	{
		for (StatusComponent comp: this.components)
		{
			comp.postCaretUpdate();
		}
	}
	
	public void postDocumentUpdate()
	{
		for (StatusComponent comp: this.components)
		{
			comp.postDocumentUpdate();
		}
	}
	
	private static <T> boolean hasElement(Collection<T> collection, Class<? extends T> _class)
	{
		for (T element: collection)
		{
			if (element.getClass() == _class)
			{
				return true;
			}
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	private static <T> void display(Collection<T> collection, Class<? extends T> _class, boolean ensure, Object... args)
	{
		if ((!hasElement(collection,_class))&&ensure)
		{
			try
			{
				Constructor<?> constructor = _class.getDeclaredConstructor(fromObjects(args));
				collection.add((T)(constructor.newInstance(args)));
			}
			catch (NoSuchMethodException|InstantiationException|IllegalAccessException|InvocationTargetException ex)
			{
				throw new InternalError(ex.getMessage());
			}
		}
		else if (!ensure)
		{
			for (T element: collection)
			{
				if (element.getClass() == _class)
				{
					collection.remove(element);
					return;
				}
			}
		}
	}
	
	private static Class<?>[] fromObjects(Object... args)
	{
		Class<?>[] classes = new Class<?>[args.length];
		for (int i=0; i<args.length; i++)
		{
			classes[i] = args[i].getClass();
		}
		return classes;
	}
}
