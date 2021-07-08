class HT4 {
	public static void main(String [] args) {
		System.out.println(new A().start());
	}
}
class A {
	public int start () {
		System.out.println((this.foo()).length);
		return 0;
	}
	public int [] foo() {
		return new int [5];
	}
}
