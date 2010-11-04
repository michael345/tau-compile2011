package IC.Parser;

public class Utils {
    public static String tokenIDToString(int t) { 
        switch (t) {
            case sym.LP: return "LP";
            case sym.IF: return "IF"; 
            case sym.RP: return "RP";
            case sym.PLUS: return "PLUS";
            case sym.MINUS: return "MINUS";
            case sym.ID: return "ID";
            case sym.ASSIGN: return "ASSIGN";
            case sym.BOOLEAN: return "BOOLEAN";
            case sym.BREAK: return "BREAK";
            case sym.CLASS: return "CLASS";
            case sym.CLASS_ID: return "CLASS_ID";
            case sym.COMMA: return "COMMA";
            case sym.CONTINUE: return "CONTINUE";
            case sym.DIVIDE: return "DIVIDE";
            case sym.EQUAL: return "EQUAL";
            case sym.EXTENDS: return "EXTENDS";
            case sym.ELSE: return "ELSE";
            case sym.INT: return "INT";
            case sym.FALSE: return "FALSE";
            case sym.GT: return "GT";
            case sym.GTE: return "GTE";
            case sym.INTEGER: return "INTEGER";
            case sym.LAND: return "LAND";
            case sym.LB: return "LB";
            case sym.LCBR: return "LCBR";
            case sym.LENGTH: return "LENGTH";
            case sym.NEW: return "NEW";
            case sym.LNEG: return "LNEG";
            case sym.LOR: return "LOR";
            case sym.LT: return "LT"; 
            case sym.LTE: return "LTE";
            case sym.MOD: return "MOD";
            case sym.MULTIPLY: return "MULTIPLY";
            case sym.NEQUAL: return "NEQUAL"; 
            case sym.NULL: return "NULL";
            case sym.RB: return "RB";
            case sym.RCBR: return "RCBR";
            case sym.RETURN: return "RETURN";
            case sym.SEMI: return "SEMI";
            case sym.STRING: return "STRING";
            case sym.QUOTE: return "QUOTE";
            case sym.THIS: return "THIS";
            case sym.TRUE: return "TRUE";
            case sym.VOID: return "VOID";
            case sym.WHILE: return "WHILE";
            case sym.EOF: return "EOF";
            case sym.STATIC: return "STATIC";
            case sym.DOT: return "DOT";   
            default: 
                return null;
        }
    }
}
