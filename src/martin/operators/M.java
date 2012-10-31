package martin.operators;

import martin.coefficients.CoeffExp;
import martin.coefficients.CoeffNumber;
import martin.coefficients.CoeffSymbol;
import martin.coefficients.Coefficient;
import martin.quantum.SystemMatrix;

public class M implements Operator {
	
	private final static Coefficient Pi = new CoeffSymbol("Pi"); // TODO BETTER WAY OF HANDLING?
	private final static Coefficient MINUS_ONE = new CoeffNumber(-1);

	private final int t, s, qubitId, b;
	private final Coefficient alpha;

	public M(int qubitId, int t, int s, Coefficient alpha, int b) {
		this.alpha = alpha;
		this.t = t;
		this.s = s;
		this.b = b;
		this.qubitId = qubitId;
	}

	@Override
	public void operate(SystemMatrix sm) throws Exception {
		if (sm.measured[qubitId])
			throw new Exception(
					"The qubit you are trying to measure has already been measured!");

		sm.measured[qubitId] = true;
		
		for (int i = 0; i < sm.size; i++) {
			
			// basis of the control bit
			int state1 = (i >> (sm.mNumbQubits - qubitId - 1)) & 1;

			if (state1 == 1) {
				
				sm.coeff[i].multiply(new CoeffExp( new CoeffSymbol( (s == 1 ? "-" : "") +"i " + alpha + (t == 1 ? " + " + Pi : "") ) )); // TODO handle addition
				
				if (b == 1) // if negative branch
					sm.coeff[i].multiply(MINUS_ONE.clone());
			}
		}
	}

}
