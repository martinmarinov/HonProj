package martin.math;

import java.util.HashMap;

import martin.quantum.tools.Tuple;

public class MathSqrt extends MathFunction {
	
	private final static String name = "sqrt";

	public MathSqrt(MathsItem expr) {
		super(name, expr);
	}
	
	private MathSqrt(final MathsItem expr, boolean negative) {
		super(name, expr);
		this.negative = negative;
	}
	
	public static MathSqrt fromString(String input) throws Exception {
		final Tuple<Boolean, MathsItem> res = MathFunction.extractInner(input, name);
		if (res == null) return null;
		return new MathSqrt(res.y, res.x);
	}

	@Override
	public boolean isZero() {
		return expr.isZero();
	}

	@Override
	public boolean isOne() {
		return expr.isOne();
	}

	@Override
	public Complex getValue(HashMap<String, Complex> rules) {
		final double sign = negative ? -1 : 1;
		
		final Complex result = expr.getValue(rules);
		
		final double r = Math.sqrt(result.R*result.R + result.I*result.I);
		final double sgnI = result.I == 0 ? 0 : (result.I > 0 ? 1 : -1);
		
		final double R = sign * Math.sqrt((r+result.R) / 2d);
		final double I = sign * sgnI * Math.sqrt((r-result.R) / 2d);
		
		result.R = R;
		result.I = I;
		
		return result;
	}

	@Override
	public void simplify() {
		expr.simplify();
	}

	@Override
	public boolean multiply(MathsItem m) {
		if (m instanceof MathSqrt) {
			final MathSqrt me = (MathSqrt) m;
			return expr.multiply(me.expr);
		}
		
		return false;
	}

	@Override
	public boolean add(MathsItem m) {
		return false;
	}

	@Override
	public MathsItem clone() {
		
		final Complex value = getValue(null);
		
		if (!value.isNaN() && value.I == 0 && value.R == (int) value.R)
				return new MathNumber(value.R);
		
		return new MathSqrt(expr.clone(), negative);
	}

}
