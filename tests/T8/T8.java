class T8{

  public static void main(String[] args){
    System.out.println(new A().access(2,3));
  }
}

class A{
  int a;
  public int access(int a, int b){
    int[] arr ;
    a = a;
    arr = new int[b];
    arr[0] = 0;
    a = arr[0];
    arr[1] = 1 + a;
    a = arr[1];
    arr[2] = 2 + a;

    return arr[2];
  }
}
