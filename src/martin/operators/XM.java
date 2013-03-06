package martin.operators;

import martin.quantum.SystemMatrix;

public class XM extends M {

	public XM(int qubitId, int t, int s, int b) {
		super(qubitId, t, s, null, b);
	}
	
	protected void perform(int i, final SystemMatrix sm) {
		final int state1 = (i >> (sm.mNumbQubits - qubitId - 1)) & 1;
		
		sm.coeff[i].multiply(ONE_OVER_SQRT_2);
		
		if (state1 == 1 && b == 1)
				sm.coeff[i].multiply(MINUS_ONE);

	}
	
	@Override
	public String toString() {
		return "XM(id=" + qubitId + ", t=" + t + ", s=" + s
				+ ", b=" + b + ")";
	}

}
