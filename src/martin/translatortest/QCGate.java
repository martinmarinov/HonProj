package martin.translatortest;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

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
				res[q1-1] = "------T------";
				for (int i = q1+1; i < q2; i++)
					res[i-1]  = "------|------";
				res[q2-1] = "-[H]-(+)-[H]-";
				break;
			case JPI:
				res[q1-1] = "-[Z]-[H]-";
				break;
			case JPI2:
				res[q1-1] = "-[R2]-[H]-";
				break;
			case JPI4:
				res[q1-1] = "-[R4]-[H]-";
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
		
		public static String printWebCicuit(final int qubit_count, final QCGate ... gate) {
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
		 * @throws IOException
		 */
		public static String translateToMBQCRaw (final String translator_exe_path, final QCGate ... gates) throws IOException {
			PrintWriter out = new PrintWriter("temp");
			out.println(QCGate.printEinarCicuit(gates));
			out.close();

			Process p = Runtime.getRuntime().exec(new String[] { translator_exe_path, "-v", "temp" });

			BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));

			final StringBuilder answer = new StringBuilder();
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
					answer.append(line+"\n");
				}
			}
			
			p.destroy();

			new File("temp").delete();
			return answer.toString();
		}
}
