/*
 * Datei angelegt am 29.09.2003
 */
package com.wanci.dmerce.workflow.webapp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;

import com.wanci.dmerce.ant.NamingConventions;
import com.wanci.dmerce.exceptions.DmerceException;
import com.wanci.dmerce.exceptions.WorkflowConfigurationException;
import com.wanci.dmerce.exceptions.WorkflowRuntimeException;
import com.wanci.dmerce.exceptions.XmlPropertiesFormatException;
import com.wanci.dmerce.kernel.XmlPropertiesReader;
import com.wanci.dmerce.taglib.form.FormField;
import com.wanci.dmerce.workflow.Workflow;
import com.wanci.java.LangUtil;

/**
 * Zentrales Organ für die Behandlung von Workflows.
 * 
 * @author Masanori Fujita
 */
public class WebappWorkflowEngine {

    public static boolean DEBUG = true;

    public static boolean DEBUG2 = true;

    /*
     * Container für alle Workflow-Definitionen
     */
    private LinkedHashMap mapWorkflows;

    private LinkedHashMap mapContexts;

    private LinkedHashMap mapForms;

    private ArrayList errorMessages;

    private Workflow currentWorkflow;

    DiskFileUpload fileUpload;

    public String FILE_STORE_PATH = "/tmp";

    public WebappWorkflowEngine() {

        try {

            DEBUG = XmlPropertiesReader.getInstance().getPropertyAsBoolean(
                "debug");
            DEBUG2 = XmlPropertiesReader.getInstance().getPropertyAsBoolean(
                "core.debug");
            FILE_STORE_PATH = XmlPropertiesReader.getInstance().getProperty(
                "fileupload.path");

        }
        catch (XmlPropertiesFormatException e) {
            e.printStackTrace();
        }

        mapWorkflows = new LinkedHashMap();
        mapContexts = new LinkedHashMap();
        mapForms = new LinkedHashMap();
        errorMessages = new ArrayList();
        fileUpload = new DiskFileUpload();

    }

    /**
     * Liefert die Workflow-Definition zu einer Workflow-ID zurück
     * 
     * @param wfid
     *            ID des Workflows
     * @return Workflow-Definition
     */
    public Workflow getWorkflow(String wfid) throws DmerceException {

        Object workflow = mapWorkflows.get(wfid);
        LangUtil.consoleDebug(DEBUG2, this, "getWorkflow(" + wfid
            + ") returns " + workflow);

        //assert workflow != null : "Workflow mit der ID " + wfid + " konnte
        // nicht gefunden werden.";
        if (workflow == null)
            throw new DmerceException("Could not find workflow '" + wfid
                + "'. Please check etc/workflows.xml");

        return (Workflow) workflow;

    }

    /**
     * Gibt den aktuell laufenden Workflow zurück.
     * 
     * @return Refernz auf den laufenden Workflow.
     */
    public Workflow getCurrentWorkflow() {
        assert currentWorkflow != null : "Zur Zeit ist kein Workflow aktiv.";
        return currentWorkflow;
    }

    /**
     * Setzt den aktuell laufenden Workflow. Dies ist eine interne Methode, die
     * von anderen Methoden dieser Klasse aufgerufen wird.
     * 
     * @param workflow
     */
    private void setCurrentWorkflow(Workflow workflow) {
        LangUtil.consoleDebug(DEBUG2, this, "setCurrentWorkflow("
            + workflow.getWorkflowId() + ")");
        this.currentWorkflow = workflow;
    }

    /**
     * Fügt einen Workflow zum Engine hinzu.
     * 
     * @param workflow
     */
    void addWorkflow(Workflow workflow) {
        assert workflow != null : "übergebener Workflow darf nicht null sein.";
        mapWorkflows.put(workflow.getWorkflowId(), workflow);
    }

