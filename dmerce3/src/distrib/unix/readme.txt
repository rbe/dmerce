
					1[wan]Ci dmerce(R) README
			Copyright (C) 2000-2004 1[wan]Ci GmbH
					http://www.1ci.com


I. dmerce
=========

0. Grundlagen, Systemanforderungen, Vorbereitung
------------------------------------------------

DMERCE_BASE ist der Bezeichner für das Verzeichnis, indem Sie dmerce
installiert haben. Beispiel: /opt/dmerce. Im Folgenden wird überall dort
dieser Bezeichner verwendet, wo das Installationsverzeichnis von dmerce
gemeint ist. Bitte verwenden Sie dort den entsprechenden Pfad auf Ihrem
System.


1. Setup von dmerce
-------------------

Nachdem Sie dmerce entpackt haben, müssen Sie die erste Konfiguration von
dmerce vornehmen. Editieren Sie die Datei DMERCE_BASE/etc/system.xml und setzen
Sie die Property "dmerce.base" auf das Verzeichniss, in dem Sie dmerce entpackt
haben:

	<property name="dmerce.base" value="/opt/dmerce"/>

Danach führen Sie das Setup aus:

dmerce:/opt/dmerce $ export DMERCE_BASE=/opt/dmerce
dmerce:/opt/dmerce $ . etc/dmercerc
dmerce:/opt/dmerce $ setup


2. Start von dmerce
-------------------

Rufen Sie aus dem Verzeichnis DMERCE_BASE/bin das Script startdmerce auf:

dmerce:/opt/dmerce $ startdmerce

Sobald dmerce gestartet ist, zeigt der Jboss-Server die Meldung

"[..] INFO  [Server] JBoss (MX MicroKernel)... Started in xxs:xxms"

erscheint.

Nun können Sie der mitgelieferten Dokumentation von dmerce näheres zum weiteren
Vorgehen entnehmen:
	
Dokumentation:

	lokal:  http://localhost:8080/dmerce-doc
	oder
	Online:	http://www.dmerce.de/dmerce-doc

Welcome-Applikation:

	lokal:  http://localhost:8080/welcome
	oder
	Online:	http://www.dmerce.de/welcome
	

3. Beenden von dmerce
---------------------

Rufen Sie aus dem Verzeichnis DMERCE_BASE/bin die Datei stopdmerce auf.
Alle Komponenten, die automatisch von dmerce gestoppt werden können, werden
angehalten.
