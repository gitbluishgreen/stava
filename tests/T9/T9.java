class T9{
  public static void main(String[] args){
    System.out.println(new A().dub(5));
  }
}

class A{
  int[] arr;
  int val;

  public int dub(int x){
    T9 obj;
    val = x;
    obj = new T9();
    arr = new int[x];
    val = this.dub2();
    return arr[0];
  }

  public int dub2(){
    int x;
    x = val;
    while(val != 0){
      arr[val-1] = 2 * val;
      val = val - 1;
      System.out.println(arr[val]);
    }
    return x;
  }
}
