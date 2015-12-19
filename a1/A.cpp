#include <iostream>
#include <functional>
#include <array>
#include <string>

static void put() {}

template<typename THead, typename... TTail>
static void put(THead current, TTail... args)
{
	std::cout << current;
	put(args...);
}

static void get() {}

template<typename THead, typename... TTail>
static void get(THead& current, TTail&... args)
{
	std::cin >> current;
	get(args...);
}

template<typename TElement, int LowerBound, int UpperBound>
class array
{
private:
	std::array<TElement, UpperBound - LowerBound + 1> entries;
public:
	array()
	{
		for(auto it = entries.begin(); it != entries.end(); it++) *it = TElement();
	}
	TElement& operator[](int index)
	{
		return entries.at(index - LowerBound);
	}
};

int main(int argc, char** argv) { 
{
    std::function<void(bool yes)> b2str;
    b2str = [&](bool yes)
    {
        if(yes) {
            put("true");
        } else {
            put("false");
        }

        return;
    };
    std::function<void(bool yes)> b2strs;
    b2strs = [&](bool yes)
    {
        b2str(yes);
        put("\n");
        return;
    };
    std::function<int()> one;
    one = [&]() -> int
    {
        return 1;
    };
    int int1 = int();
    int int2 = int();
    bool bool1 = bool();
    bool bool2 = bool();


    int1 = 1;
    int2 = 2;
    bool1 = true;
    bool2 = false;
    put("1 + 2 = ", 1 + 2, "\n");
    put("1 - 1 = ", 1 - one(), "\n");
    put("1 * 2 = ", int1 * int2, "\n");
    put("10 / 2 = ", 10 / int2, "\n");
    put("-1 + 2 = ", -int1 + int2, "\n");
    put("true | false = ");
    b2strs(bool1 | bool2);
    put("true & false = ");
    b2strs(bool1 & bool2);
    put("!true = ");
    b2strs(!bool1);
    put("1 = 2 ? ");
    b2strs(1 == 2);
    put("1 != 2 ? ");
    b2strs(1 != 2);
    put("1 < 2 ? ");
    b2strs(1 < 2);
    put("1 > 2 ? ");
    b2strs(1 > 2);
    put("1 >= 2 ? ");
    b2strs(1 >= 2);
    put("2 >= 2 ? ");
    b2strs(2 >= 2);
    put("1 <= 2 ? ");
    b2strs(1 <= 2);
    put("1 <= 1 ? ");
    b2strs(1 <= 1);
    if((20 / 10 / 2) == 1) {
        put("20 / 10 / 2 = 1 (left associative)", "\n");
    } else {
        put("20 / 10 / 2 = 4 (right associative)", "\n");
    }

    put("(20 / 10) * 2 = ", 20 / 10 * 2, "\n");
    put("(4 - 6) + 2 = ", 4 - 6 + 2, "\n");
    if(-2 * 3 / 2 + 1 - 2 == -4) {
        put("-2 * 3 / 2 + 1 - 2 = (((-2 * 3) / 2) + 1) - 2", "\n");
    } else {
        put("unexpected result", "\n");
    }

    if(1 + 0 == 1 | 0 & 1) {
        put("1 + 0 = 1 | 0 & 1 = ((1 + 0) = 1) | (0 & 1)", "\n");
    } else {
        put("unexpected result", "\n");
    }

}
    return 0;
}
