package visitor;
import syntaxtree.*;
import java.util.*;

/**
 * Extended visitor to GJNoArguDepthFirst
 */
public class MIPSGenerator<R> extends GJNoArguDepthFirst<R> {

   String fName = "";
   String rdest = "";
   
   int argCnt = 0;
   int stackSlots = 0;
   int maxArgCnt=0;

   public R visit(NodeToken n) { return (R)n.tokenImage; }
   //
   // User-generated visitor methods below
   //

   /**
    * f0 -> "MAIN"
    * f1 -> "["
    * f2 -> IntegerLiteral()
    * f3 -> "]"
    * f4 -> "["
    * f5 -> IntegerLiteral()
    * f6 -> "]"
    * f7 -> "["
    * f8 -> IntegerLiteral()
    * f9 -> "]"
    * f10 -> StmtList()
    * f11 -> "END"
    * f12 -> ( SpillInfo() )?
    * f13 -> ( Procedure() )*
    * f14 -> <EOF>
    */
   public R visit(Goal n) {
      R _ret=null;
      fName = (String)n.f0.accept(this);
      System.out.println("\t.text");
      System.out.println("\t.globl\tmain");
      System.out.println("main:\n");
      n.f1.accept(this);
      argCnt = Integer.parseInt(n.f2.accept(this).toString());
      n.f3.accept(this);
      n.f4.accept(this);
      stackSlots = Integer.parseInt(n.f5.accept(this).toString());
      n.f5.accept(this);
      n.f6.accept(this);
      n.f7.accept(this);
      maxArgCnt = Integer.parseInt(n.f8.accept(this).toString());
      
      System.out.println("\tmove $fp, $sp");
      System.out.println("\tsubu $sp, $sp, "+
      (4*(2 + this.stackSlots + Math.max(0,this.maxArgCnt-4) ))+"");
      System.out.println("\tsw $ra, -4($fp)");
      
      n.f9.accept(this);
      n.f10.accept(this);
      n.f11.accept(this);

      System.out.println("\tlw $ra, -4($fp)");
      System.out.println("\taddu $sp, $sp, " + 
                        4*(2 + this.stackSlots + Math.max(this.maxArgCnt-4,0) - Math.max(this.argCnt-4,0)));
      System.out.println("\tjr $ra\n");

      n.f12.accept(this);
      //other procedures start
      fName = "";argCnt = maxArgCnt = stackSlots = 0;
      n.f13.accept(this);
      n.f14.accept(this);

      //auxillary functions
      {
         //Hallocate statement
         System.out.println("\t.text");
         System.out.println("\t.globl _halloc");
         System.out.println("_halloc:");
         System.out.println("\tli $v0, 9");
         System.out.println("\tsyscall");
         System.out.println("\tjr $ra");
         System.out.println("");
         //Print statement
         System.out.println("\t.text");
         System.out.println("\t.globl _print");
         System.out.println("_print:");
         System.out.println("\tli $v0, 1");
         System.out.println("\tsyscall");
         System.out.println("\tla $a0, newl");
         System.out.println("\tli $v0, 4");
         System.out.println("\tsyscall");
         System.out.println("\tjr $ra");
         System.out.println("");
         //newline
         System.out.println("\t.data");
         System.out.println("\t.align   0");
         System.out.println("newl:   .asciiz \"\\n\" ");
         //error
         System.out.println("\t.data");
         System.out.println("\t.align   0");
         System.out.println("str_er:  .asciiz \" ERROR: abnormal termination\\n\" ");
      }
      
      return _ret;
   }

   /**
    * f0 -> ( ( Label() )? Stmt() )*
    */
   public R visit(StmtList n) {
      R _ret=null;String l = "";

      if(n.f0.present()){
         for(int i=0;i<n.f0.size();++i){
            if(((NodeOptional)((NodeSequence)n.f0.elementAt(i)).elementAt(0)).present())
               l = (String) ((NodeSequence)n.f0.elementAt(i)).elementAt(0).accept(this);
            else l = "";
            if(!l.equals(""))System.out.print(l+":");
            ((NodeSequence)n.f0.elementAt(i)).elementAt(1).accept(this);
         }
      }
      return _ret;
   }

