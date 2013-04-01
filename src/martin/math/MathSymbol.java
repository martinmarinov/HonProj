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

public class MathSymbol implements MathsItem {
	
	private final String symbol;
	private boolean negative = false;
	private boolean zero = false;
	private boolean complexconj = false;
	
	public MathSymbol(final String symbol) {
		this.symbol = symbol;
		negative = false;
		zero = false;
	}
	
	private MathSymbol(final String symbol, boolean negative, boolean zero, boolean complexconj) {
		this.symbol = symbol;
		this.negative = negative;
		this.zero = zero;
		this.complexconj = complexconj;
	}

	@Override
	public boolean hasNegativeSign() {
		return negative;
	}

	@Override
	public void negate() {
		negative = !negative;
	}

	@Override
	public boolean isZero() {
		return zero;
	}

	@Override
	public boolean isOne() {
		return false;
	}

	@Override
	public Complex getValue(final HashMap<String, Complex> rules) {
		
		if (zero) return new Complex(0, 0);
		
		Complex value = rules == null ? null : rules.get(symbol);
		
		if (value == null)
			return new Complex(Double.NaN, Double.NaN);
		
		value = value.clone();
		
		if (negative)
			value.negate();
		
		if (complexconj)
			value.I = - value.I;
		
		return value;
	}

	@Override
	public void simplify() {
	}

	@Override
	public boolean multiply(MathsItem m) {
		return false;
	}

	@Override
	public boolean add(MathsItem m) {
		if (m instanceof MathSymbol) {
			final MathSymbol m2 = (MathSymbol) m;
			
			if (equals(m) && m2.negative != negative) {
				zero = true;
				return true;
			}
			
		}
		return false;
	}
	
	@Override
	public MathsItem clone() {
		if (zero)
			return new MathNumber(0);
		
		if (negative) {
			final MathExpression e = new MathExpression();
			e.add(new MathSymbol(symbol, false, false, complexconj));
			e.multiply(new MathNumber(-1));
			return e;
		}
		
		return new MathSymbol(symbol, negative, false, complexconj);
	}
	
	public static MathSymbol fromString(String input) throws Exception {
		boolean negative = false;
		
		if (negative = input.startsWith("-"))
			input = input.substring(1).trim();
		
		for (int i = 0; i < input.length(); i++)
			if (!Character.isLetterOrDigit(input.charAt(i)))
					return null;
		
		return new MathSymbol(input, negative, false, false);
	}
	
	@Override
	public String toString() {
		
		if (DEBUG)
			return " [Sym]" + (negative ? "-" : "") + symbol + (complexconj ? "^" : "");
		
		if (negative)
			return "-"+symbol+ (complexconj ? "^" : "");
		else
			return symbol+ (complexconj ? "^" : "");
	}
	
	@Override
	public boolean equals(Object paramObject) {
		if (paramObject instanceof MathSymbol) {
			final MathSymbol m =  ((MathSymbol) paramObject);
			return negative == m.negative && m.symbol.equals(symbol);
		}
			
		return false;
	}

	@Override
	public boolean divide(MathsItem m) {
		return false;
	}
	
	@Override
	public void complexconjugate() {
		complexconj = !complexconj;
	}

}
