#!/usr/bin/python2

'''A Pseudo Machine Implementation for 488 Course.

    Instruction <- ./ins.txt

    Input <- ./ip.txt

    Output -> stdout

    Error/Info -> stderr

'''

import sys

#   Machine-Dependent Constants

MEMORY_SIZE = 65536
DISPLAY_SIZE = 16
MACHINE_FALSE = 0
MACHINE_TRUE = 1
INT_MIN = -32767
INT_MAX = 32767
UNDEFINED = 1 << 30

#   For convenience in setting StartMSP.
INSTRUCTION_SIZE = 10000

#   Machine-Dependent Variables
memory = None
display = None
pc = None
mlp = None
msp = None

#   Instructions
ins = []

#   Inputs
ip = ''
ip_ix = 0

#   Misc
enable_log_ins = True

################################################################

#   Machine-Wide operations
def init():
    global memory
    global display
    global pc
    global mlp
    global msp
    global ins
    
    memory = [UNDEFINED] * MEMORY_SIZE
    display = [0] * DISPLAY_SIZE
    pc = 0
    mlp = MEMORY_SIZE / 3 * 2
    msp = INSTRUCTION_SIZE

def load_ins(filename):
    global memory
    global display
    global pc
    global mlp
    global msp
    global ins
    
    with open(filename, 'r') as f:
        ins = f.readlines()
    if (len(ins) > INSTRUCTION_SIZE):
        log_msg('Instruction size is too large.')
        exit(1)
    for i in range(len(ins)):
        ins[i] = ins[i].strip()
        memory[i] = ins[i]

def load_ip(filename):
    global memory
    global display
    global pc
    global mlp
    global msp
    global ip
    global ip_ix
    
    with open(filename, 'r') as f:
        ip = f.readline()
    ip_ix = 0

def dump_ins():
    global memory
    global display
    global pc
    global mlp
    global msp
    global ins
    
    for i in ins:
        log_msg(str(i))

def log_msg(s):
    sys.stderr.write(s + '\n')

def run_ins():
    global memory
    global display
    global pc
    global mlp
    global msp
    global ins
    global enable_log_ins
    
    while True:
        if (enable_log_ins):
            log_msg('{:04d} : {}'.format(pc, memory[pc]))
        if (memory[pc].find(' ') != -1):
            operator, operands = memory[pc].split(' ', 1)
        else:
            operator, operands = memory[pc], None
        run_table[operator](operands)
        if (operator == 'HALT'):
            break
        elif (operator in ['BR', 'BF']):
            pass
        else:
            pc = pc + 1

################################################################

#   Pseudo Instructions
def __push(v):
    global memory
    global display
    global pc
    global mlp
    global msp
    
    memory[msp] = v
    msp = msp + 1


def __pop():
    global memory
    global display
    global pc
    global mlp
    global msp
    
    msp = msp - 1

def __top():
    global memory
    global display
    global pc
    global mlp
    global msp
    
    return memory[msp - 1]

#   Instructions
def _addr(operands):
    global memory
    global display
    global pc
    global mlp
    global msp
    
    ll, on = operands.split(' ')
    __push(display[int(ll)] + int(on))


def _load(operands):
    global memory
    global display
    global pc
    global mlp
    global msp
    
    a = __top()
    __pop()
    if (memory[a] == UNDEFINED):
        log_msg('Error: load')
        exit(1)
    else:
        __push(memory[a])

def _store(operands):
    global memory
    global display
    global pc
    global mlp
    global msp
    
    v = __top()
    __pop()
    a = __top()
    __pop()
    memory[a] = v

def _push(operands):
    global memory
    global display
    global pc
    global mlp
    global msp
    
    if (operands == 'MACHINE_TRUE'):
        __push(MACHINE_TRUE)
    elif (operands == 'MACHINE_FALSE'):
        __push(MACHINE_FALSE)
    elif (operands == "'\\0'"):
        __push(0)
    elif (len(operands) == 3 and operands[0] == "'" and operands[2] == "'"):
        __push(ord(operands[1]))
    elif (operands.isdigit()):
        __push(int(operands))
    elif (operands[0] == '-' and operands[1:].isdigit()):
        __push(int(operands))
    else:
        log_msg('Error:push')
        exit(1)
    
def _pushmt(operands):
    global memory
    global display
    global pc
    global mlp
    global msp
    
    __push(msp)

def _setd(operands):
    global memory
    global display
    global pc
    global mlp
    global msp
    
    ll = int(operands)
    display[ll] = __top()
    __pop()

def _popn(operands):
    global memory
    global display
    global pc
    global mlp
    global msp
    
    n = __top()
    __pop()
    for i in range(n):
        __pop()

def _pop(operands):
    global memory
    global display
    global pc
    global mlp
    global msp
    
    __pop()

def _dupn(operands):
    global memory
    global display
    global pc
    global mlp
    global msp
    
    n = __top()
    __pop()
    v = __top()
    __pop()
    for i in range(n):
        __push(v)

