package martin.operators;

import martin.coefficients.CoeffNumber;
import martin.coefficients.Coefficient;
import martin.quantum.SystemMatrix;

public class E implements Operator {
	
	private final int bitId1, bitId2;
	private final static Coefficient MINUS_ONE = new CoeffNumber(-1);
	
	public E(int bitId1, int bitId2) {
		this.bitId1 = bitId1;
		this.bitId2 = bitId2;
	}

	@Override
	public void operate(SystemMatrix s) throws Exception {
		if (s.measured[bitId1] || s.measured[bitId2])
			throw new Exception("One of the bits you are trying to entangle has already been measured!");
			
		for (int i = 0; i < s.size; i++) {
			int state1 = (i >> (s.mNumbQubits - bitId1 - 1)) & 1; // state of control qubit
			int state2 = (i >> (s.mNumbQubits - bitId2 - 1)) & 1; // state of second bit
			
			if (state1 == 1 && state2 == 1)
				s.coeff[i].multiply(MINUS_ONE.clone());
			
		}
	}
	
	
	
}
