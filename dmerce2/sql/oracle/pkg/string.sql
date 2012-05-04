CREATE OR REPLACE FUNCTION stripaeiou (str IN VARCHAR2)
RETURN VARCHAR2
AS
        l       VARCHAR2(2000);
BEGIN
        l := str;
        l := REPLACE(l, 'a', '');
        l := REPLACE(l, 'e', '');
        l := REPLACE(l, 'i', '');
        l := REPLACE(l, 'o', '');
        l := REPLACE(l, 'u', '');
        RETURN l;
END;
/
