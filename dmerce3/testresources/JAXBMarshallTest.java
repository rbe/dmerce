/*
 * JAXBMarshallTest.java
 *
 * Created on August 27, 2003, 2:17 PM
 */

package test;

import java.io.ByteArrayOutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import com.wanci.dmerce.webservice.db.xmlbridge.DATA;
import com.wanci.dmerce.webservice.db.xmlbridge.META;
import com.wanci.dmerce.webservice.db.xmlbridge.ObjectFactory;
import com.wanci.dmerce.webservice.db.xmlbridge.ROW;
import com.wanci.dmerce.webservice.db.xmlbridge.Rdbsdata;
import com.wanci.dmerce.webservice.db.xmlbridge.TYPEINFO;

/**
 *
 * @author  pg
 */
public class JAXBMarshallTest {
    
    /** Creates a new instance of JAXBMarshallTest */
    public JAXBMarshallTest() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            // Mit JAXB ein rdbsdata-Objekt füllen
            JAXBContext jc = JAXBContext.newInstance( "com.wanci.dmerce.webservice.db.xmlbridge" ); 
            ObjectFactory objFactory = new ObjectFactory();
            
            // typeinfo-Objekt erzeugen
            TYPEINFO typeinfo = objFactory.createTYPEINFO();
                // Zwei Spalten-Infos erzeugen, füllen und einfügen
                TYPEINFO.ColumnType coltype1 = objFactory.createTYPEINFOColumnType();
                coltype1.setName("Spalte 1");
                coltype1.setType("Integer");
                TYPEINFO.ColumnType coltype2 = objFactory.createTYPEINFOColumnType();
                coltype2.setName("Spalte 2");
                coltype2.setType("String");
            typeinfo.getColumn().add(coltype1);
            typeinfo.getColumn().add(coltype2);

            // meta-Objekt erzeugen
            META meta = objFactory.createMETA();
            meta.setSqlStatement("SELECT * FROM DUMMY_TABLE");
            meta.setTypeInfo(typeinfo);

            // data-Objekt erzeugen
            DATA data = objFactory.createDATA();
                // data-Objekt erzeugen
                ROW row1 = objFactory.createROW();
                row1.getField().add("123");
                row1.getField().add("Text 1");
                ROW row2 = objFactory.createROW();
                row2.getField().add("456");
                row2.getField().add("Text 2");
            data.getRow().add(row1);
            data.getRow().add(row2);
                
            // rdbsdata-Objekt erzeugen
            Rdbsdata rdbsdata = objFactory.createRdbsdata();
            rdbsdata.setMeta(meta);
            rdbsdata.setData(data);
            
            // rdbs-Objekt marshallen
            Marshaller m = jc.createMarshaller();
            m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
            ByteArrayOutputStream outputstream = new ByteArrayOutputStream();
            m.marshal( rdbsdata, outputstream );
            System.out.println(outputstream.toString());
        }
        catch (Exception e) {
            System.out.println("Fehler.");
        }
    }
    
}
