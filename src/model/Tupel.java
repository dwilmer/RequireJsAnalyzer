package model;

public class Tupel<A, B> {
	private A a;
	private B b;
	
	public Tupel(A a, B b) {
		this.a = a;
		this.b = b;
	}
	
	public A getA() {
		return this.a;
	}
	
	public B getB() {
		return this.b;
	}
}
