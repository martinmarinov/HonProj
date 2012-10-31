package martin.operators;

import martin.coefficients.CoeffNumber;
import martin.coefficients.Coefficient;
import martin.quantum.SystemMatrix;

public class Z implements Operator {
	
	private final static Coefficient MINUS_ONE = new CoeffNumber(-1);
	final int qubitId;
	final boolean skip;
	
	public Z(int qubitId, int s) {
		this.qubitId = qubitId;
		skip = s == 0;
	}

	public Z(int qubitId) {
		this.qubitId = qubitId;
		skip = false;
	}

	@Override
	public void operate(SystemMatrix s) throws Exception {
		if (s.measured[qubitId])
			throw new Exception(
					"The qubit you are trying to measure has already been measured!");
		
		if (skip) return;
		
		for (int i = 0; i < s.size; i++)
			if ( ( ( i >> ( s.mNumbQubits - qubitId - 1 ) ) & 1) == 1) s.coeff[i].multiply(MINUS_ONE.clone());

	}

}
