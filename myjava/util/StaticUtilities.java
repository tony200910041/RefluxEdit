/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package myjava.util;

import java.util.*;
import java.util.regex.*;
import java.io.*;
import static exec.SourceManager.*;

public final class StaticUtilities
{
	public static String toInvertCase(String str)
	{
		char c;
		char[] chars = str.toCharArray();
		for (int i=0; i<chars.length; i++)
		{
			c = chars[i];
			if (Character.isUpperCase(c))
			{
				chars[i] = Character.toLowerCase(c);
			}
			else if (Character.isLowerCase(c))
			{
				chars[i] = Character.toUpperCase(c);
			}
		}
		return new String(chars);
	}
	
	public static int toNumber(char letter)
	{
		switch (letter)
		{
			case ' ': return 0;
			case 'a': return 1;
			case 'b': return 2;
			case 'c': return 3;
			case 'd': return 4;
			case 'e': return 5;
			case 'f': return 6;
			case 'g': return 7;
			case 'h': return 8;
			case 'i': return 9;
			case 'j': return 10;
			case 'k': return 11;
			case 'l': return 12;
			case 'm': return 13;
			case 'n': return 14;
			case 'o': return 15;
			case 'p': return 16;
			case 'q': return 17;
			case 'r': return 18;
			case 's': return 19;
			case 't': return 20;
			case 'u': return 21;
			case 'v': return 22;
			case 'w': return 23;
			case 'x': return 24;
			case 'y': return 25;
			case 'z': return 26;
			//
			case 'A': return 27;
			case 'B': return 28;
			case 'C': return 29;
			case 'D': return 30;
			case 'E': return 31;
			case 'F': return 32;
			case 'G': return 33;
			case 'H': return 34;
			case 'I': return 35;
			case 'J': return 36;
			case 'K': return 37;
			case 'L': return 38;
			case 'M': return 39;
			case 'N': return 40;
			case 'O': return 41;
			case 'P': return 42;
			case 'Q': return 43;
			case 'R': return 44;
			case 'S': return 45;
			case 'T': return 46;
			case 'U': return 47;
			case 'V': return 48;
			case 'W': return 49;
			case 'X': return 50;
			case 'Y': return 51;
			case 'Z': return 52;
			//
			case '1': return 53;
			case '2': return 54;
			case '3': return 55;
			case '4': return 56;
			case '5': return 57;
			case '6': return 58;
			case '7': return 59;
			case '8': return 60;
			case '9': return 61;
			case '0': return 62;
			default: return 63;
		}
	}
	
	public static String toLetter(int a)
	{
		switch (a)
		{
			case 0: return "Space";
			case 1: return "a";
			case 2: return "b";
			case 3: return "c";
			case 4: return "d";
			case 5: return "e";
			case 6: return "f";
			case 7: return "g";
			case 8: return "h";
			case 9: return "i";
			case 10: return "j";
			case 11: return "k";
			case 12: return "l";
			case 13: return "m";
			case 14: return "n";
			case 15: return "o";
			case 16: return "p";
			case 17: return "q";
			case 18: return "r";
			case 19: return "s";
			case 20: return "t";
			case 21: return "u";
			case 22: return "v";
			case 23: return "w";
			case 24: return "x";
			case 25: return "y";
			case 26: return "z";
			//
			case 27: return "A";
			case 28: return "B";
			case 29: return "C";
			case 30: return "D";
			case 31: return "E";
			case 32: return "F";
			case 33: return "G";
			case 34: return "H";
			case 35: return "I";
			case 36: return "J";
			case 37: return "K";
			case 38: return "L";
			case 39: return "M";
			case 40: return "N";
			case 41: return "O";
			case 42: return "P";
			case 43: return "Q";
			case 44: return "R";
			case 45: return "S";
			case 46: return "T";
			case 47: return "U";
			case 48: return "V";
			case 49: return "W";
			case 50: return "X";
			case 51: return "Y";
			case 52: return "Z";
			//
			case 53: return "1";
			case 54: return "2";
			case 55: return "3";
			case 56: return "4";
			case 57: return "5";
			case 58: return "6";
			case 59: return "7";
			case 60: return "8";
			case 61: return "9";
			case 62: return "0";
			default: return "Others";
		}
	}
	
	public static char toChar(String unicode)
	{
		return (char)Integer.parseInt(unicode, 16);
	}
	
	public static int wordCount(String text)
	{
		if (text != null)
		{
			if (text.isEmpty())
			{
				return 0;
			}
			else
			{
				text = text.replace("\n"," ");
				ArrayList<String> list = new ArrayList<String>(Arrays.asList(text.split(" ")));
				while (list.remove("")) {}
				return list.size();
			}
		}
		else return 0;
	}
	
	public static int charCount(String text)
	{
		return text.replace("\n","").length();
	}
	
	public static String toUnicodeValue(char c)
	{
		String s = Integer.toHexString(c);
		int length = s.length();
		for (int i=1; i<5-length; i++)
		{
			s = "0" + s;
		}
		return "\\u" + s.toUpperCase();
	}
	
