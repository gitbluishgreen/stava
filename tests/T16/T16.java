class T16{

  public static void main(String[] args){
    System.out.println(new C().checkOver());
  }
}

class C{

  public int checkOver(){
    A objA;
    int n;
    objA = new A();
    n = objA.funA();
    objA = new B();
    n = objA.funA();

    System.out.println(n);
    return n;
  }
}

class A{

  int numA;

  public int funA(){
    numA = numA + 10;
    System.out.println(numA);
    return numA;
  }
}

class B extends A{

  int numB;

  public int funA(){
    numB = numA + numB;
    System.out.println(numB);
    return numB;
  }
}
