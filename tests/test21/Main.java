public class Main {
    public static void main(String arg[])
    {
            A a = new A();
            a.x = 20;
            a.arr = new int[a.x*2 + 3];
            a.arr[0] = 0;
            a.arr[1] = 1;
            a.x = 1;
            for(int i = 0;i < 10;i++)
                a.x += 1;
            int y = a.calc(10,20,a) + 25 + A.jayaku(a,new A());
            A b = new A();
            b.next = a.gimmenew(20,30);
            System.out.println(y+a.x);
        
    }   
}
class A
{
    int[] arr;
    int x;
    A next;
    int calc(int n,int u,A this1)
    {
        int i;
        for(i = 2;i < n;i++)
        {
            arr[i] = arr[i-1] + arr[i-2];
        }
        return arr[n-1];
    }
    A gimmenew(int x,int y)
    {
        return new A();
    }
    static int jayaku(A a, A a1)
    {
        return 10;
    }
}