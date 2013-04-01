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

public class MathIm extends MathFunction {

	private final static String name = "Im";
	private boolean real = false;

	public MathIm(final MathsItem expr) {
		super(name, expr);
	}

	private MathIm(final MathsItem expr, boolean negative, boolean real) {
		super(name, expr);
		this.negative = negative;
		this.real = real;
	}

	public static MathIm fromString(String input) throws Exception {
		final Tuple<Boolean, MathsItem> res = MathFunction.extractInner(input, name);
		if (res == null) return null;
		return new MathIm(res.y, res.x, false);
	}

	@Override
	public boolean isZero() {
		return expr.isZero();
	}

	@Override
	public boolean isOne() {
		return getValue(null).isOne() && !negative;
	}

	@Override
	public Complex getValue(final HashMap<String, Complex> rules) {
		
		final Complex result = expr.getValue(rules);
		
		if (negative)
			result.negate();
		
		if (real)
			return result;
		
		final double R = -result.I;
		final double I = result.R;
		result.R = R;
		result.I = I;
		
		return result;
	}

	@Override
	public void simplify() {
		expr.simplify();
	}

	@Override
	public boolean multiply(final MathsItem m) {
		
		
		
		if (m instanceof MathIm) {
			final MathIm im = (MathIm) m;
			
			if (im.negative) {
				im.negate();
				im.expr.negate();
			}
			
			if (expr.multiply(im.expr)) {
				
				if (negative) {
					negate();
					expr.negate();
				}
				
				//System.out.println("Multiplying "+this+" with "+m);
				
				if (real != im.real) {
					//System.out.print("I am "+(real ? "real" : "imag")+" he is "+(im.real ? "real" : "imag")+", therefore I become imaginary : ");
					real = false;
					//System.out.println(this);
				} else if (real == false && im.real == false) {
					//System.out.print("I am "+(real ? "real" : "imag")+" he is "+(im.real ? "real" : "imag")+", therefore I become negative and real : ");
					real = true;
					negate();
					//System.out.println(this);
				}// else {
					//System.out.println("I am "+(real ? "real" : "imag")+" he is "+(im.real ? "real" : "imag")+", therefore I stay as I am : "+this);
				//}
				
				return true;
			}
			
			return false;
		}
	
	    return expr.multiply(m);
	}

	@Override
	public boolean add(final MathsItem m) {
		if (m instanceof MathIm) {
			final MathIm im = (MathIm) m;

			//System.out.print("Adding "+im+" to this "+this+": ");
			
			if (negative) {
				negate();
				expr.negate();
			}
			
			if (im.negative) {
				im.negate();
				im.expr.negate();
			}
			
			//System.out.print("negating remote to "+im+" and this "+this+"; ");

			final boolean result = real == im.real ? expr.add(im.expr) : false;
			
			//System.out.println("result "+this);
			return result;
		}
		
		return false;
	}

	@Override
	public MathsItem clone() {

		if (Tools.SIMPLIFICATION_ENABLED) {

			if (expr.isZero())
				return new MathNumber(0);

			if (real) {
				final MathsItem clone = expr.clone();
				if (negative) {
					negative = !negative;
					clone.negate();
				}
				return clone;
			}

		}

		return new MathIm(expr.clone(), negative, real);
	}
	
	@Override
	public String toString() {
		
		if (DEBUG)
			return "( ["+name+"; "+(negative ? "-" : "+")+"; "+(real? "Re" : "Im")+"]"+expr+")";
		
		
		if (real) {
			if (expr instanceof MathExpression) {
				if (negative)
					return "-Re"+expr;
				else
					return "Re"+expr;
			}
			if (negative)
				return "-Re("+expr+")";
			else
				return "Re("+expr+")";
		}
		
		if (Tools.MATHEMATICA_FRIENDLY_OUTPUT) {
			return "(I*("+expr+"))";
		} else
			return super.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		return false;
	}

	@Override
	public boolean divide(MathsItem m) {
		return false;
	}
	
	@Override
	public void complexconjugate() {
		expr.complexconjugate();
		negate();
	}
	

}
