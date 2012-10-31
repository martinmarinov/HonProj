package martin.coefficients;

public class CoeffExp extends CoeffFunction {
	
	private static final String fname = "exp";
	
	@Override
	public Coefficient clone() {
		return new CoeffExp(arg.clone());
	}

	public CoeffExp(Coefficient arg) {
		super(fname, arg);
	}

	@Override
	protected boolean mmultiply(Coefficient c) {
		return false;
	}

}
