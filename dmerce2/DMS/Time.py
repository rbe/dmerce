#!/usr/local/bin/python
##
#
# $Author: rb $
revision = '$Revision: 2.30 $'
#
##

#import sys
#sys.path.append('/export/home/r/rb/dmerce2')
import types
import time
import string
import operator
import Core.OS
import Core.Log
import DMS.Object
import DMS.MBase

class Conversion(DMS.MBase.Class):

    """ library for time conversions """

    # Define values for time entities in seconds
    inSecs = {}
    inSecs['min']     = 60
    inSecs['hour']    = 60 * 60
    inSecs['day']     = 60 * 60 * 24
    inSecs['week']    = 60 * 60 * 24 * 7
    inSecs['month28'] = 60 * 60 * 24 * 7 * 28
    inSecs['month29'] = 60 * 60 * 24 * 7 * 29
    inSecs['month30'] = 60 * 60 * 24 * 7 * 30
    inSecs['month31'] = 60 * 60 * 24 * 7 * 31
    inSecs['year365'] = 60 * 60 * 24 * 7 * 365
    inSecs['year366'] = 60 * 60 * 24 * 7 * 366

    def __init__(self, kw):
        DMS.MBase.Class.__init__(self, kw)
        self.__log = Core.Log.File(debug = self._debug, module = '1[Time].Conversion')

    def FieldsIsoToTimestamp(self):
        """ convert a cgi form field from ISO to timestamp """
        for f in string.split(self._cgi.GetqVar('qFieldsIsoToTimestamp'), ','):
            field = string.replace(f, ' ', '')
            convertedValue = self.IsoToTimestamp(self._cgi.GetField(field))
            self._cgi.SetField(field, convertedValue)
        return 1

    def FieldsGermanToTimestamp(self):
        """ convert a cgi form field from german to timestamp """
        qfgtt = self._cgi.GetqVar('qFieldsGermanToTimestamp')
        #if qfgtt is types.StringType:
        for f in string.split(qfgtt, ','):
            field = string.replace(f, ' ', '')
            convertedValue = self.GermanToTimestamp(self._cgi.GetField(field))
            self._cgi.SetField(field, convertedValue)
        return 1

    def FieldsToTimestamp(self):
        """ convert a cgi form field from german to timestamp """
        qfgtt = self._cgi.GetqVar('qFieldsToTimestamp')
        #if qfgtt is types.StringType:
        for f in string.split(qfgtt, ','):
            field = string.replace(f, ' ', '')
            convertedValue = self.DateToTimestamp(self._cgi.GetField(field))
            self._cgi.SetField(field, convertedValue)
        return 1

    def ActualDate(self, format):
        """ return actual date """
        t = time.time()
        if format == 'ISO' or not format:
            f = '%Y-%m-%d'
        elif format == 'europe':
            f = '%d.%m.%Y'
        elif format == 'english':
            f = '%m/%d/%Y'
        else:
            f = format
        return time.strftime(f, time.localtime(time.time()))

    def ActualTime(self, format):
        """ return actual time """
        t = time.time()
        if format:
            t = time.strftime(format, time.localtime(time.time()))
        else:
            t = time.strftime('%H:%M:%S', time.localtime(time.time()))
        return t

    def Actual(self, formatDate = '', formatTime = ''):
        """ return actual date and time """
        f = time.time()
        if formatDate != 'timestamp':
            f = ''
            if formatDate:
                f = self.ActualDate(formatDate)
            if formatTime:
                if f:
                    f = f + ' '
                f = f + self.ActualTime(formatTime)
        return f

    def Calc(self, format = '%Y-%m-%d %H:%M:%S', timestamp = 0, **opt):
        """ calculate a date/time string """
        # Return seconds of time entity
        if opt.has_key('inSecs'):
            # Multiply?
            if opt.has_key('mult'):
                return self.inSecs[opt['inSecs']] * opt['mult']
            else:
                return self.inSecs[opt['inSecs']]
        # Get time tuple of 'now'
        # Convert tuple to dictionary
        timeDict = self.TimeTuple2TimeDict(time.localtime(time.time()))
        # Calculate new
        for k in opt.keys():
            # Add time
            if k != 'week':
                timeDict[k] = timeDict[k] + opt[k]
            else:
                # Week is a special thing...
                # Add week * 7 to julianDay
                timeDict['day'] = timeDict['day'] + (opt[k] * 7)
        # Convert dictionary to tuple
        timeTuple = self.TimeDict2TimeTuple(timeDict)
        # Return new formatted date/time string
        if not timestamp:
            return time.strftime(format, time.localtime(time.mktime(timeTuple)))
        else:
            return '%f' % time.mktime(timeTuple)

    def DateToTimestamp(self, date):
        """
        convert a given date to timestamp
        care about given format of date
        """
        if self.isGermanDate(date):
            return self.GermanToTimestamp(date)
        elif self.isIsoDate(date):
            return self.IsoToTimestamp(date)
        else:
            return 0.0

    def isGermanDate(self, date):
        """ check if we have an german date format """
        if string.find(date, '.') == 2 and string.find(date, '.', 3) == 5:
            return 1
        else:
            return 0
        
    def isGermanDateWithTimeHH(self, date):
        if self.isGermanDate(date) and string.find(date, ' ') == 11:
            return 1
        else:
            return 0

    def isGermanDateWithTimeHHMM(self, date):
        if self.isGermanDate(date) and string.find(date, ':') == 13:
            return 1
        else:
            return 0

    def isGermanDateWithTimeHHMMSS(self, date):
        if self.isGermanDate(date) and string.find(date, ':') == 13 and string.find(date, ':', 14) == 16:
            return 1
        else:
            return 0
  
    def ChopGermanDate(self, date = ''):
        """
        convert german date format to timestamp
        automatically recognizes what is given
        """
        year = 1970
        month = 1
        day = 1
        hour = 0
        min = 0
        sec = 0
        try:
            # Get DD-MM-YYYY
            day = int(date[0:2])
            month = int(date[3:5])
            year = int(date[6:10])
            if len(date) == 19:
                # Time also given? get HH:MM:SS
                hour = int(date[11:13])
                min = int(date[14:16])
                sec = int(date[17:19])
            elif len(date) == 16:
                # Time also given? get HH:MM:SS
                hour = int(date[11:13])
                min = int(date[14:16])
                sec = 0
            return (year, month, day, hour, min, sec)
        except:
            return (1970, 1, 1, 1, 0, 0)

    def GermanToTimestamp(self, date = ''):
        year, month, day, hour, min, sec = self.ChopGermanDate(date)
        m = time.mktime((year, month, day, hour, min, sec, 0, 0, -1))
        self.__log.Write(msg = 'GermanToTimestamp: CONVERTING YEAR=%s MONTH=%s DAY=%s'
                         ' HOUR=%s MIN=%s SEC=%s RESULT=%s'
                         % (year, month, day, hour, min, sec, m))
        return m

    def GermanToIso(self, date = ''):
        year, month, day, hour, min, sec = self.ChopGermanDate(date)
        return '%s-%02d-%02d %02d:%02d:%02d' % (year, month, day, hour, min, sec)

    def isIsoDate(self, date = ''):
        """ check if we have an ISO date format """
        if string.find(date, '-') == 4 and string.find(date, '-', 5) == 7:
            return 1
        else:
            return 0

    def isIsoDateWithTimeHH(self, date = ''):
        """ check if we have an ISO date format """
        if self.isIsoDate(date) and string.find(date, ' ') == 11:
            return 1
        else:
            return 0

    def isIsoDateWithTimeHHMM(self, date = ''):
        """ check if we have an ISO date format """
        if self.isIsoDate(date) and string.find(date, ':') == 13:
            return 1
        else:
            return 0

    def isIsoDateWithTimeHHMMSS(self, date = ''):
        """ check if we have an ISO date format """
        if self.isIsoDate(date) and string.find(date, ':') == 13 and string.find(date, ':', 14) == 16:
            return 1
        else:
            return 0

    def ChopIsoDate(self, date = ''):
        """
        convert ISO date format to timestamp
        automatically recognizes what is given
        """
        year = 1970
        month = 1
        day = 1
        hour = 0
        min = 0
        sec = 0
        # self.__log.Write(msg = 'ChopIsoDate: %s-%s' % (date, type(date)))
        # Get slices from iso; YYYY-MM-DD
        year = int(date[0:4])
        month = int(date[5:7])
        day = int(date[8:10])
        # Time also given?
        if len(date) >= 19:
            # Get slices from iso; HH:MM:SS
            hour = int(date[11:13])
            min = int(date[14:16])
            sec = int(date[17:19])
        return (year, month, day, hour, min, sec)

    def IsoToTimestamp(self, date = ''):
        year, month, day, hour, min, sec = self.ChopIsoDate(date)
        return time.mktime((year, month, day, hour, min, sec, 0, 0, -1))

    def IsoToGerman(self, date = ''):
        year, month, day, hour, min, sec = self.ChopIsoDate(date)
        if not year:
            return ''
        else:
            return '%02d.%02d.%04d %02d:%02d:%02d' % (day, month, year, hour, min, sec)

    def TimeTuple2TimeDict(self, timeTuple = ()):
        """ generate time dictionary """
        dict = {}
        # Assign values from tuple
        dict['year'] = timeTuple[0]
        dict['month'] = timeTuple[1]
        dict['day'] = timeTuple[2]
        dict['hour'] = timeTuple[3]
        dict['min'] = timeTuple[4]
        dict['secs'] = timeTuple[5]
        dict['weekDay'] = timeTuple[6]
        dict['julianDay'] = timeTuple[7]
        dict['DST'] = timeTuple[8]
        # Return dictionary
        return dict

    def TimeDict2TimeTuple(self, timeDict = {}):
        """ generate time tuple """
        # Assign values from dictionary
        return (timeDict['year'], timeDict['month'], timeDict['day'], timeDict['hour'],
                timeDict['min'], timeDict['secs'], timeDict['weekDay'], timeDict['julianDay'],
                timeDict['DST'])
    
    def UnixToLocal(self, secs = 0.0, format = '%Y-%m-%d %H:%M:%S'):
        """
        return human readable date and time format
        from UNIX time
        """
        if secs > 0:
            # If timestamp is given return formatted string
            return time.strftime(format, time.localtime(secs))
        else:
            # Return empty
            return '[never]'

    def __DiffCheck(self, d1, d2):
        """ check d1 and d2 for type; if string (no timestamp) convert """
        if not d1 or not d2:
            return 'N/A'
        if type(d1) is types.StringType:
            #if d1[0] == '0':
            #    d1 = 'N/A'
            if self.isIsoDate(d1):
                d1 = self.IsoToTimestamp(d1)
            if self.isGermanDate(d1):
                d1 = self.GermanToTimestamp(d1)
        if type(d2) is types.StringType:
            #if d2[0] == '0':
            #    d2 = 'N/A'
            if self.isIsoDate(d2):
                d2 = self.IsoToTimestamp(d2)
            if self.isGermanDate(d2):
                d2 = self.GermanToTimestamp(d2)
        return d1, d2

    def DiffInDays(self, d1, d2):
        """ calculate difference between two dates rounded to days """
        d1, d2 = self.__DiffCheck(d1, d2)
        if d1 == 'N/A' or d2 == 'N/A':
            return 'N/A'
        # Calculate difference in seconds
        delta = d1 - d2
        if delta < 0:
            delta = operator.neg(delta)
        # Calculate days
        days = int(round(delta / 60 / 60 / 24))
        if days == 0:
            days = 1
        return days

    def MergeFieldsToDateTime(self):
        """
        add cgi fields to timestamp and set to other cgi field
        if hour, minute or second were not found, we use 0 as their
        values
        """
        oc = DMS.Object.Convert()
        fieldsToMerge = string.split(self._cgi.GetqVar('qMergeFieldsToDateTime'), ',')
        for field in fieldsToMerge:
            t = time.localtime(time.time())
            y = time.strftime('%Y', t)
            year = oc.ChangeType(self._cgi.GetField('%s_Year' % field, y), 'int')
            m = time.strftime('%m', t)
            month = oc.ChangeType(self._cgi.GetField('%s_Month' % field, m), 'int')
            d = time.strftime('%d', t)
            day = oc.ChangeType(self._cgi.GetField('%s_Day' % field, d), 'int')
            h = time.strftime('%H', t)
            hour = oc.ChangeType(self._cgi.GetField('%s_Hour' % field, h), 'int')
            m = time.strftime('%M', t)
            minute = oc.ChangeType(self._cgi.GetField('%s_Minute' % field, m), 'int')
            s = time.strftime('%S', t)
            secs = oc.ChangeType(self._cgi.GetField('%s_Secs' % field, s), 'int')
            dbfield = 0
            if string.find(field, '__') > 0:
                schema, tf = string.split(field, '__')
                t, f = string.split(tf, '_')
                dbfield = 1
            elif string.find(field, '_') > 0:
                t, f = string.split(field, '_')
                dbfield = 1
            if dbfield:
                if self._sql.GetType() == 'ORACLE':
                    th = self._sql.GetTableHandler(table = schema + '__' + t)
                    th.Describe()
                    if th.GetRealFieldType(f) == 'DATE':
                        osBase = Core.OS.Base()
                        if year and month and day:
                            nt = '%s-%02i-%02i %02i:%02i:%02i' % (year, month, day, hour, minute, secs)
                        else:
                            nt = 'NULL'
                else:
                    nt = time.mktime((year, month, day, hour, minute, secs, 0, 0, -1))
            else:
                nt = time.mktime((year, month, day, hour, minute, secs, 0, 0, -1))
            self._cgi.RemoveField('%s_Year' % field)
            self._cgi.RemoveField('%s_Month' % field)
            self._cgi.RemoveField('%s_Day' % field)
            self._cgi.RemoveField('%s_Hour' % field)
            self._cgi.RemoveField('%s_Minute' % field)
            self._cgi.RemoveField('%s_Secs' % field)
            self._cgi.SetField(field, nt)
        return 1

    def Reformat(self, date, format):
        r = ''
        if date:
            if self.isIsoDate(date):
                year, month, day, hour, min, sec = self.ChopIsoDate(date)
            elif self.isGermanDate(date):
                year, month, day, hour, min, sec = self.ChopGermanDate(date)
            if string.upper(format) == 'GERMAN':
                r = '%02i.%02i.%04i %02i:%02i:%02i' % (day, month, year, hour, min, sec)
        return r

