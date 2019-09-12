
/* *
 * Developed  for the class project in COP5556 Programming Language Principles 
 * at the University of Florida, Fall 2019.
 * 
 * This software is solely for the educational benefit of students 
 * enrolled in the course during the Fall 2019 semester.  
 * 
 * This software, and any software derived from it,  may not be shared with others or posted to public web sites or repositories,
 * either during the course or afterwards.
 * 
 *  @Beverly A. Sanders, 2019
 */
package cop5556fa19;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
//import java.lang.Character.isJavaIdentifierStart;
import cop5556fa19.Token;
import cop5556fa19.Token.Kind;

public class Scanner {

	Reader r;
	static int rowpos = 0;
	static int linepos = 0;
	int iterator = 0;
	static int listIter = -1;
	StringBuilder sb = new StringBuilder();
	String inputString = "";
	boolean squote = false;
	Kind kind;
	// char[] chrArray = new chrArray[8];

	@SuppressWarnings("serial")
	public static class LexicalException extends Exception {
		public LexicalException(String arg0) {
			super(arg0);
		}
	}

	public boolean spcList(char ch) {

		StringBuilder sb = new StringBuilder();
		boolean result = true;
		switch (ch) {
		case 'r':
			sb.append('\r');
			break;
		case 'n':
			sb.append('\n');
			break;
		
		default:
			result = false;
			break;
		}
		// boolean result = whitespaceList.contains(input)? true: false;
		return result;
	}


	public Scanner(Reader r) throws IOException {
		this.r = r;
		int intValueOfChar;
		// ArrayList<Token> tknList = new ArrayList<>();
		while ((intValueOfChar = r.read()) != -1) {
			inputString += (char) intValueOfChar;
			this.kind = Kind.START;
		}

		char[] chrArray = inputString.toCharArray();
	}

