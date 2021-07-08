import java.util.Scanner;
public class Main {
    public static void main(String arg[])
    {
        Scanner sc = new Scanner(System.in);
        int x = sc.nextInt();
        A a = new A();
        if(x < 10)
        {
            a.x = a.x + a.y;
            System.out.println("Path 1");
        }
        else
        {
            a.x = a.x - a.y;
            System.out.println("Path 2");
        }
        System.out.println(a.x + a.y);
        a.foo(a);
    }
}
class A
{
    int x;
    int y;
    A()
    {
        x = 10;
        y = 20;
    }
    void foo(A a)
    {
        a.x = 2 * a.y;
        a.y = a.x * 2;
        for(int i =0;i < 10;i++)
        {
            a.x = a.x + a.y;
            a.y = a.x - a.y;
            a.x = a.x - a.y;
        }
        System.out.println(a.x + a.y);
    }
}