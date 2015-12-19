SOURCES= \
	Makefile \
	templateRoutines.txt \
	templateStatements.txt \
	templateExpressions.txt \
	templateOther.txt \
	templateStorage.txt \
	A4samples/*.machine
DOC=README
PACKAGE=csc488h.A4.08.tar
TAR=tar -cvf

submission: $(PACKAGE)

$(PACKAGE):
	$(TAR) $(PACKAGE) $(SOURCES) $(DOC)

clean:
	rm -rf $(PACKAGE)
