package martin.math;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import martin.quantum.tools.Tools;
import martin.quantum.tools.Tuple;

public class Expr2 implements MathsItem {
	
	private final HashSet<HashSet<MathsItem>> items = new HashSet<HashSet<MathsItem>>();
	public static boolean simplify = true;

	@Override
	public boolean hasNegativeSign() {
		return false;
	}

	@Override
	public void negate() {
		for (HashSet<MathsItem> multiples : items)
			for (MathsItem it : multiples) {
				it.negate();
				break;
			}
	}

	@Override
	public boolean isZero() {
		return getValue(null).isZero();
	}

	@Override
	public boolean isOne() {
		return getValue(null).isOne();
	}

	@Override
	public Complex getValue(final HashMap<String, Complex> rules) {
		final Complex ans = new Complex(0, 0);
		
		for (HashSet<MathsItem> m : items) {
			final Complex mult = new Complex(1, 0);
			
			for (MathsItem mi : m) {
				final Complex smres = mi.getValue(rules);
				mult.multiply(smres);
			}
			
			ans.add(mult);
		}
		
		return ans;
	}
	
	/**
	 * Simplifies 2*3*4 + 5*5 into 24 + 25
	 */
	private boolean simplifyMultiplicators() {
		
		boolean h = false;
		
		for (final HashSet<MathsItem> hs : items) {
			
			boolean happened = true;
			
			while (happened) {
				
				happened = false;
			
				final HashSet<MathsItem> toremove = new HashSet<MathsItem>();
			
				outer:for (final MathsItem m : hs)
					for (final MathsItem m2 : hs)
						if (m != m2 && m2.multiply(m)) { // should we have m.getClass().equals(m2.getClass())  here?
							toremove.add(m);
							happened = true;
							break outer;
						}
			
				for (final MathsItem m : toremove)
					hs.remove(m);
				
				h |= !toremove.isEmpty();
			}
			
		}
		
		return h;
	
	}
	
	/**
	 * Remove nested expressions
	 */
	private boolean removeNestedExps() {

		boolean happend = false;
		final HashSet<HashSet<MathsItem>> itclone = new HashSet<HashSet<MathsItem>>();
		cloneItems(itclone);
		items.clear();


		outer : for (final HashSet<MathsItem> hs : itclone) {

			if (hs.size() == 1) {

				for (final MathsItem it : hs) {

					if (it instanceof Expr2) {

						final Expr2 h = (Expr2) it;
						
						for (final HashSet<MathsItem> hs2 : h.items) {
							final HashSet<MathsItem> nmult = new HashSet<MathsItem>();
							for (final MathsItem mit : hs2)
								nmult.add(mit.clone());
							items.add(nmult);
						}
						happend = true;
						
						continue outer;
					} 

					break;
				}

			}

			items.add(hs);

		}
		
		return happend;

	}
	
	private boolean performAdditionOnSimpleExpressions() {
		
		final HashSet<HashSet<MathsItem>> toremove = new HashSet<HashSet<MathsItem>>();
		boolean happened = true;
		boolean h = false;
		
		while(happened) {
			
			toremove.clear();
			happened = false;
			
			outer : for (final HashSet<MathsItem> m1 : items)
				if (m1.size() == 1)
					for (final HashSet<MathsItem> m2 : items)
						if (m1 != m2 && m2.size() == 1) {
							
							oneloop : for (final MathsItem i1 : m1)
								for (final MathsItem i2 : m2) {
									
									if (i1.add(i2)) {
										
										happened = true;
										toremove.add(m2);
										break outer;
										
									}
									
									break oneloop;
								}
							
						}
			
			for (final HashSet<MathsItem> m : toremove)
				items.remove(m);
			
			h |= !toremove.isEmpty();
			
		}
		
		return h;
	}

	private boolean removeAllOnes() {
		boolean happened = false;
		
		for (final HashSet<MathsItem> hs : items) {
			
			if (hs.size() < 2)
				continue;
			
			final HashSet<MathsItem> toremove = new HashSet<MathsItem>();

			
			for (final MathsItem m : hs)
				if (m.isOne())
					toremove.add(m);

			for (final MathsItem m : toremove)
				hs.remove(m);
			
			happened |= !toremove.isEmpty();
		}
		
		return happened;
	}
	
	private boolean removeAllZeros() {
		
		final HashSet<HashSet<MathsItem>> toremove = new HashSet<HashSet<MathsItem>>();
		
		for (final HashSet<MathsItem> hs : items) {

			for (final MathsItem m : hs)
				if (m.isZero()) {
					toremove.add(hs);
					continue;
				}
		}
		
		for (final HashSet<MathsItem> m : toremove)
			items.remove(m);
		
		return !toremove.isEmpty();
	}
	
	/**
	 * Translates a*3 + a*5 into a*8
	 * @return
	 */
	private boolean commonFactors() {
		
		final HashSet<HashSet<MathsItem>> toremove = new HashSet<HashSet<MathsItem>>();
		boolean happened = true;
		boolean h = false;

		while (happened) {
			happened = false;
			toremove.clear();

			outer : for (final HashSet<MathsItem> p1 : items)
				next : for (final HashSet<MathsItem> p2 : items)
					if (p1 != p2) {

						// for each possible pair find common factors
						final HashSet<MathsItem> common = new HashSet<MathsItem>();

						for (final MathsItem m1 : p1)
							for (final MathsItem m2 : p2)
								if (m1.equals(m2)) {
									common.add(m1);
									common.add(m2);
								}

						MathsItem p1it = null, p2it = null;

						int i = 0;
						for (final MathsItem m : p1)
							if (!common.contains(m)) {
								p1it = m;
								if (i != 0) continue next;
								i++;
							}
						if (i == 0) {
							p1it = new MathNumber(1);
							p1.add(p1it);
						}
						i = 0;
						for (final MathsItem m : p2)
							if (!common.contains(m)) {
								p2it = m;
								if (i != 0) continue next;
								i++;
							}
						if (i == 0) p2it = new MathNumber(1);

						if (p1it.add(p2it)) {
							// if one item remains and we can add it, p1it is changed now
							toremove.add(p2);
							happened = true;
							h = true;
							break outer;
						}

					}
			
			if (happened)
				for (final HashSet<MathsItem> rem : toremove)
					items.remove(rem);

		}
		
		return h;
	}

