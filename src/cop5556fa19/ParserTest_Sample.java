package cop5556fa19;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import cop5556fa19.Parser;
import cop5556fa19.Parser.SyntaxException;
import cop5556fa19.AST.ASTNode;
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
import cop5556fa19.AST.ExpTrue;
import cop5556fa19.AST.ExpVarArgs;
import cop5556fa19.AST.Expressions;
import cop5556fa19.AST.Field;
import cop5556fa19.AST.FieldExpKey;
import cop5556fa19.AST.FieldImplicitKey;
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
import cop5556fa19.AST.StatGoto;
import cop5556fa19.AST.StatIf;
import cop5556fa19.AST.StatLabel;
import cop5556fa19.AST.StatLocalFunc;
import cop5556fa19.AST.StatRepeat;
import cop5556fa19.AST.StatWhile;
import cop5556fa19.Scanner;
import cop5556fa19.Token;

import static cop5556fa19.Token.Kind.*;

class ParserTest_Sample {

	// To make it easy to print objects and turn this output on and off
	static final boolean doPrint = true;
//	static final boolean doPrint = false;

	private void show(Object input) {
		if (doPrint) {
			System.out.println(input.toString());
		}
	}
	
	// creates a scanner, parser, and parses the input by calling exp().  
	Exp parseExpAndShow(String input) throws Exception {
		show("parser input:\n" + input); // Display the input
		Reader r = new StringReader(input);
		Scanner scanner = new Scanner(r); // Create a Scanner and initialize it
		Parser parser = new Parser(scanner);
		Exp e = parser.exp();
		show("e=" + e);
		return e;
	}	
	
	
	// creates a scanner, parser, and parses the input by calling block()  
	Block parseBlockAndShow(String input) throws Exception {
		show("parser input:\n" + input); // Display the input
		Reader r = new StringReader(input);
		Scanner scanner = new Scanner(r); // Create a Scanner and initialize it
		Parser parser = new Parser(scanner);
		Method method = Parser.class.getDeclaredMethod("block");
		method.setAccessible(true);
		Block b = (Block) method.invoke(parser);
		show("b=" + b);
		return b;
	}	
	
	
	//creates a scanner, parser, and parses the input by calling parse()
	//this corresponds to the actual use case of the parser
	Chunk parseAndShow(String input) throws Exception {
		show("parser input:\n" + input); // Display the input
		Reader r = new StringReader(input);
		Scanner scanner = new Scanner(r); // Create a Scanner and initialize it
		Parser parser = new Parser(scanner);
		Chunk c = parser.parse();
		show("c="+c);
		return c;
	}
	
	@Test
	void testEmpty1() throws Exception {
		String input = "";
		Block b = parseBlockAndShow(input);
		Block expected = Expressions.makeBlock();
		assertEquals(expected, b);
	}
	
	@Test
	void testEmpty2() throws Exception {
		String input = "";
		ASTNode n = parseAndShow(input);
		Block b = Expressions.makeBlock();
		Chunk expected = new Chunk(b.firstToken,b);
		assertEquals(expected,n);
	}
	
	@Test
	void testStatLabel() throws Exception{
		//StatLabel s1 = Expressions.makeStatLabel("mylabel");
		String input = "::MyLabel::";
		Block bl = parseBlockAndShow(input);
		StatLabel s1 = Expressions.makeStatLabel("MyLabel");
		Block expected = Expressions.makeBlock(s1);
		assertEquals(expected,bl);
	}
	
	@Test
	void testStatBreak() throws Exception{
		String input = "break";
		Block bl = parseBlockAndShow(input);
		StatBreak statBreak = Expressions.makeStatBreak();
		Block expected = Expressions.makeBlock(statBreak);
		assertEquals(expected,bl);
	}
	
	@Test
	void testStatDo() throws Exception{
		String input = "do ::Name:: end";
		Block bl = parseBlockAndShow(input);
		StatLabel s1 = Expressions.makeStatLabel("Name");
		StatDo statdo = Expressions.makeStatDo(s1);
		Block expected = Expressions.makeBlock(statdo);
		assertEquals(expected,bl);
	}
	