	public Token getNext() throws Exception {

		Token tok = new Token(Token.Kind.EOF, sb.toString(), rowpos, linepos);
		int len = inputString.length();
		StringBuilder sbTemp = new StringBuilder();
		if (inputString == "") { // white space check
			return new Token(Token.Kind.EOF, inputString, rowpos, linepos);
		}
		
		if(!(iterator<len)) {
			return new Token(Token.Kind.EOF, inputString, rowpos, linepos);
		}
		
		char inpstr = inputString.charAt(iterator);
		System.out.println("input:"+inputString+" inChar"+inpstr);
		
		if(!(iterator<len)) {
			return new Token(Token.Kind.EOF, inputString, rowpos, linepos);
		}
		
		if ((( Character.isJavaIdentifierStart(inpstr)) || (Character.isJavaIdentifierPart(inpstr))) && (kind != Kind.AFTER_DQUOTE)) {
			if(Character.isDigit(inpstr) && kind == Kind.NAME)
			{kind = Kind.NAME;}
			else if(Character.isDigit(inpstr) && (kind != Kind.NAME)) {
				kind = Kind.INTLIT;
			}
			else {
				kind = Kind.NAME;
			}
		}
		
		if(inpstr == '\\' ) {
			kind = Kind.ESCSEQ;
			iterator++;
			rowpos++;
		}
		
		if(kind == Kind.START) {
			sb = sbTemp;
		}
		//Token tok = null;
		switch (kind) {
		case START:
			switch (inpstr) {
			case '@':
				throw new LexicalException("Useful error message");
			case ',':
				tok = new Token(Token.Kind.COMMA, ",", rowpos, linepos);
				rowpos++;
				break;
			case ':':
				tok = new Token(Token.Kind.COLON, ":", rowpos, linepos);
				if( ((iterator+1)<len) &&(inputString.charAt(iterator+1) == ':')) {
					iterator++;
					rowpos++;
					tok = new Token(Token.Kind.COLONCOLON, "::", rowpos, linepos);
				}
					rowpos++;
					break;
			case '=':
				tok = new Token(Token.Kind.ASSIGN, "=", rowpos, linepos);
				if( ((iterator+1)<len) &&(inputString.charAt(iterator+1) == '=')) {
					iterator++;
					rowpos++;
					tok = new Token(Token.Kind.REL_EQEQ, "==", rowpos, linepos);
				}
					rowpos++;
					break;
			case '+':
				tok = new Token(Token.Kind.OP_PLUS, "+", rowpos, linepos);
				rowpos++;
				break;
			case '-':
				tok = new Token(Token.Kind.OP_MINUS, "-", rowpos, linepos);
				if( ((iterator+1)<len) &&(inputString.charAt(iterator+1) == '-')) {
					iterator++;
					rowpos++;
					kind = Kind.COMMENT;
					return getNext();
				}
				rowpos++;
				break;
			case '*':
				tok = new Token(Token.Kind.OP_TIMES, "*", rowpos, linepos);
				rowpos++;
				break;
			case '/':
				tok = new Token(Token.Kind.OP_DIV, "/", rowpos, linepos);
				rowpos++;
				if( ((iterator+1)<len) &&(inputString.charAt(iterator+1) == '/')) {
					iterator++;
					rowpos++;
					tok = new Token(Token.Kind.OP_DIVDIV, "//", rowpos, linepos);
				}
				break;
			case '%':
				tok = new Token(Token.Kind.OP_MOD, "%", rowpos, linepos);
				rowpos++;
				break;
			case '^':
				tok = new Token(Token.Kind.OP_POW, "^", rowpos, linepos);
				rowpos++;
				break;
			case '#':
				tok = new Token(Token.Kind.OP_HASH, "#", rowpos, linepos);
				rowpos++;
				break;
			case '&':
				tok = new Token(Token.Kind.BIT_AMP, "&", rowpos, linepos);
				rowpos++;
				break;
			case '~':
				tok = new Token(Token.Kind.BIT_XOR, "~", rowpos, linepos);
				if( ((iterator+1)<len) &&(inputString.charAt(iterator+1) == '=')) {
					iterator++;
					rowpos++;
					tok = new Token(Token.Kind.REL_NOTEQ, "~=", rowpos, linepos);
				}
				rowpos++;
				break;
			case '|':
				tok = new Token(Token.Kind.BIT_OR, "|", rowpos, linepos);
				rowpos++;
				break;
			case '<':
				tok = new Token(Token.Kind.REL_LT, "<", rowpos, linepos);
				rowpos++;
				if( ((iterator+1)<len) &&(inputString.charAt(iterator+1) == '<')) {
					iterator++;
					rowpos++;
					tok = new Token(Token.Kind.BIT_SHIFTL, "<<", rowpos, linepos);
					break;
				}
				if( ((iterator+1)<len) &&(inputString.charAt(iterator+1) == '=')) {
					iterator++;
					rowpos++;
					tok = new Token(Token.Kind.REL_LE, "<=", rowpos, linepos);
				}
				break;
			case '>':
				tok = new Token(Token.Kind.REL_GT, ">", rowpos, linepos);
				rowpos++;
				if( ((iterator+1)<len) &&(inputString.charAt(iterator+1) == '>')) {
					iterator++;
					rowpos++;
					tok = new Token(Token.Kind.BIT_SHIFTR, ">>", rowpos, linepos);
					break;
				}
				if( ((iterator+1)<len) &&(inputString.charAt(iterator+1) == '=')) {
					iterator++;
					rowpos++;
					tok = new Token(Token.Kind.REL_GE, ">=", rowpos, linepos);
				}
				break;
			case '(':
				tok = new Token(Token.Kind.LPAREN, "(", rowpos, linepos);
				rowpos++;
				break;
			case ')':
				tok = new Token(Token.Kind.RPAREN, ")", rowpos, linepos);
				rowpos++;
				break;
			case '{':
				tok = new Token(Token.Kind.LCURLY, "{", rowpos, linepos);
				rowpos++;
				break;
			case '}':
				tok = new Token(Token.Kind.RCURLY, "}", rowpos, linepos);
				rowpos++;
				break;
			case '[':
				tok = new Token(Token.Kind.LSQUARE, "[", rowpos, linepos);
				rowpos++;
				break;
			case ']':
				tok = new Token(Token.Kind.RSQUARE, "]", rowpos, linepos);
				rowpos++;
				break;
			case ';':
				tok = new Token(Token.Kind.SEMI, ";", rowpos, linepos);
				rowpos++;
				break;
			case '.':
				tok = new Token(Token.Kind.DOT, ".", rowpos, linepos);
				rowpos++;
				if( ((iterator+1)<len) &&(inputString.charAt(iterator+1) == '.')) {
					iterator++;
					rowpos++;
					tok = new Token(Token.Kind.DOTDOT, "..", rowpos, linepos);
					if( ((iterator+1)<len) &&(inputString.charAt(iterator+1) == '.')) {
						iterator++;
						rowpos++;
						tok = new Token(Token.Kind.DOTDOTDOT, "...", rowpos, linepos);
					}
				}
				break;
			case '"':
				   kind = Kind.AFTER_DQUOTE;
				   rowpos++;
				   sb.append(inpstr);
				   if((iterator+1)<len) {
				   iterator++;
				   return getNext();}
				   
			case '\'':
				   kind = Kind.AFTER_DQUOTE;
				   rowpos++;
				   squote = true;
				   sb.append(inpstr);
				   if((iterator+1)<len) {
				   iterator++;
				   return getNext();}
	   
				   
			};
			
			// iterator++;
			break;
		case AFTER_DQUOTE:
			
			char quote = squote? '\'' : '"';
			if(inpstr!= quote) {
				sb.append(inpstr);
				rowpos++;
				if((iterator+1)<len) {
					iterator++;
					return getNext();
				}
				else if(((iterator+1)==len) && (inpstr!= quote)){
					throw new LexicalException("Useful error message");
				//tok = new Token(Token.Kind.EOF, inputString, rowpos, linepos);
				}
			}
			else {
				sb.append(inpstr);
				rowpos++;
				squote = false;
				tok = new Token(Token.Kind.STRINGLIT, sb.toString(), rowpos, linepos);
				kind = Kind.START;
				sb = sbTemp;
			     }
			
			/*
			 * if(inpstr == '"') { sb.append('"'); rowpos++; tok = new
			 * Token(Token.Kind.STRINGLIT, sb.toString(), rowpos, linepos); kind =
			 * Kind.START; } else if(Character.isJavaIdentifierPart(inpstr)) {
			 * sb.append(inpstr); rowpos++; iterator++; return getNext(); } else {
			 * 
			 * } "a\"b\"c"
			 */
			break;
		case NAME:  //"abc"
			sb.append(inpstr);
			rowpos++;
			if((iterator+1)<len){
				if(Character.isJavaIdentifierPart(inputString.charAt(iterator+1))){
					iterator++;
					tok = new Token(Token.Kind.NAME, sb.toString(), rowpos, linepos);
					return getNext();
				}
				else {
					kind = Kind.START;
				}
			}
			if(idntList(sb.toString())== Kind.EOF) {
				tok = new Token(Token.Kind.NAME, sb.toString(), rowpos, linepos);
			}
			else {
				tok = new Token(idntList(sb.toString()), sb.toString(), rowpos, linepos);
			}
			sb =sbTemp;
			break;
		case INTLIT:
			int inValue = Character.getNumericValue(inpstr);
			sb.append(inpstr);
			rowpos++;
			if(inValue == 0 && sb.length()<2) {
				tok = new Token(Token.Kind.INTLIT, "0", rowpos, linepos);
				sb = sbTemp;
			      }
			else {
				if(((iterator+1)<len) && (Character.isDigit(inputString.charAt(iterator+1)))) {
			    	if(inputString.charAt(iterator+1) == 0) {
					tok = new Token(Token.Kind.INTLIT, sb.toString(), rowpos, linepos);
					kind = Kind.START;
				             }
				else {
					tok = new Token(Token.Kind.INTLIT, sb.toString(), rowpos, linepos);
					iterator++;
					return getNext();
				     }}
				else {
					tok = new Token(Token.Kind.INTLIT, sb.toString(), rowpos, linepos);
					sb = sbTemp;
					kind = Kind.START;
				}
			       }
			break;
		case ESCSEQ: 
            rowpos++;
            boolean escflag = false;
			switch (inputString.charAt(iterator)) {
			case 'b':
				sb.append('\u5C62');
				break;
			case 'a':
				sb.append('\u0007');
				break;
			case 'v':
				sb.append('\u5C76');
				break;
			case 't':
				sb.append('\t');
				escflag = true;
				break;
			case 'f':
				sb.append('\u5C66');
				escflag = true;
				break;
			case 'r':
				sb.append('\r');
				escflag = true;
				break;
			case 'n':
				sb.append('\u5C6E');
				escflag = true;
				break;
			case '\"':
				sb.append('\"');
				break;
			case '\'':
				sb.append('\'');
				break;
			case '\\':
				sb.append('\\');
				break;
			default:
				 //tok = new Token(Token.Kind.EOF, sb.toString(), rowpos, linepos);
				throw new LexicalException("Useful error message");
			}
			if(escflag) {
				linepos++;
			}
				kind = Kind.START;
				rowpos = 0;
				System.out.println("pos, line:"+rowpos+","+linepos);
			    tok = new Token(Token.Kind.EOF, sb.toString(), rowpos, linepos);
			    sb = sbTemp;
				if((iterator+1)<len){
					iterator++;
					return getNext();
				}
				//start from here
				
			break;
		case COMMENT:
			if(inpstr!= '\\') {
				sb.append(inpstr);
				rowpos++;
				if((iterator+1)<len) {
					iterator++;
					return getNext();
				}
				else if(((iterator+2)==len) && (inpstr!= '\\')){
					throw new LexicalException("Useful error message");
				//tok = new Token(Token.Kind.EOF, inputString, rowpos, linepos);
				}
			}
			else {
				if((iterator+1)<len) {
					
				}
				sb.append(inpstr);
				rowpos++;
			//	tok = new Token(Token.Kind.STRINGLIT, sb.toString(), rowpos, linepos);
				kind = Kind.START;
				sb = sbTemp;
			     }
			break;
	default:
	break;

		}
		++iterator;
		if(inpstr == ' ') {
			return getNext();
		}
		System.out.println("iter" + iterator);
		return tok;
	}

