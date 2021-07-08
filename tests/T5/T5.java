class T5{
  public static void main(String[] args){
    System.out.println(new A().check());
  }
}

class A{
  public int check(){
    int a;
    int b;
    int c;
    a = 5;
    b = 10;
    c = b/a;
    if(c != 2)
      c = b/a;
    else if((a <= b) && (c != 2))
      c = b/a;
    else
      c = 999999;
      System.out.println(c);
    return (c+10)+10;
  }
}
