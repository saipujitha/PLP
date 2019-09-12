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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Reader;
import java.io.StringReader;

import org.junit.Test;

import static org.junit.jupiter.api.Assertions.*;
//import org.junit.jupiter.api.Test;

import cop5556fa19.Scanner.LexicalException;

import static cop5556fa19.Token.Kind.*;

public class ScannerTest {
	
	//I like this to make it easy to print objects and turn this output on and off
	static boolean doPrint = true;
	private void show(Object input) {
		if (doPrint) {
			System.out.println(input.toString());
		}
	}

	 /**
	  * Example showing how to get input from a Java string literal.
	  * 
	  * In this case, the string is empty.  The only Token that should be returned is an EOF Token.  
	  * 
	  * This test case passes with the provided skeleton, and should also pass in your final implementation.
	  * Note that calling getNext again after having reached the end of the input should just return another EOF Token.
	  * 
	  */
	@Test 
 public	void test0() throws Exception {
		Reader r = new StringReader("");
		Scanner s = new Scanner(r);
		Token t;
		show(t= s.getNext()); 
		assertEquals(EOF, t.kind);
		show(t= s.getNext());
		assertEquals(EOF, t.kind);
	}
	
	/**
	 * Example showing how to create a test case to ensure that an exception is thrown when illegal input is given.
	 * 
	 * This "@" character is illegal in the final scanner (except as part of a String literal or comment). So this
	 * test should remain valid in your complete Scanner.
	 */
	@Test
	public void test1() throws Exception {
		Reader r = new StringReader("@");
		Scanner s = new Scanner(r);
        assertThrows(LexicalException.class, ()->{
		   s.getNext();
        });
	}
	
	/**
	 * Example showing how to read the input from a file.  Otherwise it is the same as test1.
	 *
	 */
	//@Test
	public void test2() throws Exception {
		String file = "testInputFiles\\test2.input"; 
		Reader r = new BufferedReader(new FileReader(file));
		Scanner s = new Scanner(r);
        assertThrows(LexicalException.class, ()->{
		   s.getNext();
        });
        r.close();
	}
	
	/**
	 * Another example.  This test case will fail with the provided code, but should pass in your completed Scanner.
	 * @throws Exception
	 */
	@Test
	public void test3() throws Exception {
		Reader r = new StringReader(",,::==");
		Scanner s = new Scanner(r);
		Token t;
		show(t= s.getNext());
		assertEquals(t.kind,COMMA);
		assertEquals(t.text,",");
		show(t = s.getNext());
		assertEquals(t.kind,COMMA);
		assertEquals(t.text,",");
		
		show(t = s.getNext());
		assertEquals(t.kind,COLONCOLON);
		assertEquals(t.text,"::");
		
		show(t = s.getNext());
		assertEquals(t.kind,REL_EQEQ);
		assertEquals(t.text,"==");
	}

	@Test 
	 public	void test4() throws Exception {
			Reader r = new StringReader("a1c");
			Scanner s = new Scanner(r);
			Token t;
			show(t= s.getNext());
			assertEquals(t.kind,NAME);
			assertEquals(t.text,"a1c");
			
			show(t= s.getNext());
			assertEquals(t.kind,EOF);
			
			show(t= s.getNext());
			assertEquals(t.kind,EOF);
		}
	
	@Test 
	 public	void test5() throws Exception {
			Reader r = new StringReader("anc+");
			Scanner s = new Scanner(r);
			Token t;
		
			show(t= s.getNext());
			assertEquals(t.kind,NAME);
			assertEquals(t.text,"anc");
			  
			show(t = s.getNext());
			assertEquals(t.kind,OP_PLUS);
			assertEquals(t.text,"+");
			
			show(t= s.getNext());
			assertEquals(t.kind,EOF);
			
		}
	
	@Test 
	 public	void test6() throws Exception {
			Reader r = new StringReader("anc+def");
			Scanner s = new Scanner(r);
			Token t;
		
			show(t= s.getNext());
			assertEquals(t.kind,NAME);
			assertEquals(t.text,"anc");
			  
			show(t = s.getNext());
			assertEquals(t.kind,OP_PLUS);
			assertEquals(t.text,"+");
			
			show(t= s.getNext());
			assertEquals(t.kind,NAME);
			assertEquals(t.text,"def");
		}
	
	@Test 
	 public	void test7() throws Exception {
			Reader r = new StringReader("909");
			Scanner s = new Scanner(r);
			Token t;
			show(t= s.getNext());
			assertEquals(t.kind,INTLIT);
			assertEquals(t.text,"909");
		}
	
