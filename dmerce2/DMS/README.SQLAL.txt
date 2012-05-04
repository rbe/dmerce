----------------------------------------
    How to use the dmerce SQLAL
----------------------------------------

1. Create connection:

   dmerce <= 2.2.1:

       import DMS.SQL
       dbapi = DMS.SQL.DBAPI('database type:user:password@host:database name')
       query = DMS.SQL.Query(dbapi)

   dmerce >= 2.2.2:

       import DMS.SQL
       al = DMS.SQL.Layer1('database type:user:password@host:database name')
       al.Init()
       query = al.GetQuery()

   Submit a query and receive results:

       rc, r = query['SELECT haudrauf FROM SvenBergmann WHERE stelle = "tut-ganz-doll-weh"']

       rc = Result Count
       r  = Result [{}]; List elements = result rows, dictionary = field name/value pairs

    Thats it!

2. Unique IDs in tables:

       d = DMS.SQL.DBOID(<sql conn sys db>, <sys conn data db>)
       newid = d[<table>]
