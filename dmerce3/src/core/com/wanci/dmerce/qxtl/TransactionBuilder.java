/*
 * Created on 14.07.2003
 */
package com.wanci.dmerce.qxtl;

import java.util.Date;
import java.util.Iterator;
import java.util.Vector;

import org.jdom.Element;

import com.wanci.dmerce.kernel.XmlConfigReader;
import com.wanci.dmerce.kernel.XmlJdomViewer;

/**
 * @author pg
 * @author mm
 * 
 * $Id: TransactionBuilder.java,v 1.3 2003/09/11 16:06:15 mm Exp $
 */
public class TransactionBuilder
	extends XmlConfigReader
	implements TransactionBuilderInterface {

	/**
	 * 
	 */
	Element step;

	/**
	 * 
	 */
	Element transaction = new Element("transaction");

	
	/**
	 * 
	 */
	String sender = "";

	/**
	 * @return
	 */
	public Element getTransaction() {
		
		return transaction;
	}

	/**
	 * Ueberprueft, ob das error Element einen Wert hat (int)
	 * 
	 * @param transaction
	 * @return i = Fehlercode
	 */
	public int checkTransactionResult(Element transaction) {
		
		Element error = transaction.getChild("error");
		
		if (error != null) {
		
			int i = Integer.parseInt(error.getAttributeValue("value"));
			System.out.println(i);
			return i;
		}

		return 0;
	}

	/**
	 * Setzt die Transaktion mit dem angegebenen Element.
	 * 
	 * @param transaction
	 */
	public void setTransaction(Element transaction) {
		
		this.transaction = transaction;
	}

	/**
	 * Bestimmt den Sender der Transaktion. Ausserdem wird ein
	 * Attribute "date" mit dem Datum der Erstellung erzeugt und
	 * in der Transaktion eingefuegt
	 * 
	 * @param sender
	 */
	public TransactionBuilder(String sender) {
		
		setSender(sender);
		String d = new Date().toString();
		transaction.setAttribute("date", d);
	}

	/**
	 * @see com.wanci.dmerce.kernel.TransactionBuilderInterface#addStep()
	 */
	public boolean addStep(String type, String command, Vector parameter) {
		
		step = new Element(type);

		Iterator iterator = parameter.iterator();
		
		step.addContent(new Element("cmd").setAttribute("value", command));
		
		while (iterator.hasNext()) {
			step.addContent(
				new Element("parameter").setAttribute(
					"value",
					(String) iterator.next()));
		}

		transaction.addContent(step);
		
		return true;
	}

	/**
	 * Fügt eine neue Sql Anweisung hinzu.
	 * 
	 * @param command
	 * @param parameter
	 * @return
	 */
	public boolean addStepSql(String command, Vector parameter) {
		
		return addStep("sql", command, parameter);
	}

	/**
	 * Fügt eine weitere Stored Proc Anweisung hinzu.
	 * 
	 * @param command
	 * @param names
	 * @param types
	 * @param values
	 * @param returntype
	 * @param returnvalue
	 * @return
	 */
	public boolean addStepStoredProc(
		String command,
		Vector names,
		Vector types,
		Vector values,
		String returntype,
		String returnvalue) {
			
		step = new Element("storedproc").setAttribute("sender", sender);

		int i;
		
		step.addContent(new Element("cmd").setAttribute("value", command));
		
		for (i = 0; i < names.size(); i++) {
		
			step.addContent(
				new Element("parameter")
					.setAttribute("name", (String) names.get(i))
					.setAttribute("type", (String) types.get(i))
					.setAttribute("value", (String) values.get(i)));
		}
		
		step.addContent(
			new Element("returnvalue").setAttribute(
				"type",
				returntype).setAttribute(
				"value",
				returnvalue));
		
		transaction.addContent(step);
		
		return true;
	}

	/**
	 * FRAGE: Soll der String dann auch in ein Element umgewandelt werden?
	 *  
	 * @param string
	 * @return
	 */
	public boolean addStepAsString(String string) {
		
		System.out.println(string);
		
		return true;
	}

	/**
	 * Löscht alle Elemente von type.
	 * 
	 * @see com.wanci.dmerce.kernel.TransactionBuilderInterface#clear()
	 */
	public void clearSteps(String type) {

		if (transaction.getChildren(type) != null) {
			transaction.removeChildren(type);
		}
		else {
			transaction = new Element("transaction");
		}
	}

	/**
	 * serialize the vector and draw transaction tags
	 */
	public String toString() {
		
		XmlJdomViewer v = new XmlJdomViewer();
		
		return v.elToString(transaction);
	}

	/**
	 * @param sender
	 */
	public void setSender(String sender) {
		
		this.sender = sender;
		
		transaction.setAttribute("sender", sender);
	}

	/**
	 * test routine
	 */
	public static void main(String[] args) {

		TransactionBuilder tb = new TransactionBuilder("transactionbuilder");

		Vector params = new Vector();
		params.addElement(new String("hallo"));

		tb.addStep(
			"sql",
			"INSERT INTO status (id, description) VALUES (status_seq.nextval, %description%)",
			params);

		System.out.println(tb.toString());
		System.out.println(tb.getTransaction());

	}

}
