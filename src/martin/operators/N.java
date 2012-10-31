package martin.operators;

import martin.coefficients.CoeffFraction;
import martin.coefficients.CoeffNumber;
import martin.coefficients.CoeffSqrt;
import martin.coefficients.Coefficient;
import martin.quantum.SystemMatrix;

public class N implements Operator {

	private final int qubitId;
	private Coefficient st1 = new CoeffFraction(new CoeffNumber(1), new CoeffSqrt(new CoeffNumber(2)));
	private Coefficient st2 = new CoeffFraction(new CoeffNumber(1), new CoeffSqrt(new CoeffNumber(2)));

	public N(int qubitId) {
		this.qubitId = qubitId;
	}
	
	public N(int qubitId, Coefficient st1, Coefficient st2) {
		this.qubitId = qubitId;
		this.st1 = st1;
		this.st2 = st2;
	}

	@Override
	public void operate(SystemMatrix s) throws Exception {
		if (s.measured[qubitId])
			throw new Exception("The qubit you are trying to set up has been measured already");
		
		if (qubitId > s.mNumbQubits)
			throw new Exception("This qubit does not exist in this system matrix!");

		for (int i = 0; i < s.size; i++)
			s.coeff[i].multiply( ((i >> (s.mNumbQubits - qubitId - 1)) & 1) == 0 ? st1.clone() : st2.clone());

	}

}
