package IC.Parser;

%%

%cup
%class Lexer
%public
%function next_token
%type Token
%line
%scanerror LexicalError
%state SINGLE_LINE_COMMENTS
%state MULTI_LINE_COMMENTS
%state QUOTE
DIGIT=[0-9]
LOWERALPHA=[a-z]
UPPERALPHA=[A-Z]
ALPHA=[A-Za-z_]
ALPHA_NUMERIC={ALPHA}|{DIGIT}
CLASS_IDENTIFIER={UPPERALPHA}({ALPHA_NUMERIC})*
IDENTIFIER={LOWERALPHA}({ALPHA_NUMERIC})*


%eofval{
  	return new Token(sym.EOF,yyline);
%eofval}

%%

<YYINITIAL> "//" { yybegin(SINGLE_LINE_COMMENTS); }
<YYINITIAL> "/*" { yybegin(MULTI_LINE_COMMENTS); }
<QUOTE> "\"" { yybegin(YYINITIAL); return new Token(sym.QUOTE, yyline, yytext()); }
<YYINITIAL> "\"" { yybegin(QUOTE); }
<YYINITIAL> "(" { return new Token(sym.LP,yyline); }
<YYINITIAL> ")" { return new Token(sym.RP,yyline); }
<YYINITIAL> "{" { return new Token(sym.LCBR,yyline); }
<YYINITIAL> "}" { return new Token(sym.RCBR,yyline); }
<YYINITIAL> "[" { return new Token(sym.LB,yyline); }
<YYINITIAL> "]" { return new Token(sym.RB,yyline); }
<YYINITIAL> "," { return new Token(sym.COMMA,yyline); }
<YYINITIAL> ";" { return new Token(sym.SEMI,yyline); }
<YYINITIAL> "=" { return new Token(sym.ASSIGN,yyline); }
<YYINITIAL> "-" { return new Token(sym.MINUS,yyline); }
<YYINITIAL> "+" { return new Token(sym.PLUS,yyline); }
<YYINITIAL> "/" { return new Token(sym.DIVIDE,yyline); }
<YYINITIAL> "%" { return new Token(sym.MOD,yyline); }
<YYINITIAL> "." { return new Token(sym.DOT,yyline); }
<YYINITIAL> "*" { return new Token(sym.MULTIPLY,yyline); }
<YYINITIAL> ">" { return new Token(sym.GT,yyline); }
<YYINITIAL> "<" { return new Token(sym.LT,yyline); }
<YYINITIAL> ">=" { return new Token(sym.GTE,yyline); }
<YYINITIAL> "<=" { return new Token(sym.LTE,yyline); }
<YYINITIAL> "==" { return new Token(sym.EQUAL,yyline); }
<YYINITIAL> "!=" { return new Token(sym.NEQUAL,yyline); }
<YYINITIAL> "&&" { return new Token(sym.LAND,yyline); }
<YYINITIAL> "||" { return new Token(sym.LOR,yyline); }
<YYINITIAL> "!" { return new Token(sym.LNEG,yyline); }
<YYINITIAL> "break" { return new Token(sym.BREAK,yyline); }
<YYINITIAL> "class" { return new Token(sym.CLASS,yyline); }
<YYINITIAL> "extends" { return new Token(sym.EXTENDS,yyline); }
<YYINITIAL> "static" { return new Token(sym.STATIC,yyline); }
<YYINITIAL> "continue" { return new Token(sym.CONTINUE,yyline); }
<YYINITIAL> "if" { return new Token(sym.IF,yyline); }
<YYINITIAL> "else" { return new Token(sym.ELSE,yyline); }
<YYINITIAL> "true" { return new Token(sym.TRUE,yyline); }
<YYINITIAL> "false" { return new Token(sym.FALSE,yyline); }
<YYINITIAL> "void" { return new Token(sym.VOID,yyline); }
<YYINITIAL> "while" { return new Token(sym.WHILE,yyline); }
<YYINITIAL> "int" { return new Token(sym.INT,yyline); }
<YYINITIAL> "integer" { return new Token(sym.INTEGER,yyline); }
<YYINITIAL> "length" { return new Token(sym.LENGTH,yyline); }
<YYINITIAL> "null" { return new Token(sym.NULL,yyline); }
<YYINITIAL> "new" { return new Token(sym.NEW,yyline); }
<YYINITIAL> "break" { return new Token(sym.BREAK,yyline); }
<YYINITIAL> "string" { return new Token(sym.STRING,yyline); }
<YYINITIAL> "this" { return new Token(sym.THIS,yyline); }
<YYINITIAL> "boolean" { return new Token(sym.BOOLEAN,yyline); }
<SINGLE_LINE_COMMENTS> [.] { }
<SINGLE_LINE_COMMENTS> [\n] { yybegin(YYINITIAL); }
<MULTI_LINE_COMMENTS> "*/" { yybegin(YYINITIAL); }
<MULTI_LINE_COMMENTS> {ALPHA_NUMERIC} { }
<MULTI_LINE_COMMENTS> ^{ALPHA_NUMERIC} { }
<YYINITIAL> {CLASS_IDENTIFIER} { return new Token(sym.CLASS_ID,yyline,yytext()); }
<YYINITIAL> {IDENTIFIER} { return new Token(sym.ID,yyline,yytext()); }
<YYINITIAL> [ \t\n\x0B\f\r] { }


