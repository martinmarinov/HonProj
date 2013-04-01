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
package martin.gui;

import java.util.HashMap;

import martin.math.Complex;
import martin.math.MathExpression;
import martin.math.MathsItem;
import martin.math.MathsParser;
import martin.quantum.tools.Tools;

public class MathTest {

	private final static boolean disableSimplification = false;
	
	private final static String string = "(((a*a)/(-2+b))+((a*a*a)/(b-2))+((a)/(b-2))+((a*a*a)/(b-2))+((a*a*a*a)/(-2+b))+((a*a)/(b-2))+((a*a*a)/(b-2)))";// "( ( ((a)/(b-2))/(a+1) )+( ((a)/(b-2))/(1+a) )+(a)*( (((a)/(b-2)))/(a+1) ) )"; // golden error - "(-exp(1)*exp(-3))"
	private final static String string2 = "(Im(1.0677930211090731)-1.0254292105013625)";
	private final static int testid = 3;
	private final static String[] variables = new String[] {"a", "b"};
	private final static Complex[] values = {new Complex(0, 1), new Complex(0, 2)};
	private final static HashMap<String, Complex> pairs = Tools.generatePairs(variables, values);

	public static void main(String[] args) throws Exception {
		
		if (disableSimplification)  {
			Tools.SIMPLIFICATION_ENABLED = false;
			MathExpression.DEEP_SIMPLIFY = false;
		}

		Tools.logger.println(string + " - ORIGINAL\n");

		switch (testid) {
		case 0:
			testParse(string);
			break;
		case 1:
			testMaths(string);
			break;
		case 2:
			Tools.SIMPLIFICATION_ENABLED = false;
			final MathsItem mi = testMaths(string);
			final MathsItem mi2 = testMaths(string2);
			Tools.SIMPLIFICATION_ENABLED = true;			
			mi.multiply(mi2);
			Tools.logger.println("Final "+mi+" = "+mi.getValue(pairs));
			break;
		case 3:
			Tools.SIMPLIFICATION_ENABLED = false;
			final MathsItem mii = MathsParser.parse(Tools.trimAndCheckBrackets(string));
			Tools.logger.println(mii+"\nwith value "+mii.getValue(pairs)+"\n");
			Tools.SIMPLIFICATION_ENABLED = true;
			mii.simplify();
			Tools.logger.println(mii+"\nwith value "+mii.getValue(pairs));
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
			Tools.logger.println(it + " = " + it.getValue(pairs));
		else
			Tools.logger.println("EXPRESSION IS NULL!!!");
		
		final long end = System.currentTimeMillis();

		final long aftused = runtime.totalMemory() - runtime.freeMemory();
		Tools.logger.printf("Runtime: %dms\n", end - start);
		Tools.logger.printf("Memusage: %.4f KB\n", (aftused - intused) / 1024d);

		return it;
	}

	private static void testParse(String StrinsToParse) throws Exception {
		Tools.printTuples(Tools.splitByTopLevel(
				Tools.trimAndCheckBrackets(StrinsToParse), new char[] { '+',
						'-' }, true));
	}

}
