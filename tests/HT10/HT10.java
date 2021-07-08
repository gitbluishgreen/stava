class HT10 {
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
		y = false;
		x = (((this.a()) + (this.b()))*(this.b())) <= (this.a());
    y = !(x || y);
    z = !(!x && y);
    m = !y;
    n = !z;

		if(!x)
		{
      System.out.println(1);
      if(!(m && z))
      {
        System.out.println(100);
        if(y && !n)
        {
          System.out.println(200);
        }
        else if(!y && n)
        {
          System.out.println(300);
        }
        else
        {
          System.out.println(400);
        }
      }
      else
      {
        System.out.println(500);
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
