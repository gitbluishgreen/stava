class T10{
  public static void main(String[] args){
      System.out.println(new A().nonRecursive(10));
  }
}

class A{
  public int nonRecursive(int a)
  {
    if(a != 0)
    {
      a = a - 1;
    }
    return this.iterative(a-1);

  }
  public int iterative(int x){
    int count;
    int sum;
    count = 9;
    sum = 0;
    while(count != 0){
      count = count - 1;
      sum = sum + x;
      x = x - 1;
    }
    return sum;
  }

}
