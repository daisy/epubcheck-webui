EPUB Check Web UI
=================

## Overview

This web-based validator, built around the open source tool [epubcheck](http://code.google.com/p/epubcheck/), validates EPUB files, version 2.0 and later. It is notably deployed at [http://validator.idpf.org/](http://validator.idpf.org/).

The interface allows the upload of one file at a time with a limit of 10MB per file.  If validation fails, a list of errors is shown.

## Installation on Amazon EC2

### Setup

Create an [AWS](http://aws.amazon.com) account and add an EC2 instance.  The free 'micro' instance is sufficient for testing purposes.

Log in to your instance via SSH.

Ensure that the following are installed:
 
 * Git client
 * Java runtime 6

Install the play framework on your EC2 instance:

``` 
$ wget http://download.playframework.org/releases/play-1.2.4.zip
$ unzip
```

Retrieve the web-based EPUB Check code:

``` 
$ git clone https://github.com/daisy-consortium/epubcheck-webui.git
```
 
This code includes the web UI plus a prebuilt version of EPUBCheck and all its required libraries.

Tip: It could be helpful to alias the 'play' command on your server by adding play-1.2.4 to your path.

Verify that the application works in local mode:

``` 
$ cd daisy-pipeline.epubcheck/epubcheck-web
$ play start
$ curl http://localhost:9000
```

Curl should return the raw HTML of the first page of the epubcheck web UI (the file upload form).  If this is what you get, then it works!  If not, check the log in daisy-pipeline.epubcheck/epubcheck-web/logs/application.log. 

### Configure for public use

Now you can configure your appliation for public use.  

Create a server ID by running this command in the server's idpf-epubcheck directory:
$ play id

For our purposes, we'll use 'server01' as the ID.

Find your public and private IP:

```
$ wget http://s3.amazonaws.com/ec2metadata/ec2-metadata
$ chmod 755 ec2-metadata
$ ./ec2-metadata -v
$ ./ec2-metadata -o
```

Edit idpf-epubcheck/conf/application.conf and add the following lines:
 
```
%server01.application.mode=PROD
%server01.http.port=80
%server01.http.address=<YOUR PRIVATE IP>
```

Change this line to point to your public IP:
application.baseUrl=http://107.22.114.55/

Using AWS Security Groups (via the web console), add TCP port 80.

### Run it

Start your application (you must be root to start anything on a port < 1024):

```
$ sudo play start
```

Open your browser, point to your public IP, and see if it works!

## Maintenance

### Restarting the server

```
$ cd daisy-pipeline.epubcheck/epubcheck-web
$ sudo kill `cat server.pid`
$ rm server.pid
$ sudo play start
```

### Tasks
There are two scripts that may be helpful to schedule as cron jobs:

 * `backup_log_files.sh` : move log files into a backup directory
 * `remove_temp_files.sh` : delete temp files older than 10 minutes
 
For example, the first job might run every week. The second could run every day.

Both of the above scripts use 'sudo', and because of this, you have to give ec2-user non-tty sudo permissions. Read [how to do this](http://serverfault.com/questions/111064/sudoers-how-to-disable-requiretty-per-user).

## Troubleshooting

### Error: Could not bind on port 80

 * Did you open port 80 via the AWS Security Groups settings? (see above)
 * Is another process using port 80?  Check with the netstat command

### Too many open files

If this error appears in the log files, it could be because the server settings don't allow for enough open files. See [this description](http://lj4newbies.blogspot.com/2007/04/too-many-open-files.html).  Try setting the limits to 65535.