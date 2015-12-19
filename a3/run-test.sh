#!/bin/sh

find ./testing/pass -name "*.488" | xargs -n 1 -I {} sh -c "echo {} ; ./RUNCOMPILER.sh {}"
find ./testing/fail -name "*.488" | xargs -n 1 -I {} sh -c "echo {} ; ./RUNCOMPILER.sh {}"
