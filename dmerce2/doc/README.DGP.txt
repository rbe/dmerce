*************************************************************************
*									*
*		dmerce_DGP Benutzer Referenz (Stand 27.07.01)		*
*									*
*************************************************************************

Benoetigte module:

	main_lib.py
	cipher.py
	des.py
	des_lib.py
	idea.py
	idea_lib.py
	rijndael.py
	rijndael_lib.py

Die Kryptographischen Algorithmen sind als Klassen formuliert.
D.h., man instantiiert ein Objekt zum betreffenden Algorithmus
und fuehrt Ver- und Entschluesselung dann als Methoden dieses
Objektes aus.

Z.B. fuer den Algorithmus DES: 

	plain = '<klartext>'
	key = '<rohschluessel>'
	obj = des.DES()

	p = main_lib.String_Fillup(plain, obj.Blocklen())
	obj.NewData(d)

	obj.E_Key_Create(key)

	x = obj.ECB_Encrypt()
	cipher = obj.OutFormat(x)

Dabei ist plain der zu verschluesselnde Klartext als String,
key der Roh-Schluessel als eine ganze Zahl zwischen 0 und
der maximalen Schluessellaenge (die Daten der einzelnen Algorithmen s.u.)
und obj das DES-Objekt (im Standardbetrieb: weitere Optionen werden
weiter unten beschrieben).

Mit der Funktion String_Fillup aus dem Modul main_lib.py wird der
Klartext auf die volle Blocklaenge des Algorithmus gebracht und dann
mit der Methode NewData dem DES-Objekt als Eingabe zugefuehrt.

Die Methode E_Key_Create erzeugt aus dem Rohschluessel einen 
fertigen Rundenschluessel.

Die Methode ECB_Encrypt verschluesselt den Klartext, OutFormat
formatiert den resultierenden Chiffretext in einen String.

Die Entschluesselung verlaueft fast parallel:

	cipher = <chiffretext>
	key = <schluessel>

	obj = des.DES()
	obj.NewData(cipher)
	obj.D_Key_Create(key)
	y = obj.ECB_Decrypt()
	x = obj.OutFormat(y)
	plain = main_lib.String_Filldown(x)

mit dem Unterschied, dass der Chiffretext zunaechst entschluesselt,
dann formatiert und zum Schluss die Auffuellung rueckgaengig gemacht
wird. Das ergebnis ist der urspruengliche Klartext als String.


Die Algorithmen im einzelnen:
=============================

Block-, Wort-  und Schluessellaengen werden immer in Byte angegeben.
Es wird dringend empfohlen, defaults im Zweifelsfalle beizubehalten!

DES
---

Blocklaenge: 8

Schluessellaenge: 8

Rundenzahl: frei: n > 0		(default: 16)


IDEA
----

Blocklaenge: 8

Schluessellaenge: 16

Rundenzahl: frei: n > 0		(default: 8)


Rijndael/AES
------------

Blocklaenge: waehlbar aus: 4, 8, 12, 16, 20, 24, 28, 32
				(default: 16)

		Bemerkung: Strenggenommen sollten nur 16, 24, 32 benutzt
			werden. AES legt 16 fest.

Schluessellaenge: waehlbar aus: 16, 24, 32	(default: 16)

Rundenzahl: frei: n > 0		(default: 10)

		Bemerkung: Die geeignete Rundenzahl errechnet sich aus:

		max(Blocklaenge, Schluessellaenge) / 4  +  6


Chiffriermodi:
==============

ECB (Electronic Code Book)
--------------------------

Benutzung durch Aufruf der Ver-/Entschluesselungsmethode:

<kryptoobjekt>.ECB_Encrypt()

<kryptoobjekt>.ECB_Decrypt()


CBC (Cipher Block Chaining)
---------------------------

CBC benoetigt einen Initialisierungsvektor (IV). Dieser wird vor 
der Ver-/ Entschluesselung initialisiert:

<kryptoobjekt>.IVinit(<IV>)

Da ein IV einen zusaetzlichen Block darstellt, wird er als positive
ganze Zahl angegeben, wobei 0 <= IV < 2 ^ b
ist (b = blocklaenge).

Danach wird parallel zu ECB verfahren:

<kryptoobjekt>.CBC_Encrypt()

<kryptoobjekt>.CBC_Decrypt()

