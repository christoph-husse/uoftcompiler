#!/bin/sh

# Example usage:
#
# ./format-instruction.sh a.488

if [[ $# -ne 1 ]]; then
    echo 'Usage: ./format-instruction.sh <instruction file>'
    exit 1
fi

cat "$1" | sed -f ./ins.sed > ./ins.txt
