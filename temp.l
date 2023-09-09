%{
    #include <stdio.h>
    #include "temp.tab.h"
%}

DIGIT [0-9]
INTEGER {DIGIT}+
IDENTIFIER [a-zA-Z_][a-zA-Z0-9_]*

%%

[ \t\n\r] {}
 
"//".* {}  

"&&" { 
    yylval.chararray = strdup(yytext);
    return ANDAND;
}

"||" {
    yylval.chararray = strdup(yytext);  
    return OROR;
}

"!=" {
    yylval.chararray = strdup(yytext);
    return NOTEQ;
}

"<=" {
    yylval.chararray = strdup(yytext);
    return LESSEQ;
}

"+" {
    yylval.chararray = strdup(yytext);
    return PLUS;
}

"-" {
    yylval.chararray = strdup(yytext);
    return MINUS;
}

"*" {
    yylval.chararray = strdup(yytext);
    return MUL;
}

"/" {
    yylval.chararray = strdup(yytext); 
    return DIV;
}

"(" {yylval.chararray = strdup(yytext); return LCBRACKET;}

")" {yylval.chararray = strdup(yytext); return RCBRACKET;}

"{" {yylval.chararray = strdup(yytext); return LFBRACKET;}

"}" {yylval.chararray = strdup(yytext); return RFBRACKET;}

"[" { yylval.chararray = strdup(yytext); return LSBRACKET;}

"]" { yylval.chararray = strdup(yytext);return RSBRACKET;}

";" { yylval.chararray = strdup(yytext);return SEMICOLON;}

"!" { yylval.chararray = strdup(yytext);return EXCLAMATION;}

"." { yylval.chararray = strdup(yytext);return DOT;}

"," { yylval.chararray = strdup(yytext);return COMMA;}

"=" { yylval.chararray = strdup(yytext);return EQUALS;}

"int" { yylval.chararray = strdup(yytext);return INT;}

"boolean" { yylval.chararray = strdup(yytext);return BOOLEAN;}

"System.out.println" {yylval.chararray = strdup(yytext); return PRINTLINE;}

"true" {yylval.chararray = strdup(yytext); return TRUE;}

"false" {yylval.chararray = strdup(yytext); return FALSE;}

"if" {yylval.chararray = strdup(yytext); return IF;}

"else" {yylval.chararray = strdup(yytext); return ELSE;}

"while" {yylval.chararray = strdup(yytext); return WHILE;}

"do" {yylval.chararray = strdup(yytext); return DO;}

"this" {yylval.chararray = strdup(yytext); return THIS;}

"length" {yylval.chararray = strdup(yytext); return LENGTH;}

"new" {yylval.chararray = strdup(yytext); return NEW;}

"class" {
    yylval.chararray = strdup(yytext);
    return CLASS;
}

"public" {yylval.chararray = strdup(yytext); return PUBLIC;}

"static" {yylval.chararray = strdup(yytext); return STATIC;}

"void" {yylval.chararray = strdup(yytext); return VOID;}

"main" {yylval.chararray = strdup(yytext); return MAIN;}

"String" {yylval.chararray = strdup(yytext); return STRING;}

"extends" {yylval.chararray = strdup(yytext); return EXTENDS;}

"#define" {yylval.chararray = strdup(yytext); return DEFINE;}

"return" {yylval.chararray = strdup(yytext); return RETURN;}

{IDENTIFIER} {
     yylval.chararray = strdup(yytext);
    return IDENTIFIER;
}

{INTEGER} {
    yylval.chararray = strdup(yytext);
     return INTEGER;
}

.       { printf("Failed to parse the code.\n"); exit(1); } 
