#!/usr/bin/env python
##
#
# $Author: rb $
revision = '$Revision: 2.12 $'
#
##

import os
import sys

VERSION = '2.5.0'
COPYRIGHT_SCRIPT = '1Ci(R) GmbH, http://www.1ci.com dmerce(R) Copyright 2000-2003'
COPYRIGHT_HTML = 'Copyright 2000-2003 by <a href="http://www.1ci.de">1Ci(R) GmbH</a>'
COPYRIGHT_HTML_COMMENT = '<!--\n\t1Ci(R) GmbH, http://www.1ci.de\n' \
                         '\tPage generated by dmerce(R) %s\n-->\n\n' % VERSION

def vars():
    try:
        dmerceHome = os.environ['DMERCE_HOME']
    except:
        dmerceHome = '/opt/dmerce/product/dmerce'
    sys.path.append(dmerceHome + '/' + VERSION)
    sys.path.append(dmerceHome + '/' + VERSION + '/DMS')
