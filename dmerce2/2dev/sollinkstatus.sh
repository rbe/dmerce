#!/bin/sh
#
#
# luisla@essiprojects.com
# v.1.0. 2002, February.
#
#
# Link status and info of the QFE and HME interfaces, using ndd
# commands.
#
# Put this script in a scripts dir and change permission to let
# root execution. It's easy to personalize to a different number of
# interfaces.
#
#
# P.e. ndd -set /dev/hme link_speed 1
# ndd -get /dev/hme link_status   0 = link up, 1 = link down
# ndd -get /dev/hme link_speed    0 = 10MBit,  1 = 100MBit
# ndd -get /dev/hme link_mode     0 = half duplex, 1 = full duplex
# ndd -get /dev/hme adv_autoneg_cap   0 = no autonegotiation, 1 = autoneg.

SET=`ndd -set /dev/hme instance 1`
GET="ndd -get /dev/hme"

printf "HME1: "

case `$GET link_status` in
'0')    printf "KO.\t" ;;
'1')    printf "OK.\t" ;;
esac

case `$GET link_speed` in
'0')    printf "10Mb.\t" ;;
'1')    printf "100Mb.\t" ;;
esac

case `$GET link_mode` in
'0')    printf "Half Duplex.\t" ;;
'1')    printf "Full Duplex.\t" ;;
esac

case `$GET adv_autoneg_cap` in
'0')    echo "No autonegotiation." ;;
'1')    echo "Autonegotiation." ;;
esac

for IF in `echo 0 1 2 3`;
do

SET=`ndd -set /dev/qfe instance $IF`
GET="ndd -get /dev/qfe"

printf "QFE$IF: "

case `$GET link_status` in
'0')    echo "KO."
        continue ;;
'1')    printf "OK.\t" ;;
esac

case `$GET link_speed` in
'0')    printf "10Mb.\t" ;;
'1')    printf "100Mb.\t" ;;
esac

case `$GET link_mode` in
'0')    printf "Half Duplex.\t" ;;
'1')    printf "Full Duplex.\t" ;;
esac

case `$GET adv_autoneg_cap` in
'0')    echo "No autonegotiation." ;;
'1')    echo "Autonegotiation." ;;
esac

done
