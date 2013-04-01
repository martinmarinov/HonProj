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
import martin.math.MathFract;
import martin.math.MathNumber;
import martin.math.MathSqrt;
import martin.math.MathsItem;
import martin.quantum.SystemMatrix;
import martin.quantum.tools.Tools;

public class I implements Operator {
	
	private static final MathsItem ONE_OVER_SQRT_2 = new MathFract(new MathNumber(1), new MathSqrt(new MathNumber(2)));
	private final MathsItem[] items;
	private final int inpqubits;
	
	public I(final MathsItem ... items) throws Exception {
		
		this.items = items;
		this.inpqubits = Tools.powerOfTwo(this.items.length);
	}

	@Override
	public void operate(SystemMatrix s) throws Exception {
	
		int nremaining = s.mNumbQubits - inpqubits;
		final MathsItem c = new MathExpression();
		c.add(new MathNumber(1));
		for (int i = 0; i < nremaining; i++)
			c.multiply(ONE_OVER_SQRT_2);
		
		for (int i = 0; i < s.size; i++) {
			s.coeff[i].multiply(items[i >> nremaining]);
			s.coeff[i].multiply(c);
			s.coeff[i].simplify();
		}
	}
	
//	private final static int reverse(final int number, final int size) {
//		int r = 0;
//		for (int i = 0; i < size; i++)
//			r = setBit(i, r, getBit(size-i-1, number));
//		return r;
//	}
//	
//	private final static int getBit(final int id, final int number) {
//		return (number >> id) & 1;
//	}
//	
//	private final static int setBit(final int id, final int number, final int val) {
//		return (val == 1) ?
//			((1 << id) | number) :
//			((((1 << 31) - 1) ^ (1 << id)) & number);
//	}
	
	@Override
	public String toString() {
		String ans = "I("+items[0];
		
		for (int i = 1; i < items.length; i++)
			ans+=", "+items[i];
		
		return ans+")";
	}

}
