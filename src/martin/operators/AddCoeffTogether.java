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
package martin.operators;

import martin.math.MathExpression;
import martin.math.MathNumber;
import martin.quantum.SystemMatrix;

public class AddCoeffTogether implements Operator {

	@Override
	public void operate(final SystemMatrix s) throws Exception {
		int invmask = 0;
		final boolean[] measured = s.measured;
		final int length = measured.length;
		final int size = s.size;
		
		for (int i = 0; i < length; i++)
			if (!measured[i])
				invmask |=	1 << (length - i - 1);
		
		for (int i = 0; i < size; i++) {
			final int base = i & invmask;
			if (i != base) {

				s.coeff[base].add(s.coeff[i]);
				s.coeff[i] = new MathExpression();
				s.coeff[i].add(new MathNumber(0));
				s.coeff[base].simplify();
			}
		}

	}
	
	@Override
	public String toString() {
		return "Add coefficients together";
	}

}
