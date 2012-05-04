#!/usr/bin/env python
##
#
# $Author: rb $
# $Revision: 2.7 $
#
# Revision 1.1  2000-07-12 15:58:16+02  rb
# Initial revision
#
##

class ErrMsg:

    """
    return messages for error numbers
    
    E R R O R S
    Format: E<STAGE><SYSTEM><SUBSYSTEM><NUMBER>
    
    """

    def __init__(self, lang = None):
        if lang in ['de', 'en', 'es', 'fr', 'it']:
            self.__lang = lang
        else:
            self.__lang = 'en'
        self.__ERR = {
            'en': {'0' : '',
                   '1' : 'System'},
            'es': {'0' : '',
                   '1' : 'System'},
            'de': {'0' : '',
                   '1' : 'System'},
            'fr': {'0' : '',
                   '1' : 'System'},
            'it': {'0' : '',
                   '1' : 'System'}
            }
        self.InitStage1()
        #self.InitStage2()
        self.InitStage3()
        self.InitStage4()
        #self.InitStage5()
        #self.InitStage6()

    def Get(self, no):
        """ return error message """
        try:
            em = self.__ERR[self.__lang][str(no)]
        except:
            em = ''
        return em

    def Set(self, no, lang, msg):
        """ set an error message """
        self.__ERR[str(no)][lang] = msg

    def InitStage1(self):
        """
        stage 1: Errors with operating system, database
        """
        #self.__ERR['en']['1064'] = 'SQL Syntax Error'
        # Stage 1 System 1: Operating System
        self.__ERR['en']['1111'] = 'Timestamps not correct'
        self.__ERR['de']['1111'] = 'Zeitpunkte nicht korrekt'
        self.__ERR['es']['1111'] = 'La fecha no es la correcta'
        self.__ERR['fr']['1111'] = 'Intervalle de temps incorrect'
        self.__ERR['it']['1111'] = 'Date incorrette'
        # Stage 1 System 2: Database
        #   1 = Connection
        self.__ERR['en']['1211'] = 'Can\'t connect to database'
        self.__ERR['de']['1211'] = 'Kann keine Verbindung zum Datenbanserver aufbauen'
        self.__ERR['es']['1211'] = 'No se puede conectar con la base de datos'
        self.__ERR['fr']['1211'] = 'Impossible de se connecter à la base de données'
        self.__ERR['it']['1211'] = 'Impossibile stabilire una connessione con il server banca di dati'
        self.__ERR['en']['1212'] = 'Can\'t get cursor'
        self.__ERR['de']['1212'] = 'Kann keinen Cursor bekommen'
        self.__ERR['es']['1212'] = 'No se puede accionar el cursor'
        self.__ERR['fr']['1213'] = 'Impossible d\'utiliser le curseur'
        self.__ERR['it']['1213'] = 'Il cursore sparisce'
        #   2 = Query
        self.__ERR['en']['1221'] = 'Error querying database'
        self.__ERR['de']['1221'] = 'Fehler beim Abfragen der Datenbank'
        self.__ERR['es']['1221'] = 'Error al consultar la base de datos'
        self.__ERR['fr']['1221'] = 'Erreur de requête dans la base de données'
        self.__ERR['it']['1221'] = 'Errore d\'interrogazione della banca dati'
        self.__ERR['en']['1222'] = 'Error creating table in database'
        self.__ERR['de']['1222'] = 'Fehler beim Anlegen einer Tabelle'
        self.__ERR['es']['1222'] = 'Error al crear una tabla en la base de datos'
        self.__ERR['fr']['1222'] = 'Erreur de création de table dans la base de données'
        self.__ERR['it']['1222'] = 'Errore nella creazione di una tabella'
        self.__ERR['en']['1223'] = 'Error dropping table in database'
        self.__ERR['de']['1223'] = 'Fehler beim Löschen einer Tabelle'
        self.__ERR['es']['1223'] = 'Errores al eliminar una tabla en la base de datos'
        self.__ERR['fr']['1223'] = 'Erreur d\'effacement de table dans la base de données'
        self.__ERR['it']['1223'] = 'Errore nella cancellazione di una tabella'
        self.__ERR['en']['1224'] = 'Duplicate entry in table'
        self.__ERR['de']['1224'] = 'Doppelter Eintrag gefunden!'
        self.__ERR['es']['1224'] = 'Encontró un ingreso doble!'
        self.__ERR['fr']['1224'] = 'Doublon dans la table'
        self.__ERR['it']['1224'] = 'Trovata una registrazione doppia!'
        self.__ERR['en']['1225'] = 'Error creating entry in table'
        self.__ERR['de']['1225'] = 'Fehler beim Anlegen eines Datensatzes in der Datenbank'
        self.__ERR['es']['1225'] = 'Error al ingresar información en la base de datos'
        self.__ERR['fr']['1225'] = 'Erreur de création d\'entrée dans le tableau'
        self.__ERR['it']['1225'] = 'Errore nella creazione di un record nella banca dati'
        self.__ERR['en']['1226'] = 'Error updating table'
        self.__ERR['de']['1226'] = 'Fehler beim Ändern eines Datensatzes in der Datenbank'
        self.__ERR['es']['1226'] = 'Error al modificar información en la base de datos'
        self.__ERR['fr']['1226'] = 'Erreur de mise à jour du tableau'
        self.__ERR['it']['1226'] = 'Errore nella modifica di un record nella banca dati'
        self.__ERR['en']['1227'] = 'Error deleting entry in table'
        self.__ERR['de']['1227'] = 'Fehler beim Löschen eines Datensatzes in der Datenbank'
        self.__ERR['es']['1227'] = 'Error al eliminar información en la base datos'
        self.__ERR['fr']['1227'] = 'Erreur de suppression d\'entrée dans le tableau'
        self.__ERR['it']['1227'] = 'Errore nella cancellazione di un record nella banca dati'
        #   3 = General
        self.__ERR['en']['1231'] = 'General error dealing with database'
        self.__ERR['de']['1231'] = 'Genereller Fehler beim Umgang mit der Datenbank'
        self.__ERR['es']['1231'] = 'Error al trabajar con la base de datos'
        self.__ERR['fr']['1231'] = 'Erreur générale d\'utilisation de la base de données'
        self.__ERR['it']['1231'] = 'Errore generale di gestione della banca dati'
        # Stage 1 System 3: SAM
        #   1 = Enable/Disable
        self.__ERR['en']['1311'] = 'Error creating session'
        self.__ERR['de']['1311'] = 'Kann Session nicht anlegen'
        self.__ERR['es']['1311'] = 'No se puede lograr la conexión'
        self.__ERR['fr']['1311'] = 'Erreur de création de session'
        self.__ERR['it']['1311'] = 'Impossibile iniziare una sessione'
        self.__ERR['en']['1312'] = 'Error disabling session'
        self.__ERR['de']['1312'] = 'Kann Session nicht deaktivieren'
        self.__ERR['es']['1312'] = 'No se puede desactivar la conexión'
        self.__ERR['fr']['1312'] = 'Erreur de déconnexion de session'
        self.__ERR['it']['1312'] = 'Impossibile chiudere una sessione'
        #   2 = Checking/Query
        self.__ERR['en']['1321'] = 'Error getting info about current session'
        self.__ERR['de']['1321'] = 'Kann keine Informationen über die Session bekommen'
        self.__ERR['es']['1321'] = 'No se puede obtener información sobre la conexión actual'
        self.__ERR['fr']['1321'] = 'Erreur de récupération d\'informations dans la session en cours'
        self.__ERR['it']['1321'] = 'Non apparisce nessun informazione sulla sessione'
        self.__ERR['en']['1322'] = 'Error refreshing session'
        self.__ERR['de']['1322'] = 'Kann Session nicht erneuern'
        self.__ERR['es']['1322'] = 'No se puede restablecer la conexión'
        self.__ERR['fr']['1322'] = 'Erreur d\'actualisation de la session'
        self.__ERR['it']['1322'] = 'Impossibile rinnovare la sessione'
        self.__ERR['en']['1323'] = 'Error checking timelimit of session'
        self.__ERR['de']['1323'] = 'Fehler beim Überprüfen des Zeitlimits'
        self.__ERR['es']['1323'] = 'La conexión excede el límite de tiempo permitido'
        self.__ERR['fr']['1323'] = 'Erreur de vérification de la durée maximum de la session'
        self.__ERR['it']['1323'] = 'Errore nella verifica del limite di tempo'
        self.__ERR['en']['1324'] = 'Session exceeds inactivity timelimit'
        self.__ERR['de']['1324'] = 'Session überschreitet inaktives Zeitlimit'
        self.__ERR['es']['1324'] = 'La conexión  sobrepasa el límite de tiempo de inactividad permitido'
        self.__ERR['fr']['1324'] = 'La session a dépassé la durée maximum d\'inactivité'
        self.__ERR['it']['1324'] = 'La sessione ha superato il limite di tempo inattivo'
        self.__ERR['en']['1325'] = 'Session exceeds maximum timelimit'
        self.__ERR['de']['1325'] = 'Session überschreitet maximales Zeitlimit'
        self.__ERR['es']['1325'] = 'La conexión excede el límite de tiempo máximo permitido'
        self.__ERR['fr']['1325'] = 'La session a dépassé sa durée maximum'
        self.__ERR['it']['1325'] = 'La sessione ha superato il limite di tempo massimo'
        self.__ERR['en']['1326'] = 'Error verifying session'
        self.__ERR['de']['1326'] = 'Fehler beim Überprüfen der Session'
        self.__ERR['es']['1326'] = 'Error al momento de revisar la conexión'
        self.__ERR['fr']['1326'] = 'Erreur de contrôle de session'
        self.__ERR['it']['1326'] = 'Errore nella verifica della sessione'
        self.__ERR['en']['1327'] = 'Access violation: Can not be called from the web'
        self.__ERR['de']['1327'] = 'Zugriffverletzung; Darf nicht per Web aufgerufen werden'
        self.__ERR['es']['1327'] = 'Violación de acceso: No puede recuperarse de la Web'
        self.__ERR['fr']['1327'] = 'Violation d\'accès : Ne peut être appelé depuis Internet'
        self.__ERR['it']['1327'] = 'Violazione d\'accesso; impossibile chiamare tramite la Web'

    def InitStage2(self):
        """
        stage 2: Errors with abstraction layer
        stage 2 System 1: SQLAL
        """
        pass

    def InitStage3(self):
        """
        stage 3: Errors with products/modules
        stage 3: System 1: Internal modules
        stage 3: System 2: External modules
        stage 3: System 3: Login
        """
        #   1 = WRONG DATA
        self.__ERR['en']['3311'] = 'Login and/or password not correct'
        self.__ERR['de']['3311'] = 'Login und/oder Passwort falsch'
        self.__ERR['es']['3311'] = 'Nombre de usuario y/o contraseña incorrecta'
        self.__ERR['fr']['3311'] = 'Identifiant et/ou mot de passe incorrect'
        self.__ERR['it']['3311'] = 'Login e/o passwort falsi'
        self.__ERR['en']['3312'] = 'Login incorrect'
        self.__ERR['de']['3312'] = 'Login falsch'
        self.__ERR['es']['3312'] = 'Nombre de usuario incorrecto'
        self.__ERR['fr']['3312'] = 'Identifiant incorrect'
        self.__ERR['it']['3312'] = 'Login falso'
        self.__ERR['en']['3313'] = 'Wrong stage for creating a new client'
        self.__ERR['de']['3313'] = 'Falsche Stufe um einen neuen Mandanten anzulegen'
        self.__ERR['es']['3313'] = ''
        self.__ERR['fr']['3313'] = ''
        self.__ERR['it']['3313'] = ''
        #   2 = CAN'T CHECK
        self.__ERR['en']['3321'] = 'Can not check login'
        self.__ERR['de']['3321'] = 'Kann Login nicht überprüfen'
        self.__ERR['es']['3321'] = 'No se puede comprobar el nombre del usuario'
        self.__ERR['fr']['3321'] = 'Impossible de vérifier l\'identifiant'
        self.__ERR['it']['3321'] = 'Verifica login impossibile'
        # Stage 3: System 4: Shop
        #   1 = Article: INVALID OR INSUFFICIENT DATA
        self.__ERR['en']['3411'] = 'Article is not in the basket!'
        self.__ERR['de']['3411'] = 'Artikel nicht im Warenkorb'
        self.__ERR['es']['3411'] = 'El producto no se encuentra en los artículos ofrecidos'
        self.__ERR['fr']['3411'] = 'Cet article ne figure pas dans le panier !'
        self.__ERR['it']['3411'] = 'Articolo assente nel paniere merci'
        self.__ERR['en']['3412'] = 'Can not set quantity of article'
        self.__ERR['de']['3412'] = 'Menge eines Artikels kann nicht gesetzt werden'
        self.__ERR['es']['3412'] = 'No se puede colocar la cantidad requerida de un producto'
        self.__ERR['fr']['3412'] = 'Impossible de définir la quantité de l\'article'
        self.__ERR['it']['3412'] = 'Impossibile stabilire la quantità dell\'articolo'
        #   2 = Basket: INVALID OR INSUFFICIENT DATA
        self.__ERR['en']['3421'] = 'Your basket is empty!'
        self.__ERR['de']['3421'] = 'Ihr Warenkorb ist leer'
        self.__ERR['es']['3421'] = 'No hay artículos para ofrecer'
        self.__ERR['fr']['3421'] = 'Votre panier est vide !'
        self.__ERR['it']['3421'] = 'Il Suo paniere è vuoto'
        self.__ERR['en']['3422'] = 'Please submit your name in your order!'
        self.__ERR['de']['3422'] = 'Bitte geben Sie bei einer Bestellung den Namen des Bestellers an!'
        self.__ERR['es']['3422'] = 'Por favor cuando haga un pedido indicar el nombre del solicitante'
        self.__ERR['fr']['3422'] = 'Veuillez indiquer votre nom sur la commande !'
        self.__ERR['it']['3422'] = 'Si prega d\'indicare nell\'ordinazione il nome di'
        #   3 = Basket: ACCESS VIOLATION
        self.__ERR['en']['3431'] = 'Access denied: your basket is closed'
        self.__ERR['de']['3431'] = 'Zugriff verweigert: Warenkorb gesperrt!'
        self.__ERR['es']['3431'] = 'Acceso denegado: ¡se ha cerrado la oferta de mercadería!'
        self.__ERR['fr']['3431'] = 'Accès refusé : votre panier est fermé'
        self.__ERR['it']['3431'] = 'Accesso negato: Il paniere è chiuso'
        # Stage 3: System 5: Auction
        #   1 = Auction: INVALID DATA
        self.__ERR['de']['3510'] = 'Unzureichende Informationen zum Anlegen einer Auktion'
        self.__ERR['en']['3510'] = 'Insufficent informations for creating an auction'
        self.__ERR['es']['3510'] = 'Insuficiente información para establecer una subasta'
        self.__ERR['fr']['3510'] = 'Informations insuffisantes pour créer une enchère'
        self.__ERR['it']['3510'] = 'Informazioni insufficienti per creare un\'offerta di gara'
        self.__ERR['en']['3511'] = 'Your bid is too low'
        self.__ERR['de']['3511'] = 'Es wurde zu wenig geboten!'
        self.__ERR['es']['3511'] = '¡La oferta es muy baja!'
        self.__ERR['fr']['3511'] = 'La proposition est trop basse'
        self.__ERR['it']['3511'] = 'Offerta troppo bassa!'
        self.__ERR['en']['3512'] = 'Quantity is not between minimal and offered quantity'
        self.__ERR['de']['3512'] = 'Menge ist nicht im Rahmen der Mindestmenge oder der angebotenen Menge!'
        self.__ERR['es']['3512'] = 'La  oferta  no está dentro del margen de la cantidad mínima y la ofrecida'
        self.__ERR['fr']['3512'] = 'La quantité n\'est pas comprise entre la quantité minimale et celle offerte'
        self.__ERR['it']['3512'] = 'La quantità non entra nel quadro della quantità minima e quella offerta'
        self.__ERR['en']['3513'] = 'Your bid must be less than the value of 0.1'
        self.__ERR['de']['3513'] = 'Ihr Gebot muss mindestens um den Wert 0.1 geringer sein!'
        self.__ERR['es']['3513'] = '¡Por le menos, su oferta debe ser inferior de un valor de  0.1!'
        self.__ERR['fr']['3513'] = 'Votre proposition doit être inférieure au moins d\'une valeur de 0,1'
        self.__ERR['it']['3513'] = 'La Sua offerta deve essere per lo meno più bassa dal valore 0.1'
        self.__ERR['en']['3514'] = 'You bid too much'
        self.__ERR['de']['3514'] = 'Es wurde versucht, zuviel anzubieten!'
        self.__ERR['es']['3514'] = '¡Su oferta fue demasiada alta!'
        self.__ERR['fr']['3514'] = 'Votre proposition est trop élevée'
        self.__ERR['it']['3514'] = 'Lei ha provato di proporre un\'offerta troppo alta!'
        self.__ERR['en']['3515'] = 'There are already bids!'
        self.__ERR['de']['3515'] = 'Es liegen schon Gebote vor!'
        self.__ERR['es']['3515'] = '¡Ya existen ofertas!'
        self.__ERR['fr']['3515'] = 'Il y a d\'autres propositions !'
        self.__ERR['it']['3515'] = 'Già ci sono offerte!'
        #   2 = Auction, m2-problem: INVALID OR INSUFFICIENT DATA
        self.__ERR['en']['3521'] = 'You have to submit height and width!'
        self.__ERR['de']['3521'] = 'Sie müssen die Felder Breite und Höhe angeben!'
        self.__ERR['es']['3521'] = '¡Debe proporcionar el ancho y altura!'
        self.__ERR['fr']['3521'] = 'Vous devez préciser la hauteur et la largeur !'
        self.__ERR['it']['3521'] = 'Lei deve indicare i valori per la larghezza e l\'altezza!'

    def InitStage4(self):
        """
        stage 4: Errors with template processor
        """
        #   1 =  ACCESS DENIED
        self.__ERR['en']['4111'] = 'Trigger has no access to that table!'
        self.__ERR['de']['4111'] = 'Trigger hat keinen Zugriff auf die Tabelle'
        self.__ERR['es']['4111'] = 'No se puede acceder a esa tabla'
        self.__ERR['fr']['4111'] = 'L\'amorce n\'a pas accès à cette table !'
        self.__ERR['it']['4111'] = 'Il trigger non ha accesso alla tabella'

    def InitStage5(self):
        """
        stage 5: Errors with server
        """
        pass

    def InitStage6(self):
        """
        stage 6: Errors with client
        """
        pass