   /**
    * f0 -> Label()
    * f1 -> "["
    * f2 -> IntegerLiteral()
    * f3 -> "]"
    * f4 -> "["
    * f5 -> IntegerLiteral()
    * f6 -> "]"
    * f7 -> "["
    * f8 -> IntegerLiteral()
    * f9 -> "]"
    * f10 -> StmtList()
    * f11 -> "END"
    * f12 -> ( SpillInfo() )?
    */
   public R visit(Procedure n) {
      R _ret=null;
      this.fName = (String)n.f0.accept(this);
      n.f1.accept(this);
      this.argCnt = Integer.parseInt(n.f2.accept(this).toString());
      n.f3.accept(this);
      n.f4.accept(this);
      this.stackSlots = Integer.parseInt(n.f5.accept(this).toString());
      n.f6.accept(this);
      n.f7.accept(this);
      this.maxArgCnt = Integer.parseInt(n.f8.accept(this).toString());
      n.f9.accept(this);
      
      System.out.println("\t.text");
      System.out.println("\t.globl\t"+this.fName);
      System.out.println(this.fName+":\n");
      System.out.println("\tsw $fp, -8($sp)");
      System.out.println("\tmove $fp, $sp");
      System.out.println("\tsubu $sp, $sp, " + 
                        4*(2+this.stackSlots + Math.max(this.maxArgCnt-4,0) - Math.max(this.argCnt-4,0)));
      System.out.println("\tsw $ra, -4($fp)");  

      n.f10.accept(this);
      n.f11.accept(this);
      System.out.println("\tlw $ra, -4($fp)");
      System.out.println("\tlw $fp, " + 
                        4*(this.stackSlots + Math.max(this.maxArgCnt-4,0) - Math.max(this.argCnt-4,0)) + "($sp)");   
      System.out.println("\taddu $sp, $sp, " + 
                        4*(2+this.stackSlots + Math.max(this.maxArgCnt-4,0) - Math.max(this.argCnt-4,0)));
      System.out.println("\tjr $ra\n");
      n.f12.accept(this);
      fName = "";argCnt = maxArgCnt = stackSlots = 0;
      return _ret;
   }

   /**
    * f0 -> NoOpStmt()
    *       | ErrorStmt()
    *       | CJumpStmt()
    *       | JumpStmt()
    *       | HStoreStmt()
    *       | HLoadStmt()
    *       | MoveStmt()
    *       | PrintStmt()
    *       | ALoadStmt()
    *       | AStoreStmt()
    *       | PassArgStmt()
    *       | CallStmt()
    */
   public R visit(Stmt n) {
      R _ret=null;
      n.f0.accept(this);
      return _ret;
   }

   /**
    * f0 -> "NOOP"
    */
   public R visit(NoOpStmt n) {
      R _ret=null;
      n.f0.accept(this);
      System.out.println("\tnop");
      return _ret;
   }

   /**
    * f0 -> "ERROR"
    */
   public R visit(ErrorStmt n) {
      R _ret=null;
      n.f0.accept(this);
      System.out.println("\tli $v0, 4");
      System.out.println("\tla $a0, str_er");
      System.out.println("\tsyscall");
      System.out.println("\tli $v0, 10");
      System.out.println("\tsyscall");
      return _ret;
   }

   /**
    * f0 -> "CJUMP"
    * f1 -> Reg()
    * f2 -> Label()
    */
   public R visit(CJumpStmt n) {
      R _ret=null;String r = "";String l = "";
      n.f0.accept(this);
      r = (String)n.f1.accept(this);
      l = (String)n.f2.accept(this);
      System.out.println("\tbeqz "+r + " "+l);
      return _ret;
   }

   /**
    * f0 -> "JUMP"
    * f1 -> Label()
    */
   public R visit(JumpStmt n) {
      R _ret=null;String label = "";
      n.f0.accept(this);
      label = (String)n.f1.accept(this);
      System.out.println("\tb " + label);
      return _ret;
   }

   /**
    * f0 -> "HSTORE"
    * f1 -> Reg()
    * f2 -> IntegerLiteral()
    * f3 -> Reg()
    */
   public R visit(HStoreStmt n) {
      R _ret=null;
      String r1="",r2="";int pos4=0;
      n.f0.accept(this);
      r1 = (String)n.f1.accept(this);//address
      r2 = (String)n.f3.accept(this);//value
      pos4 = Integer.parseInt((String)n.f2.accept(this));
      System.out.println("\tsw "+r2+", "+pos4+"("+r1+")");
      return _ret;
   }

