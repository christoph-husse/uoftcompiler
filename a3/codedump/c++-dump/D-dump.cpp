#include "support488.h"

void main(int argc, char** argv)
{
    int proc4param3 = int();
    bool proc4param4 = bool();
    std::function<void(int param1)> proc2;
    std::function<bool(bool param1)> func3;
    std::function<void()> proc1;
    proc1 = [&]()
    {
        put("proc1", "\n");
    };
    proc2 = [&](int param1)
    {
        put("proc2", "\n");
    };
    std::function<void(bool param1)> proc3;
    proc3 = [&](bool param1)
    {
        put("proc3", "\n");
    };
    std::function<void(int param1, bool param2, int param3, bool param4)> proc4;
    proc4 = [&](int param1, bool param2, int param3, bool param4)
    {
        put("proc4", "\n");
    };
    std::function<int()> func1;
    func1 = [&]() -> int
    {
        put("func1", "\n");
        return 1;
    };
    std::function<int(int param1)> func2;
    func2 = [&](int param1) -> int
    {
        put("func3 (input was ", param1, ")", "\n");
        return param1;
    };
    func3 = [&](bool param1) -> bool
    {
        if(param1 == true) {
            put("func3 (input was true)", "\n");
        } else {
            put("func3 (input was false)", "\n");
        }

        return param1;
    };
    std::function<int(bool param1, bool param2, int param3, int param4)> func4;
    func4 = [&](bool param1, bool param2, int param3, int param4) -> int
    {
        put("func4", "\n");
        return param3;
    };


    proc1();
    proc2(1);
    proc3(true);
    proc4param3 = 2;
    proc4param4 = false;
    proc4(1, true, proc4param3, proc4param4);
    func1();
    put("Result of func2: ", func2(2), "\n");
    if(func3(true)) {
        put("Call succeeded", "\n");
    } else {
        put("Call failed", "\n");
    }

    put("Result of func4: ", func4(true, false, 3, 4), "\n");
}
