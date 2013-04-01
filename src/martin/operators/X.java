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

import martin.math.MathsItem;
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
				
				final MathsItem temp = s.coeff[i];
				s.coeff[i] = s.coeff[second_id];
				s.coeff[second_id] = temp;
			}

	}
	
	@Override
	public String toString() {
		return "X(q="+qubitId+", s="+(skip ? "0" : "1")+")";
	}

}
