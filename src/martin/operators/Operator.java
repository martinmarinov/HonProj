package martin.operators;

import martin.quantum.SystemMatrix;

public interface Operator {
	
	void operate(SystemMatrix s) throws Exception;

}
