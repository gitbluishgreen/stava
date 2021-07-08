import syntaxtree.*;
import visitor.*;
public class A2 {
    public static void main(String[] args)
    {
        try{
            Node p = new QParJavaParser(System.in).Goal();
            GJVoidDepthFirst<Integer> v = new GJVoidDepthFirst<Integer>();
            p.accept(v,1);
        }
        catch(ParseException e)
        {
            System.out.println(e.toString());
        }
    }    
}
