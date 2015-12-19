#include "support488.h"

void main(int argc, char** argv)
{
    int simple1 = int();
    int simple2 = int();
    array<int, 0, 5> complex;


    simple1 = 0;
    simple2 = 0;
    complex[0] = 0;
    complex[1] = 0;
    complex[2] = 0;
    complex[3] = 0;
    complex[4] = 0;
    complex[5] = 0;
    put("Enter Simple Loop 1...", "\n");
    while(true) 
    {
        simple1 = simple1 + 1;
        if(simple1 == 100) {
            goto loopExit_0;
        }

    } loopExit_0:;
    put("Enter Simple Loop 2...", "\n");
    while(true) 
    {
        simple2 = simple2 + 1;
        if(simple2 == 100) goto loopExit_1;
    } loopExit_1:;
    put("Enter Complex Loop...", "\n");
    while(true) 
    {
        complex[0] = complex[0] + 1;
        while(true) 
        {
            complex[1] = complex[1] + 1;
            while(true) 
            {
                complex[2] = complex[2] + 1;
                while(true) 
                {
                    complex[3] = complex[3] + 1;
                    while(true) 
                    {
                        complex[4] = complex[4] + 1;
                        while(true) 
                        {
                            complex[5] = complex[5] + 1;
                            if(complex[5] == 100) goto loopExit_3;
                        } loopExit_2:;
                        put("Can", "I", "show", "this", "message", "?", "\n");
                    } loopExit_3:;
                    if(complex[3] == 100) goto loopExit_4;
                } loopExit_4:;
                goto loopExit_6;
            } loopExit_5:;
            put("Can I show this message?", "\n");
        } loopExit_6:;
        goto loopExit_7;
    } loopExit_7:;
    put("All loops are done.", "\n");
}
