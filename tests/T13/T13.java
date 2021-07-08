class T13{

  public static void main(String[] args){
    System.out.println(new C().funC());
  }
}

class C{
  int a;
  int b;
  public int funC(){
    A objA;
    B objB;
    objA = new A();
    a = objA.funA();
    objB = new B();
    b = objB.funB();
    System.out.println(a + b);
    return a+b;
  }
}

class A{

  int numA;

  public int funA(){
    numA = 10;
    numA = numA + 10;
    System.out.println(numA);
    return numA;
  }
}

class B extends A{

  int numB;

  public int funB(){
    numB = 100;
    numB = numA + numB;
    System.out.println(numB);
    return numB;
  }
}
