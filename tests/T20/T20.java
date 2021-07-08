class T20{

 public static void main(String[] args){
   System.out.println(new A().arraySum(10));
 }
}

class A{
int[] arr;
int[] arr2;
int c;
int a;
int b;
public int arraySum(int count){
  c = count;
  arr = new int[c];
  arr2 = new int[c];

  while(c != 0){
    c = c - 1;
    a = arr[c];
    b = arr2[c];
    arr[c] = a + b;
  }
  a = arr[c];
  b = arr2[c];
  arr[c] = a + b;

  return arr[0];

 }

}
