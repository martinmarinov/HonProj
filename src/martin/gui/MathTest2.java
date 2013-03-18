package martin.gui;

import martin.math.MathsItem;
import martin.math.MathsParser;

public class MathTest2 {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		MathsItem it = MathsParser.parse("exp(Im((0*Pi)/4))/2");
		System.out.println(it+" = "+it.getValue(null));
		it.simplify();
		System.out.println(it+" = "+it.getValue(null));
	}

}
