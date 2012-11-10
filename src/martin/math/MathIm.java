package martin.math;

import java.util.HashMap;

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
		return getValue(null).isOne();
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
			
			if (expr.multiply(im.expr.clone())) {
				
				if (real != im.real)
					real = false;
				
				if (real == false && real == im.real) {
					real = true;
					negate();
				}
				
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
			
			return real == im.real ? expr.add(im.expr.clone()) : false;
		}
		
		return false;
	}

	@Override
	public MathsItem clone() {
		if (real) {
			final MathsItem clone = expr.clone();
			if (negative) {
				negative = !negative;
				clone.negate();
			}
			return clone;
		} else
			return new MathIm(expr.clone(), negative, real);
	}
	
	@Override
	public String toString() {
		
		if (DEBUG)
			return "( ["+name+"; "+(negative ? "-" : "+")+"; "+(real? "Re" : "Im")+"]"+expr+")";
		
		if (real) {
			if (expr instanceof Expr2) {
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
					
		return super.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		return false;
	}

}
