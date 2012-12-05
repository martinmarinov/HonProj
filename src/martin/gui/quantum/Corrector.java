package martin.gui.quantum;

import java.awt.BasicStroke;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class Corrector extends Item {
	
	private final static int DEFAULT_FONT_SIZE = Qubit.DEFAULT_FONT_SIZE;
	
	private static final int ICON_OFFSET = 16;
	private static final int QUBIT_SIZE = 32;
	private final static Stroke DASHED_STROKE = new BasicStroke(4.0f);
	private static final int PICKING_DISTANCE = 6;
	private static final int DEFAULT_ARC_DISTANCE = 100;
	
	private Qubit i1, i2;
	private int arcd = DEFAULT_ARC_DISTANCE;
	
	private Font font = null;
	
	private boolean select_first = true;
	
	public enum corrtype {X, Z};
	
	private final corrtype t;
	
	public Corrector(final corrtype t) {
		this.t = t;
	}
	
	private Corrector(final Qubit i1, final Qubit i2, corrtype t) {
		this(t);
		this.i1 = i1;
		this.i2 = i2;
	}

	@Override
	void renderInstance(Graphics2D g, Visualizer vis) {
		final Stroke def = g.getStroke();
		g.setStroke(DASHED_STROKE);	

		final Point2D.Float p = new Point2D.Float();
		g.draw(getBezier(p));
		
		g.setStroke(def);
		
		final Font defaultFont = g.getFont();
		if (font == null) font = new Font(defaultFont.getFamily(), Font.PLAIN, DEFAULT_FONT_SIZE);
		g.setFont(font);
		final String sid = String.valueOf(t);
		final FontMetrics fm = g.getFontMetrics();
		final Rectangle2D f = fm.getStringBounds(sid, g);
		int stx = (int) (p.x - f.getWidth()  / 2);
		int sty = (int) (p.y - f.getHeight() / 2  + fm.getAscent());
		g.drawString(sid, stx, sty);
		g.setFont(defaultFont);
	}
	
	private CubicCurve2D.Double getBezier(final Point2D.Float p) {
		final int sx = i1.x;
		final int sy = i1.y;
		final int ex = i2.x;
		final int ey = i2.y;
		
		final int ddx = sx - ex;
		final int ddy = sy - ey;
		final double nang = Math.atan2(ddy, ddx)+Math.PI/2;
		final int sin = (int) (Math.sin(nang) * arcd);
		final int cos = (int) (Math.cos(nang) * arcd);
		
		final int cp1x = sx + cos;
		final int cp1y = sy + sin;
		final int cp2x = ex + cos;
		final int cp2y = ey + sin;
		
		if (p != null) {
			p.x = (cp1x + cp2x) / 2;
			p.y = (cp1y + cp2y) / 2;
		}
		
		return new CubicCurve2D.Double(sx, sy, cp1x, cp1y, cp2x, cp2y, ex, ey);	
	}

	@Override
	boolean isMouseOntop(int x, int y) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	String getIconFileName() {
		return "correction_" + t +".png";
	}

	@Override
	void moveWith(int dx, int dy, Graphics2D g, Visualizer vis) {}

	@Override
	void mouseMove(Graphics2D g, int x, int y, Visualizer vis) {
		if (select_first)
			g.drawImage(icon, x-ICON_OFFSET, y-icon.getHeight()/2, vis);
		else {
			final Stroke def = g.getStroke();
			g.setStroke(DASHED_STROKE);	
			
			final int dx = i1.x - x;
			final int dy = i1.y - y;
			final double nang = Math.atan2(dy, dx)+Math.PI/2;
			final int d = (int) Math.sqrt(dx * dx + dy * dy) / 2;
			
			final int cp1x = (int) (i1.x + Math.cos(nang) * d);
			final int cp1y = (int) (i1.y + Math.sin(nang) * d);
			final int cp2x = (int) (x + Math.cos(nang) * d);
			final int cp2y = (int) (y + Math.sin(nang) * d);
			
			final CubicCurve2D.Double bezcurve = new CubicCurve2D.Double(i1.x, i1.y, cp1x, cp1y, cp2x, cp2y, x, y);
			g.draw(bezcurve);
			
			g.setStroke(def);
			g.drawImage(icon, x-icon.getWidth()+ICON_OFFSET, y-icon.getHeight()/2, vis);
		}
	}

	@Override
	void mouseClick(Graphics2D g, int x, int y, Visualizer vis) {
		mouseMove(g, x, y, vis);
	}

	@Override
	void mouseDrag(Graphics2D g, int x, int y, Visualizer vis) {
		mouseMove(g, x, y, vis);
	}

	@Override
	void mousePressed(Graphics2D g, int x, int y, Visualizer vis) {
		if (select_first) {
			i1 = getQubitAt(x, y, vis);
			if (i1 != null)
				select_first =  false;
		} else {
			i2 = getQubitAt(x, y, vis);
			if (i2 != null) {
				
				vis.addItem(new Corrector(i1, i2, t));
				vis.grabDefaultTool();
				
				i1 = null;
				i2 = null;
				select_first = true;
			}
		}
		mouseMove(g, x, y, vis);
	}
	
	private Qubit getQubitAt(int x, int y, Visualizer vis) {
		for (final Item i : vis.items)
			if (i instanceof Qubit)
				if (i.isMouseOntop(x, y))
					return (Qubit) i;
		
		return null;
	}

	@Override
	void mouseReleased(Graphics2D g, int x, int y, Visualizer vis) {
		mouseMove(g, x, y, vis);
	}

	@Override
	Cursor getCursor() {
		return null;
	}

}
