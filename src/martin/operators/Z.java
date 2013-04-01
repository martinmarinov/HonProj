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

import martin.math.MathNumber;
import martin.quantum.SystemMatrix;

public class Z implements Operator {
	
	private final static MathNumber MINUS_ONE = new MathNumber(-1);
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
		
		for (int i = 0; i < s.size; i++) {
			if ( ( ( i >> ( s.mNumbQubits - qubitId - 1 ) ) & 1) == 1) s.coeff[i].multiply(MINUS_ONE.clone());
			s.coeff[i].simplify();
		}

	}

	@Override
	public String toString() {
		return "Z(q="+qubitId+", s="+(skip ? "0" : "1")+")";
	}
}
