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
import cop5556fa19.AST.ExpFunctionCall;
import cop5556fa19.AST.ExpInt;
import cop5556fa19.AST.ExpName;
import cop5556fa19.AST.ExpNil;
import cop5556fa19.AST.ExpString;
import cop5556fa19.AST.ExpTable;
import cop5556fa19.AST.ExpTableLookup;
import cop5556fa19.AST.ExpTrue;
import cop5556fa19.AST.ExpUnary;
import cop5556fa19.AST.ExpVarArgs;
import cop5556fa19.AST.Expressions;
import cop5556fa19.AST.Field;
import cop5556fa19.AST.FieldExpKey;
import cop5556fa19.AST.FieldImplicitKey;
import cop5556fa19.AST.FieldNameKey;
import cop5556fa19.AST.FuncBody;
import cop5556fa19.AST.FuncName;
import cop5556fa19.AST.Name;
import cop5556fa19.AST.ParList;
import cop5556fa19.AST.RetStat;
import cop5556fa19.AST.Stat;
import cop5556fa19.AST.StatAssign;
import cop5556fa19.AST.StatBreak;
import cop5556fa19.AST.StatDo;
import cop5556fa19.AST.StatFor;
import cop5556fa19.AST.StatForEach;
import cop5556fa19.AST.StatFunction;
import cop5556fa19.AST.StatGoto;
import cop5556fa19.AST.StatIf;
import cop5556fa19.AST.StatLabel;
import cop5556fa19.AST.StatLocalAssign;
import cop5556fa19.AST.StatLocalFunc;
import cop5556fa19.AST.StatRepeat;
import cop5556fa19.AST.StatWhile;
import cop5556fa19.AST.Var;
import cop5556fa19.Token.Kind;
import static cop5556fa19.Token.Kind.*;
import static org.junit.Assert.assertNotNull;

public class Parser {
	
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


	Parser(Scanner s) throws Exception {
		this.scanner = s;
		t = scanner.getNext(); //establish invariant
	}

	Exp e10 = null;
    boolean Nameflag = false;
    boolean hasVarArgs = false;
	private ParList p;
	private FuncBody functionbody;
    List<Field> fieldList = new ArrayList<>();
    Token check = null;
    
    public Chunk parse() throws Exception {
		Token first = t;
		Chunk chunk = new Chunk(first,block());
		return chunk;
	}
    
    private Block insideBlock() throws Exception{
    	List<Stat> statList = new ArrayList<Stat>();
		if(t.kind == Kind.SEMI)
		{
			return Expressions.makeBlock();
		}
    	if(t.kind == Kind.EOF)
		{
			return Expressions.makeBlock();
		}
		while( !isKind(KW_end) && !isKind(KW_until) && !isKind(KW_else) && !isKind(KW_elseif)){
			statList.add(andBlock());
			check = t;
			if(isKind(SEMI)) {
				consume();
			}
		
		}
		return new Block(t,statList); }
    
    private Block block() throws Exception {
		List<Stat> statList = new ArrayList<Stat>();
		if(t.kind == Kind.SEMI)
		{
			return Expressions.makeBlock();
		}
    	if(t.kind == Kind.EOF)
		{
			return Expressions.makeBlock();
		}
		while(!isKind(EOF) ) { 
			statList.add(andBlock());
			check = t;
			if(isKind(SEMI)) {
				consume();
			}
		
		}
		return new Block(t,statList); 
    }
    
