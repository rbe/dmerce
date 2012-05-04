Tabellen-Struktur: wie bei Anwendungsbeispiel "simple" bei MySQL

Stored Procedure:

NEW_KUNDE (name_in IN VARCHAR2, vorname_in IN VARCHAR2, email_in IN VARCHAR2) AS
BEGIN
	INSERT INTO vkunde (kundennr, name, vorname, email) VALUES (VKUNDE_SEQ.nextval, name_in, vorname_in, email_in);
END;
