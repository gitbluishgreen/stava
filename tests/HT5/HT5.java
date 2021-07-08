class HT5 {
    public static void main(String[] a) {
        System.out.println(new A().go());
    }
}

class A {
    public int go() {
        Class1 c1;
        Class2 c2;
        Class3 c3;
        Class1 c1_2;
        Class1 c1_3;
        Class2 c2_3;
        Class4 c4;
        int x;
        int y;
        x = 2090;
        y = x;
        c1 = new Class1();
        c2 = new Class2();
        c3 = new Class3();
        c1_2 = new Class1();
        c4 = new Class4();
        x = c3.FMethod(c2);
        y = c4.FMethod(c3);
        return 1337;
    }
}

class Class1 {
    public int AMethod() {
      System.out.println(0);
        return 0;
    }

    public int BMethod() {
      System.out.println(1);
        return 1;
    }

    public int CMethod() {
      int b;
      System.out.println(2);
      b = this.BMethod();
        return 2;
    }
}

class Class2 extends Class1 {
    public int BMethod() {
      System.out.println(3);
        return 3;
    }

    public int DMethod() {
      System.out.println(4);
        return 4;
    }
}

class Class3 extends Class2 {
    public int AMethod() {
      System.out.println(7);
        return 7;
    }

    public int DMethod() {
      System.out.println(5);
        return 5;
    }

    public int FMethod(Class1 x) {
      System.out.println(6);
        return 6;
    }
}

class Class4 extends Class3{
  public int AMethod() {
    int y;

    System.out.println(8);
    y = this.CMethod();
      return 8;
  }

  public int DMethod() {
    System.out.println(9);
      return this.AMethod();
  }

  public int FMethod(Class2 x) {
    int ab;
    System.out.println(10);
    ab = this.DMethod();
    System.out.println(ab);
      return 10;
  }
}
