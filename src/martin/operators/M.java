/*******************************************************************************
 * Copyright (c) 2013 Martin Marinov.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Martin - initial API and implementation
 ******************************************************************************/
package martin.operators;

import martin.math.MathExpression;
import martin.math.MathExp;
import martin.math.MathIm;
import martin.math.MathNumber;
import martin.math.MathSqrt;
import martin.math.MathsItem;
import martin.math.MathFract;
import martin.quantum.SystemMatrix;

public class M implements Operator {

	protected final static MathsItem MINUS_ONE = new MathNumber(-1);
	protected final static MathsItem ONE_OVER_SQRT_2 = new MathFract(
			new MathNumber(1), new MathSqrt(new MathNumber(2)));

	protected final int t;
	protected final int s;
	protected final int qubitId;
	protected final int b;
	protected final MathsItem alpha;

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

		for (int i = 0; i < sm.size; i++)
			perform(i, sm);
	}

	protected void perform(int i, final SystemMatrix sm) {
		// basis of the control bit
		final int state1 = (i >> (sm.mNumbQubits - qubitId - 1)) & 1;

		if (state1 == 1) {
			// if the state of the qubit is 1, multiply by the exp coefficient
			
			final MathExpression angle = new MathExpression();
			angle.add(alpha);
			if (s == 1)
				angle.multiply(MINUS_ONE.clone());

			sm.coeff[i].multiply(new MathExp(new MathIm(angle)));

			if (b == 1) // if negative branch
				sm.coeff[i].multiply(MINUS_ONE.clone());

			if (t == 1) // Z correction
				sm.coeff[i].multiply(MINUS_ONE.clone());

			sm.coeff[i].multiply(ONE_OVER_SQRT_2.clone());
			sm.coeff[i].simplify();
		} else
			sm.coeff[i].multiply(ONE_OVER_SQRT_2.clone());
	}

	@Override
	public String toString() {
		return "M(id=" + qubitId + ", t=" + t + ", s=" + s + ", alpha=" + alpha
				+ ", b=" + b + ")";
	}

}
