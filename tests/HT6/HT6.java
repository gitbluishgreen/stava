class HT6{

  public static void main(String[] args){
    System.out.println(new D().funCall());
  }
}

class D{

  public int funCall(){
    A objA;
    int x;
    objA = new A();
    x = objA.funA();
    System.out.println(x);
    x = objA.funA2();
    System.out.println(x);
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
    return numA;
  }
  public int funA2(){
    numA = numA - 50;
    return numA;
  }
}
