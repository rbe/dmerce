#!/usr/bin/env python
##
#
# $Author: rb $
# $Revision: 1.2.7.1 $
#
# Revision 1.1  2000-07-12 15:58:16+02  rb
# Initial revision
#
##

#####################################################################
#
# E R R O R S
#
# Format: E<STAGE><SYSTEM><SUBSYSTEM><NUMBER>
#
#####################################################################

ERR = {}

#####################################################################
# Stage 1: Errors with operating system, database

# Stage 1 System 1: Operating System
ERR['1111'] = 'Timestamps are not correct'

# Stage 1 System 2: Database
#   1 = Connection
ERR['1211'] = 'Can\'t connect database'
ERR['1212'] = 'Can\'t get cursor'

#   2 = Query
ERR['1221'] = 'Error querying database'
ERR['1222'] = 'Error creating table in database'
ERR['1223'] = 'Error dropping table in database'
ERR['1224'] = 'Duplicate entry in table'
ERR['1225'] = 'Error creating entry in table'
ERR['1226'] = 'Error updating table'
ERR['1227'] = 'Error deleting entry in table'

#   3 = General
ERR['1231'] = 'General error dealing with database'

# Stage 1 System 3: SAM
#   1 = Enable/Disable
ERR['1311'] = 'Error creating session'
ERR['1312'] = 'Error disabling session'

#   2 = Checking/Query
ERR['1321'] = 'Error getting info about current session'
ERR['1322'] = 'Error refreshing session'
ERR['1323'] = 'Error checking timelimit of session'
ERR['1324'] = 'Session exceeds inactivity timelimit'
ERR['1325'] = 'Session exceeds maximum timelimit'
ERR['1326'] = 'Error verifying session'
ERR['1327'] = 'Access violation: Can not be called from the web'

#####################################################################
# Stage 2: Errors with abstraction layer
# Stage 2 System 1: SQLAL

#####################################################################
# Stage 3: Errors with products/modules

# Stage 3: System 1: Internal modules

# Stage 3: System 2: External modules

# Stage 3: System 3: Login
#   1 = WRONG DATA
ERR['3311'] = 'Login and/or password not correct'
ERR['3312'] = 'Login incorrect'

#   2 = CAN'T CHECK
ERR['3321'] = 'Can not check login'

# Stage 3: System 4: Shop
#   1 = Article: INVALID OR INSUFFICIENT DATA
ERR['3411'] = 'Article is not in the basket!'
ERR['3412'] = 'Can not set quantity of article'

#   2 = Basket: INVALID OR INSUFFICIENT DATA
ERR['3421'] = 'Your basket is empty!'
ERR['3422'] = 'Please submit your name in your order!'

#   3 = Basket: ACCESS VIOLATION
ERR['3431'] = 'Access denied: your basket is closed'

# Stage 3: System 5: Auction
#   1 = Auction: INVALID DATA
ERR['3511'] = 'Your bid is too low'
ERR['3512'] = 'Quantity is not between minimal and offered quantity'
ERR['3513'] = 'Your bid must be less than the value of 0.1'
ERR['3514'] = 'You bid too much'
ERR['3515'] = 'There are already bids!'

#   2 = Auction, m2-problem: INVALID OR INSUFFICIENT DATA
ERR['3521'] = 'You have to submit height and width!'

#####################################################################
# Stage 4: Errors with template processor
#   1 =  ACCESS DENIED
ERR['4111'] = 'Trigger has no access to that table!'

#####################################################################
# Stage 5: Errors with server

#####################################################################
# Stage 6: Errors with client
