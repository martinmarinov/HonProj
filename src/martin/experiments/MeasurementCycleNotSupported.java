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
