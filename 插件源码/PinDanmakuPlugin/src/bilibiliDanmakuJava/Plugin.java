package bilibiliDanmakuJava;


import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.jar.Manifest;

import net.sf.json.JSONObject;


public class Plugin
{
	public interface EventListener
	{

		public JSONObject savePreferences();

		public void loadPreferences(JSONObject json);

		public void connected(int id);

		public void disconnected(boolean err);

		public void viewer(int v);

		public void message(String m);

		public void onLoad();

		public void onUnload();

		public String getName();

		public String getName_ful();

		public void show();

		public void setMsgMethod(Method m,Object obj);

		public void setRequestSavePreMethod(Method m,Object obj);
	}

	public static Plugin main(URL me,ClassLoader mClassLoader)
	{
		return null;
	}

	JarURLConnection	jar;
	EventListener		listener;
	Manifest			manifest;
	ClassLoader			mClassLoader;
}