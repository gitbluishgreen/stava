#!/bin/bash

#Uncomment this the first time you run it:-

#wget -r -nH --cut-dirs=2 --no-parent --reject="index.html*" https://www.cse.iitm.ac.in/~krishna/cs3300/microIR/

javac P5.java

for file in ./microIR_tests/*
do
	java P5 < $file >dump
	echo "Testing $file"
	java -jar kgi.jar <dump
	echo "Testing done"
	echo "-------------------------"
	rm -rf dump
done
