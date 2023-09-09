run:
	bison -d temp.y
	flex temp.l
	gcc temp.tab.c lex.yy.c -o ans -lfl
#	./ans
#	./ans < input > output

clean:
	rm -rf ans temp.tab.c temp.tab.h lex.yy.c output