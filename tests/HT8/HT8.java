class HT8{

  public static void main(String[] args){
    System.out.println(new D().funCall(2,3,4));
  }
}

class D{

  public int funCall(int a, int b, int c){
    A objA;
    int x;
    objA = new A();
    x = objA.funA(a,b,c);
    System.out.println(x);
    x = c;
    System.out.println(x);
    x = objA.funA2();
    System.out.println(x);
    return x;
  }
}


class A{

  int numA;

  public int funA(int numA, int numB, int numC){
    numA = numA + 100;
    numA = numA + 10;
    System.out.println((numA+numB));
    return numA;
  }
  public int funA2(){
    numA = numA - 50;
    return numA;
  }
}
