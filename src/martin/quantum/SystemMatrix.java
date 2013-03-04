package martin.quantum;

import java.util.HashMap;

import martin.math.Complex;
import martin.math.MathExpression;
import martin.math.MathNumber;
import martin.math.MathsItem;
import martin.operators.AddCoeffTogether;
import martin.operators.Operator;
import martin.quantum.tools.Tools;

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
			
			rep += coeff[i]+"\t"+getBraKet(i);
		}

		return rep;
	}
	
	public String getBraKet(int id) {
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
	
	public void performReverse(Operator ... actions) throws Exception
	{
		for (int i = actions.length - 1; i >= 0; i--) {
			Tools.logger.println("Performing "+actions[i]);
			actions[i].operate(this);
			if (Tools.VERBOSE) {
				(new AddCoeffTogether()).operate(this);
				Tools.logger.println(this);
				Tools.logger.println();
			}
		}
	}
	
	public void perform(Operator ... actions) throws Exception
	{
		for (int i = 0; i < actions.length; i++) {
			Tools.logger.println("Performing "+actions[i]);
			actions[i].operate(this);
			if (Tools.VERBOSE) {
				(new AddCoeffTogether()).operate(this);
				Tools.logger.println(this);
				Tools.logger.println();
			}
		}
	}
	
	/** Gets the vector representing the system matrix dotted with itself */
	public MathsItem getProbability() {
		final MathsItem probab = new MathExpression();
		
		for (int i = 0; i < size; i++) {
			
			if (coeff[i].isZero())
				continue;
			
			final MathsItem square = new MathExpression();
			square.add(new MathNumber(1));
			square.multiply(coeff[i].clone());
			
			final MathsItem complconj = coeff[i].clone();
			complconj.complexconjugate();
			square.multiply(complconj);
			square.simplify();
			
			probab.add(square);
		}

		probab.simplify();
		return probab;
	}

}