	@Test 
	 public	void test8() throws Exception {
			Reader r = new StringReader("8100+");
			Scanner s = new Scanner(r);
			Token t;
		
			show(t= s.getNext());
			assertEquals(t.kind,INTLIT);
			assertEquals(t.text,"8100");
			  
			show(t = s.getNext());
			assertEquals(t.kind,OP_PLUS);
			assertEquals(t.text,"+");
			
		}
	
	@Test 
	 public	void test9() throws Exception {
			Reader r = new StringReader("530+258");
			Scanner s = new Scanner(r);
			Token t;
		
			show(t= s.getNext());
			assertEquals(t.kind,INTLIT);
			assertEquals(t.text,"530");
			  
			show(t = s.getNext());
			assertEquals(t.kind,OP_PLUS);
			assertEquals(t.text,"+");
			
			show(t= s.getNext());
			assertEquals(t.kind,INTLIT);
			assertEquals(t.text,"258");
		}
	
	@Test 
	 public	void test10() throws Exception {
			Reader r = new StringReader("052");
			Scanner s = new Scanner(r);
			Token t;
		
			show(t= s.getNext());
			assertEquals(t.kind,INTLIT);
			assertEquals(t.text,"0");
			
			show(t= s.getNext());
			assertEquals(t.kind,INTLIT);
			assertEquals(t.text,"52");
		}
	
	@Test 
	 public	void test11() throws Exception {
			Reader r = new StringReader("0100");
			Scanner s = new Scanner(r);
			Token t;
		
			show(t= s.getNext());
			assertEquals(t.kind,INTLIT);
			assertEquals(t.text,"0");
			
			show(t= s.getNext());
			assertEquals(t.kind,INTLIT);
			assertEquals(t.text,"100");
		}
	
	@Test 
	 public	void test12() throws Exception {
			Reader r = new StringReader("233+abs");
			Scanner s = new Scanner(r);
			Token t;
		
			show(t= s.getNext());
			assertEquals(t.kind,INTLIT);
			assertEquals(t.text,"233");
			
			show(t= s.getNext());
			assertEquals(t.kind,OP_PLUS);
			assertEquals(t.text,"+");
			
			show(t= s.getNext());
			assertEquals(t.kind,NAME);
			assertEquals(t.text,"abs");
		}
	
	@Test 
	 public	void test13() throws Exception {
			Reader r = new StringReader("abs+522");
			Scanner s = new Scanner(r);
			Token t;
		
			show(t= s.getNext());
			assertEquals(t.kind,NAME);
			assertEquals(t.text,"abs");
			
			show(t= s.getNext());
			assertEquals(t.kind,OP_PLUS);
			assertEquals(t.text,"+");
		
			show(t= s.getNext());
			assertEquals(t.kind,INTLIT);
			assertEquals(t.text,"522");
		}
	
	@Test 
	 public	void test14() throws Exception {
			Reader r = new StringReader("-+");
			Scanner s = new Scanner(r);
			Token t;
			show(t= s.getNext()); 
			assertEquals(t.kind, OP_MINUS);
			
			show(t= s.getNext()); 
			assertEquals(t.kind, OP_PLUS);
		}
	
	@Test 
	 public	void test15() throws Exception {
			Reader r = new StringReader("abs/522");
			Scanner s = new Scanner(r);
			Token t;
		
			show(t= s.getNext());
			assertEquals(t.kind,NAME);
			assertEquals(t.text,"abs");
			
			show(t= s.getNext());
			assertEquals(t.kind,OP_DIV);
			assertEquals(t.text,"/");
		
			show(t= s.getNext());
			assertEquals(t.kind,INTLIT);
			assertEquals(t.text,"522");
		}
	
	@Test 
	 public	void test16() throws Exception {
			Reader r = new StringReader("abs*fe1");
			Scanner s = new Scanner(r);
			Token t;
		
			show(t= s.getNext());
			assertEquals(t.kind,NAME);
			assertEquals(t.text,"abs");
			
			show(t= s.getNext());
			assertEquals(t.kind,OP_TIMES);
			assertEquals(t.text,"*");
		
			show(t= s.getNext());
			assertEquals(t.kind,NAME);
			assertEquals(t.text,"fe1");
		}
	
	@Test 
	 public	void test17() throws Exception {
			Reader r = new StringReader("//");
			Scanner s = new Scanner(r);
			Token t;
		
			show(t= s.getNext());
			assertEquals(t.kind,OP_DIVDIV);
			assertEquals(t.text,"//");
		}
	
