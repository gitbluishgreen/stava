public class Main {
    public static void main(String[] arg)
    {
        A a = new A();
        B b = new B();
        int i,j;
        a.x = 10;
        a.y = 20;
        int z = b.foo();
        while(z < 0)
        {
            a.x *= 2;
            z++;
        }
        System.out.printf("%d %d\n",a.x,a.y);
        b.bar(0, b);
    }   
}
class A
{
    static B b1;
    int x;
    int y;
    private int z;
    A()
    {
        x = 10;
        y = 20;
        z = 0;
    }
    boolean nextIteration()
    {
        return (++z < 10);
    }
}
class B
{
    B()
    {

    }
    int foo()
    {
        return 0;
    }
    void bar(int x,B this1)
    {
        A.b1 = this1;
        return;
    }
}