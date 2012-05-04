/*
 * Datei angelegt am 29.09.2003
 */
package com.wanci.dmerce.workflow;

import com.wanci.dmerce.exceptions.WorkflowConfigurationException;
import com.wanci.dmerce.exceptions.WorkflowRuntimeException;

/**
 * @author Masanori Fujita
 */
public class Transition {
	
	/**
	 * Konstante, die anzeigt, dass diese Transition nicht feuern soll.
	 */
	public static final int SKIP = 0;
	/**
	 * Konstante, die anzeigt, dass diese Transition feuern soll.
	 */
	public static final int FIRE = 1; 
	
	private State parent;
	private State destination;
	private Condition condition;
	private Action action;
	private String transitionId;
	private String destinationStateId;

	/**
	 * Initialisiert den Zustandsübegang mit dem umgebenden Zustand, dem
	 * Zielzustand und einer Bedingung.
	 * 
	 * @param parent
	 *            Quellzustand, von dem der Zustandsübergang ausgeht.
	 * @param strId
	 *            ID für diese Transition
	 */
	public Transition(State parent, String strId) throws WorkflowConfigurationException {
		
		if (parent == null)
			throw new WorkflowConfigurationException("Der Quellzustand darf nicht null sein!");
		this.parent = parent;
		this.transitionId = strId;
		
		// Selbstverwaltung: Sich selbst zur Transitionsliste des
		// Eltern-Zustandes hinzufügen.
		parent.addTransition(this);
		
	}

	/**
	 * Initialisiert den Zustandsübegang mit dem umgebenden Zustand und dem
	 * Zielzustand.
	 * 
	 * @param parent
	 *            Quellzustand, von dem der Zustandsübergang ausgeht.
	 * @param destination
	 *            Zielzustand, zu dem der Zustandsübergang führt.
	 * @param strId
	 *            ID der Transition
	 */
	public Transition(State parent, State destination, String strId) throws WorkflowConfigurationException {
		
		this(parent, strId);
		setDestinationState(destination);
		
	}

	/**
	 * Initialisiert den Zustandsübegang mit dem umgebenden Zustand und der ID
	 * des Zielzustandes. Der Zielzustand muss zu diesem Zeitpunkt noc nicht
	 * existieren.
	 * 
	 * @param parent
	 *            Quellzustand, von dem der Zustandsübergang ausgeht.
	 * @param destinationStateId
	 *            ID des Zielzustandes, zu dem der Zustandsübergang führen
	 *            soll.
	 * @param strId
	 *            ID der Transition
	 */
	public Transition(State parent, String destinationStateId, String strId) throws WorkflowConfigurationException {
		this(parent, strId);
		setDestinationStateId(destinationStateId);
	}
	
	/**
	 * Gibt die ID der Transition zurück.
	 */
	public String getId() {
		return transitionId;
	}
	
	/**
	 * Setzt die ID der Transition.
	 * 
	 * @param id
	 *            Workflowweit eindeutige ID der Transition
	 */
	public void setId(String id) {
		transitionId = id;
	}

	/**
	 * Setzt die ID des Zielzustandes
	 * 
	 * @param destinationStateId
	 *            ID des Zielzustandes
	 */
	public void setDestinationStateId(String destinationStateId) {
		this.destinationStateId = destinationStateId;
	}
	
	/**
	 * Gibt die ID des Zielzustandes zurück.
	 * 
	 * @return ID des Zielzustandes
	 */
	public String getDestinationStateId() {
		return destinationStateId;
	}

	/**
	 * @return Bedingung, die mit diesem Zustandsübergang verknüpft ist.
	 */
	public Condition getCondition() throws WorkflowConfigurationException {
		if (condition == null)
			throw new WorkflowConfigurationException("Es wurde keine Condition festgelegt.");
		return condition;
	}
	
	/**
	 * Fügt dem Zustandsübergang eine Bedingung hinzu.
	 */
	public void setCondition(Condition condition) {
		assert condition != null : "Die übergebene Condition darf nicht null sein.";
		this.condition = condition;
	}

	/**
	 * @return Bedingung, die mit diesem Zustandsübergang verknüpft ist.
	 */
	public Action getAction() throws WorkflowConfigurationException {
		if (action== null)
			throw new WorkflowConfigurationException("Es wurde keine Action in der Transition "+getId()+" festgelegt.");
		return action;
	}
	
	/**
	 * Fügt dem Zustandsübergang eine Aktion hinzu.
	 * 
	 * @param condition
	 */
	public void setAction(Action action) throws WorkflowConfigurationException {
		if (action== null)
			throw new WorkflowConfigurationException("Die übergebene Action darf nicht null sein.");
		this.action = action;
	}
	
