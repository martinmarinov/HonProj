package martin.coefficients;

public class CoeffSqrt extends CoeffFunction {
	
	private static final String fname = "sqrt";
	
	@Override
	public Coefficient clone() {
		return new CoeffSqrt(arg.clone());
	}

	public CoeffSqrt(Coefficient arg) {
		super(fname, arg);
	}

	@Override
	protected boolean mmultiply(Coefficient c) {
		CoeffSqrt cs = (CoeffSqrt) c;
		this.arg.multiply(cs.arg);
		return true;
	}

}
