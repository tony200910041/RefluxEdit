/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package myjava.util;

public class WordCounter
{
	private String str;
	private volatile boolean _continue = true;
	public WordCounter(String str)
	{
		super();
		this.str = str.trim();
	}
	
	public void stop()
	{
		this._continue = false;
	}
	
	public int count()
	{
		if (str.length() == 0)
		{
			return 0;
		}
		else
		{
			int countTotal = 0;
			for (int i=0; i<str.length(); i++)
			{
				if (_continue)
				{
					if (Character.isWhitespace(str.charAt(i)))
					{
						//find next non-whitespace character
						i++;
						while (Character.isWhitespace(str.charAt(i)))
						{
							i++;
							if (!_continue) return -1;
							if (i == str.length()) break;
						}
						//so now charAt(i) is not a whitespace, or i == str.length()
						countTotal++;
						continue;
					}
				}
				else return -1;
			}
			return countTotal+1;
		}
	}
}
