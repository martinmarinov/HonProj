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
import martin.math.MathsItem;
import martin.quantum.SystemMatrix;

/**
 * This is the entanglement operator. It entangles two qubits.
 * @author Martin
 *
 */
public class E implements Operator {
	
	private final int bitId1, bitId2;
	private final static MathsItem MINUS_ONE = new MathNumber(-1);
	
	/**
	 * Entangle the two qubits together according to the MCalc definition
	 * @param bitId1
	 * @param bitId2
	 */
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
				s.coeff[i].multiply(MINUS_ONE); // do control Z
			
			s.coeff[i].simplify();
		}
	}
	
	@Override
	public String toString() {
		return "E(q1="+bitId1+", q2="+bitId2+")";
	}
	
	
}
