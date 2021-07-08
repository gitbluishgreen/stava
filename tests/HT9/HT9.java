class HT9{

  public static void main(String[] args){
    System.out.println(new D().funCall());
  }
}

class D{

  public int funCall(){
    A objA;
    B objB;
    int n;
    n = 5000;
    objA = new A();
    n = objA.funA(n);
    System.out.println(n);
    objB = new B();
    n = objB.funB(n,10000);
    System.out.println(n);
    objA = objB;
    n = objA.funA(n);
    System.out.println(n);

    objB = new C();
    n = objB.funB(n,n);
    System.out.println(n);
    n = objB.funC();

    return n;
  }
}


class A{

  int numA;

  public int funA(int a){
    int numA;
    numA = a;
    numA = numA + 10;
    System.out.println(numA);
    numA = this.funA2();
    return numA;
  }
  public int funA2(){
    numA = numA + 1000;
    return numA;
  }
}

class B extends A{

  int numB;

  public int funC(){
    int numB;
    numB = this.funOtherB();
    System.out.println(numB);
    return numB;
  }

  public int funB(int numA, int numB){
    numB = numB + 200;
    numB = numA + numB;
    System.out.println(numB);
    return numB;
  }
  public int funOtherB(){
    int numA;
    numA = 500;
    System.out.println((numA +numB));
    return numA;
  }
}

class C extends B{

  int numC;

  public int funB(int numA, int numB){
    System.out.println((numB+2000));
    return  this.funA(numC);
  }

}
