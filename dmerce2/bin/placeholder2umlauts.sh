#!/bin/ksh

cat $1 | sed '
s/*/�/g
s/!/�/g
s/\$/�/g
s/#/�/g
s/(/�/g
s/)/�/g
s/\?/�/g
' > $1.sed
