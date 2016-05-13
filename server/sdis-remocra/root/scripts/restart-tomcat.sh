#!/bin/sh
# Redémarrage de Tomcat

/sbin/service tomcat6 restart > /dev/null

/bin/sleep 10

/sbin/service httpd reload > /dev/null

