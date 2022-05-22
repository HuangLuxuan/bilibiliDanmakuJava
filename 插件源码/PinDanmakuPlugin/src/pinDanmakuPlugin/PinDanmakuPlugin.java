package pinDanmakuPlugin;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import net.sf.json.JSONObject;
import bilibiliDanmakuJava.Plugin;


public class PinDanmakuPlugin extends Plugin implements Plugin.EventListener,ActionListener
{
	static ClassLoader	cl;

	public static void main(String[] args)
	{
		JOptionPane.showMessageDialog(null, "这是Java版弹幕姬的附属插件，请在\"插件>添加\"中导入", "错误的打开方式", JOptionPane.ERROR_MESSAGE);
	}

	@SuppressWarnings("unchecked")
	public static Plugin main(URL me,ClassLoader mClassLoader)
	{
		try
		{
			cl = mClassLoader;
			while (cDanmaku == null)
			{
				try
				{
					cDanmaku = Class.forName("bilibiliDanmakuJava.Danmaku", false, cl);
				}
				catch (ClassNotFoundException e)
				{
					cl = cl.getParent();
				}
			}
			mgetMessage = cDanmaku.getDeclaredMethod("getMessage", JSONObject.class);
			mgetName = cDanmaku.getDeclaredMethod("getName", JSONObject.class);
			return new PinDanmakuPlugin();
		}
		catch (Exception e)
		{
			throw new RuntimeException("不支持的弹幕姬版本……请确认弹幕姬版本是否低于V1.5.1或者弹幕姬过新", e);
		}

	}

	JListExt<JSONObject>	danmakuList,pinedList;
	JFrame					jframe,pined;
	LinkedList<JSONObject>	danmaku		= new LinkedList<JSONObject>();
	ArrayList<JSONObject>	pinedDan	= new ArrayList<>();
	int						maxCount	= 500;
	JScrollPaneExt			jsp;
	int						pined_mouseX,pined_mouseY;

