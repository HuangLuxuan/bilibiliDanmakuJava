package pinDanmakuPlugin;



import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicListUI;

import sun.swing.DefaultLookup;


public class JListExt<E> extends JList<E>
{
	public boolean	noImage	= false;

	@Override
	public int getScrollableBlockIncrement(Rectangle visibleRect,int orientation,int direction)
	{
		return 150;
	}

	public JListExt(ListModel<E> dataModel)
	{
		super(dataModel);
	}

	public JListExt(final E[] listData)
	{
		super(listData);
	}

	public JListExt(final Vector<? extends E> listData)
	{
		super(listData);
	}

	public JListExt()
	{
		super();
	}

	@Override
	public void updateUI()
	{
		setUI(new JListExtUI());

		ListCellRenderer<? super E> renderer = getCellRenderer();
		if (renderer instanceof Component)
		{
			SwingUtilities.updateComponentTreeUI((Component) renderer);
		}
	}

	public static class JListExtUI extends BasicListUI
	{
		@Override
		protected void installDefaults()
		{
			boolean cellRenderIsNull = list.getCellRenderer() == null;
			super.installDefaults();
			if (cellRenderIsNull) list.setCellRenderer(new JListExtCellRender());
		}

		@Override
		protected void updateLayoutState()
		{
			/* If both JList fixedCellWidth and fixedCellHeight have been
			 * set, then initialize cellWidth and cellHeight, and set
			 * cellHeights to null.
			 */

			int fixedCellHeight = list.getFixedCellHeight();
			int fixedCellWidth = list.getFixedCellWidth();

			cellWidth = (fixedCellWidth != -1) ? fixedCellWidth : -1;

			if (fixedCellHeight != -1)
			{
				cellHeight = fixedCellHeight;
				cellHeights = null;
			}
			else
			{
				cellHeight = -1;
				cellHeights = new int[list.getModel().getSize()];
			}

			/* If either of  JList fixedCellWidth and fixedCellHeight haven't
			 * been set, then initialize cellWidth and cellHeights by
			 * scanning through the entire model.  Note: if the renderer is
			 * null, we just set cellWidth and cellHeights[*] to zero,
			 * if they're not set already.
			 */

			if ((fixedCellWidth == -1) || (fixedCellHeight == -1))
			{

				ListModel dataModel = list.getModel();
				int dataModelSize = dataModel.getSize();
				ListCellRenderer renderer = list.getCellRenderer();

				if (renderer != null)
				{
					for (int index = 0; index < dataModelSize; index++)
					{
						Object value = dataModel.getElementAt(index);
						if (list instanceof JListExt) ((JListExt) list).noImage = true;
						Component c = renderer.getListCellRendererComponent(list, value, index, false, false);
						if (list instanceof JListExt) ((JListExt) list).noImage = false;
						rendererPane.add(c);
						c.setSize(list.getWidth(), 0);
						c.doLayout();
						Dimension cellSize = c.getPreferredSize();
						if (fixedCellWidth == -1)
						{
							cellWidth = Math.max(cellSize.width, cellWidth);
						}
						if (fixedCellHeight == -1)
						{
							cellHeights[index] = cellSize.height;
						}
					}
				}
				else
				{
					if (cellWidth == -1)
					{
						cellWidth = 0;
					}
					if (cellHeights == null)
					{
						cellHeights = new int[dataModelSize];
					}
					for (int index = 0; index < dataModelSize; index++)
					{
						cellHeights[index] = 0;
					}
				}
			}
		}
	}

	public static class JListExtCellRender<E> extends JLabelExt implements ListCellRenderer<E>
	{

		private static final Border	SAFE_NO_FOCUS_BORDER	= new EmptyBorder(1, 1, 1, 1);
		private static final Border	DEFAULT_NO_FOCUS_BORDER	= new EmptyBorder(1, 1, 1, 1);
		protected static Border		noFocusBorder			= DEFAULT_NO_FOCUS_BORDER;

		@Override
		public Component getListCellRendererComponent(JList<? extends E> list,E value,int index,boolean isSelected,boolean cellHasFocus)
		{
			setComponentOrientation(list.getComponentOrientation());

			Color bg = null;
			Color fg = null;

			JList.DropLocation dropLocation = list.getDropLocation();
			if (dropLocation != null && !dropLocation.isInsert() && dropLocation.getIndex() == index)
			{

				bg = DefaultLookup.getColor(this, ui, "List.dropCellBackground");
				fg = DefaultLookup.getColor(this, ui, "List.dropCellForeground");

				isSelected = true;
			}

			if (isSelected)
			{
				setBackground(bg == null ? list.getSelectionBackground() : bg);
				setForeground(fg == null ? list.getSelectionForeground() : fg);
			}
			else
			{
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}

			if (value instanceof Icon)
			{
				setIcon((Icon) value);
				setText("");
			}
			else
			{
				setIcon(null);
				setText((value == null) ? "" : value.toString());
			}

			setEnabled(list.isEnabled());
			setFont(list.getFont());

			Border border = null;
			if (cellHasFocus)
			{
				if (isSelected)
				{
					border = DefaultLookup.getBorder(this, ui, "List.focusSelectedCellHighlightBorder");
				}
				if (border == null)
				{
					border = DefaultLookup.getBorder(this, ui, "List.focusCellHighlightBorder");
				}
			}
			else
			{
				border = getNoFocusBorder();
			}
			setBorder(border);

			return this;
		}

		private Border getNoFocusBorder()
		{
			Border border = DefaultLookup.getBorder(this, ui, "List.cellNoFocusBorder");
			if (System.getSecurityManager() != null)
			{
				if (border != null) return border;
				return SAFE_NO_FOCUS_BORDER;
			}
			else
			{
				if (border != null && (noFocusBorder == null || noFocusBorder == DEFAULT_NO_FOCUS_BORDER)){ return border; }
				return noFocusBorder;
			}
		}
	}
}