    private List<Exp> makeVarName() throws Exception {
    	 Token first = t;
 		List<Exp> nameList = new ArrayList<>();
 		List<Exp> expList = new ArrayList<>();
 		ExpName en1 = new ExpName(t);
 		ExpFunctionCall eSt = null;
 		if(isKind(NAME)) {
 			consume();
 			
 			if(isKind(COLON)){
 				ExpName en2 = new ExpName(t);
 				Exp eTemp = exp();
 				expList.add(en1);
 				if(isKind(LCURLY)) {
 					eTemp =tableConstructor();
 					expList.add(eTemp);
 	 				//ExpFunctionCall eFc = new ExpFunctionCall(first,en1,expList);
 	 				//nameList.add(eFc);
 				}
 				else if(isKind(STRINGLIT)) {
 					eTemp = exp();
 					expList.add(eTemp);
 	 				//ExpFunctionCall eS = new ExpFunctionCall(first,en1,expList);
 	 				//nameList.add(eS);
 				}
 				else if(isKind(LPAREN)) {
 					consume();
 	 				while(!isKind(RPAREN)) {
 	 					expList.add(exp());
 	 					if(isKind(COMMA)) {
 	 						consume();
 	 					}}consume();
 				}
 				if(isKind(STRINGLIT) || isKind(LCURLY)){
 					 eSt = new ExpFunctionCall(first,en2,expList);
 				}
 				else {
 					 eSt = new ExpFunctionCall(first,en2,expList);
 				}
 				    ExpTableLookup eS = new ExpTableLookup(first,en1,eSt);
 				   nameList.add(eS);
 			}
 			
 			if(isKind(STRINGLIT)){
 				expList.add(exp());
 				ExpFunctionCall eS = new ExpFunctionCall(first,en1,expList);
 				nameList.add(eS);
 			}
 			if(isKind(COMMA)) {
 				nameList.add(en1);
 				consume();
 				while(isKind(NAME)) {
 					ExpName en = new ExpName(t);
 					consume();
 					nameList.add(en);
 					if(isKind(COMMA)) {
 						consume();
 					}
 		         }
 			}else if(isKind(DOT)) {
 				consume();
 				ExpTableLookup et = makeVarDotken(first,en1);
 				nameList.add(et);
 			}
 			else if(isKind(LPAREN)) {
 				consume();
 				while(!isKind(RPAREN)) {
 					expList.add(exp());
 					if(isKind(COMMA)) {
 						consume();
 					}}consume();
 				ExpFunctionCall eFc = new ExpFunctionCall(first,en1,expList);
 				nameList.add(eFc);
 			}
 			else if(isKind(LCURLY)){
 				expList.add(tableConstructor());
 				ExpFunctionCall eFc = new ExpFunctionCall(first,en1,expList);
 				nameList.add(eFc);
 			}
 			else {nameList.add(en1);}
 		}else {
 			expList.add(exp());
			while(isKind(COMMA)) {
				consume();
				if(isKind(NAME)) {
					List<Exp> temp = makeVarName();
					expList.add(temp.get(0));
				}
				else {
				expList.add(exp());}
				consume();
				}
	    return expList;   
 		}
 		return nameList;
    }
    private ExpTableLookup makeVarLSq(Token first,Exp en ) throws Exception {
    	consume();
    	ExpTableLookup eTL = new ExpTableLookup(first,en,exp());
		consume();
		while(isKind(LSQUARE)){
			consume();
			//nameList.add(exp());
			 eTL = new  ExpTableLookup(first,eTL,exp());
			consume();
			if(isKind(RSQUARE)) {
				consume();
			}
		}
		return eTL;}
    
    private ExpTableLookup makeVarDotken (Token first,Exp en ) throws Exception {
    	ExpString en1 = new ExpString(t);
    	ExpTableLookup eTL = new ExpTableLookup(first,en,en1);
    	consume();
		while(isKind(DOT)){
			consume();
			ExpString en2 = new ExpString(t);
			 eTL = new  ExpTableLookup(first,eTL,en2);
			consume();
		}
		return eTL;}
    
