import syntaxtree.*;
import visitor.*;

public class P3 {
   public static void main(String [] args) {
      try {
         Node root = new MiniJavaParser(System.in).Goal();
         root.accept(new GJDepthFirst<String,String>(),"");           
      }
      catch (ParseException e) {
         System.out.println(e.toString());
      }
   }
} 



