/*
 * InetServiceAudit.java
 *
 * Created on January 6, 2003, 8:36 PM
 */

package com.wanci.ncc.audit;

import com.wanci.ncc.servicemonitor.InetServiceDefinition;
import com.wanci.ncc.servicemonitor.MonitorInetServiceDefinition;



/** Definiert einen Audit/Datensatz für Monitoring von Internet Diensten
 *
 * @author  rb
 * @version $Id: InetServiceAudit.java,v 1.1 2004/02/02 09:41:53 rb Exp $
 */
public interface InetServiceAudit extends ServiceAudit {
    
    void chooseColor();
    
    long getMaximumResponseTime();
    
    long getResponseTime();
    
    boolean getServiceResponded();
    
    InetServiceDefinition getInetServiceDefinition();
        
    MonitorInetServiceDefinition getMonitorInetServiceDefinition();
        
    boolean isContentOk();
    
    void setServiceResponded(boolean serviceResponded);
    
    void setResponseTime(long responseTime);
    
    void setMaximumResponseTime(long maximumResponseTime);
    
    void setContentOk(boolean contentOk);
    
}