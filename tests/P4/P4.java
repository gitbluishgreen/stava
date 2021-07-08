import syntaxtree.*;
import visitor.*;

public class P4 {
   public static void main(String [] args) {
      try {
         MiniIRParser mjp = new MiniIRParser(System.in);
         Node root = MiniIRParser.Goal();
         root.accept(new GJDepthFirst<String, String>(), ""); // Your assignment part is invoked here.
      }
      catch (ParseException e) {
         System.out.println(e.toString());
      }
   }
} 


