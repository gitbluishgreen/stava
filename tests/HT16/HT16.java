class HT16 {
	public static void main (String [] args) {
		System.out.println(new A().start());
	}
}
class A {
	public int start() {
		return this.foo(this.a(), this.b(), this.c());
	}
	public int a() {
		System.out.println(1);
		return 1;
	}
	public int b() {
		System.out.println(2);
		return 2;
	}
	public int c() {
		System.out.println(3);
		return 3;
	}
	public int foo(int a1, int b1, int c1) {
		return 0;
	}
}