	public PinDanmakuPlugin()
	{
		jframe = new JFrame();
		jframe.setLocationByPlatform(true);
		jframe.setSize(240, 360);
		jframe.setTitle("选择置顶弹幕");
		danmakuList = new JListExt<JSONObject>();
		danmakuList.setCellRenderer(new DanmakuCellRender());
		danmakuList.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if (e.getClickCount() > 1)
				{
					int index = danmakuList.locationToIndex(e.getPoint());
					if (index < 0) return;
					JSONObject json = danmaku.get(index);
					if (pinedDan.contains(json))
						pinedDan.remove(json);
					else
						pinedDan.add(json);
					pinedList.setListData(pinedDan.toArray(new JSONObject[pinedDan.size()]));
					pinedList.invalidate();
				}
			}
		});
		jsp = new JScrollPaneExt(danmakuList, JScrollPaneExt.VERTICAL_SCROLLBAR_ALWAYS, JScrollPaneExt.HORIZONTAL_SCROLLBAR_NEVER);
		jframe.setLayout(new BorderLayout());
		jframe.add(jsp, BorderLayout.CENTER);

		JButton jb = new JButton("显示隐藏置顶弹幕窗口");
		jb.setActionCommand("pined_show_hide");
		jb.addActionListener(this);
		JPanel jp = new JPanel(new GridLayout(1, 2));
		jp.add(jb);
		jb = new JButton("设置置顶弹幕窗口大小");
		jb.setActionCommand("pined_size");
		jb.addActionListener(this);
		jp.add(jb);
		jframe.add(jp, BorderLayout.SOUTH);

		pined = new JFrame();
		pined.setLocationByPlatform(true);
		pined.setSize(240, 360);
		pined.setUndecorated(true);
		pined.setTitle("被置顶的弹幕");
		pined.setBackground(Color.black);
		pinedList = new JListExt<JSONObject>();
		pinedList.setCellRenderer(new DanmakuCellRender(Color.WHITE));
		pinedList.setBackground(new Color(0));
		pined.add(pinedList);
		pinedList.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if (e.getClickCount() > 1)
				{
					int index = pinedList.locationToIndex(e.getPoint());
					if (index < 0) return;
					pinedDan.remove(index);
					pinedList.setListData(pinedDan.toArray(new JSONObject[pinedDan.size()]));
				}
			}

			@Override
			public void mousePressed(MouseEvent e)
			{
				pined_mouseX = e.getX();
				pined_mouseY = e.getY();
			}

		});
		pinedList.addMouseMotionListener(new MouseMotionAdapter()
		{

			@Override
			public void mouseDragged(MouseEvent e)
			{
				int x1 = e.getXOnScreen();
				int y1 = e.getYOnScreen();
				int x = x1 - pined_mouseX;
				int y = y1 - pined_mouseY;
				pined.setLocation(x, y);
			}
		});
	}

	@SuppressWarnings("rawtypes")
	static Class	cDanmaku;
	static Method	mgetName,mgetMessage;

	public class DanmakuCellRender implements ListCellRenderer<JSONObject>
	{
		Color	foreground	= null;

		public DanmakuCellRender()
		{}

		public DanmakuCellRender(Color foreground)
		{
			this.foreground = foreground;
		}

		@Override
		public Component getListCellRendererComponent(JList<? extends JSONObject> list,JSONObject value,int index,boolean isSelected,boolean cellHasFocus)
		{
			String name = null,message = null;
			try
			{
				name = (String) mgetName.invoke(null, value);
				message = (String) mgetMessage.invoke(null, value);
			}
			catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
			{
				JLabelExt jl = new JLabelExt("ERROR:" + e.getLocalizedMessage() + "\nJSON:" + value.toString());
				jl.setForeground(Color.RED);
				return jl;
			}
			JLabelExt jl = new JLabelExt(name + ":" + message);
			if (foreground != null) jl.setForeground(foreground);
			return jl;
		}

	}

	int	width	= 240;
	int	height	= 360;

	@Override
	public JSONObject savePreferences()
	{
		JSONObject jo = new JSONObject();
		jo.put("width", width);
		jo.put("height", height);
		return jo;
	}

	@Override
	public void loadPreferences(JSONObject json)
	{
		try
		{
			width = json.getInt("width");
			height = json.getInt("height");
			pined.setSize(width, height);
		}
		catch (Exception e)
		{}
	}

	@Override
	public void connected(int id)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void disconnected(boolean err)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void viewer(int v)
	{
		// TODO Auto-generated method stub

	}

	static String	unprintableTypes[]	=
										{ "STOP_LIVE_ROOM_LIST", "ACTIVITY_EVENT", "ROOM_REAL_TIME_MESSAGE_UPDATE", "ROOM_RANK", "INTERACT_WORD", "LIVE_INTERACTIVE_GAME", "WIDGET_BANNER", "COMBO_SEND", "COMBO_END", "ONLINE_RANK_COUNT", "ONLINE_RANK_V2",
			"ENTRY_EFFECT", "HOT_RANK_CHANGED", "HOT_RANK_CHANGED_V2", "WATCHED_CHANGE" };

	static boolean isPrintable(JSONObject json)
	{
		String type = json.getString("cmd");
		for (String s : unprintableTypes)
			if (s.equals(type)) return false;
		if (type.contains("SYS")) return false;
		return true;
	}

	@Override
	public void message(String m)
	{
		JSONObject dan = JSONObject.fromObject(m);
		if (!isPrintable(dan)) return;
		try
		{
			if (!(mgetMessage.invoke(null, dan) instanceof String)) return;
			if (!(mgetName.invoke(null, dan) instanceof String)) return;
		}
		catch (Exception e)
		{
			return;
		}
		danmaku.add(dan);
		while (danmaku.size() > maxCount)
			danmaku.removeLast();
		danmakuList.setListData(danmaku.toArray(new JSONObject[danmaku.size()]));
		jsp.invalidate();
	}

	@Override
	public void onLoad()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onUnload()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public String getName()
	{
		return "PinDanmaku";
	}

	@Override
	public String getName_ful()
	{
		return "置顶展示弹幕";
	}

	@Override
	public void show()
	{
		jframe.setVisible(true);
		jframe.setExtendedState(JFrame.NORMAL);
	}

	@Override
	public void setMsgMethod(Method m,Object obj)
	{}

	Method	msavePreference;
	Object	osavePreference;

	@Override
	public void setRequestSavePreMethod(Method m,Object obj)
	{
		msavePreference = m;
		osavePreference = obj;
	}

	static Pattern	sizePattern	= Pattern.compile("(\\d+)[xX, ](\\d+)");

	@Override
	public void actionPerformed(ActionEvent e)
	{
		String cmd = e.getActionCommand();
		if (cmd.equals("pined_show_hide"))
		{
			if (pined.isVisible() && pined.getExtendedState() == JFrame.NORMAL)
			{
				pined.setVisible(false);
			}
			else
			{
				pined.setVisible(true);
				pined.setExtendedState(JFrame.NORMAL);
			}
		}
		else if (cmd.equals("pined_size"))
		{
			String input = JOptionPane.showInputDialog("请输入大小，长乘宽（或宽乘高）", width + "x" + height);
			if (input == null) return;
			Matcher m = sizePattern.matcher(input);
			if (!m.find()) return;
			width = Integer.parseInt(m.group(1));
			height = Integer.parseInt(m.group(2));
			try
			{
				msavePreference.invoke(osavePreference);
			}
			catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e1)
			{}
			pined.setSize(width, height);
		}
	}

}
