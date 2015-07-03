/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package myjava.util;

public class ClassCreator
{
	public static final int DEFAULT = 0;
	public static final int PUBLIC = 1;
	public static final int PROTECTED = 2;
	public static final int PRIVATE = 3;
	public static final int STATIC = 4;
	public static final int ALLMAN = 10;
	public static final int ONETBS = 11;
	private Class<?> superClass;
	private Class<?>[] interfaces;
	private boolean isSingleton;
	private boolean main;
	private boolean isGUI;
	private int access;
	private int indentation;
	private boolean isStatic;
	private String packageName;
	private String name;
	public ClassCreator()
	{
		super();
	}
	
	public void setSuperClass(Class<?> c)
	{
		this.superClass = c;
	}
	
	public void setInterfaces(Class<?>[] interfaces)
	{
		this.interfaces = interfaces;
	}
	
	public void setSingleton(boolean isSingleton)
	{
		this.isSingleton = isSingleton;
	}
	
	public void setCreateMain(boolean main)
	{
		this.main = main;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public void setPackage(String packageName)
	{
		if (packageName.isEmpty())
		{
			this.packageName = null;
		}
		else
		{
			this.packageName = packageName;
		}
	}
	
	public void setGUI(boolean isGUI)
	{
		this.isGUI = isGUI;
	}
	
	public void setAccess(int access)
	{
		this.access = access;
	}
	
	public void setIndentation(int indentation)
	{
		this.indentation = indentation;
	}
	
	public void setStatic(boolean isStatic)
	{
		this.isStatic = isStatic;
	}
	
	public String toClassString()
	{
		StringBuilder s = new StringBuilder("");
		if (packageName != null)
		{
			s.append("package " + packageName + ";\n\n");
		}
		if (isGUI)
		{
			s.append("import java.awt.*;\nimport java.awt.event.*;\nimport javax.swing.*;\n\n");
		}
		switch (this.access)
		{
			case 0:
			default:
			break;
			
			case 1:
			s.append("public ");
			break;
			
			case 2:
			s.append("protected ");
			break;
			
			case 3:
			s.append("private ");
			break;
		}
		if (this.isStatic)
		{
			s.append("static ");
		}
		s.append("class " + this.name + " ");
		if (superClass != null)
		{
			s.append("extends " + superClass.getSimpleName() + " ");
		}
		if (interfaces != null)
		{
			s.append("implements ");
			for (int i=0; i<interfaces.length; i++)
			{
				s.append(interfaces[i].getSimpleName());
				if (i != (interfaces.length-1))
				{
					s.append(", ");
				}
			}
		}
		switch (indentation)
		{
			/*
			 * Allman
			 * 
			 */
			case 10:
			s.append("\n{\n");
			if (this.isSingleton)
			{
				s.append("	private static final " + name + " INSTANCE = new " + name + "();\n"); 
				s.append("	private " + name + "()\n");
				s.append("	{\n");
				s.append("	}\n\n");
				s.append("	public static " + name + " getInstance()\n");
				s.append("	{\n");
				s.append("		return INSTANCE;\n");
				s.append("	}\n\n");
			}
			if (this.main)
			{
				s.append("	public static void main(String[] args)\n");
				s.append("	{\n");
				if (this.isGUI)
				{
					s.append("		SwingUtilities.invokeLater(new Runnable()\n");
					s.append("		{\n");
					s.append("			@Override\n");
					s.append("			public void run()\n");
					s.append("			{\n");
					s.append("			}\n"); 
					s.append( "		});\n");
				}
				s.append("	}\n");
			}		
			s.append("}\n");
			break;
			
			/*
			 * 1TBS
			 * 
			 */
			case 11:
			s.append(" {\n");
			if (this.isSingleton)
			{
				s.append("	private static final " + name + " INSTANCE = new " + name + "();\n"); 
				s.append("	private " + name + "() {\n");
				s.append("	}\n\n");
				s.append("	public static " + name + " getInstance() {\n");
				s.append("		return INSTANCE;\n");
				s.append("	}\n\n");
			}
			if (this.main)
			{
				s.append("	public static void main(String[] args) {\n");
				if (this.isGUI)
				{
					s.append("		SwingUtilities.invokeLater(new Runnable() {\n" + 
							 "			@Override\n" +
							 "			public void run() {\n" + 
							 "			}\n" + 
							 "		});\n");
				}
				s.append("	}\n");
			}		
			s.append("}\n");
			break;
		}
		return s.toString();
	}
}
