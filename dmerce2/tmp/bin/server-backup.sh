#!/bin/sh

# Backupskript: backup from localhost to remote server

BACKUP_DIR=
DMERCE_HOME=
BACKUP_SRV=

echo
echo "`date`: START BACKUP"
echo "`date`: BACKUP FROM ${DMERCE_HOME} TO ${BACKUP_SRV}:${BACKUP_DIR}"
scp -r ${DMERCE_HOME}/* ${BACKUP_SRV}:${BACKUP_DIR}
echo "`date`: BACKUP DONE"
echo