    private List<Exp> makeVarLparen() throws Exception {
        Token first = t;
		List<Exp> nameList = new ArrayList<>();
		List<Exp> expList = new ArrayList<>();
		consume();
		ExpTableLookup eTL = null;
		 if(isKind(LPAREN)){
			 Exp en1 =  exp();
				if(isKind(RPAREN)) {
					consume();
				}
				if(isKind(COLON)) {
					ExpFunctionCall eSt = null;
	 				ExpName en2 = new ExpName(t);
	 				Exp eTemp = exp();
	 				expList.add(en1);
	 				if(isKind(LCURLY)) {
	 					eTemp =tableConstructor();
	 					expList.add(eTemp);
	 	 				//ExpFunctionCall eFc = new ExpFunctionCall(first,en1,expList);
	 	 				//nameList.add(eFc);
	 				}
	 				else if(isKind(STRINGLIT)) {
	 					eTemp = exp();
	 					expList.add(eTemp);
	 	 				//ExpFunctionCall eS = new ExpFunctionCall(first,en1,expList);
	 	 				//nameList.add(eS);
	 				}
	 				else if(isKind(LPAREN)) {
	 					consume();
	 	 				while(!isKind(RPAREN)) {
	 	 					expList.add(exp());
	 	 					if(isKind(COMMA)) {
	 	 						consume();
	 	 					}}consume();
	 				}
	 				
					if(isKind(STRINGLIT) || isKind(LCURLY)){
	 					 eSt = new ExpFunctionCall(first,en2,expList);
	 				}
	 				else {
	 					 eSt = new ExpFunctionCall(first,en2,expList);
	 				}
	 				     eTL = new ExpTableLookup(first,en1,eSt);
	 				   nameList.add(eTL);
	 			
				}
				if(isKind(LSQUARE) || isKind(DOT) || isKind(LPAREN) || isKind(LCURLY) || isKind(STRINGLIT)) {
			while(isKind(LSQUARE) || isKind(DOT) || isKind(LPAREN) || isKind(STRINGLIT) || isKind(LCURLY)) {
				   if(isKind(STRINGLIT)){
					   expList.add(exp());
					   ExpFunctionCall eF =  new ExpFunctionCall(first,en1,expList);
					   en1 = eF;
						nameList.add(eF); 
				   }
					if(isKind(LSQUARE)){
						 eTL = makeVarLSq(first,en1);
						 nameList.add(eTL);
						 en1 = eTL;
					}
					if(isKind(DOT)){
						consume();
						eTL = makeVarDotken(first,en1);
						nameList.add(eTL);
						en1 = eTL;
					}
					if(isKind(LCURLY)) {
						expList.add(tableConstructor());
						ExpFunctionCall eF =  new ExpFunctionCall(first,en1,expList);	
						nameList.add(eF);
						 en1 = eF;
					}
					if(isKind(LPAREN)){
						consume();
						while(!isKind(RPAREN)){
							expList.add(exp());
			    			while(isKind(COMMA)) {
			    				consume();
			    				expList.add(exp());
			    			}
						}
					ExpFunctionCall eF =  new ExpFunctionCall(first,en1,expList);	
					nameList.add(eF);
					 en1 = eF;
					}
			}}
			else {
				nameList.add(en1);
			}
		}
		
		return nameList;
    }
    
