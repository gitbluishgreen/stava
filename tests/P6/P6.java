import syntaxtree.*;
import visitor.*;

public class P6 {
   public static void main(String [] args) {
      try {

         MiniRAParser mip = new MiniRAParser(System.in);
         Node root = MiniRAParser.Goal();
         root.accept(new MIPSGenerator<String>()); 
      }
      catch (ParseException e) {
         System.out.println(e.toString());
      }
   }
}