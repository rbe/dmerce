/*
 * Created on May 31, 2004
 *  
 */
package com.wanci.dmerce.test;

import java.text.NumberFormat;

/**
 * @author rb
 * @version $Id: StringToDouble.java,v 1.1 2004/06/03 23:51:54 rb Exp $
 *  
 */
public class StringToDouble {

    public static void main(String[] args) {

        String a = "abcd";
        String b = "1.0";

        Double da = null;
        Double db = null;

        try {

            da = new Double(a);
            db = new Double(b);
            NumberFormat nf = NumberFormat.getInstance();
            nf.setMaximumFractionDigits(0);
            b = nf.format(db);

        }
        catch (NumberFormatException e) {
        }

        System.out.println(a + " -> " + da);
        System.out.println(b + " -> " + db);

    }

}