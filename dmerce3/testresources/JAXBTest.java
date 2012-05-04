/*
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package test;
 
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

/*
 * $Id: JAXBTest.java,v 1.1 2004/02/09 18:14:03 rb Exp $
 *
 * Copyright 2003 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
 
public class JAXBTest {
    
    // This sample application demonstrates how to unmarshal an instance
    // document into a Java content tree and access data contained within it.
    
    public static void main( String[] args ) {
        try {
            // create a JAXBContext capable of handling classes generated into
            // the primer.po package
            JAXBContext jc = JAXBContext.newInstance( "com.wanci.dmerce.webservice.xjctest" );
            
            // create an Unmarshaller
            Unmarshaller u = jc.createUnmarshaller();
            
            // unmarshal a po instance document into a tree of Java content
            // objects composed of classes from the primer.po package.
            RDBSdataType rdbs = 
                (RDBSdataType) u.unmarshal( new FileInputStream( "/export/home/pg/netbeans_work/dmerce3/webservice/RDBDExample.xml" ) );
                
            // examine some of the content in the PurchaseOrder
            System.out.println( "Ausgabe: " );
            
            RDBSdataType.DATAType data = rdbs.getDATA();
            List rowlist = data.getRow();
            RDBSdataType.DATAType.RowType aRow = (RDBSdataType.DATAType.RowType) rowlist.get(0);
            List fieldlist = aRow.getField();
            AnyType anyType = null;
            List contentList;
            for (int i = 0; i < fieldlist.size(); i++) {
                anyType = (AnyType) fieldlist.get(i);
                contentList = anyType.getContent();
                for (int j = 0; j < contentList.size(); j++) {
                    System.out.println(contentList.get(j));
                }
            }
            
        } catch( JAXBException je ) {
            je.printStackTrace();
        } catch( IOException ioe ) {
            ioe.printStackTrace();
        }
    }
    
}