class Get(DMS.MBase.Class):

    """
    get components from date and time
    information
    """

    def __init__(self, kw):
        DMS.MBase.Class.__init__(self, kw)
        #self.__log = Core.Log.File(debug = self._debug, module = 'Time.Get')

    def ActualDay(self, timestamp):
        return time.strftime('%d', time.localtime(timestamp))

    def ActualMonth(self):
        return time.strftime('%m', time.localtime(timestamp))

    def AcutalYear(self):
        return time.strftime('%Y', time.localtime(timestamp))

    def AcutalHour(self):
        return time.strftime('%H', time.localtime(timestamp))

    def AcutalMinute(self):
        return time.strftime('%M', time.localtime(timestamp))

    def AcutalSecond(self):
        return time.strftime('%S', time.localtime(timestamp))

    def Midnight(self, year = 0, month = 0, day = 0):
        """
        return timestamp of midnight at a given date
        default: return timestamp of midnight of actual date
        """
        if type(month) is types.StringType:
            if month[0] == '0':
                month = int(month[1:])
        if type(day) is types.StringType:
            if day[0] == '0':
                day = int(day[1:])
        tl = time.localtime(time.time())
        if not year:
            year = int(time.strftime('%Y', tl))
        if not month:
            month = int(time.strftime('%m', tl))
        if not day:
            day = int(time.strftime('%d', tl))
        return time.mktime((year, month, day, 23, 59, 59, 0, 0, -1))