	@Test
	void testStatGoto() throws Exception{
		String input = "goto MyLabel";
		Block bl = parseBlockAndShow(input);
		StatGoto statBreak = Expressions.makeStatGoto("MyLabel");
		Block expected = Expressions.makeBlock(statBreak);
		assertEquals(expected,bl);
	}
	
	
	@Test
	void testAssign1() throws Exception {
		String input = "a=b";
		Block b = parseBlockAndShow(input);		
		List<Exp> lhs = Expressions.makeExpList(Expressions.makeExpNameGlobal("a"));
		List<Exp> rhs = Expressions.makeExpList(Expressions.makeExpNameGlobal("b"));
		StatAssign s = Expressions.makeStatAssign(lhs,rhs);
		Block expected = Expressions.makeBlock(s);
		assertEquals(expected,b);
	}
	
	@Test
	void testAssignChunk1() throws Exception {
		String input = "a=b";
		ASTNode c = parseAndShow(input);		
		List<Exp> lhs = Expressions.makeExpList(Expressions.makeExpNameGlobal("a"));
		List<Exp> rhs = Expressions.makeExpList(Expressions.makeExpNameGlobal("b"));
		StatAssign s = Expressions.makeStatAssign(lhs,rhs);
		Block b = Expressions.makeBlock(s);
		Chunk expected = new Chunk(b.firstToken,b);
		assertEquals(expected,c);
	}
	

    @Test
	void testMultiAssign1() throws Exception {
		String input = "a,c=8,9";
		Block b = parseBlockAndShow(input);		
		List<Exp> lhs = Expressions.makeExpList(
					Expressions.makeExpNameGlobal("a")
					,Expressions.makeExpNameGlobal("c"));
		Exp e1 = Expressions.makeExpInt(8);
		Exp e2 = Expressions.makeExpInt(9);
		List<Exp> rhs = Expressions.makeExpList(e1,e2);
		StatAssign s = Expressions.makeStatAssign(lhs,rhs);
		Block expected = Expressions.makeBlock(s);
		assertEquals(expected,b);		
	}
	

	

	@Test
	void testMultiAssign3() throws Exception {
		String input = "a,c=8,f(x)";
		Block b = parseBlockAndShow(input);		
		List<Exp> lhs = Expressions.makeExpList(
					Expressions.makeExpNameGlobal("a")
					,Expressions.makeExpNameGlobal("c"));
		Exp e1 = Expressions.makeExpInt(8);
		List<Exp> args = new ArrayList<>();
		args.add(Expressions.makeExpNameGlobal("x"));
		Exp e2 = Expressions.makeExpFunCall(Expressions.makeExpNameGlobal("f"),args, null);
		List<Exp> rhs = Expressions.makeExpList(e1,e2);
		StatAssign s = Expressions.makeStatAssign(lhs,rhs);
		Block expected = Expressions.makeBlock(s);
		assertEquals(expected,b);			
	}
	

	
	@Test
	void testAssignToTable() throws Exception {
		String input = "g.a.b = 3";
		Block bl = parseBlockAndShow(input);
		ExpName g = Expressions.makeExpNameGlobal("g");
		ExpString a = Expressions.makeExpString("a");
		Exp gtable = Expressions.makeExpTableLookup(g,a);
		ExpString b = Expressions.makeExpString("b");
		Exp v = Expressions.makeExpTableLookup(gtable, b);
		Exp three = Expressions.makeExpInt(3);		
		Stat s = Expressions.makeStatAssign(Expressions.makeExpList(v), Expressions.makeExpList(three));;
		Block expected = Expressions.makeBlock(s);
		assertEquals(expected,bl);
	}
	
	@Test
	void testAssignTableToVar() throws Exception {
		String input = "x = g.a.b";
		Block bl = parseBlockAndShow(input);
		ExpName g = Expressions.makeExpNameGlobal("g");
		ExpString a = Expressions.makeExpString("a");
		Exp gtable = Expressions.makeExpTableLookup(g,a);
		ExpString b = Expressions.makeExpString("b");
		Exp e = Expressions.makeExpTableLookup(gtable, b);
		Exp v = Expressions.makeExpNameGlobal("x");		
		Stat s = Expressions.makeStatAssign(Expressions.makeExpList(v), Expressions.makeExpList(e));;
		Block expected = Expressions.makeBlock(s);
		assertEquals(expected,bl);
	}
	@Test
	void testStatAssign() throws Exception{
		String input = "a=b<<c";
		Token t = new Token(NAME, "a", 1, 1);
		Token t1 = new Token(NAME, "b", 1, 1);
		Block bl = parseBlockAndShow(input);
		Exp v = Expressions.makeExpNameGlobal("a");
		Exp y = Expressions.makeExpNameGlobal("b");
		Exp c = Expressions.makeExpNameGlobal("c");
		ExpBinary e = new ExpBinary(t1,y,BIT_SHIFTL,c);
		Stat s = Expressions.makeStatAssign(Expressions.makeExpList(v), Expressions.makeExpList(e));
		Block expected = Expressions.makeBlock(s);
		assertEquals(expected,bl);
	}

