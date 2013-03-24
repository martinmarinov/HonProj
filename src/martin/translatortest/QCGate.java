package martin.translatortest;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;

import martin.quantum.McalcDescription;

public class QCGate {
		public enum type {H, ZZ, JPI, JPI2, JPI4, JPI8};
		public int q1 = -1, q2 = -1;
		public type t;
		
		public QCGate(type t, int q1, int q2) {
			this.t = t;
			this.q1 = q1;
			this.q2 = q2;
		}
		
		public QCGate (type t, int q1) {
			this.t = t;
			this.q1 = q1;
		}
		
		public String toString() {
			switch (t) {
			case H:
				return "H q"+q1;
			case ZZ:
				return "ZZ q"+q1+" q"+q2;
			case JPI:
				return "J(pi) q"+q1;
			case JPI2:
				return "J(pi/2) q"+q1;
			case JPI4:
				return "J(pi/4) q"+q1;
			case JPI8:
				return "J(pi/8) q"+q1;
			}
			return null;
		}
		
		private String[] toWebFormat(final int qubits) {
			final String[] res = new String[qubits];
			int maxsize = 0;
			
			switch (t) {
			case H:
				res[q1-1] = "-[H]-";
				break;
			case ZZ:
				res[q1-1] = 	"------†------";
				for (int i = q1+1; i < q2; i++)
					res[i-1]  = "------|------";
				res[q2-1] = 	"-[H]-(+)-[H]-";
				break;
			case JPI:
				res[q1-1] = "-[Z]-[H]-";
				break;
			case JPI2:
				res[q1-1] = "-[R2]-[H]-";
				break;
			case JPI4:
				res[q1-1] = "-[R4]-[H]-";
				break;
			case JPI8:
				res[q1-1] = "-[R8]-[H]-";
				break;
			}
			
			for (int i = 0; i < res.length; i++)
				if (res[i] != null && res[i].length() > maxsize) maxsize = res[i].length();
			
			for (int i = 0; i < res.length; i++) {
				if (res[i] == null)
					res[i] = repeat('-', maxsize);
				else if (res[i].length() < maxsize)
					res[i] += repeat('-', res[i].length() - maxsize);
			}
			
			return res;
		}
		
		private final static String repeat(final char c, final int count) {
			final StringBuilder sb = new StringBuilder();
			for (int i = 0; i < count; i++)
				sb.append(c);
			return sb.toString();
		}
		
		private static int getQubitCount(final QCGate ... gate) {
			int max = 0;
			for (final QCGate g : gate) {
				if (g.q1 > max) max = g.q1;
				if (g.q2 > max) max = g.q2;
			}
			return max;
		}
		
		public static String printWebCicuit(final QCGate ... gate) {
			final int qubit_count = getQubitCount(gate);
			final String[][] gs = new String[gate.length][];
			for (int i = 0; i < gs.length; i++) gs[i] = gate[i].toWebFormat(qubit_count);
			
			final String[] g = new String[qubit_count];
			for (int i = 0; i < qubit_count; i++) g[i] = "";
			
			for (int gid = 0; gid < gate.length; gid++)
				for (int i = 0; i < qubit_count; i++)
					g[i] += gs[gid][i];
			
			final StringBuilder sb = new StringBuilder();
			for (final String s : g) sb.append(s+"\n");
			return sb.toString();
		}
		
		public static String printEinarCicuit(final QCGate ... gate) {
			final StringBuilder sb = new StringBuilder();
			for (final QCGate g : gate) sb.append(g+"\n");
			return sb.toString();
		}
		
