#!/bin/bash

echo
echo "***************************************"
echo "Testing source files from assignment 1."
echo "***************************************"

find ../a1 -name "*.488" -exec echo \; -exec echo "Parsing {}" \; -exec java -jar dist/compiler488.jar {} \;

echo
echo "******************************************"
echo "Testing files with expected syntax errors."
echo "******************************************"

find testcase -name "*.488" -exec echo \; -exec echo "Parsing {} (syntax error expected)" \; -exec java -jar dist/compiler488.jar {} \;
