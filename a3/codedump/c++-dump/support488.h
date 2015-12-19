#ifndef _SUPPORT488_H_
#define _SUPPORT488_H_

#pragma once

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
		for(auto& e : entries) e = TElement();
	}

	TElement& operator[](int index)
	{
		return entries.at(index - LowerBound);
	}
};

#endif