def _dup(operands):
    global memory
    global display
    global pc
    global mlp
    global msp
    
    __push(__top())

def _br(operands):
    global memory
    global display
    global pc
    global mlp
    global msp
    
    a = __top()
    __pop()
    pc = a

def _bf(operands):
    global memory
    global display
    global pc
    global mlp
    global msp
    
    a = __top()
    __pop()
    v = __top()
    __pop()
    if (v == MACHINE_FALSE):
        pc = a

def _neg(operands):
    global memory
    global display
    global pc
    global mlp
    global msp
    
    #   Cannot use __top() here.
    memory[msp - 1] = - memory[msp - 1]

def _add(operands):
    global memory
    global display
    global pc
    global mlp
    global msp
    
    y = __top()
    __pop()
    x = __top()
    __pop()
    n = x + y
    if (n > INT_MAX or n < INT_MIN):
        log_msg('Error:add')
        exit(1)
    else:
        __push(n)

def _sub(operands):
    global memory
    global display
    global pc
    global mlp
    global msp
    
    y = __top()
    __pop()
    x = __top()
    __pop()
    n = x - y
    if (n > INT_MAX or n < INT_MIN):
        log_msg('Error:sub')
        exit(1)
    else:
        __push(n)

def _mul(operands):
    global memory
    global display
    global pc
    global mlp
    global msp
    
    y = __top()
    __pop()
    x = __top()
    __pop()
    n = x * y
    if (n > INT_MAX or n < INT_MIN):
        log_msg('Error:sub')
        exit(1)
    else:
        __push(n)

def _div(operands):
    global memory
    global display
    global pc
    global mlp
    global msp
    
    y = __top()
    __pop()
    x = __top()
    __pop()
    if (y == 0):
        log_msg('Error:div (0-division)')
        exit(1)
    else:
        n = x / y
        if (n > INT_MAX or n < INT_MIN):
            log_msg('Error:div')
            exit(1)
        else:
            __push(n)

def _eq(operands):
    global memory
    global display
    global pc
    global mlp
    global msp
    
    y = __top()
    __pop()
    x = __top()
    __pop()
    if (x == y):
        __push(MACHINE_TRUE)
    else:
        __push(MACHINE_FALSE)

def _lt(operands):
    global memory
    global display
    global pc
    global mlp
    global msp
    
    y = __top()
    __pop()
    x = __top()
    __pop()
    if (x < y):
        __push(MACHINE_TRUE)
    else:
        __push(MACHINE_FALSE)

def _or(operands):
    global memory
    global display
    global pc
    global mlp
    global msp
    
    y = __top()
    __pop()
    x = __top()
    __pop()
    if (x == MACHINE_TRUE or y == MACHINE_TRUE):
        __push(MACHINE_TRUE)
    else:
        __push(MACHINE_FALSE)

def _swap(operands):
    global memory
    global display
    global pc
    global mlp
    global msp
    
    x = __top()
    __pop()
    y = __top()
    __pop()
    __push(x)
    __push(y)

def _readc(operands):
    global memory
    global display
    global pc
    global mlp
    global msp
    global ip
    global ip_ix
    
    __push(ord(ip[ip_ix]))
    ip_ix += 1

def _printc(operands):
    global memory
    global display
    global pc
    global mlp
    global msp
    
    print chr(__top())
    __pop()

def _readi(operands):
    global memory
    global display
    global pc
    global mlp
    global msp
    global ip
    global ip_ix

    s = ''
    while (ip[ip_ix].isdigit()):
        s = s + ip[ip_ix]
        ip_ix += 1
    __push(int(s))

def _printi(operands):
    global memory
    global display
    global pc
    global mlp
    global msp

    print __top()
    __pop()
    

def _halt(operands):
    global memory
    global display
    global pc
    global mlp
    global msp
    
    pass

#   Mapping from instruction name to instruction
run_table = {
    'ADDR' : _addr, 
    'LOAD' : _load, 
    'STORE' : _store, 
    'PUSH' : _push, 
    'PUSHMT' : _pushmt, 
    'SETD' : _setd, 
    'POPN' : _popn, 
    'POP' : _pop, 
    'DUPN' : _dupn, 
    'DUP' : _dup, 
    'BR' : _br, 
    'BF' : _bf, 
    'NEG' : _neg, 
    'ADD' : _add, 
    'SUB' : _sub, 
    'MUL' : _mul, 
    'DIV' : _div, 
    'EQ' : _eq, 
    'LT' : _lt, 
    'OR' : _or, 
    'SWAP' : _swap, 
    'READC' : _readc, 
    'PRINTC' : _printc, 
    'READI' : _readi, 
    'PRINTI' : _printi, 
    'HALT' : _halt, 
}

def main():
    init()
    load_ins('ins.txt')
    load_ip('ip.txt')
    #dump_ins()
    run_ins()

if __name__ == '__main__':
    main()
