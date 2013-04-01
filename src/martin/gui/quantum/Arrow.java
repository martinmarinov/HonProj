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

import java.awt.Cursor;
import java.awt.Graphics2D;

public class Arrow extends Item {
	
	private boolean defcur = true;
	private Item selected = null;
	private int msx, msy;

	@Override
	void renderInstance(Graphics2D g, Visualizer vis) {}

	@Override
	String getIconFileName() {
		return "arrow.png";
	}

	@Override
	void mouseMove(Graphics2D g, int x, int y, Visualizer vis) {

		selected = vis.getItemAt(x, y);
		
		if (selected == null) {
			if (!defcur) vis.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			defcur = true;
		} else {
			if (defcur) vis.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			defcur = false;
		}
		
		msx = x;
		msy = y;
	}
	
	@Override
	void mouseClick(Graphics2D g, int x, int y, Visualizer vis) {
		msx = x;
		msy = y;
	}

	@Override
	void mouseDrag(Graphics2D g, int x, int y, Visualizer vis) {
		if (selected != null) 
			selected.moveWith(x - msx, y - msy, x, y, g, vis);
		msx = x;
		msy = y;
	}

	@Override
	Cursor getCursor() {
		defcur = true;
		return Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
	}

	@Override
	boolean isMouseOntop(int x, int y) {
		return false;
	}

	@Override
	void mousePressed(Graphics2D g, int x, int y, Visualizer vis) {
		msx = x;
		msy = y;
	}

	@Override
	void mouseReleased(Graphics2D g, int x, int y, Visualizer vis) {
		msx = x;
		msy = y;
	}


}
