import types, error, math

###########################################################################
## Data Class
###########################################################################

class Data:

    def SetX(self, x):
        if not isinstance(x, types.ListType):
            raise error.Error, 'Die Argumentmenge ist keine Liste!'
        c = 1
        for i in x:
            if not (isinstance(i, types.StringType)
                    or isinstance(i, types.IntType)
                    or isinstance(i, types.LongType)
                    or isinstance(i, types.FloatType)):
                raise error.Error('Argument Nr.' + str(c) + ' ist weder String noch Zahl!',
                            self.__size)
            c = c+1
        self.__x = x

    def GetX(self):
        return self.__x

    def SetY(self, y):
        self.__y = []
        if not isinstance(y, types.ListType):
            raise error.Error, 'Die Liste der Wertmengen ist keine korrekte Liste!'
        if not len(self.__x) == len(y):
            raise error.Error, 'Die Wertliste hat nicht die richtige Laenge!'
        for j in range(0, len(y)):
                if not (isinstance(j, types.IntType) or
                        isinstance(j, types.LongType) or
                        isinstance(j, FloatType)):
                    raise error.Error, 'Wert Nr. ' + str(j) +  ' ist keine Zahl!'
        self.__y.append(y)

    def GetY(self, i):
        return self.__y[i]

##########################################################################
## Size Tuple Class
##########################################################################

class Size:

    def Init(self, size):
        if not (isinstance(size, types.TupleType) and len(size) == 2):
            raise error.Error, 'Die Groessenangabe fuer die Graphik ist kein 2-Tupel!'
        if not (isinstance(size[0], types.IntType) and size[0] > 0):
            raise error.Error, 'Die Bildbreite ist keine natuerliche Zahl > 0!'
        if not (isinstance(size[1], types.IntType) and size[1] > 0):
            raise error.Error, 'Die Bildhoehe ist keine natuerliche Zahl > 0!'
        self.__size = size

    def SetSize(self, size):
        if not (isinstance(size, types.TupleType) and len(size) == 2):
            raise error.Error, 'Die Groessenangabe fuer die Graphik ist kein 2-Tupel!'
        if not (isinstance(size[0], types.IntType) and size[0] > 0):
            raise error.Error, 'Die Bildbreite ist keine natuerliche Zahl > 0!'
        if not (isinstance(size[1], types.IntType) and size[1] > 0):
            raise error.Error, 'Die Bildhoehe ist keine natuerliche Zahl > 0!'
        self.__size = size

    def GetSize(self):
        return self.__size