   /**
    * f0 -> "HLOAD"
    * f1 -> Reg()
    * f2 -> Reg()
    * f3 -> IntegerLiteral()
    */
   public R visit(HLoadStmt n) {
      R _ret=null;
      String r1,r2;int pos4 = 0;r1=r2="";
      n.f0.accept(this);
      r1 = (String)n.f1.accept(this);//register temp
      r2 = (String)n.f2.accept(this);//address
      pos4 = Integer.parseInt((String)n.f3.accept(this));
      System.out.println("\tlw "+ r1+", "+pos4+"("+r2+") ");
      return _ret;
   }

   /**
    * f0 -> "MOVE"
    * f1 -> Reg()
    * f2 -> Exp()
    */
   public R visit(MoveStmt n) {
      R _ret=null;String e = "";
      n.f0.accept(this);
      rdest = (String)n.f1.accept(this);
      n.f2.accept(this);
      rdest = "";
      return _ret;
   }

   /**
    * f0 -> "PRINT"
    * f1 -> SimpleExp()
    */
   public R visit(PrintStmt n) {
      R _ret=null;
      String se = "";
      n.f0.accept(this);
      se = (String)n.f1.accept(this);
      switch(n.f1.f0.which){
         case 0://reg
         System.out.println("\tmove $a0 " + se );
         System.out.println("\tjal _print");
         break;
         case 1://int
         System.out.println("\tli $a0   " + se );
         System.out.println("\tjal _print");
         break;
         default:
      }
      return _ret;
   }

   /**
    * f0 -> "ALOAD"
    * f1 -> Reg()
    * f2 -> SpilledArg()
    */
   public R visit(ALoadStmt n) {
      R _ret=null;
      String r = "";int pos = 0;
      n.f0.accept(this);
      r = n.f1.accept(this).toString();
      pos = Integer.parseInt(n.f2.accept(this).toString());
      //check if argument
      if(pos < Math.max(0,this.argCnt - 4))System.out.println("\tlw "+r+", "+(4*pos)+"($fp)");
      else System.out.println("\tlw "+r+", "+(4*(pos - Math.max(0,this.argCnt-4)+ Math.max(this.maxArgCnt-4, 0))) + "($sp)");
      return _ret;
   }

   /**
    * f0 -> "ASTORE"
    * f1 -> SpilledArg()
    * f2 -> Reg()
    */
   public R visit(AStoreStmt n) {
      R _ret=null;
      String r = "";
      int pos=0;

      n.f0.accept(this);
      pos = Integer.parseInt(n.f1.accept(this).toString());
      r = n.f2.accept(this).toString();
      if(pos < Math.max(0,this.argCnt - 4))System.out.println("\tsw "+r+", "+(4*pos)+"($fp)");
      else System.out.println("\tsw "+r+", "+(4*(pos + Math.max(this.maxArgCnt-4, 0) - Math.max(0, this.argCnt-4) )) + "($sp)");
      return _ret;
   }

   /**
    * f0 -> "PASSARG"
    * f1 -> IntegerLiteral()
    * f2 -> Reg()
    */
   public R visit(PassArgStmt n) {
      R _ret=null;String r = "";int pos = 0;
      n.f0.accept(this);
      pos = Integer.parseInt(n.f1.accept(this).toString());
      r = n.f2.accept(this).toString();
      System.out.println("\tsw "+r+", "+(4*(pos-1))+"($sp)");
      return _ret;
   }

   /**
    * f0 -> "CALL"
    * f1 -> SimpleExp()
    */
   public R visit(CallStmt n) {
      R _ret=null;
      String se = "";
      n.f0.accept(this);
      se = (String)n.f1.accept(this);
      switch(n.f1.f0.which){
         case 0://register
         System.out.println("\tjalr "+se);
         break;
         case 2://label
         System.out.println("\tjal "+ se);
         break;
         default:
      }
      return _ret;
   }

