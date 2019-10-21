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
import cop5556fa19.AST.Chunk;
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
import cop5556fa19.AST.Expressions;
import cop5556fa19.AST.Field;
import cop5556fa19.AST.FieldExpKey;
import cop5556fa19.AST.FieldImplicitKey;
import cop5556fa19.AST.FieldNameKey;
import cop5556fa19.AST.FuncBody;
import cop5556fa19.AST.Name;
import cop5556fa19.AST.ParList;
import cop5556fa19.AST.Stat;
import cop5556fa19.Token.Kind;
import static cop5556fa19.Token.Kind.*;

public class ExpressionParser {
	
	@SuppressWarnings("serial")
	public
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
    boolean Nameflag = false;
    boolean hasVarArgs = false;
	private ParList p;
	private FuncBody functionbody;
    List<Field> fieldList = new ArrayList<>();
    List<Stat> statList = new ArrayList<>();
	Exp exp() throws Exception {
		Token first = t;
		
		
         if(isKind(LCURLY)){
        	 consume();
        	 e10 = tableConstructor(); 
        	 return e10;
         }
		 
         
		 Exp e0 = andExp();
			while (isKind(KW_or)) {
				Token op = consume();
				Exp e1 = andExp();
				e0 = new ExpBinary(first, e0, op, e1);
			}
			
		return e0;
	}
	
private Exp tableConstructor() throws Exception {
	Token first = t; 
	Exp e0,e1 = null;
	Nameflag = false;
	//List<Field> fieldList = new ArrayList<>();
	while(isKind(RCURLY) == false ) {
		if(isKind(LSQUARE)) {
			fieldExpGen();
			if(isKind(COMMA) || isKind(SEMI)) {
				consume();
			}else if(isKind(RCURLY)) {
				break;
			}
			else {
				error(t,t.text);
				break;
			}
		}else {
			e0 = exp();
			if(Nameflag == false){
				FieldImplicitKey fimp = new FieldImplicitKey(first,e0);
				fieldList.add(fimp);
			}
			if(isKind(COMMA) || isKind(SEMI)) {
				consume();
			}else if(isKind(RCURLY)) {
				break;
			}
			else {
				error(t,t.text);
	 			break;
			}
		}
		
	}
	ExpTable exptab = new ExpTable(first,fieldList);
	return exptab;
}	

private void fieldExpGen() throws Exception {
	Token first = t; 
	Exp e0,e1 = null;
//	FieldExpKey result = new Field(); 
	if(isKind(LSQUARE)) {
		consume();
		e0 = exp();
		if(isKind(RSQUARE)) {
			consume();
		}else {
			error(t,t.text);
		      }
		   if(isKind(ASSIGN)) {
			  consume();
			  e1 = exp();
		   }else {
			   error(t,t.text);
		   }
   FieldExpKey fexpkey = new FieldExpKey(first,e0,e1);
   fieldList.add(fexpkey);
	}else {
		error(t,t.text);
	}
	} 

private void fieldNameGen(Name name) throws Exception{
	Token first = t;
	 consume();
	 Exp e0 = exp();
	 FieldNameKey fnamkey = new FieldNameKey(first,name,e0);
	 fieldList.add(fnamkey);
}

private Exp andExp() throws Exception{
   Token first = t;
	Exp e0 = checkForLT();
	
	while (isKind(KW_and)) {
		Token op = consume();
		Exp e1 = checkForLT();
		e0 = new ExpBinary(first, e0, op, e1);
	}
return e0;
} 

private Exp checkForLT() throws Exception {
	Token first = t;
	Exp e0 = checkForBitOr();
	 //1+2*3
	while(isKind(REL_LT) || isKind(REL_GT) || isKind(REL_GE) || isKind(REL_LE) || isKind(REL_NOTEQ) || isKind(REL_EQEQ)) {
		Token op = consume();
		Exp e1 = checkForBitOr();
		e0 = new ExpBinary(first, e0, op, e1);
	}
	return e0;	
}

private Exp checkForBitOr() throws Exception {
	Token first = t;
	Exp e0 = checkForBitXor();
	 //1+2*3
	while(isKind(BIT_OR)) {
		Token op = consume();
		Exp e1 = checkForBitXor();
		e0 = new ExpBinary(first, e0, op, e1);
	}
	return e0;	
}

private Exp checkForBitXor() throws Exception {
	Token first = t;
	Exp e0 = checkForBitAmp();
	 //1+2*3
	while(isKind(BIT_XOR)) {
		Token op = consume();
		Exp e1 = checkForBitAmp();
		e0 = new ExpBinary(first, e0, op, e1);
	}
	return e0;	
}

private Exp checkForBitAmp() throws Exception {
	Token first = t;
	Exp e0 = checkForBitShift();
	 //1+2*3
	while(isKind(BIT_AMP)) {
		Token op = consume();
		Exp e1 = checkForBitShift();
		e0 = new ExpBinary(first, e0, op, e1);
	}
	return e0;	
}

private Exp checkForBitShift() throws Exception {
	Token first = t;
	Exp e0 = checkForDotdot();
	 //1+2*3
	while(isKind(BIT_SHIFTL) || isKind(BIT_SHIFTR)) {
		Token op = consume();
		Exp e1 = checkForDotdot();
		e0 = new ExpBinary(first, e0, op, e1);
	}
	return e0;	
}

private Exp checkForDotdot() throws Exception {
	Token first = t;
	Exp e0 = checkForPlus();
	 //1+2*3
	while(isKind(DOTDOT)) {
		Token op = consume();
		Exp e1 = checkForPlus();
		if(isKind(DOTDOT)) {
				/*
				 * Exp tmp =e0; e0 = e1;
				 */
			     e0 = new ExpBinary(first, e0,DOTDOT, Expressions.makeBinary(e1,consume().kind,checkForDotdot()));
		}
		else {
		e0 = new ExpBinary(first, e0, op, e1);}
	}
	return e0;	
}

