<%@ include file="header.jsp" %>

<q:headline name="INCLUDE TEST"/>

<q:include url="http://localhost:8081/dmerce/workflow?qWorkflow=register"/>

<q:include url="http://localhost:8081/dmerce/workflow?qWorkflow=login"/>
	
<q:var name="message"/>

</body>
</html>
