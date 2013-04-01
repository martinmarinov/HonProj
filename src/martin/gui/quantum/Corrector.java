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

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.FlatteningPathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashSet;

public class Corrector extends Item {
	
	private final String[] menu_entries = new String[] {"Change direction"};
	
	private final static int DEFAULT_FONT_SIZE = Qubit.DEFAULT_FONT_SIZE;
	
	private static final int ICON_OFFSET = 16;
	private static final int QUBIT_SIZE = 30;
	private final static Stroke DASHED_STROKE = new BasicStroke(4.0f);
	private static final int PICKING_DISTANCE = 10;
	private static final int DEFAULT_ARC_DISTANCE = 100;
	private static final int ARROW_HEIGHT = 15;
	private static final int ARROW_WIDTH = 7;
	
	Qubit i1, i2;
	private int arcd;
	
	private Font font = null;
	
	private boolean select_first = true;
	
	// The triangle end
	int[] trix = new int[3], triy = new int[3];
	
	public enum corrtype {X, Z};
	
	final corrtype t;
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Corrector) {
			final Corrector o = (Corrector) obj;
			return o.i1 == i1 && o.i2 == i2 && o.t == t;
		}
		return false;
	}
	
	public Corrector(final corrtype t) {
		this.t = t;
		arcd = t == corrtype.X ? DEFAULT_ARC_DISTANCE : - DEFAULT_ARC_DISTANCE;
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
		ArrayList<double[]> bpts = generatePoints(getBezier(null, i1.x, i1.y, i2.x, i2.y));
		final int size = bpts.size();
		
		if (size > 1) {
			final double[] first = bpts.get(0);
			final double[] last = bpts.get(size - 1);
			final CubicCurve2D.Double bezier = getBezier(p, (int) first[0], (int) first[1], (int) last[0], (int) last[1]);
			
			bpts = generatePoints(bezier);
			
			int next_to_last_id = 0;
			double[] next_to_last = null;
			for (int i = bpts.size() - 1; i > 0; i--) {
				final double[] temp = bpts.get(i);
				final double ddx = last[0] - temp[0];
				final double ddy = last[1] - temp[1];
				final double dist = Math.sqrt(ddx * ddx + ddy * ddy);
				if (dist > ARROW_HEIGHT) {
					next_to_last = temp;
					next_to_last_id = i;
					break;
				}
			}
			
			if (next_to_last != null) {
				
				final int id_to_get = next_to_last_id != bpts.size() - 1 ? next_to_last_id + 1 : next_to_last_id;
				final double[] todrawlast = bpts.get(id_to_get);
				g.draw(getBezier(p, (int) first[0], (int) first[1], (int) todrawlast[0], (int) todrawlast[1]));
				
				final double ddx = last[0] - next_to_last[0];
				final double ddy = last[1] - next_to_last[1];

				final double dsize = Math.sqrt(ddx * ddx + ddy * ddy);
				final double sx = last[0] - ddx * ARROW_HEIGHT / dsize;
				final double sy = last[1] - ddy * ARROW_HEIGHT / dsize;

				final double nang = Math.atan2(ddy, ddx)+Math.PI/2;
				final int sin = (int) (Math.sin(nang) * ARROW_WIDTH);
				final int cos = (int) (Math.cos(nang) * ARROW_WIDTH);

				final double cp1x = sx + cos;
				final double cp1y = sy + sin;	

				final double cp2x = sx - cos;
				final double cp2y = sy - sin;

				trix[0] = (int) cp1x; triy[0] = (int) cp1y;
				trix[1] = (int) cp2x; triy[1] = (int) cp2y;
				trix[2] = (int) last[0]; triy[2] = (int) last[1];
				g.fillPolygon(trix, triy, 3);
			}
		}
		
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
	
	private CubicCurve2D.Double getBezier(final Point2D.Float p, final int sx, final int sy, final int ex, final int ey) {
		
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
	
	private ArrayList<double[]> generatePoints(CubicCurve2D.Double bezier) {
		final FlatteningPathIterator pi = new FlatteningPathIterator(bezier.getPathIterator(null), 0.1);

		final ArrayList<double[]> path = new ArrayList<double[]>();

		while (!pi.isDone()) {  

			final double[] coordinates = new double[6];
			pi.currentSegment(coordinates);

			final double dx1 = coordinates[0] - i1.x;
			final double dy1 = coordinates[1] - i1.y;
			final double d1 = dx1 * dx1 + dy1 * dy1;
			
			if (d1 > QUBIT_SIZE * QUBIT_SIZE) {
				final double dx2 = coordinates[0] - i2.x;
				final double dy2 = coordinates[1] - i2.y;
				final double d2 = dx2 * dx2 + dy2 * dy2;
				
				if (d2 > QUBIT_SIZE * QUBIT_SIZE)
					path.add(coordinates);
			}

			pi.next();
		}
		
		return path;
	}

	@Override
	boolean isMouseOntop(int x, int y) {
		final Point2D.Float p = new Point2D.Float();
		final ArrayList<double[]> bpts = generatePoints(getBezier(null, i1.x,
				i1.y, i2.x, i2.y));
		final int sizeb = bpts.size();

		if (sizeb > 1) {
			final double[] first = bpts.get(0);
			final double[] last = bpts.get(sizeb - 1);
			final ArrayList<double[]> path = generatePoints(getBezier(p,
					(int) first[0], (int) first[1], (int) last[0],
					(int) last[1]));

			final int size = path.size();

			double[] prev = path.get(0);
			for (int i = 1; i < size; i++) {
				final double[] curr = path.get(i);
				final double dx = x - curr[0];
				final double dy = y - curr[1];
				if (dx * dx + dy * dy < PICKING_DISTANCE * PICKING_DISTANCE)
					return true;
				if (distFromLine(x, y, prev[0], prev[1], curr[0], curr[1]) < PICKING_DISTANCE)
					return true;
				prev = curr;
			}
		}
		return false;
	}
	
	public double distFromLine(double px, double py, double sx, double sy, double ex, double ey) {
		
		if (ex > sx && (px < sx || px > ex))
			return Double.NaN;
		if (sx > ex && (px < ex || px > sx))
			return Double.NaN;
		if (ey > sy && (py < sy || py > ey))
			return Double.NaN;
		if (sy > ey && (py < ey || py > sy))
			return Double.NaN;

		final double ddx = ex - sx;
		final double ddy = ey - sy;
		final double normalLength = Math.sqrt(ddx * ddx + ddy * ddy);
		return Math.abs((px - sx) * ddy - (py - sy) * ddx) / normalLength;
	}

	@Override
	String getIconFileName() {
		return "correction_" + t +".png";
	}

	@Override
	void moveWith(int dx, int dy, int mx, int my, Graphics2D g, Visualizer vis) {
		final int ddx = i1.x - i2.x;
		final int ddy = i1.y - i2.y;
		final double nang = Math.atan2(ddy, ddx)+Math.PI/2;
		
		final double ddirx = Math.cos(nang);
		final double ddiry = Math.sin(nang);
		
		arcd += (int) (ddirx * dx + ddiry * dy);
	}

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
			final int d = (t == corrtype.X ? 1 : -1) * (int) Math.sqrt(dx * dx + dy * dy) / 2;
			
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
	String[] getMenuEntries(Visualizer vis) {
		return menu_entries;
	}

	@Override
	void onMenuEntryClick(int id, Visualizer vis) {
		switch (id) {
		case 0:
			final Qubit temp = i1;
			i1 = i2;
			i2 = temp;
			arcd = -arcd;
			break;
		}
	}

	@Override
	boolean doesItNeedToBeDeleted(final HashSet<Item> dependencies) {
		for (final Item it : dependencies)
			if (it == i1 || it == i2)
				return true;
		return false;
	}
	
	@Override
	protected Item loadFromString(String p, Visualizer vis) {
		final String[] data = stringToStringArray(p);
		int c = 0;
		
		final int id1 = Integer.parseInt(data[c++]);
		final int id2 = Integer.parseInt(data[c++]);
		final corrtype t = corrtype.valueOf(data[c++]);
		final int arcd = Integer.parseInt(data[c++]);
		
		final Qubit q1 = getQubitWithId(id1, vis);
		final Qubit q2 = getQubitWithId(id2, vis);
		
		if (q1 == null || q2 == null) return null;
		
		final Corrector corr = new Corrector(q1, q2, t);
		corr.arcd = arcd;
		return corr;
	}
	
	@Override
	protected String saveToString() {
		final ArrayList<String> data = new ArrayList<String>();
		
		data.add(String.valueOf(i1.id));
		data.add(String.valueOf(i2.id));
		data.add(String.valueOf(t));
		data.add(String.valueOf(arcd));
		
		return stringArrayToString(data.toArray(new String[0]));
	}
}
