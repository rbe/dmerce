-- #############################################################################################
--
-- %Purpose: Send E-Mail Messages from PL/SQL with Oracle 8.1.6 using UTL_TCP or UTL_SMTP
--
-- Notes:    From Oracle8i release 8.1.6 one can send e-mail messages
--           directly from PL/SQL using either the UTL_TCP or UTL_SMTP
--           packages. No pipes or external procedures required.
--
--           The UTL_TCP is a TPC/IP package that provides PL/SQL procedures
--           to support simple TCP/IP-based communications between servers and the
--           outside world. It is used by the SMTP package, to implement Oracle server-based
--           clients for the internet email protocol.
--
--           This package requires that you install the JServer option
--
-- Author:   Frank Naude (frank@ibi.co.za)
--
-- #############################################################################################
--
CREATE OR REPLACE PROCEDURE send_mail (
  msg_from    varchar2 := 'martin.zahn@akadia.com',    -- Must be a vaild E-Mail address !
  msg_to      varchar2 := 'martin.zahn@akadia.com',    -- Must be a vaild E-Mail address !
  msg_subject varchar2 := 'Message from PL/SQL daemon',
  msg_text    varchar2 := 'This Message was automatically send by PL/SQL daemon' )
IS
  conn          utl_tcp.connection;
  rc            integer;
  mailhost      varchar2(30) := 'rabbit.akadia.com';
BEGIN
  conn := utl_tcp.open_connection(mailhost,25);        -- open the SMTP port
  dbms_output.put_line(utl_tcp.get_line(conn, TRUE));
  rc := utl_tcp.write_line(conn, 'HELO '||mailhost);
  dbms_output.put_line(utl_tcp.get_line(conn, TRUE));
  rc := utl_tcp.write_line(conn, 'MAIL FROM: '||msg_from);
  dbms_output.put_line(utl_tcp.get_line(conn, TRUE));
  rc := utl_tcp.write_line(conn, 'RCPT TO: '||msg_to);
  dbms_output.put_line(utl_tcp.get_line(conn, TRUE));
  rc := utl_tcp.write_line(conn, 'DATA');               -- Start message body
  dbms_output.put_line(utl_tcp.get_line(conn, TRUE));
  rc := utl_tcp.write_line(conn, 'Subject: '||msg_subject);
  rc := utl_tcp.write_line(conn, '');
  rc := utl_tcp.write_line(conn, msg_text);
  rc := utl_tcp.write_line(conn, '.');                  -- End of message body with a "."
  dbms_output.put_line(utl_tcp.get_line(conn, TRUE));
  rc := utl_tcp.write_line(conn, 'QUIT');
  dbms_output.put_line(utl_tcp.get_line(conn, TRUE));
  utl_tcp.close_connection(conn);                       -- Close the connection
EXCEPTION
  when others then
       raise_application_error(-20000,
       'Unable to send E-mail message from pl/sql procedure');
END;
/
show errors

--  Examples:
set serveroutput on

exec send_mail();
exec send_mail(msg_to  =>'martin.zahn@akadia.com');
exec send_mail(msg_to  =>'martin.zahn@akadia.com',
               msg_text=>'How to send E-Mail from PL/SQL');


CREATE OR REPLACE PROCEDURE send_mail2 (
                      sender    IN VARCHAR2,
                      recipient IN VARCHAR2,
                      message   IN VARCHAR2)
IS
    mailhost   VARCHAR2(30) := 'rabbit.akadia.com';
    smtp_error  EXCEPTION;
    mail_conn   utl_tcp.connection;
    PROCEDURE smtp_command(command IN VARCHAR2,
                           ok      IN VARCHAR2 DEFAULT '250')
    IS
        response varchar2(3);
        rc integer;
    BEGIN
        rc := utl_tcp.write_line(mail_conn, command);
        response := substr(utl_tcp.get_line(mail_conn), 1, 3);
        IF (response <> ok) THEN
            RAISE smtp_error;
        END IF;
    END;

BEGIN
    mail_conn := utl_tcp.open_connection(mailhost, 25);
    smtp_command('HELO ' || mailhost);
    smtp_command('MAIL FROM: ' || sender);
    smtp_command('RCPT TO: ' || recipient);
    smtp_command('DATA', '354');
    smtp_command(message);
    smtp_command('QUIT', '221');
    utl_tcp.close_connection(mail_conn);
EXCEPTION
    WHEN OTHERS THEN
       raise_application_error(-20000,
       'Unable to send e-mail message from pl/sql');
END;
/

exec send_mail2('martin.zahn@akadia.com','martin.zahn@akadia.com','Test');

CREATE OR REPLACE PROCEDURE send_mail3 (sender    IN VARCHAR2,
                     recipient IN VARCHAR2,
                     message   IN VARCHAR2)
IS
    mailhost    VARCHAR2(30) := 'rabbit.akadia.com';
    mail_conn  utl_smtp.connection;

BEGIN
    mail_conn := utl_smtp.open_connection(mailhost, 25);
    utl_smtp.helo(mail_conn, mailhost);
    utl_smtp.mail(mail_conn, sender);
    utl_smtp.rcpt(mail_conn, recipient);
    utl_smtp.data(mail_conn, message);
    utl_smtp.quit(mail_conn);
EXCEPTION
    WHEN OTHERS THEN
       raise_application_error(-20000,
       'Unable to send e-mail message from pl/sql');
END;
/

exec send_mail3('martin.zahn@akadia.com','martin.zahn@akadia.com','Subject: Test');