    private Stat andBlock() throws Exception{
    	Token first = t; 
    	Block block = null;
    	Token temp;
    	Stat stat = null;
    	
    	
    	if(isKind(KW_function)){
    	consume();
    	List<ExpName> nameList = new ArrayList<>();
			/*
			 * if(t.kind == Kind.LPAREN) { e10 = new ExpFunction(first, funbody()); } else
			 */ 
    	 if(isKind(NAME)){
			          while(isKind(NAME)) {
					ExpName en = new ExpName(t);
					consume();
					nameList.add(en);
					if(isKind(DOT)) {
						consume();
					}
		         }
			if(isKind(COLON)) {
				consume();
				if(isKind(NAME)){
					ExpName end = new ExpName(t);
					nameList.add(end);
				}
			}          
    	  }
    	consume();
    	StatFunction sFun = new StatFunction(first,new FuncName(first,nameList.get(0)),funbody());
    	return sFun;
    	}
    	
    	else if(t.kind == Kind.KW_local){
    		List<ExpName> nameList = new ArrayList<>();
    		List<Exp> expList = new ArrayList<>();
    		consume();
    		if(t.kind == Kind.KW_function){
    			consume();
    			if(isKind(NAME)){
    				ExpName en = new ExpName(t);
    				consume();
    			StatLocalFunc slf = new StatLocalFunc(first,new FuncName(first,en),funbody());
    			return slf;
    			}
    		}
    		else if(isKind(NAME)) {
    			 while(isKind(NAME)) {
 					ExpName en = new ExpName(t);
 					consume();
 					nameList.add(en);
 					if(isKind(COMMA)) {
 						consume();
 					}
 		         }
    			 if(isKind(ASSIGN)) {
    				 consume();
    				 while(!isKind(EOF)){
    					 expList.add(exp());
    					 consume();
    				 }
    			 }
    		StatLocalAssign sla = new StatLocalAssign(first,nameList,expList);
    		return sla;
    		}
    	}
    	
    	else if(t.kind == Kind.COLONCOLON) 
		{
			consume();
			if(t.kind == Kind.NAME) {
				 temp = consume();
				if(t.kind == Kind.COLONCOLON) {
				Name label = new Name(temp,temp.getName());
			StatLabel sL = new StatLabel(temp,label);
			consume();
			return sL;
			}}
		}
    	else if(t.kind == Kind.KW_break){
			 StatBreak sB = new StatBreak(t);
			 consume();
			 return sB;
		}
    	else if(t.kind == Kind.KW_do)
		{
			consume();
			/*
			 * List<Stat> stats = new ArrayList<Stat>(); stats.add(andBlock());
			 */
			block = insideBlock();
			if(check.kind == Kind.KW_end) {
			StatDo sD = new StatDo(t,block);
			consume();
			return sD;
			}
		}
    	else if(t.kind == Kind.KW_goto)
		{
			 consume();
			 if(t.kind == Kind.NAME) {
				 Name label = new Name(t,t.getName());
				 StatGoto sG = new StatGoto(t,label);
				 consume();
				 return sG;
			 }
		}
    	else if(t.kind == Kind.KW_while) 
		{
			consume();
			e10 = exp();
			if(t.kind == Kind.KW_do)
			{
				consume();
				
				block = insideBlock();
				if(check.kind == Kind.KW_end) {
					StatWhile sW = new StatWhile(first,e10,block);
					consume();
					return sW;
				}
			}
		}
    	else if(t.kind == Kind.KW_repeat) {
			consume();
			block = insideBlock();
			if(check.kind == Kind.KW_until) {
				consume();
				e10 = exp();
				consume();
				if(e10!= null) {
					StatRepeat sR = new StatRepeat(first, block, e10);
					consume();
					return sR;
				}
			}
		}
		
    	else if(t.kind == Kind.KW_if) {
			consume();
			e10 = exp();
			List<Exp> expList = new ArrayList<>();
			List<Block> blockList = new ArrayList<>();
			expList.add(e10);
			if(t.kind == Kind.KW_then){
				consume();
				block = insideBlock();
				blockList.add(block);
					while(check.kind == Kind.KW_elseif) {
						consume();
						expList.add(exp());
						if(t.kind == Kind.KW_then){
							consume();
							block = insideBlock();
							blockList.add(block);
					}
				  consume();
					}
					if(check.kind == Kind.KW_else) {
						consume();
						block = insideBlock();
						blockList.add(block);
						consume();
					}
					if(check.kind == Kind.KW_end){
						StatIf sI = new StatIf(first,expList,blockList);
						consume();
						return sI ;
					}
		}}
    	else if(t.kind == Kind.KW_for){
			List<ExpName> nameList = new ArrayList<>();
			List<Exp> expList   = new ArrayList<>();
			consume();
				while(isKind(NAME)) {
					ExpName inName = new ExpName(t.text);
					consume();
					nameList.add(inName);
					if(isKind(COMMA)) {
						consume();
					}
		}
		if(nameList.size() > 1){
		if(t.kind == Kind.KW_in) {
			while(t.kind != Kind.KW_do){
			consume();
			expList.add(exp());
			}
			while(t.kind != Kind.KW_end){
			consume();
			block = insideBlock();
			}
		}
		StatForEach sFe = new StatForEach(first,nameList,expList,block);
		consume();
		return sFe;
		}else {
		if(t.kind == Kind.ASSIGN) {
			consume();
			expList.add( exp());
		if(t.kind == Kind.COMMA){
			consume();
			expList.add( exp());
			while(t.kind == Kind.COMMA) {
		    consume();
			expList.add( exp());
			}
		if(t.kind == Kind.KW_do){
			consume();
			block = insideBlock();
			if(t.kind == Kind.KW_end){
				StatFor sF = new StatFor(first,nameList.get(0),expList.get(0),expList.get(1),expList.get(2),block);
				consume();
				return sF;
			}
		}}}}}
    	else if(isKind(KW_return)) {
			RetStat eS = returnStat();
			consume();
			return eS;
		}
    	else if(isKind(LPAREN)) {
    		List<Exp>expList = new ArrayList<>();
    		List<Exp>varList = makeVarLparen();
    		if(isKind(ASSIGN)){
    			consume();
    			expList.add(exp());
    			while(isKind(COMMA)) {
    				consume();
    				expList.add(exp());
    			}}
    		
    	StatAssign sA = new StatAssign(first,varList,expList);
    	return sA;}
    	
    	else if(isKind(NAME))
    	{
    		List<Exp>expList = new ArrayList<>();
        	List<Exp> nameList = new ArrayList<>();
    		nameList = makeVarName();
    		if(isKind(ASSIGN)){
			consume();
			expList.add(exp());
		}
    		/*else if(nameList.size() ==1 ) {
			ExpFunctionCall expfunc = (ExpFunctionCall) nameList.get(0);
			
		}*/
	StatAssign sA = new StatAssign(first,nameList,expList);
	return sA;}
    	
		return stat;
 }
	