    /**
     * Führt die Ablaufsteuerung durch.
     * 
     * @param request
     * @param response
     */
    public void processWorkflow(HttpServletRequest request,
        HttpServletResponse response) throws WorkflowRuntimeException,
        WorkflowConfigurationException, IOException, DmerceException {
        LangUtil.consoleDebug(DEBUG,
            "(:WorkflowEngine).processWorkflow(request, response)");
        // Angeforderten Workflow ermitteln
        String strWorkflowID = request.getParameter("qWorkflow");
        assert strWorkflowID != null && !strWorkflowID.equals("") : "qWorkflow wurde nicht als Request-Parameter gefunden.";
        LangUtil.consoleDebug(DEBUG, "Workflow-ID aus request: "
            + strWorkflowID);
        // Workflow zur Workflow-ID holen
        Workflow workflow = getWorkflow(strWorkflowID);
        // aktuell laufenden Workflow setzen.
        setCurrentWorkflow(workflow);
        // Falls Workflow geschützt ist, im geschützten Bereich ausführen
        if (workflow.isSecured()) {
            // Auf geschützten Bereich umleiten, falls noch nicht geschehen
            LangUtil.consoleDebug(DEBUG2, this, "Secured-Request:  "
                + request.getRequestURI());
            System.out.println(request.getContextPath());
            String requestPathWithoutContext = request.getRequestURI()
                .substring(request.getContextPath().length());
            System.out.println(requestPathWithoutContext);
            System.out.println(request.getQueryString());
            if (!requestPathWithoutContext.startsWith(NamingConventions
                .getSecuredAreaPathForWorkflow(workflow.getWorkflowId()))) {
                String targetUrl = request.getContextPath()
                    + NamingConventions.getSecuredAreaPathForWorkflow(workflow
                        .getWorkflowId()) + requestPathWithoutContext + "?"
                    + request.getQueryString();
                LangUtil.consoleDebug(DEBUG2, this, "forwarding to '"
                    + targetUrl + "'");
                response.sendRedirect(targetUrl);
                return;
            }
        }
        // Falls explizit ein Neustart angefordert wurde, den Kontext löschen
        if (request.getParameter("restart") != null) {
            mapContexts.remove(workflow);
        }
        // Workflow-Context holen (wird automatisch erzeugt, falls Workflow
        // noch nicht lüuft)
        WebappWorkflowContext context = getWorkflowContext(workflow);
        // Kontext mit den POST-Daten synchronisieren
        synchronizeContext(context, request, response);

        // Hilfsvariable, um anzuzeigen, ob wir und am Anfang des Workflows
        // befinden
        boolean isAtBeginOfWorkflow = false;
        // Variablen für Zielzustand und Zieltemplate
        WebappState destState = null;
        String destTemplate = null;
        while (destTemplate == null) {
            // Schleife betreten, solange keine Template-Seite ermittelt werden
            // kann.
            // Prüfe, ob wir uns am Anfang des Workflows befinden
            if (context.getCurrentState() == null) {
                // Noch kein CurrentState gesetzt, also am Anfang
                context.setCurrentState(workflow.getInitialState());
                isAtBeginOfWorkflow = true;
            }
            else {
                // Nur wenn gePOSTed wurde, bzw. wir uns am Anfang des
                // Workflows befinden, process durchführen
                if (context.getRequestMethod() == WebappWorkflowContext.POST
                    | isAtBeginOfWorkflow) {
                    // CurrentState gesetzt, also Zustandsübergang anstoüen
                    workflow.process(context);
                }
            }
            // Zielzustand holen
            destState = (WebappState) context.getCurrentState();
            // Template-Seite holen
            destTemplate = destState.getTemplate();
        }

        /*
         * EXPERIMENTELL AUSKOMMENTIERT AM 05.03.04 von MF Grund: Neue
         * Spezifikation: WorkflowContext wird nie gelöscht. // Falls der
         * Zielzustand ein Endpoint ist, wird der WorkflowContext // zu diesem
         * Workflow gelöscht if (destState.isEndpoint()) {
         * mapContexts.remove(workflow); }
         */

        // Zum Zielzustand verzweigen
        LangUtil.consoleDebug(DEBUG, "-> Zielzustand : "
            + context.getCurrentState().getStateId());
        LangUtil.consoleDebug(DEBUG, "-> Zieltemplate: "
            + ((WebappState) context.getCurrentState()).getTemplate());
        request.getSession().setAttribute("qTemplate", destTemplate);
        response.sendRedirect(request.getContextPath() + "/dmerce");

    }

    /**
     * Prüft, ob es einen WorkflowContext zu dem übergebenen Workflow gibt, und
     * erzeugt bei Bedarf einen neuen Kontext.
     * 
     * @param workflow
     *            Workflow, zu dem ein Kontext gesucht werden soll.
     * @return
     */
    public WebappWorkflowContext getWorkflowContext(Workflow workflow) {

        LangUtil.consoleDebug(DEBUG, "(:WorkflowEngine).getWorkflowContext("
            + workflow.getWorkflowId() + ")");

        WebappWorkflowContext context;
        Object o = mapContexts.get(workflow);

        if (o != null) {
            // LangUtil.consoleDebug(DEBUG, "WorkflowContext gefunden.");
            context = (WebappWorkflowContext) o;
        }
        else {
            // LangUtil.consoleDebug(DEBUG, "Kein WorkflowContext gefunden.
            // Erzeuge einen neuen WorkflowContext.");
            context = new WebappWorkflowContext(this);
            // context.setCurrentState(workflow.getInitialState());
            mapContexts.put(workflow, context);
        }

        return context;

    }

