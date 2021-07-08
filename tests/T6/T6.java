class T6{

  public static void main(String[] args){
    System.out.println(new A().callFun());
  }
}

class A{

  public boolean callFun(){
    int loop;
    int a ;
    int b ;
    loop = 10;
    while(loop != 0)
    {
      a = 10;
      b = 20;
      System.out.println((a+b)+loop);
      loop = loop -1;
    }
    return !(loop != 0);
  }
}
