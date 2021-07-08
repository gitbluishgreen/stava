class T4{
  public static void main(String[] args){
    System.out.println(new A().fun());
  }
}

class A{

  public int fun(){
    int a;
    int b;
    int c;
    int d;
    int e;
    b=20;
    c=10;
    a = b-c;
    d = b*4;
    if(d <= a)
      e = d + c;
    else
      e = a;
    return e;
  }
}
