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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import martin.quantum.tools.Tools;
import martin.quantum.tools.Tuple;

public class MathFract implements MathsItem {
	
	private MathExpression num, den;
	
	public MathFract(final MathsItem num, final MathsItem den) {
		if (num instanceof MathExpression)
			this.num = (MathExpression) num;
		else {
			final MathExpression e = new MathExpression();
			e.add(num);
			this.num = e;
		}
		
		if (den instanceof MathExpression)
			this.den = (MathExpression) den;
		else {
			final MathExpression e = new MathExpression();
			e.add(den);
			this.den = e;
		}
	}
	
	public static MathFract fromString(final String input) throws Exception {
		final ArrayList<Tuple<char[], String>> multiples = Tools.splitByTopLevel(input, new char[]{'/'}, false);
		
		if (multiples == null || multiples.size() != 2)
			return null;
		
		final MathsItem num = MathsParser.parse(Tools.trimAndCheckBrackets( multiples.get(0).y ));
		final MathsItem den = MathsParser.parse(Tools.trimAndCheckBrackets( multiples.get(1).y ));
		
		return new MathFract(num, den);
	}

	@Override
	public boolean hasNegativeSign() {
		return false;
	}

	@Override
	public void negate() {
		num.negate();
	}

	@Override
	public boolean isZero() {
		return num.isZero();
	}

	@Override
	public boolean isOne() {
		return num.equals(den);
	}

	@Override
	public Complex getValue(HashMap<String, Complex> rules) {
		final Complex n = num.getValue(rules);
		final Complex d = den.getValue(rules);
		
		final double c2d2 = d.I*d.I + d.R*d.R;
		
		final double R = (d.R*n.R+d.I*n.I) / c2d2;
		final double I = (d.R*n.I-d.I*n.R) / c2d2;
		
		n.R = R;
		n.I = I;
		return n;
	}

	@Override
	public void simplify() {
		
		if (!Tools.SIMPLIFICATION_ENABLED)
			return;
		
		num.simplify();
		den.simplify();
		
		final Collection<Integer> ints = new ArrayList<Integer>();

		if (!putIntsInHere(num, ints) || !putIntsInHere(den, ints))
			return;

		if (ints.isEmpty())
			return;

		final int dividor = gcd(ints);
		if (dividor != 1) {
//			System.out.print("Common dividor of [");
//			for (final Integer in : ints) System.out.print(in+", ");
//			System.out.println("] is "+dividor+" for "+this);
			
			final MathNumber numb = new MathNumber(dividor);

			if (num.divide(numb)) {
				if (!den.divide(numb)) {
					num.multiply(numb);
				} else {
					den.simplify();
					num.simplify();
				}
			}

		}
	}
	
	/** Takes all of the int coefficients of a MathExpression. Returns false on error */
	private boolean putIntsInHere(final MathExpression from, final Collection<Integer> ints) {
		for (final HashSet<MathsItem> mult : from.items) {
			int count = 0;
			
			for (final MathsItem item : mult)
				if (item instanceof MathNumber) {
					final MathNumber mn = (MathNumber) item;
					if (count != 0) return false;
					if (mn.isInteger()) {
						ints.add((int) mn.number);
						count++;
					}
				}
			
			if (count != 1) return false;
		}
		
		return true;
	}
	
	/** Return greatest common devisor */
	private static int gcd(final Iterable<Integer> numbers) {
		final int minn = min(numbers);
		
		outer : for (int n = minn; n >= 1; n--) {
			
			for (final Integer in : numbers)
				if (in % n != 0) continue outer;
			
			return n;
		}
		
		return 1;
	}
	
	private static Integer min(final Iterable<Integer> numbers) {
		Integer ans = null;
		for (final Integer n : numbers)
			if (ans == null || n < ans) ans = n;
		return ans;
	}

	@Override
	public boolean multiply(final MathsItem m) {
		if (m instanceof MathFract) {
			
			final MathFract mul = (MathFract) m;
			
			num.multiply(mul.num);
			den.multiply(mul.den);
			
			return true;
		} else if (m instanceof MathsItem)
			return num.multiply(m);
					
		return false;
	}

	@Override
	public boolean add(MathsItem m) {
		if (m instanceof MathFract) {
			
			final MathFract ad = (MathFract) m;
			
			if (ad.den.equals(den))
				return num.add(ad.num);
			
			final MathExpression nD = (MathExpression) den.clone();
			nD.multiply(ad.den);
			
			final MathExpression nN1 = (MathExpression) num.clone();
			nN1.multiply(ad.den);
			
			final MathExpression nN2 = (MathExpression) ad.num.clone();
			nN2.multiply(den);
			
			final MathExpression nN = new MathExpression();
			nN.add(nN1);
			nN.add(nN2);
			
			den = nD;
			num = nN;
			
			return true;
		}
		
		return false;
	}
	
	@Override
	public MathsItem clone() {
		if (den.isZero())
			return new MathNumber(Double.NaN);
		
		return new MathFract(num.clone(), den.clone());
	}
	
	@Override
	public String toString() {
		return "("+num+"/"+den+")";
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof MathFract) {
			final MathFract mf = (MathFract) obj;
			return num.equals(mf.num) && den.equals(mf.den);
		} else if (obj instanceof MathsItem)
			return getValue(null).equals(((MathsItem) obj).getValue(null));
		
		return false;
	}

	@Override
	public boolean divide(MathsItem m) {
		return false;
	};
	
	@Override
	public void complexconjugate() {
		num.complexconjugate();
		den.complexconjugate();
	}

}
