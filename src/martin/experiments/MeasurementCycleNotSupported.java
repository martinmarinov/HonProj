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
package martin.experiments;

public class MeasurementCycleNotSupported extends Exception {

	private static final long serialVersionUID = 7486464308637360873L;
	
	public MeasurementCycleNotSupported(final String sequence, final int id) {
		super("Measurement '"+sequence.charAt(id)+"' in base "+sequence+" is not supported!");
	}
	
	public MeasurementCycleNotSupported(final String msg) {
		super(msg);
	}

}
