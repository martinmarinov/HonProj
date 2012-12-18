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
	
	private int getItemsCountAt(int x, int y, Visualizer vis, Object same) {
		int c = 0;
		
		for (final Item i : vis.items)
			if (i.isMouseOntop(x, y) && same.getClass().equals(i.getClass()))
				c++;
		
		return c;
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
	void moveWith(int dx, int dy, int mx, int my, final Graphics2D g, Visualizer vis) {

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

	@Override
	String[] getMenuEntries() {return null;}

	@Override
	void onMenuEntryClick(int id) {}



}
