package martin.coefficients;

public abstract class CoeffFunction extends Coefficient {
	
	private final String name;
	protected Coefficient arg;
	
	public CoeffFunction(String name, Coefficient arg) {
		this.name = name;
		this.arg = arg;
	}

	abstract protected boolean mmultiply(Coefficient c);

	@Override
	protected String getVisualName() {
		return name+"("+arg+")";
	}

}
