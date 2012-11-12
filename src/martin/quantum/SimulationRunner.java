package martin.quantum;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import martin.math.Complex;
import martin.math.MathsItem;
import martin.math.MathsParser;
import martin.operators.AddCoeffTogether;
import martin.operators.E;
import martin.operators.I;
import martin.operators.M;
import martin.operators.Operator;
import martin.operators.X;
import martin.operators.Z;
import martin.quantum.tools.Tools;

public final class SimulationRunner {

	public static SystemMatrix run(int n, final String inputs, final String entanglement, final String measurements, final String corrections, final String branches) throws Exception {
		
		final SystemMatrix sm = new SystemMatrix(n);
		final ArrayList<Operator> al = new ArrayList<Operator>();
		
		final int[] b = parseBranches(branches);
		
		parseInputs(al, inputs);
		parseEntanglement(al, entanglement);
		parseMeasurements(al, measurements, b);
		parseCorrections(al, corrections, b);
		
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
	
	/**
	 * @param al
	 * @param measurements format (id, s, t, alpha);
	 * @throws Exception 
	 */
	private static void parseMeasurements(final ArrayList<Operator> al, final String measurements, final int[] b) throws Exception {
		if (measurements.trim().isEmpty())
			return;
		
		final String[] scsv = measurements.split(";");
		
		for (String m : scsv) {
			m = Tools.trimAndCheckBrackets(m);
			
			final String[] csv = m.split(",");
			
			int qubitId = Integer.parseInt(csv[0].trim());
			final MathsItem sitem = MathsParser.parse(csv[1]);
			final MathsItem titem = MathsParser.parse(csv[2]);
			final MathsItem alpha = MathsParser.parse(csv[3]);

			final Complex sval = sitem.getValue(genPairs(b, 's'));
			final Complex tval = titem.getValue(genPairs(b, 't'));
			
			final int s = ((int) sval.R) % 2;
			final int t = ((int) tval.R) % 2;
			
			al.add(new M(qubitId, t, s, alpha, b[qubitId]));
		}
		
	}
	
	private static void parseCorrections(final ArrayList<Operator> al, final String corrections, final int[] b) throws Exception {
		if (corrections.trim().isEmpty())
			return;

		final String[] scsv = corrections.split(";");

		for (String m : scsv) {
			m = Tools.trimAndCheckBrackets(m);

			final String[] csv = m.split(",");

			int qubitId = Integer.parseInt(csv[0].trim());
			final char meas = csv[1].trim().toLowerCase().charAt(0);
			final MathsItem sitem = MathsParser.parse(csv[2]);

			final Complex sval = sitem.getValue(genPairs(b, 's'));

			final int s = ((int) sval.R) % 2;


			switch (meas) {
			case 'x':
				al.add(new X(qubitId, s));
				break;
			case 'z':
				al.add(new Z(qubitId, s));
				break;
			default:
				throw new Exception("Unsupported correction specified!");
			}
		}
	}
	
	private static HashMap<String, Complex> genPairs(final int[] b, final char index) {
		final String[] ses = new String[b.length];
		final Complex[] bes = new Complex[b.length];
		for (int i = 0; i < b.length; i++) {
			ses[i] = Character.toString(index)+i;
			bes[i] = new Complex(b[i], 0);
		}
		return Tools.generatePairs(ses, bes);
	}
	
	private static int[] parseBranches(final String branches) throws Exception {
		
		if (branches.trim().isEmpty())
			return new int[0];
		
		final String[] csv = branches.split(",");
		final int[] b = new int[csv.length];
		
		for (int i = 0; i < csv.length; i++) {
			b[i] = Integer.parseInt(csv[i].trim());
			if (!(b[i] == 0 || b[i] == 1))
				throw new Exception("Branch values must be either 0 or 1 for each measurement!");
		}
		
		return b;
	}
	
	/**
	 * Parses values for symbolic variables to be used in evaluation
	 * @param input "a = 2 + Im(3); b = 4 + Im(5)"
	 * @return
	 * @throws Exception
	 */
	public static HashMap<String, Complex> parseVariablesAndValues(final String input) throws Exception {
		if (input.trim().isEmpty())
			return null;
		
		final String[] expressions = input.split(";");
		final String[] variables = new String[expressions.length];
		final Complex[] values = new Complex[expressions.length];
		
		for (int i = 0; i < expressions.length; i++) {
			final String exp = expressions[i];
			
			final String[] parts = exp.split("=");
			variables[i] = parts[0].trim();
			values[i] = MathsParser.parse(parts[1]).getValue(null);
		}
		
		return Tools.generatePairs(variables, values);
	}
	

	
}