	/*
	 * protected boolean isOp(Kind kind) { kind.toString() if() return t.kind ==
	 * kind; }
	 */

	private Exp checkForPlus() throws Exception {
		Token first = t;
		Exp e0 = checkForTimes();
		 //1+2*3
		while(isKind(OP_PLUS) || isKind(OP_MINUS)) {
			Token op = consume();
			Exp e1 = checkForTimes();
			e0 = new ExpBinary(first, e0, op, e1);
		}
		return e0;	
}
	

	private Exp checkForTimes() throws Exception {
		Token first = t;
		Exp e0 = checkForPow();
		while(isKind(OP_TIMES) || isKind(OP_DIV) || isKind(OP_DIVDIV) || isKind(OP_MOD) ) {
			Token op = consume();
			Exp e1 = checkForPow();
			e0 = new ExpBinary(first, e0, op, e1);
		}
		return e0;	
}
	
	private Exp checkForPow() throws Exception {
		Token first = t;
		Exp e0 = term();
		 //1^2^3+4
		while(isKind(OP_POW)) {
			Token op = consume();
			Exp e1 = term();
			if(isKind(OP_POW)) {
					/*
					 * Exp tmp =e0; e0 = e1;
					 */
				     e0 = new ExpBinary(first, e0,OP_POW, Expressions.makeBinary(e1,consume().kind,checkForPow()));
			}
			else {
			e0 = new ExpBinary(first, e0, op, e1);}
		}
		return e0;	
	}

