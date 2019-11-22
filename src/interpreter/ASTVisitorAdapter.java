package interpreter;

import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import built_ins.print;
import built_ins.println;
import cop5556fa19.Token;
import cop5556fa19.Token.Kind;
import cop5556fa19.AST.ASTVisitor;
import cop5556fa19.AST.Block;
import cop5556fa19.AST.Chunk;
import cop5556fa19.AST.Exp;
import cop5556fa19.AST.ExpBinary;
import cop5556fa19.AST.ExpFalse;
import cop5556fa19.AST.ExpFunction;
import cop5556fa19.AST.ExpFunctionCall;
import cop5556fa19.AST.ExpInt;
import cop5556fa19.AST.ExpList;
import cop5556fa19.AST.ExpName;
import cop5556fa19.AST.ExpNil;
import cop5556fa19.AST.ExpString;
import cop5556fa19.AST.ExpTable;
import cop5556fa19.AST.ExpTableLookup;
import cop5556fa19.AST.ExpTrue;
import cop5556fa19.AST.ExpUnary;
import cop5556fa19.AST.ExpVarArgs;
import cop5556fa19.AST.Field;
import cop5556fa19.AST.FieldExpKey;
import cop5556fa19.AST.FieldImplicitKey;
import cop5556fa19.AST.FieldList;
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

public abstract class ASTVisitorAdapter implements ASTVisitor {
	
	
	
	@SuppressWarnings("serial")
	public
	static class TypeException extends Exception{

		public TypeException(String msg) {
			super(msg);
		}
		
		public TypeException(Token first, String msg) {
			super(first.line + ":" + first.pos + " " + msg);
		}
		
	}
	HashMap<Exp,LuaValue> variableList = new HashMap<>();
	HashMap<Integer,StatGoto> ScopeGoToList = new HashMap<>();
	HashMap<Integer,StatLabel> ScopeLabelList = new HashMap<>();
	int scope=1;
	boolean gotoFlag = false;
	boolean labelFlag =  false;
	boolean retFlag = true;
	boolean sem =  false;
	public abstract List<LuaValue> load(Reader r) throws Exception;

	@Override
	public Object visitExpNil(ExpNil expNil, Object arg) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object visitExpBin(ExpBinary expBin, Object arg) throws Exception {
		Exp e0 = expBin.e0;
		Exp e1 = expBin.e1;
		Kind op = expBin.op;
		LuaInt lua = new LuaInt();
		int result=0;
		LuaInt tempE1 = new LuaInt();
		LuaInt tempE2 = new LuaInt();
			
			if(e0 instanceof ExpName) {
				if(variableList.containsKey(e0)) {
					tempE1 =(LuaInt) variableList.get(e0);
				}else {
					tempE1 = (LuaInt) LuaNil.nil;
				}
			}else {
				tempE1 = new LuaInt(((ExpInt)e0).v);
			}
			
			if(e1 instanceof ExpName) {
				if(variableList.containsKey(e1)) {
					tempE2 =(LuaInt) variableList.get(e1);
				}else {
					tempE2 = (LuaInt) LuaNil.nil;
				}
			}else {
				tempE2 = new LuaInt(((ExpInt)e1).v);
			}
			
				if(op == Kind.OP_DIV) {
					result = (tempE1).v / tempE2.v;
				}
				else if(op == Kind.OP_MINUS) {
					result = (tempE1).v - tempE2.v;
				}
				else if(op == Kind.OP_MOD) {
					result = (tempE1).v % tempE2.v;
				}
				else if(op == Kind.OP_PLUS) {
					result = (tempE1).v + tempE2.v;
				}
				else if(op == Kind.OP_POW) {
					result = (tempE1).v ^ tempE2.v;
				}
				else if(op == Kind.OP_TIMES) {
					result = (tempE1).v * tempE2.v;
				}
		
		return new LuaInt(result);
	}

	@Override
	public Object visitUnExp(ExpUnary unExp, Object arg) throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object visitExpInt(ExpInt expInt, Object arg) {
            LuaInt ret = new LuaInt(expInt.v);
            return ret;
	}

