package martin.gui.quantum;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JPanel;

public class Visualizer extends JPanel {

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
		});
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
	
	
}
