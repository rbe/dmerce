# !/bin/sh
NCCDATA=/export/honey/opt/dmerce/NCC
CMDPATH=/usr/local/bin

# DELETING SECTION
echo "deleting old ns data on ns1"
$CMDPATH/ssh irb.ns1.1xsp.com "rm -rf /export/ns1.1xsp.com/agent-*"
echo
echo "deleting old ns data on hellfire.muenster1.de.1ci.net"
$CMDPATH/ssh 62.72.64.194 "rm -rf /var/isc-bind/agent-*"
echo
echo "deleting old ns data on tux.bensmann.com"
$CMDPATH/ssh tux.bensmann.com "rm -rf /var/isc-bind/agent-*"
echo
echo "deleting old ns data on bunny.frankfurt1.de.1ci.net"
$CMDPATH/ssh bunny.frankfurt1.de.1ci.net "rm -rf /export/ns4.1xsp.com/agent-*"
echo

# COPY SECTION
echo "copy to tux"
$CMDPATH/scp -r $NCCDATA/tux.bensmann.com/dns/* tux.bensmann.com:/var/isc-bind
echo
echo "copy to hellfire"
$CMDPATH/scp -r $NCCDATA/hellfire.muenster1.de.1ci.net/dns/* 62.72.64.194:/var/isc-bind
echo
echo "copy to bunny"
$CMDPATH/scp -r $NCCDATA/bunny.frankfurt1.de.1ci.net/dns/* bunny.frankfurt1.de.1ci.net:/export/ns4.1xsp.com
echo
echo "copy to ns1"
$CMDPATH/scp -r $NCCDATA/honey.hamburg2.de.1ci.net/dns/* irb.ns1.1xsp.com:/export/ns1.1xsp.com
echo

# RESTARTING SECTION
echo "restarting nameserver on ns1"
$CMDPATH/ssh irb.ns1.1xsp.com "/export/ns1.1xsp.com/named-ncc.sh restart"
echo "SLEEP 5 SECONDS"
sleep 5
echo
echo "restarting nameserver on hellfire"
$CMDPATH/ssh 62.72.64.194 "/var/isc-bind/named-ncc.sh restart"
echo
echo "restarting nameserver on tux"
$CMDPATH/ssh tux.bensmann.com "/var/isc-bind/named-ncc.sh restart"
echo
echo "restarting nameserver on bunny"
$CMDPATH/ssh bunny.frankfurt1.de.1ci.net "/export/ns4.1xsp.com/ns-restart.sh restart"
