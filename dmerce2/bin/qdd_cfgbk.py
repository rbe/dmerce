#!/usr/bin/env python

import sys
import string
import os
import os.path
import commands
import getopt

print """
1Ci(R) GmbH, http://www.1ci.de, Copyright 2000-2002
"""

arguments = sys.argv[1:]

optstring, args = getopt.getopt(arguments, 'c:e:s:w:')

option = {}
for i in range(len(optstring)):
    option[optstring[i][0]] = optstring[i][1]
del optstring

#Option c -> Configfile
if option.has_key('-c'):
    datei = open(option['-c'],'r')
    zeilen = datei.readlines()
else:
    zeilen = []


#Option w -> Working Directory
if option.has_key('-w'):
    workdir=os.path.abspath(option['-w'])
    logdir=workdir+'/logs.'+os.uname()[1]
else:
    workdir=os.environ['HOME']
    logdir=os.environ['HOME']+'/logs.'+os.uname()[1]

if not os.path.exists(workdir):
    print 'No such directory:',workdir
    sys.exit()
else:
    os.chdir(workdir)

#Option e -> Extension z.B.: conf
if option.has_key('-e') and option.has_key('-s'):
    cfgfiles = string.split(commands.getstatusoutput('find %s -name "*.%s" -print' % (option['-s'], option['-e']))[1],'\n')
else:
    print 'ERROR: wrong arguments!'
    print
    print 'usage:', sys.argv[0], '[arguments]'
    print 'arguments ::='
    print '              -c <configfile>'
    print '              -e <extension>'
    print '              -s <search_path>'
    print '              -w <working directory>'
    print
    sys.exit()

if cfgfiles:
    for i in cfgfiles:
        zeilen.append(i+';f\n')

neuzeile=[]
# Loeschen der alten logs
if os.path.exists(logdir):
    commands.getstatusoutput('rm -r '+logdir)
# Erzeugen eines neuen logs Verzeichnis
if not os.path.exists(logdir):
    os.mkdir(logdir,0755)
    
for i in range(len(zeilen)):
    neuzeile.append(string.split(string.strip(zeilen[i]),';'))
    localpath = string.split(neuzeile[i][0],'/')
    localpath[0] = logdir
    
    dir=localpath[0]
    for j in (range(len(localpath)-1)):
    # Erst die Verzeichnisstrucktur von den Files ('f') erzeugen
        if str(neuzeile[i][1])== 'f':
            if j is not 0:
                dir= dir + '/' + localpath[j]
            if not os.path.exists(dir):
                print 'Make',j,dir
                os.mkdir(dir)
            # Jetzt fuer Directories ('d') die Strucktur erzeugen
        if str(neuzeile[i][1])== 'd':
            if j is not 0 and not (len(localpath)-2):
                dir= dir + '/' + localpath[j]
            if not os.path.exists(dir):
                print 'Make',j,dir
                os.mkdir(dir)
            # Verzeichnisstrucktur ~/logs ist up to date        
    # Kopiere Dateien bzw. Ordner
    if str(neuzeile[i][1])== 'f':
        commands.getstatusoutput('cp '+neuzeile[i][0]+' '+localpath[0]+neuzeile[i][0])
    if str(neuzeile[i][1])== 'd':
        if os.uname()[0]=='Linux':
            commands.getstatusoutput('cp -a '+neuzeile[i][0]+' '+localpath[0]+neuzeile[i][0])
        if os.uname()[0]=='SunOS':
            commands.getstatusoutput('cp -R '+neuzeile[i][0]+' '+localpath[0]+neuzeile[i][0])
        if os.uname()[0]=='FreeBSD':
            commands.getstatusoutput('cp -R '+neuzeile[i][0]+' '+localpath[0]+neuzeile[i][0])

commands.getstatusoutput('find '+logdir+' -print | cpio -ocaO cfgbk.'+os.uname()[1]+'.cpio')
commands.getstatusoutput('rm -rf '+logdir)
