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
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.HashSet;


public class Entangler extends Item {
	
	private final String[] menu_entries = new String[] {};
	
	public static final int QUBIT_SIZE = 32;
	
	private static final int ICON_OFFSET = 16;
	private final static Stroke DASHED_STROKE = new BasicStroke(4.0f);
	private static final int PICKING_DISTANCE = 6;
	
	Qubit i1, i2;
	
	private boolean select_first = true;
	
	public Entangler() {}
	
	private Entangler(Qubit i1, Qubit i2) {
		this.i1 = i1;
		this.i2 = i2;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Entangler) {
			final Entangler o = (Entangler) obj;
			return (o.i1 == i1 && o.i2 == i2) || (o.i1 == i2 && o.i2 == i1);
		}
		return false;
	}

	@Override
	void renderInstance(Graphics2D g, Visualizer vis) {
		final int dx = i2.x - i1.x;
		final int dy = i2.y - i1.y;
		final double d = Math.sqrt(dx * dx + dy * dy);
		final double cos = dx / d;
		final double sin = dy / d;

		final int sx = i1.x + (int) (cos * QUBIT_SIZE);
		final int sy = i1.y + (int) (sin * QUBIT_SIZE);
		final int ex = i2.x - (int) (cos * QUBIT_SIZE);
		final int ey = i2.y - (int) (sin * QUBIT_SIZE);
		
		final Stroke def = g.getStroke();
		g.setStroke(DASHED_STROKE);
		g.drawLine(sx, sy, ex, ey);
		g.setStroke(def);
	}

	@Override
	boolean isMouseOntop(int x, int y) {
		final double dist = distFromLine(x, y);
		
		if (dist < PICKING_DISTANCE)
			return true;
		
		return false;
	}
	
	public double distFromLine(int px, int py) {
		
		final int dx = i2.x - i1.x;
		final int dy = i2.y - i1.y;
		final double d = Math.sqrt(dx * dx + dy * dy);
		final double cos = dx / d;
		final double sin = dy / d;

		final int sx = i1.x + (int) (cos * QUBIT_SIZE);
		final int sy = i1.y + (int) (sin * QUBIT_SIZE);
		final int ex = i2.x - (int) (cos * QUBIT_SIZE);
		final int ey = i2.y - (int) (sin * QUBIT_SIZE);
		
		if (ex > sx && (px < sx || px > ex))
			return Double.NaN;
		if (sx > ex && (px < ex || px > sx))
			return Double.NaN;
		if (ey > sy && (py < sy || py > ey))
			return Double.NaN;
		if (sy > ey && (py < ey || py > sy))
			return Double.NaN;

		final int ddx = ex - sx;
		final int ddy = ey - sy;
		final double normalLength = Math.sqrt(ddx * ddx + ddy * ddy);
		return Math.abs((px - sx) * dy - (py - sy) * dx) / normalLength;
	}

	@Override
	String getIconFileName() {
		return "entangle.png";
	}

	@Override
	void moveWith(int dx, int dy, int mx, int my, Graphics2D g, Visualizer vis) {}

	@Override
	void mouseMove(Graphics2D g, int x, int y, Visualizer vis) {
		if (select_first)
			g.drawImage(icon, x-ICON_OFFSET, y-icon.getHeight()/2, vis);
		else {
			final Stroke def = g.getStroke();
			g.setStroke(DASHED_STROKE);
			g.drawLine(i1.x, i1.y, x, y);
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
				
				vis.addItem(new Entangler(i1, i2));
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
		
		final Qubit q1 = getQubitWithId(id1, vis);
		final Qubit q2 = getQubitWithId(id2, vis);
		
		if (q1 == null || q2 == null) return null;
		
		return new Entangler(q1, q2);
	}
	
	@Override
	protected String saveToString() {
		final ArrayList<String> data = new ArrayList<String>();
		
		data.add(String.valueOf(i1.id));
		data.add(String.valueOf(i2.id));
		
		return stringArrayToString(data.toArray(new String[0]));
	}

}
