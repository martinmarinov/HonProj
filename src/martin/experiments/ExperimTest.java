package martin.experiments;

import java.util.ArrayList;

import martin.quantum.tools.Tools;

public class ExperimTest {

	private final static String input_dir = "C:\\Users\\Martin\\Desktop\\data\\";
	private final static String output_dir = "C:\\Users\\Martin\\Desktop\\data\\martoout\\";
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		Tools.SILENT = true;
		Tools.MATHEMATICA_FRIENDLY_OUTPUT = true;
		doN(0);
		
	}

	private static void doN(int n) throws Exception {
		final CSVExporter csv = new CSVExporter(output_dir+n+"_report.csv", ",");
		final Worksheet ws = new Worksheet(input_dir + n + "_all.csv");
		
		//csv.putRow("", ws.storage.columns.toArray(new int[0])); // put header

		final int rows = ws.storage.getRowCount();
		for (int r = 0; r < rows; r++) {

			final ArrayList<Double> originals = new ArrayList<Double>();
			final ArrayList<Double> simulated = new ArrayList<Double>();

			final int columns = ws.storage.getColCount();
			for (int c = 0; c < columns; c++) {

				final LabCluster lc = new LabCluster(n);
				try {
					lc.perform(ws.storage.getRowHeader(r),
							ws.storage.getColumnHeader(c));

					originals.add((double) ws.storage.getValueAt(c, r));
					final double probdv = lc.getQuickProbability(Tools.PI_rule);
					simulated.add(probdv);

				} catch (Exception e) {}
			}

			if (!originals.isEmpty()) {
				final String rowhead = ws.storage.getRowHeader(r);
				final Double[] origs = originals.toArray(new Double[0]);
				final Double[] sims = simulated.toArray(new Double[0]);
				
				ExperimentVisualizer.add(rowhead+" originals", origs, rowhead+" simulated", sims);
				
				csv.putRow(rowhead, origs);
				csv.putRow("predicted", sims);
			}
		}
		
		ExperimentVisualizer.dumpToFile(output_dir+n+"_report.png");
		csv.close();
	}
}
