<%@ include file="/init.jsp" %>

<q:usetemplate path="/design.jsp">
	<q:setbrick name="ueberschrift">dmerce&reg; Dokumentation</q:setbrick>
	<q:setbrick name="content">
	Dies ist die Entwickler-Dokumentation f&uuml;r dmerce&reg;.<p/>
	Hier werden Interna der dmerce-Architektur erkl&auml;rt und Vorgehensweisen vorgestellt,
	um das System zu warten und weiterzuentwickeln.<p/>
	</q:setbrick>
	<q:setbrick name="toc" path="devtoc.jsp"/>
	<q:setbrick name="subnavi">&nbsp;</q:setbrick>
</q:usetemplate>
