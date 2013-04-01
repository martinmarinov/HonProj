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
package martin.quantum;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

public class McalcDescription {
	
	private final Properties p = new Properties();
	
	public int n;
	
	/** a, b, c,..., z */
	public String inputs = "";
	
	/** (id1, id2); (id3, id4) */
	public String entanglement = "";
	
	/** (qubitid, s, t, alpha); (qubit2, p, q, beta) */
	public String measurements = "";
	
	/** (qubitid, M, s); (qubit, M, s) */
	public String corrections = "";
	
	/** 1, 0, 1 or (2, 0); (4, 1) */
	public String branches = "";
	
	/** a = 2 + Im(3); b = 4 + Im(5) */
	public String variables = "";
	
	public McalcDescription() {};
	
	public McalcDescription(final int n, final String inputs, final String entanglement, final String measurements, final String corrections, final String branches, final String variables) {
		this.n = n;
		this.inputs = inputs;
		this.entanglement = entanglement;
		this.measurements = measurements;
		this.corrections = corrections;
		this.branches = branches;
		this.variables = variables;
	}
	
	public McalcDescription(final int n, final String inputs, final String entanglement, final String measurements, final String corrections, final String branches) {
		this(n, inputs, entanglement, measurements, corrections, branches, "");
	}
	
	public McalcDescription(final File f) throws Exception {
		loadFromFile(f);
	}	
	
	public void loadFromFile(final File f)  throws Exception {
		final FileInputStream in = new FileInputStream(f);
		p.load(in);
		in.close();
		
		n = Integer.valueOf(p.getProperty("prop_no"));
		branches = p.getProperty("prop_branch");
		corrections = p.getProperty("prop_corr");
		entanglement = p.getProperty("prop_ent");
		inputs = p.getProperty("prop_in");
		measurements = p.getProperty("prop_meas");
		variables = p.getProperty("prop_vars");
	}
	
	public void loadFromFile(final String config_filename) throws Exception {
		loadFromFile(new File(config_filename));
	}
	
	public void saveToFile(final File f) throws Exception {

			p.setProperty("prop_no", String.valueOf(n));
			p.setProperty("prop_branch", branches);
			p.setProperty("prop_corr", corrections);
			p.setProperty("prop_ent", entanglement);
			p.setProperty("prop_in", inputs);
			p.setProperty("prop_meas", measurements);
			p.setProperty("prop_vars", variables);
			
			
			final FileOutputStream out = new FileOutputStream(f);
			p.store(out, "---MBQC Pattern description---");
			out.close();
		
	}
	
	public void saveToFile(final String config_filename) throws Exception {
		saveToFile(new File(config_filename));
	}
	
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("prop_no: " + String.valueOf(n) +"\n");
		sb.append("prop_branch: " + branches +"\n");
		sb.append("prop_corr: " + corrections +"\n");
		sb.append("prop_ent: " + entanglement +"\n");
		sb.append("prop_in: " + inputs +"\n");
		sb.append("prop_meas: " + measurements +"\n");
		sb.append("prop_vars: " + variables +"\n");
		return sb.toString();
	}
}
