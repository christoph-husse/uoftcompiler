DOT=dot
J=java
JFLAGS=-jar
JAR=dist/compiler488.jar

SOURCES=$(wildcard testing/pass/*.488)
DOTS=$(SOURCES:.488=.dot)
PDFS=$(DOTS:.dot=.pdf)
GRAPHDIR=graphs

default:
	@echo "Use ant to build the compiler. This makefile builds AST graphs."

graphs: $(PDFS)
	mkdir -p $(GRAPHDIR)
	mv $(PDFS) $(GRAPHDIR)

dots: $(DOTS)

%.dot: %.488
	$(J) $(JFLAGS) $(JAR) -X -i $^ -o $@ -f=dot -v

%.pdf: %.dot
	$(DOT) -T pdf -o $@ $^

cleandots:
	@echo "Removing intermediate PDF generation files."
	rm -rf $(DOTS) $(PDFS)

cleangraphs:
	rm -rf $(GRAPHDIR) $(PDFS) $(DOTS)

