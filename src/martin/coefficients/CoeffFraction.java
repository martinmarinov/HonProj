package martin.coefficients;

public class CoeffFraction extends Coefficient {

	private final Coefficient num, den;
	
	@Override
	public Coefficient clone() {
		return new CoeffFraction(num.clone(), den.clone());
	}
	
	public CoeffFraction(Coefficient num, Coefficient den) {
		this.num = num;
		this.den = den;
	}
	
	protected boolean mmultiply(Coefficient c)
	{
		CoeffFraction cf = (CoeffFraction) c;
		
		num.multiply(cf.num);
		den.multiply(cf.den);
		
		return true;
	}
	
	@Override
	protected String getVisualName() {
		return "("+num+" / "+den+")";
	}

}