   /**
    * f0 -> HAllocate()
    *       | BinOp()
    *       | SimpleExp()
    */
   public R visit(Exp n) {
      R _ret=null;
      String e = "";
      e = (String)n.f0.accept(this);

      if(n.f0.which == 0)System.out.println("\tmove "+rdest+" $v0 ");
      else if(n.f0.which ==2){
         if(e.charAt(0) == '$')System.out.println("\tmove "+rdest+" "+e);
         else if(e.charAt(0) <= 57 && e.charAt(0) >=48 )System.out.println("\tli "+rdest+", "+e);
         else System.out.println("\tla "+rdest+", "+e);
      }else;
      return _ret;
   }

   /**
    * f0 -> "HALLOCATE"
    * f1 -> SimpleExp()
    */
   public R visit(HAllocate n) {
      R _ret=null;
      String se = "";
      n.f0.accept(this);
      se = (String)n.f1.accept(this);
      switch(n.f1.f0.which){
         case 0://register
         System.out.println("\tmove $a0 " + se + " ");
         System.out.println("\tjal _halloc ");
         _ret = (R)("$v0");
         break;
         case 1://integer literal
         System.out.println("\tli $a0 " + se + " ");
         System.out.println("\tjal _halloc ");
         _ret = (R)("$v0");
         break;
         default:
      }
      return _ret;
   }

   /**
    * f0 -> Operator()
    * f1 -> Reg()
    * f2 -> SimpleExp()
    */
   public R visit(BinOp n) {
      R _ret=null;
      String r = "";
      String s = "";
      n.f0.accept(this);
      r = (String)n.f1.accept(this);
      s = (String)n.f2.accept(this);
      System.out.println(" " + r + ", "+s );
      _ret = (R)"";
      return _ret;
   }

   /**
    * f0 -> "LE"
    *       | "NE"
    *       | "PLUS"
    *       | "MINUS"
    *       | "TIMES"
    *       | "DIV"
    */
   public R visit(Operator n) {
      R _ret=null;
      n.f0.accept(this);
      if(n.f0.which==0)     System.out.print("\tsle " + rdest + ", ");
      else if(n.f0.which==1)System.out.print("\tsne " + rdest + ", ");
      else if(n.f0.which==2)System.out.print("\tadd " + rdest + ", ");
      else if(n.f0.which==3)System.out.print("\tsub " + rdest + ", ");
      else if(n.f0.which==4)System.out.print("\tmul " + rdest + ", ");
      else if(n.f0.which==5)System.out.print("\tdiv " + rdest + ", ");
      else ; 
      return _ret;
   }

   /**
    * f0 -> "SPILLEDARG"
    * f1 -> IntegerLiteral()
    */
   public R visit(SpilledArg n) {
      R _ret=null;//return integer offset
      n.f0.accept(this);
      _ret = n.f1.accept(this);
      return _ret;
   }

   /**
    * f0 -> Reg()
    *       | IntegerLiteral()
    *       | Label()
    */
   public R visit(SimpleExp n) {
      R _ret=null;
      _ret = n.f0.accept(this);
      return _ret;
   }

   /**
    * f0 -> "a0"
    *       | "a1"
    *       | "a2"
    *       | "a3"
    *       | "t0"
    *       | "t1"
    *       | "t2"
    *       | "t3"
    *       | "t4"
    *       | "t5"
    *       | "t6"
    *       | "t7"
    *       | "s0"
    *       | "s1"
    *       | "s2"
    *       | "s3"
    *       | "s4"
    *       | "s5"
    *       | "s6"
    *       | "s7"
    *       | "t8"
    *       | "t9"
    *       | "v0"
    *       | "v1"
    */
   public R visit(Reg n) {
      R _ret=null;
      _ret = (R)("$" + n.f0.accept(this).toString());
      return _ret;
   }

   /**
    * f0 -> <INTEGER_LITERAL>
    */
   public R visit(IntegerLiteral n) {
      R _ret=null;
      _ret = n.f0.accept(this);
      return _ret;
   }

   /**
    * f0 -> <IDENTIFIER>
    */
   public R visit(Label n) {
      R _ret=null;
      _ret = n.f0.accept(this);
      return _ret;
   }

   /**
    * f0 -> "//"
    * f1 -> SpillStatus()
    */
   public R visit(SpillInfo n) {
      R _ret=null;
      n.f0.accept(this);
      n.f1.accept(this);
      return _ret;
   }

   /**
    * f0 -> <SPILLED>
    *       | <NOTSPILLED>
    */
   public R visit(SpillStatus n) {
      R _ret=null;
      n.f0.accept(this);
      return _ret;
   }

}
