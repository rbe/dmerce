#!/usr/bin/env python
##
#
# $Author: rb $
# $Revision: 1.2.7.1 $
#
# Revision 1.1  2000-05-11 15:04:06+02  rb
# Initial revision
#
##

#####################################################################
#
# O L D  A N D  O B S O L E T E
#
#####################################################################
# Errors
EUnknownError = 'An unknown error occured:'

EImportModule = 'Module MySQLdb cannot be located.'

EDatabaseGeneral = 'Error dealing with database:'
EDatabaseConnection = 'Error connecting database:'
EDatabaseCursor = 'Error getting database cursor:'
EDatabaseQuery = 'Error querying database:'
EDatabaseCreateTable = 'Error creating session table in database:'
EDatabaseDropTable = 'Error dropping session table in database:'

ESessionEnable = 'Error enabling session:'
ESessionStat = 'Error getting info about current session:'
ESessionRefresh = 'Error refreshing session:'
ESessionTimelimitCheck = 'Error checking timelimit of session:'
ESessionTimelimitInactiveExceeded = 'Session exceeds inactivity timelimit'
ESessionTimelimitMaximumExceeded = 'Session exceeds maximum timelimit'
ESessionDisable = 'Error disabling session:'
ESessionCheck = 'Error verifying session:'

ELogin = 'Error verfiying login:'

# Warnings
WUnknown = 'Warning:'