	@Test 
	 public	void test18() throws Exception {
			Reader r = new StringReader(":://");
			Scanner s = new Scanner(r);
			Token t;
		
			show(t= s.getNext());
			assertEquals(t.kind,COLONCOLON);
			assertEquals(t.text,"::");
			
			show(t= s.getNext());
			assertEquals(t.kind,OP_DIVDIV);
			assertEquals(t.text,"//");
		}
	
	@Test 
	 public	void test19() throws Exception {
			Reader r = new StringReader(">");
			Scanner s = new Scanner(r);
			Token t;
		
			show(t= s.getNext());
			assertEquals(t.kind,REL_GT);
			assertEquals(t.text,">");
		}
	
	@Test 
	 public	void tes20() throws Exception {
			Reader r = new StringReader(">=");
			Scanner s = new Scanner(r);
			Token t;
		
			show(t= s.getNext());
			assertEquals(t.kind,REL_GE);
			assertEquals(t.text,">=");
		}
	
	@Test 
	 public	void tes21() throws Exception {
			Reader r = new StringReader(">>=");
			Scanner s = new Scanner(r);
			Token t;
			
			show(t= s.getNext());
			assertEquals(t.kind,BIT_SHIFTR);
			assertEquals(t.text,">>");
		
			show(t= s.getNext());
			assertEquals(t.kind,ASSIGN);
			assertEquals(t.text,"=");
		}
	
	@Test 
	 public	void tes22() throws Exception {
			Reader r = new StringReader(".");
			Scanner s = new Scanner(r);
			Token t;
			
			show(t= s.getNext());
			assertEquals(t.kind,DOT);
			assertEquals(t.text,".");
		}
	
	@Test 
	 public	void tes23() throws Exception {
			Reader r = new StringReader("..");
			Scanner s = new Scanner(r);
			Token t;
			
			show(t= s.getNext());
			assertEquals(t.kind,DOTDOT);
			assertEquals(t.text,"..");
		}
	
	@Test 
	 public	void tes24() throws Exception {
			Reader r = new StringReader("...");
			Scanner s = new Scanner(r);
			Token t;
			
			show(t= s.getNext());
			assertEquals(t.kind,DOTDOTDOT);
			assertEquals(t.text,"...");
		}
	
	@Test 
	 public	void tes25() throws Exception {
			Reader r = new StringReader("....");
			Scanner s = new Scanner(r);
			Token t;
			
			show(t= s.getNext());
			assertEquals(t.kind,DOTDOTDOT);
			assertEquals(t.text,"...");
			
			show(t= s.getNext());
			assertEquals(t.kind,DOT);
			assertEquals(t.text,".");
		}
	
	@Test 
	 public	void tes26() throws Exception {
			Reader r = new StringReader(".....");
			Scanner s = new Scanner(r);
			Token t;
			
			show(t= s.getNext());
			assertEquals(t.kind,DOTDOTDOT);
			assertEquals(t.text,"...");
			
			show(t= s.getNext());
			assertEquals(t.kind,DOTDOT);
			assertEquals(t.text,"..");
		}
	
	@Test 
	 public	void tes27() throws Exception {
			Reader r = new StringReader("{}");
			Scanner s = new Scanner(r);
			Token t;
			
			show(t= s.getNext());
			assertEquals(t.kind,LCURLY);
			assertEquals(t.text,"{");
			
			show(t= s.getNext());
			assertEquals(t.kind,RCURLY);
			assertEquals(t.text,"}");
		}
	
	@Test 
	 public	void tes28() throws Exception {
			Reader r = new StringReader("()");
			Scanner s = new Scanner(r);
			Token t;
			
			show(t= s.getNext());
			assertEquals(t.kind,LPAREN);
			assertEquals(t.text,"(");
			
			show(t= s.getNext());
			assertEquals(t.kind,RPAREN);
			assertEquals(t.text,")");
		}
	
	@Test 
	 public	void tes29() throws Exception {
			Reader r = new StringReader("~~=");
			Scanner s = new Scanner(r);
			Token t;
			
			show(t= s.getNext());
			assertEquals(t.kind,BIT_XOR);
			assertEquals(t.text,"~");
			
			show(t= s.getNext());
			assertEquals(t.kind,REL_NOTEQ);
			assertEquals(t.text,"~=");
		}
	