	@Override
	public void simplify() {
		
		if (!simplify)
			return;
		
		boolean happened = true;
		
		while (happened) {
		
			happened = false;
			
			happened |= removeAllZeros();
			happened |= removeAllOnes();
			happened |= simplifyMultiplicators();
			happened |= removeNestedExps();
			happened |= performAdditionOnSimpleExpressions();
			happened |= commonFactors();
		
		}
		
	}

	@Override
	public boolean multiply(MathsItem m) {

		if (m instanceof Expr2) {
			final Expr2 remote = (Expr2) m;

			final HashSet<HashSet<MathsItem>> itclone = new HashSet<HashSet<MathsItem>>();
			cloneItems(itclone);
			items.clear();

			for (final HashSet<MathsItem> remm : remote.items)
				for (final HashSet<MathsItem> mym : itclone) {

					// add mym * remm

					final HashSet<MathsItem> newit = new HashSet<MathsItem>();
					for (final MathsItem myit : mym)
						newit.add(myit.clone());
					for (final MathsItem remit : remm)
						newit.add(remit.clone());

					items.add(newit);

				}
			
		} else {
			for (HashSet<MathsItem> my : items)
				my.add(m.clone());
		}

		m = null;


		return true;
	}

	@Override
	public boolean add(MathsItem m) {
		
		if (m instanceof Expr2) {
			for (HashSet<MathsItem> multiples : ((Expr2) m).items) {
				final HashSet<MathsItem> clone = new HashSet<MathsItem>();
				
				for (MathsItem it : multiples)
					clone.add(it.clone());
				
				items.add(clone);
			}
		} else {
			final HashSet<MathsItem> multiple = new HashSet<MathsItem>();
			multiple.add(m.clone());
			items.add(multiple);
		}
		
		m = null;
		
		return true;
	}
	
	@Override
	public MathsItem clone() {
		final Expr2 e = new Expr2();
		
		cloneItems(e.items);
		
		return e;
	}
	
	private void cloneItems(final HashSet<HashSet<MathsItem>> dest) {
		for (final HashSet<MathsItem> m : items) {
			final HashSet<MathsItem> clone = new HashSet<MathsItem>();
			
			for (final MathsItem it : m)
				clone.add(it.clone());
			
			dest.add(clone);
		}
	}
	
	@Override
	public String toString() {
		
		simplify();
		
		final StringBuilder ans = new StringBuilder();
		
		ans.append("(");
		if (DEBUG) ans.append(" [Exp]");
		
		boolean first = true;
		
		for (HashSet<MathsItem> multiples : items) {
			
			if (multiples.isEmpty())
				continue;
			
			boolean first2 = true;
			
			for (MathsItem it : multiples) {
				
				final boolean itnegative = it.hasNegativeSign();
				
				if (!first2)
					ans.append("*");
				else if (DEBUG || (!itnegative && !first))
					ans.append("+");
				
				if (!first2 && itnegative && multiples.size() > 1) ans.append("(");
				ans.append(it);
				if (!first2 && itnegative && multiples.size() > 1) ans.append(")");
	
				first2 = false;
			}
			
			first = false;
				
		}
		
		ans.append(")");
		
		return ans.toString();
	}
	
	public static Expr2 fromString(final String input) throws Exception {
		
		final ArrayList<Tuple<char[], String>> els = Tools.splitByTopLevel(input, new char[]{'+', '-'}, true);
	
		if (els == null) {
			// dirty hack to fix inputs like "2*3"
			
			final ArrayList<Tuple<char[], String>> temp = Tools.splitByTopLevel(input, new char[]{'*'}, false);
			
			if (temp != null && !temp.isEmpty())
				return fromString("0+"+input);
			else
				return null;
		}
		
		//if (els.size() < 2) return null;
		
		final Expr2 ans = new Expr2();

		for (final Tuple<char[], String> ms : els)
			ans.items.add(parsemultiples(ms));

		ans.simplify();
		
		return ans;
	}
	
	private static HashSet<MathsItem> parsemultiples(final Tuple<char[], String> ms) throws Exception {
		final ArrayList<Tuple<char[], String>> multiples = Tools.splitByTopLevel(ms.y, new char[]{'*'}, false);
		final HashSet<MathsItem> forstore = new HashSet<MathsItem>();
		
		int sign = 1;
		
		for (int i = 0; i < ms.x.length; i++)
			if (ms.x[i] == '-')
				sign *= -1;
		
		if (multiples != null) {

			boolean first = true;
			
			for (final Tuple<char[], String> it : multiples) {
				final MathsItem mit = MathsParser.parse(it.y);
				if (first && sign < 1) mit.negate();
				forstore.add(mit);
				first = false;
			}
		} else {
			final MathsItem mit = MathsParser.parse(ms.y);
			if (sign < 1) mit.negate();
			forstore.add(mit);
		}
		
		return forstore;
	}

}
