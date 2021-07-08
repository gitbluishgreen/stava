class T11{
  public static void main(String[] args){
    System.out.println(new A().retVal());
  }
}

class A{
  int x;
  int y;
  int z;

  public int retVal(){
    A obj;
    obj = new A();

    x = 10;
    y = 15;
    z = 20;

    obj = this;
    z = obj.changeZ();
    return z;

  }

  public int changeZ(){
    int z;
    z = 1+x;
    return z;
  }
}
