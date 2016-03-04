/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package myjava.gui.syntax;

import java.awt.*;
import javax.swing.*;
import java.util.*;
import java.util.concurrent.*;
import exec.*;
import static myjava.gui.syntax.Token.Type.*;

public abstract class Painter implements Comparable<Painter>, Cloneable
{
	private static final Painter DEFAULT_INSTANCE = new Painter("Default")
	{
		@Override
		public Color fromType(Token.Type type)
		{
			switch (type)
			{
				case MATCHED_BRACKET: return new Color(32,113,47,80);
				case UNMATCHED_BRACKET: return new Color(153,51,0,80);
				case KEYWORD: return new Color(0,0,255);
				case PRIMITIVE: return new Color(255,0,0);
				case NUMBER: return new Color(0,150,0);
				case OPERATOR: return new Color(150,150,150);
				case STRING: return new Color(255,153,0);
				case CHARACTER: return new Color(255,153,0);
				case SINGLE_LINE_COMMENT: 
				case MULTI_LINE_COMMENT: return new Color(100,20,20);
				case PREPROCESS: return new Color(67,125,120);
				default: return Color.BLACK;
			}
		}
	};
	private static Painter CURRENT_INSTANCE;
	private static SortedSet<Painter> PAINTER_SET = new ConcurrentSkipListSet<>(Collections.singleton(DEFAULT_INSTANCE));
	static
	{
		for (String key: SourceManager.keys())
		{
			String painterPrefix = "painter.userDefined.";
			if (key.startsWith(painterPrefix))
			{
				try
				{
					PAINTER_SET.add(Painter.create(key.substring(painterPrefix.length()),SourceManager.getConfig0(key)));
				}
				catch (Exception ex)
				{
					SourceManager.removeConfig0(key);
				}
			}
		}
		Painter current = getInstance(SourceManager.getConfig0("syntax.selectedPainter"));
		CURRENT_INSTANCE = current==null?DEFAULT_INSTANCE:current;
	}
	private String name;
	private Color matchedBracket, unmatchedBracket;
	protected Painter(String name)
	{
		super();
		this.name = name;
	}
	
	public static Painter create(String name, String data)
	{
		try
		{
			final String[] tokens = data.split(":");
			if (tokens.length != 11)
			{
				throw new IllegalArgumentException("token length != 11");
			}
			return new Painter(name)
			{
				private Color matchedBracket = toColor(tokens[0],true);
				private Color unmatchedBracket = toColor(tokens[1],true);
				private Color keyword = toColor(tokens[2]);
				private Color primitive = toColor(tokens[3]);
				private Color number = toColor(tokens[4]);
				private Color operator = toColor(tokens[5]);
				private Color string = toColor(tokens[6]);
				private Color character = toColor(tokens[7]);
				private Color singleLineComment = toColor(tokens[8]);
				private Color multiLineComment = toColor(tokens[9]);
				private Color preprocess = toColor(tokens[10]);
				@Override
				public Color fromType(Token.Type type)
				{
					switch (type)
					{
						case MATCHED_BRACKET: return matchedBracket;
						case UNMATCHED_BRACKET: return unmatchedBracket;
						case KEYWORD: return keyword;
						case PRIMITIVE: return primitive;
						case NUMBER: return number;
						case OPERATOR: return operator;
						case STRING: return string;
						case CHARACTER: return character;
						case SINGLE_LINE_COMMENT: return singleLineComment;
						case MULTI_LINE_COMMENT: return multiLineComment;
						case PREPROCESS: return preprocess;
						default: return Color.BLACK;
					}
				}
			};
		}
		catch (Exception ex)
		{
			throw new IllegalArgumentException("invalid data "+data,ex);
		}
	}
	
	public Painter newInstance()
	{
		return this.newInstance(this.getName());
	}
	
	public Painter newInstance(String name)
	{
		return new Painter(name)
		{
			@Override
			public Color fromType(Token.Type type)
			{
				return Painter.this.fromType(type);
			}
		};
	}
	
	public static Painter getCurrentInstance()
	{
		return CURRENT_INSTANCE;
	}
	
	public static Painter getDefaultInstance()
	{
		return DEFAULT_INSTANCE;
	}
	
	public static Painter getInstance(String name)
	{
		for (Painter p: PAINTER_SET)
		{
			if (p.getName().equals(name))
			{
				return p;
			}
		}
		return null;
	}
	
	public static void setCurrentInstance(Painter instance)
	{
		if (PAINTER_SET.contains(instance))
		{
			Painter.CURRENT_INSTANCE = instance;
		}
		else throw new IllegalArgumentException("invalid painter instance");
	}
	
	public static SortedSet<Painter> getPainters()
	{
		return Collections.unmodifiableSortedSet(PAINTER_SET);
	}
	
	public static void remove(Painter painter)
	{
		PAINTER_SET.remove(painter);
	}
	
	public static void removeAll()
	{
		PAINTER_SET.clear();
		PAINTER_SET.add(DEFAULT_INSTANCE);
	}
	
	public static void removeAll(Collection<? extends Painter> painters)
	{
		PAINTER_SET.removeAll(painters);
		PAINTER_SET.add(DEFAULT_INSTANCE);
	}
	
	public static void add(Painter painter)
	{
		PAINTER_SET.add(painter);
	}
	
	public static void addAll(Collection<? extends Painter> painters)
	{
		PAINTER_SET.addAll(painters);
	}
	
	public static boolean isValidPrompt(String s, Component relative)
	{
		for (Painter p: PAINTER_SET)
		{
			if (p.getName().equals(s))
			{
				JOptionPane.showMessageDialog(relative,"The name " + s + " has already been used.\nPlease use another one.","Error",JOptionPane.ERROR_MESSAGE);
				return false;
			}
		}
		return true;
	}
	
	private static Color toColor(String s)
	{
		return toColor(s,false);
	}
	
	private static Color toColor(String s, boolean autoAlpha)
	{
		Color c = new Color(Integer.parseInt(s));
		return autoAlpha?new Color(c.getRed(),c.getGreen(),c.getBlue(),80):c;
	}
	
	public String getName()
	{
		return this.name;
	}
	
	@Override
	public String toString()
	{
		return this.name;
	}
	
	@Override
	public int compareTo(Painter p)
	{
		return this.getName().compareTo(p.getName());
	}
	
	@Override
	public boolean equals(Object o)
	{
		return o instanceof Painter?this.getName().equals(((Painter)o).getName()):false;
	}
	
	@Override
	public int hashCode()
	{
		return this.getName().hashCode();
	}
	
	public String toWritableString()
	{
		return fromTypeInt(MATCHED_BRACKET)+":"+fromTypeInt(UNMATCHED_BRACKET)+":"+fromTypeInt(KEYWORD)+":"
			  +fromTypeInt(PRIMITIVE)+":"+fromTypeInt(NUMBER)+":"+fromTypeInt(OPERATOR)+":"
			  +fromTypeInt(STRING)+":"+fromTypeInt(CHARACTER)+":"+fromTypeInt(SINGLE_LINE_COMMENT)+":"
			  +fromTypeInt(MULTI_LINE_COMMENT)+":"+fromTypeInt(PREPROCESS);
	}
	
	private int fromTypeInt(Token.Type type)
	{
		return fromType(type).getRGB();
	}
	
	public abstract Color fromType(Token.Type type);
}
