%{
    #include<stdio.h>
    #include<stdlib.h>
    #include<string.h>

    int number_of_macros=0;
    
    struct args { char*arg_name; };

    struct macros
    {
        char*macro_name;
        struct args macro_args[5000]; //Maximum number of arguments for a macro are 5000
        char*macro_func;
        int number_of_args;
    };

    struct macros macro_list[5000];

    int check_the_macro(char*id)
    {
        for(int i=0;i<number_of_macros;i++)
        {
            if(strcmp(macro_list[i].macro_name,id)==0)
            {
                return 1; //return true if macro is already defined
            }
        }
        return 0; //return false if macro is not defined
    }

    void store_the_macro(char*id,char*args_list,char*func)
    {
        if(check_the_macro(id)==1)
        {
            printf("\"%s\" macro is already defined\n",id);
            exit(1);
        }
        else
        {
            macro_list[number_of_macros].macro_name = (char*)malloc(strlen(id) + 1);
            strcpy(macro_list[number_of_macros].macro_name, id);
            int number_of_Args = 0; int arg_list_index = 0; int arg_index; 
            while(arg_list_index < strlen(args_list))
            {
                arg_index = 0;
                char*arg = (char*)malloc(sizeof(char)*(strlen(args_list))+1);
                while(args_list[arg_list_index] != ',' && arg_list_index < strlen(args_list))
                {
                    arg[arg_index] = args_list[arg_list_index];
                    arg_index++; arg_list_index++;
                }
                arg[arg_index] = '\0'; arg_list_index++;
                macro_list[number_of_macros].macro_args[number_of_Args].arg_name = (char*)malloc(sizeof(char)*strlen(arg));
                strcpy(macro_list[number_of_macros].macro_args[number_of_Args].arg_name,arg);
                number_of_Args++;
                free(arg);
            }
            macro_list[number_of_macros].number_of_args = number_of_Args;
            macro_list[number_of_macros].macro_func = (char*)malloc(strlen(func) + 1);
            strcpy(macro_list[number_of_macros].macro_func, func);
            number_of_macros++;
        }
    }

    char*replacement_string(char*function,struct args*org_Args,struct args*args,int number_of_Arguments)
    {
        int length = 0;
        while(length != number_of_Arguments)
        {
            int len_func = strlen(function);
            int len_arg = strlen(args[length].arg_name);
            int index = (char*)strstr(function,args[length].arg_name) - function;
            char*first_part = (char*)malloc(sizeof(char)*(index)+1);
            strncpy(first_part, function, index);
            first_part[index]= '\0';
            int length_second = len_func-(len_arg+index);
            char*next_part = (char*)malloc(sizeof(char)*(length_second+1));
            strncpy(next_part,function+index+len_arg,length_second);
            next_part[length_second] = '\0';
            function = (char*)malloc(sizeof(char)*(strlen(first_part)+strlen(org_Args[length].arg_name)+strlen(next_part)+1));
            strcpy(function,first_part);
            strcat(function,org_Args[length].arg_name);
            strcat(function,next_part);
            length++;
        }
        return function;
    }

    char* replace_the_macro(char*id,char*args_list)
    {
        for(int i=0;i<number_of_macros;i++)
        {
            if(strcmp(macro_list[i].macro_name,id)==0)
            {
                char*function = macro_list[i].macro_func;
                struct args org_args[macro_list[i].number_of_args+1];
                int number_of_args = 0; int arg_list_index = 0; int arg_index; 
                while(arg_list_index < strlen(args_list))
                {
                    arg_index = 0;
                    char*arg = (char*)malloc(sizeof(char)*(strlen(args_list))+1);
                    while(args_list[arg_list_index] != ',' && arg_list_index < strlen(args_list))
                    {
                        arg[arg_index] = args_list[arg_list_index];
                        arg_index++; arg_list_index++;
                    }
                    arg[arg_index] = '\0'; arg_list_index++;
                    org_args[number_of_args].arg_name = (char*)malloc(sizeof(char)*strlen(arg));
                    strcpy(org_args[number_of_args].arg_name,arg);
                    number_of_args++;
                    free(arg);
                }
                if(number_of_args == macro_list[i].number_of_args)
                {
                    return replacement_string(function,org_args,macro_list[i].macro_args,macro_list[i].number_of_args);
                }
                else
                {
                    printf("Number of arguments are not matching to the Macro specified %s\n",id);
                    exit(1); 
                }
                return NULL;
            }
        }
        printf("\"%s\" macro is not defined\n"); //improve
        return NULL;
    }
%}