    /**
     * Gleicht die Werte der FormFields mit den vom Formular-POST übergebenen
     * Werten ab. Auüerdem werden
     */
    private void synchronizeContext(WebappWorkflowContext context,
        HttpServletRequest request, HttpServletResponse response)
        throws WorkflowRuntimeException {
        LangUtil.consoleDebug(DEBUG,
            "(:WorkflowEngine).synchronizeContext(context, request, response)");
        // Request-Methode übernehmen
        if (request.getMethod().equals("GET"))
            context.setRequestMethod(WebappWorkflowContext.GET);
        else if (request.getMethod().equals("POST"))
            context.setRequestMethod(WebappWorkflowContext.POST);
        else
            context.setRequestMethod(0);

        // Falls multipart/form-data Daten codieren
        List multipart_items = null;
        if (request.getContentType() != null
            && request.getContentType().indexOf("multipart/form-data") > -1) {
            try {
                // multipart/form-data parsen
                multipart_items = fileUpload.parseRequest(request);
            }
            catch (FileUploadException e) {
                e.printStackTrace();
                throw new WorkflowRuntimeException(
                    "WebappWorkflowEngine: Fehler beim Parsen des mutipart/form-data Inhalts. "
                        + e.getMessage());
            }
        }

        // Nur, wenn der Workflow schon läuft und damit einen aktuellen Zustand
        // besitzt,
        // sollen die Werte der Formularfelder übernommen werden.
        if (context.getCurrentState() != null) {
            // Formularfelder übernehmen
            synchronizeFormFields(context, request, response, multipart_items);
        }

        // Liste, die die Feldnamen speichert. Wird weiter unten benütigt,
        // um die Parameter zu übernehmen, die nicht Formularfelder sind.
        ArrayList fieldnameList = new ArrayList();
        Iterator it = context.getFormFields().iterator();
        while (it.hasNext()) {
            FormField element = (FormField) it.next();
            fieldnameList.add(element.getName());
        }

        // alle anderen (nicht in Formularen enthaltenen) Parameter in den
        // Kontext übernehmen
        Enumeration enumParameters = request.getParameterNames();
        while (enumParameters.hasMoreElements()) {
            String paramname = (String) enumParameters.nextElement();
            if (!fieldnameList.contains(paramname)) {
                context.put(paramname, request.getParameter(paramname));
            }
        }

        // Die restlichen multipart/form-data-Werte übernehmen
        if (request.getContentType() != null
            && request.getContentType().indexOf("multipart/form-data") > -1) {
            Iterator itFileItems = multipart_items.iterator();
            while (itFileItems.hasNext()) {
                FileItem item = (FileItem) itFileItems.next();
                if (!fieldnameList.contains(item.getFieldName())) {
                    context.put(item.getFieldName(), item.getString());
                }
            }
        }

    }

    /**
     * Gleicht die Werte der FormFields mit den vom Formular-POST übergebenen
     * Werten ab.
     */
    private void synchronizeFormFields(WebappWorkflowContext context,
        HttpServletRequest request, HttpServletResponse response,
        List multipart_items) throws WorkflowRuntimeException {
        LangUtil
            .consoleDebug(DEBUG,
                "(:WorkflowEngine).synchronizeFormFields(context, request, response)");
        // Formularfelder aktualisieren
        WebappState currentState = (WebappState) context.getCurrentState();
        String strFormId = currentState.getFormId();
        // Falls kein Formular verbunden, hier abbrechen
        if (strFormId == null)
            return;
        // Felder der aktuellen Page aus dem Kontext holen und über diese
        // Felder iterieren
        Iterator it = context.getFormFields(currentState.getPageId())
            .iterator();
        while (it.hasNext()) {
            FormField field = (FormField) it.next();

            // Formularwerte holen
            String[] values = null;
            if (request.getContentType() != null
                && request.getContentType().indexOf("multipart/form-data") > -1) {
                try {
                    // Werte aus dem Multipart/form-data holen
                    values = getMultipartValues(field.getName(),
                        multipart_items);
                }
                catch (Exception e) {
                    throw new WorkflowRuntimeException(
                        "WebappWorkflowEngine: Fehler beim Analysieren der multipart/form-data. "
                            + e.getMessage());
                }
            }
            if (request.getContentType() != null
                && request.getContentType().indexOf(
                    "application/x-www-form-urlencoded") > -1) {
                // Wert direkt aus dem Request holen
                values = request.getParameterValues(field.getName());
            }

            //Wert(e) setzen
            if (field.getType().equals("boolean")) {
                // Boolean-Felder manuell setzen
                if (values == null) {
                    field.setValue("false");
                }
                else {
                    if (!values[0].equals("true")) {
                        field.setValue("false");
                    }
                    else {
                        field.setValue("true");
                    }
                }
                /*
                 * } else if (field.getType().equals("list")) { // List-Felder
                 * manuell setzen if (values == null) { field.setValues(null); }
                 * else { if (values.length == 1) { field.setValue(values[0]); }
                 * else { field.setValues(values); } }
                 */
            }
            if (field.getType().equals("file")) {
                // Bei File-Feldern nur Wert setzen, falls neue Datei
                // hochgeladen worden ist.
                if (values != null) {
                    field.setValues(values);
                }
            }
            else {
                field.setValues(values);
            }
        }
    }

