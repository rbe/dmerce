#!/usr/bin/env python
##
#
# $Author: rb $
revision = '$Revision: 1.1 $'
#
##

#
# I M P O R T  M O D U L E S
#
try:
    import sys
    import string
    import types
    import Core.Log
    import Core.Error
    import DMS.SQL
except:
    print '[Guardian.anasam: ERROR LOADING MODULES]'
    sys.exit()

#
# Analyse a customer
#
class Customer:

    def __init__(self, num):
        self.__sql = DMS.SQL.Query(DMS.SQL.DBAPI('MySQL:root:Mu%rs3)@obelix.xstone.com:Xstone'), 'sam')
        self.__num = num
        self.__timeSum = 0
        self.__ip = {}
        self.__ana = []
        self.__GetName()
        self.__Analyse()

    # Get analysed data
    def Get(self):
        return self.__ana

    # Get name of customer
    def __GetName(self):
        rc, r = self.__sql['SELECT Name, Vorname, Firma, Firma2 FROM Kunde WHERE ID = %i' % self.__num]
        try:
            self.__name = '%s %s: %s %s' % (r[0]['Firma'], r[0]['Firma2'], r[0]['Vorname'], r[0]['Name'])
            self.__name = string.replace(self.__name, ',', ' ')
        except:
            self.__name = 'Unbekannt'

    def __Analyse(self):
        rc, r = self.__sql['SELECT remote_addr, first_time, last_time FROM sam' \
                           ' WHERE customer_id = %i' % self.__num]
        if not rc:
            self.__ana.append(self.__num)
            self.__ana.append(self.__name)
            self.__ana.append(0)
            self.__ana.append(0)
            self.__ana.append(0)
            self.__ana.append('keine')
            return
        self.__loginCount = rc
        for row in r:
            # Replace commas for CSV
            for k in row.keys():
                if row[k] is types.StringType:
                    row[k] = string.replace(row[k], ',', ' ')
            # times
            timeDelta = row['last_time'] - row['first_time']
            self.__timeSum = self.__timeSum + timeDelta
            # remote_addr
            if not self.__ip.has_key(row['remote_addr']):
                self.__ip[row['remote_addr']] = 1
            else:
                self.__ip[row['remote_addr']] = self.__ip[row['remote_addr']] + 1
        # Find an IP from which a customer came more than 5 times
        ip = 'div.'
        for k in self.__ip.keys():
            if self.__ip[k] >= 5:
                ip = k
        # Calculate
        try:
            timeSumH = float(self.__timeSum / 60 / 60)
        except:
            timeSumH = 0
        try:
            timeAvg = float(self.__timeSum / rc / 60 / 60)
        except:
            timeAvg = 0
        self.__ana.append(self.__num)
        self.__ana.append(self.__name)
        self.__ana.append(self.__loginCount)
        self.__ana.append('%.2f' % timeSumH)
        self.__ana.append('%.2f' % timeAvg)
        self.__ana.append(ip)

# Sort a multi-dimensional list by columns
def mysort(list, *indices):
    nlist = map(lambda x, indices = indices: map(\
        lambda i, x = x: x[i], indices) + [x], list)
    nlist.sort()
    return map(lambda l: l[-1], nlist)

# M A I N
def main():
    l = []
    sql = DMS.SQL.Query(DMS.SQL.DBAPI('MySQL:root:Mu%rs3)@obelix.xstone.com:Xstone'), 'Kunde')
    rc, r = sql['SELECT MAX(ID) FROM Kunde']
    for i in range(0, r[0]['MAX(ID)']):
        c = Customer(i)
        l.append(c.Get())
    # Sort with serveral columns and save them
    for sorting in [2, 3, 4, 5]:
        l = mysort(l, sorting)
        l.reverse()
        fd = open('Xstone_SAM_%i.txt' % sorting, 'w')
        for line in l:
            s = ''
            for item in range(len(line)):
                fd.write('%s%s' % (s, line[item]))
                if item < len(line) - 1:
                    fd.write(',')
            fd.write('\n')
        fd.close()

if __name__ == '__main__':
    main()
