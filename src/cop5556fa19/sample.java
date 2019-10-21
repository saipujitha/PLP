package cop5556fa19;



public class sample{
	static boolean val = false;
	
	public static void main(String[] args) {
		
		check();
System.out.println("after check copy:"+copy);
	}
	
	
	private static  void check() {
		val = true;
		System.out.println("in check val:"+val);
	}	
	
	static boolean copy = val;
}