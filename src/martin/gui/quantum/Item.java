package martin.gui.quantum;

import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

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
	abstract boolean isMouseOntop(int x, int y);
	
	/**
	 * Return the file name that would be used as an icon
	 * @return
	 */
	abstract String getIconFileName();
	
	abstract void moveWith(int dx, int dy, final Graphics2D g, Visualizer vis);
	
	/**
	 * Override if you don't want to use icons from files
	 * @return
	 */
	BufferedImage getIcon() {
		try {
			return icon = ImageIO.read(new File( getIconFileName() ));
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
	abstract void mouseMove(final Graphics2D g, int x, int y, Visualizer vis);
	
	abstract void mouseClick(final Graphics2D g, int x, int y, Visualizer vis);
	
	abstract void mouseDrag(final Graphics2D g, int x, int y, Visualizer vis);
	
	abstract void mousePressed(final Graphics2D g, int x, int y, Visualizer vis);
	
	abstract void mouseReleased(final Graphics2D g, int x, int y, Visualizer vis);
	
	/**
	 * @return the default cursor for the current {@link Item} interaction
	 */
	abstract Cursor getCursor();

}
