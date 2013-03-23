package martin.translatortest;

import java.io.File;
import java.util.HashMap;
import java.util.Scanner;

import martin.math.Complex;
import martin.quantum.McalcDescription;
import martin.quantum.SimulationRunner;
import martin.quantum.SystemMatrix;
import martin.quantum.tools.Tools;

public class TranslatorRunner {

	private final static String translator_path = "C:\\Users\\Martin\\Desktop\\data\\einar\\ParallelQC.exe";
	
	public static void main(String[] args) throws Exception {
		final String[] res = runTestFromFile(translator_path, "C:\\Users\\Martin\\Desktop\\data\\einar\\circ1.txt");
		System.out.println(res[0]);
		System.out.println(res[1]);
	}
	
	/**
	 * Runs a quantum circuit through Einar's circuit to MBQC translator.
	 * It then runs the MBQC and outputs the result.
	 * It also outputs the initial circuit in web emulator friendly form to be input in http://www.davyw.com/quantum
	 * @param path_to_translator_exe the path to the exe "ParallelQC"
	 * @param fname file containing a description of a quantum circuit in the format accepted by ParallelQC
	 * @return first element is the web friendly view, second element is the result of running the circuit
	 * @throws Exception
	 */
	public static String[] runTestFromFile(final String path_to_translator_exe, final String fname) throws Exception {
		return runTestFromString(path_to_translator_exe, new Scanner(new File(fname)).useDelimiter("\\Z").next());
	}
	
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
		final QCGate[] circuit = QCGate.generateCircuitFromEinar(circ_desc);
		final McalcDescription desc = QCGate.translateToMBQCRaw(path_to_translator_exe, circuit);
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
	


}