		/**
		 * Uses Flaviu's translator to translate a circuit to a raw MBQC cicuit
		 * @param translator_exe_path
		 * @param gates
		 * @return
		 * @throws Exception 
		 */
		public static McalcDescription translateToMBQCRaw (final String translator_exe_path, final QCGate ... gates) throws Exception {
			PrintWriter out = new PrintWriter("temp");
			out.println(QCGate.printEinarCicuit(gates));
			out.close();

			Process p = Runtime.getRuntime().exec(new String[] { translator_exe_path, "-v", "temp" });

			BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));

			final ArrayList<String> lines = new ArrayList<String>();
			String line;
			boolean lookfor = false;
			while ((line = input.readLine()) != null) {
				if (line.trim().equals("Input circuit in MBQC model:")) {
					lookfor = true;
					continue;
				}
				
				if (lookfor) {
					if (line.trim().isEmpty())
						break;
					lines.add(line);
				}
			}
			
			p.destroy();

			new File("temp").delete();
			return parseMcalFromRaw(lines);
		}
		
		private static McalcDescription parseMcalFromRaw(final ArrayList<String> lines) throws Exception {
			final McalcDescription desc = new McalcDescription();
			int last_qubit = 0;
		
			for (String line : lines) {
				
				final boolean input = line.startsWith("Input qubits:");
				
				int idx = line.indexOf(':');
				if (idx >= 0) line = line.substring(idx+2);
				
				// parse inputs
				if (input) {
					final String[] ns = line.split(" ");
					for (int i = 0; i < ns.length; i++)
						if (!ns[i].equals(Integer.toString(i+1)))
							throw new Exception("The input qubits must be in the beginning! Error on "+i);
					last_qubit = ns.length;
					
					final int coeff_to_gen = 1 << ns.length;
					
					final StringBuilder sb = new StringBuilder();
					char first = 'a';
					sb.append(first);
					
					for (int i = 1; i < coeff_to_gen; i++) {
						first++;
						sb.append(", "+first);
					}
					
					desc.inputs = sb.toString();
					continue;
				}
				
				// parse entanglement
				if (line.startsWith("E")) {
					line = line.substring(2);
					final String[] ns = line.split(" ");
					
					final int q1 = Integer.parseInt(ns[0]);
					if (q1 > last_qubit) last_qubit = q1;
					final int q2 = Integer.parseInt(ns[1]);
					if (q2 > last_qubit) last_qubit = q2;
					
					if (desc.entanglement == null || desc.entanglement.isEmpty())
						desc.entanglement = "("+ns[0]+", "+ns[1]+")";
					else
						desc.entanglement += "; ("+ns[0]+", "+ns[1]+")";
					continue;
				}
				
				// parse measurement
				if (line.startsWith("M(")) {
					final int pos = line.indexOf(")");
					final String ang = line.substring(2, pos);
					
					line = line.substring(pos+1);
					line = line.replace(";", "");
					line = line.trim();
					
					final String[] things = line.split(" ");
					if (!(things.length == 1 || things.length == 4)) throw new Exception("Cannot parse M "+line);
					
					String s = "0";
					String t = "0";
					
					if (things.length == 4) {
						if (things[1].equals("s"))
							s = "s"+things[3];
						else if (things[1].equals("t"))
							t = "t"+things[3];
					}
					
					final int q = Integer.parseInt(things[0]); // if error happens here, probably there is a correction defined as well
					if (q > last_qubit) last_qubit = q;
					
					if (desc.measurements == null || desc.measurements.isEmpty())
						desc.measurements = "("+q+", "+s+", "+t+", "+ang+")"; // (2, s1, t1, 0)
					else
						desc.measurements += "; ("+q+", "+s+", "+t+", "+ang+")";
					continue;
				}
				
				if (line.startsWith("X") || line.startsWith("Z")) {
					char meas = line.toLowerCase().charAt(0);
					line = line.substring(2);
					line = line.replace(";", "");
					line = line.trim();
					final String[] ns = line.split(" ");
					
					final int q = Integer.parseInt(ns[0]);
					if (q > last_qubit) last_qubit = q;
					
					int corr = Integer.parseInt(ns[3]); 
					if (corr > last_qubit) last_qubit = corr;
					String s = "s"+corr;
					for (int i = 5; i < ns.length; i+=2) {
						corr = Integer.parseInt(ns[i]); 
						if (corr > last_qubit) last_qubit = corr;
						s+=" + s"+corr;
					}
					
					if (desc.corrections == null || desc.corrections.isEmpty())
						desc.corrections = "("+ns[0]+", "+meas+", "+s+")";
					else
						desc.corrections += "; ("+ns[0]+", "+meas+", "+s+")";
					continue;
				}
				
				throw new Exception("Unknown line: "+line);
				
			}
			
			desc.branches = "0";
			for (int i = 1; i < last_qubit; i++)
				desc.branches += ",0";
			
			desc.n = last_qubit;
			return desc;
		}
		
		public static QCGate[] generateCircuitFromEinar(final String desc) throws Exception {
			final String[] lines = desc.split("\n");
			final QCGate[] ans = new QCGate[lines.length];
			
			for (int l = 0; l < lines.length; l++) {
				String line = lines[l];
				
				if (line.startsWith("ZZ")) {
					line = line.substring(3).trim();
					final String[] vals = line.split(" ");
					ans[l] =  new QCGate(type.ZZ, Integer.parseInt(vals[0].substring(1)), Integer.parseInt(vals[1].substring(1)));
					continue;
				}
				
				if (line.startsWith("H")) {
					line = line.substring(2).trim();
					ans[l] =  new QCGate(type.H, Integer.parseInt(line.substring(1)));
					continue;
				}
				
				if (line.startsWith("J(pi)")) {
					line = line.substring(6).trim();
					ans[l] =  new QCGate(type.JPI, Integer.parseInt(line.substring(1)));
					continue;
				}
				
				if (line.startsWith("J(pi/2)")) {
					line = line.substring(8).trim();
					ans[l] =  new QCGate(type.JPI2, Integer.parseInt(line.substring(1)));
					continue;
				}
				
				if (line.startsWith("J(pi/4)")) {
					line = line.substring(8).trim();
					ans[l] =  new QCGate(type.JPI4, Integer.parseInt(line.substring(1)));
					continue;
				}
				
				if (line.startsWith("J(pi/8)")) {
					line = line.substring(8).trim();
					ans[l] =  new QCGate(type.JPI8, Integer.parseInt(line.substring(1)));
					continue;
				}
				
				if (line.trim().isEmpty())
					continue;
				
				throw new Exception(line+" is not supported!");
			}
			
			return ans;
		}
}
