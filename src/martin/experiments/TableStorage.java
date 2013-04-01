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

import java.util.ArrayList;

public class TableStorage<R, C, V> {
	
	private final static int CHARS_IN_OUTPUT = 7;
	
	final ArrayList<R> rowheaders = new ArrayList<R>();
	final ArrayList<C> columns = new ArrayList<C>();
	final ArrayList<ArrayList<V>> values = new ArrayList<ArrayList<V>>();
	
	public int getRowCount() {
		return rowheaders.size();
	}
	
	public int getColCount() {
		return columns.size();
	}
	
	public void putRowHeader(final R head) {
		rowheaders.add(head);
		values.add(new ArrayList<V>());
	}
	
	public void putColumnHeader(final C head) {
		columns.add(head);
	}
	
	public R getRowHeader(final int r) {
		return rowheaders.get(r);
	}
	
	public C getColumnHeader(final int c) {
		return columns.get(c);
	}
	
	public V getValueAt(final int c, final int r) {
		return values.get(r).get(c);
	}
	
	public void putValue(final V value, final int r) {
		final ArrayList<V> row = values.get(r);
		row.add(value);
	}
	
	@Override
	public String toString() {
		final StringBuilder stb = new StringBuilder();
		stb.append("Table\t");
		final int ccount = columns.size();
		
		int id = 0;
		for (final C c : columns) {
			
			printObj(c, stb);
			if (id != ccount - 1) stb.append("\t");
			
			id++;
		}
		
		stb.append("\n");
	
		id = 0;
		for (final ArrayList<V> r : values) {
			printObj(rowheaders.get(id), stb);
			stb.append("\t");
			
			int id2 = 0;
			final int vcount = r.size();
			for (final V v : r) {
				printObj(v, stb);
				if (id2 != vcount - 1) stb.append("\t");
				
				id2++;
			}
			
			stb.append("\n");
			
			id++;
		}
		
		return stb.toString();
	}
	
	private static void printObj(final Object obj, final StringBuilder strb) {
		if (obj instanceof Object[]) {
			final Object[] arr = (Object[]) obj;
			
			strb.append("{");
			for (final Object o : arr) {
				final String v = o.toString();
				strb.append(v.length() < CHARS_IN_OUTPUT ? v : v.substring(0, CHARS_IN_OUTPUT));
				strb.append(", ");
			}
			strb.append("}");
		} else {
			final String v = obj.toString();
			strb.append(v.length() < CHARS_IN_OUTPUT ? v : v.substring(0, CHARS_IN_OUTPUT));
		}
	}
}
