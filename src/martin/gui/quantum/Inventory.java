/*******************************************************************************
 * Copyright (c) 2013 Martin Marinov.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Martin - initial API and implementation
 ******************************************************************************/
package martin.gui.quantum;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

public class Inventory extends JToolBar {
	
	private static final int ICON_SIZE = 30;
	
	private static final long serialVersionUID = 3452668488964049619L;
	private Item selected = null;
	private final JToggleButton[] btns;
	private inventoryClickListener listener = null;
	Item[] items;
	
	public Inventory(final Item ... items) {
		this.items = items;
		btns = new JToggleButton[items.length];

		
		for (int i = 0; i < items.length; i++) {
			final BufferedImage canvas = new BufferedImage(ICON_SIZE, ICON_SIZE, BufferedImage.TYPE_INT_ARGB);
			final Graphics2D g = canvas.createGraphics();
			g.setComposite(AlphaComposite.Src);
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			g.drawImage(items[i].getIcon(), 0, 0, ICON_SIZE, ICON_SIZE, null);
			
			btns[i] = new JToggleButton(new ImageIcon( canvas ));
			btns[i].setBackground(getBackground());
			btns[i].setBorder(BorderFactory.createEmptyBorder());
			
			btns[i].addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					final JToggleButton doer = (JToggleButton) e.getSource();
					
					int id;
					for (id = 0; id < btns.length; id++) if (btns[id] == doer) break;
					
					selected = items[id];
					if (listener != null) listener.onSelected(selected);
					
					for (int i = 0; i < btns.length; i++) btns[i].setSelected(id == i);
				}
			});
			
			add(btns[i]);
		}
		
	}
	
	public void registerInventoryClickListener(final inventoryClickListener listener) {
		this.listener = listener;
	}
	
	public Item getSelectedItem() {
		return selected;
	}
	
	public void grabDefaultTool() {
		btns[0].doClick();
	}
	
	static abstract class inventoryClickListener {
		
		protected abstract void onSelected(final Item item);
		
	}

}
