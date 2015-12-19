begin
    integer global
    proc recA()
    begin

        global := global * 13
    end

    proc recB(integer a)
    begin

        recA()
        global := global * a
    end

    integer fib(integer a)
    begin

        if a <= 0 then
            result 1
        fi

        result fib(a - 1) + fib(a - 2)
    end

    integer recC()
    begin

        recB(3)
        recA()
        result global + 546
    end

    integer recD(integer a, integer b)
    begin
        proc recE()
        begin
            proc recF()
            begin

                global := global * 7
                return
            end


            global := global * 27
            recF()
        end

        integer recG()
        begin
            proc recF()
            begin

                global := global * 23
            end


            global := global * 41
            recF()
            result global - (global / 123) * 123
        end


        global := global + recG()
        recE()
        result global * a + a * b * recC()
    end


    if recD(53, 67) + fib(30) != 4117155 then
        put "[Error]: Testcase FAILED"
    else
        put "OK"
    fi

end