%union
{
    char* chararray;
}

%token IDENTIFIER INTEGER WHILE EXCLAMATION INT BOOLEAN EXTENDS COMMA OPERATOR THIS FALSE RETURN RCBRACKET 
%token SEMICOLON IF ELSE DO TRUE CLASS PUBLIC STATIC VOID MAIN STRING ANDAND OROR NOTEQ LESSEQ PLUS LCBRACKET 
%token LENGTH NEW PRINTLINE LSBRACKET RSBRACKET LFBRACKET RFBRACKET EQUALS MINUS MUL DIV DOT DEFINE

%type <chararray> Goal MacroDef MainClass TypeDec TypeDeclaration MethodDec MethodDeclaration IDdec ArgumentList 
%type <chararray> MoreArg ListStmt Type Statement MacroStmt NextMacroStmt Expression PrimaryExpression ListId 
%type <chararray> NextListIDs MacroDefinition MacroDefExpression MacroDefStatement Identifier Integer
%type <chararray> SEMICOLON IF ELSE DO WHILE EXCLAMATION INT BOOLEAN EXTENDS COMMA OPERATOR THIS FALSE RETURN
%type <chararray> LENGTH NEW PRINTLINE TRUE CLASS PUBLIC STATIC VOID MAIN STRING ANDAND OROR NOTEQ LESSEQ PLUS INTEGER
%type <chararray> LCBRACKET RCBRACKET LSBRACKET RSBRACKET LFBRACKET RFBRACKET EQUALS MINUS MUL DIV DOT DEFINE IDENTIFIER

