/*
 * Datei angelegt am 14.10.2003
 */
package com.wanci.dmerce.workflow.webapp;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.jdom.Document;
import org.jdom.Element;

import com.wanci.dmerce.exceptions.DmerceException;
import com.wanci.dmerce.exceptions.WorkflowConfigurationException;
import com.wanci.dmerce.exceptions.XmlPropertiesFormatException;
import com.wanci.dmerce.forms.FIELD;
import com.wanci.dmerce.forms.FORM;
import com.wanci.dmerce.forms.Forms;
import com.wanci.dmerce.kernel.XmlPropertiesReader;
import com.wanci.dmerce.servlet.XmlDocumentCache;
import com.wanci.dmerce.taglib.form.FormField;
import com.wanci.dmerce.workflow.AlwaysTrueCondition;
import com.wanci.dmerce.workflow.DoNothingAction;
import com.wanci.dmerce.workflow.Transition;
import com.wanci.dmerce.workflow.TransitionFactory;
import com.wanci.dmerce.workflow.Workflow;
import com.wanci.dmerce.workflow.xmlbridge.ACTION;
import com.wanci.dmerce.workflow.xmlbridge.CONDITION;
import com.wanci.dmerce.workflow.xmlbridge.FIELDMAP;
import com.wanci.dmerce.workflow.xmlbridge.FORMFIELD;
import com.wanci.dmerce.workflow.xmlbridge.FORMMAP;
import com.wanci.dmerce.workflow.xmlbridge.PAGE;
import com.wanci.dmerce.workflow.xmlbridge.PARAMETER;
import com.wanci.dmerce.workflow.xmlbridge.TRANSITION;
import com.wanci.dmerce.workflow.xmlbridge.WORKFLOW;
import com.wanci.dmerce.workflow.xmlbridge.Workflows;
import com.wanci.java.LangUtil;

/**
 * @author Masanori Fujita
 */
public class WebappWorkflowBuilder {

    private boolean DEBUG = false;

    private boolean DEBUG2 = false;

    public static final int MODUS_INSERT = 101;

    public static final int MODUS_UPDATE = 102;

    private Forms forms;

    private Workflows workflows;

    private Document security_xml;

    private Set securedWorkflowIds;

    /**
     * Initialisiert die Workflow-Engine mit den XML-Konfigurationen
     * workflows.xml, forms.xml und security.xml aus dem XmlDocumentCache.
     * 
     * @param xmldata
     *            XML-Konfiguration aus der workflows.xml
     */
    public WebappWorkflowBuilder() throws DmerceException {

        try {

            DEBUG = XmlPropertiesReader.getInstance().getPropertyAsBoolean(
                "debug");
            DEBUG2 = XmlPropertiesReader.getInstance().getPropertyAsBoolean(
                "core.debug");

        }
        catch (XmlPropertiesFormatException e) {
            e.printStackTrace();
        }

        LangUtil.consoleDebug(DEBUG2, this, "Initializing");

        this.forms = XmlDocumentCache.getInstance().getForms();
        this.workflows = XmlDocumentCache.getInstance().getWorkflows();
        //this.security_xml = XmlDocumentCache.getInstance().getSecurity();
        //rememberSecuredWorkflows();

    }

    /**
     * Erzeugt in der übergebenen Engine die Workflow-Strukturen anhand der
     * workflows.xml und der forms.xml
     * 
     * @param engine
     */
    public void readWorkflows(WebappWorkflowEngine engine)
        throws WorkflowConfigurationException {

        LangUtil.consoleDebug(DEBUG2, this, "Reading workflows");

        Iterator itWorkflows = this.workflows.getWorkflow().iterator();
        while (itWorkflows.hasNext()) {

            WORKFLOW wf = (WORKFLOW) itWorkflows.next();

            LangUtil.consoleDebug(DEBUG2, this, "Reading workflow id '"
                + wf.getId() + "'");

            Workflow newWorkflow = new Workflow(wf.getId());
            //newWorkflow.setSecured(isWorkflowSecured(wf.getId()));
            fillWorkflow(newWorkflow, wf);
            engine.addWorkflow(newWorkflow);

        }

    }

