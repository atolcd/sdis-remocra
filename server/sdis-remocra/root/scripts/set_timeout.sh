
# Mise à jour du timeout remocra minutes / secondes
export newremocratimeoutmin=60
export newremocratimeoutsec=3600
sed -i "s/Sdis\.Remocra\.network\.ClientSession\.setDelaysSec\(.*, 30\)/Sdis\.Remocra\.network\.ClientSession\.setDelaysSec\(${newremocratimeoutsec}, 30\)/g" /var/lib/tomcat6/webapps/remocra/js/app/remocra/App.js
sed -i "s/Sdis\.Remocra\.network\.ClientSession\.setDelaysSec\(.*,30\)/Sdis\.Remocra\.network\.ClientSession\.setDelaysSec\(${newremocratimeoutsec},30\)/g" /var/lib/tomcat6/webapps/remocra/js/all-classes*.js
sed -i "s/<session-timeout>.*<\/session-timeout>/<session-timeout>$newremocratimeoutmin<\/session-timeout>/g" /var/lib/tomcat6/webapps/remocra/WEB-INF/web.xml

service tomcat6 restart
service httpd reload

