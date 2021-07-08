class HT17 {
	public static void main(String [] args) {
		System.out.println(new A().start());
	}
}
class A {
	public int start() {
		int a;
		int b;
		int c;
		int d;
		int x;
		int y;
		int z;
		a = 1;
		b = 2;
		c = 3;
		d = 4;
		x = 10;
		y = 20;
		z = 30;
		if(((a+(b*(c+d)))<=(x-(y+(z))))&&(a<=2))
			System.out.println(1);
		else
			System.out.println(0);
		return 0;
	}
}
