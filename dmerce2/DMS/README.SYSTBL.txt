----------------------------------------
      SysTbl - dmerce System Tables
----------------------------------------

Usage:
------

1. In web projects:

	import DMS.SysTbl
	import Guardian.Config
        sysTbl = DMS.SysTbl.Retrieve('<server.domain.tld>', Guardian.Config.RFC822())
        sysTbl.Init()
        sqlSys = sysTbl.GetSQLSys()          # Query object to system database
        sqlData = sysTbl.GetSQLData()        # Query object to data database
        prjcfg = sysTbl.GetConfiguration()   # Project configuration data

	prjcfg holds a dictionary with the following information about
	a project:
	--------------------------------------------------------------
	ID	        int(11)
	FQHN		varchar(250)
	HostSerial	int(11)
	SQL		varchar(250)
	FileUploadDir	varchar(250)
	Debug		int(1)

	Example of a dmerce.cfg:
	------------------------
	[q]
	Sys = MySQL:<user>:<passwd>@<host>:<database name>

2. In scripts:

	import DMS.SysTbl
	import Guardian.Config
        sysTbl = DMS.SysTbl.Retrieve()
	sysTbl.SetConfig(Guardian.Config.RFC822())	# Set config instance
	sysTbl.SetFQHN('<server.domain.tld>')		# Set fqhn
	sysTbl.InitSQLSysConn()				# Init connection to sql sys db
	sysTbl.SetSysConfig()				# retrieve and set system config
	sysTbl.InitSQLDataConn()			# Init connection to sql data db
        sqlSys = sysTbl.GetSQLSys()			# Query object to system database
        sqlData = sysTbl.GetSQLData()			# Query object to data database
        prjcfg = sysTbl.GetConfiguration()		# Get project configuration data
