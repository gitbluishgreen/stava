import syntaxtree.*;
import visitor.*;

public class P5 {
   public static void main(String [] args) {
      try {

         microIRParser mip = new microIRParser(System.in);
         Node root = microIRParser.Goal();
         root.accept(new RegisterAllocator<String, String>(), ""); 
      }
      catch (ParseException e) {
         System.out.println(e.toString());
      }
   }
}