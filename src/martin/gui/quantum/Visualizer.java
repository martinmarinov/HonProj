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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import martin.gui.quantum.Corrector.corrtype;
import martin.quantum.McalcDescription;
import martin.quantum.tools.Tools;

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
	
	public static Color background = new Color(0x3e3b5a);
	public static Color foreground = Color.white;
	public static Color fill_color = new Color(255, 255, 255, 120);
	public static Color grid_color = new Color(Visualizer.fill_color.getRed(), Visualizer.fill_color.getBlue(), Visualizer.fill_color.getGreen(), 50);
	
	public ArrayList<Item> items = new ArrayList<Item>();
	public ArrayList<JMenuItem> popup_items = new ArrayList<JMenuItem>();
	
	private JPopupMenu menu = new JPopupMenu();
	private Item item_for_menu;
	
	private Inventory inv;
	
	private final Properties p = new Properties();
	
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
		if (!items.contains(i)) items.add(i);
	}
	
	@Override
	public void setBounds(int x, int y, int width, int height) {
		bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		g = bi.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
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
		g.setBackground(background);
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
	
	/**
	 * Gets all of the items from the given type
	 * @param arr
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T[] getAllItemsOfType(final T[] arr) {
		
		final Class<?> input = arr.getClass().getComponentType();
		final ArrayList<T> al = new ArrayList<T>();
		
		for (final Item it : items)
			if (it.getClass().equals(input))
				al.add((T) it);
		
		return al.toArray(arr);
	}
	
	/**
	 * Gets all of the qubits that have pointers starting from them pointing to the qubit q
	 * @param q
	 * @param type
	 * @return
	 */
	public Qubit[] getQubitsDepending(final Qubit q, final Corrector.corrtype type) {
		final ArrayList<Qubit> qubits = new ArrayList<Qubit>();
		final Corrector[] correctors = getAllItemsOfType(new Corrector[0]);
		
		for (final Corrector c : correctors)
			if (c.t == type && c.i2.id == q.id)
				qubits.add(c.i1);
		
		return qubits.toArray(new Qubit[0]);
	}
	
	private String getDependance(final Qubit q, final Corrector.corrtype type, final String var) {
		
		
		final Qubit[] dep = getQubitsDepending(q, type);
		
		if (dep.length == 0) return "0";
		
		final StringBuilder sb = new StringBuilder();
		
		sb.append(var + Tools.zBOB(dep[0].getId()));
		
		for (int i = 1; i < dep.length; i++)
			sb.append("+"+var+Tools.zBOB(dep[i].getId()));
		
		return sb.toString();
	}
	
	public McalcDescription generateMcalcDesc() throws Exception {
		final McalcDescription desc = new McalcDescription();
		final StringBuilder sb = new StringBuilder(); // for efficient String building
		
		final Qubit[] qubit = getAllItemsOfType(new Qubit[0]);
		Arrays.sort(qubit);
		
		// error checking
		for (int i = qubit.length - 1; i > 0; i--)
			if (qubit[i].getType() == Qubit.type.input && qubit[i-1].getType() != Qubit.type.input)
				throw new Exception("The current algorithm assumes the first n qubits are input qubits. Please, change your pattern to satisfy that requirement.");
		
		// set n
		desc.n = qubit.length;
		
		// set inputs
		int n_input = 0;
		for (int i = 0; i < qubit.length; i++)
			if (qubit[i].getType() == Qubit.type.input)
				n_input++;
			else
				break;
		
		if (n_input > 0) {
			final int coeff_to_gen = 1 << n_input;
			
			char first = 'a';
			sb.append(first);
			
			for (int i = 1; i < coeff_to_gen; i++) {
				first++;
				sb.append(", "+first);
			}
			
			desc.inputs = sb.toString();
			sb.setLength(0);
		}
		
		// set entanglement
		final Entangler[] entanglers = getAllItemsOfType(new Entangler[0]);
		boolean firstitem = true;
		for (final Entangler e : entanglers) {
			final String text = "("+Tools.zBOB(e.i1.getId())+", "+Tools.zBOB(e.i2.getId())+")";
			if (firstitem) {
				sb.append(text);
				firstitem = false;
			} else
				sb.append("; "+text);
		}
		desc.entanglement = sb.toString();
		sb.setLength(0);
		
		// set measurements
		firstitem = true;
		for (final Qubit q : qubit)
			if (q.perform_measurement) {
				final String text = "("+Tools.zBOB(q.getId())+", "+getDependance(q, corrtype.X, "s")+", "+getDependance(q, corrtype.Z, "t")+", "+q.measurement_angle+")";
				if (firstitem) {
					sb.append(text);
					firstitem = false;
				} else
					sb.append("; "+text);
			}
		desc.measurements = sb.toString();
		sb.setLength(0);
		
		// corrections
		firstitem = true;
		for (final Qubit q : qubit)
			if (!q.perform_measurement) {
				
				final String xdep = getDependance(q, corrtype.X, "s");
				final String zdep = getDependance(q, corrtype.Z, "s");

				final String textx = xdep.equals("0") ? null : "("+Tools.zBOB(q.getId())+", x, "+xdep+")";
				final String textz = zdep.equals("0") ? null : "("+Tools.zBOB(q.getId())+", z, "+zdep+")";


				if (textx != null) {
					if (firstitem) {
						sb.append(textx);
						firstitem = false;
					} else
						sb.append("; "+textx);
				}

				if (textz != null) {
					if (firstitem) {
						sb.append(textz);
						firstitem = false;
					} else
						sb.append("; "+textz);
				}
			}
		desc.corrections = sb.toString();
		sb.setLength(0);
		
		return desc;
	}
		
	private int getOccurances(final String key, final HashMap<String, Integer> db) {
		final Integer count = db.get(key);
		return count == null ? 0 : count;
	}
	
	public void saveToFile(final File f) throws Exception {
		// for storing how many times each class have been seen
		final HashMap<String, Integer> db = new HashMap<String, Integer>();
		
		for (final Item it : items) {
			final String its = it.saveToString();
			if (its != null) {
				final String className = it.getClass().getName();
				final Integer occurances = getOccurances(className, db);
				
				p.setProperty(className+"&"+occurances, its);
				
				db.put(className, occurances == null ? 1 : (occurances + 1));
			}
		}
		
		// save occurances
		for (final String key : db.keySet())
			p.setProperty(key, String.valueOf(db.get(key)));
		
		FileOutputStream out;
		out = new FileOutputStream(f);
		p.store(out, "---MBQC Graphical Pattern description---");
		out.close();

	}
	
	public void loadFromFile(final File f) throws Exception {
		items.clear();
		if (f != null) {

			final FileInputStream in = new FileInputStream(f);
			final HashSet<String> passed = new HashSet<String>();
			p.load(in);
			in.close();
			int c = 0;

			boolean notok = true;
			while(notok) {
				c++;
				int items_c = 0;
				notok = false;
				for (final Item it : inv.items) {
					final String className = it.getClass().getName();
					if (passed.contains(className)) continue;
					passed.add(className);

					final int occurances = Integer.parseInt(p.getProperty(className, "0"));
					items_c += occurances;
					for (int i = 0; i < occurances; i++) {
						final Item item = it.loadFromString(p.getProperty(className+"&"+i), this);

						if (item != null && !items.contains(item))
							items.add(item);
						else
							notok = true;
					}
				}
				if (c > items_c) throw new Exception("Corrupted file! Probably a missing reference!");
			}
		}

		repaint();
	}

}
