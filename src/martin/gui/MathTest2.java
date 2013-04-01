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
