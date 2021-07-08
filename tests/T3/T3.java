class T3{
  public static void main(String[] args){
    System.out.println(new A().check());
  }
}

class A{
  public int check()
  {
    int a;
    int b;
    int c;
    a=10;
    b=20;
    c=30;
    if(a <= c)
    {
      a = b+c;
    }
    return a;
  }
}
