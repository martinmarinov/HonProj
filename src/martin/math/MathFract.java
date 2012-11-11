package martin.math;

import java.util.ArrayList;
import java.util.HashMap;

import martin.quantum.tools.Tools;
import martin.quantum.tools.Tuple;

public class MathFract implements MathsItem {
	
	private MathExpression num, den;
	
	public MathFract(final MathsItem num, final MathsItem den) {
		if (num instanceof MathExpression)
			this.num = (MathExpression) num;
		else {
			final MathExpression e = new MathExpression();
			e.add(num);
			this.num = e;
		}
		
		if (den instanceof MathExpression)
			this.den = (MathExpression) den;
		else {
			final MathExpression e = new MathExpression();
			e.add(den);
			this.den = e;
		}
	}
	
	public static MathFract fromString(final String input) throws Exception {
		final ArrayList<Tuple<char[], String>> multiples = Tools.splitByTopLevel(input, new char[]{'/'}, false);
		
		if (multiples == null || multiples.size() != 2)
			return null;
		
		final MathsItem num = MathsParser.parse(Tools.trimAndCheckBrackets( multiples.get(0).y ));
		final MathsItem den = MathsParser.parse(Tools.trimAndCheckBrackets( multiples.get(1).y ));
		
		return new MathFract(num, den);
	}

	@Override
	public boolean hasNegativeSign() {
		return false;
	}

	@Override
	public void negate() {
		num.negate();
	}

	@Override
	public boolean isZero() {
		return num.isZero();
	}

	@Override
	public boolean isOne() {
		return num.equals(den);
	}

	@Override
	public Complex getValue(HashMap<String, Complex> rules) {
		final Complex n = num.getValue(rules);
		final Complex d = den.getValue(rules);
		
		final double c2d2 = d.I*d.I + d.R*d.R;
		
		final double R = (d.R*n.R+d.I*n.I) / c2d2;
		final double I = (d.R*n.I-d.I*n.R) / c2d2;
		
		n.R = R;
		n.I = I;
		return n;
	}

	@Override
	public void simplify() {
		num.simplify();
		den.simplify();
	}

	@Override
	public boolean multiply(MathsItem m) {
		if (m instanceof MathFract) {
			
			final MathFract mul = (MathFract) m;
			
			num.multiply(mul.num.clone());
			den.multiply(mul.den.clone());
			
			return true;
		} //else if (m instanceof MathsItem)
			//return num.multiply(((MathsItem) m).clone());
					
		return false;
	}

	@Override
	public boolean add(MathsItem m) {
		if (m instanceof MathFract) {
			
			final MathFract ad = (MathFract) m;
			
			if (ad.den.equals(den))
				return num.add(ad.num.clone());
			
			final MathExpression nD = (MathExpression) den.clone();
			nD.multiply(ad.den.clone());
			
			final MathExpression nN1 = (MathExpression) num.clone();
			nN1.multiply(ad.den.clone());
			
			final MathExpression nN2 = (MathExpression) ad.num.clone();
			nN2.multiply(den.clone());
			
			final MathExpression nN = new MathExpression();
			nN.add(nN1);
			nN.add(nN2);
			
			den = nD;
			num = nN;
			
			return true;
		}
		
		return false;
	}
	
	@Override
	public MathsItem clone() {
		return new MathFract(num.clone(), den.clone());
	}
	
	@Override
	public String toString() {
		return "("+num+"/"+den+")";
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof MathFract) {
			final MathFract mf = (MathFract) obj;
			return num.equals(mf.num) && den.equals(mf.den);
		} else if (obj instanceof MathsItem)
			return getValue(null).equals(((MathsItem) obj).getValue(null));
		
		return false;
	};

}
