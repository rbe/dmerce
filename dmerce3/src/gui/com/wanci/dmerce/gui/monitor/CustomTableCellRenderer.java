package com.wanci.dmerce.gui.monitor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.HashMap;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import com.wanci.dmerce.gui.util.MonitorConstants;

/**
 * Sorgt dafür, dass bei der Dartstellung der XML-Log-Einträge
 * die Zeilen in der Tabelle bei den Message-Typen
 * mit unterschliedlichen Hintergrundfarben hinterlegt werden.
 * @author	Ron Kastner
 */
public class CustomTableCellRenderer extends JLabel implements TableCellRenderer, MonitorConstants {

	/** Datenstruktur zur Verwaltung der Farben */
	protected static HashMap colormap;

	static {
		colormap = new HashMap();
		colormap.put(MSGTYPE_ACCOUNT,COLOR_MSG_ACCOUNT);
		colormap.put(MSGTYPE_DEBUG,COLOR_MSG_DEBUG);
		colormap.put(MSGTYPE_ERROR,COLOR_MSG_ERROR);
		colormap.put(MSGTYPE_INFO,COLOR_MSG_INFO);	
		colormap.put(MSGTYPE_WARNING,COLOR_MSG_WARNING);	
	}
		
	public CustomTableCellRenderer() {
		super();
		setOpaque(true);
		setFont(new Font("SansSerif",Font.PLAIN,12));
		setForeground(Color.black);
	}	
	
	public Component getTableCellRendererComponent(JTable table,
	                                               Object value,
	                                               boolean isSelected,
	                                               boolean hasFocus,
	                                               int row,
	                                               int column) {
	
			//Component comp = super.getTableCellRendererComponent(table,value,isSelected,hasFocus,row,column);			
			
			this.setText( (value == null) ? "" : value.toString());
			
			String msg_type = null;
			
			if (table.getValueAt(row,COL_MSGTYPE) != null) {
				
				msg_type = (String)table.getValueAt(row,COL_MSGTYPE);//table.convertColumnIndexToModel(COL_MSGTYPE)); 				
				Object color = colormap.get(msg_type.toUpperCase());				
				setBackground( (color == null) ? Color.white : (Color)color);		
			}
			else
				setBackground(Color.white);
				
			if (isSelected) 			
			  setBackground(table.getSelectionBackground());
			
			return this;
	}
	
}

