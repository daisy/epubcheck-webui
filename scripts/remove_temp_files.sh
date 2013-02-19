#!/bin/bash

WEBTMP=/home/ec2-user/src/daisy-pipeline.epubcheck/epubcheck-web/tmp/
echo "Removing files older than 10 minutes in $WEBTMP"
sudo find $WEBTMP -maxdepth 1 -mmin +10  -exec rm -f {} \;