	@Test
	void testStatwhile() throws Exception{
		String input = "while x == 1 do a=b end";
		List<Exp> expList   = new ArrayList<>();
		Token t = new Token(KW_while, "while", 1, 1);
		Block bl = parseBlockAndShow(input);
		ExpName v = Expressions.makeExpNameGlobal("x");
		Exp y = Expressions.makeExpInt(1);
		Token t1 = new Token(NAME, "x", 1, 1);
		ExpBinary e = new ExpBinary(t1,v,REL_EQEQ,y);
		expList.add(y);
		Stat s = Expressions.makeStatAssign(Expressions.makeExpList(Expressions.makeExpNameGlobal("a")), Expressions.makeExpList(Expressions.makeExpNameGlobal("b")));
		Block blc = Expressions.makeBlock(s);
		StatWhile sw = new StatWhile(t,e, blc);
		Block exp = Expressions.makeBlock(sw);
		assertEquals(exp,bl);
	}
	
	@Test
	void testStatFor() throws Exception{
		String input = "for i = 0 , i < 10 , i + 1 do a = b end";
		List<Exp> expList   = new ArrayList<>();
		Token t = new Token(KW_for, "for", 1, 1);
		Block bl = parseBlockAndShow(input);
		ExpName v = Expressions.makeExpNameGlobal("i");
		Exp y = Expressions.makeExpInt(0);
		expList.add(y);
		Token t1 = new Token(NAME, "i", 1, 1);
		Exp c = Expressions.makeExpInt(10);
		ExpBinary e = new ExpBinary(t1,v,REL_LT,c);
		expList.add(e);
		Exp d = Expressions.makeExpInt(1);
		ExpBinary e1 = new ExpBinary(t1,v,OP_PLUS,d);
		expList.add(e1);
		Stat s = Expressions.makeStatAssign(Expressions.makeExpList(Expressions.makeExpNameGlobal("a")), Expressions.makeExpList(Expressions.makeExpNameGlobal("b")));
		Block blc = Expressions.makeBlock(s);
		StatFor sf = new StatFor(t, v,expList.get(0),expList.get(1),expList.get(2), blc);
		Block exp = Expressions.makeBlock(sf);
		assertEquals(exp,bl);
	}
	
	@Test
	void testStatRepeat() throws Exception{
		String input = "repeat x=x*y until a==nil;";
		Token t = new Token(KW_repeat, "repeat", 1, 1);
		Token t1 = new Token(NAME, "x", 1, 1);
		Block bl = parseBlockAndShow(input);
		Exp v = Expressions.makeExpNameGlobal("x");
		Exp y = Expressions.makeExpNameGlobal("y");
		ExpBinary e = new ExpBinary(t1,v,OP_TIMES,y);
		Stat s = Expressions.makeStatAssign(Expressions.makeExpList(v), Expressions.makeExpList(e));
		Block blc = Expressions.makeBlock(s);
		Exp a = Expressions.makeExpNameGlobal("a");
		ExpNil b = new ExpNil(new Token(NAME, "a", 1, 1));
		ExpBinary e1 = new ExpBinary(new Token(NAME, "a", 1, 1),a,REL_EQEQ,b);
		StatRepeat rep = new StatRepeat(t,blc,e1);
		Block expected = Expressions.makeBlock(rep);
		assertEquals(expected,bl);
	}
	
	@Test //local function x(c) a = b end
	void testAssignLocal() throws Exception {
		Token t = new Token(KW_local, "local", 1, 1);
		String input = "local function x(c) a = b end";
		Block bl = parseBlockAndShow(input);
		ExpName n = Expressions.makeExpNameGlobal("x");
		FuncName fn = new FuncName(t, n);
		Exp v = Expressions.makeExpNameGlobal("a");
		Exp e = Expressions.makeExpNameGlobal("b");
		Stat s = Expressions.makeStatAssign(Expressions.makeExpList(v), Expressions.makeExpList(e));
		Block blc = Expressions.makeBlock(s);
		Token t1 = new Token(NAME, "c", 1, 1);
		Name inName = new Name(t1,"c");
		List<Name> nameList = new ArrayList<>();
		nameList.add(inName);
		ParList parList = new ParList(t,nameList,false);
		FuncBody fb = new FuncBody(t, parList, blc);
		StatLocalFunc slf = new StatLocalFunc(t,fn,fb);
		Block expected = Expressions.makeBlock(slf);
		assertEquals(expected,bl);
	}

