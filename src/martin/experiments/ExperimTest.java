package martin.experiments;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

import martin.math.Complex;
import martin.math.MathsItem;
import martin.math.MathsParser;

public class ExperimTest {

	public static ExperimentalResult perform(final String input_file, final String lab_cluster, final HashMap<String, Complex> rule, final ProgressListener listener) throws Exception {
		final CSVExporter csv = new CSVExporter(",");
		final Worksheet ws = new Worksheet(input_file);
		final ExperimentVisualizer vis = new ExperimentVisualizer();
		
		final MathsItem[] items = MathsParser.items(lab_cluster, ",");
		final double init_norm = new LabCluster(items).getQuickProbability(rule);
		if ( Math.abs(1.0d - init_norm) > 0.1d)
			throw new Exception("Lab cluster input is not normalized to 1! (The norm is "+init_norm+")");
		
		//csv.putRow("", ws.storage.columns.toArray(new int[0])); // put header

		long lastupdate = System.currentTimeMillis();
		final int rows = ws.storage.getRowCount();
		final int columns = ws.storage.getColCount();
		final double total = rows * columns;
		int sofar = 0;
		for (int r = 0; r < rows; r++) {

			final ArrayList<Double> originals = new ArrayList<Double>();
			final ArrayList<Double> simulated = new ArrayList<Double>();

			for (int c = 0; c < columns; c++) {

				final LabCluster lc = new LabCluster(items);
				try {
					lc.perform(ws.storage.getRowHeader(r),
							ws.storage.getColumnHeader(c));

					originals.add((double) ws.storage.getValueAt(c, r));
					simulated.add(lc.getQuickProbability(rule));

				} catch (Exception e) {}
				
				sofar++;
				if ((sofar % 32) == 0) {
					final long now = System.currentTimeMillis();
					if (now - lastupdate > 100) {
						lastupdate = now;
						listener.onProgress(sofar / total);
					}
				}
			}

			if (!originals.isEmpty()) {
				final String rowhead = ws.storage.getRowHeader(r);
				final Double[] origs = originals.toArray(new Double[0]);
				final Double[] sims = simulated.toArray(new Double[0]);
				
				vis.add(rowhead+" originals", origs, rowhead+" simulated", sims);
				
				csv.putRow(rowhead, origs);
				csv.putRow("predicted", sims);
			}
		}
		listener.onProgress(1.0d);
		
		final ExperimentalResult result = new ExperimentalResult();
		
		result.image = vis.getImage();
		result.csv = csv;
		
		return result;
	}
	
	public static class ExperimentalResult {
		public BufferedImage image;
		public CSVExporter csv;
	}
	
	public abstract static class ProgressListener {
		public abstract void onProgress(double percentage);
	}
}
