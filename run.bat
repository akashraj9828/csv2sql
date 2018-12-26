@echo off
del a.class
REM javac a.java
javac -Xlint a.java
java -verbose a

REM code out.sql