package test;

public class Test {
	public static void main(String a[]){
		long orig = 524185200000L;
		long imp  = 524268000000L;
		long diff = imp-orig;
		
		System.out.println(diff/3600/1000);
	}
}
