#!/usr/local/bin/python1.5
import os
import string
import time
import sys

def CheckTime(isodate, period):
    dumptime = time.mktime([int(isodate[0:4]), int(isodate[4:6]), int(isodate[6:8]), 0, 0, 0, 0, 0, 1])
    if float(dumptime) < float(time.time() - period):
        return 1
    else:
        return None

def help():
    print "Syntax error !!"
    print "Usage: %s <path to dumpfiles>" % sys.argv[0]

def main():
    try:
        dumppath = sys.argv[1]
    except:
        help()
        sys.exit()
    files = os.listdir(dumppath)
    todeltime = 345600 # 3 days in seconds
    
    for i in range(len(files)):
        try:
            if string.count(files[i], '-') == 2:
                if CheckTime(string.split(files[i], '-')[-2], todeltime):
                    os.system('rm %s/%s' % (dumppath, files[i]))
        except:
            print 'String split error in filename ', files[i]

main()
