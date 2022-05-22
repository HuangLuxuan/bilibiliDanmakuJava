package pinDanmakuPlugin;


import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.LabelUI;


public class JLabelExt extends JLabel
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= -201492966654019253L;

	String						text;

	int							w;

	@Override
	public void paint(Graphics g)
	{
		if (w != getWidth())
		{
			w = getWidth();
			setText(text);
		}
		//System.out.println(toString() + "\n\t\tgetSize()=" + getSize());
		super.paint(g);
	}

	/**
	 * Returns the text string that the label displays.
	 *
	 * @return a String
	 * @see #setText
	 */
	@Override
	public String getText()
	{
		StackTraceElement[] stack = Thread.currentThread().getStackTrace();
		try
		{
			if (stack.length >= 3)
			{
				Class<?> clazz = Class.forName(stack[2].getClassName());
				if (LabelUI.class.isAssignableFrom(clazz)) return super.getText();
			}
		}
		catch (ClassNotFoundException e)
		{}
		return text;
	}

	/**
	 * Defines the single line of text this component will display.  If
	 * the value of text is null or empty string, nothing is displayed.
	 * <p>
	 * The default value of this property is null.
	 * <p>
	 * This is a JavaBeans bound property.
	 *
	 * @see #setVerticalTextPosition
	 * @see #setHorizontalTextPosition
	 * @see #setIcon
	 * @beaninfo
	 *    preferred: true
	 *        bound: true
	 *    attribute: visualUpdate true
	 *  description: Defines the single line of text this component will display.
	 */
	@Override
	public void setText(String text)
	{
		super.setText(JlabelSetText(this, this.text = text));
	}

	/**
	 * Creates a <code>JLabelExt</code> instance with the specified
	 * text, image, and horizontal alignment.
	 * The label is centered vertically in its display area.
	 * The text is on the trailing edge of the image.
	 *
	 * @param text  The text to be displayed by the label.
	 * @param icon  The image to be displayed by the label.
	 * @param horizontalAlignment  One of the following constants
	 *           defined in <code>SwingConstants</code>:
	 *           <code>LEFT</code>,
	 *           <code>CENTER</code>,
	 *           <code>RIGHT</code>,
	 *           <code>LEADING</code> or
	 *           <code>TRAILING</code>.
	 */
	public JLabelExt(String text,Icon icon,int horizontalAlignment)
	{
		super(text, icon, horizontalAlignment);
	}

	/**
	 * Creates a <code>JLabelExt</code> instance with the specified
	 * text and horizontal alignment.
	 * The label is centered vertically in its display area.
	 *
	 * @param text  The text to be displayed by the label.
	 * @param horizontalAlignment  One of the following constants
	 *           defined in <code>SwingConstants</code>:
	 *           <code>LEFT</code>,
	 *           <code>CENTER</code>,
	 *           <code>RIGHT</code>,
	 *           <code>LEADING</code> or
	 *           <code>TRAILING</code>.
	 */
	public JLabelExt(String text,int horizontalAlignment)
	{
		super(text, horizontalAlignment);
	}

	/**
	 * Creates a <code>JLabelExt</code> instance with the specified text.
	 * The label is aligned against the leading edge of its display area,
	 * and centered vertically.
	 *
	 * @param text  The text to be displayed by the label.
	 */
	public JLabelExt(String text)
	{
		super(text);
	}

	/**
	 * Creates a <code>JLabelExt</code> instance with the specified
	 * image and horizontal alignment.
	 * The label is centered vertically in its display area.
	 *
	 * @param image  The image to be displayed by the label.
	 * @param horizontalAlignment  One of the following constants
	 *           defined in <code>SwingConstants</code>:
	 *           <code>LEFT</code>,
	 *           <code>CENTER</code>,
	 *           <code>RIGHT</code>,
	 *           <code>LEADING</code> or
	 *           <code>TRAILING</code>.
	 */
	public JLabelExt(Icon image,int horizontalAlignment)
	{
		super(image, horizontalAlignment);
	}

	/**
	 * Creates a <code>JLabelExt</code> instance with the specified image.
	 * The label is centered vertically and horizontally
	 * in its display area.
	 *
	 * @param image  The image to be displayed by the label.
	 */
	public JLabelExt(Icon image)
	{
		super(image);
	}

	/**
	 * Creates a <code>JLabelExt</code> instance with
	 * no image and with an empty string for the title.
	 * The label is centered vertically
	 * in its display area.
	 * The label's contents, once set, will be displayed on the leading edge
	 * of the label's display area.
	 */
	public JLabelExt()
	{
		super();
	}

	static String	HTMLescTable[][]	= new String[][]
										{
										{ "&", "&amp;" },
										{ "<", "&lt;" },
										{ ">", "&gt;" },
										{ " ", "&nbsp;" } };

	public static String HTML_Escape(String text)
	{
		for (String esc[] : HTMLescTable)
			text = text.replaceAll(esc[0], esc[1]);
		return text;
	}

	public static String JlabelSetText(JLabel jLabel,String longString)
	{
		return JlabelSetText(jLabel, longString, jLabel.getWidth());
	}

	static int	dx	= 0;

	public static String JlabelSetText(JLabel jLabel,String longString,int width)
	{
		width -= dx;
		String str = "<html>";
		char[] chars = longString.toCharArray();
		Font f = jLabel.getFont();
		if (f == null)
		{
			//new RuntimeException("JLabel.getFont() returned null").printStackTrace();
			if (!str.contains("\n")) return longString;
			return "<html>" + HTML_Escape(longString).replaceAll("\n", "<br>") + "</html>";
		}
		FontMetrics fontMetrics = jLabel.getFontMetrics(f);
		int start = 0;
		int len = 0;
		while (start + len < longString.length())
		{
			while (true)
			{
				len++;
				if (start + len >= longString.length()) break;
				if (fontMetrics.charsWidth(chars, start, len + 1) > width && len > 0) break;
				if (start + len <= longString.length() && chars[start + len - 1] == '\n') break;
			}
			if (chars[start + len - 1] == '\n')
				str += HTML_Escape(new String(chars, start, len - 1)) + "<br>";
			else
				str += HTML_Escape(new String(chars, start, len)) + (start + len >= longString.length() ? "" : "<br>");
			start = start + len;
			len = 0;
		}
		str += "</html>";
		if (!str.contains("<br>")) return longString;
		return str;
	}

	/**
	 * If the <code>preferredSize</code> has been set to a
	 * non-<code>null</code> value just returns it.
	 * 如果父控件存在则返回父控件宽度下布局的预期大小，否则直接返回{@link JLabelExt#setText(String) setText}所设置的字符串不自动换行时的预期大小
	 *
	 * @return the value of the <code>preferredSize</code> property
	 * @see #setPreferredSize
	 * @see ComponentUI
	 */
	//boolean	inVisible	= false;

	@Override
	public Dimension getPreferredSize()
	{
		int width = getWidth();
		Component parent = this;
		/*while (parent.getParent() != null)
		{
			parent = parent.getParent();
			if (parent instanceof JViewport)
			{
				JViewport viewport = (JViewport) parent;
				width = viewport.getBounds().width - dx;
				break;
			}
		}*/
		/*if (!inVisible)
		{
			inVisible = true;
			getRootPane().validate();
			inVisible = false;
		}
		else
		{
			return new Dimension(0, getHeight());
		}*/
		Dimension result = new Dimension(0, 0);
		/*String rawstr = HTML_getText(JlabelSetText(this, text, width).replace("\n", ""));
		String strs[] = rawstr.split("\n");
		for (String str : strs)
		{
			Rectangle2D rect = getFont().getStringBounds(str, ((Graphics2D) getGraphics()).getFontRenderContext());
			result.height += rect.getHeight();
			result.width = Math.max(result.width, (int) rect.getWidth());
		}*/
		String oldText = super.getText();
		super.setText(JlabelSetText(this, text, width));
		result = super.getPreferredSize();
		super.setText(oldText);
		//System.out.println(toString() + "\ngetPreferredSize()=" + result);
		return result;
	}

	@Override
	public void reshape(int x,int y,int w,int h)
	{
		//System.out.println(toString() + "\n\t\tgetSize()=" + getSize());
		//System.out.println("\t\treshape(" + x + "," + y + "," + w + "," + h + ")");
		boolean needUpdate = w != getWidth();
		super.reshape(x, y, w, h);
		if (needUpdate) invalidate();
	}

	static String HTML_getText(String html)
	{
		char chars[] = html.replaceAll("<br>", "\n").toCharArray();
		String result = "";
		char token = ' ';
		for (int i = 0; i < chars.length; i++)
		{
			if (token == '<')
			{
				if (chars[i] == '>') token = ' ';
				continue;
			}
			else if (token == '"')
			{
				if (chars[i] == '"') token = ' ';
				continue;
			}
			else if (token == '\'')
			{
				if (chars[i] == '\'') token = ' ';
				continue;
			}
			else if ("<'\"".indexOf(chars[i]) >= 0)
			{
				token = chars[i];
				continue;
			}
			result += chars[i];
		}
		for (int i = HTMLescTable.length - 1; i >= 0; i--)
			result = result.replaceAll(HTMLescTable[i][1], HTMLescTable[i][0]);
		return result;
	}
}
