package com.wanci.dmerce.taglib.sql;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * <p>Executes its body if the last ResultSet tag received 0 rows
 * from the database.  You must be after a ResultSet tag and inside
 * a StatementTag or PreparedStatementTag, or an error will be generated.</p>
 *
 * <p>The subclass WasNotEmpty sets the "value" property to false,
 * and therefore executes its tag body is the ResultSet contained
 * <i>greater than</i> 0 rows.  
 * 
 * <p>JSP Tag Lib Descriptor
 * <pre>
 * &lt;name&gt;wasEmpty&lt;/name&gt;
 * &lt;tagclass&gt;org.apache.taglibs.dbtags.statement.WasEmptyTag&lt;/tagclass&gt;
 * &lt;bodycontent&gt;JSP&lt;/bodycontent&gt;
 * &lt;info&gt;
 * Executes its body if the last ResultSet tag received 0 rows
 * from the database.  You must be after a ResultSet tag and inside
 * a StatementTag or PreparedStatementTag, or an error will be generated.
 * &lt;/info&gt;
 * </pre>
 */
public class EmptyTag extends TagSupport {

	private boolean _value = true;

	/**
	 * Sets the necessary boolean value in order to
	 * execute the tag body.
	 * 
	 * @param value
	 */
	protected void setValue(boolean value) {
		_value = value;
	}

	public int doStartTag() throws JspTagException {

		Integer integer =
			(Integer) pageContext.getAttribute(
				"com.wanci.dmerce.taglib.sql.rowcount");

		if (integer == null) {
			throw new JspTagException("Empty and NotEmpty tags must follow a ResultSet tag.");
		}

		int rowCount = integer.intValue();

		boolean wasEmpty = true;
		
		if (rowCount > 0) {
			wasEmpty = false;
		}

		// evaluate the body only if wasEmpty matches the desired
		// value (true or false)
		if (wasEmpty == _value) {
			return EVAL_BODY_INCLUDE;
		}
		
		else {
			return SKIP_BODY;
		}
	}

}