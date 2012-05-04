#!/usr/bin/env python
##
#
# $Author: rb $
revision = '$Revision: 2.8 $'
#
##

import exceptions
import ErrorMessages

class dmerceError(exceptions.Exception):

    """ base class for all dmerce errors """

    def __init__(self, errno, errmsg = ''):
        em = ErrorMessages.ErrMsg('en')
        self.args = (errno, em.Get(errno), errmsg)

class ConfigurationError(dmerceError):
    pass

class OSError(dmerceError):
    """ errors with operating system """
    pass

class FilesystemError(OSError):
    """ errors with file system """
    pass

class FileNotFoundError(FilesystemError):
    """ file not found """
    pass

class LogError(dmerceError):
    """ errors with logging """
    pass

class CannotCreateLogPathError(LogError):
    """ error when creating a path in log base dir """
    pass

class CannotAccessLogPathForWriting(LogError):
    """ no write perms on log file/path """
    pass

class CannotAccessLog(LogError):
    """ cannot access a logfile """
    pass

class LicenseError(dmerceError):
    """ license error handling """
    pass

class HostSerialNotValidError(LicenseError):
    """ validation of host serial number failed """
    pass

class SQLError(dmerceError):
    """ sql errors """
    pass

class DatabaseConnectStringError(SQLError):
    """
    dmerce cannot read database connection string
    from dmerce_sys system database
    """
    pass

class StatementError(SQLError):
    """ SQL statement errors """
    pass

class StatementSelectNoFieldsError(StatementError):
    """ select statement has no fields """
    pass

class StatementSelectNoTablesError(StatementError):
    """ select statement has no table(s) """
    pass

class DataError(SQLError):
    """ sql: data error """
    pass

class ProgrammingError(SQLError):
    pass

class OperationalError(SQLError):
    """ operational errors """
    pass

class UnknownDatabaseError(OperationalError):
    """ unknown database """
    pass

class UserAccessDeniedError(OperationalError):
    """ user access denied """
    pass

class UnknownServerError(OperationalError):
    """ connection errors """
    pass

class SQLSyntaxError(OperationalError):
    """ syntax error in SQL """
    pass

class NoDatabaseSelectedError(OperationalError):
    """ no database selected """
    pass

class TableExistsError(OperationalError):
    """ table exists """
    pass

class UnknownTableError(OperationalError):
    """ unknown table / table not found """
    pass

class UnknownColumnInWhereClauseError(OperationalError):
    """ unknown columns xy in where clause """
    pass

class IntegrityError(SQLError):
    """ key error: duplicate entry """
    pass

class DuplicateEntryError(IntegrityError):
    """ key error: duplicate entry """
    pass

class NoResultFoundError(SQLError):
    pass

class DBWarning(SQLError):
    pass

class SAMError(dmerceError):
    """ SAM error handling """
    pass

class CannotConnectDatabaseError(SAMError):
    """ SAM cannot connect to database """
    pass

class SessionError(SAMError):
    """ SAM has an error with a session """
    pass

class CannotGetAuthorizationError(SessionError):
    pass

class InactiveTimeoutError(SessionError):
    pass

class CannotFindSessionError(SessionError):
    """ cannot find a session (id) """
    pass

class SessionIdNotValidError(SessionError):
    """ session id is not valid """
    pass

class CannotRefreshSessionError(SessionError):
    """ cannot refresh a session """
    pass

class TimelimitNotOKError(SessionError):
    """ timelimit not ok """
    pass

class MustBeAuthorizedError(SessionError):
    """ session must be authorized but isn't """
    pass

class SessionIdError(SessionError):
    """ error with session id """
    pass

class TimestampNotValidError(SessionIdError):
    """ timestamp not valid """
    pass

class UserAgentNotValidError(SessionIdError):
    """ user agent not valid """
    pass

class HttpRefererNotValidError(SessionIdError):
    """ HTTP referer not valid """
    pass

class AuthenticationError(SessionError):
    """ error with authenticating sessions """
    pass

class AuthorizationError(SessionError):
    """ error with authorization sessions """
    pass

class CannotSetAuthFlagError(AuthorizationError):
    """ error when authorizing a session """
    pass

class AuthorizationFailedError(AuthorizationError):
    """ error when trying to set group and id in session """
    pass

class LoginInvalidError(AuthorizationError):
    """ login invalid """
    pass

class UXSError(dmerceError):
    """ UXS error handling """
    pass

class UXSNoUXSIError(UXSError):
    pass

class GroupsError(UXSError):
    pass

class CannotLookupGroupsError(GroupsError):
    pass

class SQLSelectPermissionDenied(UXSError):
    pass

class SQLInsertPermissionDenied(UXSError):
    pass

class SQLUpdatePermissionDenied(UXSError):
    pass

class SQLDeletePermissionDenied(UXSError):
    pass

class RightNotApplicableError(UXSError):
    pass

class TemplateError(dmerceError):
    errmsg = "Dispatcher; Template error handling"

class NoTemplateError(TemplateError):
    pass

class NoValidTemplateGivenError(TemplateError):
    pass

class TemplateEvalComplete(TemplateError):
    """ this is not an error :-) """
    pass

class IfError(TemplateError):
    pass

class IfExprError(IfError):
    pass

class IfHasNoEndif(IfError):
    pass

class RepeatError(TemplateError):
    pass

class RepeatHastNoLineTo(RepeatError):
    pass

class RepeatNotFoundError(RepeatError):
    pass

class TriggerExecutionError(dmerceError):
    pass

class PDBAFieldNotFoundForTableError(TemplateError):
    pass

class PDBATableNotFoundError(TemplateError):
    pass

class PDBAUpdateNoWhoIDError(TemplateError):
    pass

class ModuleError(dmerceError):
    """ base class for errors with modules """
    pass

class AuctionError(ModuleError):
    """ module Auction """
    pass

class MerchantError(ModuleError):
    pass

class MerchantArticleNotFound(MerchantError):
    pass

class MCLG(dmerceError):
    pass

class MCLGWrongStage(MCLG):
    pass

