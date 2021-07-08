class HT3 {
	public static void main(String [] args) {
		System.out.println(new A().start());
	}
}
class A {
	public int start () {
		int x;
		x = 10;
		System.out.println((((this.meth1(2+x)).meth2()).meth3()).meth4());
		return 0;
	}
	public A meth1(int a) {
		return this;
	}
	public A meth2() {
		return this;
	}
	public A meth3() {
		return this;
	}
	public int meth4() {
		int x;
		x = 10;
		return x;
	}
}
