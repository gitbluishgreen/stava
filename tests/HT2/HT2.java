class HT2{

  public static void main(String[] args){
    System.out.println(new B().funCall());
  }
}

class A{
int x1;
int x2;
  public int funCall(){
    int n;
    x1 = 10;
    System.out.println(x1);
    x2 = 20;
    System.out.println(x2);
    n = this.f1();
    return 1;
  }

  public int f1(){
    int n;
    System.out.println(x1);
    System.out.println(x2);
    n = this.f2();
    return n;
  }
  public int f2(){
    System.out.println(x1);
    System.out.println(x2);
    return x2;
  }

}

class B extends A{

  int x1;

  public int f3(){
  x1 = 100;
  System.out.println(x1);
  System.out.println(x2);
  return x1;
  }

  public int f2(){
      int n;
      n = this.f3();
      System.out.println(x1);
      return x1;
  }
}