	@Test //if x == 123 then a = b elseif x == 321 then a = c end
	void testFunc() throws Exception {
		Token t = new Token(KW_if, "if", 1, 1);
		List<Exp> expList   = new ArrayList<>();
		List<Block> blockList   = new ArrayList<>();
		List<Stat> statList = new ArrayList<>();
		String input = "if x == 123 then a = b elseif x == 321 then a = c else y=11 end return x,y";
		Chunk bl = parseAndShow(input);
		ExpName n = Expressions.makeExpNameGlobal("x");
		Exp m = Expressions.makeExpInt(123);
		ExpBinary e1 = new ExpBinary(new Token(NAME, "x", 1, 1),n,REL_EQEQ,m);
		expList.add(e1);
		Exp v = Expressions.makeExpNameGlobal("a");
		Exp e = Expressions.makeExpNameGlobal("b");
		Stat s = Expressions.makeStatAssign(Expressions.makeExpList(v), Expressions.makeExpList(e));
		Block blc = Expressions.makeBlock(s);
		blockList.add(blc);
		Exp a = Expressions.makeExpInt(321);
		ExpBinary e2 = new ExpBinary(new Token(NAME, "x", 1, 1),n,REL_EQEQ,a);
		expList.add(e2);
		Stat s1 = Expressions.makeStatAssign(Expressions.makeExpList(v), Expressions.makeExpList(Expressions.makeExpNameGlobal("c")));
		Block blc1 = Expressions.makeBlock(s1);
		Stat s2 = Expressions.makeStatAssign(Expressions.makeExpList(new ExpName("y")), Expressions.makeExpList(Expressions.makeExpInt(11)));
		blockList.add(blc1);
		blockList.add(Expressions.makeBlock(s2));
		StatIf slf = new StatIf(t,expList,blockList);
		statList.add(slf);
		List<Exp> lhs = new ArrayList<>();
		lhs.add(parseExpAndShow("x"));
		lhs.add(parseExpAndShow("y"));
		RetStat statret = new RetStat(t,lhs);
		statList.add(statret);
		Block expected = Expressions.makeBlock(slf,statret);
		Chunk expectedChunk = new Chunk(expected.firstToken, expected);
		assertEquals(expectedChunk,bl);
	}
	
	
	@Test
	void testmultistatements6() throws Exception {
		String input = "x = g.a.b ; ::mylabel:: do  y = 2 goto mylabel f=a(0,200) end break"; //same as testmultistatements0 except ;
		ASTNode c = parseAndShow(input);
		ExpName g = Expressions.makeExpNameGlobal("g");
		ExpString a = Expressions.makeExpString("a");
		Exp gtable = Expressions.makeExpTableLookup(g,a);
		ExpString b = Expressions.makeExpString("b");
		Exp e = Expressions.makeExpTableLookup(gtable, b);
		Exp v = Expressions.makeExpNameGlobal("x");		
		Stat s0 = Expressions.makeStatAssign(v,e);
		StatLabel s1 = Expressions.makeStatLabel("mylabel");
		Exp y = Expressions.makeExpNameGlobal("y");
		Exp two = Expressions.makeExpInt(2);
		Stat s2 = Expressions.makeStatAssign(y,two);
		Stat s3 = Expressions.makeStatGoto("mylabel");
		Exp f = Expressions.makeExpNameGlobal("f");
		Exp ae = Expressions.makeExpNameGlobal("a");
		Exp zero = Expressions.makeExpInt(0);
		Exp twohundred = Expressions.makeExpInt(200);
		List<Exp> args = Expressions.makeExpList(zero, twohundred);
		ExpFunctionCall fc = Expressions.makeExpFunCall(ae, args, null);		
		StatAssign s4 = Expressions.makeStatAssign(f,fc);
		StatDo statdo = Expressions.makeStatDo(s2,s3,s4);
		StatBreak statBreak = Expressions.makeStatBreak();
		Block expectedBlock = Expressions.makeBlock(s0,s1,statdo,statBreak);
		Chunk expectedChunk = new Chunk(expectedBlock.firstToken, expectedBlock);
		assertEquals(expectedChunk,c);
	}
}

