package martin.experiments;

import martin.math.MathsItem;
import martin.math.MathsParser;
import martin.operators.Operator;
import martin.operators.XM;
import martin.operators.YM;
import martin.quantum.SystemMatrix;

public class LabCluster extends SystemMatrix {

	private final MathsItem zero;
	
	public LabCluster(int n) {
		super(4);

		try {
			zero = MathsParser.parse("0");
			
			final MathsItem firstcoeff = MathsParser.parse("exp(Im((" + n + "*Pi)/4))");
			final MathsItem coeffi = MathsParser.parse("Im(1)");
			final MathsItem secondcoeff = MathsParser.parse("-Im(exp(Im((" + n + "*Pi)/4)))");

			coeff[1].multiply(zero);
			coeff[2].multiply(zero);

			coeff[3].multiply(firstcoeff);

			coeff[4].multiply(zero);
			coeff[5].multiply(zero);
			coeff[6].multiply(zero);
			coeff[7].multiply(zero);
			coeff[8].multiply(zero);
			coeff[9].multiply(zero);
			coeff[10].multiply(zero);
			coeff[11].multiply(zero);

			coeff[12].multiply(coeffi);

			coeff[13].multiply(zero);
			coeff[14].multiply(zero);

			coeff[15].multiply(secondcoeff);

			final MathsItem oneoversqrt2 = MathsParser.parse("1/2");

			for (int i = 0; i < size; i++)
				coeff[i].multiply(oneoversqrt2);

			simplify();

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Perform a measurement cycle.
	 * 
	 * @param base
	 *            in the form "ZZYZ", etc
	 * @throws MeasurementCycleNotSupported when the measurement cannot be done because it is not supported
	 */
	public void perform(String base, final int[] branches) throws Exception {
		base = base.trim().toUpperCase();
		final int measurements = base.length();
		
		if (branches.length != measurements)
			throw new MeasurementCycleNotSupported("More branches specified than measurements to be made!");
		
		if (measurements > mNumbQubits)
			throw new MeasurementCycleNotSupported("There are "+mNumbQubits+" qubits in the system but measurements of "+measurements+" qubits are requested.");
		
		final Operator[] ops = new Operator[measurements];
		for (int i = 0; i < measurements; i++)
			ops[i] = getOp(base.charAt(i), base, i, branches);
		
		perform(ops);
	}
	
	private Operator getOp(final char meas, final String base, int i, final int[] branches) throws MeasurementCycleNotSupported {
		switch (meas) {
		case 'X':
			return new XM(i, 0, 0, branches[i]);
		case 'Y':
			return new YM(i, 0, 0, branches[i]);

		default:
			throw new MeasurementCycleNotSupported(base, i);
		}
	}
	
	/**
	 * Parse CCCC_1H2V3H4H to {0,1,0,0}
	 * @param thing
	 * @return
	 */
	public static int[] parseBranches(final String thing) throws Exception {
		if (!thing.startsWith("CCCC_"))
			throw new Exception("The string '"+thing+"' is not 4 coincidence!");
		
		final String remainder = thing.substring(5, thing.length());
		int branches = remainder.length()/2;
		final int[] res = new int[branches];
		
		for (int i = 0; i < branches; i++) {
			int id = Integer.parseInt(remainder.charAt(2*i)+"");
			if (id != i + 1)
				throw new Exception("The number "+id+" in "+remainder+" was not expected! Rather "+i+" was expected at char "+2*i+" in '"+remainder+"'!");
			
			char meas = remainder.charAt(2*i+1);
			
			res[i] = meas == 'H' ? 0 : 1;
		}
		
		return res;
	}
	
}