	@Test 
	 public	void tes30() throws Exception {
			Reader r = new StringReader(";//::");
			Scanner s = new Scanner(r);
			Token t;
			
			show(t= s.getNext());
			assertEquals(t.kind,SEMI);
			assertEquals(t.text,";");
			
			show(t= s.getNext());
			assertEquals(t.kind,OP_DIVDIV);
			assertEquals(t.text,"//");
			
			show(t= s.getNext());
			assertEquals(t.kind,COLONCOLON);
			assertEquals(t.text,"::");
		}
	
	//@Test 
	 public	void tes31() throws Exception {
			Reader r = new StringReader("\\n");
			Scanner s = new Scanner(r);
			Token t;
			
			show(t= s.getNext());
			assertEquals(t.kind,EOF);
			assertEquals("\n",t.text);
		}
	
	@Test 
	 public	void tes32() throws Exception {
			Reader r = new StringReader("abc\\n");
			Scanner s = new Scanner(r);
			Token t;
			
			show(t= s.getNext());
			assertEquals(t.kind,NAME);
			assertEquals(t.text,"abc");
			
			show(t= s.getNext());
			assertEquals(t.kind,EOF);
		}
	
	@Test 
	 public	void tes33() throws Exception {
			Reader r = new StringReader("abc\\n123");
			Scanner s = new Scanner(r);
			Token t;
			
			show(t= s.getNext());
			assertEquals(t.kind,NAME);
			assertEquals(t.text,"abc");
			
			show(t= s.getNext());
			assertEquals(t.kind,INTLIT);
			assertEquals(t.text,"123");
		}
	
	@Test 
	 public	void tes34() throws Exception {
			Reader r = new StringReader("\\nabb");
			Scanner s = new Scanner(r);
			Token t;
			
			show(t= s.getNext());
			assertEquals(t.kind,NAME);
			assertEquals(t.text,"abb");
		//	assertEquals(4,t.line);
			
		}

	
	@Test 
	 public	void tes35() throws Exception {
			Reader r = new StringReader("\\kllb");
			Scanner s = new Scanner(r);
			Token t;
		
			assertThrows(LexicalException.class, ()->{
				   s.getNext();
		        });

		}
	
	@Test 
	 public	void tes36() throws Exception {
			Reader r = new StringReader("llb\\m");
			Scanner s = new Scanner(r);
			Token t;
		
			show(t= s.getNext());
			 assertEquals(t.kind,NAME);
			 assertEquals(t.text,"llb");
			 
			assertThrows(LexicalException.class, ()->{
				   s.getNext();
		        });
		}
	
	
	
	@Test 
	 public	void tes38() throws Exception {
			Reader r = new StringReader("a;b");
			Scanner s = new Scanner(r);
			Token t;
		
			show(t= s.getNext());
			 assertEquals(t.kind,NAME);
			 assertEquals(t.text,"a");
			 
			 show(t= s.getNext());
			 assertEquals(t.kind,SEMI);
			 assertEquals(t.text,";");
			 
			 show(t= s.getNext());
			 assertEquals(t.kind,NAME);
			 assertEquals(t.text,"b");
		}
	@Test 
	 public	void tes39() throws Exception {
			Reader r = new StringReader("a;b");
			Scanner s = new Scanner(r);
			Token t;
		
			show(t= s.getNext());
			 assertEquals(t.kind,NAME);
			 assertEquals(t.text,"a");
			 
			 show(t= s.getNext());
			 assertEquals(t.kind,SEMI);
			 assertEquals(t.text,";");
			 
			 show(t= s.getNext());
			 assertEquals(t.kind,NAME);
			 assertEquals(t.text,"b");
		}
	
	@Test 
	 public	void tes40() throws Exception {
			Reader r = new StringReader("\"abcdef\"");
			Scanner s = new Scanner(r);
			Token t;
		
			show(t= s.getNext());
			 assertEquals(t.kind,STRINGLIT);
			 assertEquals(t.text,"\"abcdef\"");
			 
		}
	
	@Test 
	 public	void tes37() throws Exception {
			Reader r = new StringReader("\"abc\ndef\"");  
			Scanner s = new Scanner(r);
			Token t;
		
			show(t= s.getNext());
			 assertEquals(t.kind,STRINGLIT);
			 assertEquals(t.text,"\"abc\ndef\"");
			 
		}
	
	@Test 
	 public	void tes48() throws Exception {
			Reader r = new StringReader("\"abc\'m\'def\"");  
			Scanner s = new Scanner(r);
			Token t;
		
			show(t= s.getNext());
			 assertEquals(t.kind,STRINGLIT);
			 assertEquals(t.text,"\"abc\'m\'def\"");
			 
		}
	
