package martin.quantum;

import java.util.HashMap;

import martin.math.Complex;
import martin.math.MathExpression;
import martin.math.MathNumber;
import martin.math.MathsItem;
import martin.operators.Operator;

public class SystemMatrix {

	public final int mNumbQubits;
	public MathsItem[] coeff;
	public final boolean[] measured;
	public final int size;

	public SystemMatrix(int qubitCount) {
		mNumbQubits = qubitCount;
		size = 1 << qubitCount;
		coeff = new MathsItem[size];
		measured = new boolean[qubitCount];
		for (int i = 0; i < qubitCount; i++) measured[i] = false;
		for (int i = 0; i < size; i++) {
			coeff[i] = new MathExpression();
			coeff[i].add(new MathNumber(1));
		}
	}

	public void setCoeff(final MathsItem c, int... indeces) throws Exception {
		if (indeces.length != mNumbQubits)
			throw new Exception("Not enough indeces supplied");

		int id = 0;
		for (int i = 0; i < mNumbQubits; i++)
			if (indeces[i] != 0)
				id += indeces[i] * (1 << i);
		coeff[id] = c;

	}

	public MathsItem getCoeff(int... indeces) throws Exception {
		if (indeces.length != mNumbQubits)
			throw new Exception("Not enough indeces supplied");

		int id = 0;
		for (int i = 0; i < mNumbQubits; i++)
			if (indeces[i] != 0)
				id += indeces[i] * (1 << i);
		return coeff[id];
	}

	public int getIdFromIndexes(int... indeces) throws Exception {
		if (indeces.length != mNumbQubits)
			throw new Exception("Not enough indeces supplied");

		int id = 0;
		for (int i = 0; i < mNumbQubits; i++)
			if (indeces[i] != 0)
				id += indeces[i] * (1 << i);
		return id;
	}

	public int[] getIndexesFromId(int id) {

		int[] idx = new int[mNumbQubits];
		for (int i = 0; i < mNumbQubits; i++)
			idx[mNumbQubits - i - 1] = id - ((id = (id >> 1)) << 1);

		return idx;
	}

	@Override
	public String toString() {
		String rep = "";

		for (int i = 0; i < size; i++) {
			
			if (coeff[i].isZero())
				continue;
			
			if (i != 0) rep += " + \n";
			
			rep += coeff[i]+" "+getBraKet(i);
		}

		return rep;
	}
	
	private String getBraKet(int id) {
		final int[] idxes = getIndexesFromId(id);
		String rep = "|";

			for (int j = 0; j < idxes.length; j++)
				if (!measured[j])
					rep += idxes[j];
				else
					rep += "_";

			rep += ">";
		return rep;
	}
	
	public String printValues(final HashMap<String, Complex> rules) {
		String rep = "";

		for (int i = 0; i < size; i++) {
			
			if (coeff[i].isZero())
				continue;
			
			if (i != 0) rep += " + \n";
			
			rep += coeff[i].getValue(rules)+" "+getBraKet(i);
		}

		return rep;
	}
	
	public void perform(Operator ... actions) throws Exception
	{
		for (int i = actions.length - 1; i >= 0; i--)
			actions[i].operate(this);
	}

}
