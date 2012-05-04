<%@ include file="init.jsp" %>

<html>
<head>
<title>test</title>
</head>
<body>

<qauth:logged-in username="Master,User1,User2">
user geht
</qauth:logged-in>

<qauth:logged-in realm="Master,User">
realm geht
</qauth:logged-in>

</body>
</html>
