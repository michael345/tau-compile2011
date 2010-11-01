package IC.Parser;

%%

%cup
%{
  	private StringBuffer curString = new StringBuffer();
  	private int getLine() {return yyline+1;}
%}

%class Lexer
%public
%function next_token
%type Token
%line
%scanerror LexicalError
%state SINGLE_LINE_COMMENTS
%state MULTI_LINE_COMMENTS
%state QUOTE

INTEGERLITERAL=0|[1-9][0-9]*
LOWERALPHA=[a-z]
UPPERALPHA=[A-Z]
ALPHA=[A-Za-z_]
ALPHA_NUMERIC=({ALPHA}|{INTEGERLITERAL})+
CLASS_IDENTIFIER={UPPERALPHA}({ALPHA_NUMERIC})*
IDENTIFIER={LOWERALPHA}({ALPHA_NUMERIC})*
WHITESPACE=[ \t\n\r]

%eofval{
  	return new Token(sym.EOF,getLine());
%eofval}

%%

<SINGLE_LINE_COMMENTS> { 
	[^\n] { }
	\n { yybegin(YYINITIAL); }
}

<MULTI_LINE_COMMENTS>  {
	"*/" { yybegin(YYINITIAL); }
	.|\n { }
	<<EOF>> { throw new LexicalError(getLine(),"Unexpected EOF"); }
}

<QUOTE> { 
	[\"] {
		curString.append(yytext());
		yybegin(YYINITIAL);
	 	return new Token(sym.QUOTE, getLine(), curString.toString()); 
	 }
	.|\n { curString.append(yytext());}
	<<EOF>> { throw new LexicalError(getLine(),"Unexpected EOF"); }
}

<YYINITIAL> {
	 \" { curString.setLength(0);curString.append(yytext()); yybegin(QUOTE); }
	 "//" { yybegin(SINGLE_LINE_COMMENTS); }
	 "(" { return new Token(sym.LP,getLine()); }
	 ")" { return new Token(sym.RP,getLine()); }
	 "/*" { yybegin(MULTI_LINE_COMMENTS); }
	 "{" { return new Token(sym.LCBR,getLine()); }
	 "}" { return new Token(sym.RCBR,getLine()); }
	 "[" { return new Token(sym.LB,getLine()); }
	 "]" { return new Token(sym.RB,getLine()); }
	 "," { return new Token(sym.COMMA,getLine()); }
	 ";" { return new Token(sym.SEMI,getLine()); }
	 "=" { return new Token(sym.ASSIGN,getLine()); }
	 "-" { return new Token(sym.MINUS,getLine()); }
	 "+" { return new Token(sym.PLUS,getLine()); }
	 "/" { return new Token(sym.DIVIDE,getLine()); }
	 "%" { return new Token(sym.MOD,getLine()); }
	 "." { return new Token(sym.DOT,getLine()); }
	 "*" { return new Token(sym.MULTIPLY,getLine()); }
	 ">" { return new Token(sym.GT,getLine()); }
	 "<" { return new Token(sym.LT,getLine()); }
	 ">=" { return new Token(sym.GTE,getLine()); }
	 "<=" { return new Token(sym.LTE,getLine()); }
	 "==" { return new Token(sym.EQUAL,getLine()); }
	 "!=" { return new Token(sym.NEQUAL,getLine()); }
	 "&&" { return new Token(sym.LAND,getLine()); }
	 "||" { return new Token(sym.LOR,getLine()); }
	 "!" { return new Token(sym.LNEG,getLine()); }
	 "break" { return new Token(sym.BREAK,getLine()); }
	 "class" { return new Token(sym.CLASS,getLine()); }
	 "extends" { return new Token(sym.EXTENDS,getLine()); }
	 "static" { return new Token(sym.STATIC,getLine()); }
	 "continue" { return new Token(sym.CONTINUE,getLine()); }
	 "if" { return new Token(sym.IF,getLine()); }
	 "else" { return new Token(sym.ELSE,getLine()); }
	 "true" { return new Token(sym.TRUE,getLine()); }
	 "false" { return new Token(sym.FALSE,getLine()); }
	 "void" { return new Token(sym.VOID,getLine()); }
	 "while" { return new Token(sym.WHILE,getLine()); }
	 "int" { return new Token(sym.INT,getLine()); }
	 "integer" { return new Token(sym.INTEGER,getLine()); }
	 "length" { return new Token(sym.LENGTH,getLine()); }
	 "null" { return new Token(sym.NULL,getLine()); }
	 "new" { return new Token(sym.NEW,getLine()); }
	 "break" { return new Token(sym.BREAK,getLine()); }
	 "string" { return new Token(sym.STRING,getLine()); }
	 "this" { return new Token(sym.THIS,getLine()); }
	 "boolean" { return new Token(sym.BOOLEAN,getLine()); }
	 "return" { return new Token(sym.RETURN,getLine()); }
	 {INTEGERLITERAL} { return new Token(sym.INTEGER,getLine(),yytext()); }
	 {CLASS_IDENTIFIER} { return new Token(sym.CLASS_ID,getLine(),yytext()); }
	 {IDENTIFIER} { return new Token(sym.ID,getLine(),yytext()); }
	 {INTEGERLITERAL}{ALPHA_NUMERIC} { throw new LexicalError(getLine(),"bad input"); }
	 {WHITESPACE} { }
	 .|\n { throw new LexicalError(getLine(),"Unexpected input: " + yytext()); } 
}

