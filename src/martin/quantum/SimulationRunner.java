package martin.quantum;

import java.util.ArrayList;
import java.util.Arrays;

import martin.math.MathsItem;
import martin.math.MathsParser;
import martin.operators.AddCoeffTogether;
import martin.operators.E;
import martin.operators.I;
import martin.operators.Operator;
import martin.quantum.tools.Tools;

public final class SimulationRunner {

	public static SystemMatrix run(int n, final String inputs, final String entanglement, final String measurements, final String corrections, final String branches) throws Exception {
		
		final SystemMatrix sm = new SystemMatrix(n);
		final ArrayList<Operator> al = new ArrayList<Operator>();
		
		final int[] b = parseBranches(branches);
		
		parseInputs(al, inputs);
		parseEntanglement(al, entanglement);
		parseMeasurements(al, measurements);
		parseCorrections(al, corrections);
		
		System.out.println("branches = "+Arrays.toString(b));
		for (Operator o : al)
			System.out.println("Will perform: "+o);
		
		sm.perform(al.toArray(new Operator[0]));	
		
		sm.perform(new AddCoeffTogether());
		return sm;
	}
	
	/**
	 * @param al
	 * @param inputs format "(a,b,c...)" for qubit 0...000, 0...001, 0...010, etc
	 */
	private static void parseInputs(final ArrayList<Operator> al, String inputs) throws Exception {
		
		if (inputs.trim().isEmpty())
			return;
		
		final String[] csv = inputs.split(",");
		final MathsItem[] items = new MathsItem[csv.length];
		
		for (int i = 0; i < csv.length; i++) {
			items[i] = MathsParser.parse(csv[i]);
			items[i].simplify();
		}
			
		al.add(new I(items));
	}
	
	/**
	 * @param al
	 * @param enganglement format "(id1, id2); (id3, id4)" for E(id1, id2), E(id3, id4)
	 */
	private static void parseEntanglement(final ArrayList<Operator> al, String enganglement) throws Exception {
		
		if (enganglement.trim().isEmpty())
			return;
		
		final String[] scsv = enganglement.split(";");
		
		for (String m : scsv) {
			m = Tools.trimAndCheckBrackets(m);
			
			final String[] csv = m.split(",");
			
			al.add(new E( Integer.parseInt(csv[0].trim()), Integer.parseInt(csv[1].trim()) ));
		}
	}
	
	private static void parseMeasurements(final ArrayList<Operator> al, final String measurements) {
		if (measurements.trim().isEmpty())
			return;
	}
	
	private static void parseCorrections(final ArrayList<Operator> al, final String corrections) {
		if (corrections.trim().isEmpty())
			return;
	}
	
	private static int[] parseBranches(final String branches) {
		
		if (branches.trim().isEmpty())
			return new int[0];
		
		final String[] csv = branches.split(",");
		final int[] b = new int[csv.length];
		
		for (int i = 0; i < csv.length; i++)
			b[i] = Integer.parseInt(csv[i].trim());
		
		return b;
	}
	

	
}
