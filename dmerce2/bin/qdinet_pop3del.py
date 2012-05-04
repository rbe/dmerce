#!/usr/local/bin/python

import sys
import string
import time
import poplib

def t():
    return time.strftime('%a %b %d %X %Z %Y:', time.localtime(time.time()))

def help():
    print
    print "pop3del <host> <user> <password> <string>"
    print

def checkParams():
    if len(sys.argv) != 5:
        return 0
    else:
        return 1

def popDelete():
    """ delete all mails where From: == from """
    p = poplib.POP3(sys.argv[1])
    x = p.getwelcome()
    x = p.user(sys.argv[2])
    x = p.pass_(sys.argv[3])
    rc, msgList, size = p.list()
    print t(), 'There are %s messages' % len(msgList)
    for msg in msgList:
        msgDelete = 0
        msgNo = string.split(msg, ' ')[0]
        print t(), 'Reading message number %s - Checking for %s' % (msgNo, sys.argv[4])
        rc, msgContent, size = p.retr(msgNo)
        for line in msgContent:
            pos = string.find(line, sys.argv[4])
            if pos >= 0:
                msgDelete = 1
        if msgDelete:
            print t(), 'Deleting message %s' % msgNo
            p.dele(msgNo)
    p.quit()

def main():
    if checkParams():
        popDelete()
    else:
        help()

if __name__ == '__main__':
    main()
