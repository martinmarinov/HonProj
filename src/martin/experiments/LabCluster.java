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
package martin.experiments;

import martin.math.MathsItem;
import martin.math.MathsParser;
import martin.operators.M;
import martin.operators.Operator;
import martin.operators.ZM;
import martin.quantum.SystemMatrix;

public class LabCluster extends SystemMatrix {
	
	public LabCluster(final MathsItem[] items) throws Exception {
		super(items);
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
		try {
			switch (meas) {
			case 'X':
				return new M(i, 0, 0, MathsParser.parse("0"), branches[i]);
				//return new XM(i, 0, 0, branches[i]);
			case 'Y':
				//return new YM(i, 0, 0, branches[i]);
				return new M(i, 0, 0, MathsParser.parse("Pi/2"), branches[i]);
			case 'M':
				return new M(i, 0, 0, MathsParser.parse("-Pi/4"), branches[i]);
			case 'P':
				return new M(i, 0, 0, MathsParser.parse("Pi/4"), branches[i]);
			case 'Z':
				return new ZM(i, 0, 0, branches[i]);

			default:
				throw new MeasurementCycleNotSupported(base, i);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
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
