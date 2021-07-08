class HT1{

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
  //  n = objB.funB();
    System.out.println(n);
    n = objB.funC();

    return 1;
  }
}


class A{

  int numA;

  public int funA(){
    numA = 100;
    numA = numA + 10;
    System.out.println(numA);
    return numA;
  }
}

class B extends A{

  int numB;

  public int funC(){
    numB = this.funOtherB();
    System.out.println(numB);
    numB = this.funB();
    return numB;
  }

  public int funB(){
    numB = 200;
    numB = numA + numB;
    System.out.println(numB);
    return numB;
  }

  public int funOtherB(){
    System.out.println(numB);
    return numB;
  }
}

class C extends B{

  int numB;

  public int funB(){
    System.out.println(numB);
    return  this.funA();
  }

}
