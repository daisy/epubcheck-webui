#!/bin/bash

LOGS=/home/ec2-user/src/daisy-pipeline.epubcheck/epubcheck-web/logs/
BACKUP_LOGS=/home/ec2-user/src/daisy-pipeline.epubcheck/epubcheck-web/logs/backup/

DATE=$(date "+%Y%m%d")
THIS_BACKUP="${BACKUP_LOGS}${DATE}"

echo "Backing up log files to ${THIS_BACKUP}"

sudo mkdir $THIS_BACKUP
sudo mv $LOGS/application.log.* $THIS_BACKUP