    /**
     * Fileupload
     * 
     * @param fieldname
     * @param fileItems
     * @return @throws
     *         Exception
     */
    private String[] getMultipartValues(String fieldname, List fileItems)
        throws Exception {

        assert fileItems != null;
        int itemCounter = 0;
        Iterator it = fileItems.iterator();

        while (it.hasNext()) {
            FileItem item = (FileItem) it.next();
            if (item.isFormField() && item.getFieldName().equals(fieldname))
                itemCounter++;
        }

        String[] result = new String[itemCounter];
        itemCounter = 0;
        it = fileItems.iterator();

        while (it.hasNext()) {

            FileItem item = (FileItem) it.next();

            if (item.getFieldName().equals(fieldname)) {

                if (item.isFormField()) {
                    // Normale Formularfelder
                    result[itemCounter++] = item.getString();
                }
                else {

                    // Dateiupload
                    if (!item.getName().equals("")) {

                        File uploadedFile = new File(item.getName());
                        LangUtil.consoleDebug(true, this,
                            "getMultipartValues(): uploadedFile.getName()="
                                + uploadedFile.getName());

                        File serverFile = new File(this.FILE_STORE_PATH + "/"
                            + (new Date()).getTime() + "_"
                            + uploadedFile.getName());
                        item.write(serverFile);

                        result = new String[2];
                        // Server-Dateiname
                        result[0] = serverFile.getName();
                        // Ursprünglicher Dateiname
                        result[1] = uploadedFile.getName();

                    }
                    else
                        result = null;

                }

            }

        }

        return result;

    }

    /**
     * Weiterleitung zu der Fehlerseite.
     */
    private void errorPage(HttpServletResponse response) throws IOException {
        mapContexts.remove(getCurrentWorkflow());
        response.sendRedirect("error.jsp");
    }

    /**
     * Bereitet darauf vor, FormFields zu einem Formular zu speichern.
     * 
     * @param formId
     */
    void addForm(String formId) {
        assert !mapForms.containsKey(formId) : "Das Formular mit der ID "
            + formId + " wurde schon hinzugefügt.";
        mapForms.put(formId, new LinkedHashMap());
    }

    /**
     * Gibt eine Map der FormFields zu dem angegebenen Formular zurück. Diese
     * Map sollte auch verwendet werden, um FormFields zu einem Formular
     * hinzuzufügen. Der Key ist jeweils der Name des Formularfeldes als String.
     * Der Value ist das Formularfeld als FormField-Objekt.
     * 
     * @param formId
     */
    public HashMap getFields(String formId) {
        assert formId != null : "Formular-ID darf nicht null sein.";
        Object o = mapForms.get(formId);
        assert o != null : "Für Formular mit der ID " + formId
            + " wurde noch keine Feldliste angelegt.";
        return (HashMap) o;
    }

    /**
     * Gibt eine Collection aller FormFields zurück, die aus der forms.xml
     * ausgelesen werden konnten.
     */
    public Collection getFields() {
        Collection colGesamt = new ArrayList();
        Collection colMapFields = mapForms.values();
        Iterator it = colMapFields.iterator();
        while (it.hasNext()) {
            HashMap map = (HashMap) it.next();
            colGesamt.addAll(map.values());
        }
        return colGesamt;
    }

    /**
     * Hilfsmethode, um in dem Array nach einem bestimmten Wert zu suchen.
     * 
     * @param values
     *            Array, in dem nach einem Wert gesucht werden soll
     * @param searchValue
     *            zu suchender Wert
     * @return true, wenn searchValue im Array enthalten ist, fase sonst
     */
    private boolean containsValue(String[] values, String searchValue) {

        if (values == null)
            return false;

        for (int i = 0; i < values.length; i++) {
            if (values[i].equals(searchValue))
                return true;
        }

        return false;

    }

    /**
     * Gibt die Fehlermeldungen zurück.
     * 
     * @return Fehlermeldungen als String in einer ArrayList
     */
    public ArrayList getErrorMessages() {
        return errorMessages;
    }

    /**
     * Gibt eine Menge zurück, in der die Workflow-IDs als Strings gespeichert
     * sind.
     */
    public Set getWorkflowIDs() {
        return this.mapWorkflows.keySet();
    }

}