	@Override
	public Object visitExpString(ExpString expString, Object arg) {
		LuaString ret = new LuaString(expString.v);
		return ret;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object visitExpTable(ExpTable expTableConstr, Object arg) throws Exception {
		LuaTable lua = new LuaTable();
		ExpTable eT = expTableConstr;
		List<Field> fields = new ArrayList<>();
		fields = eT.fields;
		for(int i=0; i < fields.size(); i++) {
			Field f = fields.get(i);
			if(f instanceof FieldImplicitKey) {
				lua.putImplicit((LuaValue) visitFieldImplicitKey((FieldImplicitKey) f, arg));
			}
			else if(f instanceof FieldExpKey) {
				HashMap<LuaValue,LuaValue> res = new HashMap<>();
				res = (HashMap<LuaValue, LuaValue>) visitFieldExpKey((FieldExpKey) f,arg);
				Set<LuaValue> keys = res.keySet();
				for(LuaValue key: keys) {
					lua.put(key,res.get(key));
				}
			}
			else if(f instanceof FieldNameKey){
				HashMap<LuaValue,LuaValue> res = new HashMap<>();
				res = (HashMap<LuaValue, LuaValue>) visitFieldNameKey((FieldNameKey) f,arg);
				Set<LuaValue> keys = res.keySet();
				for(LuaValue key: keys) {
					lua.put(key,res.get(key));
				}
			}
		}
        return lua;
	}

	@Override
	public Object visitExpList(ExpList expList, Object arg) throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object visitParList(ParList parList, Object arg) throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object visitFunDef(ExpFunction funcDec, Object arg) throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object visitName(Name name, Object arg) {
		throw new UnsupportedOperationException();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object visitBlock(Block block, Object arg) throws StaticSemanticException  {
      List<Stat> statList = block.getStat();
      List<LuaValue> lua = new ArrayList<LuaValue>();
    	  try {
      int len = statList.size();
      int k =0;
      while(k<len) {
    	  if((k==0) || ((k-1)>=0) && retFlag) {
    		  for(int i=k; i< statList.size(); i++){
    			  Stat iter = statList.get(i);
    			  if(gotoFlag) {
    				  while((!(iter instanceof StatLabel)) && i <(statList.size()-1)) {
    					  i++;
    					  k=i;
    					  iter = statList.get(i);  
    				  }
    			  }
            	  
            	  if(iter instanceof RetStat) {
						lua = (List<LuaValue>) visitRetStat((RetStat) iter, arg);
            		  break;
            	  }
            	  else if(iter instanceof StatAssign) {
            		  lua = (List<LuaValue>) visitStatAssign((StatAssign)iter, arg);
            		  break;
            	  }
            	  else if(iter == null){
            		  lua = null;
            	  }
            	  else if(iter instanceof StatDo){
            		  lua = (List<LuaValue>) visitStatDo((StatDo)iter, arg);
            		  break;
             	  }
            	  else if(iter instanceof StatGoto){
            		  lua = (List<LuaValue>) visitStatGoto((StatGoto)iter, arg);
            		  gotoFlag = true;
            		  int j=i;
            			  Stat iter1 = statList.get(j);
            			  if(gotoFlag) {
            				  while((!(iter1 instanceof StatLabel)) && j <(statList.size()-1)) {
            					  j++;
            					  iter1 = statList.get(j);  
            				  }
            				  if(iter1 instanceof StatLabel) {
            					  break;
            				  }else { throw new StaticSemanticException(block.firstToken, "GoTo");}
            			  }
             	  }
            	  else if(iter instanceof StatLabel){
            		  lua = (List<LuaValue>) visitLabel((StatLabel)iter, arg);
            		  break;
             	  }
            	  else if(iter instanceof StatIf){
            		  lua = (List<LuaValue>) visitStatIf((StatIf)iter,arg);
            		  break;
            	  }
            	  else if(iter instanceof StatWhile){
            		  lua = (List<LuaValue>) visitStatWhile((StatWhile)iter, arg);
            		  break;
             	  }
              }
    	  }else {break;}
    	  k++;
      }} catch (Exception e) {
			
		}
      scope--;
      return lua;
	}

	@SuppressWarnings("unchecked")
	public Object expConstruct(Exp e, Object arg) throws Exception {
		List<LuaValue> lua = new ArrayList<LuaValue>();
		if(e instanceof ExpInt) {
			lua.add((LuaValue) visitExpInt((ExpInt) e, arg));
		}
		else if(e instanceof ExpName){
			lua.add((LuaValue) visitExpName((ExpName) e, arg));
		}
		else if(e instanceof ExpTrue){
			lua.add((LuaValue) visitExpTrue((ExpTrue) e, arg));
		}
		else if(e instanceof ExpFalse){
			lua.add((LuaValue) visitExpFalse((ExpFalse) e, arg));
		}
		else if(e instanceof ExpString){
			lua.add((LuaValue) visitExpString((ExpString) e, arg));
		}
		else if(e instanceof ExpBinary){
			lua.add((LuaValue) visitExpBin((ExpBinary) e, arg));
		}
		else if(e instanceof ExpFunctionCall){
			lua = (List<LuaValue>) visitExpFunctionCall((ExpFunctionCall) e, arg);
		}
		else if(e instanceof ExpNil){
			LuaNil ln = new LuaNil();
			lua.add(ln);
		}
		else if(e instanceof ExpTable){
			ExpTable eT = (ExpTable)e;
			LuaTable lT = new LuaTable();
			if((eT.fields).isEmpty()) {
				lua.add(lT);
			}else {
				lua.add((LuaValue) visitExpTable((ExpTable)e, arg));
			}
		}
		else if(e instanceof ExpTableLookup) {
			ExpTableLookup eTL = (ExpTableLookup) e;
			lua.add((LuaValue) visitExpTableLookup(eTL,arg));
		}
 return lua;
	} 
	@Override
	public Object visitStatBreak(StatBreak statBreak, Object arg, Object arg2) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object visitStatBreak(StatBreak statBreak, Object arg) throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object visitStatGoto(StatGoto statGoto, Object arg) throws Exception {
		List<LuaValue> lua = new ArrayList<LuaValue>();
		ScopeGoToList.put(scope,statGoto);
		return lua;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object visitStatDo(StatDo statDo, Object arg) throws Exception {
		List<LuaValue> lua = new ArrayList<LuaValue>();
		Block blc = statDo.b;
		scope++;
		lua = (List<LuaValue>) visitBlock(blc,arg);
		return lua;
	}

	@Override
	public Object visitStatWhile(StatWhile statWhile, Object arg) throws Exception {
		Exp exp = statWhile.e;
		Object vis = null;
		if(exp instanceof ExpBinary) {
		Exp e0 = ((ExpBinary)exp).e0;
		Exp e1 = ((ExpBinary)exp).e1;
		Kind op = ((ExpBinary)exp).op;
			if(e0 instanceof ExpName && (e1 instanceof ExpInt)) {
          int comp = Integer.compare(((LuaInt)(variableList.get(e0))).v,((LuaInt)visitExpInt((ExpInt)e1, arg)).v);
               if(op == Kind.REL_EQEQ) {
            	   while(comp ==0) {
   					vis = visitBlock(statWhile.b,arg);
   					comp = Integer.compare(((LuaInt)(variableList.get(e0))).v,((LuaInt)visitExpInt((ExpInt)e1, arg)).v);
   					}
               }
               else if(op == Kind.REL_GE) {
            	   while(comp >= 0) {
      					vis = visitBlock(statWhile.b,arg);
      					comp = Integer.compare(((LuaInt)(variableList.get(e0))).v,((LuaInt)visitExpInt((ExpInt)e1, arg)).v);
      					}}
            	   else if(op == Kind.REL_GT) {
                	   while(comp > 0) {
          					vis = visitBlock(statWhile.b,arg);
          					comp = Integer.compare(((LuaInt)(variableList.get(e0))).v,((LuaInt)visitExpInt((ExpInt)e1, arg)).v);
          					}
               }
            	   else if(op == Kind.REL_LE) {
                	   while(comp <= 0) {
          					vis = visitBlock(statWhile.b,arg);
          					comp = Integer.compare(((LuaInt)(variableList.get(e0))).v,((LuaInt)visitExpInt((ExpInt)e1, arg)).v);
          					}
               }
            	   else if(op == Kind.REL_LT) {
                	   while(comp < 0) {
          					vis = visitBlock(statWhile.b,arg);
          					comp = Integer.compare(((LuaInt)(variableList.get(e0))).v,((LuaInt)visitExpInt((ExpInt)e1, arg)).v);
          					}
               }
            	   else if(op == Kind.REL_NOTEQ) {
                	   while(comp != 0) {
          					vis = visitBlock(statWhile.b,arg);
          					comp = Integer.compare(((LuaInt)(variableList.get(e0))).v,((LuaInt)visitExpInt((ExpInt)e1, arg)).v);
          					}
               }
			}
			
		}
		return vis;
	}

	@Override
	public Object visitStatRepeat(StatRepeat statRepeat, Object arg) throws Exception {
		throw new UnsupportedOperationException();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object visitStatIf(StatIf statIf, Object arg) throws Exception {
		List<Exp> expList = new ArrayList<>();
		List<Block> blockList = new ArrayList<>();
		List<LuaValue> lua = new ArrayList<>();
		List<LuaValue> luaExp = new ArrayList<>();
		LuaValue val = null;
		int Sv = 1;
		boolean attempt = false;
		boolean there = false;
		expList = statIf.es;
		blockList = statIf.bs;
		int s = blockList.size();
		LuaNil luaNil = new LuaNil();
		   for(int i=0; i<expList.size();i++ ){
			   luaExp = (List<LuaValue>)( expConstruct(expList.get(i), arg));
			   if(luaExp.get(0) instanceof LuaBoolean || luaExp.get(0) instanceof LuaInt || luaExp.get(0) instanceof LuaString || luaExp.get(0) instanceof LuaValue){
				   if(luaExp.get(0) instanceof LuaString) {
					   if(variableList.containsKey(expList.get(i))) {
						    val = variableList.get(expList.get(i));
						    Sv = ((LuaInt)val).v;
						    if(Sv == 0) {
						    	there = true;
						    }
					   }}
				   if((attempt == false) && ((luaExp.get(0) instanceof LuaString && there )||(luaExp.get(0) instanceof LuaBoolean && (((LuaBoolean)luaExp.get(0)).value == true))||(luaExp.get(0) instanceof LuaInt && (((LuaInt)luaExp.get(0)).v == 0)))) {
					   
					   scope++;
					   lua = (List<LuaValue>)( visitBlock(blockList.get(i),arg));
					   attempt = true;
				   }
				   else {
					   lua.add(luaNil.nil);
				   }
			   }else {lua = (List<LuaValue>)( visitBlock(blockList.get(s-1),arg));}
		    }
		return lua;
	}

	@Override
	public Object visitStatFor(StatFor statFor1, Object arg) throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object visitStatForEach(StatForEach statForEach, Object arg) throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object visitFuncName(FuncName funcName, Object arg) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object visitStatFunction(StatFunction statFunction, Object arg) throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object visitStatLocalFunc(StatLocalFunc statLocalFunc, Object arg) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object visitStatLocalAssign(StatLocalAssign statLocalAssign, Object arg) throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object visitRetStat(RetStat retStat, Object arg) throws Exception {
		List<LuaValue> lua = new ArrayList<LuaValue>();
		List<Exp> expList= retStat.getValue();
		retFlag = false;
		for(Exp e: expList) {
			if(e instanceof ExpInt) {
				lua.add((LuaValue) visitExpInt((ExpInt) e, arg));
			}
			else if(e instanceof ExpName) {
				if(variableList.containsKey(e)) {
					lua.add(variableList.get(e));
				}
				else {
					LuaNil ln = new LuaNil();
					lua.add(ln.nil);
				}
			}
			else if(e instanceof ExpTableLookup) {
				lua.add((LuaValue)visitExpTableLookup((ExpTableLookup)e, arg));
			}
		}
		return lua;
	}
	

	@SuppressWarnings("unchecked")
	@Override
	public Object visitChunk(Chunk chunk, Object arg) throws Exception {
		Block block = chunk.getBlock();
		SemanticAnalysis sa = new SemanticAnalysis();
		 sem = sa.semantics(block, arg);
		if(!sem) {
			throw new StaticSemanticException(block.firstToken, "GoTo");
		}
		List<LuaValue> lua = new ArrayList<LuaValue>();
		lua = (List<LuaValue>) visitBlock(block,arg);
		if(lua == null || lua.isEmpty()) {
			return null;
		}
		return lua;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Object visitFieldExpKey(FieldExpKey fieldExpKey, Object arg) throws Exception {
		Exp eK = fieldExpKey.key;
		Exp eV = fieldExpKey.value;
		LuaValue eVL, eKL;
		HashMap<LuaValue,LuaValue> res = new HashMap<>();
		if(eV == null) {
			eV = eK;
		}
		if(eV instanceof ExpName) {
			if(variableList.containsKey(eV)) {
					eVL = variableList.get(eV);}
			else {eVL = LuaNil.nil;}
		}else {
			
			eVL = ((List<LuaValue>) expConstruct(eV, arg)).get(0) ;
		}
		
			if(variableList.containsKey(eK)) {
			eKL = variableList.get(eK);
			res.put( eKL,eVL);
			}
		else {
			variableList.put(eK,eVL);
			eKL = ((List<LuaValue>) expConstruct(eK, arg)).get(0) ;
		res.put(eKL ,eVL );}
		return res;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Object visitFieldNameKey(FieldNameKey fieldNameKey, Object arg) throws Exception {
		Name key = fieldNameKey.name;
		ExpName eK = new ExpName(key.name);
		Exp eV = fieldNameKey.exp;
		HashMap<LuaValue,LuaValue> res = new HashMap<>();
		res.put(((List<LuaValue>) expConstruct(eK, arg)).get(0) ,((List<LuaValue>) expConstruct(eV, arg)).get(0) );
		return res;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public Object visitFieldImplicitKey(FieldImplicitKey fieldImplicitKey, Object arg) throws Exception {
		Exp eF = fieldImplicitKey.exp;
		LuaValue lua = null;
		if(eF instanceof ExpName) {
			if(variableList.containsKey(eF)) {
				lua = variableList.get(eF);}
		else {lua = LuaNil.nil;}

		}else {
			lua = ((List<LuaValue>) expConstruct(eF, arg)).get(0);
		}
		return lua;
	}

	@Override
	public Object visitExpTrue(ExpTrue expTrue, Object arg) {
		LuaBoolean lb = new LuaBoolean(true);
		return lb;
	}

	@Override
	public Object visitExpFalse(ExpFalse expFalse, Object arg) {
		LuaBoolean lb = new LuaBoolean(false);
		return lb;
	}

	@Override
	public Object visitFuncBody(FuncBody funcBody, Object arg) throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object visitExpVarArgs(ExpVarArgs expVarArgs, Object arg) {
		throw new UnsupportedOperationException();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object visitStatAssign(StatAssign statAssign, Object arg) throws Exception {
		List<Exp>expList = new ArrayList<>();
		List<Exp>varList = new ArrayList<>();
		List<LuaValue> lua = new ArrayList<>();
		expList = statAssign.expList;
		varList = statAssign.varList;
		Exp varVal = null;
		if(expList.size() == varList.size()) {
			for(int i=0; i<expList.size();i++ ){
				varVal =  varList.get(i);
				lua = (List<LuaValue>)( expConstruct(expList.get(i), arg));
				if(varVal instanceof ExpTableLookup) {
					EditExpTable((ExpTableLookup) varVal, lua.get(0));
				}
				else {
					if(variableList.containsKey(varVal)) {
						 variableList.replace(varVal,lua.get(0));
						}else {
						 variableList.put(varVal,lua.get(0));}
					}
				}
				
		}
		return lua;
	}
	
    @SuppressWarnings("unchecked")
	private void EditExpTable(ExpTableLookup varVal, LuaValue lua) throws Exception {
	ExpName t = (ExpName) varVal.table;
	Exp k = varVal.key;
	if(variableList.containsKey(t)) {
		LuaValue eT = variableList.get(t);
		LuaValue l = ((List<LuaValue>)( expConstruct(k, null))).get(0);
		if(variableList.containsKey(k)) {
			l = variableList.get(k);
		}
		if(l instanceof LuaInt) {
			int i=1;
			while(i<=((LuaInt)l).v) {
				if(i ==((LuaInt)l).v ) {
					((LuaTable)eT).putImplicit(lua);
				}else {
					((LuaTable)eT).putImplicit(((LuaTable)eT).get(new LuaInt(i)));	
				}
				i++;
			}
		}else {
			((LuaTable)eT).putImplicit(lua);
		}
	}
    }
	
	@Override
	@SuppressWarnings("unchecked")
	public Object visitExpTableLookup(ExpTableLookup e, Object arg) throws Exception {
		ExpTableLookup input = e;
		LuaTable lT = new LuaTable();
		LuaValue val = null,eV = null;
		Exp eVal = input.key;
		if(variableList.containsKey(eVal)) {
		 eV = variableList.get(eVal);
		}else {
		 eV = ((List<LuaValue>) expConstruct(eVal, arg)).get(0);
		}
		if(variableList.containsKey(input.table)) {
			lT = (LuaTable) variableList.get(input.table);
			if((lT.get(eV)).equals(null)){
			   val = eV;
			}else {
				val = lT.get(eV);
			}
		}
		return val;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object visitExpFunctionCall(ExpFunctionCall expFc, Object arg) throws Exception {
		ExpName e =(ExpName) expFc.f;
		List<Exp> expList = new ArrayList<>();
		List<LuaValue> luaExp = new ArrayList<>();
		expList = expFc.args;
		for(int i=0; i<expList.size();i++ ){
			Exp ex = expList.get(i);
			if(ex instanceof ExpName) {
				if(variableList.containsKey((ExpName)ex)) {
					luaExp.add(variableList.get(ex));
				}
				else {
					LuaNil ln = new LuaNil();
					luaExp.add(ln.nil);
				}
			}else {luaExp.add(((List<LuaValue>)( expConstruct(ex, arg))).get(0));}
			   }
		if((e.name).equals("print")) {
			print p = new print();
			p.call(luaExp);
		}
		if((e.name).equals("println")) {
			println p = new println();
			p.call(luaExp);
		}
		return luaExp;
	}

	@Override
	public Object visitLabel(StatLabel statLabel, Object ar) {
		StatGoto g = null;
		if(gotoFlag) {
			int i = 0;
		Set<Integer> keys = ScopeGoToList.keySet();
		for(int key:keys) {
			if(key>scope && i==0) {
				g=	ScopeGoToList.get(key);
				i++;
			}
		}
			//g=	ScopeGoToList.get(2);
		if(((g.name).name).equalsIgnoreCase(((statLabel.label).name))) {
			gotoFlag = false;
		}
		}
		List<LuaValue> lua = new ArrayList<LuaValue>();
		ScopeLabelList.put(scope,statLabel);
		return lua;
	}

	@Override
	public Object visitFieldList(FieldList fieldList, Object arg) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object visitExpName(ExpName expName, Object arg) {
		 LuaString ret = new LuaString(expName.name);
         return ret;
	}



}
