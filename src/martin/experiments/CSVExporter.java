package martin.experiments;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class CSVExporter {
	
	private final PrintWriter out;
	private final StringWriter string;
	private final String delimiter;
	
	public CSVExporter(final String delimiter) throws IOException {
		out = new PrintWriter(string = new StringWriter());
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
	
	@Override
	public String toString() {
		return string.toString();
	}
	
	public void saveToFile(final String filename) throws FileNotFoundException {
		final PrintWriter out = new PrintWriter(filename);
		out.write(string.toString());
		out.close();
	}
	

}
