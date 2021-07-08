public class Main
{
    public static void main(String[] args) {
    Test t = new Test();
    Test t1 = new Test();
    int y = t.x;
    //int y2 = t.x;
    t1.x = y;
    int y1 = t.x;    
}
//a = new A();
//====new 
//====dup
//====invokespecial
}

class Test
{
    int x;
    Test()
    {
        x = 10;
    }
}