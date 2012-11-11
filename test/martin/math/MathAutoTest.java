package martin.math;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Random;

import martin.quantum.tools.Tools;

import org.junit.Test;


public class MathAutoTest {

	private enum mathtypes {exp, expression, fract, im, number, sqrt, symbol};
	
	private final static boolean disableSimplification = true;
	private final static mathtypes[] THINGS_TO_TEST = {mathtypes.number, mathtypes.symbol};
	
	private final static String[] POSSIBLE_SYMBOLS = new String[] {"a", "b"};
	private final static Complex[] values = {new Complex(-3, 5), new Complex(1, -2)};
	
	private final static double DOUBLE_RANGE = 5;
	private final static int NO_OF_ELS = 300;
	private final static int NO_OF_TRIES = 1000;
	
	private final static int PROGRESS_BAR_SIZE = 18;
	
	private final static Holder[] items = new Holder[NO_OF_ELS];
	private final static Random r = new Random();
	private final static HashMap<String, Complex> pairs = Tools.generatePairs(POSSIBLE_SYMBOLS, values);
	
	@Test
	public void test() {
		Runtime runtime = Runtime.getRuntime();
		runtime.gc();
		final long intused = runtime.totalMemory() - runtime.freeMemory();
		long peakmem = 0;

		final long start = System.currentTimeMillis();
		
		if (disableSimplification)  {
			MathExpression.simplify = false;
			MathExpression.deep_simplify = false;
		}
		
		for (int i = 0; i < NO_OF_ELS; i++) {
			final MathsItem m = generateRandomNumber();
			items[i] = new Holder(m.getValue(pairs), m);
		}
		
		final double sizeofblah = 100d / PROGRESS_BAR_SIZE;
		final double oneit = 100d / (double) NO_OF_TRIES;
		double so_far = 0;
		System.out.println("0%");
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
			
			generateComplexMathItem();
		}

		final long end = System.currentTimeMillis();
		final long aftused = runtime.totalMemory() - runtime.freeMemory();
		if (aftused > peakmem) peakmem = aftused;
		System.out.printf("done in %dms,  %.4f MB; peak usage %.4f MB\n", end - start, (aftused - intused) / 1048576d,  peakmem / 1048576d);
	}
	
	private final static MathsItem generateRandomNumber() {
		final MathExpression e = new MathExpression();
		e.add(new MathNumber(generateRandomDouble()));
		e.add(new MathIm(new MathNumber(generateRandomDouble())));
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
		
		final MathsItem item = new MathExpression();
		
		switch (THINGS_TO_TEST[rand]) {
		case exp:
			break;
		case fract:
			break;
		case expression:
			break;
		case im:
			break;
		case sqrt:
			break;
		
		case number:
			final double real = generateRandomDouble();
			final double im = generateRandomDouble();
			item.add(new MathNumber(real));
			item.add(new MathIm(new MathNumber(im)));
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
			expected.add(random.result);
			item.add(random.item);
		} else {
			expected.multiply(random.result);
			item.multiply(random.item);
			action = " [*] ";
		}
		
		final Complex actual = item.getValue(pairs);
		final String msg = "\n"+random.item+action+"\n"+orig+" = \n"+item+"\n(i.e. "+random.result+" to "+origexp+")\nand expected to get\n"+expected+" but got \n"+actual;
		assertEquals(msg, expected.R, actual.R, 0.0000001d);
		assertEquals(msg, expected.I, actual.I, 0.0000001d);
		putComplex(actual, item);
	}
	
	private final static double generateRandomDouble() {
		final double maxd = DOUBLE_RANGE;
		final double mind = -DOUBLE_RANGE;
		return r.nextDouble()*maxd+r.nextDouble()*mind;
	}
	
	private static class Holder {
		public Complex result;
		public MathsItem item;
		
		public Holder(final Complex result, final MathsItem item) {
			this.result = result;
			this.item = item;
		}
	}

}