    /**
     * Baut ein Workflow-Objekt anhand des Workflow-Tags auf.
     * 
     * @param workflowToFill
     *            Das Workflow-Objekt, das aufgebaut werden soll.
     * @param workflowToReadFrom
     *            Der Workflow-Tag, aus dem das Workflow-Objekt aufgebaut werden
     *            soll.
     */
    public void fillWorkflow(Workflow workflowToFill,
        WORKFLOW workflowToReadFrom) throws WorkflowConfigurationException {

        LangUtil.consoleDebug(DEBUG2, this, "Filling workflow id '"
            + workflowToFill.getWorkflowId() + "'");

        // Eine Variable für den Anfangszustand reservieren
        boolean initialStateSet = false;

        // PAGE-Tags durchlaufen
        Iterator itPages = workflowToReadFrom.getPage().iterator();
        while (itPages.hasNext()) {

            PAGE page = (PAGE) itPages.next();
            WebappState newState = null;

            if (page.getFormid() == null || page.getFormid().equals("")) {

                // kein Formular in der PAGE angegeben
                // d.h. einfache Anzeigeseite
                newState = new WebappState(workflowToFill, page.getId(), page
                    .getId(), new WebappTemplate(page.getTemplate()));

                fillState(newState, page, 0);

            }
            else {

                // Formular in der PAGE angegeben´
                // unterscheide ob das Formular editierbar oder writeonly ist
                if (page.isEditable()) {

                    // Formular ist editierbar
                    // Dummy-Zustand erzeugen
                    newState = WebappState.createDummyState(workflowToFill,
                        page.getId());

                    // Transitionen für den Zustand erstellen
                    fillEditableFormState(newState, page);

                }
                else {

                    // Formular ist writeonly
                    newState = new WebappState(workflowToFill, page.getId(),
                        page.getId(), new WebappTemplate(page.getTemplate()),
                        page.getFormid());

                    // Transitionen für den Zustand erstellen
                    fillState(newState, page, MODUS_INSERT);

                }

                // Formmap zu dem verbundenen Formular holen
                Formmap newFormmap = getFormmap(page);
                // Formmap speichern
                newState.setFormmap(newFormmap);

            }

            // Den ersten Zustand in der workflows.xml als Anfangszustand
            // übernehmen
            if (!initialStateSet) {

                workflowToFill.setInitialState(newState);
                initialStateSet = true;

            }

        }

    }

    /**
     * Erzeugt einen Zustand, der den Beginn eines editierbaren Formularflusses
     * darstellt. Diese Methode erzeugt gleichzeitig auch die nachfolgenden
     * Zustände und Transitionen für das Editieren und Anlegen.
     * 
     * @param stateToFill
     *            Zustandsobjekt, das aufgebaut werden soll
     * @param pageToReadFrom
     *            PAGE-Tag, aus dem das Zustandobjekt erzeugt werden soll
     */
    private void fillEditableFormState(WebappState stateToFill,
        PAGE pageToReadFrom) throws WorkflowConfigurationException {

        LangUtil.consoleDebug(DEBUG2, this,
            "Creating states for editable form '" + pageToReadFrom.getFormid()
                + "' in workflow '" + stateToFill.getWorkflow().getWorkflowId()
                + "'");

        // ACHTUNG: Die Reihenfolge der Erzeugung der Transitionen ist hier
        // wichtig. Also Vorsicht
        // bei möglichen Änderungen!

        // Zweig zum Ändern von Datensätzen erzeugen
        WebappState updateState = new WebappState(stateToFill.getWorkflow(),
            pageToReadFrom.getId() + "_update_form", pageToReadFrom.getId(),
            new WebappTemplate(pageToReadFrom.getTemplate()), pageToReadFrom
                .getFormid());

        // Update-Transition
        Transition updateTransition = new Transition(stateToFill, updateState,
            pageToReadFrom.getId() + "_update_branch");
        new WebappContextVarSetCondition(updateTransition, "id");
        new WebappLoadAction(updateTransition, getFormmap(pageToReadFrom));
        fillState(updateState, pageToReadFrom, MODUS_UPDATE);

        // Zweig zum Einfügen von Datensätzen erzeugen
        WebappState insertState = new WebappState(stateToFill.getWorkflow(),
            pageToReadFrom.getId() + "_insert_form", pageToReadFrom.getId(),
            new WebappTemplate(pageToReadFrom.getTemplate()), pageToReadFrom
                .getFormid());

        // Insert-Transition
        Transition insertTransition = new Transition(stateToFill, insertState,
            pageToReadFrom.getId() + "_insert_branch");
        new AlwaysTrueCondition(insertTransition);
        new DoNothingAction(insertTransition);
        fillState(insertState, pageToReadFrom, MODUS_INSERT);

    }

