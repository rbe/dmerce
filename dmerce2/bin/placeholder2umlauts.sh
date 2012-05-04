#!/bin/ksh

cat $1 | sed '
s/*/ß/g
s/!/ü/g
s/\$/ö/g
s/#/ä/g
s/(/Ü/g
s/)/Ö/g
s/\?/Ä/g
' > $1.sed
