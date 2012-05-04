/**
 * Created on Jan 20, 2003
 *
 */
package com.wanci.java;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * @author rb
 *
 */
public class UtilUtil {

    /** You cannot create an instance */
    private UtilUtil() {
    }

	/** Erzeugt ein neues Properties-Objekt wobei nur die Einträge,
	 * die per Schlüssel/Array übergeben werden, in das neue Properties-
	 * Objekt übernommen werden
	 *
	 * @param filterKeys String-Array mit zu suchenden Schlüsseln
	 * @param props Properties-Objekt, das durchsucht werden soll
	 * @return Ein neues Properties-Objekt
	 */
	public static Properties filterPropertiesForKeys(
		String[] filterKeys,
		Properties props) {

		Properties p = new Properties();

		if (props != null) {
			Enumeration e = props.keys();

			while (e.hasMoreElements()) {

				String key = (String) e.nextElement();

				for (int i = 0; i < filterKeys.length; i++) {
					if (key.indexOf(filterKeys[i]) == 0)
						p.put(key, props.getProperty(key));

				}

			}

		}

		return p;

	}

	/** Erzeugt ein neues Properties-Objekt wobei nur die Einträge,
	 * die per Werte/Array übergeben werden, in das neue Properties-
	 * Objekt übernommen werden
	 *
	 * @param filterValues String-Array mit zu suchenden Werten
	 * @param props Properties-Objekt, das durchsucht werden soll
	 * @return Ein neues Properties-Objekt
	 */
	public static Properties filterPropertiesForValues(
		String[] filterValues,
		Properties props) {

		Properties p = new Properties();

		if (props != null) {

			Enumeration e = props.keys();

			while (e.hasMoreElements()) {

				String key = (String) e.nextElement();
				String value = props.getProperty(key);

				for (int i = 0; i < filterValues.length; i++) {
					if (value.indexOf(filterValues[i]) != -1)
						p.put(key, props.getProperty(key));
				}

			}

		}

		return p;

	}

	/** Zeigt ein Properties-Objekt auf stdout an
	 *
	 * @param props Ein Properties-Objekt
	 */
	public static void dumpProperties(Properties props) {

		if (props != null) {

			Enumeration e = props.keys();

			while (e.hasMoreElements()) {

				String key = (String) e.nextElement();
				System.out.println(key + "=" + props.getProperty(key));

			}

		}

	}

	public static String setKeysToCommaString(Set s) {
		StringBuffer sb = new StringBuffer();

		Iterator i = s.iterator();

		while (i.hasNext()) {

			Map.Entry o = (Map.Entry) i.next();

			sb.append((String) o.getKey());

			if (i.hasNext())
				sb.append(", ");

		}

		return sb.toString();
	}

	public static String searchHashMap(HashMap hm, String searchFor) {

		Iterator i = hm.entrySet().iterator();
		String newName = null;

		while (i.hasNext()) {

			Map.Entry entry = (Map.Entry) i.next();
            String key = (String) entry.getKey();
            String value = (String) entry.getValue();

			if (key.equals(searchFor))
				newName = value;

			if (value.equals(searchFor))
				newName = key;

		}

		return newName;
	}
    
    public static String searchHashMapIgnoreCase(HashMap hm, String searchFor) {

        Iterator i = hm.entrySet().iterator();
        String newName = null;

        while (i.hasNext()) {

            Map.Entry entry = (Map.Entry) i.next();
            String key = (String) entry.getKey();
            String value = (String) entry.getValue();

            if (key.equalsIgnoreCase(searchFor))
                newName = value;

            if (value.equalsIgnoreCase(searchFor))
                newName = key;

        }

        return newName;
    }
    
}