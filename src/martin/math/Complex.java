package martin.math;

public class Complex {
	
	public double R = 0;
	public double I = 0;
	
	public Complex(final double R, final double I) {
		this.R = R;
		this.I = I;
	}
	
	public Complex(final String number) throws Exception {
		final Complex c = MathsParser.parse(number).getValue(null);
		
		if (c.isNaN())
			throw new Exception("'"+number+"' is not a valid complex number!");
		
		R = c.R;
		I = c.I;
	}
	
	public void multiply(final Complex n) {
		final double nR = R * n.R - I * n.I;
		final double nI = R * n.I + I * n.R;
		R = nR;
		I = nI;
	}
	
	public void add(final Complex n) {
		R += n.R;
		I += n.I;
	}
	
	public void negate() {
		R = -R;
		I = -I;
	}
	
	public boolean isZero() {
		return R == 0 && I == 0;
	}
	
	public boolean isOne() {
		return R == 1 && I == 0;
	}
	
	public Complex clone() {
		return new Complex(R, I);
	}
	
	public boolean isNaN() {
		return Double.isNaN(R) || Double.isNaN(I);
	}
	
	@Override
	public String toString() {
		final String real = (int) R == R ? String.valueOf((int) R) : String.valueOf(R);
		final String im = "Im(" + ( (int) I == I ? String.valueOf((int) I) : String.valueOf(I) ) + ")";
		
		if (I == 0)
			return real;
		if (R == 0)
			return im;
		else
			return "("+real+"+"+im+")";
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Complex) {
			
			final Complex c = (Complex) obj;
			
			if (isNaN() || c.isNaN())
				return false;
			
			return R == c.R && I == c.I;
		}
		return false;
	}
	
	/**
	 * Compare the two numbers up to an accuracy of 100*ACCURACY % of the number
	 * @param c
	 * @param ACCURACY
	 * @return
	 */
	public boolean similarValue(final Complex c, double ACCURACY) {
		if (isNaN() && c.isNaN())
			return true;
		
		if (ACCURACY < 0.0000000001) ACCURACY = 0.0000000001;
		
		final double racc = ACCURACY * Math.abs(R);
		final double iacc = ACCURACY * Math.abs(I);
		return (Math.abs(c.R - R) <= racc) && (Math.abs(c.I - I) <= iacc);
	}
}
