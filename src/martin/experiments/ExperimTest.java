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

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

import martin.math.Complex;
import martin.math.MathsItem;
import martin.math.MathsParser;
import martin.quantum.tools.Tools;

public class ExperimTest {
	
	public static void main(String[] args) throws Exception {
		final MathsItem[] items = MathsParser.items("1/2,0,0,exp(Im((n*Pi)/4))/2,0,0,0,0,0,0,0,0,Im(1)/2,0,0,-Im(exp(Im((n*Pi)/4)))/2", ",");
		
		
		final Integer[] bins = new Integer[50];
		for (int i = 0; i < bins.length; i++) bins[i] = 0;
		
		double avg = 0;
		double min = 30;
		String maxs = "";
		String mins = "";
		double max = 0;
		int c = 0;
		
		for (int n = 0; n < 8; n++) {
			final Worksheet ws = new Worksheet("C:\\Users\\Martin\\Desktop\\data\\"+n+"_all.csv");
			final HashMap<String, Complex> rule = Tools.generatePairs(new String[]{"Pi", "n"}, new Complex[]{new Complex(Math.PI, 0), new Complex(n, 0)});
			final String[] res_rows = perform(items, ws, rule, null).csv.toString().split("\n");
			final int initc = c;
			
			for (int r = 0; r < res_rows.length; r+=2) {
				final String[] origs = res_rows[r].split(",");
				final Double[] orig = new Double[origs.length-1];
				double origsum = 0;
				for (int i = 1; i < origs.length; i++) origsum += orig[i-1] = Double.parseDouble(origs[i]);
				if (Math.abs(1-origsum) > 0.1) throw new Exception();
				final String[] sims = res_rows[r+1].split(",");
				final Double[] sim = new Double[sims.length-1];
				double simsum = 0;
				for (int i = 1; i < sims.length; i++) simsum += sim[i-1] = Double.parseDouble(sims[i]);
				if (Math.abs(1-simsum) > 0.1) throw new Exception();
				final Double[] res = new Double[sim.length];
				double ressum = 0;
				for (int i = 0; i < res.length; i++) ressum += res[i] = Math.abs(sim[i] - orig[i]);
				
				double avgres = ressum / (double) res.length;
				if (avgres > max) {
					max = avgres;
					maxs = "Max "+avgres+" in "+origs[0]+" in file "+n+"_all.csv";
				}
				if (avgres < min) {
					min = avgres;
					mins = "Min "+avgres+" in "+origs[0]+" in file "+n+"_all.csv";
				}
				
				int binid = (int) ((bins.length-1) * (avgres - 0.008804020937532187) / (double) ( 0.10810810886323452 - 0.008804020937532187 ));
				bins[binid]++;
				
				avg += avgres;
				c++;
			}
			System.out.println(n+"_all.csv ready! " + (c-initc) +" done");
		}
		
		CSVExporter csv = new CSVExporter(",");
		for (int i = 0; i < bins.length; i++)
			csv.putRow("" + (0.008804020937532187+(i / (double) bins.length) * (0.10810810886323452 - 0.008804020937532187)), "" + bins[i]);
		csv.saveToFile("bins.csv");
		
		System.out.println("Average residual of all: "+avg / (double) c);
		System.out.println(maxs);
		System.out.println(mins);
		
	}

	public static ExperimentalResult perform(final String input_file, final String lab_cluster, final HashMap<String, Complex> rule, final ProgressListener listener) throws Exception {
		
		final Worksheet ws = new Worksheet(input_file);
		
		final MathsItem[] items = MathsParser.items(lab_cluster, ",");
		final double init_norm = new LabCluster(items).getQuickProbability(rule);
		if ( Math.abs(1.0d - init_norm) > 0.1d)
			throw new Exception("Lab cluster input is not normalized to 1! (The norm is "+init_norm+")");
		
		return perform(items, ws, rule, listener);
	}
	
	private static ExperimentalResult perform(final MathsItem[] items, final Worksheet ws, final HashMap<String, Complex> rule, final ProgressListener listener1) throws Exception {
		final ProgressListener listener = listener1 == null ? new ProgressListener() {public void onProgress(double percentage) {}} : listener1;
		final ExperimentVisualizer vis = new ExperimentVisualizer();
		final CSVExporter csv = new CSVExporter(",");
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

					final double original = (double) ws.storage.getValueAt(c, r);
					final double prediction = lc.getQuickProbability(rule);
					
					originals.add(original);
					simulated.add(prediction);

				} catch (Exception e) {}
				
				sofar++;
				if ((sofar % 64) == 0)
					listener.onProgress(sofar / total);
			}

			if (!originals.isEmpty()) {
				final String rowhead = ws.storage.getRowHeader(r);
				final Double[] origs = originals.toArray(new Double[0]);
				final Double[] sims = simulated.toArray(new Double[0]);
				
				vis.add(rowhead+" actual", origs, rowhead+" simulated", sims);
				
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
