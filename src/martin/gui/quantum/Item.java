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
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashSet;

import javax.imageio.ImageIO;

/**
 * The {@link Item} serves two purposes. <br/>
 * <ul>
 * <li>Creating of similar {@link Item}s by interactively using the mouse and clicking onto a {@link Visualizer}.
 * See {@link #mouseClick(Graphics2D, int, int, Visualizer)}, {@link #mouseDrag(Graphics2D, int, int, Visualizer)},
 * {@link #mouseMove(Graphics2D, int, int, Visualizer)} and {@link #getCursor()} for information how this mode works.</li>
 * <li>For actually rendering the {@link Item} on the screen one it has been created. See {@link #renderInstance(Graphics2D)}</li>
 * </ul>
 * Each item should implement its own {@link #getIconFileName()} in order for the interaction to work properly.
 * 
 * @author Martin Marinov
 *
 */
public abstract class Item  {
	
	protected BufferedImage icon;
	
	/**
	 * Render the item onto the visualizer. This is for active qubits, not for their creation.
	 * @param g the graphics that is used to draw ontop of
	 */
	abstract void renderInstance(final Graphics2D g, Visualizer vis);
	
	/**
	 * @param x
	 * @param y
	 * @return true if current {@link Item} contains points x and y on screen
	 */
	boolean isMouseOntop(int x, int y) {return false;};
	
	/**
	 * Return the file name that would be used as an icon
	 * @return
	 */
	abstract String getIconFileName();
	
	/**
	 * Someone requires the item to move with a certain amount
	 * @param dx the amount in the x direction that the mouse moved
	 * @param dy the amount in the y direction that the mouse moved
	 * @param mx the x position of the mouse
	 * @param my the y position of the mouse
	 * @param g
	 * @param vis
	 */
	void moveWith(int dx, int dy, int mx, int my, final Graphics2D g, Visualizer vis) {};
	
	/**
	 * Override if you don't want to use icons from files
	 * @return
	 */
	BufferedImage getIcon() {
		try {
			return icon = ImageIO.read(this.getClass().getResourceAsStream(getIconFileName()));
		} catch (IOException e) {
			return null;
		}
	}

	// Creator
	
	/**
	 * If current creator is being selected and moved. Keep in mind, that
	 * this could be only used for spawning new instance of this {@link Item} to
	 * the {@link Visualizer} or for interactively interacting with the {@link Visualizer}
	 * @param g
	 * @param x
	 * @param y
	 * @param vis
	 */
	void mouseMove(final Graphics2D g, int x, int y, Visualizer vis) {};
	
	void mouseClick(final Graphics2D g, int x, int y, Visualizer vis) {};
	
	void mouseDrag(final Graphics2D g, int x, int y, Visualizer vis) {};
	
	void mousePressed(final Graphics2D g, int x, int y, Visualizer vis) {};
	
	void mouseReleased(final Graphics2D g, int x, int y, Visualizer vis) {};
	
	/**
	 * @return the default cursor for the current {@link Item} interaction
	 */
	Cursor getCursor() {return null;};
	
	String[] getMenuEntries(Visualizer vis) {return null;};
	
	void onMenuEntryClick(int id, Visualizer vis) {};
	
	/** Returns true if the current item needs to be destroyed. The list of dependencies
	 * are the {@link Item}s that would be deleted, so if the curreint {@link Item} depends
	 * on any of those, it should return true so that it will be deleted!
	 * @param dependencies
	 * @return
	 */
	boolean doesItNeedToBeDeleted(HashSet<Item> dependencies) {return false;};
	
	/** This will get called when items are deleted on the screen so the items that survive can rearange themselves accordingly */
	protected void onPostLayoutChanged(Visualizer vis){};

	protected String saveToString() {return null;};
	
	protected Item loadFromString(String p, Visualizer vis) {return null;};
	
	/**
	 * Converts an array of strings to a string
	 * @param arr
	 * @return
	 */
	protected String stringArrayToString(final String[] arr) {
		final StringBuilder sb = new StringBuilder();
		
		for (int i = 0; i < arr.length; i++) {
			if (i == 0)
				sb.append(arr[0]);
			else
				sb.append("\0" + arr[i]);
		}
		
		return sb.toString();
	}
	
	/**
	 * Converts a string to an array of strings
	 * @param input
	 * @return
	 */
	protected String[] stringToStringArray(final String input) {
		return input.split("\0");
	}
	
	// a helper
	protected static Qubit getQubitWithId(final int id, final Visualizer vis) {
		for (final Item i : vis.items)
			if (i instanceof Qubit) {
				final Qubit q = (Qubit) i;
				if (q.id == id)
					return q;
			}
				
		return null;
	}
}
