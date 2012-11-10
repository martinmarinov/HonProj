package martin.quantum;

import java.util.HashMap;

import martin.math.Complex;
import martin.math.Expr2;
import martin.math.MathsItem;
import martin.math.MathsParser;
import martin.quantum.tools.Tools;

public class MathTest {

	private final static String string = "sqrt(4)+(sqrt(2)*sqrt(2)+sqrt(2)*sqrt(2))";
	private final static String string2 = "1+3+5*(3+2*(-a-4)*(a+5))+a*(3+4)";
	private final static int testid = 1;
	private final static String[] variables = {"a"};
	private final static Complex[] values = {new Complex(-3, 0)};
	private final static HashMap<String, Complex> pairs = Tools.generatePairs(variables, values);

	public static void main(String[] args) throws Exception {

		System.out.println(string + " - ORIGINAL\n");

		switch (testid) {
		case 0:
			testParse(string);
			break;
		case 1:
			testMaths(string);
			break;
		case 2:
			Expr2.simplify = false;
			final MathsItem mi = testMaths(string);
			final MathsItem mi2 = testMaths(string2);
			Expr2.simplify = true;			
			mi.multiply(mi2);
			System.out.println("Final "+mi+" = "+mi.getValue(pairs));
			break;
		}

	}
	
	private static MathsItem testMaths(String StrinsToParse) throws Exception {
		Runtime runtime = Runtime.getRuntime();
		final long intused = runtime.totalMemory() - runtime.freeMemory();

		final long start = System.currentTimeMillis();

		final MathsItem it = MathsParser.parse(Tools
				.trimAndCheckBrackets(StrinsToParse));

		if (it != null)
			System.out.println(it + " = " + it.getValue(pairs));
		else
			System.out.println("EXPRESSION IS NULL!!!");
		
		final long end = System.currentTimeMillis();

		final long aftused = runtime.totalMemory() - runtime.freeMemory();
		System.out.printf("Runtime: %dms\n", end - start);
		System.out.printf("Memusage: %.4f KB\n", (aftused - intused) / 1024d);

		return it;
	}

	private static void testParse(String StrinsToParse) throws Exception {
		Tools.printTuples(Tools.splitByTopLevel(
				Tools.trimAndCheckBrackets(StrinsToParse), new char[] { '+',
						'-' }, true));
	}

}
