class T12{
  public static void main(String[] args){
    System.out.println(new A().check());

  }
}

class A{
  boolean x;
  boolean y;

  public boolean check(){
    x = true;
    y = true;
    if(this.check2())
    {
      x = false;
      y = false;
    }
    return ((x && true) && (y || false));
  }

  public boolean check2(){
    return x && y;
  }
}