	private FuncBody funbody() throws Exception{  //function (parList) block end
		consume();
		Token first = t;
		List<Name> nameList = new ArrayList<>();
		if(isKind(LPAREN)) {
			consume();
			while(isKind(NAME)) {
				Name inName = new Name(t,t.text);
				consume();
				nameList.add(inName);
				if(isKind(COMMA)) {
					consume();
				}else {
					error(t.kind);
				}
			}
			if(isKind(DOTDOTDOT)) {
				hasVarArgs = true;
				Name inName = new Name(t,t.text);
				nameList.add(inName);
				ParList parList = new ParList(t,nameList,hasVarArgs);
				setParList(parList);
				consume();
			}
			else {
				error(t.kind);
			}
		}else {
			error(t.kind);
		}
		
		if(isKind(RPAREN)) {
			consume();
			if(isKind(KW_end)) {
				FuncBody funcb = new FuncBody(first,getParList(),block());
				setFuncb(funcb);
			}else {
				error(t.kind);
			}
		}else {
			error(t.kind);
		}
		
		
		return getFuncb();
		
	}
	
	 private Exp term() throws Exception{
		 switch(t.kind) {   // 1+2*3
			
			case NAME:
			{
				ExpName en = new ExpName(t);
				Name inName = new Name(t,t.text);
				consume();
				if(isKind(ASSIGN)) {
					fieldNameGen(inName);
					Nameflag = true;
				}
		    	return en;
			}
			case STRINGLIT:
			{
				ExpString es = new ExpString(t);
				Name inName = new Name(t,t.text);
				consume();
				if(isKind(ASSIGN)) {
					fieldNameGen(inName);
					Nameflag = true;
				}
				return es;	
			}
			case INTLIT:
			{
				ExpInt ei = new ExpInt(t);
				Name inName = new Name(t,t.text);
				consume();
				if(isKind(ASSIGN)) {
					fieldNameGen(inName);
					Nameflag = true;
				}
				return ei;
			}	
			case KW_true:
			{
				ExpTrue et = new ExpTrue(t);
				Name inName = new Name(t,t.text);
				consume();
				if(isKind(ASSIGN)) {
					fieldNameGen(inName);
					Nameflag = true;
				}
				return et;
			}
			case KW_false:
			{
				ExpFalse ef = new ExpFalse(t);
				Name inName = new Name(t,t.text);
				consume();
				if(isKind(ASSIGN)) {
					fieldNameGen(inName);
					Nameflag = true;
				}
				return ef;
			}
			case KW_nil:
			{
				ExpNil ef = new ExpNil(t);
				Name inName = new Name(t,t.text);
				consume();
				if(isKind(ASSIGN)) {
					fieldNameGen(inName);
					Nameflag = true;
				}
				return ef;
			}
			case LPAREN:
			{
				consume();
				return exp();
			}
			case RPAREN:
			{
				consume();
				return exp();
			}
			case DOTDOTDOT:
			{
				e10 = new ExpVarArgs(t);
				break;
			}
			case KW_function:
			{
				e10 = new ExpFunction(t, funbody());
				break;
			}
			case OP_HASH:
			{
				e10 = new ExpUnary(consume(),OP_HASH,term());
				break;
			}
			case OP_MINUS:
			{   
				e10 = new ExpUnary(consume(),OP_MINUS,term());
				break;
			}
			case KW_not:
			{
				e10 = new ExpUnary(consume(),KW_not,term());
				break;
			}
			case BIT_XOR:
			{
				e10 = new ExpUnary(consume(),BIT_XOR,term());
				break;
			}
			default:
				error(t, t.text);
				break;
			}
		 return e10;
	 }

	 public ParList getParList() {
		return p;
	 }
	 
	 public  void setParList(ParList p) {
		 this.p =p;
	 }
	 
	 public FuncBody getFuncb() {
			return functionbody;
		 }
		 
		 public  void setFuncb(FuncBody functionbody) {
			 this.functionbody =functionbody;
		 }
	 
	private Block block() {
		
		return new Block(t,statList);  //this is OK for Assignment 2
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

	public Chunk parse() {
		// TODO Auto-generated method stub
		return null;
	}
	


}
