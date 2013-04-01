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

import martin.quantum.tools.Tools;
import martin.quantum.tools.Tuple;

public abstract class MathFunction implements MathsItem {
	
	protected final String name;
	protected final MathExpression expr;
	protected boolean negative = false;
	
	public MathFunction(final String name, final MathsItem expr) {
		this.name = name;
		
		if (expr instanceof MathExpression)
			this.expr = (MathExpression) expr;
		else {
			final MathExpression e = new MathExpression();
			e.add(expr);
			this.expr = e;
		}
	}
	
	public static Tuple<Boolean, MathsItem> extractInner(String input, final String name) throws Exception {
		boolean negative = false;
		
		if (negative = input.startsWith("-"))
			input = input.substring(1).trim();
		
		if (!input.startsWith(name+"("))
			return null;
		
		return new Tuple<Boolean, MathsItem>(negative, MathsParser.parse(Tools.trimAndCheckBrackets(input.substring(name.length()))));
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
	public abstract boolean isZero();
	
	@Override
	public abstract boolean isOne();
	
	@Override
	public abstract Complex getValue(HashMap<String, Complex> rules);
	
	@Override
	public abstract void simplify();
	
	@Override
	public abstract boolean multiply(final MathsItem m);
	
	@Override
	public abstract boolean add(final MathsItem m);
	
	@Override
	public String toString() {
		
		String name = this.name;
		if (Tools.MATHEMATICA_FRIENDLY_OUTPUT) name = name.substring(0, 1).toUpperCase() + name.substring(1);
		
		if (expr instanceof MathExpression) {
			if (negative)
				return "-"+name+expr;
			else
				return name+expr;
		}
		if (negative)
			return "-"+name+"("+expr+")";
		else
			return name+"("+expr+")";
	}
	
	@Override
	public abstract MathsItem clone();
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof MathFunction) {
			final MathFunction e = (MathFunction) obj;
			return name.equals(e.name) && e.negative == negative && e.expr.equals(expr);
		}
		return false;
	}
	
	@Override
	public void complexconjugate() {
		expr.complexconjugate();
	}

}
