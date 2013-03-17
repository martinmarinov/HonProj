package martin.experiments;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class CSVExporter {
	
	private final PrintWriter out;
	private final String delimiter;
	
	public CSVExporter(final String filename, final String delimiter) throws IOException {
		out = new PrintWriter(new FileWriter( new File(filename) ));
		this.delimiter = delimiter;
	}
	
	public void putRow(final String row_head, final Object[] row_data) {
		out.print(row_head);
		for (int i = 0; i < row_data.length; i++)
			out.print(delimiter+row_data[i]);
		out.println();
	}
	
	public void putRow(final String row_head, final double[] row_data) {
		out.print(row_head);
		for (int i = 0; i < row_data.length; i++)
			out.print(delimiter+row_data[i]);
		out.println();
	}
	
	public void putRow(final String row_head, String ... row_data) {
		out.print(row_head);
		for (int i = 0; i < row_data.length; i++)
			out.print(delimiter+row_data[i]);
		out.println();
	}
	
	public void close() {
		out.close();
	}
	

}