    /**
     * Füllt einen Zustand, der nicht mit einem Formular verbunden ist oder
     * lediglich eine einfache Eingabeseite darstellt, mit den zugehörigen
     * Transitionen auf.
     * 
     * @param stateToFill
     *            Zustandsobjekt, das aufgebaut werden soll
     * @param pageToReadFrom
     *            PAGE-Tag, aus dem das Zustandobjekt erzeugt werden soll
     * @param modus
     *            Über diesen Parameter können Zusatzinformationen übergeben
     *            werden. Hierüber kann z.B. festgelegt werden, ob bei einer
     *            Transition vom Typ maintain eine Insert oder Update-Action
     *            eingefügt werden soll. Die möglichen Werte werden in dieser
     *            Klasse als Konstanten definiert.
     */
    private void fillState(WebappState stateToFill, PAGE pageToReadFrom,
        int modus) throws WorkflowConfigurationException {

        // Einfach alle Transitionen durchlaufen und behandeln lassen
        Iterator it = pageToReadFrom.getTransition().iterator();
        while (it.hasNext()) {

            TRANSITION transition = (TRANSITION) it.next();
            // Validator zwischenschalten
            String destinationStateId = "";
            // Neue Transition erzeugen
            Transition newTransition = new Transition(stateToFill,
                destinationStateId, transition.getName());

            if (transition.isValidation()) {

                // Dummy-Zustand für Validierung erzeugen
                String validationDummyStateId = transition.getName()
                    + (modus == MODUS_INSERT ? "_insert" : "_update")
                    + "_validation";

                WebappState validationDummyState = WebappState
                    .createDummyState(stateToFill.getWorkflow(),
                        validationDummyStateId);

                validationDummyState.setFormId(stateToFill.getFormId());
                validationDummyState.setPageId(pageToReadFrom.getId());

                // Validator-Transition vom Dummy-State zurück zum
                // Quell-Zustand schalten
                Transition validationFailedTransition = TransitionFactory
                    .createNoActionTransition(validationDummyState, stateToFill);
                new ValidationFailedCondition(validationFailedTransition);

                // bei erfolgreicher Validierung Transition zum Zielzustand
                // schalten und Aktion
                // übernehmen
                Transition validationSuccessfulTransition = new Transition(
                    validationDummyState, transition.getTarget(), transition
                        .getName()
                        + "_validation_successful");
                new AlwaysTrueCondition(validationSuccessfulTransition);
                handleAction(validationSuccessfulTransition, transition
                    .getAction(), modus);

                // Start-Transition zum Dummy-Zustand führen und Bedingung
                // übernehmen
                newTransition.setDestinationStateId(validationDummyState
                    .getStateId());
                handleCondition(newTransition, transition.getCondition());
                new DoNothingAction(newTransition);

            }
            else {

                // Destination-State-Id auf den eigentlichen Zielzustand aus
                // der workflows.xml
                // setzen
                newTransition.setDestinationStateId(transition.getTarget());

                // Standardbehandlung, Bauen von Standard-Transitionen
                handleCondition(newTransition, transition.getCondition());
                handleAction(newTransition, transition.getAction(), modus);

            }

        }

    }

    /**
     * Nimmt einen CONDITION-Tag entgegen und daraus ein passendes
     * Condition-Objekt.
     * 
     * @param conditionToReadFrom
     *            CONDITION-Tag, aus dem die Condition erzeugt werden soll.
     * @return eine fertig erzeugte Condition
     */
    private void handleCondition(Transition transitionToFill,
        CONDITION conditionToReadFrom) throws WorkflowConfigurationException {

        if (conditionToReadFrom == null) {
            new AlwaysTrueCondition(transitionToFill);
        }
        else {

            // Typ: benutzerdefinierte Java-Klasse angegegeben
            String strJavaClass = conditionToReadFrom.getJavaclass();
            String condition = conditionToReadFrom.getType();

            if (strJavaClass != null) {

                try {

                    Object[] argarray = {
                        transitionToFill
                    };

                    // Condition instanziieren
                    Class.forName(strJavaClass).getConstructors()[0]
                        .newInstance(argarray);

                    //return;

                }
                catch (Exception e) {

                    throw new WorkflowConfigurationException(
                        "Error while creating instance of class '"
                            + e.getClass().getName() + "': " + e.getMessage());

                }
            }
            // Typ: buttonpressed
            else if (condition.equals("buttonpressed")) {

                new ButtonPressedCondition(transitionToFill,
                    conditionToReadFrom.getValue());

                //return;

            }
            // Typ: equals
            else if (condition.equals("equals")) {

                new WebappEqualsCondition(transitionToFill, conditionToReadFrom);

                //return;

            }
            else {

                String message = "Could not find handler for condition type '"
                    + condition + "' in transition " + transitionToFill.getId()
                    + "' in workflow '"
                    + transitionToFill.getWorkflow().getWorkflowId()
                    + "'. Please check etc/workflows.xml";

                LangUtil.consoleDebug(DEBUG, message);

                // Wenn die Methode bis hierhin kommt, konnte kein Handler
                // gefunden werden.
                throw new WorkflowConfigurationException(message);

            }

        }

    }

