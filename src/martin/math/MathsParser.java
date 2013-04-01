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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import martin.quantum.tools.Tools;

/**
 * Parses a String and converts it to an {@link MathsItem}. See {@link #parse(String)} for more information
 * on how to do this.
 * 
 * @author Martin Marinov
 *
 */
public class MathsParser {
	
	/**
	 * The classes ordered by precedence
	 */
	private static final Class<?>[] CLASSES = {MathExpression.class, MathFract.class, MathNumber.class, MathExp.class, MathIm.class, MathSqrt.class, MathSymbol.class};
	
	/**
	 * The private static function that each of the classes in {@link #CLASSES} must implement
	 */
	private static final String FUNCTION_NAME = "fromString";
	
	/**
	 * Converts an input string to a {@link MathsItem}. Be careful, {@link MathsItem} are not
	 * required to allow for parsing, so not all of the input may be parsed.
	 * 
	 * In order to make sure that a parsing of a {@link MathsItem} could be done, define a
	 * "private static MathsItem fromString(final String input)" method in the class and add it
	 * to the {@link #CLASSES} array of {@link MathsParser}.
	 * 
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public final static MathsItem parse(String input) throws Exception {
		MathsItem it = null;
		input = Tools.trimAndCheckBrackets(input);
		
		for (Class<?> c : CLASSES)
			if ((it = invoke(c, input)) != null)
				break;
		
		if (it == null)
			throw new Exception("Error parsing '"+input+"'");
		
		return it;
	}
	
	private static final MathsItem invoke(final Class<?> c, final String input) throws Exception {
		final Method m = c.getMethod(FUNCTION_NAME, String.class);

		try {
			return (MathsItem) m.invoke(null, input);
		} catch (InvocationTargetException e) {
			throw (Exception) e.getCause();
		}
	}
	
	/**
	 * Parse an array of MathsItems
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public final static MathsItem[] items(final String input, final String delimiter) throws Exception {
		final String[] split = input.split(delimiter);
		final MathsItem[] ans = new MathsItem[split.length];
		for (int i = 0; i < ans.length; i++)
			ans[i] = parse(split[i]);
		return ans;
	}

}
