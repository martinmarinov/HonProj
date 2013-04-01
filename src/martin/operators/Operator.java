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

import martin.quantum.SystemMatrix;

/**
 * The base interface of a quantum operator that could act on a {@link SystemMatrix}
 * @author Martin
 *
 */
public interface Operator {
	
	/**
	 * Apply the quantum operator to the system matrix s.
	 * The quantum operator has full access to s and must ensure that
	 * it is not trying to measure qubits that have already been measured.
	 * Also the vectors representing the SystemMatrix may not be in the same
	 * coefficient (if the current operator does only multiplication, you should not worry)
	 * @param s
	 * @throws Exception
	 */
	void operate(SystemMatrix s) throws Exception;

}