    /**
     * Nimmt einen ACTION-Tag entgegen und daraus ein passendes Action-Objekt.
     * 
     * @param actionToReadFrom
     *            ACTION-Tag, aus dem die Action erzeugt werden soll.
     */
    private void handleAction(Transition transitionToFill,
        ACTION actionToReadFrom, int modus)
        throws WorkflowConfigurationException {

        LangUtil.consoleDebug(DEBUG2, this, "handleAction()");

        if (actionToReadFrom == null) {
            LangUtil.consoleDebug(DEBUG2, this,
                "handleAction() actionToReadFrom == null");
            new DoNothingAction(transitionToFill);
        }
        else {

            LangUtil.consoleDebug(DEBUG2, this,
                "handleAction() actionToReadFrom != null");

            String actionType = actionToReadFrom.getType();
            LangUtil.consoleDebug(DEBUG2, this, "handleAction() actionType="
                + actionType);

            // Typ: maintain zum INSERT bzw. UPDATE von Datensätzen
            if (actionType != null && actionType.equals("maintain")) {
                if (modus == MODUS_INSERT) {
                    LangUtil.consoleDebug(DEBUG2, this,
                        "handleAction() mode == MODUS_INSERT");
                    // Fieldlist holen
                    FieldList fieldlist = getFieldList(actionToReadFrom);
                    // Insert-Aktion erzeugen
                    new WebappInsertAction(transitionToFill, fieldlist);
                }
                else if (modus == MODUS_UPDATE) {
                    LangUtil.consoleDebug(DEBUG2, this,
                        "handleAction() modus == MODUS_UPDATE");
                    // Fieldlist holen
                    FieldList fieldlist = getFieldList(actionToReadFrom);
                    // Insert-Aktion erzeugen
                    new WebappUpdateAction(transitionToFill, fieldlist);
                }
                else
                    throw new WorkflowConfigurationException(
                        "In der workflows.xml wurde bei der Page "
                            + transitionToFill.getWorkflow().getWorkflowId()
                            + "."
                            + transitionToFill.getParentState().getStateId()
                            + " keine Formular-ID angegeben.");
                return;
            }

            // Typ: set wird verwendet, um explizit Werte im Kontext zu setzen.
            if (actionType != null && actionType.equals("set")) {
                HashMap paramMap = new HashMap();
                Iterator it = actionToReadFrom.getParameter().iterator();
                while (it.hasNext()) {
                    PARAMETER parameter = (PARAMETER) it.next();
                    LangUtil.consoleDebug(DEBUG2, this,
                        "handleAction() parameter=" + parameter.getName()
                            + " value=" + parameter.getValue());
                    paramMap.put(parameter.getName(), parameter.getValue());
                }
                new SetContextValuesAction(transitionToFill, paramMap);
                return;
            }

            // Typ: callproc wird verwendet, um Stored Procedures ohne
            // Rückgabewerte aufzurufen.
            if (actionType != null && actionType.equals("callproc")) {
                // Fieldlist holen
                FieldList fieldlist = getFieldList(actionToReadFrom);
                // Name der Stored-Proc aus Parameter-Liste holen
                assert actionToReadFrom.getParameter().size() == 1;
                PARAMETER parameter = (PARAMETER) actionToReadFrom
                    .getParameter().get(0);
                assert parameter.getName().equals("name");
                LangUtil.consoleDebug(DEBUG2, this,
                    "handleAction() stored procedure=" + parameter.getValue());
                new WebappCallprocAction(transitionToFill,
                    parameter.getValue(), fieldlist);
                return;
            }

            // Typ: benutzerdefinierte Java-Klasse angegegeben
            String strJavaClass = actionToReadFrom.getJavaclass();
            if (strJavaClass != null) {

                try {

                    LangUtil
                        .consoleDebug(
                            DEBUG2,
                            this,
                            "Java class for handling action '' in transition '"
                                + transitionToFill.getId()
                                + "' is '"
                                + strJavaClass
                                + "' having "
                                + Class.forName(strJavaClass).getConstructors().length
                                + " constructors.");

                    Object[] argarray = {
                        transitionToFill
                    };

                    Class.forName(strJavaClass).getConstructors()[0]
                        .newInstance(argarray);

                }
                catch (Exception e) {

                    if (DEBUG2)
                        e.printStackTrace();

                    String message = "Could not create instance of class '"
                        + e.getClass().getName() + "': " + e.getMessage();

                    LangUtil.consoleDebug(DEBUG, message);

                    throw new WorkflowConfigurationException(message);

                }

                return;

            }

            // Wenn die Methode bis hierhin kommt, konnte kein Handler gefunden
            // werden.
            throw new WorkflowConfigurationException(
                "Kein passender Handler für die ACTION vom Typ \""
                    + actionToReadFrom.getType() + "\" gefunden.");
        }
    }

