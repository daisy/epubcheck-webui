# idpf-epubcheck

TODOs for this file:
 * finalize required versions of play and epubcheck

## Overview

This web-based validator, built around the open source tool [http://code.google.com/p/epubcheck/ epubcheck], validates EPUB files, version 2.0 and later.

The interface allows the upload of one file at a time with a limit of 10MB per file.  If validation fails, a list of errors is shown.


## Building

Requirements:

 * [www.playframework.org/ Play! Framework], version 1.2.4
 * Java runtime 6
 * [http://code.google.com/p/daisy-pipeline/source/browse/?repo=sandbox#hg%2Fidpf-epubcheck idpf-epubcheck]
 * [http://code.google.com/p/daisy-pipeline/source/browse/?repo=sandbox#hg%2FEpubcheckBackend EpubcheckBackend]
 * [http://code.google.com/p/epubcheck/downloads/list epubcheck-3.0b3.jar]

Build EpubcheckBackend from the command line and copy the jar into idpf-epubcheck/lib.

The lib folder of idpf-epubcheck must contain the following:
lib/
  commons-compress-1.2.jar
  epubcheckbackend.jar
  flute.jar
  jing.jar
  sac.jar
  saxon9he.jar

This can be accomplished by building the code locally using eclipse.

## Installation on Amazon EC2

Create an [http://aws.amazon.com AWS] account and add an EC2 instance.  The free 'micro' instance is sufficient for testing purposes.

Log in to your instance via SSH.

Install the play framework on your EC2 instance:
 * $ wget http://download.playframework.org/releases/play-1.2.4.zip
 * $ unzip

Transfer your local idpf-epubcheck code, once you've verified it works. 

It could be helpful to alias the 'play' command on your server by adding ~/play-1.2.4 to your path.

Verify that the application works in local mode:
 * $ cd idpf-epubcheck
 * $ play start
 * $ curl http://localhost:9000

Curl should return the raw HTML of the first page of the epubcheck web UI (the file upload form).  If this is what you get, then it works!  If not, check the log in idpf-epubcheck/logs/system.out. 

Now you can configure your appliation for public use.  

Create a server ID by running this command in the server's idpf-epubcheck directory:
$ play id

For our purposes, we'll use 'server01' as the ID.

Find your public and private IP:

 * $ wget http://s3.amazonaws.com/ec2metadata/ec2-metadata
 * $ chmod 755 ec2-metadata
 * $ ./ec2-metadata -v
 * $ ./ec2-metadata -o

Edit idpf-epubcheck/conf/application.conf and add the following lines:
 * %server01.application.mode=PROD
 * %server01.http.port=80
 * %server01.http.address=<YOUR PRIVATE IP>

Change this line to point to your public IP:
application.baseUrl=http://107.22.114.55/

Using AWS Security Groups (via the web console), add TCP port 80.

Start your application (you must be root to start anything on a port < 1024):
$ sudo play start

Open your browser, point to your public IP, and see if it works!

## Troubleshooting

### Error: Could not bind on port 80

 * Did you open port 80?
 * Is another process using port 80?  Check with the netstat command
