/*
 * Created on Mar 26, 2003
 *
 */
package com.wanci.dmerce.csv;

import java.util.LinkedList;
import java.util.StringTokenizer;

import com.wanci.dmerce.exceptions.NoCsvDataPresentException;
import com.wanci.java.LangUtil;



/**
 * @author rb
 * @version $Id: CsvData.java,v 1.1 2004/03/29 13:39:35 rb Exp $
 *
 */
public class CsvData {
    
    /**
     *
     */
    private boolean DEBUG = false;
    
    /**
     *
     */
    private CsvSettings csvSettings = new CsvSettings();
    
    /**
     *
     */
    private String line;
    
    /**
     *
     */
    private int fieldCount = 0;
    
    /**
     *
     */
    private StringTokenizer tokens;
    
    /**
     *
     */
    private LinkedList dataFields = new LinkedList();
    
    /**
     *
     * @param line
     */
    public CsvData(String line) {
        setLine(line);
    }
    
    /**
     *
     * @param csvSettings
     * @param line
     */
    public CsvData(CsvSettings csvSettings, String line) {
        this.csvSettings = csvSettings;
        setLine(line);
    }
    
    /**
     *
     * @return
     */
    public StringTokenizer splitLine() {
        
        boolean lastSemicolon = false;
        boolean currentSemicolon = false;
        
        if (tokens == null && fieldCount == 0) {
            
            tokens =
            new StringTokenizer(line, csvSettings.getDelimiter(), true);
            
            while (tokens.hasMoreTokens()) {
                
                String s = tokens.nextToken();
                if (s.equals(csvSettings.getDelimiter())) {
                    
                    if (currentSemicolon)
                        lastSemicolon = true;
                    else
                        lastSemicolon = false;
                    
                    currentSemicolon = true;
                    
                }
                else {
                    currentSemicolon = false;
                }
                
                if (lastSemicolon && currentSemicolon)
                    s = null;
                
                if (!lastSemicolon && currentSemicolon)
                    continue;
                
                dataFields.add(s);
                fieldCount++;
                
            }
            
        }
        
        return tokens;
        
    }
    
    /**
     *
     * @param i
     * @return
     */
    public String getFieldDataByPosition(int i)
    throws NoCsvDataPresentException {
        
        LangUtil.consoleDebug(DEBUG, "getFieldDataByPosition(): " + i + "/" + dataFields.get(i));
        
        try {
            String s = (String) dataFields.get(i);
            if (s != null) {
                LangUtil.consoleDebug(DEBUG, "getFieldDataByPosition(): '" + s.trim() + "'");
                return s.trim();
            }
            else {
                LangUtil.consoleDebug(DEBUG, "getFieldDataByPosition(): '" + s + "'");
                return s;
            }
        }
        catch (IndexOutOfBoundsException e) {
            throw new NoCsvDataPresentException(
            "#fields: " + dataFields + ": " + e.getMessage());
        }
        
    }
    
    /**
     *
     * @return
     */
    public int getFieldCount() {
        return fieldCount;
    }
    
    /**
     *
     * @return
     */
    public String getLine() {
        return line;
    }
    
    /**
     *
     * @param line
     */
    public void setLine(String line) {
        this.line = line;
    }
    
    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        
                /*
                StringTokenizer st = new StringTokenizer("ha;;llo", ";", true);
                System.out.println(st.countTokens());
                 
                int i = 0;
                while (st.hasMoreTokens()) {
                 
                        System.out.println(i + ": " + st.nextToken());
                        i++;
                 
                }
                 */
        
        CsvSettings csvSettings = new CsvSettings();
        
        CsvData csvDataLine =
        new CsvData(csvSettings, "1;;a;b;c;d;;e;;f;;g;;;h");
        csvDataLine.splitLine();
        
        for (int i = 0; i < csvDataLine.getFieldCount(); i++) {
            
            try {
                System.out.println(
                "Pos: "
                + i
                + " Data: "
                + csvDataLine.getFieldDataByPosition(i));
            }
            catch (NoCsvDataPresentException e) {
                e.printStackTrace();
            }
            
        }
        
    }
    
}