package martin.gui;

import martin.math.MathNumber;
import martin.math.MathSymbol;
import martin.operators.AddCoeffTogether;
import martin.operators.E;
import martin.operators.M;
import martin.operators.N;
import martin.operators.X;
import martin.operators.Z;
import martin.quantum.SystemMatrix;

public class Main {

	public static void main(String[] args) throws Exception {
		
		quantumTeleport();

	}
	
	public static void quantumTeleport() throws Exception {
		
		SystemMatrix m = new SystemMatrix(3);
		
		m.performReverse(
				new X(2), // make Z correction because we took |alpha - > branch in M0
				new Z(2), // make X correction because we took |alpha - > branch in M1
				
				new M(1, 0, 0, new MathNumber(0), 1), // make a measurement of qubit 1 with alpha = 0, take |alpha - > branch
				new X(1), // make X correction because we have taken |alpha - > branch
				new M(0, 0, 0, new MathNumber(0), 1), // make a measurement of qubit 0 with alpha = 0, take |alpha - > branch
				
				new E(1, 2), // entangle 1 and 2
				new E(0, 1), // entangle 0 and 1
				
				new N(2), // qubit 2 in state |+>
				new N(1), // qubit 1 in state |+>
				new N(0, new MathSymbol("a"), new MathSymbol("b")) // my input qubit 0 in state a|0>+b|1>
				);
		
		m.performReverse(new AddCoeffTogether());
		
		System.out.println(m);
		
		System.exit(0);
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
		
		System.out.println(m);
		
		System.exit(0);
	}

}