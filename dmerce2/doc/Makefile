TEXFILE?=	kompendium

all:	index pdf clean

clean:
	rm -f ${TEXFILE}.out
	rm -f ${TEXFILE}.aux
	rm -f ${TEXFILE}.log

index:
	makeindex ${TEXFILE}.tex

pdf:
	rm -f ${TEXFILE}.toc
	pdflatex ${TEXFILE}.tex
	pdflatex ${TEXFILE}.tex

dvi:
	rm -f ${TEXFILE}.toc
	latex ${TEXFILE}.tex
	latex ${TEXFILE}.tex
