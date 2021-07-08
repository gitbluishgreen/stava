class T7{

  public static void main(String[] args){
    System.out.println(new A().fun(10));
  }
}

class A{
  public int fun(int a)
  {
    int b;
    b = a;
    return this.fun2(b);
  }

  public int fun2(int b)
  {
    int c ;
    int loop;
    int i;
    loop = 10;
    c = b;
    i = 0;
    while(i <= loop){
      System.out.println((c-b)+loop);
      c = c + i;
      i = i + 1;
      loop = loop - 1;
    }
    return c;
  }
}
