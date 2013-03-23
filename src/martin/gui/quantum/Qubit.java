package martin.gui.quantum;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
public class Qubit extends Item implements Comparable<Qubit> {
	
	private type[] menu_types;

	public static final int QUBIT_SIZE = 30;
	public static final int SQUARE_AROUND_QUBIT_SIZE = 39;
	
	
	private final static int GRID_SIZE = 100;
	private final static int GRID_OFF_X = GRID_SIZE / 2;
	private final static int GRID_OFF_Y = GRID_SIZE / 2;
	private final static int GRID_SNAP_DIST = 10;
	
	public final static int DEFAULT_FONT_SIZE = 22;
	
	private int menu_swap_id, menu_measurement_angle, menu_perform_measurement;
	
	private final static Stroke DASHED_STROKE = new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
	        BasicStroke.JOIN_MITER, 2.0f, new float[]{2.0f}, 0.0f);
	private final static Stroke CIRCLE_STROKE = new BasicStroke(4.0f);
	private Font font = null;
	String measurement_angle = "0";
	boolean perform_measurement = false;
	
	public enum type {input, normal};
	
	int x, y;
	int movex, movey;
	int id;
	
	private final static String ICON = "btn_qubit";
	private type t;
	
	public Qubit(final type t) {
		this.t = t;
	}
	
	private Qubit(final type t, int x, int y, int id) {
		this(t);
		this.id = id;
		this.x = x;
		this.y = y;
		movex = x;
		movey = y;
		getIcon();
	}
	
	@Override
	Cursor getCursor() {
		return Toolkit.getDefaultToolkit().createCustomCursor(
				new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB), new Point(0, 0), "blank");
	}

	@Override
	boolean isMouseOntop(int mx, int my) {
		final double dx = x - mx;
		final double dy = y - my;
		final double dist = dx * dx + dy * dy;
		final int widtho2 = icon.getWidth()/2;
		return dist < widtho2 * widtho2;
	}
	
	@Override
	void moveWith(int dx, int dy, int mx, int my, Graphics2D g, Visualizer vis) {
		movex += dx;
		movey += dy;
		renderGrid(g, vis);
		final int[] temp = new int[] {movex, movey};
		snapToGrid(temp);
		x = temp[0];
		y = temp[1];
	}
	
	private void renderGrid(Graphics2D g, Visualizer vis) {
		final Stroke orig = g.getStroke();
		final Color c = g.getColor();
		g.setStroke(DASHED_STROKE);
		g.setColor(Visualizer.grid_color);
		final int width = vis.getWidth();
		final int height = vis.getHeight();
		
		for (int x = GRID_OFF_X; x < width; x += GRID_SIZE)
			g.drawLine(x, 0, x, height);
		
		for (int y = GRID_OFF_Y; y < height; y += GRID_SIZE)
			g.drawLine(0, y, width, y);
		
		g.setStroke(orig);
		g.setColor(c);
	}
	
	private void snapToGrid(final int[] coords) {
		int cx = ((coords[0] + GRID_SIZE / 2 - GRID_OFF_X) / GRID_SIZE) * GRID_SIZE + GRID_OFF_X;
		int cy = ((coords[1] + GRID_SIZE / 2 - GRID_OFF_Y) / GRID_SIZE) * GRID_SIZE + GRID_OFF_Y;
		int dx = Math.abs(coords[0] - cx);
		int dy = Math.abs(coords[1] - cy);
		if (dx < GRID_SNAP_DIST && dy < GRID_SNAP_DIST) {
			coords[0] = cx;
			coords[1] = cy;
		}
	}
	
	@Override
	public void renderInstance(Graphics2D g, Visualizer vis) {
		final int halficw = icon.getWidth()/2;
		final int halfich = icon.getHeight()/2;
		//g.drawImage(icon, x-halficw, y-halfich, null);
		
		final Stroke backup = g.getStroke();
		g.setStroke(CIRCLE_STROKE);
		g.drawOval(x-QUBIT_SIZE, y-QUBIT_SIZE, QUBIT_SIZE*2, QUBIT_SIZE*2);
		
		if (t == type.input)
			g.drawRect(x-SQUARE_AROUND_QUBIT_SIZE, y-SQUARE_AROUND_QUBIT_SIZE, SQUARE_AROUND_QUBIT_SIZE*2, SQUARE_AROUND_QUBIT_SIZE*2);
			
		if (perform_measurement) {
			final Color cbackup = g.getColor();
			g.setColor(Visualizer.fill_color);
			g.fillOval(x-QUBIT_SIZE, y-QUBIT_SIZE, QUBIT_SIZE*2, QUBIT_SIZE*2);
			g.setColor(cbackup);
		}
		g.setStroke(backup);
		
		final Font defaultFont = g.getFont();
		if (font == null) font = new Font(defaultFont.getFamily(), Font.PLAIN, DEFAULT_FONT_SIZE);
		g.setFont(font);
		final String sid = String.valueOf(id);
		final FontMetrics fm = g.getFontMetrics();
		final Rectangle2D f = fm.getStringBounds(sid, g);
		int sx = (int) (x  - f.getWidth()  / 2);
		int sy = (int) (y - f.getHeight() / 2  + fm.getAscent());
		g.drawString(sid, sx, sy);
		
		if (perform_measurement)
			g.drawString("α = "+measurement_angle, x - halficw, y - halfich);
		
		g.setFont(defaultFont);
		
	}

	@Override
	public void mouseMove(Graphics2D g, int x, int y, Visualizer vis) {
		renderGrid(g, vis);
		final int[] temp = new int[] {x, y};
		snapToGrid(temp);
		g.drawImage(icon, temp[0]-icon.getWidth()/2, temp[1]-icon.getHeight()/2, vis);
	}

	@Override
	public void mouseClick(Graphics2D g, int x, int y, Visualizer vis) {
		final int[] temp = new int[] {x, y};
		snapToGrid(temp);
		
		vis.addItem(new Qubit(t, temp[0], temp[1], getFirstFreeId(vis)));
		vis.grabDefaultTool();
	}
	
	private boolean isThereAQubitWithId(int id, Visualizer vis) {
		for (final Item i : vis.items)
			if (i instanceof Qubit && ((Qubit) i).id == id)
				return true;
		
		return false;
	}
	
	private int getFirstFreeId(Visualizer vis) {
		int id = 1;
		while (isThereAQubitWithId(id, vis)) id++;
		return id;
	}

	@Override
	public void mouseDrag(Graphics2D g, int x, int y, Visualizer vis) {
		mouseMove(g, x, y, vis);
	}

	@Override
	String getIconFileName() {
		return ICON + "_" + t + ".png";
	}

	@Override
	void mousePressed(Graphics2D g, int x, int y, Visualizer vis) {
		mouseClick(g, x, y, vis);
	}

	@Override
	void mouseReleased(Graphics2D g, int x, int y, Visualizer vis) {
		mouseMove(g, x, y, vis);
	}

	@Override
	String[] getMenuEntries(Visualizer vis) {
		final type[] types = type.values();
		final ArrayList<String> entries = new ArrayList<String>();
		menu_types = new type[types.length - 1];
		
		int j = 0;
		for (int i = 0; i < types.length; i++)
			if (types[i] != t) {
				entries.add("Change to "+types[i]+" qubit");
				menu_types[j++] = types[i];
			}
		
		// additional actions, starting from id 0
		int additional_id = 0;
		menu_swap_id = -1;
		menu_measurement_angle = -1;
		menu_perform_measurement = -1;
		
		if (numberOfQubitsInVisualizer(vis) > 1) {
			entries.add("Edit qubit id");
			menu_swap_id = additional_id++;
		}
		
		entries.add((perform_measurement ? "✓ " : "") + "Perform measurement");
		menu_perform_measurement = additional_id++;

		if (perform_measurement) {
			entries.add("Set measurement angle");
			menu_measurement_angle = additional_id++;
		}

		return entries.toArray(new String[0]);
	}
	
	private int numberOfQubitsInVisualizer(final Visualizer vis) {
		int c = 0;
		
		for (final Item i : vis.items)
			if (i instanceof Qubit)
				c++;
		
		return c;
	}
	
	public type getType() {
		return t;
	}
	
	/**
	 * Get 0 based qubit id. Keep in mind this is 0 based!
	 * @return
	 */
	public int getId() {
		return id - 1;
	}
	
	public Qubit getHighestQubitId(final Visualizer vis) {
		int highest = -1;
		Qubit ans = null;
		
		for (final Item i : vis.items)
			if (i instanceof Qubit) {
				final Qubit q = (Qubit) i;
				if (q.id > highest) {
					highest = q.id;
					ans = q;
				}
			}
		
		return ans;
	}
	
	public Qubit getHighestQubitLessThanMe(final Visualizer vis) {
		int highest = -1;
		Qubit ans = null;
		
		for (final Item i : vis.items)
			if (i instanceof Qubit) {
				final Qubit q = (Qubit) i;
				if (q.id > highest && q.id < id) {
					highest = q.id;
					ans = q;
				}
			}
		
		return ans;
	}

	@Override
	void onMenuEntryClick(int id, Visualizer vis) {
		if (id < menu_types.length) {
			t = menu_types[id];
			getIcon();
			return;
		}
		
		final int additional_id = id - menu_types.length;
		if (additional_id == menu_swap_id) {
			
			final ArrayList<String> options = new ArrayList<String>();
			for (final Item i : vis.items)
				if (i instanceof Qubit) {
					final Qubit q = (Qubit) i;
					if (q.id != this.id)
						options.add(String.valueOf(q.id));
				}
			
			final int selected = vis.showOptionDialog("Select new id", "Choose a qubit to swap id with.", options.toArray(new String[0]));
			if (selected >= 0) {
				final int newqubitid = Integer.parseInt(options.get(selected));
				
				for (final Item i : vis.items)
					if (i instanceof Qubit) {
						final Qubit q = (Qubit) i;
						if (q.id == newqubitid) {
							final int temp = q.id;
							q.id = this.id;
							this.id = temp;
						}
					}
			}
			
		} else if (additional_id == menu_measurement_angle) {
			
			final String new_ang = vis.showInputDialog("Enter angle", "Input the measurement angle for this qubit", measurement_angle);
			if (new_ang != null) measurement_angle = new_ang;
			measurement_angle = measurement_angle.trim();
			if (measurement_angle.isEmpty()) measurement_angle = "0";
			
		} else if (additional_id == menu_perform_measurement) {
			
			perform_measurement = ! perform_measurement;
			
		}
	}
	
	@Override
	protected void onPostLayoutChanged(final Visualizer vis) {		
		
		// look for gaps in the big numbers
		final Qubit highest_qubit = getHighestQubitId(vis);
		final int highest_id = highest_qubit.id;
		
		if (highest_id != id)
			highest_qubit.onPostLayoutChanged(vis);
		else
			fixId(vis);

	}
	
	private void fixId(final Visualizer vis) {
		final Qubit lowerqubit = getHighestQubitLessThanMe(vis);

		if (lowerqubit == null) {
			id = 1;
			return;
		}
		
		lowerqubit.fixId(vis);
		id = lowerqubit.id + 1;
	}

	@Override
	public int compareTo(Qubit o) {
		return id > o.id ? 1 : (id < o.id ? -1 : 0);
	}
	
	@Override
	public String toString() {
		return "Qubit "+id;
	}

	
	@Override
	protected Item loadFromString(String p, Visualizer vis) {
		final String[] data = stringToStringArray(p);
		int c = 0;
		
		final int x = Integer.parseInt(data[c++]);
		final int y = Integer.parseInt(data[c++]);
		final type t = type.valueOf(data[c++]);
		final int id = Integer.parseInt(data[c++]);
		final String angle = data[c++];
		final boolean perform_m = Boolean.parseBoolean(data[c++]);
		
		final Qubit q = new Qubit(t, x, y, id);
		q.measurement_angle = angle;
		q.perform_measurement = perform_m;
		return q;
	}
	
	@Override
	protected String saveToString() {
		final ArrayList<String> data = new ArrayList<String>();
		
		data.add(String.valueOf(x));
		data.add(String.valueOf(y));
		data.add(String.valueOf(t));
		data.add(String.valueOf(id));
		data.add(String.valueOf(measurement_angle));
		data.add(String.valueOf(perform_measurement));
		
		return stringArrayToString(data.toArray(new String[0]));
	}
}
