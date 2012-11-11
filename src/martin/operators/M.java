package martin.operators;

import martin.math.MathExpression;
import martin.math.MathExp;
import martin.math.MathFract;
import martin.math.MathIm;
import martin.math.MathNumber;
import martin.math.MathSqrt;
import martin.math.MathSymbol;
import martin.math.MathsItem;
import martin.quantum.SystemMatrix;

public class M implements Operator {
	
	private final static MathsItem Pi = new MathSymbol("Pi"); // TODO BETTER WAY OF HANDLING?
	private final static MathsItem MINUS_ONE = new MathNumber(-1);
	private final static MathsItem ONE_OVER_SQRT_2 = new MathFract(new MathNumber(1), new MathSqrt(new MathNumber(2)));;

	private final int t, s, qubitId, b;
	private final MathsItem alpha;

	public M(int qubitId, int t, int s, MathsItem alpha, int b) {
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
				// 1/sqrt(2)
				
				final MathExpression angle = new MathExpression();
				angle.add(alpha);
				if (s == 1)
					angle.multiply(MINUS_ONE);
				if (t == 1)
					angle.add(Pi);
				
				sm.coeff[i].multiply(new MathExp( new MathIm(angle) )); // TODO handle addition
				
				if (b == 1) // if negative branch
					sm.coeff[i].multiply(MINUS_ONE.clone());
				
				sm.coeff[i].multiply(ONE_OVER_SQRT_2);
			} else
				sm.coeff[i].multiply(ONE_OVER_SQRT_2);
			
			sm.coeff[i].simplify();
		}
	}
	
	@Override
	public String toString() {
		return "M(id="+qubitId+", s="+s+", t="+t+", alpha="+alpha+")";
	}

}
