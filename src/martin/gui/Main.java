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
package martin.gui;

import martin.math.MathNumber;
import martin.math.MathSymbol;
import martin.operators.AddCoeffTogether;
import martin.operators.E;
import martin.operators.I;
import martin.operators.M;
import martin.operators.N;
import martin.operators.X;
import martin.operators.Z;
import martin.quantum.SystemMatrix;
import martin.quantum.tools.Tools;

public class Main {

	public static void main(String[] args) throws Exception {
		
		quantumTeleport();
		//AltQunatumTeleport();

	}
	
	public static void quantumTeleport() throws Exception {
		
		int b[] = new int[]{1, 1}; // which branches to take
		SystemMatrix m = new SystemMatrix(3);
		m.performReverse(
				new X(2, b[1]), // make X correction
				new Z(2, b[0]), // make Z correction

				// measure qubit 1 with alpha = 0, and t dependency b[0], take branch b[1]
		new M(1, b[0], 0, new MathNumber(0), b[1]),
				// measure qubit 0 with alpha = 0, take branch b[0]
				new M(0, 0, 0, new MathNumber(0), b[0]), 

				new E(1, 2), // entangle 1 and 2
				new E(0, 1), // entangle 0 and 1

				new N(2), // qubit 2 in state |+>
				new N(1), // qubit 1 in state |+>

		// initialize input qubit in state a|0>+ b|0>
				new N(0, new MathSymbol("a"), new MathSymbol("b")));

		m.performReverse(new AddCoeffTogether());
		
		System.out.println(m);

	}
	
	public static void AltQunatumTeleport() throws Exception {
		
		Tools.logger.println();
		Tools.logger.println("Quantum teleportation 2:");
		
		SystemMatrix m = new SystemMatrix(3);
		
		int b[] = new int[]{1, 1};
		
		m.performReverse(
				new X(2, b[1]), // make X correction
				new Z(2, b[0]), // make Z correction
				
				new M(1, b[0], 0, new MathNumber(0), b[1]), // make a measurement of qubit 1 with alpha = 0, take |alpha - > branch
				new M(0, 0, 0, new MathNumber(0), b[0]), // make a measurement of qubit 0 with alpha = 0, take |alpha - > branch
				
				new E(1, 2), // entangle 1 and 2
				new E(0, 1), // entangle 0 and 1
				
				new I(new MathSymbol("a"), new MathSymbol("b"))
				);
		
		m.performReverse(new AddCoeffTogether());
		
		Tools.logger.println(m);
	}
	
	
	public static void moreComplicatedTest() throws Exception {
		SystemMatrix m = new SystemMatrix(4);
		
		int[] b = {1, 1, 1};
		
		m.performReverse(
				new Z(3, b[1]),
				new X(3, b[2]),
				
				new M(2, b[1], b[1], new MathSymbol("gama"), b[2]),
				new M(1, 0, b[0], new MathSymbol("beta"), b[1]),
				new M(0, 0, 0, new MathSymbol("alpha"), b[0]),
				
				new E(2, 3), // entangle 2 and 3
				new E(1, 2), // entangle 1 and 2
				new E(0, 1), // entangle 0 and 1
				
				new N(3), // qubit 3 in state |+>
				new N(2), // qubit 2 in state |+>
				new N(1), // qubit 1 in state |+>
				new N(0, new MathSymbol("a"), new MathSymbol("b")) // my input qubit 0 in state a|0>+b|1>
				);
		
		m.performReverse(new AddCoeffTogether());
		
		Tools.logger.println(m);
	}

}