	/**
	 * @return Zielzustand, zu dem der Zustandsübergang führen soll.
	 */
	public State getDestinationState() throws WorkflowConfigurationException {
		destination = getWorkflow().getState(this.destinationStateId); 
		if (destination == null)
			throw new WorkflowConfigurationException("Es wurde kein Zielzustand festgelegt.");
		return destination;
	}
	
	/**
	 * Setzt den Zielzustand.
	 */
	public void setDestinationState(State destination) throws WorkflowConfigurationException {
		if (destination == null)
			throw new WorkflowConfigurationException("Der übergebene Zielzustand darf nicht null sein.");
		this.destination = destination;
		setDestinationStateId(destination.getStateId());
	}

	/**
	 * @return Quellzustand, von dem der Zustandsübergang ausgeht.
	 */
	public State getParentState() {
		return parent;
	}
	
	/**
	 * Ermittlt über den Quellzustand den zugehörigen Workflow.
	 * 
	 * @return Workflow, zu dem diese Transition gehört.
	 */
	public Workflow getWorkflow() {
		return getParentState().getWorkflow();
	}
	
	/**
	 * @return Prüft, ob die verbundene Bedingung erfüllt ist.
	 */
	public boolean isSatisfied(WorkflowContext context) throws WorkflowConfigurationException {
		assert context != null : "Übergebener Kontext darf nicht null sein.";
		return getCondition().isSatisfied(context);
	}
	
	/**
	 * Versucht diese Transition zu feuern. 1) Bedingung prüfen 2) Bei
	 * erfolgreicher Bedingung Aktion ausführen.
	 * 
	 * @return SKIP oder FIRE, um anzuzeigen, ob diese Transition gefeuert
	 *         werden soll oder die nächste Transition geprüft werden soll.
	 */
	public int process(WorkflowContext context) throws WorkflowRuntimeException, WorkflowConfigurationException {
		int status;
		if (isSatisfied(context)) {
			status = FIRE;
			getAction().execute(context);
		}
		else {
			status = SKIP;
		}
		return status;
	}
	
	/**
	 * Obligatorische Textausgabe
	 */
	public String toString() {
		String result;
		try {
			result = "Transition [ID="+getId()+", destination="+getDestinationState().getStateId()+"]\n";
			result += "    "+getCondition().toString()+"\n";
			result += "    "+getAction().toString()+"\n";
		} catch (WorkflowConfigurationException e) {
			e.printStackTrace();
			result = "Nicht korrekt konfigurierte Transition [ID="+transitionId+"] ("+e.getMessage()+")";
		}
		return result;		
	}
	
	/**
	 * Obligatorische Textausgabe
	 */
	public String toHtml() {
		String result;
		try {
			result = "<font color=\"#C040C0\"><b>Transition</b> [ID=<u>"+getId()+"</u>, destination="+getDestinationState().getStateId()+"]</font><br>\n";
			result += "&nbsp;&nbsp;&nbsp;&nbsp;"+getCondition().toHtml()+"<br>\n";
			result += "&nbsp;&nbsp;&nbsp;&nbsp;"+getAction().toHtml()+"<br><br>\n";
		} catch (WorkflowConfigurationException e) {
			e.printStackTrace();
			result = "Nicht korrekt konfigurierte Transition [ID="+transitionId+"] ("+e.getMessage()+")";
		}
		return result;		
	}
	
	/**
	 * Prüft, ob die Transition gültig ist.
	 * 
	 * @throws WorkflowConfigurationException
	 *             Wirft eine Exception, falls die Transition nicht korrekt
	 *             eingestellt ist. In der Exception wird genauer beschrieben,
	 *             welche Einstellungen noch gemacht werden müssen.
	 */
	public void validate() throws WorkflowConfigurationException {
		String strFehlerBegin = "Transition [ID="+transitionId+"]: ";
		String strFehler = "";
		
		try {
			State destState = getDestinationState();
		}
		catch (WorkflowConfigurationException e) {
			strFehler += strFehlerBegin + e.getMessage()+"\n";
		}
		
		try {
			Condition condition = getCondition();
		}
		catch (WorkflowConfigurationException e) {
			strFehler += strFehlerBegin + e.getMessage()+"\n";
		}
		
		try {
			Action action = getAction();
		}
		catch (WorkflowConfigurationException e) {
			strFehler += strFehlerBegin + e.getMessage()+"\n";
		}
		
		if (!strFehler.equals(""))
			throw new WorkflowConfigurationException(strFehler);
	}
}
