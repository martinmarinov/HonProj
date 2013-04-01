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
package martin.translatortest;

import java.util.HashMap;
import java.util.Random;

import martin.math.Complex;
import martin.quantum.McalcDescription;
import martin.quantum.SimulationRunner;
import martin.quantum.SystemMatrix;
import martin.quantum.tools.Tools;
import martin.translatortest.QCGate.type;

public class TranslatorRunner {
	
	private final static Random r = new Random();

	/**
	 * Runs a quantum circuit through Einar's circuit to MBQC translator.
	 * It then runs the MBQC and outputs the result.
	 * It also outputs the initial circuit in web emulator friendly form to be input in http://www.davyw.com/quantum
	 * @param path_to_translator_exe the path to the exe "ParallelQC"
	 * @param circ_desc a description of the circuit that is accepted by the translator
	 * @return first element is the web friendly view, second element is the result of running the circuit
	 * @throws Exception
	 */
	public static String[] runTestFromString(final String path_to_translator_exe, final String circ_desc) throws Exception {
		final QCGate[] circuit = QCGate.generateCircuitFromEinar(circ_desc.trim().replace("\r", "").replaceAll("\n+", "\n"));
		final McalcDescription desc = QCGate.translateToMBQCRaw(path_to_translator_exe, circuit);
		//System.out.println(desc);
		if (desc.n > 12) throw new Exception(desc.n+" are too many qubits to run!");
		final SystemMatrix system = SimulationRunner.run(desc);
		final int numb_inputs = Tools.powerOfTwo(desc.inputs.split(", ").length);
		final int coeff_to_gen = 1 << numb_inputs;
		
		final String[] result = new String[2];
		result[0] = QCGate.printWebCicuit(circuit);
		
		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < coeff_to_gen; i++) {
			sb.append("Input: "+SystemMatrix.getBraKet(i, numb_inputs)+" -> \n");
			sb.append(system.printValues(genRules(numb_inputs, i), true));
			sb.append("\n\n");
		}
		result[1] = sb.toString();
		return result;
	}
	
	private static HashMap<String, Complex> genRules(final int input_qubits, int id) throws Exception {
		final StringBuilder sb = new StringBuilder();
		final int coeff_to_gen = 1 << input_qubits;
		
		char first = 'a'-1;
		for (int i = 0; i < coeff_to_gen; i++) {
			first++;
			sb.append(first);
			sb.append(" = " + (id == i ? "1" : "0")+"; ");
		}
		sb.append("pi = "+Math.PI);
		
		return SimulationRunner.parseVariablesAndValues(sb.toString());
	}
	
	/**
	 * Generates random circuit description
	 * @return
	 */
	public static String generateRandomCircuitDesc() {
		final int qubits = 2 + r.nextInt(2);
		final int gates = 1 + r.nextInt(5);
		
		final QCGate[] circuit = new QCGate[gates];
		int q1 = 1;
		for (int i = 0; i < circuit.length; i++) {
			circuit[i] = getRandomGate(qubits, q1);
			q1 += r.nextInt(2);
			if (q1 > qubits) q1 = qubits;
		}
		
		return QCGate.printEinarCicuit(circuit);
	}
	
	private static QCGate getRandomGate(final int qubits, final int q1) {
		final type t = getRandomType();
		
		switch (t) {
		case ZZ:
			int q2 = 1 + r.nextInt(qubits);
			while (q2 == q1) q2 = 1 + r.nextInt(qubits);
			return new QCGate(t, q1, q2);
		case H:
		case JPI:
		case JPI2:
		case JPI4:
		case JPI8:
			return new QCGate(t, q1);
		default:
			return null;
		}
	}
	
	private static type getRandomType() {
		final type[] values = type.values();
		return values[r.nextInt(values.length)];
	}

}