    /**
     * @param transitionToReadFrom
     * @return
     */
    private FieldList getFieldList(ACTION actionToReadFrom) {

        FieldList fieldlist = new FieldList();
        Iterator it = actionToReadFrom.getFormfield().iterator();

        while (it.hasNext()) {

            FORMFIELD formfield = (FORMFIELD) it.next();

            fieldlist.addField(formfield.getFormid(), formfield.getName(),
                formfield.getPageid());

        }

        return fieldlist;

    }

    /**
     * Extrahiert aus der übergebenen PAGE eine Formmap.
     * 
     * @param page
     *            PAGE-Tag, aus dem dir Formmap ausgelesen werden soll.
     * @return Formmap-Objekt
     */
    private Formmap getFormmap(PAGE page) {

        FORMMAP formmap = page.getFormmap();

        if (formmap != null) {

            Formmap newFormmap = new Formmap();
            newFormmap.setPrimaryKeyName(formmap.getPrimarykey());
            newFormmap.setTable(formmap.getTable());

            Iterator it = formmap.getFieldmap().iterator();
            while (it.hasNext()) {

                FIELDMAP fieldmap = (FIELDMAP) it.next();

                newFormmap.setDbFieldName(fieldmap.getFormfield(), fieldmap
                    .getDbfield());

                if (fieldmap.isFile())
                    newFormmap.addFileField(fieldmap.getDbfield());

            }

            return newFormmap;

        }
        else
            return null;

    }

    /**
     * Liest über den XmlDocumentCache die forms.xml ein. In der übergebenen
     * Engine werden die ausgelesenen FormFields abgelegt.
     * 
     * @param engine
     */
    public void readForms(WebappWorkflowEngine engine) {

        Iterator itForms = forms.getForm().iterator();
        while (itForms.hasNext()) {

            FORM form = (FORM) itForms.next();

            engine.addForm(form.getId());
            Iterator itFieldOrGroup = form.getField().iterator();
            while (itFieldOrGroup.hasNext()) {

                Object o = itFieldOrGroup.next();
                // FIELD
                FIELD field = (FIELD) o;
                HashMap fieldsInForm = engine.getFields(form.getId());

                fieldsInForm.put(field.getName(), new FormField(field, form
                    .getId()));

            }

        }

    }

    /**
     * Initialisierungsmethode, die alle zu schützenden Workflows in einem Set
     * behält.
     */
    private void rememberSecuredWorkflows() {

        securedWorkflowIds = new HashSet();
        Element root = security_xml.getRootElement();

        Iterator itSecConst = root.getChildren("security-constraint")
            .iterator();
        while (itSecConst.hasNext()) {

            Element elSecConst = (Element) itSecConst.next();
            Iterator itWorkflow = elSecConst.getChildren("workflow").iterator();
            while (itWorkflow.hasNext()) {

                Element elWorkflow = (Element) itWorkflow.next();
                securedWorkflowIds.add(elWorkflow.getAttributeValue("id"));

            }

        }

    }

    /**
     * Prüft, ob der angegebene Workflow in der Menge der zu schützenden
     * Workflows enthalten ist.
     */
    private boolean isWorkflowSecured(String workflowId) {
        return securedWorkflowIds.contains(workflowId);
    }

}