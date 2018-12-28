@echo off
cls
del a.class
javac a.java
REM javac -Xlint a.java
REM java -verbose a
java a %1 %2 %3 
REM java a

REM code out.sql