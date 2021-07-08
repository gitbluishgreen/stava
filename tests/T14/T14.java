class T14{

   public static void main(String[] args) {
      System.out.println(new B().multi(5,10));
   }
}

class A {
   int z;

   public int add(int x, int y){
      z = x + y;
      System.out.println(z);
      return z;
   }

   public int  sub(int x, int y){
      z = x - y;
      System.out.println(z);
      return z;
   }
}

class B extends A {

       public boolean multi(int x, int y) {
      z = x * y;
      System.out.println(z);
      z = this.add(x,y);
      z = this.sub(x,y);
      return true;
   }
 }