	public static String insertSpaces(String text)
	{
		//e.g. from "ABC" to "A B C"
		char[] chars = text.toCharArray();
		ArrayList<Character> tmpList = new ArrayList<>(chars.length*2);
		for (int i=0; i<chars.length; i++)
		{
			tmpList.add(chars[i]);
			boolean _this = (!Character.isSpaceChar(chars[i]))&&(chars[i] != '\t');
			boolean _next = true;
			if (i != chars.length-1)
			{
				_next = (!Character.isSpaceChar(chars[i+1]))&&(chars[i+1] != '\t');
			}
			if (_this&&_next) tmpList.add(' ');
		}
		StringBuilder builder = new StringBuilder(tmpList.size());
		for (Character c: tmpList)
		{
			builder.append(c);
		}
		return builder.toString();
	}
	
	public static String reverse(String text)
	{
		//e.g. from "ABC" to "CBA"
		char[] chars = text.toCharArray();
		ArrayList<Character> list = new ArrayList<>();
		for (int i=chars.length-1; i>=0; i--)
		{
			list.add(chars[i]);
		}
		int size = list.size();
		chars = new char[size];
		for (int i=0; i<size; i++)
		{
			chars[i] = list.get(i);
		}
		return new String(chars);
	}
	
	public static String getFileName(File file)
	{
		//e.g. from new File("C:/1.txt") to "1"
		String name = file.getName();
		if (name.contains("."))
		{
			return name.substring(0, name.lastIndexOf("."));
		}
		return name;
	}
	
	public static String getFileExtension(File file)
	{
		String name = file.getName();
		int lastIndex = name.lastIndexOf(".");
		if (lastIndex > -1)
		{
			return name.substring(lastIndex+1).toLowerCase();
		}
		return "";
	}
	
	public static String getCommand(String ext)
	{
		//must not return null
		String command = getConfig("Compile.command.default."+ext);
		if (command != null)
		{
			return command;
		}
		else
		{
			switch (ext)
			{
				case "c":
				return "gcc -o %a %f";
				
				case "cpp":
				return "g++ -o %a %f";
				
				case "pl":
				case "plx":				
				case "py":
				return "";
						
				case "java":
				return "javac -classpath %p %f";
				
				default:
				case "":
				return "";
			}
		}
	}
	
	public static String getRunCommand(String ext)
	{
		String runCommand = getConfig("Compile.runCommand.default."+ext);
		if (runCommand != null)
		{
			return runCommand;
		}
		else
		{
			switch (ext)
			{
				case "c":
				case "cpp":
				return "cd %p%n%a%nPAUSE%ndel \"%~f0\"";
				
				case "pl":
				case "plx":
				return "cd %p%nperl %f%nPAUSE%ndel \"%~f0\"";
				
				case "py":
				return "cd %p%npython %f%nPAUSE%ndel \"%~f0\"";
				
				case "java":
				return "cd %p%njava -classpath %p %a%nPAUSE%ndel \"%~f0\"";
				
				default:
				case "":
				return "";
			}
		}
	}
	
	public static int count(String text, String find, boolean useRegex, boolean isCaseSensitive)
	{
		if (!isCaseSensitive)
		{
			text = text.toLowerCase();
			find = find.toLowerCase();
		}
		if (useRegex)
		{
			Matcher matcher = Pattern.compile(find).matcher(text);
			int count = 0;
			while (matcher.find())
			{
				count++;
			}
			return count;
		}
		else
		{
			return (text.length()-text.replace(find,"").length())/find.length();
		}
	}
	
	public static String replace(String text, String find, String match, boolean useRegex, boolean isCaseSensitive)
	{
		if (useRegex)
		{
			if (isCaseSensitive)
			{
				return text.replaceAll(find,match);
			}
			else
			{
				return text.replaceAll("(?i)"+find,match);
			}
		}
		else
		{
			if (isCaseSensitive)
			{
				return text.replace(find,match);
			}
			else
			{
				return text.replaceAll("(?i)"+Matcher.quoteReplacement(find),match);
			}
		}
	}
	
	public static String unescape(String text) throws IOException
	{
		/*
		 * from \\ to \ and from \n to (newline)
		 */
		//write the expression to a ByteArrayOutputStream
		ByteArrayOutputStream output = null;
		output = new ByteArrayOutputStream();
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output));
		writer.write("key=" + text);
		writer.close();
		//now read the stream using Properties
		Properties prop = new Properties();
		ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
		prop.load(input);
		output.close();
		return prop.getProperty("key");
	}
	
	public static String escape(String text) throws IOException
	{		
		/*
		 * reverse: from (newline) to \n
		 */
		Properties prop = new Properties();
		prop.setProperty("key",text);
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		prop.store(output,null);
		//now read the byte[]
		ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
		output.close();
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		//discard the first line (date/time)
		reader.readLine();
		String s = reader.readLine();
		return s.substring(s.indexOf("key=")+4);
	}
}
