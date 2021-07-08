class HT11 {
	public static void main (String [] args) {
		System.out.println(new A().start());
	}
}
class A {
	public int start() {
		boolean x;
    boolean y;
    boolean z;
    boolean m;
    boolean n;
		int num1;
		int num2;
		int num3;
		y = false;
		num1 = 10;
		num2 = 20;
		num3 = 5;

		x = (((this.a()) + (this.b()))*(this.b())) <= (this.a());
    y = !(x || y);
    z = !(!x && y);
    m = !y;
    n = !z;

		if(!x)
		{
      while(num1 != 0){

				System.out.println(num1);
				while(num2 != 0){
					System.out.println(num2);
					num2 = num2 -1;
					while((num2 + num3) <= 50){
						System.out.println(num3);
						num3 = num3 + 2;
					}

				}
				num1 = num1 -1;
				num3 = 5;
			}
    }
		else
			System.out.println(3);
		return 3;
	}
	public int a() {
		System.out.println(1);
		return 1;
	}
	public int b() {
		System.out.println(2);
		return 2;
	}
}
