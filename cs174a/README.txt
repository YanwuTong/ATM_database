javac -d out/ -cp /usr/lib/oracle/19.3/client64/lib/ojdbc8.jar cs174a/*.java

java -cp /usr/lib/oracle/19.3/client64/lib/ojdbc8.jar:out:. cs174a.Main
