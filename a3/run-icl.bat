@echo off
@call "C:\Program Files (x86)\Intel\Composer XE 2013\bin\iclvars.bat" intel64
icl /fp:precise /D "WIN32" /D "_CONSOLE" /D "_UNICODE" /D "UNICODE" /Qstd=c++0x /Zc:forScope /GS /W /MT /O3 %1 /link /SUBSYSTEM:CONSOLE /out:%2