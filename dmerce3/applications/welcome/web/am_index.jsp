<%@ include file="/init.jsp" %>

<q:usetemplate path="/layout.jsp">
<q:setbrick name="menu" path="bricks/menu.jsp"/>
<q:setbrick name="content">
<h1>Adress-Manager</h1>
<p>
Diese Anwendung zeigt ein kleines Adressbuch inkl. Fotos.<br><br>
Bitte achten Sie darauf, dass Sie die Beispieltabellen in Ihre Datenbank geladen
haben.
</p>
<%@ include file="am_footer.jsp" %>
</q:setbrick>
</q:usetemplate>