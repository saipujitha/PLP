/**
 * Developed  for the class project in COP5556 Programming Language Principles 
 * at the University of Florida, Fall 2019.
 * 
 * This software is solely for the educational benefit of students 
 * enrolled in the course during the Fall 2019 semester.  
 * 
 * This software, and any software derived from it,  may not be shared with others or posted to public web sites,
 * either during the course or afterwards.
 * 
 *  @Beverly A. Sanders, 2019
 */

package cop5556fa19;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import cop5556fa19.AST.Block;
import cop5556fa19.AST.Exp;
import cop5556fa19.AST.ExpBinary;
import cop5556fa19.AST.ExpFalse;
import cop5556fa19.AST.ExpFunction;
import cop5556fa19.AST.ExpInt;
import cop5556fa19.AST.ExpName;
import cop5556fa19.AST.ExpNil;
import cop5556fa19.AST.ExpString;
import cop5556fa19.AST.ExpTable;
import cop5556fa19.AST.ExpTrue;
import cop5556fa19.AST.ExpUnary;
import cop5556fa19.AST.ExpVarArgs;
import cop5556fa19.AST.Field;
import cop5556fa19.AST.FieldExpKey;
import cop5556fa19.AST.FieldImplicitKey;
import cop5556fa19.AST.FieldNameKey;
import cop5556fa19.AST.FuncBody;
import cop5556fa19.AST.Name;
import cop5556fa19.AST.ParList;
import cop5556fa19.Token.Kind;
import static cop5556fa19.Token.Kind.*;

public class ExpressionParser {
	
	@SuppressWarnings("serial")
	class SyntaxException extends Exception {
		Token t;
		
		public SyntaxException(Token t, String message) {
			super(t.line + ":" + t.pos + " " + message);
		}
	}
	
	final Scanner scanner;
	Token t;  //invariant:  this is the next token


	ExpressionParser(Scanner s) throws Exception {
		this.scanner = s;
		t = scanner.getNext(); //establish invariant
	}

	Exp e10 = null;
    int iterator = 0;
    
    
	Exp exp() throws Exception {
		Token first = t;
		 Exp e0 = null;
		 iterator++;
		switch(t.kind)
		{
		case LPAREN:
		{
			consume();
			e0 = andExp();
			break;
		}
		case OP_MINUS:
		{
			Token op = consume();
			   Exp e1 = andExp(); 
			   if(iterator > 1) {
				   e0 = new  ExpBinary(first, e10, op, e1);
				   }
				   else {
					   e0 = new  ExpUnary(first, op.kind, e1);
				   }
			break;
		}
		case DOTDOT:
		{
			Token op = consume();
			Exp e1 = andExp();
			e0 = new ExpBinary(first,e10,op.kind,exp());
			break;
		}
		
		case OP_POW:
		{
			Token op = consume();
			Exp e1 = andExp();
			e0 = new ExpBinary(first,e10,op.kind,exp());
			break;
		}
		
		default:
			//e10 = e0;
		if(t.kind == OP_PLUS || t.kind == OP_MINUS )
		{     Exp e1 = null;
			Token op = consume(); // 1+2*3
			if(t.kind != EOF) {
				 e1 = andExp(); 
			}
				   e0 = new ExpBinary(first,e10,op.kind,exp());
			break;
		}
			e0 = andExp();
			break;
		}

		e10 = e0;
		consume();
     if(t.kind != Token.Kind.EOF && t.kind != Token.Kind.RPAREN) {
    	 e0=exp();
     }
		return e0;
	}

	
private Exp andExp() throws Exception{
   
	switch(t.kind) {
	
	case NAME:
	{
		ExpName en = new ExpName(t);
    	return en;
	}
	case STRINGLIT:
	{
		//t.text = t.getStringVal();
		ExpString es = new ExpString(t);
		return es;	
	}
	case INTLIT:
	{
		ExpInt ei = new ExpInt(t);
		return ei;
	}	
	case KW_true:
	{
		ExpTrue et = new ExpTrue(t);
		return et;
	}
	case KW_false:
	{
		ExpFalse ef = new ExpFalse(t);
		return ef;
	}
	default:
		error( t, t.text);
	}
return e10;
}


	private void checkNext() {
		try {
			consume();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
}


	private Block block() {
		return new Block(null);  //this is OK for Assignment 2
	}


	protected boolean isKind(Kind kind) {
		return t.kind == kind;
	}

	protected boolean isKind(Kind... kinds) {
		for (Kind k : kinds) {
			if (k == t.kind)
				return true;
		}
		return false;
	}

	/**
	 * @param kind
	 * @return
	 * @throws Exception
	 */
	Token match(Kind kind) throws Exception {
		Token tmp = t;
		if (isKind(kind)) {
			consume();
			return tmp;
		}
		error(kind);
		return null; // unreachable
	}

	/**
	 * @param kind
	 * @return
	 * @throws Exception
	 */
	Token match(Kind... kinds) throws Exception {
		Token tmp = t;
		if (isKind(kinds)) {
			consume();
			return tmp;
		}
		StringBuilder sb = new StringBuilder();
		for (Kind kind1 : kinds) {
			sb.append(kind1).append(kind1).append(" ");
		}
		error(kinds);
		return null; // unreachable
	}

	Token consume() throws Exception {
		Token tmp = t;
        t = scanner.getNext();
		return tmp;
	}
	
	void error(Kind... expectedKinds) throws SyntaxException {
		String kinds = Arrays.toString(expectedKinds);
		String message;
		if (expectedKinds.length == 1) {
			message = "Expected " + kinds + " at " + t.line + ":" + t.pos;
		} else {
			message = "Expected one of" + kinds + " at " + t.line + ":" + t.pos;
		}
		throw new SyntaxException(t, message);
	}

	void error(Token t, String m) throws SyntaxException {
		String message = m + " at " + t.line + ":" + t.pos;
		throw new SyntaxException(t, message);
	}
	


}
