class HT14{

  public static void main(String[] args){
    System.out.println(new D().funCall(20,4,10,5));
  }
}

class D{

  public int funCall(int a, int b, int c, int d){
    int[] arr;
    int x;
    int y;

    x = a;
    y = b;

    arr = new int[(((x/y)+c)/d)];

    arr[(b-2)] = 4;
    System.out.println(arr[2]);
    arr[((a/10)-1)] = 20;
    System.out.println(arr[1]);

    return 1;
  }
}
