package martin.math;

import java.util.HashMap;

/**
 * Contains an item of symbolic maths.
 * 
 * @author Martin Marinov
 *
 */
public interface MathsItem {
	
	static final boolean DEBUG = false;

	/**
	 * If there is an explicit minus sign before this item
	 * 
	 * @return true if there is an explicit minus sign. Helps neat printing.
	 */
	public boolean hasNegativeSign();
	
	/**
	 * Change the sign of this {@link MathsItem}
	 */
	public void negate();
	
	public boolean isZero();
	
	public boolean isOne();
	
	public Complex getValue(final HashMap<String, Complex> rules);
	
	// TODO! Optimize clonging! Cloning must be done ONLY when storing!
	public MathsItem clone();
	
	/**
	 * Simplify the underlying expression.
	 */
	public void simplify();
	
	/**
	 * Multiply current {@link MathsItem} by a new one
	 * 
	 * @param m the item that we would like to multiply with
	 * @return true if multiplication was successful and value of this item was changed, false if multiplication cannot be carried
	 */
	public boolean multiply(final MathsItem m);
	
	/**
	 * Add a value to the current {@link MathsItem}
	 * 
	 * @param m the item to be added
	 * @return if addition was successful and value of this was changed, false if addition cannot be carried out
	 */
	public boolean add(final MathsItem m);
	
	/**
	 * @return the absolute value of the underlying representation
	 */
	public String toString();
	
}
