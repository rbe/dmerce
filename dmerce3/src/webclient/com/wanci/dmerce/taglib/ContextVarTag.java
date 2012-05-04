package com.wanci.dmerce.taglib;

import com.wanci.dmerce.exceptions.FieldNotFoundException;
import com.wanci.dmerce.taglib.form.FormField;
import com.wanci.dmerce.taglib.form.FormTag;
import com.wanci.dmerce.workflow.webapp.WebappState;
import com.wanci.dmerce.workflow.webapp.WebappWorkflowContext;
import com.wanci.dmerce.workflow.webapp.WebappWorkflowEngine;
import com.wanci.java.LangUtil;

/**
 */
public class ContextVarTag extends DmerceTagSupport {

    protected boolean DEBUG = true;

    protected boolean DEBUG2 = true;

    private FormTag formTag;

    /*
     * Variablen für die Tag-Attribute
     */
    private String name;

    private String pageid;

    private String formid;

    private int index;

    /**
     * 
     *  
     */
    public ContextVarTag() {
        super();
    }

    /**
     * Method called at start of Tag
     * 
     * @return either a EVAL_BODY_INCLUDE or a SKIP_BODY
     */
    public int doStartTag() {
        formTag = (FormTag) findAncestorWithClass(this,
            com.wanci.dmerce.taglib.form.FormTag.class);
        return SKIP_BODY;
    }

    /**
     * Method Called at end of Tag
     * 
     * @return either EVAL_PAGE or SKIP_PAGE
     */
    public int doEndTag() {

        WebappWorkflowEngine wwe;
        WebappWorkflowContext context;

        wwe = (WebappWorkflowEngine) pageContext.getSession().getAttribute(
            "qWorkflowEngine");
        assert wwe != null : "Das Contextvar-Tag kann nur innerhalb eines Workflows verwendet werden.";
        context = wwe.getWorkflowContext(wwe.getCurrentWorkflow());
        assert context != null : "Das Contextvar-Tag kann nur innerhalb eines Workflows-Kontexts verwendet werden.";

        try {

            if ((pageid == null | formid == null) && context.get(name) != null) {
                // Zunächst im Kontext suchen
                pageContext.getOut().write((String) context.get(name));
            }
            else {

                // Erst jetzt in der Menge der FormFields suchen
                if (pageid == null) {
                    // Wenn keine Page-ID gesetzt ist, auf aktuelle Seite
                    // verweisen
                    pageid = ((WebappState) context.getCurrentState())
                        .getPageId();
                }

                if (formid == null) {
                    // Wenn keine Form-ID gesetzt ist, auf aktuelles Formular
                    // verweisen
                    formid = formTag.getId();
                }

                LangUtil.consoleDebug(DEBUG2, this, "doEndTag(): processing "
                    + pageid + "." + formid + "." + name);

                if (formTag != null
                    && context.containsFormField(pageid, formTag.getId(), name)) {

                    try {
                        FormField ff = context.getFormField(pageid, formTag
                            .getId(), name);
                        if (ff.getValues() != null && ff.getValues().length > 1) {
                            pageContext.getOut().write(ff.getValues()[index]);
                        }
                        else {
                            if (ff.getValue() != null)
                                pageContext.getOut().write(ff.getValue());
                        }
                    }
                    catch (FieldNotFoundException e) {
                        pageContext.getOut().write(e.getMessage());
                    }

                }

            }
        }
        catch (Exception e) {
            LangUtil.consoleDebug(DEBUG2, this, "Exception: " + e.getMessage());
        }

        return EVAL_PAGE;

    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param formid
     *            The formid to set.
     */
    public void setFormid(String formid) {
        this.formid = formid;
    }

    /**
     * @param pageid
     *            The pageid to set.
     */
    public void setPageid(String pageid) {
        this.pageid = pageid;
    }

    public void setIndex(int index) {
        this.index = index;
    }

}