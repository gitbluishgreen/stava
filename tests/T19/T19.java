class T19{

 public static void main(String[] args){
   System.out.println(new A().fib(5));
 }
}

class A{
int n1;
int n2;
int n3;
boolean x;
public boolean printFibonacci(int count){
  if((0 <= count) && (count != 0)){
       n3 = n1 + n2;
       n1 = n2;
       n2 = n3;
       System.out.println(n3);
       x = this.printFibonacci(count-1);
   }
   return true;
 }

  public boolean fib(int i){
    int count;
    boolean b;
    n1 = 0;
    n2 = 1;
    n3 = 0;
    count = i;
    System.out.println(n1);
    System.out.println(n2);
    b = this.printFibonacci(count-2);
    return b;
  }
}
