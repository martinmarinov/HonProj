package martin.operators;

import martin.coefficients.Coefficient;
import martin.quantum.SystemMatrix;

public class X implements Operator {

	final int qubitId;
	final boolean skip;
	
	public X(int qubitId, int s) {
		this.qubitId = qubitId;
		skip = s == 0;
	}

	public X(int qubitId) {
		this.qubitId = qubitId;
		skip = false;
	}

	@Override
	public void operate(SystemMatrix s) throws Exception {
		if (s.measured[qubitId])
			throw new Exception(
					"The qubit you are trying to measure has already been measured!");
		
		if (skip) return;
		
		int mask = 1 << (s.mNumbQubits - qubitId - 1);
				
		for (int i = 0; i < s.size; i++)
			if (((i >> (s.mNumbQubits - qubitId - 1)) & 1) == 0) {
				// if the state of this qubit is 0, swap it with the other one
				
				int second_id = i | mask;
				
				final Coefficient temp = s.coeff[i];
				s.coeff[i] = s.coeff[second_id];
				s.coeff[second_id] = temp;
			}

	}

}
