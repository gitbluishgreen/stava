public class Main
{
    public static void main(String arg[])
    {
        A a = new A();
        //A a1 = new B();
        //long z = 0;
        //$i1 = $r0.x;
        //$i2 = $r0.y;
        //aloadi
        // ==> aload
        int b = a.x + a.y;
        //int c = a.x / 2;
        int c = b/4;
        a.x = c;
        //long z1 = z/16;
        //a.x = b + a.y*2 + a.x/2 + a.x*a.x;
        a.a1 = a;
        //System.out.printf("We have %d,%d\n",a.x,a.y);
        a.bar(10);
        return;
        //a1.a1 = a1;
        //a1.x = a.x + a1.y;
    }
}
class A
{
    public int x;
    public int y;
    public A a1;
    public A()
    {
        x = 10;
        y = 20;
    }
    public void bar(int y)
    {
        A a = new A();
        B1 b1 = new B1();
        float c1 = (float)2.1;
        byte b11 = (byte)1;
        A b = b1.test(null,0,0,1.1,null,false,c1,b11);
        a.x = 1 + 10 + 15;
        a.y = y + 20;
        a.x = y + a.y;
        int b12 = a.x + a.y*2 + a.x/2;
        int c = y+b12;
    }
    class B1
    {
        public A test(A[][][] j,int x,int y,double z,String[] arg,boolean x1,float c1,byte b1)
        {
            A a = new A();
            int b = a.x/2 + a.y/2 + a.x*a.x + 1;
            return a;
        }
    }
}
class B extends A
{
    public void bar(int z)
    {

    }
}