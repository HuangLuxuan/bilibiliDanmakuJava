package pinDanmakuPlugin;



import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.Point;

import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.ListCellRenderer;
import javax.swing.Scrollable;
import javax.swing.ViewportLayout;


public class JScrollPaneExt extends JScrollPane
{
	public JScrollPaneExt()
	{
		super();
	}

	public JScrollPaneExt(int vsbPolicy,int hsbPolicy)
	{
		super(vsbPolicy, hsbPolicy);
	}

	public JScrollPaneExt(Component view)
	{
		super(view);
	}

	public JScrollPaneExt(Component view,int vsbPolicy,int hsbPolicy)
	{
		super(view, vsbPolicy, hsbPolicy);
	}

	@Override
	protected JViewport createViewport()
	{
		return new JScrollPaneExtViewport(getVerticalScrollBarPolicy() == VERTICAL_SCROLLBAR_NEVER, getHorizontalScrollBarPolicy() == HORIZONTAL_SCROLLBAR_NEVER);
	}

	public static class JScrollPaneExtViewport extends JViewport
	{
		boolean	vertical_no_scroll,horizontal_no_scroll;

		public JScrollPaneExtViewport(boolean vertical_no_scroll,boolean horizontal_no_scroll)
		{
			this.vertical_no_scroll = vertical_no_scroll;
			this.horizontal_no_scroll = horizontal_no_scroll;
			setLayout(createLayoutManager());
		}

		@Override
		protected LayoutManager createLayoutManager()
		{
			return new JScrollPaneExtViewportLayout(vertical_no_scroll, horizontal_no_scroll);
		}

		public static class JScrollPaneExtViewportLayout extends ViewportLayout
		{
			boolean	vertical_no_scroll,horizontal_no_scroll;

			public JScrollPaneExtViewportLayout(boolean vertical_no_scroll,boolean horizontal_no_scroll)
			{
				this.vertical_no_scroll = vertical_no_scroll;
				this.horizontal_no_scroll = horizontal_no_scroll;
			}

			@Override
			public void layoutContainer(Container parent)
			{
				JScrollPaneExtViewport vp = (JScrollPaneExtViewport) parent;
				Component view = vp.getView();
				Scrollable scrollableView = null;

				if (view == null)
				{
					return;
				}
				else if (view instanceof Scrollable)
				{
					scrollableView = (Scrollable) view;
				}

				/* All of the dimensions below are in view coordinates, except
				 * vpSize which we're converting.
				 */

				//Insets insets = vp.getInsets();
				Dimension viewPrefSize = view.getPreferredSize();
				Dimension vpSize = vp.getSize();
				Dimension extentSize = vp.toViewCoordinates(vpSize);
				Dimension viewSize = new Dimension(viewPrefSize);

				if (scrollableView != null)
				{
					if (scrollableView.getScrollableTracksViewportWidth())
					{
						viewSize.width = vpSize.width;
					}
					if (scrollableView.getScrollableTracksViewportHeight())
					{
						viewSize.height = vpSize.height;
					}
				}
				if (horizontal_no_scroll) viewSize.width = vpSize.width;
				if (vertical_no_scroll) viewSize.height = vpSize.height;

				Point viewPosition = vp.getViewPosition();

				/* If the new viewport size would leave empty space to the
				 * right of the view, right justify the view or left justify
				 * the view when the width of the view is smaller than the
				 * container.
				 */
				if (scrollableView == null || vp.getParent() == null || vp.getParent().getComponentOrientation().isLeftToRight())
				{
					if ((viewPosition.x + extentSize.width) > viewSize.width)
					{
						viewPosition.x = Math.max(0, viewSize.width - extentSize.width);
					}
				}
				else
				{
					if (extentSize.width > viewSize.width)
					{
						viewPosition.x = viewSize.width - extentSize.width;
					}
					else
					{
						viewPosition.x = Math.max(0, Math.min(viewSize.width - extentSize.width, viewPosition.x));
					}
				}

				/* If the new viewport size would leave empty space below the
				 * view, bottom justify the view or top justify the view when
				 * the height of the view is smaller than the container.
				 */
				if ((viewPosition.y + extentSize.height) > viewSize.height)
				{
					viewPosition.y = Math.max(0, viewSize.height - extentSize.height);
				}

				/* If we haven't been advised about how the viewports size
				 * should change wrt to the viewport, i.e. if the view isn't
				 * an instance of Scrollable, then adjust the views size as follows.
				 *
				 * If the origin of the view is showing and the viewport is
				 * bigger than the views preferred size, then make the view
				 * the same size as the viewport.
				 */
				if (scrollableView == null)
				{
					if ((viewPosition.x == 0) && (vpSize.width > viewPrefSize.width))
					{
						viewSize.width = vpSize.width;
					}
					if ((viewPosition.y == 0) && (vpSize.height > viewPrefSize.height))
					{
						viewSize.height = vpSize.height;
					}
				}
				vp.setViewPosition(viewPosition);
				vp.setViewSize(viewSize);
				if (view instanceof JList)
				{
					JList jl = (JList) view;
					ListCellRenderer render = jl.getCellRenderer();
					jl.setCellRenderer(null);
					jl.setCellRenderer(render);
				}
			}
		}
	}
}
