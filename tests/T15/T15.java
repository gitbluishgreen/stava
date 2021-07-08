class T15{

  public static void main(String[] args){
    System.out.println(new B().funA(5));
  }
}

class A{

  int numA;

  public int funA(){
    numA = 20;
    numA = numA + 10;
    System.out.println(numA);
    return numA;
  }
}

class B extends A{

  int numB;

  public int funA(int a){
    numB = a;
    numB = numA + numB;
    System.out.println(numB);
    return numB;
  }
}
