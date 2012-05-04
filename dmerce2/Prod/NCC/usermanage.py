#!/usr/bin/env python

""" imports from python """
import sys
import os
import commands
import string
import time

""" imports from NCC """
import vars
import DMS.SysTbl
import Guardian.Config
import DMS.SQL
import DMS.InitDmerce
import DMS.Lock

class UserAccounts:
    
    """ 1Ci - NCC - UserAccounts """

    def __init__(self):
        vars.vars()
        dmercecfg = DMS.InitDmerce.NCC()
        dmercecfg.InitNCC()
        self.__sqlData = dmercecfg.InitDBConnection()
        self.__dboid = DMS.SQL.DBOID(self.__sqlData, self.__sqlData)
        self.__homeprefix = dmercecfg.GetVar('Homeprefix')
        self.__homeprefix = string.replace(self.__homeprefix, ' ', '')
        if self.__homeprefix[len(self.__homeprefix)-1] != '/':
            self.__homeprefix = self.__homeprefix + '/'

    def GetNewUserData(self):
        """ Gets the new users, which have Todo=1 and active=1 """
        usercount, user = self.__sqlData["SELECT u.ID,u.UID,u.Login,u.GID,u.Passwd,u.Description,i.Shell" \
                                         " FROM AccUser AS u, AccShells AS i" \
                                         " WHERE u.Todo='%s' AND u.active='%s' AND u.Shell = i.ID " % ('1', '1')]
        return usercount, user

    def GetModifiedUser(self):
        """ Gets modified users, which have Modify=1 and active=1 """
        modifycount, modify = self.__sqlData["SELECT u.ID,u.UID,u.Login,u.GID,u.Passwd,u.Description,i.Shell" \
                                             " FROM AccUser AS u, AccShells AS i" \
                                             " WHERE u.Modify='%s' AND u.active='%s' AND u.Shell = i.ID " % ('1', '1')]
        return modifycount, modify
                                             
    def GetDeletedUser(self):
        """ Gets the deleted users, which have ToDo=1 and active=0 """
        delusercount, deluser = self.__sqlData["SELECT Login FROM AccUser WHERE Todo='%s' AND active='%s'" % ('1', '0')]
        return delusercount, deluser
    
    def AddUser(self, usercount, user):
        """ Adds userdata and set ToDo-Flag to zero in AccUser """
        for i in range(usercount):
            login = string.replace(user[i]['Login'], ' ', '')
            home = self.__homeprefix + login[0] + '/' + login
            action = 'userman -A ' + login + ' -p ' + user[i]['Passwd'] + ' -u ' + str(user[i]['UID']) + \
                     ' -g ' + str(user[i]['GID']) + ' -H ' + home + ' -s ' + user[i]['Shell'] 
            output = commands.getstatusoutput(action)
            print output
            updatecount, update = self.__sqlData["UPDATE AccUser SET ToDo = 0 WHERE Login = '%s'" % (login)]

    def ModifyUser(self, modifycount, modify):
        """ Modifies userdata and set Modify-Flag to zero in AccUser """
        for i in range(modifycount):
            login = string.replace(modify[i]['Login'], ' ', '')
            home = self.__homeprefix + login[0] + '/' + login
            action = 'userman -M ' + login + ' -p ' + modify[i]['Passwd'] + ' -u ' + \
                     str(modify[i]['UID']) + ' -g ' + str(modify[i]['GID']) + ' -H ' + home + ' -s ' + \
                     modify[i]['Shell'] 
            output = commands.getstatusoutput(action)
            print output
            updatecount, update = self.__sqlData["UPDATE AccUser SET Modify = 0 WHERE Login = '%s'" % (login)]

    def DeleteUser(self, delusercount, deluser):
        """ Deletes userdata in passwd and shadow and set ToDo-Flag to zero in AccUser """
        for i in range(delusercount):
            login = string.replace(deluser[i]['Login'], ' ', '')
            action = 'userman -D ' + login
            output = commands.getstatusoutput(action)
            print output
            updatecount, update = self.__sqlData["UPDATE AccUser SET ToDo = 0 WHERE Login = '%s'" % (login)]
        
if __name__ == '__main__':
    lock = DMS.Lock.Lock('dmerce.usermanage.lock')
    """ Create time string for log entry """
    logTime = time.localtime(time.time())
    print '[%s-%s-%s  %s:%s:%s]\n' % (logTime[0], logTime[1], logTime[2],
                                      logTime[3], logTime[4], logTime[5])
    """ now we have to check, if this script is still running """
    if not lock.CheckLock():
        print 'usermanage script such running'
        sys.exit()
    lock.AquireLock()
    a = UserAccounts()
    #Get new users
    usercount, user = a.GetNewUserData()
    #Add new users and set flag in DB
    if usercount !=None:
        a.AddUser(usercount, user)

    #Get modified users
    modifycount, modify = a.GetModifiedUser()
    #Modify Entries and set flag in DB
    if modifycount != None:
        a.ModifyUser(modifycount, modify)

    #Get deleted users
    delusercount, deluser = a.GetDeletedUser()
    #Delte users and set flag in DB
    if delusercount != None:
        a.DeleteUser(delusercount, deluser)
    """ end of script, remove lock file """
    lock.ReleaseLock()
