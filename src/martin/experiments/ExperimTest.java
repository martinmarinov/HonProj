package martin.experiments;

import java.util.HashMap;

import martin.math.Complex;
import martin.math.MathsItem;
import martin.quantum.tools.Tools;

public class ExperimTest {

	private final static String[] variables = new String[] { "Pi" };
	private final static Complex[] values = { new Complex(Math.PI, 0) };
	private final static HashMap<String, Complex> rules = Tools.generatePairs(
			variables, values);

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		Tools.SILENT = true;
		doN(4);
	}

	private static void doN(int n) throws Exception {
		final Worksheet ws = new Worksheet("C:\\Users\\Martin\\Desktop\\data\\"
				+ n + "_all.csv");

		final int rows = ws.storage.getRowCount();
		for (int r = 0; r < rows; r++) {

			final int columns = ws.storage.getColCount();
			for (int c = 0; c < columns; c++) {

				final LabCluster lc = new LabCluster(n);
				try {
					lc.perform(ws.storage.getRowHeader(r),
							ws.storage.getColumnHeader(c));
					final MathsItem prob = lc.getProbability();

					System.out.println("Row "+ws.storage.getRowHeader(r)+"\tCol "+
					intArrToString(ws.storage.getColumnHeader(c))+"\tPredicted: "
							+ prob.getValue(rules)
							+ "\tActual: " + ws.storage.getValueAt(c, r));
					
				} catch (Exception e) {}
			}
		}
	}

	private static String intArrToString(final int[] arr) {
		String ans = "["+arr[0];
		
		for (int i = 1; i < arr.length; i++)
			ans += ", "+arr[i];
		return ans+"]";
	}
}
