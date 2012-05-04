
					1[wan]Ci dmerce(R) README
			Copyright (C) 2000-2004 1[wan]Ci GmbH
					http://www.1ci.com


I. dmerce
=========

0. Grundlagen, Systemanforderungen, Vorbereitung
------------------------------------------------

DMERCE_BASE ist der Bezeichner f�r das Verzeichnis, indem Sie dmerce
installiert haben. Beispiel: C:\dmerce. Im Folgenden wird �berall dort
dieser Bezeichner verwendet, wo das Installationsverzeichnis von dmerce
gemeint ist. Bitte verwenden Sie dort den entsprechenden Pfad auf Ihrem
System.


1. Setup von dmerce
-------------------

Nachdem Sie dmerce entpackt haben, m�ssen Sie die erste Konfiguration von
dmerce vornehmen. Editieren Sie die Datei etc/system.xml und setzen Sie
die Property "dmerce.base" auf das Verzeichniss, in dem Sie dmerce entpackt
haben:

	<property name="dmerce.base" value="C:/dmerce"/>

Danach f�hren Sie das Setup aus:

C:\>cd dmerce
C:\dmerce>cd bin
C:\dmerce\bin>set DMERCE_BASE=c:\dmerce
C:\dmerce\bin>setup


2. Start von dmerce
-------------------

Rufen Sie aus dem Verzeichnis DMERCE_BASE\bin die Datei startdmerce.bat auf.
Es werden sich Konsolenfenster �ffnen, in denen dmerce gestartet wird. Die Fenster
bleiben offen, solange dmerce l�uft. Beispiel:

C:\dmerce\bin>startdmerce

Sobald dmerce gestartet ist, zeigt der JBoss-Server die Meldung

"[..] INFO  [Server] JBoss (MX MicroKernel)... Started in xxs:xxms"

erscheint.

Nun k�nnen Sie der mitgelieferten Dokumentation von dmerce n�heres zum weiteren
Vorgehen entnehmen:

Welcome-Applikation:

	lokal:  http://localhost:8080/welcome
	oder
	Online:	http://www.dmerce.de/welcome
	
Dokumentation:

	lokal:  http://localhost:8080/dmerce-doc
	oder
	Online:	http://www.dmerce.de/dmerce-doc


3. Beenden von dmerce
---------------------

Rufen Sie aus dem Verzeichnis DMERCE_BASE\bin die Datei stopdmerce.bat auf.
Alle Komponenten, die automatisch von dmerce gestoppt werden k�nnen, werden
angehalten. Darunter sind derzeit nur MySQL und Oracle. Apache und JBoss
beenden Sie, indem Sie die zugeh�rigen DOS-Fenster schlie�en.

Empfohlene Reihenfolge f�r das Stoppen aller Komponenten:

- Beenden Sie die Webserver Apache und JBoss durch schlie�en der DOS-Fenster
- Rufen Sie stopdmerce.bat auf
