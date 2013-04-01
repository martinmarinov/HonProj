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
package martin.math;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Random;

import martin.quantum.tools.Tools;

import org.junit.Test;


public class MathAutoTest {

	private enum mathtypes {exp, fract, im, number, sqrt, symbol};
	
	private final static boolean disableSimplification = false; // disable simplification globally
	private final static boolean useIntegersOnly = true;
	private final static boolean useImaginary = true; // whether or not to disable imaginary numbers.
	private final static boolean alwaysSimplify = false; // whether or not to simplify only from time to time in order to generate more complex equations
	private final static mathtypes[] THINGS_TO_TEST = {
		mathtypes.sqrt, mathtypes.exp, mathtypes.fract,
		mathtypes.im, mathtypes.number, mathtypes.symbol
		}; // types of functions that would be tested, if you supply im here, useImaginary will be ignored
	
	private final static String[] POSSIBLE_SYMBOLS = new String[] {"a", "b"};
	private final static Complex[] values = {new Complex(-3, 5), new Complex(1, -2)};
	
	private final static double DOUBLE_RANGE = 5;
	private final static int NO_OF_ELS = 20;
	private final static int NO_OF_TRIES = 10;
	
	private final static double DOUBLE_COMPARISON_ACCURACY = 0.001; // %0.1 of the number means it's accurate
	
	private final static int PROGRESS_BAR_SIZE = 18;
	private final static int TEST_REPEATS = 10;
	
	private final static Holder[] items = new Holder[NO_OF_ELS];
	private final static Random r = new Random();
	private final static HashMap<String, Complex> pairs = Tools.generatePairs(POSSIBLE_SYMBOLS, values);
	
	@Test
	public void test() {
		for (int t = 0; t < TEST_REPEATS; t++) {
			Runtime runtime = Runtime.getRuntime();
			runtime.gc();
			final long intused = runtime.totalMemory() - runtime.freeMemory();
			long peakmem = 0;

			final long start = System.currentTimeMillis();

			if (disableSimplification)  {
				Tools.SIMPLIFICATION_ENABLED= false;
				MathExpression.DEEP_SIMPLIFY = false;
			}

			for (int i = 0; i < NO_OF_ELS; i++) {
				final MathsItem m = generateRandomNumber();
				items[i] = new Holder(m.getValue(pairs), m);
			}

			final double sizeofblah = 100d / PROGRESS_BAR_SIZE;
			final double oneit = 100d / (double) NO_OF_TRIES;
			double so_far = 0;
			System.out.println("\nTest #"+t+"\n0%");
			for (int i = 0; i < NO_OF_TRIES; i++) {
				so_far += oneit;

				if (so_far > sizeofblah) {
					so_far = 0;
					System.out.printf("%.2f%% - ", 100 * i / (double) NO_OF_TRIES );

					final long end = System.currentTimeMillis();
					final long aftused = runtime.totalMemory() - runtime.freeMemory();
					if (aftused > peakmem) peakmem = aftused;
					System.out.printf(": %dms,  %.4f MB\n", end - start, (aftused - intused) / 1048576d);
				}

				sanityCheck();
				generateComplexMathItem();
			}

			final long end = System.currentTimeMillis();
			final long aftused = runtime.totalMemory() - runtime.freeMemory();
			if (aftused > peakmem) peakmem = aftused;
			System.out.printf("done in %dms,  %.4f MB; peak usage %.4f MB\n", end - start, (aftused - intused) / 1048576d,  peakmem / 1048576d);
			runtime.gc();
		}
	}
	
	private final static void sanityCheck() {
		for (int i = 0; i < NO_OF_ELS; i++) {
			final Complex c1 = items[i].item.getValue(pairs);
			final Complex c2 = items[i].item.getValue(pairs);
			assertTrue("\nSanity check failed for \n"+items[i].item+",\nmeasurements yield different results!\norig: "+items[i].result+"\nm1: "+c1+"\nm2: "+c2+printPairs(), c1.similarValue(items[i].result, DOUBLE_COMPARISON_ACCURACY) && c2.similarValue(items[i].result, DOUBLE_COMPARISON_ACCURACY));
		}
	}
	
