public class Main {
    public static void main(String arg[])
    {
        A a = new A();
        a.next = new A();
        a.next.next = new A();
        int x = (arg.length > 0)?1:-1;
        a.foo(0);
    }
}
class A
{
    int x;
    int y;
    A next;
    int a_val = 10;
    void foo(int x)
    {
        B b = new B();
        b.foo();     
    }   
    class B
    {
        int a_val = 20;
        void foo()
        {
            for(int i = 0;i< 20;i++)
            {
                A.this.a_val = A.this.a_val + 1;
            }
        }
        class C
        {
            int a_val = 30;
            void foo(A obj)
            {
                B obj1 = new B();
                obj.a_val = 10;
                obj1.a_val = 20;
                for(int i = 0;i< 20;i++)
                {
                    A.this.a_val = A.this.a_val + 1;
                }
            }
        }
    }
}
