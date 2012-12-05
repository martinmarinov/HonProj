package martin.gui.quantum;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

public class Qubit extends Item {
	
	private final static int GRID_SIZE = 100;
	private final static int GRID_OFF_X = GRID_SIZE / 2;
	private final static int GRID_OFF_Y = GRID_SIZE / 2;
	private final static int GRID_SNAP_DIST = 10;
	
	private final static Stroke DASHED_STROKE = new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
	        BasicStroke.JOIN_MITER, 6.0f, new float[]{6.0f}, 0.0f);
	private final static Color GRID_COLOR = new Color(255, 255, 255, 50);
	
	public enum type {input, normal, output};
	
	int x, y;
	int movex, movey;
	
	private final static String ICON = "btn_qubit";
	private type t;
	
	public Qubit(final type t) {
		this.t = t;
	}
	
	private Qubit(final type t, int x, int y) {
		this(t);
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
	void moveWith(int dx, int dy, Graphics2D g, Visualizer vis) {
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
		g.setColor(GRID_COLOR);
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
		g.drawImage(icon, x-icon.getWidth()/2, y-icon.getHeight()/2, null);
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
		vis.addItem(new Qubit(t, temp[0], temp[1]));
	}

	@Override
	public void mouseDrag(Graphics2D g, int x, int y, Visualizer vis) {
	}

	@Override
	String getIconFileName() {
		return ICON + "_" + t + ".png";
	}

	@Override
	void mousePressed(Graphics2D g, int x, int y, Visualizer vis) {
		// TODO Auto-generated method stub
		
	}

	@Override
	void mouseReleased(Graphics2D g, int x, int y, Visualizer vis) {
		// TODO Auto-generated method stub
		
	}

}
