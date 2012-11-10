package martin.coefficients;

public class CoeffNumber extends Coefficient {
	
	private double number;
	private boolean unity = false;
	
	public CoeffNumber() {
		number = 1;
		unity = true;
	}
	
	public CoeffNumber(double number) {
		this.number = number;
	}

	@Override
	public Coefficient clone() {
		return unity ? new CoeffNumber() : new CoeffNumber(number);
	}
	
	@Override
	protected boolean mmultiply(Coefficient c) {
		CoeffNumber cn = (CoeffNumber) c;
		
		number *= cn.number;
		
		unity = false;
		return true;
	}

	@Override
	protected String getVisualName() {
		if (unity)
			return "";
		else
			return number + "";
	}

}
