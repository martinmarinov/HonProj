package martin.gui;

import java.util.HashMap;

import martin.experiments.LabCluster;
import martin.math.Complex;
import martin.math.MathsItem;
import martin.math.MathsParser;
import martin.quantum.tools.Tools;

public class MathTest2 {

	private final static String[] variables = new String[] { "Pi" };
	private final static Complex[] values = { new Complex(Math.PI, 0) };
	private final static HashMap<String, Complex> rules = Tools.generatePairs(
			variables, values);
	
	public static void main(String[] args) throws Exception {
		//final LabCluster lc = new LabCluster(7);
		//System.out.println("Cluster: "+lc);
		MathsItem p = MathsParser.parse("-Im(exp(Im(Pi)))");//lc.getProbability();
		System.out.println("Pre before "+p+" = "+p.getValue(rules));
		p.complexconjugate();
		System.out.println("Pre prob "+p+" = "+p.getValue(rules));
	}

}
