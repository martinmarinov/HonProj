package martin.coefficients;

public abstract class Coefficient {

	private Coefficient next = null;

	public void multiply(Coefficient c) {
		
		if (this.getClass().equals(c.getClass()) && mmultiply(c))
			return;
		else if (next != null)
			next.multiply(c);
		else
			next = c;
	}

	abstract protected boolean mmultiply(Coefficient c);

	abstract protected String getVisualName();
	
	public abstract Coefficient clone();

	@Override
	public String toString() {
		if (next == null)
			return getVisualName();
		else {
			String thName = getVisualName();
			String ntName = next.toString();

			if (thName.equals(""))
				return ntName;
			else if (ntName.equals(""))
				return thName;
			else
				return "(" + thName + " * " + ntName+")";
		}

	}
	
	public static Coefficient fromString(String input) {
		input = input.trim();
		
		try {
			return new CoeffNumber(Double.parseDouble(input));
		} catch (Exception e) {
			return new CoeffSymbol(input);
		}
	}

}
