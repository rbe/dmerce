package com.wanci.dmerce.gui.monitor;

import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

/**
 * Default Implementierung des TabellenFilters.<br>
 * Fragt nach Bedingungen spaltenweise die übergebene
 * Zeile ab.
 * 
 * @author Ron Kastner
 * @version $Id: DefaultTableFilter.java,v 1.1 2003/11/10 18:47:39 rb Exp $
 * 
 */
public class DefaultTableFilter implements TableFilter {

	/** Dictionary mit Filter-Bedingungen */
	HashMap map = new HashMap();

	/**
	 * 
	 * @author rb
	 * @version $Id: DefaultTableFilter.java,v 1.1 2003/11/10 18:47:39 rb Exp $
	 *
	 * To change the template for this generated type comment go to
	 * Window>Preferences>Java>Code Generation>Code and Comments
	 */
	protected class FilterObj {

		/**
		 * 
		 */
		private int rule;
		
		/**
		 * 
		 */
		private Object[] obj;

		/**
		 * 
		 * @param rule
		 * @param obj
		 */
		public FilterObj(int rule, Object[] obj) {
			this.rule = rule;
			this.obj = obj;
		}

		/**
		 * 
		 * @return
		 */
		public int getRule() {
			return rule;
		}

		/**
		 * 
		 * @return
		 */
		public Object[] getObject() {
			return obj;
		}

	}

	/**
	 * 
	 */
	public final static int RULE_EQUALS = 0;
	public final static int RULE_CONTAINS = 1;
	public final static int RULE_FROM_TO = 2;
	public final static int RULE_BEGINS_WITH = 3;

	/**
	 * 
	 */
	public void setCondition(int col, int rule, Object[] obj) {
		map.put("" + col, new FilterObj(rule, obj));
	}

	/**
	 * 
	 */
	public void removeCondition(int col) {
		map.remove("" + col);
	}

	/**
	 * 
	 */
	public void clearAllConditions() {
		map.clear();
	}

	/**
	 * 
	 */
	public boolean displayRow(Vector row) {

		// Überprüfen der einzelnen Spalte
		for (int i = 0; i < row.size(); i++) {
			boolean result = checkCondition(i, row.elementAt(i));
			if (!result)
				return result;
		}

		return true;

	}

	/**
	 * 
	 * @param column
	 * @param obj
	 * @return
	 */
	protected boolean checkCondition(int column, Object obj) {

		FilterObj f = (FilterObj) map.get("" + column);

		if (f == null)
			return true;

		else {
			switch (f.getRule()) {

				case RULE_EQUALS :

					if (obj instanceof String)
						return obj.toString().equalsIgnoreCase(
							f.getObject()[0].toString());
					else
						return obj.equals(f.getObject()[0]);

				case RULE_CONTAINS :
					return (
						obj.toString().toUpperCase().indexOf(
							f.getObject()[0].toString().toUpperCase())
							!= -1);

				case RULE_FROM_TO :
					if (obj instanceof Date) {

						long t = ((Date) obj).getTime();
						long n1 = ((Date) f.getObject()[0]).getTime();
						long n2 = ((Date) f.getObject()[1]).getTime();

						return (t >= n1 && t <= n2);
					}
					else
						return (
							obj.hashCode() > f.getObject()[0].hashCode()
								&& obj.hashCode() < f.getObject()[1].hashCode());

				case RULE_BEGINS_WITH :

					for (int i = 0; i < f.getObject().length; i++) {
						if (obj
							.toString()
							.startsWith(f.getObject()[i].toString()))
							return true;
					}
					return false;
			}
		}

		return true;
	}

}