class HT7{

  public static void main(String[] args){
    System.out.println(new D().funCall());
  }
}

class D{

  public int funCall(){
    A objA;
    B objB;
    int n;
    objA = new A();
    n = objA.funA();
    System.out.println(n);
    objB = new B();
    n = objB.funB();
    System.out.println(n);
    objA = objB;
    n = objA.funA();
    System.out.println(n);

    objB = new C();
    n = objB.funB();
    System.out.println(n);
    n = objB.funC();

    return 1;
  }
}


class A{

  int numA;

  public int funA(){
    int numA;
    numA = 100;
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

  public int funB(){
    numB = 200;
    numB = numA + numB;
    System.out.println(numB);
    return numB;
  }
  public int funOtherB(){
    int numA;
    numA = 500;
    System.out.println((numA +numB));
    return numA+numB;
  }
}

class C extends B{

  int numC;

  public int funB(){
    System.out.println(numB);
    return  this.funA();
  }

}
