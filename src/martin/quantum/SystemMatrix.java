package martin.quantum;

import martin.coefficients.CoeffNumber;
import martin.coefficients.Coefficient;
import martin.operators.Operator;

public class SystemMatrix {

	public final int mNumbQubits;
	public Coefficient[] coeff;
	public final boolean[] measured;
	public final int size;

	public SystemMatrix(int qubitCount) {
		mNumbQubits = qubitCount;
		size = 1 << qubitCount;
		coeff = new Coefficient[size];
		measured = new boolean[qubitCount];
		for (int i = 0; i < qubitCount; i++) measured[i] = false;
		for (int i = 0; i < size; i++) coeff[i] = new CoeffNumber();
	}

	public void setCoeff(final Coefficient c, int... indeces) throws Exception {
		if (indeces.length != mNumbQubits)
			throw new Exception("Not enough indeces supplied");

		int id = 0;
		for (int i = 0; i < mNumbQubits; i++)
			if (indeces[i] != 0)
				id += indeces[i] * (1 << i);
		coeff[id] = c;

	}

	public Coefficient getCoeff(int... indeces) throws Exception {
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

	public int[] getIndexesFromId(int id) throws Exception {
		if (id >= size || id < 0)
			throw new Exception("Index does not exist");

		int[] idx = new int[mNumbQubits];
		for (int i = 0; i < mNumbQubits; i++)
			idx[mNumbQubits - i - 1] = id - ((id = (id >> 1)) << 1);

		return idx;
	}

	@Override
	public String toString() {
		String rep = "";

		for (int i = 0; i < size; i++) {
			int[] idxes;
			try {
				idxes = getIndexesFromId(i);

				rep += coeff[i]+" |";
				
				for (int j = 0; j < idxes.length; j++)
					if (!measured[j])
						rep += idxes[j];
					else
						rep += "_";

				rep += ">";
				if (i != size-1) rep += " + ";
				rep += "\n";
			} catch (Exception e) {
			}
		}
		
		return rep;
	}
	
	public void perform(Operator ... actions) throws Exception
	{
		for (int i = actions.length - 1; i >= 0; i--)
			actions[i].operate(this);
	}

}