	private final static MathsItem generateRandomNumber() {
		final MathExpression e = new MathExpression();
		e.add(new MathNumber(generateRandomDouble()));
		if (useImaginary) e.add(new MathIm(new MathNumber(generateRandomDouble())));
		return e;
	}
	
	private final static void putComplex(final Complex c, final MathsItem m) {
		final int rand = r.nextInt(NO_OF_ELS);
		items[rand].result = c;
		items[rand].item = m;
	}
	
	private final static Holder getRandom() {
		final int rand = r.nextInt(NO_OF_ELS);
		return items[rand];
	}
	
	private final static void generateComplexMathItem() {
		int rand = r.nextInt(THINGS_TO_TEST.length);
		
		boolean simplify = alwaysSimplify ? true : r.nextBoolean();
		
		final MathsItem item = new MathExpression();
		
		final double real = generateRandomDouble();
		final double im = generateRandomDouble();
		
		switch (THINGS_TO_TEST[rand]) {
		
		case exp:
			item.add(new MathExp(getRandom().item));
			break;
		case fract:
			item.add(new MathFract(getRandom().item, getRandom().item));
			break;
		case sqrt:
			item.add(new MathSqrt(getRandom().item));
			break;
		case im:
			if (useImaginary) item.add(new MathIm(new MathNumber(im)));
			break;
		case number:
			item.add(new MathNumber(real));
			if (useImaginary) item.add(new MathIm(new MathNumber(im)));
			break;
		case symbol:
			final int ra = r.nextInt(POSSIBLE_SYMBOLS.length);
			item.add(new MathSymbol(POSSIBLE_SYMBOLS[ra]));
			break;
		default:
			return;
		}
		
		final Complex expected = item.getValue(pairs);
		final MathsItem orig = item.clone();
		final Complex origexp = expected.clone();
		final Holder random = getRandom();
		String action = " [+] ";
		if (r.nextInt(2) == 0) {
			expected.add(random.result.clone());
			item.add(random.item.clone());
		} else {
			expected.multiply(random.result.clone());
			item.multiply(random.item.clone());
			action = " [*] ";
		}
		
		final Complex actual = item.getValue(pairs);
		final Complex a2 = item.getValue(pairs);
		final MathsItem origbefore = item.clone();
		
		if (simplify) item.simplify();
		
		final Complex aft1 = item.getValue(pairs);
		final Complex aft2 = item.getValue(pairs);
		
		assertTrue("\nMeasurements before and after simplification different!\nBefore expr: "+origbefore+"\nAfter expr:"+item+"\nbefore1: "+actual+"\nbefore2: "+a2+"\nafter1: "+aft1+"\nafter2: "+aft2+"\n"+printPairs()+"\n", actual.similarValue(aft2, DOUBLE_COMPARISON_ACCURACY));
		
		assertTrue("\n"+random.item+action+"\n"+orig+" = \n"+item+"\ni.e. "+random.result+action+"\n"+origexp+" =\nexpected = "+expected+" but got \n"+actual+printPairs()+"\n", actual.similarValue(expected, DOUBLE_COMPARISON_ACCURACY));
		putComplex(actual, item);
	}
	
	private final static double generateRandomDouble() {
		final double maxd = DOUBLE_RANGE;
		final double mind = -DOUBLE_RANGE;
		final double rand = r.nextDouble()*maxd+r.nextDouble()*mind;
				
		return useIntegersOnly ? (int) rand : rand;
	}
	
	private static class Holder {
		private Complex result;
		private MathsItem item;
		
		public Holder(final Complex result, final MathsItem item) {
			this.result = result;
			this.item = item;
		}

	}
	
	private final static String printPairs() {
		String ans = "\n\nPairs:\n";
		for (String h : pairs.keySet())
			ans += h +" = "+pairs.get(h)+"\n";
		return ans;
	}

}