	public static Token.Kind idntList(String chr) {
		HashMap<String, Token.Kind> idntList = new HashMap<>();

		idntList.put("and", Token.Kind.KW_and);
		idntList.put("break", Token.Kind.KW_break);
		idntList.put("do", Token.Kind.KW_do);
		idntList.put("else", Token.Kind.KW_else);
		idntList.put("elseif", Token.Kind.KW_elseif);
		idntList.put("end", Token.Kind.KW_end);
		idntList.put("false", Token.Kind.KW_false);
		idntList.put("for", Token.Kind.KW_for);
		idntList.put("function", Token.Kind.KW_function);
		idntList.put("goto", Token.Kind.KW_goto);
		idntList.put("if", Token.Kind.KW_if);
		idntList.put("in", Token.Kind.KW_in);
		idntList.put("local", Token.Kind.KW_local);
		idntList.put("nil", Token.Kind.KW_nil);
		idntList.put("not", Token.Kind.KW_not);
		idntList.put("or", Token.Kind.KW_or);
		idntList.put("repeat", Token.Kind.KW_repeat);
		idntList.put("return", Token.Kind.KW_return);
		idntList.put("then", Token.Kind.KW_then);
		idntList.put("true", Token.Kind.KW_true);
		idntList.put("until", Token.Kind.KW_until);
		idntList.put("while", Token.Kind.KW_while);
		Kind  result = idntList.containsKey(chr)? idntList.get(chr): Kind.EOF;
		return result;
	}

}
