#include "stdio.h"
#include "stdint.h"

int64_t global;

void recA()
{
  global = global * 13;
}

int64_t fib(int64_t a)
{
  if(a <= 0)
    return 1;

  return fib(a - 1) + fib(a - 2);
}

void recB(int64_t a)
{
  recA();
  global = global * a;
}

int64_t recC()
{
  recB(3);
  recA();
  return global + 546;
}

int64_t recD(int64_t a, int64_t b)
{
  auto recE = [&]()
  {
    auto recF = [&]()
    {
      global = global * 7;
      return;
    };
  
    global = global * 27;
    recF();
  };

  auto recG = [&]() -> int64_t
  {
    auto recF = [&]()
    {
      global = global * 23;
    };
    
    global = global * 41;
    recF();
    return global - (global / 123) * 123;
  };
  
  global = global + recG();
  recE();
  return global * a + a * b * recC();
}

int main(int argc, char** argv)
{ 
  printf("\nResult = %ld\n\n", recD(53, 67) + fib(30));
  return 0;
}
