package com.wanci.dmerce.gui.monitor;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.table.DefaultTableCellRenderer;


/**
 * Eigener Renderer für die Anzeige
 * eines Icons in der zu sortierenden Spalte.
 * @author Ron Kastner
 */
public class LogTableHeaderRenderer extends DefaultTableCellRenderer {


	class ArrowIcon implements Icon {
		public final static int DOWN = 0;
		public final static int UP = 1;
		
		int direction = DOWN;
		
		public int getIconWidth()	{ return 9; }
		public int getIconHeight()	{ return 9; }
		public ArrowIcon(int dir) { direction = dir; }
		public void paintIcon(Component c,Graphics g,int x,int y) {
			g.setColor(Color.black);
			
			int[] xp = new int[0];
			int[] yp = new int[0];
			
			switch (direction) {
				case DOWN :
					xp = new int[] {
						x,
						x+getIconWidth(),
						x+getIconWidth()/2	
					};
					
					yp = new int[] {
						y,
						y,
						y+getIconHeight()
					};
					break;
				case UP:
					xp = new int[] {
						x+getIconWidth()/2,
						x,
						x+getIconWidth()	
					};
					
					yp = new int[] {
						y,
						y+getIconHeight(),
						y+getIconHeight()
					};				
			}
						
			g.fillPolygon(xp,yp,xp.length);
		}
	}
	
	private Icon arrow_down = new ArrowIcon(ArrowIcon.DOWN);
	private Icon arrow_up = new ArrowIcon(ArrowIcon.UP);
	
	private JLabel lab;
	private Component comp;
	private TableSorter sorter;


	public LogTableHeaderRenderer(TableSorter sorter) {
		this.sorter = sorter;
	}


	public Component getTableCellRendererComponent(JTable table,
                                               Object value,
                                               boolean isSelected,
                                               boolean hasFocus,
                                               int row,
                                               int column)	{
                      
                               	
		comp = super.getTableCellRendererComponent(table,value,isSelected,hasFocus,row,column);
		
		lab = (JLabel)comp;
		
		lab.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		lab.setOpaque(false);
		lab.setHorizontalTextPosition(SwingConstants.LEFT);


		lab.setIcon(null);
		
		if (sorter.getIndex() != -1) {
			if (sorter.getIndex() == table.convertColumnIndexToModel(column)) {
				lab.setIcon(sorter.getAscending() ? arrow_up : arrow_down);
			}
		}
			
		return lab;                                               	
	}
}