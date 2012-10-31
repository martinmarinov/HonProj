package martin.coefficients;

public class CoeffSymbol extends Coefficient {
	
	String name;
	
	@Override
	public Coefficient clone() {
		return new CoeffSymbol(name+"");
	}
	
	public CoeffSymbol(String name) {
		this.name = name;
	}
	
	protected boolean mmultiply(Coefficient c)
	{
		return false;
	}
	
	@Override
	protected String getVisualName() {
		return name;
	}

}
