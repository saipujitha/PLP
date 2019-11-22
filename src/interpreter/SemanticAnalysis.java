package interpreter;

import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import cop5556fa19.AST.Block;
import cop5556fa19.AST.Exp;
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
	HashMap<Integer,StatLabel> ScopeLabelList = new HashMap<>();
	int scope = 1;
	
	public boolean semantics(Block block, Object arg) throws StaticSemanticException {
		visitBlock(block,arg);
		Set<Integer> keys = ScopeGoToList.keySet();
		for(int key:keys) {
			if(ScopeLabelList.containsKey(key)) {
				ScopeLabelList.remove(key);
			}
		}
		if(ScopeGoToList.size() == ScopeLabelList.size() ) {
			return true;
		}else {
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
            		  
            		  ScopeLabelList.put(scope,(StatLabel) iter);
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
	public void visitStatIf(StatIf statIf, Object arg) throws Exception {
		List<Exp> expList = new ArrayList<>();
		List<Block> blockList = new ArrayList<>();
		List<LuaValue> lua = new ArrayList<>();
		List<LuaValue> luaExp = new ArrayList<>();
		LuaValue val = null;
		boolean attempt = false;
		expList = statIf.es;
		blockList = statIf.bs;
		int s = blockList.size();
		LuaNil luaNil = new LuaNil();
		   for(int i=0; i<blockList.size();i++ ){
			   visitBlock(blockList.get(i), arg);
			   
		   }
	}


	
}
