#!/bin/sh

dmerceHome=/export/honey/opt/dmerce

cp $dmerceHome/NCC/honey.hamburg2.de.1ci.net/mail/virtusertable /etc/mail
cp $dmerceHome/NCC/honey.hamburg2.de.1ci.net/mail/access /etc/mail
cp $dmerceHome/NCC/honey.hamburg2.de.1ci.net/mail/local-host-names /etc/mail
sleep 2
chown root:other /etc/mail/virtusertable /etc/mail/access /etc/mail/local-host-names
chmod u=rw,go=r /etc/mail/virtusertable /etc/mail/access /etc/mail/local-host-names
sleep 2
makemap dbm /etc/mail/virtusertable < /etc/mail/virtusertable
makemap hash /etc/mail/access < /etc/mail/access
sleep 2

pkill sendmail
/usr/lib/sendmail -bd -q15m
