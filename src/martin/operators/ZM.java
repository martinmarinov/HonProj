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

public class ZM extends M {
	
	protected final static MathsItem ZERO = new MathNumber(0);
	
	public ZM(int qubitId, int t, int s, int b) {
		super(qubitId, t, s, null, b);
	}
	
	protected void perform(int i, final SystemMatrix sm) {
		final int state1 = (i >> (sm.mNumbQubits - qubitId - 1)) & 1;
		
		if (state1 != b)
				sm.coeff[i].multiply(ZERO);
	}
	
	@Override
	public String toString() {
		return "ZM(id=" + qubitId + ", t=" + t + ", s=" + s
				+ ", b=" + b + ")";
	}

}
