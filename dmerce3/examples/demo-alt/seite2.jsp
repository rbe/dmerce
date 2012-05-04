<%@ include file="init.jsp" %>
<q:usetemplate path="designvorlage.jsp">
	<q:setbrick name="header">
	  <img src="<q:res path="/images/dmerce_logo.gif"/>" border="0">&nbsp;III alpha2
	  <hr>
	  <h1>Seite 2</h1>
	</q:setbrick>
	<q:setbrick name="content" path="bricks/getraenke_form.jsp"/>
	<q:setbrick name="footer" path="bricks/footer.jsp"/>
</q:usetemplate>
