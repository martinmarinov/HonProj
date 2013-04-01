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
package martin.math;

import java.util.HashMap;

/**
 * Implements a holder for a double precision floating point number.
 * 
 * @author Martin Marinov
 *
 */
public class MathNumber implements MathsItem {
	
	double number;
	
	/**
	 * Create a double precision floating point item with a given value
	 * 
	 * @param value
	 */
	public MathNumber(final double value) {
		this.number = value;
	}

	@Override
	public boolean hasNegativeSign() {
		return number < 0;
	}

	@Override
	public boolean multiply(MathsItem m) {
		
		if (m instanceof MathNumber) {
			number *= ((MathNumber) m).number;
			return true;
		}
		
		return false;
	}

	@Override
	public boolean add(MathsItem m) {
		
		if (m instanceof MathNumber) {
			number += ((MathNumber) m).number;
			return true;
		}
		
		return false;
	}

	@Override
	public void negate() {
		number = -number;
	}
	
	@Override
	public MathsItem clone() {
		return new MathNumber(number);
	}
	
	@Override
	public boolean equals(Object m) {
		
		if (m instanceof MathNumber)
			return number == ((MathNumber) m).number;
		
		return false;
	}
	
	@Override
	public String toString() {
		
		if (DEBUG)
			return "( [Num]"+number+")";
		
		if ((int) (number) == number)
				return String.valueOf((int) number );
		
		return String.valueOf( number);
	}

	@Override
	public boolean isZero() {
		return number == -0 || number == +0;
	}

	@Override
	public boolean isOne() {
		return number == 1;
	}
	
	public boolean isInteger() {
		return number == (int) number;
	}

	@Override
	public Complex getValue(final HashMap<String, Complex> rules) {
		return new Complex(number, 0);
	}
	
	@Override
	public void simplify() {}
	
	public static MathNumber fromString(final String input) throws Exception {
		try {
			return new MathNumber(Double.parseDouble(input));
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public boolean divide(MathsItem m) {
		
		if (m instanceof MathNumber) {
			final MathNumber mn = (MathNumber) m;
			if (!mn.isInteger() || !isInteger())
				return false;
			
			number /= mn.number;
			return true;
		}
		
		return false;
	}
	
	@Override
	public void complexconjugate() {}

}
