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

import martin.math.MathFract;
import martin.math.MathNumber;
import martin.math.MathSqrt;
import martin.math.MathsItem;
import martin.quantum.SystemMatrix;

public class N implements Operator {

	private final int qubitId;
	private MathsItem st1 = new MathFract(new MathNumber(1), new MathSqrt(new MathNumber(2)));
	private MathsItem st2 = new MathFract(new MathNumber(1), new MathSqrt(new MathNumber(2)));

	public N(int qubitId) {
		this.qubitId = qubitId;
	}
	
	public N(int qubitId, MathsItem st1, MathsItem st2) {
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

		for (int i = 0; i < s.size; i++) {
			s.coeff[i].multiply( ((i >> (s.mNumbQubits - qubitId - 1)) & 1) == 0 ? st1 : st2);
			s.coeff[i].simplify();
		}

	}
	
	@Override
	public String toString() {
		return "N(q="+qubitId+", a="+st1+", b="+st2+")";
	}

}
