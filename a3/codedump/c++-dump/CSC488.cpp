#include "support488.h"

namespace Tests
{
	namespace A { 
		#include "A.cpp" 
	}
	namespace B { 
		#include "B.cpp" 
	}
	namespace C { 
		#include "C.cpp" 
	}
	namespace D { 
		#include "D.cpp" 
	}
	namespace E { 
		#include "E.cpp" 
	}
}

void main(int argc, char** argv)
{
    Tests::A::main(argc, argv);
	Tests::B::main(argc, argv);
	Tests::C::main(argc, argv);
	Tests::D::main(argc, argv);
	Tests::E::main(argc, argv);
}
