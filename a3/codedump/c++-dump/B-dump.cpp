#include "support488.h"

void main(int argc, char** argv)
{
    array<int, 0, 4> array1;
    array<int, 2, 5> array2;
    array<int, -3, 1> array3;
    array<int, 0, 5> array4;
    int i = int();
    int arrayEntry = int();


    array1[1] = 1;
    array1[2] = 1;
    array1[3] = 2;
    array1[4] = 3;
    put("array1 index 1: ", array1[1], "\n");
    put("array1 index 2: ", array1[2], "\n");
    put("array1 index 3: ", array1[3], "\n");
    put("array1 index 4: ", array1[4], "\n");
    put("\n");
    array2[2] = 5;
    array2[3] = 8;
    array2[4] = 13;
    array2[5] = 21;
    array3[-3] = 34;
    array3[-2] = 55;
    array3[-1] = 89;
    array3[0] = 144;
    array3[1] = 233;
    put("array2 index 2 (first index): ", array2[2], "\n");
    put("array2 index 3 (second index):", array2[3], "\n");
    put("array2 index 4 (third index):", array2[4], "\n");
    put("array2 index 5 (fourth index):", array2[5], "\n");
    put("array3 index -3 (first index): ", array3[-3], "\n");
    put("array3 index -2 (second index): ", array3[-2], "\n");
    put("array3 index -1 (third index): ", array3[-1], "\n");
    put("array3 index 0 (fourth index): ", array3[0], "\n");
    put("array3 index 1 (fifth index): ", array3[1], "\n");
    put("\n");
    i = 0;
    while(true) 
    {
        i = i + 1;
        put("Next array value? (integer):", "\n");
        get(arrayEntry);
        array4[i] = arrayEntry;
        if(i == 5) goto loopExit_0;
    } loopExit_0:;
    put("\n");
    i = 0;
    while(true) 
    {
        i = i + 1;
        put("array4 index ", i, ": ", array4[i], "\n");
        if(i == 5) goto loopExit_1;
    } loopExit_1:;
}
