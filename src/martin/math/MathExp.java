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

public class MathExp extends MathFunction {
	
	private final static String name = "exp";
	
	public MathExp(final MathsItem expr) {
		super(name, expr);
	}
	
	private MathExp(final MathsItem expr, boolean negative) {
		super(name, expr);
		this.negative = negative;
	}

	public static MathExp fromString(String input) throws Exception {
		final Tuple<Boolean, MathsItem> res = MathFunction.extractInner(input, name);
		if (res == null) return null;
		return new MathExp(res.y, res.x);
	}

	@Override
	public boolean isZero() {
		return false;
	}

	@Override
	public boolean isOne() {
		return expr.isZero() && !negative;
	}

	@Override
	public Complex getValue(HashMap<String, Complex> rules) {
		final double sign = negative ? -1 : 1;
		
		final Complex result = expr.getValue(rules);
		final double expRe = Math.exp(result.R);
		final double cosIm = Math.cos(result.I);
		final double sinIm = Math.sin(result.I);
		
		result.I = sign * expRe * sinIm;
		result.R = sign * expRe * cosIm;
		
		return result;
	}

	@Override
	public void simplify() {
		expr.simplify();
	}

	@Override
	public boolean multiply(final MathsItem m) {
		if (m instanceof MathExp) {
			final MathExp me = (MathExp) m;
			
			negative = negative != me.negative;
			
			return expr.add(me.expr);
		}
		
		return false;
	}

	@Override
	public boolean add(final MathsItem m) {
		return false;
	}
	
	@Override
	public MathsItem clone() {
		if (Tools.SIMPLIFICATION_ENABLED) {
			if (expr.isZero())
				return new MathNumber(1);
		}
		
		return new MathExp(expr.clone(), negative);
	}

	@Override
	public boolean divide(MathsItem m) {
		return false;
	}

}
