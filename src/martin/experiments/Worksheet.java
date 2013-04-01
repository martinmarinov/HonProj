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

import java.io.BufferedReader;
import java.io.FileReader;

public class Worksheet {

	private final BufferedReader file;
	public final TableStorage<String, int[], Float> storage =  new TableStorage<String, int[], Float>();
	
	public Worksheet(final String filename) throws Exception {
		file= new BufferedReader(new FileReader(filename));
		
		String line = null;
		int lineid = 0;
		while ((line = file.readLine()) != null) {
		    lineid++;
		    final String[] items = line.split(",");
		    
		    if (lineid == 1) {
		    	for (int i = 1; i < items.length; i++)
		    		storage.putColumnHeader(LabCluster.parseBranches(items[i]));
		    } else {
		    	
		    	storage.putRowHeader(items[0]);
		    	
		    	final int[] values = new int[items.length-1];
		    	int sum = 0;
		    	for (int i = 0; i < values.length; i++) {
		    		values[i] = Integer.parseInt(items[1+i]);
		    		sum += values[i];
		    	}
		    	
		    	for (int i = 0; i < values.length; i++)
		    		storage.putValue(values[i] / (float) sum, lineid-2);
		    }
		}
	}
	
	@Override
	protected void finalize() throws Throwable {
		file.close();
		super.finalize();
	}
	
	@Override
	public String toString() {
		return storage.toString();
	}

}
