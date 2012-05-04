#!/usr/bin/env python
##
#
# $Author: rb $
# $Revision: 1.2.7.1 $
#
# Revision 1.1  2000-07-12 17:12:43+02  rb
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
ERR['1111'] = 'Zeitpunkte nicht korrekt'

# Stage 1 System 2: Database
#   1 = Connection
ERR['1211'] = 'Kann keine Verbindung zum Datenbanserver aufbauen'
ERR['1212'] = 'Kann keinen Cursor bekommen'

#   2 = Query
ERR['1221'] = 'Fehler beim Abfragen der Datenbank'
ERR['1222'] = 'Fehler beim Anlegen einer Tabelle'
ERR['1223'] = 'Fehler beim Löschen einer Tabelle'
ERR['1224'] = 'Doppelter Eintrag gefunden!'
ERR['1225'] = 'Fehler beim Anlegen eines Datensatzes in der Datenbank'
ERR['1226'] = 'Fehler beim Ändern eines Datensatzes in der Datenbank'
ERR['1227'] = 'Fehler beim Löschen eines Datensatzes in der Datenbank'

#   3 = General
ERR['1231'] = 'Genereller Fehler beim Umgang mit der Datenbank'

# Stage 1 System 3: SAM
#   1 = Enable/Disable
ERR['1311'] = 'Kann Session nicht anlegen'
ERR['1312'] = 'Kann Session nicht deaktivieren'

#   2 = Checking/Query
ERR['1321'] = 'Kann keine Informationen über die Session bekommen'
ERR['1322'] = 'Kann Session nicht erneuern'
ERR['1323'] = 'Fehler beim Überprüfen des Zeitlimits'
ERR['1324'] = 'Session überschreitet inaktives Zeitlimit'
ERR['1325'] = 'Session überschreitet maximales Zeitlimit'
ERR['1326'] = 'Fehler beim Überprüfen der Session'
ERR['1327'] = 'Zugriffverletzung; Darf nicht per Web aufgerufen werden'

#####################################################################
# Stage 2: Errors with abstraction layer
# Stage 2 System 1: SQLAL

#####################################################################
# Stage 3: Errors with products/modules

# Stage 3: System 1: Internal modules

# Stage 3: System 2: External modules

# Stage 3: System 3: Login
#   1 = WRONG DATA
ERR['3311'] = 'Login und/oder Passwort falsch'
ERR['3312'] = 'Login falsch'

#   2 = CAN'T CHECK
ERR['3321'] = 'Kann Login nicht überprüfen'

# Stage 3: System 4: Shop
#   1 = Article: INVALID OR INSUFFICIENT DATA
ERR['3411'] = 'Artikel nicht im Warenkorb'
ERR['3412'] = 'Menge kann nicht gesetzt werden'

#   2 = Basket: INVALID OR INSUFFICIENT DATA
ERR['3421'] = 'Ihr Warenkorb ist leer'
ERR['3422'] = 'Bitte geben Sie bei einer Bestellung den Namen des Bestellers an!'

#   3 = Basket: ACCESS VIOLATIONS
ERR['3431'] = 'Zugriff verweigert: Warenkorb gesperrt!'

# Stage 3: System 5: Auction
#   1 = Auction: INVALID OR INSUFFICIENT DATA
ERR['3511'] = 'Es wurde zu wenig geboten!'
ERR['3512'] = 'Menge ist nicht im Rahmen der Mindestmenge oder der angebotenen Menge!'
ERR['3513'] = 'Ihr Gebot muss mindestens um den Wert 0.1 geringer sein!'
ERR['3514'] = 'Es wurde versucht, zuviel anzubieten!'
ERR['3515'] = 'Es liegen schon Gebote vor!'

#   2 = Auction, m2-problem: INVALID OR INSUFFICIENT DATA
ERR['3521'] = 'Sie müssen die Felder Breite und Höhe angeben!'

#####################################################################
# Stage 4: Errors with template processor
# Stage 4: System 1: Trigger
#   1 = ACCESS DENIED
ERR['4111'] = 'Trigger hat keinen Zugriff auf die Tabelle'

#####################################################################
# Stage 5: Errors with server

#####################################################################
# Stage 6: Errors with client