    RetStat returnStat() throws Exception{
    	Token first = t;
			List<Exp> expList   = new ArrayList<>();
			consume();
			while(!isKind(EOF)){
				expList.add( exp());
			}
			RetStat reStat = new RetStat(first,expList);
			return reStat;
    }
    
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
	Exp e0 = null;
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
		//consume();
		Token first = t;
		List<Name> nameList = new ArrayList<>();
		List<Stat> statList = new ArrayList<Stat>();
		if(isKind(LPAREN)) {
			consume();
			if(isKind(NAME)) {while(isKind(NAME)) {
				Name inName = new Name(t,t.text);
				consume();
				nameList.add(inName);
				if(isKind(COMMA)) {
					consume();
					if(isKind(DOTDOTDOT)) {
						hasVarArgs = true;
						Name inName1 = new Name(t,t.text);
						nameList.add(inName1);
						ParList parList = new ParList(t,nameList,hasVarArgs);
						setParList(parList);
						consume();
					}
				}
			}}
			else if(isKind(DOTDOTDOT)) {
				hasVarArgs = true;
				Name inName = new Name(t,t.text);
				nameList.add(inName);
				ParList parList = new ParList(t,nameList,hasVarArgs);
				setParList(parList);
				consume();
			}
			
			if(hasVarArgs == false) {
				ParList parList = new ParList(t,nameList,false);
				setParList(parList);
			}
		}else {
			error(t.kind);
		}
		
		if(isKind(RPAREN)) {
			consume();
			statList.add(andBlock());
			if(isKind(KW_end)) {
				consume();
				FuncBody funcb = new FuncBody(first,getParList(),new Block(first,statList));
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
				e10 = exp();
				 if (t.kind == Kind.RPAREN) {
				consume();
				 }
				 return e10;
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
				consume();
				e10 = new ExpFunction(t, funbody());
				while(t.kind != EOF) {
					Kind op = t.kind;
					consume();
					Exp e1 = exp();
					e10 = new ExpBinary(t, e10, op, e1);
				}
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
