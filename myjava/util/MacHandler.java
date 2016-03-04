/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
 
package myjava.util;

import java.awt.*;
import java.awt.image.*;
import java.lang.reflect.*;
import exec.*;

public class MacHandler
{
	public static void initialize()
	{
		try
		{
			register();
		}
		catch (Throwable ex)
		{
			//just skip if any Throwable is thrown
		}
	}
	
	static void register() throws Exception
	{
		Class<?> utilClass = Class.forName("com.apple.mrj.MRJApplicationUtils");
		Class<?> implClass = Class.forName("myjava.util.ControllerImpl");
		Method aboutMethod = utilClass.getMethod("registerAboutHandler",Class.forName("com.apple.mrj.MRJAboutHandler"));
		Method prefsMethod = utilClass.getMethod("registerPrefsHandler",Class.forName("com.apple.mrj.MRJPrefsHandler"));
		Method quitMethod = utilClass.getMethod("registerQuitHandler",Class.forName("com.apple.mrj.MRJQuitHandler"));
		//
		Object impl = implClass.newInstance();
		aboutMethod.invoke(null,impl);
		prefsMethod.invoke(null,impl);
		quitMethod.invoke(null,impl);
		//
		Class<?> app = Class.forName("com.apple.eawt.Application");
		Method get = app.getMethod("getApplication");
		Method disableSuddenTermination = app.getMethod("disableSuddenTermination");
		disableSuddenTermination.invoke(get.invoke(null));
		/*
		Class<?> quitStrategy = Class.forName("com.apple.eawt.QuitStrategy");
		Method valueOf = quitStrategy.getMethod("valueOf",String.class);
		Method setQuitStrategy = app.getMethod("setQuitStrategy");*/
	}
	
	/*static void setDockImage()
	{
		try
		{
			Image dockImage = SourceManager.icon("APPICON").getImage().getScaledInstance(40,40,Image.SCALE_FAST);;
			BufferedImage image48 = new BufferedImage(48,48,BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2d = image48.createGraphics();
			g2d.drawImage(dockImage,4,4,null);
			Class<?> c = Class.forName("com.apple.eawt.Application");
			Method m1 = c.getMethod("getApplication");
			Method m2 = c.getMethod("setDockIconImage",Image.class);
			m2.invoke(m1.invoke(null),image48);
		}
		catch (Throwable ex)
		{
			ex.printStackTrace();
		}
	}*/
}
