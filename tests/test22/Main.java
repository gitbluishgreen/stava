public class Main {
 public static void main(String [] arg)
 {
     A a = new A();
     a.foo(a,a,10,a);
 }
}

class A
{
    int x;
    int y;
    void foo(A a,A b,int x, A c)
    {
        //A y = new A();
        this.x = 40;
        //this.y = 30;
        a.x = 10;
        b.x = 20;
        c.x = 30;
        a = new A();
        //a.x = 20;
    }
}