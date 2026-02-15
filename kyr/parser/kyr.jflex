package kyr.parser;

import kyr.exceptions.*;
import java_cup.runtime.*;

%%

%class Lexer
%public

%line
%column

%type Symbol
%eofval{
        return symbol(Symbols.EOF);
%eofval}

%cup

%{
    private StringBuilder string = new StringBuilder();

    private Symbol symbol(int type) {
        return new Symbol(type, yyline, yycolumn);
    }

    private Symbol symbol(int type, Object value) {
        return new Symbol(type, yyline, yycolumn, value);
    }
%}

Digit = [0-9]
Integer = {Digit}+
Boolean = vrai|faux
Identifier = [a-zA-Z_#][a-zA-Z0-9_#]*


LineTerminator = \r|\n|\r\n
WhiteSpace = {LineTerminator} | [ \t\f]

%state STRING

%%

/* String literal */

<YYINITIAL> {
  \"                  { yybegin(STRING); string.setLength(0); }
}

<STRING> {
  \"                  { yybegin(YYINITIAL); return symbol(Symbols.STRING, string.toString()); }

  [^\r\n\"\\]+        { string.append( yytext() ); }
  "\\b"               { string.append( '\b' ); }
  "\\t"               { string.append( '\t' ); }
  "\\n"               { string.append( '\n' ); }
  "\\f"               { string.append( '\f' ); }
  "\\r"               { string.append( '\r' ); }
  "\\\""              { string.append( '\"' ); }
  "\\'"               { string.append( '\'' ); }
  "\\\\"              { string.append( '\\' ); }

  \\.                 { throw new LexicalError("illegal escape sequence in line " + yyline + ", column " + yycolumn); }
  {LineTerminator}    { throw new LexicalError("unterminated string at end of line " + yyline); }
}

"//".*                { /* DO NOTHING */ }

/* Keywords */
"variables"           { return symbol(Symbols.VARIABLES_KW); }
"debut"               { return symbol(Symbols.BEGIN); }
"fin"                 { return symbol(Symbols.END); }
"ecrire"              { return symbol(Symbols.PRINT); }
"entier"              { return symbol(Symbols.INTEGER_KW); }
"booleen"             { return symbol(Symbols.BOOLEAN_KW); }


/* Control flow*/
"si"                  { return symbol(Symbols.IF_KW); }
"alors"               { return symbol(Symbols.THEN_KW); }
"sinon"               { return symbol(Symbols.ELSE_KW); }
"finsi"               { return symbol(Symbols.ENDIF_KW); }

"repeter"             { return symbol(Symbols.REPEAT_KW); }
"jusqua"              { return symbol(Symbols.UNTIL_KW); }

/* Boolean operators */
"et"                  { return symbol(Symbols.AND_OP); }
"ou"                  { return symbol(Symbols.OR_OP); }
"non"                 { return symbol(Symbols.NOT_OP); }

/* Punctuation*/
";"                   { return symbol(Symbols.SEMICOLON); }
","                   { return symbol(Symbols.COMMA); }
"("                   { return symbol(Symbols.LPAREN); }
")"                   { return symbol(Symbols.RPAREN); }

/*Operators*/
/* Multi-char operators must be defined BEFORE single-char ones */
"=="                  { return symbol(Symbols.EQ_OP); }
"!="                  { return symbol(Symbols.NEQ_OP); }
"<="                  { return symbol(Symbols.LE_OP); }
">="                  { return symbol(Symbols.GE_OP); }
"<"                   { return symbol(Symbols.LT_OP); }
">"                   { return symbol(Symbols.GT_OP); }

"="                   { return symbol(Symbols.ASSIGN_OP); }

"+"                   { return symbol(Symbols.PLUS_OP); }
"-"                   { return symbol(Symbols.MINUS_OP); }
"*"                   { return symbol(Symbols.TIMES_OP); }
"/"                   { return symbol(Symbols.DIV_OP); }
"%"                   { return symbol(Symbols.MOD_OP); }

/* Literals and Identifiers */
{Integer}             { return symbol(Symbols.INTEGER, yytext()); }
{Boolean}             { return symbol(Symbols.BOOLEAN, yytext()); }
{Identifier}          { return symbol(Symbols.IDENTIFIER, yytext()); }      // Last position to avoid capturing keywords.


{WhiteSpace}          { }
.                     { throw new LexicalError("line " + yyline + ", column " + yycolumn + ": " + yytext()); }


