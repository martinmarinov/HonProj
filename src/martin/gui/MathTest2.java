package martin.gui;

import martin.math.MathsItem;
import martin.math.MathsParser;
import martin.quantum.SystemMatrix;
import martin.quantum.tools.Tools;

public class MathTest2 {
	
	public static void main(String[] args) throws Exception {
		//final LabCluster lc = new LabCluster(7);
		//System.out.println("Cluster: "+lc);
		MathsItem p = MathsParser.parse("(-1*Im(-1)*((1)/(sqrt(2)))*((1)/(sqrt(2)))*((-Im(exp(Im(((7*Pi)/(4))))))/(2))*((1)/(sqrt(2)))*(-1)*(-1)*Im(-1)*((1)/(sqrt(2))))");//lc.getProbability();
		System.out.println("Pre before "+p+" = "+p.getValue(Tools.PI_rule));
		MathsItem square = SystemMatrix.square(p);
		System.out.println("After "+square+" = "+square.getValue(Tools.PI_rule));
	}

}
