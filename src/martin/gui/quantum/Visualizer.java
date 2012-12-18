package martin.gui.quantum;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

public class Visualizer extends JPanel {

	private final static String[] menu_entries = new String[] {"Delete"};
	
	private static final long serialVersionUID = -7435977815386640930L;
	
	private enum mousestate {moved, dragged, clicked, none, released, pressed}
	
	private int width = 0, height = 0;
	private Graphics2D g;
	private BufferedImage bi;
	private Item selected = null;
	private int mx, my;
	
	private mousestate laststate = mousestate.none;
	
	private final static Color background = new Color(0x3e3b5a);
	private final static Color foreground = Color.white;
	
	public ArrayList<Item> items = new ArrayList<Item>();
	public ArrayList<JMenuItem> popup_items = new ArrayList<JMenuItem>();
	
	private JPopupMenu menu = new JPopupMenu();
	private Item item_for_menu;
	
	private Inventory inv;
	
	public Visualizer() {
		addMouseMotionListener(new MouseMotionListener() {
			
			@Override
			public void mouseMoved(MouseEvent e) {
				mx = e.getX();
				my = e.getY();
				laststate = mousestate.moved;
				repaint();
			}
			
			@Override
			public void mouseDragged(MouseEvent e) {
				mx = e.getX();
				my = e.getY();
				laststate = mousestate.dragged;
				repaint();
			}
		});
		
		addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				mx = e.getX();
				my = e.getY();
				laststate = mousestate.released;
				if (SwingUtilities.isRightMouseButton(e)) showPopup(mx, my);
				repaint();
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				mx = e.getX();
				my = e.getY();
				laststate = mousestate.pressed;
				repaint();
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				mx = -1;
				my = -1;
				laststate = mousestate.none;
				repaint();
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				laststate = mousestate.none;
				repaint();
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				mx = e.getX();
				my = e.getY();
				laststate = mousestate.clicked;
				repaint();
			}

			private void showPopup(int mx, int my) {
				final Item it = getItemAt(mx, my);

				if (it != null) {
					item_for_menu = it;
					menu.removeAll();
					popup_items.clear();

					for (int i = 0; i < menu_entries.length; i++) {
						final JMenuItem item = new JMenuItem(menu_entries[i]);
						item.addActionListener(popup_listener);
						popup_items.add(item);
					}

					final String[] options = it.getMenuEntries(Visualizer.this);
					if (options != null)
					for (int i = 0; i < options.length; i++) {
						final JMenuItem item = new JMenuItem(options[i]);
						item.addActionListener(popup_listener);
						popup_items.add(item);
					}

					for (final JMenuItem pit : popup_items)
						menu.add(pit);

					menu.show(Visualizer.this, mx, my);
				}
			}
		});
		
	}
	
	private ActionListener popup_listener = new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			final int popup_items_c = popup_items.size();
			
			for (int i = 0; i < popup_items_c; i++)
				if (popup_items.get(i) == e.getSource()) {
					if (i < menu_entries.length)
						onMenuEntryClick(i);
					else
						item_for_menu.onMenuEntryClick(i - menu_entries.length, Visualizer.this);
					return;
				}
		}
	};
	
	public void onMenuEntryClick(int id) {
		switch (id) {
		case 0:
			boolean items_being_deleted = true;
			final HashSet<Item> to_delete = new HashSet<Item>();
			to_delete.add(item_for_menu);
			while (items_being_deleted) {
				items_being_deleted = false;
				
				for (final Item it : items)
					if (!to_delete.contains(it) && it.doesItNeedToBeDeleted(to_delete)) {
						items_being_deleted = true;
						to_delete.add(it);
					}
			}
			for (final Item it : to_delete)
				items.remove(it);
			for (final Item it : items)
				it.onPostLayoutChanged(this);
			break;
		}
	}
	
	public void addItem(final Item i) {
		items.add(i);
	}
	
	@Override
	public void setBounds(int x, int y, int width, int height) {
		bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		g = bi.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setBackground(background);
		g.clearRect(0, 0, width, height);
		this.width = width;
		this.height = height;
		super.setBounds(x, y, width, height);
	}
	
	@Override
	public void paint(Graphics g) {
		paint();
		if (bi != null) g.drawImage(bi, 0, 0, this);
	}
	
	public void registerInventory(final Inventory inv) {
		this.inv = inv;
		inv.registerInventoryClickListener(new Inventory.inventoryClickListener() {
			@Override
			protected void onSelected(final Item item) {
				selected = item;
				final Cursor c = selected.getCursor();
				if (c != null)
					setCursor(c);
				else
					setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
		});
	}

	public void paint() {
		g.clearRect(0, 0, width, height);
		
		g.setColor(foreground);
		
		if (selected != null && mx > 0 && my > 0) {
			switch (laststate) {
			case moved:
				selected.mouseMove(g, mx, my, this);
				break;
			case clicked:
				selected.mouseClick(g, mx, my, this);
				break;
			case dragged:
				selected.mouseDrag(g, mx, my, this);
				break;
			case pressed:
				selected.mousePressed(g, mx, my, this);
				break;
			case released:
				selected.mouseReleased(g, mx, my, this);
				break;
			}
			laststate = mousestate.none;
		}
		
		for (final Item i : items)
			i.renderInstance(g, this);
	}
	
	public void grabDefaultTool() {
		inv.grabDefaultTool();
	}
	
	public Item getItemAt(int x, int y) {
		for (final Item i : items)
			if (i.isMouseOntop(x, y))
				return i;
		
		return null;
	}
	
	/**
	 * Shows an option dialog with a droplist for the user to select from a certain range of options.
	 * @param title the title of the dialog
	 * @param description the description, which should be longer than the title
	 * @param options the possible options that would appear in the droplist
	 * @return the id of the option that was selected or -1 if nothing was selected
	 */
	public int showOptionDialog(final String title, final String description, final Object[] options) {
		final Object ans = JOptionPane.showInputDialog(
                this,
                description,
                title,
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0].toString());
		if (ans == null) return -1;
		for (int i = 0; i < options.length; i++) if (ans == options[i]) return i;
		return -1;
	}
	
	/**
	 * Shows an input dialog that the user can type information in.
	 * @param title the title of the dialog
	 * @param description the description, which should be longer than the title
	 * @param default_text the default text to be shown in the dialog
	 * @return
	 */
	public String showInputDialog(final String title, final String description, final String default_text) {
		return (String) JOptionPane.showInputDialog(
                this,
                description,
                title,
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                default_text);
	}
}
