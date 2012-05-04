package com.wanci.dmerce.taglib.sql;

import java.util.Collection;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.wanci.dmerce.exceptions.DmerceException;
import com.wanci.dmerce.webservice.SQLService;
import com.wanci.dmerce.webservice.db.QResult;

/**
 * Execute Tag Class this is part of the dmerce-sql-taglib
 * 
 * Dieser Tag führt eine Abfrage aufgrund vorher definierter Abfrage aus und
 * übernimmt die Speicherung des Abfrageergebnisses, das sich von anderen tags
 * aus abfragen lässt.
 * 
 * @author pg
 * @author mm
 * @version $Id: ExecuteTag.java,v 1.17 2004/05/16 19:43:23 rb Exp $
 *  
 */
public class ExecuteTag extends BodyTagSupport {

    /**
     * gibt einen result vom Typ QResult zurück
     */
    private QResult result = null;

    /**
     * id des Datensatzes
     */
    private String id = null;

    /**
     * setter for total row count
     * 
     * @param rowCount
     */
    protected void setTotalRowCount(int rowCount) {
        pageContext.setAttribute(id + ".rowcount", new Integer(rowCount));
    }

    /**
     * getter for row count
     * 
     * @return int Anzahl der Zeilen im Abfrageergebnis. Im Fehlerfall wird -1
     *         zurückgegeben.
     */
    public int getRowCount() {
        if (result != null)
            return result.getRowCount();
        else
            return -1;
    }

    /**
     * setter for id
     * 
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Das Execute Tag führt eine Sql Abfrage durch und prüft vorher ob ein SQL
     * Query gespeichert wurde. Falls ein Query vorhanden ist, wird es
     * ausgeführt ansonsten auf eine Fehlerseite weitergeleitet.
     * 
     * @return TAG-int
     */
    public int doStartTag() throws JspTagException {
        
    	SQLService query = null;
    	
        // Webservice initialisieren
        try {
            query = new SQLService();
        }
        catch (DmerceException e) {
            throw new JspTagException(e.getMessage());
        }

        // Check ob ein einfacher Query gespeichert wurde
        String sql = (String) pageContext.getAttribute("qsql." + id + ".query");
        if (sql != null) {
        	
            try {
                result = query.executeQuery(sql);
            }
            catch (Exception e) {
            	
                JspTagException jspe = new JspTagException(e.getClass()
                    .getName()
                    + ": " + e.getMessage());
                jspe.setStackTrace(e.getStackTrace());
                
                throw jspe;
                
            }
            
        }

        return EVAL_BODY_INCLUDE;
        
    }

    /**
     * gibt alle Datenzeilen einer Abfrage zurück
     * 
     * @return Collection die Datenbankergebnisse an QRow-Objekte
     */
    public Collection getRows() {
        return result.getRows();
    }

    /**
     * TAGLIB: EndTag
     * 
     * @throws JspTagException
     * @return TAG-int
     */
    public int doEndTag() {
        pageContext.removeAttribute(id);
        return EVAL_PAGE;
    }

}