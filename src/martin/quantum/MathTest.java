package martin.quantum;

import java.util.HashMap;

import martin.math.Complex;
import martin.math.MathExpression;
import martin.math.MathsItem;
import martin.math.MathsParser;
import martin.quantum.tools.Tools;

public class MathTest {

	private final static boolean disableSimplification = false;
	
	private final static String string = "sqrt(Im(-2)+1)*sqrt(-3+Im(-5))"; // golden error - "(-exp(1)*exp(-3))"
	private final static String string2 = "(Im(1.0677930211090731)-1.0254292105013625)";
	private final static int testid = 3;
	private final static String[] variables = new String[] {"a", "b"};
	private final static Complex[] values = {new Complex(-3, 5), new Complex(1, -2)};
	private final static HashMap<String, Complex> pairs = Tools.generatePairs(variables, values);

	public static void main(String[] args) throws Exception {
		
		if (disableSimplification)  {
			MathExpression.simplify = false;
			MathExpression.deep_simplify = false;
		}

		System.out.println(string + " - ORIGINAL\n");

		switch (testid) {
		case 0:
			testParse(string);
			break;
		case 1:
			testMaths(string);
			break;
		case 2:
			MathExpression.simplify = false;
			final MathsItem mi = testMaths(string);
			final MathsItem mi2 = testMaths(string2);
			MathExpression.simplify = true;			
			mi.multiply(mi2);
			System.out.println("Final "+mi+" = "+mi.getValue(pairs));
			break;
		case 3:
			MathExpression.simplify = false;
			final MathsItem mii = MathsParser.parse(Tools.trimAndCheckBrackets(string));
			System.out.println(mii+"\nwith value "+mii.getValue(pairs)+"\n");
			MathExpression.simplify = true;
			mii.simplify();
			System.out.println(mii+"\nwith value "+mii.getValue(pairs));
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
