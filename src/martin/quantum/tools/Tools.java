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
package martin.quantum.tools;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;

import martin.math.Complex;
import martin.math.MathsItem;

public final class Tools {
	
	/** Default logger to output messages to */
	public static PrintStream logger = System.out;
	public static final boolean FORCE_SYSTEM_LOGGER = false;
	
	public static boolean SIMPLIFICATION_ENABLED = true;
	
	public static boolean VERBOSE = false;
	public static boolean SILENT = false;
	public static boolean MATHEMATICA_FRIENDLY_OUTPUT = false;
	
	/**
	 * Parse an expression like "(blah)" to "blah", removes only the outer layer of brackets. Brackets are not compulsory!
	 * @param input
	 * @return
	 */
	public static String trimAndCheckBrackets(String input) throws Exception {
		input = input.trim();
		
		if (input.isEmpty())
			return input;
		
		if (!balancedBrackets(input))
			throw new Exception("There is an unbalanced number of brackets in '"+input+"'");
		
		if (input.charAt(0) == '(' && input.charAt(input.length()-1) == ')') {
			final String trimmed = input.substring(1, input.length()-1);
			
			if (balancedBrackets(trimmed))
				return trimmed.trim();
		}
		
		return input;
	}
	
	/**
	 * Check whether a string contains a balanced set of brackets
	 * @param input
	 * @return
	 */
	public static boolean balancedBrackets(String input) {
		int start = 0;
		final int length = input.length();
		
		for (int i = 0; i < length; i++) {
			char c = input.charAt(i);
			
			if (c == ')' || c == '(') start += c == ')' ? -1 : 1;
			
			if (start < 0) return false;
		}
		return start == 0;
	}
	
	/**
	 * Splits input by a top level operator. <br/><br/>
	 * It will transform a string like "(1 + 2*(4 + 5)) + (5 + 6)" into "(1 + 2*(4 + 5)) " and " (5 + 6)"<br/>
	 * @param input formatted by {@link #trimAndCheckBrackets(String)}
	 * @param operator the top level operator that we're looking for, in the above case, it's "+". If more than one operator specified, the first one is considered default. Ie "8+2", 8 will have default operator
	 * @param stackoperators true for + and -. Allows expressions line (1 + 2) + 3 to evaluate to 1 + 2 + 3. Keep in mind for * this should be false since (1 * 3) + 4 * 5 will be undefined in this context
	 * @return the left and right side around operator or null if top level operator not available. Keep in mind that you would
	 * need to apply {@link #trimAndCheckBrackets(String)} to the outputs!
	 * @throws Exception 
	 */
	public static ArrayList<Tuple<char[], String>> splitByTopLevel(final String input, char[] operator, boolean stackoperators) throws Exception {

		int i = -1;
		int count = 0;
		final ArrayList<Tuple<char[], String>> db = new ArrayList<Tuple<char[], String>>();
		final ArrayList<Integer> idb = new ArrayList<Integer>();
		final int length = input.length();
		
		while ((i++) + 1 < length) {
			final char c = input.charAt(i);
			
			if (c == '(')
				count--;
			else if (c == ')')
				count++;

			if (count == 0 && doesItContain(operator, c))
				idb.add(i);
		}
		
		if (idb.isEmpty())
			return null;
		
		char prev = operator[0];
		int lastpos = -1;
		
		for (final Integer pos : idb) {
			final String item = trimAndCheckBrackets(input.substring(lastpos+1, pos));
			
			if (!item.isEmpty())
				db.add(new Tuple<char[], String>(new char[]{prev}, item));
			
			prev = input.charAt(pos);
			lastpos = pos;
		}
		
		final String last = trimAndCheckBrackets(input.substring(lastpos+1, input.length()));
			
		if (!last.isEmpty())	
			db.add(new Tuple<char[], String>(new char[]{prev}, last));
		
		if (!stackoperators)
			return db;
		
		final ArrayList<Tuple<char[], String>> additional = new ArrayList<Tuple<char[], String>>();
		final ArrayList<Tuple<char[], String>> toremove = new ArrayList<Tuple<char[], String>>();
		for (final Tuple<char[], String> t : db) {
			final ArrayList<Tuple<char[], String>> ans = splitByTopLevel(t.y, operator, stackoperators);
			
			if (ans == null)
				continue;
			
			toremove.add(t);
			for (final Tuple<char[], String> t2 : ans) {
				final int t2length = t2.x.length;
				final int tlength = t.x.length;
				final int l = t2length + tlength;
				
				final char[] newchar = new char[l];
				
				for (int ii = tlength; ii < l; ii++)
					newchar[ii] = t2.x[ii-tlength];
				
				for (int ii = 0; ii < tlength; ii++)
					newchar[ii] = t.x[ii];
				
				additional.add(new Tuple<char[], String>(newchar, t2.y));
			}
		}
		
		for (final Tuple<char[], String> t : additional)	
			db.add(t);
		
		for (final Tuple<char[], String> t : toremove)
			db.remove(t);
			
		return db;	
	}
	
	private static boolean doesItContain(final char[] chars, final char c) {
		for (int i = 0; i < chars.length; i++)
			if (chars[i] == c)
				return true;
		return false;
		
	}

	public static void printTuples(final ArrayList<Tuple<char[], String>> arrayList) {
		if (arrayList == null) {
			Tools.logger.println("Tuples null!");
			return;
		}
		
		int i = 0;
		
		for (Tuple<char[], String> t : arrayList) {
			
			if (t == null) {
				Tools.logger.println("NULL TUPLE!");
				continue;
			}
			
			i++;
			
			System.out.print("[");
			for(int ii = 0; ii < t.x.length; ii++)
				if (ii == 0)
					Tools.logger.print("'"+t.x[ii]+"'");
				else
					Tools.logger.print(", '"+t.x[ii]+"'");
			Tools.logger.println("]; "+t.y);
		}
		
		if (i == 0) Tools.logger.println("No tuples!");
		Tools.logger.println();
	}
	
	/**
	 * Generate string - value pairs for {@link MathsItem#getValue(HashMap)}
	 * @param variables a string {"a", "b"}
	 * @param values values like {1, 2} meaning a = 1, b = 2
	 * @return
	 */
	public static HashMap<String, Complex> generatePairs(final String[] variables, final Complex[] values) {
		final HashMap<String, Complex> ans = new HashMap<String, Complex>();
		for (int i = 0; i < variables.length; i++)
			ans.put(variables[i], values[i]);
		return ans;
	}

	
	/**
	 * Use for translating internal qubit representation to user visible representation.
	 * Convert zero based qubit indexing to one based qubit indexing.
	 * I know it's just qubitid+1 but using this function makes the code more readable.
	 * @param qubitid
	 * @return qubitid+1;
	 */
	public static final int zBOB(final int qubitid) {
		return qubitid+1;
	}
	
	/**
	 * Use for translating user visible representation to internal qubit representation.
	 * Convert one based qubit indexing to zero based qubit indexing.
	 * I know it's just qubitid-1 but using this function makes the code more readable.
	 * @param qubitid
	 * @return qubitid-1;
	 */
	public static final int oBZB(final int qubitid) {
		return qubitid-1;
	}
	
	public static int powerOfTwo(final int initnumber) throws Exception {
		int power = 0;
		int number = initnumber;
		
		while ((number >>= 1) != 0)
			power++;
		
		if ((1 << power) != initnumber)
			throw new Exception("The input is not a power of 2!");
		
		return power;
	}
}
