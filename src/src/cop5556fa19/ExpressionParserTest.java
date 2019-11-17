/* *
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

import static cop5556fa19.Token.Kind.*;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

import java.io.Reader;
import java.io.StringReader;
import org.junit.jupiter.api.Test;

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
import cop5556fa19.AST.ParList;
import cop5556fa19.Parser.SyntaxException;

class ExpressionParserTest {

	// To make it easy to print objects and turn this output on and off
	static final boolean doPrint = true;

	private void show(Object input) {
		if (doPrint) {
			System.out.println(input.toString());
		}
	}


	
	// creates a scanner, parser, and parses the input.  
	Exp parseAndShow(String input) throws Exception {
		show("parser input:\n" + input); // Display the input
		Reader r = new StringReader(input);
		Scanner scanner = new Scanner(r); // Create a Scanner and initialize it
		Parser parser = new Parser(scanner);  // Create a parser
		Exp e = parser.exp(); // Parse and expression
		show("e=" + e);  //Show the resulting AST
		return e;
	}
	


	@Test
	void testIdent0() throws Exception {
		String input = "x";
		Exp e = parseAndShow(input);
		assertEquals(ExpName.class, e.getClass());
		assertEquals("x", ((ExpName) e).name);
	}

	@Test
	void testIdent1() throws Exception {
		String input = "(x)";
		Exp e = parseAndShow(input);
		assertEquals(ExpName.class, e.getClass());
		assertEquals("x", ((ExpName) e).name);
	}

	@Test
	void testString() throws Exception {
		String input = "\"string\"";
		Exp e = parseAndShow(input);
		assertEquals(ExpString.class, e.getClass());
		assertEquals("string", ((ExpString) e).v);
	}

	@Test
	void testBoolean0() throws Exception {
		String input = "true";
		Exp e = parseAndShow(input);
		assertEquals(ExpTrue.class, e.getClass());
	}

    @Test
	void testBoolean1() throws Exception {
		String input = "false";
		Exp e = parseAndShow(input);
		assertEquals(ExpFalse.class, e.getClass());
	}


	@Test
	void testBinary0() throws Exception {
		String input = "1 + 2";
		Exp e = parseAndShow(input);
		Exp expected = Expressions.makeBinary(1,OP_PLUS,2);
		show("expected="+expected);
		assertEquals(expected,e);
	}
	
	@Test
	void testUnary0() throws Exception {
		String input = "-2";
		Exp e = parseAndShow(input);
		Exp expected = Expressions.makeExpUnary(OP_MINUS, 2);
		show("expected="+expected);
		assertEquals(expected,e);
	}
	
	@Test
	void testUnary1() throws Exception {
		String input = "-*2\n";
		assertThrows(SyntaxException.class, () -> {
		Exp e = parseAndShow(input);
		});	
	}
	

	
	@Test
	void testRightAssoc() throws Exception {
		String input = "\"concat\" .. \"is\"..\"right associative\"";
		Exp e = parseAndShow(input);
		Exp expected = Expressions.makeBinary(Expressions.makeExpString("concat"), DOTDOT, Expressions.makeBinary("is",DOTDOT,"right associative"));
		show("expected=" + expected);
		assertEquals(expected,e);
	}
	
	@Test
	void testLeftAssoc() throws Exception {
		String input = "\"minus\" + \"is\" * \"left associative\"";
		Exp e = parseAndShow(input);
		Exp expected = Expressions.makeBinary(Expressions.makeExpString("minus"), OP_PLUS, Expressions.makeBinary(Expressions.makeExpString("is"), OP_TIMES, 
				Expressions.makeExpString("left associative")));
		show("expected=" + expected);
		assertEquals(expected,e);
		
	}
	
	@Test
	void testparens() throws Exception {
		String input = "2*(3+4)";
		Exp e = parseAndShow(input);
		Exp expected = Expressions.makeBinary(Expressions.makeInt(2),OP_TIMES,Expressions.makeBinary(Expressions.makeInt(3),OP_PLUS,Expressions.makeInt(4)));
		show("expected=" + expected);
		assertEquals(expected,e);
		
	}
	
	@Test
	void testoper() throws Exception {
		String input = "2..3|4";
		Exp e = parseAndShow(input);
		Exp expected = Expressions.makeBinary(Expressions.makeBinary(Expressions.makeInt(2),DOTDOT,Expressions.makeInt(3)),BIT_OR,Expressions.makeInt(4));
		show("expected=" + expected);
		assertEquals(expected,e);
		
	}
	
	@Test
	void testoperDot() throws Exception {
		String input = "2..3+4";
		Exp e = parseAndShow(input);
		Exp expected = Expressions.makeBinary(Expressions.makeInt(2),DOTDOT,Expressions.makeBinary(Expressions.makeInt(3),OP_PLUS,Expressions.makeInt(4)));
		show("expected=" + expected);
		assertEquals(expected,e);
		
	}
	
	@Test
	void testoperPow() throws Exception {
		String input = "2^3+4";
		Exp e = parseAndShow(input);
		Exp expected = Expressions.makeBinary(Expressions.makeBinary(Expressions.makeInt(2),OP_POW,Expressions.makeInt(3)),OP_PLUS,Expressions.makeInt(4));
		show("expected=" + expected);
		assertEquals(expected,e);
		
	}
	
	@Test
	void testoperPowcheck() throws Exception {
		String input = "2*3^4";
		Exp e = parseAndShow(input);
		Exp expected = Expressions.makeBinary(Expressions.makeInt(2),OP_TIMES,Expressions.makeBinary(Expressions.makeInt(3),OP_POW,Expressions.makeInt(4)));
		show("expected=" + expected);
		assertEquals(expected,e);
		
	}
	
	@Test
	void testfailed0() throws Exception {
		String input = "123 + (456 - 789) - 101112";
		Exp e = parseAndShow(input);
		Exp expected = Expressions.makeBinary(Expressions.makeBinary(Expressions.makeInt(123),OP_PLUS,Expressions.makeBinary(Expressions.makeInt(456),OP_MINUS,Expressions.makeInt(789))),OP_MINUS,Expressions.makeInt(101112));
		show("expected=" + expected);
		assertEquals(expected,e);
	}		
	
	@Test
	void testfailed1() throws Exception {
		String input = "1 ^ (4 + 2) * 3";
		Exp e = parseAndShow(input);
		Exp expected = Expressions.makeBinary(Expressions.makeBinary(Expressions.makeInt(1),OP_POW,Expressions.makeBinary(Expressions.makeInt(4),OP_PLUS,Expressions.makeInt(2))),OP_TIMES,Expressions.makeInt(3));
		show("expected=" + expected);
		assertEquals(expected,e);
		
	}
	
	@Test
	void testfailed2() throws Exception {
		String input = "(1 <= 2) == (4 >= 3)";
		Exp e = parseAndShow(input);
		Exp expected = Expressions.makeBinary(Expressions.makeBinary(Expressions.makeInt(1),REL_LE,Expressions.makeInt(2)),REL_EQEQ,Expressions.makeBinary(Expressions.makeInt(4),REL_GE,Expressions.makeInt(3)));
		show("expected=" + expected);
		assertEquals(expected,e);
		
	}
	
	@Test
	void testfailed3() throws Exception {
		String input = "(1 * 2) / (3 % 4) // 5 ";
		Exp e = parseAndShow(input);
		Exp expected = Expressions.makeBinary(Expressions.makeBinary(Expressions.makeBinary(Expressions.makeInt(1),OP_TIMES,Expressions.makeInt(2)),OP_DIV,Expressions.makeBinary(Expressions.makeInt(3),OP_MOD,Expressions.makeInt(4))),OP_DIVDIV,Expressions.makeInt(5));
		show("expected=" + expected);
		assertEquals(expected,e);
		
	}
	
	@Test
	void testoperPowAssoc() throws Exception {
		String input = "2^3^4";
		Exp e = parseAndShow(input);
		Exp expected = Expressions.makeBinary(Expressions.makeInt(2),OP_POW,Expressions.makeBinary(Expressions.makeInt(3),OP_POW,Expressions.makeInt(4)));
		show("expected=" + expected);
		assertEquals(expected,e);
		
	}
	//function (aa, b) end >> function(test, l, ...) end
	
	@Test
	void testFunc() throws Exception {
		String input = "function(...)end";
		Exp e = parseAndShow(input);
	//	show("expected=" + expected);
		assertEquals(ExpFunction.class, e.getClass());
	}
	
	@Test
	void testfailed4() throws Exception {
		String input = "function (aa, b) end >> function(test, l, ...) end";
		Exp e = parseAndShow(input);
	//	show("expected=" + expected);
		assertEquals(ExpBinary.class, e.getClass());
	}
	//function (aa, b) end >> function(test, l, ...) end & function(...) end
	
	@Test
	void testfailed5() throws Exception {
		String input = "function (aa, b) end >> function(test, l, ...) end & function(...) end";
		Exp e = parseAndShow(input);
	//	show("expected=" + expected);
		assertEquals(ExpBinary.class, e.getClass());
	}
	
	@Test
	void testfailed6() throws Exception {
		String input = "function(test, l, ...) end & function(...) end";
		Exp e = parseAndShow(input);
	//	show("expected=" + expected);
		assertEquals(ExpBinary.class, e.getClass());
	}
	
	@Test
	void testfailed7() throws Exception {
		String input = "function () end";
		Exp e = parseAndShow(input);
	//	show("expected=" + expected);
		assertEquals(ExpFunction.class, e.getClass());
	}
	@Test
	void testfailed8() throws Exception {
		String input = "function (a,b) end";
		Exp e = parseAndShow(input);
	//	show("expected=" + expected);
		assertEquals(ExpFunction.class, e.getClass());
	}
	
	@Test
	void testFuncErr() throws Exception {
		String input = "function (a...)end";
	//	show("expected=" + expected);
		assertThrows(SyntaxException.class, () -> {
			Exp e = parseAndShow(input);
			});	
	}
	
	@Test
	void testfailed9() throws Exception {
		String input = "function (xy,zy, ...) end";
		Exp e = parseAndShow(input);
	//	show("expected=" + expected);
		assertEquals(ExpFunction.class, e.getClass());
	}
	
	@Test
	void testTableExp() throws Exception {
		String input = "{ [1]=2 }";
		Exp e = parseAndShow(input);
	//	show("expected=" + expected);
		assertEquals(ExpTable.class, e.getClass());
	}
	
	@Test
	void testTableName() throws Exception {
		String input = "{ is=2 }";
		Exp e = parseAndShow(input);
	//	show("expected=" + expected);
		assertEquals(ExpTable.class, e.getClass());
	}
	
	@Test
	void testTableName1() throws Exception {
		String input = "{ 1=2 }";
		Exp e = parseAndShow(input);
	//	show("expected=" + expected);
		assertEquals(ExpTable.class, e.getClass());
	}
	
	@Test
	void testTableImp() throws Exception {
		String input = "{ 2 }";
		Exp e = parseAndShow(input);
	//	show("expected=" + expected);
		assertEquals(ExpTable.class, e.getClass());
	}
	
	@Test
		void testTableImp1() throws Exception {
			String input = "{}";
			Exp e = parseAndShow(input);
		//	show("expected=" + expected);
			assertEquals(ExpTable.class, e.getClass());
		}
	
	@Test
	void testTableErr() throws Exception {
		String input = "{ [1=2] }";
		assertThrows(SyntaxException.class, () -> {
			Exp e = parseAndShow(input);
			});
	}
	
	@Test
	void testRight1() throws Exception {
		String input = "1^2^3+4";
		Exp e = parseAndShow(input);
		Exp expected =Expressions.makeBinary(Expressions.makeBinary( Expressions.makeInt(1)
				, OP_POW
		, Expressions.makeBinary(Expressions.makeInt(2),OP_POW,Expressions.makeInt(3))),OP_PLUS,Expressions.makeInt(4));
		show("expected=" + expected);
		assertEquals(expected,e);
		
	}
	
	@Test
	void testPowerPrecedence() throws Exception {
		String input = "1^2^3^4+5";
		Exp e = parseAndShow(input);
		Exp expected =Expressions.makeBinary(Expressions.makeBinary( Expressions.makeInt(1)
				, OP_POW
		, Expressions.makeBinary(Expressions.makeInt(2),OP_POW,Expressions.makeBinary(Expressions.makeInt(3),OP_POW,Expressions.makeInt(4)))),OP_PLUS,Expressions.makeInt(5));
		show("expected=" + expected);
		assertEquals(expected,e);
		
	}
	
	@Test
	void testDotdot() throws Exception {
		String input = "1..2..3..4|5";
		Exp e = parseAndShow(input);
		Exp expected =Expressions.makeBinary(Expressions.makeBinary( Expressions.makeInt(1)
				, DOTDOT
		, Expressions.makeBinary(Expressions.makeInt(2),DOTDOT,Expressions.makeBinary(Expressions.makeInt(3),DOTDOT,Expressions.makeInt(4)))),BIT_OR,Expressions.makeInt(5));
		show("expected=" + expected);
		assertEquals(expected,e);
		
	}
	
	/*
	 * @Test void testLeftAssoc1() throws Exception { String input =
	 * "\"1\" + \"2\" * \"3\""; Exp e = parseAndShow(input); Exp expected =
	 * Expressions.makeBinary(1,OP_PLUS,Expressions.makeBinary(2,OP_POW,3));
	 * show("expected=" + expected); assertEquals(expected,e);
	 * 
	 * }
	 */

}
