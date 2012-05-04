<%@ page contentType="text/html" language="java" import="java.sql.*" %>

<html>
<body>
MySQL Connection Tets

<%  
    out.println("Test SQL Connect Beginn!");   
    Class.forName("com.mysql.jdbc.Driver").newInstance();
    out.println("Class.forName(...) wurde aufgerufen!");
        
    java.sql.Connection conn = null;
    java.sql.Statement statement = null;
    ResultSet result = null;
    
    try {
    out.println("Vor der Connection.");
    conn = DriverManager.getConnection(
           "jdbc:mysql://10.48.30.13/dmercetest?user=dmerce&password=dm789dm");
    out.println("Connection wurde hergestellt.");
    statement = conn.createStatement();
    out.println("conn.createStatement");
    statement.execute("SELECT dk_name FROM dmerce_kunde");
    out.println("statement.execute");
    result = statement.getResultSet();
    out.println("statement.getResultSet.");
    } catch (Exception e) {
        e.printStackTrace();
    }
    finally {
        out.println("Connection wird geschlossen.");
        conn.close();
        out.println("Connection geschlossen.");
    }

%>

</body>
</html>
