###############################################################################
#
# Synopsis:	db_check.sh [-b] [sid ...]
# Purpose:	to check the health of database instances
#
# Author:	Steve Adams (based on various similar scripts)
#
# Description:  This script checks the named instances, or all instances
#		listed in the oratab file with a Y in the third field.
#		The -b option should be used during the backup window.
#
#		It may be used in 3 ways:
#		* interactively
#		* from cron
#		* from other scripts
#
#		The following checks are performed:
#		* a listener must be running
#		* each instance must be available
#		* instances must not be in resticted session mode
#		* auto-archiving must be enabled in archivelog mode
#		* archiver processes must not be stuck
#		* tablespaces must not be in hot backup mode
#		During the backup window, instances may be down, or in
#		restricted session mode, and tablespaces may be in hot
#		backup mode.
#		
#		If any checks fail, they are reported:
#		* to the terminal, if running interactively
#		* to the system log via logger (syslog)
#		* with a non-zero exit status
#
###############################################################################

# set variables
#
export PROGRAM=${0##*/}			# program name
export ORATAB				# path to the oratab file
export INTERACTIVE=:			# whether to print messages to stdout
export DEBUG=				# whether to actually log messages
# export DEBUG=print			# (uncomment this line while debugging)
export FACILITY=oracle			# change to "user" if logger complains
export BACKUP=false			# are we in the backup window
export ORACLE_SID=			# the instance being checked
export SPOOL=/tmp/$$.spool		# the spool file for output
export READY=/tmp/$$.ready		# file to flag that output is ready
export STATUS=0				# exit status


# find the oratab file
# if none, assume no Oracle and quit
#
{ ORATAB=/etc/opt/oracle/oratab && [[ -r $ORATAB ]] ; } ||
{ ORATAB=/var/opt/oracle/oratab && [[ -r $ORATAB ]] ; } ||
{ ORATAB=/etc/oratab            && [[ -r $ORATAB ]] ; } ||
exit 0


# are we running interactively?
# if not, we must have logger
#
tty -s && INTERACTIVE=print || whence logger > /dev/null || exit 1


# check that a listener is running
#
if ps -fu oracle | grep -v grep | grep -c tnslsnr >/dev/null 
then
    $INTERACTIVE "Listener is running"
else
    msg="$PROGRAM: Listener not running - no network access to Oracle"
    $DEBUG logger -p $FACILITY.err "$msg"
    STATUS=1
    $INTERACTIVE $msg
fi


# check for oraenv
#
whence oraenv > /dev/null ||
{
    msg="$PROGRAM: Cannot check Oracle - oraenv not in PATH"
    $DEBUG logger -p $FACILITY.warning "$msg"
    $INTERACTIVE $msg
    exit 1
}


# are we in the backup window
#
[[ $1 = -b ]] && { BACKUP=true; shift; }


# check specified instances or all auto-started instances
#
for ORACLE_SID in ${*-$(awk -F: '!/^#/ && $3=="Y" {print $1}' $ORATAB)}
do                           
    # set environment and check for sqlplus
    #
    ORAENV_ASK=NO . oraenv                   
    whence sqlplus > /dev/null ||
    {
	msg="$PROGRAM: Cannot check Oracle - sqlplus not in PATH"
	$DEBUG logger -p $FACILITY.warning "$msg"
	STATUS=1
	$INTERACTIVE $msg
	continue
    }


    # check connectivity for an ordinary user
    # (we expect an ORA-01017 error; most others mean something)
    #
    rm -f $READY
    print "
	connect nobody/really
	host touch $READY
	exit " |
    sqlplus /nolog > $SPOOL &

    # wait for up to 59 seconds
    #
    ((timeout = 60))
    while ((timeout -= 1)) && [[ ! -r $READY ]]
    do
	sleep 1
    done

    # check for hang
    #
    [[ -r $READY ]] ||
    {
	kill $!
	msg="$PROGRAM: Oracle instance $ORACLE_SID is not responding"
	$DEBUG logger -p $FACILITY.err "$msg"
	STATUS=1
	$INTERACTIVE $msg
	continue
    }

    # check for other problems
    #
    ERROR=$(sed -n 's/.*ORA-\([0-9]*\):.*/\1/p' $SPOOL|head -1)
    case $ERROR in
	    #
	    # this is what we expect
	    #
    01017)  # invalid username/password
	    ;;
	    #
	    # this group are not expected, but OK anyway
	    #
    "")     # no error (connected OK)
	    ;;
    01035)  # instance in restricted session mode
	    ;;
    01040)  # invalid password
	    ;;
    01045)  # no create session priv
	    ;;
	    #
	    # any other error is a problem
	    #
    00257)  # archiver stuck
	    #
	    msg="$PROGRAM: Oracle archiver for $ORACLE_SID is stuck"
	    $DEBUG logger -p $FACILITY.alert "$msg"
	    STATUS=1
	    $INTERACTIVE $msg
	    ;;
    01034)  # Oracle not available
	    #
	    msg="$PROGRAM: Oracle instance $ORACLE_SID is not up"
	    $BACKUP || $DEBUG logger -p $FACILITY.err "$msg"
	    $BACKUP || STATUS=1
	    $BACKUP || $INTERACTIVE $msg
	    continue
	    ;;
    *)      # something else
	    #
	    msg="$PROGRAM: Got Oracle error ORA-$ERROR from $ORACLE_SID"
	    $DEBUG logger -p $FACILITY.err "$msg"
	    STATUS=1
	    $INTERACTIVE $msg
	    continue
	    ;;
    esac
    $INTERACTIVE "Oracle instance $ORACLE_SID is up"

    # now connect internal and check everything else
    #
    { SVRMGRL=svrmgrl		&& whence $SVRMGRL > /dev/null ; } ||
    { SVRMGRL=sqldba		&& whence $SVRMGRL > /dev/null ; } ||
    {
	msg="$PROGRAM: Cannot check Oracle - svrmgrl not in PATH"
	$DEBUG logger -p $FACILITY.warning "$msg"
	STATUS=1
	$INTERACTIVE $msg
	continue
    }
    rm -f $READY
    print "
	connect internal
	select '/actives=' || count(*) from v\$backup where status = 'ACTIVE';
	select '/restrict=' || value from v\$instance where key like 'RESTR%';
	select '/restrict=' || logins from v\$instance;
	archive log list;
	host touch $READY
	exit " |
    $SVRMGRL > $SPOOL &

    # wait for up to 89 seconds
    #
    ((timeout = 90))
    while ((timeout -= 1)) && [[ ! -r $READY ]]
    do
	sleep 1
    done

    # check for hang (most unlikely at this point)
    #
    [[ -r $READY ]] ||
    {
	kill $!
	msg="$PROGRAM: Oracle instance $ORACLE_SID is not responding"
	$DEBUG logger -p $FACILITY.err "$msg"
	STATUS=1
	$INTERACTIVE $msg
	continue
    }

    # check for active backups, archiver and restricted session
    #
    eval $(sed -n 's:^/\(.*=.*\):\1:p' $SPOOL)
    [[ $actives -eq 0 ]] ||
    {
	msg="$PROGRAM: Database $ORACLE_SID has files in hot backup mode"
	$BACKUP || $DEBUG logger -p $FACILITY.warning "$msg"
	$BACKUP || STATUS=1
	$BACKUP || $INTERACTIVE $msg
    }
    eval $(awk '/Database log mode/ {print "mode=" $(NF-2)}
		/Automatic archival/ {print "auto=" $NF}' $SPOOL)
    [[ $auto = Enabled || $mode = No ]] ||
    {
	msg="$PROGRAM: Oracle archiver for $ORACLE_SID is not running"
	$DEBUG logger -p $FACILITY.err "$msg"
	STATUS=1
	$INTERACTIVE $msg
    }
    [[ $restrict = 0 || $restrict = ALLOWED ]] ||
    {
	msg="$PROGRAM: Oracle instance $ORACLE_SID is in restricted mode"
	$BACKUP || $DEBUG logger -p $FACILITY.warning "$msg"
	$BACKUP || STATUS=1
	$BACKUP || $INTERACTIVE $msg
    }
done

# cleanup
#
$DEBUG rm -f $SPOOL
$DEBUG rm -f $READY

exit $STATUS
