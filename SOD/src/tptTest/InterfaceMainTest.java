package tptTest;

interface A {
	default void run() {
		System.out.println("Vechicle running...");
	}
}

interface B {
	default void run() {
		System.out.println("Car running...");
	}
}

class Car implements A,B{

	@Override
	public void run() {
		// TODO Auto-generated method stub
		A.super.run();
	}
	
} 

public class InterfaceMainTest{
	
	public static void main(String[] args) {
		Car t=new Car();
		t.run();
	}
	
}