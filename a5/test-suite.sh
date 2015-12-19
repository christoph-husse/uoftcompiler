#!/bin/bash

# timeout in seconds
TIMEOUT=5
TEST_DIR=testing/pass
LOG_DIR=testing/log

mkdir -p ${LOG_DIR}
TOTAL=0
PASS=0
for f in ${TEST_DIR}/*.488 
do
	TEMP=${f:0:-4}.log
	TOTAL=$((TOTAL+1))
	OUTPUT_FILE=${LOG_DIR}/${TEMP##*/}
	( java -jar dist/compiler488.jar -i ${f} \
		-cpp --validate > ${OUTPUT_FILE} ) & PID=$!
	( sleep $TIMEOUT && kill -HUP $PID ) 2>/dev/null & WATCHER=$!
	wait $PID 2>/dev/null && pkill -HUP -P $WATCHER
	LAST_LINE=`tail -n 1 ${OUTPUT_FILE}`
	if [ -n "`echo $LAST_LINE | grep identical`" ]; then
		echo "[PASS] ${f}"
		rm -rf ${OUTPUT_FILE}
		PASS=$((PASS+1))
	elif [ -n "`echo $LAST_LINE | grep semantic | grep FATAL`" ]; then
		echo "[SEMANTIC] ${f} (output saved: ${OUTPUT_FILE})"
	elif [ -n "`echo $LAST_LINE | grep STACKTRACE`" ]; then
		echo "[TIMEOUT] ${f} (output saved: ${OUTPUT_FILE})"
	else
		echo "[FAIL] ${f} (output saved: ${OUTPUT_FILE})"
	fi
done
echo
echo "Summary:"
echo "Pass: ${PASS}"
echo "Fail: $((TOTAL-PASS))" 
echo "Total: ${TOTAL}"
echo
