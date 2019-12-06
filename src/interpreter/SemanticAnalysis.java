package interpreter;

import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import cop5556fa19.AST.Block;
import cop5556fa19.AST.Exp;
import cop5556fa19.AST.ExpBinary;
import cop5556fa19.AST.ExpFalse;
import cop5556fa19.AST.ExpFunctionCall;
import cop5556fa19.AST.ExpInt;
import cop5556fa19.AST.ExpName;
import cop5556fa19.AST.ExpNil;
import cop5556fa19.AST.ExpString;
import cop5556fa19.AST.ExpTable;
import cop5556fa19.AST.ExpTableLookup;
import cop5556fa19.AST.ExpTrue;
import cop5556fa19.AST.Name;
import cop5556fa19.AST.RetStat;
import cop5556fa19.AST.Stat;
import cop5556fa19.AST.StatAssign;
import cop5556fa19.AST.StatDo;
import cop5556fa19.AST.StatGoto;
import cop5556fa19.AST.StatIf;
import cop5556fa19.AST.StatLabel;
import cop5556fa19.AST.StatWhile;
import cop5556fa19.AST.TableDeref;

public class SemanticAnalysis {
	
	HashMap<Integer,StatGoto> ScopeGoToList = new HashMap<>();
	HashMap<Name,Name> MatchGoTo = new HashMap<>();
	HashMap<Integer,StatLabel> ScopeLabelList = new HashMap<>();
	int scope = 1;
	boolean labelExist = false;
	ASTVisitorSubAdapt astSub = new ASTVisitorSubAdapt();
	public boolean semantics(Block block, Object arg) throws StaticSemanticException {
		visitBlock(block,arg);
		Set<Integer> keys = ScopeGoToList.keySet();
		for(int key:keys) {
			/*
			 * if(ScopeLabelList.containsKey(key)) { ScopeLabelList.remove(key); }
			 */		}
		if(ScopeGoToList.size() == ScopeLabelList.size() ) {
			return true;
		}
		else if(ScopeGoToList.size() == 0){
			return true;
		}
		else {
		return false;}
	}
	
	@SuppressWarnings("unchecked")
	public Object visitBlock(Block block, Object arg) throws StaticSemanticException  {
		scope++;
      List<Stat> statList = block.getStat();
      List<LuaValue> lua = new ArrayList<LuaValue>();
    	  try {
      int len = statList.size();
    		  for(int i=0; i<len; i++){
    			  Stat iter = statList.get(i);
            	  if(iter instanceof RetStat) {
            	  }
            	  else if(iter instanceof StatAssign) {
            		 astSub.visitStatAssign((StatAssign)iter,arg);
            	  }
            	  else if(iter == null){
            		  lua = null;
            	  }
            	  else if(iter instanceof StatDo){
            		  Block blc = ((StatDo)iter).b;
            		  visitBlock(blc, arg);
             	  }
            	  else if(iter instanceof StatGoto){
            		  ScopeGoToList.put(scope,(StatGoto) iter);
             	  }
            	  else if(iter instanceof StatLabel){
            		  labelExist = false;
            		Collection<StatGoto> goLabels = ScopeGoToList.values();
            		List<StatGoto> labelList =  new ArrayList<>();   
            		for(StatGoto s: goLabels) {
            			labelList.add(s);
            		}
            		for(int k=0; k<labelList.size();k++) {
            			StatGoto sg = labelList.get(k);
            			String lab =(((StatLabel)iter).label).name;
            			String goN = sg.name.name;
            			if( (lab.equalsIgnoreCase(goN)) && (!(MatchGoTo.containsKey(sg.name)))){
            				MatchGoTo.put(sg.name, ((StatLabel)iter).label);
            				labelExist = true;
            			}
            		}if(labelExist){
            			
            			 ScopeLabelList.put(scope,(StatLabel) iter);
            		}
             	  }
            	  else if(iter instanceof StatIf){
            		  visitStatIf((StatIf)iter, arg);
            	  }
            	  else if(iter instanceof StatWhile){
             	  }
              }
      } catch (Exception e) {
			
		}
    	  scope--;
      return lua;
	}
	@SuppressWarnings("unchecked")
	public void visitStatIf(StatIf statIf, Object arg) throws Exception {
		List<Exp> expList = new ArrayList<>();
		List<Block> blockList = new ArrayList<>();
		boolean attempt = false;
		List<LuaValue> luaExp = new ArrayList<>();
		LuaValue val = null;
		int Sv = 1;
		boolean there = false;
		expList = statIf.es;
		blockList = statIf.bs;
		   for(int i=0; i<expList.size(); i++) {
			   luaExp = (List<LuaValue>)( astSub.expConstruct(expList.get(i), arg));
			   if(luaExp.get(0) instanceof LuaBoolean || luaExp.get(0) instanceof LuaInt || luaExp.get(0) instanceof LuaString || luaExp.get(0) instanceof LuaValue){
				   if(luaExp.get(0) instanceof LuaString) {
					   if(astSub.variableList.containsKey(expList.get(i))) {
						    val = astSub.variableList.get(expList.get(i));
						    Sv = ((LuaInt)val).v;
						    if(Sv == 0) {
						    	there = true;
						    }
					   }}
		   }
			   if((attempt == false) && ((luaExp.get(0) instanceof LuaString && there )||(luaExp.get(0) instanceof LuaBoolean && (((LuaBoolean)luaExp.get(0)).value == true))||(luaExp.get(0) instanceof LuaInt && (((LuaInt)luaExp.get(0)).v == 0)))) {
				   
				   scope++;
				   List<LuaValue> lua = (List<LuaValue>)( visitBlock(blockList.get(i),arg));
				   attempt = true;
			   }}
	}

	
}