class Check(DMS.MBase.Class):

    """ check things around date and time """

    def __init__(self, kw):
        DMS.MBase.Class.__init__(self, kw)
        self.__log = Core.Log.File(debug = self._debug, module = 'Time.Check')

    def DayInMonth(self):
        """ check if a day is possible in a given month """
        month30 = [4, 6, 9, 11]  ### FEB!!!
        month31 = [1, 3, 5, 7, 8, 10, 12]

class Select(DMS.MBase.Class):

    """ generate <select>-boxes for date and time """

    def __init__(self, kw):
        DMS.MBase.Class.__init__(self, kw)
        self.__log = Core.Log.File(debug = self._debug, module = 'Time.Select')

    def CareForTimestamp(self, timestamp):
        c = Conversion({})
        if type(timestamp) is types.StringType:
            if c.isIsoDate(timestamp):
                timestamp = c.IsoToTimestamp(timestamp)
            elif c.isGermanDate(timestamp):
                timestamp = c.GermanToTimestamp(timestamp)
        return timestamp

    def __GenerateActual(self, timestamp, name, selected, t, r, p, m, withNull = 0, nullSelected = 0):
        if timestamp is None:
            timestamp = time.time()
        else:
            timestamp = self.CareForTimestamp(timestamp)
        s = '<select name="%s">\n' % name
        if withNull:
            s = s + '<option value="0">0</option>'
        if nullSelected:
            selected = 0
        elif selected is None and timestamp:
            selected = int(time.strftime(t, time.localtime(timestamp))) + p - m
        for i in range(r[0], r[1] + 1):
            s = s + '<option value="%0i"' % i
            if i == selected:
                s = s + ' selected'
            s = s + '>%02i</option>\n' % i
        s = s + '</select>\n'
        return s

    def ActualDay(self, timestamp = None, name = 'ActualDay', selected = None, rangeFrom = None, rangeTo = None, plus = 0, minus = 0, withNull = 0, nullSelected = 0):
        if rangeFrom is None:
            rangeFrom = 1
        if rangeTo is None:
            rangeTo = 31
        return self.__GenerateActual(timestamp, name, selected, '%d', (rangeFrom, rangeTo), plus, minus, withNull, nullSelected)

    def ActualMonth(self, timestamp = None, name = 'ActualMonth', selected = None, rangeFrom = None, rangeTo = None, plus = 0, minus = 0, withNull = 0, nullSelected = 0):
        if rangeFrom is None:
            rangeFrom = 1
        if rangeTo is None:
            rangeTo = 12
        return self.__GenerateActual(timestamp, name, selected, '%m', (rangeFrom, rangeTo), plus, minus, withNull, nullSelected)

    def ActualYear(self, timestamp = None, name = 'ActualYear', selected = None, rangeFrom = None, rangeTo = None, plus = 0, minus = 0, withNull = 0, nullSelected = 0):
        if not timestamp:
            timestamp = time.time()
        else:
            timestamp = self.CareForTimestamp(timestamp)
        if rangeFrom is None:
            rangeFrom = int(time.strftime('%Y', time.localtime(timestamp)))
        elif rangeFrom < 0:
            # actual year - rangeFrom
            rf = int(time.strftime('%Y', time.localtime(timestamp))) - operator.neg(rangeFrom)
            rangeFrom = rf
        if rangeTo is None:
            rangeTo = rangeFrom + 1
        elif rangeTo < 1000:
            rangeTo = int(time.strftime('%Y', time.localtime(timestamp))) + rangeTo
        return self.__GenerateActual(timestamp, name, selected, '%Y', (rangeFrom, rangeTo), plus, minus, withNull, nullSelected)

    def ActualHour(self, timestamp = None, name = 'ActualHour', selected = None, rangeFrom = None, rangeTo = None, plus = 0, minus = 0, withNull = 0, nullSelected = 0):
        if rangeFrom is None:
            rangeFrom = 0
        if rangeTo is None:
            rangeTo = 23
        return self.__GenerateActual(timestamp, name, selected, '%H', (rangeFrom, rangeTo), plus, minus, withNull, nullSelected)

    def ActualMinute(self, timestamp = None, name = 'ActualMinute', selected = None,
                     rangeFrom = None, rangeTo = None, plus = 0, minus = 0, withNull = 0, nullSelected = 0):
        if rangeFrom is None:
            rangeFrom = 0
        if rangeTo is None:
            rangeTo = 59
        return self.__GenerateActual(timestamp, name, selected, '%M', (rangeFrom, rangeTo), plus, minus, withNull, nullSelected)

    def ActualSecond(self, timestamp = None, name = 'ActualSecond', selected = None,
                     rangeFrom = None, rangeTo = None, plus = 0, minus = 0, withNull = 0, nullSelected = 0):
        if rangeFrom is None:
            rangeFrom = 0
        if rangeTo is None:
            rangeTo = 59
        return self.__GenerateActual(timestamp, name, selected, '%S', (rangeFrom, rangeTo), plus, minus, withNull, nullSelected)

#c = Conversion({})
#print c.Actual(formatDate='europe')
#print c.DiffInDays(d1='10.04.2002 16:36',d2=1017671815.53)
#print c.IsoToGerman('1959-04-02 00:00:00.00')[:10]
#g = Get({})
#print g.Midnight(year='2002',month='08',day='07')
