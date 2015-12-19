SOURCES=A.488 B.488 C.488 D.488 E.488
DOC=README
PACKAGE=csc488h.A1.08.tar
TAR=tar -cvf

submission: $(PACKAGE)

$(PACKAGE):
	$(TAR) $(PACKAGE) $(SOURCES) $(DOC)

clean:
	rm -rf $(PACKAGE)
