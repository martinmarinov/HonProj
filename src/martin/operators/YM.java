package martin.operators;

import martin.math.MathIm;
import martin.math.MathNumber;
import martin.math.MathsItem;
import martin.quantum.SystemMatrix;

public class YM extends M {

	private final static MathsItem IM = new MathIm(new MathNumber(1));
	
	public YM(int qubitId, int t, int s, int b) {
		super(qubitId, t, s, null, b);
	}
	
	protected void perform(int i, final SystemMatrix sm) {
		final int state1 = (i >> (sm.mNumbQubits - qubitId - 1)) & 1;
		
		sm.coeff[i].multiply(ONE_OVER_SQRT_2);
		
		if (state1 == 1) {
			sm.coeff[i].multiply(IM);
			
			if (b == 1) // if negative branch
				sm.coeff[i].multiply(MINUS_ONE);
		}
	}
	
	@Override
	public String toString() {
		return "YM(id=" + qubitId + ", t=" + t + ", s=" + s
				+ ", b=" + b + ")";
	}
	
}
