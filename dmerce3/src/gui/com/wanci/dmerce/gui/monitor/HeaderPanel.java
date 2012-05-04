package com.wanci.dmerce.gui.monitor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import javax.swing.Icon;
import javax.swing.JComponent;

import com.wanci.dmerce.gui.ApplHelper;

/**
 * Dekorative Komponente für die Anzeige eines Titels.
 * @author Ron Kastner
 */
public class HeaderPanel extends JComponent {

	private static Font FONT = new Font("SansSerif",Font.BOLD, 17 );
	private String title = new String();
	private Icon   icon = null;
	
	public HeaderPanel() { this (""); }
	public HeaderPanel( String title ) { this ( title, (Icon)null ); }
	public HeaderPanel( String title, String filename ) { 	
		this ( title, ApplHelper.getIcon( filename ) );
	}
	public HeaderPanel( String title, Icon icon ) { 
		setTitle( title ); 
		setIcon ( icon );
	}
	
	public Dimension getPreferredSize() {
		return new Dimension( 100,40 );
	}
	
	public void setTitle( String title ) { this.title = title; };
	public String getTitle() { return title; }
	public void setIcon( Icon icon ) { this.icon = icon; };
	
	public void paintComponent(Graphics g) {
		
		Graphics2D g2 = (Graphics2D)g;
		g2.setColor( Color.white );
		g2.fillRect(0,0,getWidth(),getHeight()-3);

		// Gradienten malen
		Rectangle r = new Rectangle( getWidth() - 100, 0, 100, getHeight()-3 );
		GradientPaint gp = new GradientPaint( getWidth()-100, 10, Color.white, getWidth(), 10, new Color(210,210,210), false );
		g2.setPaint(gp);
		g2.fill(r);

		// Streifen
		int y = 4;
		
		g2.setColor( Color.white );
		g2.fillRect( getWidth() - 100, y, 100, 2);
		y+=9;
		g2.fillRect( getWidth() - 100, y, 100, 2);
		y+=9;
		g2.fillRect( getWidth() - 100, y, 100, 2);
		y+=9;
		g2.fillRect( getWidth() - 100, y, 100, 2);

		g2.setColor( Color.darkGray );
		g2.drawLine( 0, getHeight() - 2, getWidth(), getHeight() - 2 );
		
		g2.setColor( Color.white );
		g2.drawLine( 0, getHeight() - 1, getWidth(), getHeight() - 1 );

		g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
		g2.setFont( FONT );				
		g2.setColor( Color.black );
		g2.drawString( title, 10, (getHeight() + FONT.getSize()) / 2 );		
		
		if (icon != null) {
			icon.paintIcon( this, g2, getWidth()- icon.getIconWidth(), ( getHeight()- icon.getIconHeight() ) / 2 );
		}
	}

}