%%
    Goal : MacroDef MainClass TypeDec
    {
        $$ = (char*)malloc(sizeof(char)*(strlen($1)+strlen($2)+strlen($3)+1));
        $$[0]='\0';
        strcat($$,$2);
        strcat($$,$3);
        printf("%s\n",$$);
        //debug();
    }
    ;

    MacroDef : MacroDefinition MacroDef 
        {
            sprintf($$,"%s %s", $1,$2);
        }
      | 
        { 
            $$ = "";
        }
    ;

    TypeDec : TypeDeclaration TypeDec 
    {
        sprintf($$,"%s %s", $1,$2);
    }
    |   
    {   
        $$ = "";
    }  
    ;

    MainClass : CLASS Identifier LFBRACKET PUBLIC STATIC VOID MAIN LCBRACKET STRING LSBRACKET RSBRACKET Identifier RCBRACKET LFBRACKET PRINTLINE LCBRACKET Expression RCBRACKET SEMICOLON RFBRACKET RFBRACKET
    {
        sprintf($$,"%s %s %s\n%s %s %s %s %s %s %s %s %s %s %s\n%s %s %s %s\n%s\n%s\n%s\n", $1,$2,$3,$4,$5,$6,$7,$8,$9,$10,$11,$12,$13,$14,$15,$16,$17,$18,$19,$20,$21);
    }
    ;

    TypeDeclaration : CLASS Identifier LFBRACKET IDdec MethodDec RFBRACKET {sprintf($$,"%s %s %s\n%s %s\n%s\n", $1,$2,$3,$4,$5,$6);}                     
                    | CLASS Identifier EXTENDS Identifier LFBRACKET IDdec MethodDec RFBRACKET  
                    {
                        sprintf($$,"%s %s %s %s %s\n%s %s\n%s\n", $1,$2,$3,$4,$5,$6,$7,$8);
                    }         
                    ;

    MethodDec : MethodDeclaration MethodDec
    {
        sprintf($$,"%s %s", $1,$2);
    }| { $$ = ""; }
    ;

    MethodDeclaration : PUBLIC Type Identifier LCBRACKET ArgumentList RCBRACKET LFBRACKET IDdec ListStmt RETURN Expression SEMICOLON RFBRACKET 
    {
        sprintf($$,"%s %s %s %s %s %s %s\n%s %s %s %s\n%s\n%s\n", $1,$2,$3,$4,$5,$6,$7,$8,$9,$10,$11,$12,$13);
    }
    ;

    IDdec : Type Identifier SEMICOLON IDdec
    {
        sprintf($$,"%s %s %s\n%s", $1,$2,$3,$4);
    }
    | Statement
     |  { $$ = "";}   
    ;

    ArgumentList : Type Identifier MoreArg
    {
        sprintf($$,"%s %s %s", $1,$2,$3);
    } | { $$ = "";}
    ;
    
    MoreArg : COMMA Type Identifier MoreArg
    {
        sprintf($$,"%s %s %s %s", $1,$2,$3,$4);
    }
     | { $$ = "";}
    ; 

    ListStmt : Statement ListStmt 
    {
        sprintf($$,"%s %s", $1,$2);
    }| { $$ = "";}
    ;

    Type : INT LSBRACKET RSBRACKET {sprintf($$,"%s %s %s", $1,$2,$3);}
         | BOOLEAN {sprintf($$,"%s", $1);}
         | INT {sprintf($$,"%s", $1);}
         | Identifier {sprintf($$,"%s", $1);}
        ;
    
    MacroStmt : Expression NextMacroStmt
        {
            sprintf($$,"%s %s", $1,$2);
        } 
      | { $$ = "";}
    ;

    NextMacroStmt : COMMA Expression NextMacroStmt
    {
        sprintf($$,"%s %s %s", $1,$2,$3);
    } | { $$ = "";}
    ;

    Statement : LFBRACKET ListStmt RFBRACKET {sprintf($$,"%s\n %s\n%s\n", $1,$2,$3);}
              | PRINTLINE LCBRACKET Expression RCBRACKET SEMICOLON {sprintf($$,"%s %s %s %s %s\n", $1,$2,$3,$4,$5);}
              | Identifier EQUALS Expression SEMICOLON {sprintf($$,"%s %s %s %s\n", $1,$2,$3,$4);} //check here prin statement   
              | Identifier LSBRACKET Expression RSBRACKET EQUALS Expression SEMICOLON {sprintf($$,"%s %s %s %s %s %s %s\n", $1,$2,$3,$4,$5,$6,$7);}
              | IF LCBRACKET Expression RCBRACKET Statement {sprintf($$,"%s %s %s %s %s", $1,$2,$3,$4,$5);}
              | IF LCBRACKET Expression RCBRACKET Statement ELSE Statement {sprintf($$,"%s %s %s %s %s %s %s", $1,$2,$3,$4,$5,$6,$7);}
              | DO Statement WHILE LCBRACKET Expression RCBRACKET SEMICOLON {sprintf($$,"%s %s %s %s %s %s %s\n", $1,$2,$3,$4,$5,$6,$7);}
              | WHILE LCBRACKET Expression RCBRACKET Statement {sprintf($$,"%s %s %s %s %s", $1,$2,$3,$4,$5);}
              | Identifier LCBRACKET MacroStmt RCBRACKET SEMICOLON 
              {
                char*arguments = (char*)malloc(sizeof(char)*(strlen($3)+1));
                arguments[0]='\0';
                sprintf(arguments ,"%s",$3);

                char*macro_id = (char*)malloc(sizeof(char)*(strlen($1)+1));
                macro_id[0]='\0';
                sprintf(macro_id,"%s",$1);

                char*replaced_macro = replace_the_macro(macro_id,arguments);
                sprintf($$,"%s",replaced_macro);
              }
              ;
 
    Expression : PrimaryExpression ANDAND PrimaryExpression {sprintf($$,"%s %s %s", $1,$2,$3);}
                | PrimaryExpression OROR PrimaryExpression {sprintf($$,"%s %s %s", $1,$2,$3);}
                | PrimaryExpression NOTEQ PrimaryExpression {sprintf($$,"%s %s %s", $1,$2,$3);}
                | PrimaryExpression LESSEQ PrimaryExpression {sprintf($$,"%s %s %s", $1,$2,$3);}
                | PrimaryExpression PLUS PrimaryExpression {sprintf($$,"%s %s %s", $1,$2,$3);}
                | PrimaryExpression MINUS PrimaryExpression {sprintf($$,"%s %s %s", $1,$2,$3);}
                | PrimaryExpression MUL PrimaryExpression {sprintf($$,"%s %s %s", $1,$2,$3);}
                | PrimaryExpression DIV PrimaryExpression {sprintf($$,"%s %s %s", $1,$2,$3);}
                | PrimaryExpression LSBRACKET PrimaryExpression RSBRACKET {sprintf($$,"%s %s %s %s", $1,$2,$3,$4);}
                | PrimaryExpression DOT LENGTH {sprintf($$,"%s %s %s", $1,$2,$3);}
                | PrimaryExpression {sprintf($$,"%s", $1);}
                | PrimaryExpression DOT Identifier  LCBRACKET MacroStmt RCBRACKET {sprintf($$,"%s %s %s %s %s %s", $1,$2,$3,$4,$5,$6);}
                | Identifier LCBRACKET MacroStmt RCBRACKET 
                 {
                    char*arguments = (char*)malloc(sizeof(char)*(strlen($3)+1));
                    arguments[0]='\0';
                    sprintf(arguments ,"%s",$3);

                    char*macro_id = (char*)malloc(sizeof(char)*(strlen($1)+1));
                    macro_id[0]='\0';
                    sprintf(macro_id,"%s",$1);

                    char*replaced_macro = replace_the_macro(macro_id,arguments);
                    sprintf($$,"%s",replaced_macro);
                    //sprintf($$,"%s %s %s %s", $1,$2,$3,$4);
                 }
                ;
    
    PrimaryExpression : Integer 
                        {
                            sprintf($$,"%s", $1);}
                      | TRUE {sprintf($$,"%s", $1);}
                      | FALSE {sprintf($$,"%s", $1);}
                      | Identifier {sprintf($$,"%s", $1);}
                      | THIS {sprintf($$,"%s", $1);}
                      | NEW INT LSBRACKET Expression RSBRACKET {sprintf($$,"%s %s %s %s %s", $1,$2,$3,$4,$5);} 
                      | NEW Identifier LCBRACKET RCBRACKET 
                      {
                            sprintf($$,"%s %s %s %s", $1,$2,$3,$4);
                      }
                      | EXCLAMATION Expression {sprintf($$,"%s %s", $1,$2);}
                      | LCBRACKET Expression RCBRACKET {sprintf($$,"%s %s %s", $1,$2,$3);}
                      ;
    
    ListId : Identifier NextListIDs {sprintf($$,"%s %s", $1,$2);}
    | { $$ = "";} 
    ;

    NextListIDs : COMMA Identifier NextListIDs
    {
        sprintf($$,"%s %s %s", $1,$2,$3);
    }
     | { $$ = "";}
    ;

    MacroDefinition : MacroDefExpression  { sprintf($$,"%s", $1);}
     | MacroDefStatement  {sprintf($$,"%s", $1);}
    ;

    MacroDefStatement : DEFINE Identifier LCBRACKET ListId RCBRACKET LFBRACKET ListStmt RFBRACKET
    {
        char*id = (char*)malloc(sizeof(char)*(strlen($2)+1));
        id[0]='\0'; 
        sprintf(id,"%s",$2);

        char*listid = (char*)malloc(sizeof(char)*(strlen($4)+1));
        listid[0]='\0';
        sprintf(listid,"%s",$4);

        char*liststmt = (char*)malloc(sizeof(char)*(strlen($7)+1));
        liststmt[0]='\0';
        sprintf(liststmt,"%s",$7);

        store_the_macro(id,listid,liststmt);
    }
    ;

    MacroDefExpression : DEFINE Identifier LCBRACKET ListId RCBRACKET LCBRACKET Expression RCBRACKET
    {
        char*id = (char*)malloc(sizeof(char)*(strlen($2)+1));
        //id[0]='\0'; 
        sprintf(id,"%s",$2);

        char*listid = (char*)malloc(sizeof(char)*(strlen($4)+1));
        //listid[0]='\0';
        sprintf(listid,"%s",$4);

        char*liststmt = (char*)malloc(sizeof(char)*(strlen($7)+3));
        //liststmt[0]='\0';
        sprintf(liststmt,"( %s )",$7);

        store_the_macro(id,listid,liststmt);
    }
    
    ;
                        
    Identifier : IDENTIFIER 
    {
        sprintf($$, "%s", $1);
        //printf("the identifier is from identify is %s\n",$1);
    }
    ;

    Integer : INTEGER {
        sprintf($$, "%s", $1);
    }
    ;
 
%%

int main(int argc,char**argv)
{
    yyparse();
    return 0;
}

void yyerror(char *s)
{
   fprintf(stderr,"error : %s\n",s);
}