	@Test 
	 public	void tes41() throws Exception {
			Reader r = new StringReader("if");
			Scanner s = new Scanner(r);
			Token t;
		
			show(t= s.getNext());
			 assertEquals(t.kind,KW_if);
			 assertEquals(t.text,"if");
			 
		}
	
	@Test 
	 public	void tes42() throws Exception {
			Reader r = new StringReader("if else");
			Scanner s = new Scanner(r);
			Token t;
		
			show(t= s.getNext());
			 assertEquals(t.kind,KW_if);
			 assertEquals(t.text,"if");
			 
			 show(t= s.getNext());
			 assertEquals(t.kind,KW_else);
			 assertEquals(t.text,"else");
			 
		}

	@Test 
	 public	void tes43() throws Exception {
			Reader r = new StringReader("a\"b\"c");
			Scanner s = new Scanner(r);
			Token t;
		
			show(t= s.getNext());
			 assertEquals(t.kind,NAME);
			 assertEquals(t.text,"a");
			 
			 show(t= s.getNext());
			 assertEquals(t.kind,STRINGLIT);
			 assertEquals(t.text,"\"b\"");
			 
			 show(t= s.getNext());
			 assertEquals(t.kind,NAME);
			 assertEquals(t.text,"c");
			 
		}
	
	//@Test 
	 public	void tes44() throws Exception {
			Reader r = new StringReader("\\b");
			Scanner s = new Scanner(r);
			Token t;
			
			show(t= s.getNext());
			assertEquals(t.kind,EOF);
			assertEquals(t.text,"\b");
		}
	
	//@Test 
	 public	void tes45() throws Exception {
			Reader r = new StringReader("\\a");
			Scanner s = new Scanner(r);
			Token t;
			
			show(t= s.getNext());
			assertEquals(t.kind,EOF);
			assertEquals(t.text,"\u5C61");
		}
	
	@Test 
	 public	void tes46() throws Exception {
			Reader r = new StringReader("\\v");
			Scanner s = new Scanner(r);
			Token t;
			
			show(t= s.getNext());
			assertEquals(t.kind,EOF);
			assertEquals(t.text,"\u5C76");
		}
	
	//@Test 
	 public	void tes47() throws Exception {
			Reader r = new StringReader("\\v\\a");
			Scanner s = new Scanner(r);
			Token t;
			
			show(t= s.getNext());
			assertEquals(t.kind,EOF);
			assertEquals(t.text,"\\v\\a");
			
		}
	
	@Test 
	 public	void tes49() throws Exception {
			Reader r = new StringReader("abb\\v");
			Scanner s = new Scanner(r);
			Token t;
			
			show(t= s.getNext());
			assertEquals(t.kind,NAME);
			assertEquals(t.text,"abb");
		}
	
	@Test 
	 public	void tes50() throws Exception {
			Reader r = new StringReader("\"abc\'def\"");  
			Scanner s = new Scanner(r);
			Token t;
		
			show(t= s.getNext());
			 assertEquals(t.kind,STRINGLIT);
			 assertEquals(t.text,"\"abc\'def\"");
			 
		}
	
	@Test
	public void test51() throws Exception {
		String file = "testInputFiles\\test2.input"; 
		Reader r = new BufferedReader(new FileReader(file));
		Scanner s = new Scanner(r);
        Token t;
        
		show(t= s.getNext());
		 assertEquals(t.kind,STRINGLIT);
		 assertEquals("\'a\'",t.text);
		
        r.close();
	}
	
	@Test 
	 public	void tes52() throws Exception {
			Reader r = new StringReader("\'abc\'");  
			Scanner s = new Scanner(r);
			Token t;
		
			show(t= s.getNext());
			 assertEquals(t.kind,STRINGLIT);
			 assertEquals(t.text,"\'abc\'");
			 
		}
	
	@Test 
	 public	void tes53() throws Exception {
			Reader r = new StringReader("\"if\"");
			Scanner s = new Scanner(r);
			Token t;
		
			show(t= s.getNext());
			 assertEquals(t.kind,STRINGLIT);
			 assertEquals(t.text,"\"if\"");
			 
		}
	
	@Test
	public void test54() throws Exception {
		String file = "testInputFiles\\test2.input"; 
		Reader r = new BufferedReader(new FileReader(file));
		Scanner s = new Scanner(r);
        Token t;
        
		show(t= s.getNext());
		 assertEquals(t.kind,EOF);
		 assertEquals("--comment\n",t.text);
		
        r.close();
	}
}

