package martin.quantum;

import java.util.ArrayList;

import martin.coefficients.Coefficient;
import martin.operators.E;
import martin.operators.N;
import martin.operators.Operator;
import martin.quantum.tools.Tools;

public class SimulationRunner {

	public static SystemMatrix run(int n, final String preparation, final String inputs, final String enganglement, final String measurements, final String corrections, final String branches) throws Exception {
		
		final SystemMatrix sm = new SystemMatrix(n);
		final ArrayList<Operator> al = new ArrayList<Operator>();
		
		parsePreparation(al, preparation);
		parseInputs(al, inputs);
		parseEntanglement(al, enganglement);
		parseMeasurements(al, measurements);
		parseCorrections(al, corrections);
		parseBranches(al, branches);
		
		sm.perform(al.toArray(new Operator[0]));	
		return sm;
	}
	
	/**
	 * @param al
	 * @param preparation format "id1, id2, id3" for N(id1), N(id2), N(id3)
	 */
	private static void parsePreparation(final ArrayList<Operator> al, final String preparation) {
		final String[] csv = preparation.split(",");
		
		for (String s : csv)
			al.add(new N(Integer.parseInt(s.trim())));
	}
	
	/**
	 * @param al
	 * @param inputs format "(id1, c1, c2); (id2, c3, c4)" for N(id1, c1, c2), N(id2, c3, c4)
	 */
	private static void parseInputs(final ArrayList<Operator> al, String inputs) throws Exception {		
		final String[] scsv = inputs.split(";");
		
		for (String m : scsv) {
			m = Tools.trimAndCheckBrackets(m);
			
			final String[] csv = m.split(",");
			
			al.add(new N( Integer.parseInt(csv[0].trim()), Coefficient.fromString(csv[1]), Coefficient.fromString(csv[2]) ));
		}
	}
	
	/**
	 * @param al
	 * @param enganglement format "(id1, id2); (id3, id4)" for E(id1, id2), E(id3, id4)
	 */
	private static void parseEntanglement(final ArrayList<Operator> al, String enganglement) throws Exception {

		final String[] scsv = enganglement.split(";");
		
		for (String m : scsv) {
			m = Tools.trimAndCheckBrackets(m);
			
			final String[] csv = m.split(",");
			
			al.add(new E( Integer.parseInt(csv[0].trim()), Integer.parseInt(csv[0].trim()) ));
		}
	}
	
	private static void parseMeasurements(final ArrayList<Operator> al, final String measurements) {
		
	}
	
	private static void parseCorrections(final ArrayList<Operator> al, final String corrections) {
		
	}
	
	private static void parseBranches(final ArrayList<Operator> al, final String branches) {
		
	}
	

	
}
