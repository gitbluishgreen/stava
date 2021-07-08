/**
 * @author Vimarsh Sathia
 * 
 */

package visitor;
import syntaxtree.*;
import java.util.*;

class Tuple {

    public String var;
    public int first;
    public int second;
    public int isArg;
    public Tuple(){
        this.var = "";
        this.first = 0;
        this.second = 0;
    }

    public Tuple(String var,int first,int second){
        this.var = var;
        this.first = first;
        this.second = second;
    }

    public void print(){
        System.out.print("For " + var + "->");
        if(isArg == 0){
            System.out.println(" is a value from "+this.first + " to " + this.second);
        }
        
    }

}

class SymbolTable{

    HashMap<String, MethodTable> mlist;
    MethodTable curr_method;
    
    SymbolTable(){
        mlist = new HashMap<String,MethodTable>();
        curr_method = null;
    }

    void addMethod(String fName, String argCnt){
        MethodTable mobj = new MethodTable(fName,argCnt);
        mlist.put(fName, mobj);
    }

    void debugUseDef(){
        mlist.forEach((k,v)->v.debugUseDef());
    }
    
}

class MethodTable{

    String fName;
    String argCnt;
    int maxCallArgCnt;
    int allRegCnt;
    String [] allRegs;

    LinkedHashMap<String,BlockTable> labelMap;
    LinkedHashMap<Integer,BlockTable> blockMap;
    BlockTable curr_block;
    String curr_label;
    int blockCnt;

    LinkedHashSet<String> allTemps;
    LinkedHashMap<String,Tuple> liveIntervalMap;

    LinkedHashMap<String,String> regMap;
    LinkedHashMap<String,String> spillMap;
    int spillCnt;
    boolean spillFlag;

    //Number of stack slots needed
    int stackSlotCnt;
    //Start of s-registers in Stack
    int regBaseS;
    //Start of t-registers in Stack
    int regBaseT;
    //Start of spilled temps in Stack
    int spillBase;
    //no of arguments defined in block 0
    int argDef;

    MethodTable(){

        this.fName = null;
        this.argCnt = null;
        this.curr_block = null;
        this.maxCallArgCnt = 0;
        this.blockCnt = 1;
        this.curr_label = null;
        this.labelMap = new LinkedHashMap<String,BlockTable>();
        this.blockMap = new LinkedHashMap<Integer,BlockTable>();
        this.liveIntervalMap = new LinkedHashMap<String,Tuple>();
        this.allTemps = new LinkedHashSet<String>();
        this.spillFlag = false;
        this.allRegs = new String[]{"s0","s1","s2","s3","s4","s5","s6","s7","t0","t1","t2","t3","t4","t5","t6","t7","t8","t9"};
        this.allRegCnt = this.allRegs.length;
        this.regMap = new LinkedHashMap<String,String>();
        this.spillMap = new LinkedHashMap<>();
        this.spillCnt = 0;
        this.stackSlotCnt = 0;
        this.regBaseS = 0;
        this.regBaseT = 0;
        this.spillBase = 0;
        this.argDef = 0;
    }

    MethodTable(String fName,String argCnt){

        this.fName = fName;
        this.argCnt = argCnt;
        this.curr_block = null;
        this.blockCnt = 1;
        this.curr_label = null;
        this.labelMap = new LinkedHashMap<String,BlockTable>();
        this.blockMap = new LinkedHashMap<Integer,BlockTable>();
        this.liveIntervalMap = new LinkedHashMap<String,Tuple>();
        this.allTemps = new LinkedHashSet<String>();
        this.spillFlag = false;
        this.allRegs = new String[]{"s0","s1","s2","s3","s4","s5","s6","s7","t0","t1","t2","t3","t4","t5","t6","t7","t8","t9"};
        this.allRegCnt = this.allRegs.length;
        this.regMap = new LinkedHashMap<String,String>();
        this.spillMap = new LinkedHashMap<String,String>();
        this.spillCnt = 0;
        this.stackSlotCnt = 0;
        this.regBaseS = 0;
        this.regBaseT = 0;
        this.spillBase = 0;
        this.argDef = 0;
    }

    void setBlock(int addr){curr_block = blockMap.get(addr);}
    
    void resetBlock(){curr_block = null;}

    //returns the generated index-also updates successor of previous block
    int addBlock(){
        BlockTable node;
        if(this.curr_label != null){
            node = labelMap.get(curr_label);
        }
        else {
            node = new BlockTable();
        }
        if(blockMap.containsKey(blockCnt-1)){
            //reversi!
            BlockTable parent = blockMap.get(blockCnt-1);
            parent.addSucc(node);
        }
        blockMap.put(blockCnt,node);

        return blockCnt++;
    }

    //add entry to label list or set the value of curr_label if already present
    void addLabel(String label){
        if(labelMap.containsKey(label)){
            this.curr_label = label;
        }
        else{
            BlockTable node = new BlockTable(label);
            labelMap.put(label,node);
        }
    }

    void analyzeLiveness(){
        //Add a special block for the method arguments as def in block 0- only allocate upto 4 regs
        //rest are already spilt
        BlockTable special = new BlockTable();
        this.argDef = Math.min(4,Integer.parseInt(this.argCnt));
        for(int i = 0;i < this.argDef;++i){
            special.def.add("TEMP" + i);            
        }
        //Block 1 is successor of block 0
        special.succ.add(blockMap.get(1));
        blockMap.put(0,special);
        
        //Analyze liveness for all blocks starting from 0

        //System.out.println("Liveness for " + this.fName + "::");

        int iter=0;
        boolean flag = false;
        do{
            iter++;          
            flag = false;
            //Iterate and update in and out for every node 
            Iterator itr = blockMap.entrySet().iterator();
            while(itr.hasNext()){
                Map.Entry pair= (Map.Entry)itr.next();
                Integer key = (Integer)pair.getKey();
                BlockTable node = (BlockTable)pair.getValue();
                //save in in1 & out in out1
                node.copySet(1);
                node.copySet(2);
                //redefine in
                node.updateIn();
                //redefine out
                node.updateOut();
            }

            //Check exit condition
            itr = blockMap.entrySet().iterator();
            while(itr.hasNext()){

                Map.Entry pair= (Map.Entry)itr.next();
                Integer key = (Integer)pair.getKey();
                BlockTable node = (BlockTable)pair.getValue();

                flag = (flag) || !(node.checkSet());
            }

            //Debugging Liveness
            //this.debugLiveness(iter);
                   
        }while(flag);
        //System.out.println("Iterations = " + iter);

        //Update live ranges
        this.updateLiveRanges();
    }

    int getEnd(String str){
        //return last in as end
        int ans = 0;
        for(int j = this.blockCnt - 1; j>=0; --j){
            if(blockMap.get(j).in.contains(str)){ans = j;
                break;
            }
        }
        return ans;
    }

    int getStart(String str){
        //return first def as start
        int ansDef = this.blockCnt;

        for(int j = 0;j<this.blockCnt;++j){
            if(blockMap.get(j).def.contains(str)){ansDef = j;break;}
        }

        /* for(int j = 0;j<this.blockCnt;++j){
            if(blockMap.get(j).in.contains(str)){
                ansIn = j-1;
                break;
            }
        } */

        return ansDef;
        
    }
    
    void updateLiveRanges(){
        
        //Remove all arguments from TEMP<4> to TEMP<argCnt> from allTemps
        for(int i = 4;i < Integer.parseInt(this.argCnt);++i){
            this.allTemps.remove(("TEMP"+Integer.toString(i)));
        }

        Iterator<String> itr = this.allTemps.iterator();
        //System.out.println(allTemps);
        while(itr.hasNext()){
            String str = itr.next();
            int start = this.getStart(str);
            int end = this.getEnd(str);
            if(start > end){
                itr.remove();
                continue;
            }
            Tuple active_range = new Tuple(str,start,end);
            liveIntervalMap.put(str, active_range);
        }

        //this.debugLiveRanges();
    }
    
    void doLinearScan(){

        //Perform linear scan register  allocation
        ArrayList<Tuple> liveIntervals = new ArrayList<Tuple>();
        ArrayList<Tuple> activeIntervals = new ArrayList<Tuple>();
        ArrayList<Integer> activeRegisters = new ArrayList<Integer>();
        
        //Maintain free register pool
        boolean [] freeRegPool = new boolean[18];
        for(int i=0;i<18;++i)freeRegPool[i] = true;

        //Build liveIntervals
        Iterator itr = liveIntervalMap.entrySet().iterator();
        while(itr.hasNext()){
            Map.Entry pair = (Map.Entry)itr.next();
            Tuple curr_range = (Tuple)pair.getValue();
            int pos=-1;
            //Iterate and search for correct position to insert
            for(int i = 0;i < liveIntervals.size();++i){
                if(liveIntervals.get(i).first > curr_range.first){
                    pos = i;
                    break;
                }
            }

            if(pos==-1){
                //add at the end
                liveIntervals.add(curr_range);
            }
            else{
                //add in between
                liveIntervals.add(pos,curr_range);
            }
        }

        //this.debugLiveIntervalArray(liveIntervals);

        //Linear Scan Allocation
        for(int i = 0;i< liveIntervals.size();++i){
            //Expire all old Intervals
            for(int j = 0;j < activeIntervals.size();){
                if(activeIntervals.get(j).second >= liveIntervals.get(i).first){
                    break;
                }
                else{
                    freeRegPool[activeRegisters.get(j).intValue()] = true;
                    activeIntervals.remove(j);
                    activeRegisters.remove(j);
                }
            }

            //Check if we need to Spill
            if(activeRegisters.size() == this.allRegCnt){

                this.spillFlag = true;
                Tuple spill = activeIntervals.get(activeIntervals.size()-1);

                if(spill.second > liveIntervals.get(i).second){
                    int freshReg = activeRegisters.get(activeRegisters.size()-1).intValue();
                    activeRegisters.remove(activeRegisters.size()-1);
                    activeIntervals.remove(activeIntervals.size()-1);
                    //Add spill to stack space && remove entry from reg_map
                    regMap.remove(spill.var);
                    regMap.put(liveIntervals.get(i).var, this.allRegs[freshReg]);
                    spillMap.put(spill.var, Integer.toString(spillCnt++));

                    //Add i to active
                    //Find position in sorted order
                    int pos = -1;
                    for(int j = 0;j < activeIntervals.size();++j){
                        if(activeIntervals.get(j).second > liveIntervals.get(i).second){
                            pos = j;
                            break;
                        }
                    }
                    //Add at position pos
                    if(pos == -1){
                        activeIntervals.add(liveIntervals.get(i));
                        activeRegisters.add(Integer.valueOf(freshReg));
                    }
                    else{
                        activeIntervals.add(pos, liveIntervals.get(i));
                        activeRegisters.add(pos,Integer.valueOf(freshReg));
                    }

                }
                else{
                    //add liveIntervals[i] to new stack location
                    spillMap.put(liveIntervals.get(i).var,Integer.toString(spillCnt++));
                }
                
            }
            else{
                //Assign a free register
                int freshReg=0;
                while(freshReg<18){
                    
                    if(freeRegPool[freshReg] == true){
                        freeRegPool[freshReg] = false;
                        break;
                    }
                    ++freshReg;
                }
                //Assign freshReg to interval i 
                regMap.put(liveIntervals.get(i).var,this.allRegs[freshReg]);
                //Add to active
                int pos = -1;
                for(int j = 0;j < activeIntervals.size();++j){
                    if(activeIntervals.get(j).second > liveIntervals.get(i).second){
                        pos = j;
                        break;
                    }
                }
                //Add at position pos
                if(pos == -1){
                    activeIntervals.add(liveIntervals.get(i));
                    activeRegisters.add(Integer.valueOf(freshReg));
                }
                else{
                    activeIntervals.add(pos, liveIntervals.get(i));
                    activeRegisters.add(pos,Integer.valueOf(freshReg));
                }
            }
        }
        this.initializeStackSlots();
    }

    void initializeStackSlots(){

        //A procedure's stack contains its arguments, s & t registers and spilled variables 
        
        //Arguments
        if(Integer.parseInt(this.argCnt) > 4){
            this.stackSlotCnt += Integer.parseInt(this.argCnt) - 4;
        }
        //s-registers start here 
        this.regBaseS = this.stackSlotCnt;
        
        if(!this.fName.equals("MAIN")){
            //no need to save s registers in MAIN function(optimization)
            this.stackSlotCnt+=8;
        }

        //t-registers start here
        this.regBaseT = this.stackSlotCnt;
        //Only save the t-registers if there is a function call(optimization)
        if(this.maxCallArgCnt >= 0){this.stackSlotCnt +=10;}

        //spilled variables start here
        this.spillBase = this.stackSlotCnt;
        
        //Modify all entries in spillMap to add an offset of spill base -lambda power!
        spillMap.replaceAll((k, v) -> Integer.toString(Integer.parseInt(v) + this.spillBase));
        
        //Add arguments of the function > 4 to the spill map also
        for(int i = 4; i < Integer.parseInt(this.argCnt); ++i){
            spillMap.put("TEMP"+Integer.toString(i), Integer.toString(i-4));
        }

        this.stackSlotCnt+=this.spillCnt;     
        //this.debugLinearScan();
    }

    void debugUseDef(){
        System.out.println("In Function :: " + this.fName);
        blockMap.forEach((k,v)->{System.out.println("Block " + (1+k.intValue()) + "::");v.debugUseDef();});
    }

    void debugLiveness(int iter){
        Iterator itr = blockMap.entrySet().iterator();
        System.out.println("~Iteration : " + iter );
        while(itr.hasNext()){
            Map.Entry pair= (Map.Entry)itr.next();
            Integer key = (Integer)pair.getKey();
            BlockTable node = (BlockTable)pair.getValue();
            System.out.println("##Block " + (key.intValue()) + " : ");
            
            System.out.print("Use -> ");
            System.out.println(node.use);
            System.out.print("Def -> ");
            System.out.println(node.def);
            System.out.print("In1 -> ");
            System.out.println(node.in1);
            System.out.print("In -> ");
            System.out.println(node.in);
            System.out.print("Out1 -> ");
            System.out.println(node.out1);
            System.out.print("Out -> ");
            System.out.println(node.out);
        }
    }

    void debugLiveRanges(){
        System.out.println("In Function :: "+ this.fName);
        liveIntervalMap.forEach((k,v)->{v.print();});
    }

    void debugLiveIntervalArray(ArrayList<Tuple> arr){
        System.out.println("Printing live intervals before linear scan(sorted)");
        arr.forEach((n)->{n.print();});
    }

    void debugLinearScan(){
        //Print out reg_map and spill_map for program
        System.out.println("In Function :: " + this.fName);
        System.out.println("Register mapping is :: ");
        System.out.println(regMap);
        System.out.println("Spill mapping is :: ");
        System.out.println(spillMap);
    }

}

class BlockTable{

    LinkedHashSet<String> use;
    LinkedHashSet<String> def;
    LinkedHashSet<String> in;
    LinkedHashSet<String> out;
    LinkedHashSet<String> in1;
    LinkedHashSet<String> out1;
    LinkedHashSet<BlockTable> succ;

    String label;

    BlockTable(){
        label = null;
        use = new LinkedHashSet<String>();
        def = new LinkedHashSet<String>();
        in = new LinkedHashSet<String>();
        out = new LinkedHashSet<String>();
        in1 = new LinkedHashSet<String>();
        out1 = new LinkedHashSet<String>();
        succ = new LinkedHashSet<BlockTable>();
    }

    BlockTable(String label){
        this.label = label;
        use = new LinkedHashSet<String>();
        def = new LinkedHashSet<String>();
        in = new LinkedHashSet<String>();
        out = new LinkedHashSet<String>();
        in1 = new LinkedHashSet<String>();
        out1 = new LinkedHashSet<String>();
        succ = new LinkedHashSet<BlockTable>();
    }

    void addDef(String temp){this.def.add(temp);}
    void addUse(String temp){this.use.add(temp);}
    void addSucc(BlockTable node){this.succ.add(node);}
    boolean checkSet(){return in1.equals(in) && out1.equals(out);}

    void copySet(int choice){
        if(choice==1){
            this.in1 = new LinkedHashSet<String>();
            Iterator<String> itr = this.in.iterator();
            while(itr.hasNext()){this.in1.add(itr.next());}
        }else{
            this.out1= new LinkedHashSet<String>();
            Iterator<String> itr = this.out.iterator();//reversi!
            while(itr.hasNext()){this.out1.add(itr.next());}
        }
    }

    void updateIn(){
        this.in =  new LinkedHashSet<String>();

        //add elements of use
        Iterator<String> itr = this.use.iterator();
        while(itr.hasNext()){
            this.in.add(itr.next());
        }
        
        LinkedHashSet noDefs = new LinkedHashSet<String>();
        
        itr = this.out.iterator();
        while(itr.hasNext()){
            String tmp = itr.next();
            if(!this.def.contains(tmp))noDefs.add(tmp);
        }
        //Take union
        itr = noDefs.iterator();
        while(itr.hasNext()){
            String temp = itr.next();
            if(!this.in.contains(temp)){this.in.add(temp);}
        }

    }

    void updateOut(){
        this.out = new LinkedHashSet<String>();
        //take union of in of successors and put it in out
        Iterator<BlockTable> itr = this.succ.iterator();
        while(itr.hasNext()){
            BlockTable child = itr.next();
            Iterator<String> itr2 = child.in.iterator();
            while(itr2.hasNext()){
                String temp = itr2.next();
                this.out.add(temp);
            }
        }
    }
    
    void debugUseDef(){

        if(label!=null)System.out.println("Label:"+label);
        System.out.print("Def:\n");
        System.out.println(this.def);
        System.out.print("Use:\n");
        System.out.println(this.use);
        System.out.println();
    }

}

public class RegisterAllocator<R,A> extends GJDepthFirst<R,A> {

    SymbolTable T = new SymbolTable();

    public R visit(NodeToken n, A argu) { return (R)n.tokenImage; }

    /**
    * f0 -> "MAIN"
    * f1 -> StmtList()
    * f2 -> "END"
    * f3 -> ( Procedure() )*
    * f4 -> <EOF>
    */
    public R visit(Goal n, A argu) {
        R _ret=null;

        n.f0.accept(this,(A)"1");
        
        T.addMethod("MAIN","0");
        T.curr_method = T.mlist.get("MAIN");
        
        n.f1.accept(this,(A)"1");
        n.f2.accept(this,(A)"1");

        T.curr_method.analyzeLiveness();
        T.curr_method.doLinearScan();

        T.curr_method = null;
        
        n.f3.accept(this,(A)"1");
        n.f4.accept(this,(A)"1");
        
        //Code Generation

        n.f0.accept(this,(A)"2");

        T.curr_method = T.mlist.get("MAIN");
        System.out.println("MAIN [" + T.curr_method.argCnt + "] [" + T.curr_method.stackSlotCnt + "] [" 
        + T.curr_method.maxCallArgCnt + "] " );
         
        n.f1.accept(this,(A)"2");
        n.f2.accept(this,(A)"2");

        System.out.println("END");
        if(T.curr_method.spillFlag)System.out.println("//SPILLED");
        else System.out.println("//NOTSPILLED");
        T.curr_method = null;

        n.f3.accept(this,(A)"2");
        n.f4.accept(this,(A)"2");

        //T.debugUseDef();
        return _ret;
    }

    /**
    * f0 -> ( ( Label() )? Stmt() )*
    */
    public R visit(StmtList n, A argu) {
        R _ret=null;
        String label = "";

        if(n.f0.present()){
            //Check if label is present
            for(int i=0;i<n.f0.size();++i){

                //Label
                if(((NodeOptional)((NodeSequence)n.f0.elementAt(i)).elementAt(0)).present()){
                    label = (String) ((NodeSequence)n.f0.elementAt(i)).elementAt(0).accept(this,argu);

                    if(argu.toString().equals("1")){
                        T.curr_method.addLabel(label);
                    }

                    T.curr_method.curr_label = label;
                } 
                else{
                    label = "";
                }

                //Print label-need to check global??
                if(argu.toString().equals("2")){
                    if(!label.equals("")){
                        System.out.println(T.curr_method.fName + label);
                    }
                }

                //Statement
                ((NodeSequence)n.f0.elementAt(i)).elementAt(1).accept(this,argu);
                T.curr_method.curr_label = null;

            }
        }
        return _ret;
    }

    /**
    * f0 -> Label()
    * f1 -> "["
    * f2 -> IntegerLiteral()
    * f3 -> "]"
    * f4 -> StmtExp()
    */
    public R visit(Procedure n, A argu) {
        R _ret=null;
        String fName = "";
        String argCnt = "";

        switch(argu.toString()){

            case "1": 
            fName = (String)n.f0.accept(this, argu);
            n.f1.accept(this, argu);
            argCnt = (String)n.f2.accept(this, argu);
            n.f3.accept(this, argu);
            T.addMethod(fName,argCnt);
            T.curr_method = T.mlist.get(fName);
            n.f4.accept(this, argu);
            T.curr_method.analyzeLiveness();
            T.curr_method.doLinearScan();
            T.curr_method = null;
            break;

            case "2":
            fName = (String)n.f0.accept(this, argu);
            n.f1.accept(this, argu);
            argCnt = (String)n.f2.accept(this, argu);
            n.f3.accept(this, argu);
            T.curr_method = T.mlist.get(fName);

            System.out.println(fName + " [" + argCnt + "] [" + T.curr_method.stackSlotCnt + "] ["
                              + T.curr_method.maxCallArgCnt + "] " );
            
            n.f4.accept(this, argu);
            if(T.curr_method.spillFlag)System.out.println("//SPILLED");
            else System.out.println("//NOTSPILLED");
            T.curr_method = null;
            break;
        }
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
    */
    public R visit(Stmt n, A argu) {
        R _ret=null;
        n.f0.accept(this, argu);
        return _ret;
    }

    /**
    * f0 -> "NOOP"
    */
    public R visit(NoOpStmt n, A argu) {
        R _ret=null;
        if(argu.toString().equals("1")){int index = T.curr_method.addBlock();}
        if(argu.toString().equals("2")){System.out.println("\tNOOP");}
        n.f0.accept(this, argu);
        return _ret;
    }

    /**
    * f0 -> "ERROR"
    */
    public R visit(ErrorStmt n, A argu) {
        R _ret=null;
        if(argu.toString().equals("1")){int index = T.curr_method.addBlock();}
        if(argu.toString().equals("2")){System.out.println("\tERROR");}
        n.f0.accept(this, argu);
        return _ret;
    }

    /**
    * f0 -> "CJUMP"
    * f1 -> Temp()
    * f2 -> Label()
    */
    public R visit(CJumpStmt n, A argu) {
        R _ret=null;
        String tmp = "";
        String label = "";
        int index = 0;

        switch(argu.toString()){
            case "1":
                index = T.curr_method.addBlock();
                T.curr_method.setBlock(index);
                n.f0.accept(this, argu);    
                tmp = (String)n.f1.accept(this, argu);
                label = (String)n.f2.accept(this, argu);
                T.curr_method.curr_block.addUse(tmp);
                T.curr_method.addLabel(label);
                //add label node as a successor to the current node
                T.curr_method.curr_block.addSucc(T.curr_method.labelMap.get(label));
                T.curr_method.resetBlock();
                break;
            case "2":
                n.f0.accept(this,argu);
                tmp = (String)n.f1.accept(this,argu);
                label = (String)n.f2.accept(this,argu);
                
                if(T.curr_method.regMap.containsKey(tmp)){
                    //register mapped value
                    System.out.println("\tCJUMP " + T.curr_method.regMap.get(tmp) + " " +T.curr_method.fName + label + " " );
                }else{
                    //tmp is a spilled value
                    System.out.println("\tALOAD v1 SPILLEDARG " + T.curr_method.spillMap.get(tmp));
                    System.out.println("\tCJUMP v1 " + T.curr_method.fName + label);
                }
            break;
        }
        return _ret;
    }

    /**
    * f0 -> "JUMP"
    * f1 -> Label()
    */
    public R visit(JumpStmt n, A argu) {
        R _ret=null;
        String label = "";
        int index = 0;
        if(argu.toString().equals("1")){
            index = T.curr_method.addBlock();
            T.curr_method.setBlock(index);
        }
        n.f0.accept(this, argu);
        label = (String)n.f1.accept(this, argu);

        if(argu.toString().equals("1")){
            T.curr_method.addLabel(label);
            T.curr_method.curr_block.addSucc(T.curr_method.labelMap.get(label));
            T.curr_method.resetBlock();
        }

        if(argu.toString().equals("2")){
            System.out.println("\tJUMP " + T.curr_method.fName + label + " ");
        }
        return _ret;
    }

    /**
    * f0 -> "HSTORE"
    * f1 -> Temp()
    * f2 -> IntegerLiteral()
    * f3 -> Temp()
    */
    public R visit(HStoreStmt n, A argu) {
        R _ret=null;
        String tmp1 ="";
        String tmp2 = "";
        String offset = "";
        if(argu.toString().equals("1")){
            T.curr_method.setBlock(T.curr_method.addBlock());
        }

        n.f0.accept(this, argu);
        tmp1 = (String)  n.f1.accept(this, argu);
        offset = (String)n.f2.accept(this, argu);
        tmp2 = (String)  n.f3.accept(this, argu);
        
        if(argu.toString().equals("1")){
            T.curr_method.curr_block.addUse(tmp1);
            T.curr_method.curr_block.addUse(tmp2);
            T.curr_method.resetBlock();
        }

        if(argu.toString().equals("2")){
            
            if(T.curr_method.regMap.containsKey(tmp1)){
                if(T.curr_method.regMap.containsKey(tmp2)){
                    //Both mapped
                    System.out.println("\tHSTORE " + T.curr_method.regMap.get(tmp1) + " " + offset + " " 
                    + T.curr_method.regMap.get(tmp2) + " ");
                }
                else{
                    //tmp1 mapped, tmp2 spilled
                    System.out.println("\tALOAD v1 SPILLARG " + T.curr_method.spillMap.get(tmp2));
                    System.out.println("\tHSTORE " + T.curr_method.regMap.get(tmp1) + " " + offset + " v1 ");
                }
            }else{
                if(T.curr_method.regMap.containsKey(tmp2)){
                    //tmp 2 mapped, tmp1 spilled
                    System.out.println("\tALOAD v1 SPILLARG " + T.curr_method.spillMap.get(tmp1));
                    System.out.println("\tHSTORE v1 " + " " + offset + " " + T.curr_method.regMap.get(tmp2) + " ");
                }
                else{
                    //Both are spilled
                    System.out.println("\tALOAD v0 SPILLARG " + T.curr_method.spillMap.get(tmp1));
                    System.out.println("\tALOAD v1 SPILLARG " + T.curr_method.spillMap.get(tmp2));
                    System.out.println("\tHSTORE v0 " + offset + " v1 ");
                }
            }

        }
        return _ret;
    }

    /**
    * f0 -> "HLOAD"
    * f1 -> Temp()
    * f2 -> Temp()
    * f3 -> IntegerLiteral()
    */
    public R visit(HLoadStmt n, A argu) {
        //def
        R _ret=null;
        String tmp1 ="";
        String tmp2 = "";
        String offset = "";
        if(argu.toString().equals("1")){
            T.curr_method.setBlock(T.curr_method.addBlock());
        }

        n.f0.accept(this, argu);
        tmp1 = (String)n.f1.accept(this, argu);
        tmp2 = (String)n.f2.accept(this, argu);
        offset= (String)n.f3.accept(this, argu);

        if(argu.toString().equals("1")){
            T.curr_method.curr_block.addDef(tmp1);
            T.curr_method.curr_block.addUse(tmp2);
            T.curr_method.resetBlock();
        }

        if(argu.toString().equals("2")){
            if(T.curr_method.regMap.containsKey(tmp1)){
                if(T.curr_method.regMap.containsKey(tmp2)){
                    System.out.println("\tHLOAD " + T.curr_method.regMap.get(tmp1) + " " 
                    + T.curr_method.regMap.get(tmp2) + " "+ offset +" ");
                }
                else{
                    //tmp2 spilled
                    System.out.println("\tALOAD v1 SPILLARG " + T.curr_method.spillMap.get(tmp2) + " ");
                    System.out.println("\tHLOAD " + T.curr_method.regMap.get(tmp1)  + " v1 " + offset+ " ");
                }
            }
            else if(T.curr_method.spillMap.containsKey(tmp1)){
                if(T.curr_method.regMap.containsKey(tmp2)){
                    //tmp1 spilled
                    System.out.println("\tHLOAD v1 " + T.curr_method.regMap.get(tmp2) + " "+ offset + " " );
                    System.out.println("\tASTORE SPILLEDARG " + T.curr_method.spillMap.get(tmp1) + " v1 ");
                }
                else{
                    //both spilled
                    System.out.println("\tALOAD v0 SPILLARG " + T.curr_method.spillMap.get(tmp2) + " ");
                    System.out.println("\tHLOAD v1 " + " v0 " + offset+" ");
                    System.out.println("\tASTORE SPILLEDARG " + T.curr_method.spillMap.get(tmp1) + " v1 ");
                }
            }
            else{
                //dont load at all-temp 1 is unused
            }
        }

        return _ret;
    }

    /**
    * f0 -> "MOVE"
    * f1 -> Temp()
    * f2 -> Exp()
    */
    public R visit(MoveStmt n, A argu) {
        //Def
        R _ret=null;
        String tmp1 = "";
        String expr = "";
        
        if(argu.toString().equals("1")){
            T.curr_method.setBlock(T.curr_method.addBlock());
        }
        
        n.f0.accept(this, argu);
        tmp1 = (String)n.f1.accept(this, argu);
        expr = (String)n.f2.accept(this, argu);

        if(argu.toString().equals("1")){
            T.curr_method.curr_block.addDef(tmp1);
            T.curr_method.resetBlock();
        }

        if(argu.toString().equals("2")){
            if(T.curr_method.regMap.containsKey(tmp1)){
                System.out.print("\tMOVE " + T.curr_method.regMap.get(tmp1) + " " + expr + "\n");
            }
            else if(T.curr_method.spillMap.containsKey(tmp1)){
                //expr might contain an instruction or a ret value or a ret register-> if its spilledarg then always v0
                System.out.print("\tMOVE v1 "+ expr + "\n");
                System.out.println("\tASTORE SPILLEDARG " + T.curr_method.spillMap.get(tmp1) + " v1 ");
            }
            else{
                //do nothing- temp is unused
            }
        }

        return _ret;
    }

    /**
    * f0 -> "PRINT"
    * f1 -> SimpleExp()
    */
    public R visit(PrintStmt n, A argu) {
        R _ret=null;
        String inst0 = "";
        String inst1 = "";
        if(argu.toString().equals("1")){
            T.curr_method.setBlock(T.curr_method.addBlock());
        }

        inst0 = (String)n.f0.accept(this, argu);
        inst1 = (String)n.f1.accept(this, argu);

        if(argu.toString().equals("1")){
            T.curr_method.resetBlock();
        }

        if(argu.toString().equals("2")){
            if(T.curr_method.regMap.containsKey(inst1)){
                System.out.println("\t" + inst0 + " " + T.curr_method.regMap.get(inst1) + " ");
            }
            else{
                System.out.println("\tALOAD v1 SPILLEDARG " + T.curr_method.spillMap.get(inst1)+ " ");
                System.out.println("\t" + inst0 + " v1 ");
            }
        }

        return _ret;
    }

    /**
    * f0 -> Call()
    *       | HAllocate()
    *       | BinOp()
    *       | SimpleExp()
    */
    public R visit(Exp n, A argu) {
        R _ret=null;
        _ret = n.f0.accept(this, argu);
        if(n.f0.which == 3){
            //If simpleExp is a temp, then convert it to a register
            String str = (String)_ret;
            if(str.startsWith("TEMP")){
                //convert it to register
                if(T.curr_method.regMap.containsKey(str)){
                    _ret = (R)T.curr_method.regMap.get(str);
                }
                else{
                    //register is spilled->return v0
                    if(argu.toString().equals("2")){
                        System.out.println("\tALOAD v0 SPILLEDARG " + T.curr_method.spillMap.get(str));
                        _ret = (R)("v0");
                    }
                        
                }
            }
            else{
                //Check if element is an integer, if yes move it to v1 and return v1
                if(argu.toString().equals("2")){
                    try{
                        int x = Integer.parseInt(str);
                        System.out.println("\tMOVE v1 "+ str);
                        _ret = (R)("v1");
                    }
                    catch(Exception e){
                        //do nothing to _ret value
                        _ret = (R)str;
                    }
                }
                
            }
        }

        return _ret;
    }

    /**
    * f0 -> "BEGIN"
    * f1 -> StmtList()
    * f2 -> "RETURN"
    * f3 -> SimpleExp()
    * f4 -> "END"
    */
    public R visit(StmtExp n, A argu) {
        R _ret=null;
        String rval = "";
        switch(argu.toString()){

            case "1":
            n.f0.accept(this, argu);
            n.f1.accept(this, argu);
            n.f2.accept(this, argu);
            int addr = T.curr_method.addBlock();
            T.curr_method.setBlock(addr);
            n.f3.accept(this, argu);
            n.f4.accept(this, argu);
            T.curr_method.resetBlock();
            break;
            
            case "2":
            n.f0.accept(this, argu);

            //Save s-registers
            for(int i=0;i<=7;++i){
                System.out.println("\tASTORE SPILLEDARG "+(T.curr_method.regBaseS + i) +" s" + i);
            }

            //Assign registers to arguments- other arguments are already spilled
            for(int i = 0; i < Math.min(4,Integer.parseInt(T.curr_method.argCnt));++i){
                if(T.curr_method.regMap.containsKey("TEMP" + Integer.toString(i))){
                    System.out.println("MOVE " + T.curr_method.regMap.get("TEMP" + Integer.toString(i)) + " a" + i);
                }
            }

            n.f1.accept(this, argu);
            n.f2.accept(this, argu);

            rval = (String)n.f3.accept(this, argu);
            if(rval.startsWith("TEMP")){
                if(T.curr_method.regMap.containsKey(rval)){
                    System.out.println("\tMOVE v0 " + T.curr_method.regMap.get(rval));
                }
                else{
                    System.out.println("\tALOAD v1 SPILLEDARG "+  T.curr_method.spillMap.get(rval));
                    System.out.println("\tMOVE v0 v1 ");
                }
            }
            else{
                //Integer literal
                System.out.println("\tMOVE v0 " + rval);
            }

            n.f4.accept(this, argu);
            
            //Restore s- registers
            for(int i = 0;i <= 7 ; ++i){
                System.out.println("\tALOAD s" + (i) +" SPILLEDARG " + (T.curr_method.regBaseS +i));
            }
            System.out.println("END");
            break;
        }

        return _ret;
    }

    /**
    * f0 -> "CALL"
    * f1 -> SimpleExp()
    * f2 -> "("
    * f3 -> ( Temp() )*
    * f4 -> ")"
    */
    public R visit(Call n, A argu) {
        R _ret=null;
        
        if(argu.toString().equals("2")){
            //Save all t regs
            for(int i = 0;i<=9;++i){
                System.out.println("\tASTORE SPILLEDARG " + (T.curr_method.regBaseT + i) + " t" + i);
            }
        }
        
        String tmp= "";
        String inst0= "";
        String inst1 = "";
        
        inst0 = (String)n.f0.accept(this, argu);
        inst1 = (String)n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        
        if(n.f3.present()){
            if(argu.toString().equals("1")){
                T.curr_method.maxCallArgCnt = Math.max(T.curr_method.maxCallArgCnt,n.f3.size());
            }
            
            for(int i=0;i<n.f3.size();++i){
                tmp = (String)((Node)n.f3.elementAt(i)).accept(this,argu);
                if(argu.toString().equals("1")){
                    T.curr_method.curr_block.addUse(tmp);
                }
                if(argu.toString().equals("2")){
                    if(i < 4){
                        if(T.curr_method.regMap.containsKey(tmp)){
                            System.out.println("\tMOVE a" + i + " " + T.curr_method.regMap.get(tmp) + " ");
                        }
                        else{
                            //tmp is spilt
                            System.out.println("\tALOAD v1 SPILLEDARG " + T.curr_method.spillMap.get(tmp) + " ");
                            System.out.println("\tMOVE a"+i +" v1 " );
                        }
                    }
                    else{
                        //a is a passarg
                        if(T.curr_method.regMap.containsKey(tmp)){
                            System.out.println("\tPASSARG "+ (i-3) + " "+ T.curr_method.regMap.get(tmp) + " ");
                        }
                        else{
                            //temp is spilt
                            System.out.println("\tALOAD v1 SPILLEDARG "+ T.curr_method.spillMap.get(tmp) + " ");
                            System.out.println("\tPASSARG "+ (i-3) + " v1 ");
                        }
                    }
                }
            }
        }

        if(argu.toString().equals("2")){

            if(inst1.startsWith("TEMP")){
                if(T.curr_method.regMap.containsKey(inst1)){
                    System.out.println("\tCALL "  + T.curr_method.regMap.get(inst1) + " ");
                }
                else{
                    //spilt calling function temp
                    System.out.println("\tALOAD v1 SPILLEDARG "+T.curr_method.spillMap.get(inst1) + " ");
                    System.out.println("\tCALL v1 ");
                }
            }
            else{
                //label
                System.out.println("\tCALL "+inst1);
            }
            //Restore all t regs
            for(int i = 0;i<=9;++i){
                System.out.println("\tALOAD t" + i + " SPILLEDARG " + (i+ T.curr_method.regBaseT) + " ");
            }
        }

        n.f4.accept(this, argu);
        _ret = (R)("v0");
        return _ret;
    }

    /**
    * f0 -> "HALLOCATE"
    * f1 -> SimpleExp()
    */
    public R visit(HAllocate n, A argu) {
        //HAllocate with a padding of 8 bytes
        R _ret=null;
        String inst0 = "";
        String inst1 = "";
        inst0 = (String)n.f0.accept(this, argu);
        inst1 = (String)n.f1.accept(this, argu);
        
        if(argu.toString().equals("2")){
            if(inst1.startsWith("TEMP")){
                //Temporary
                if(T.curr_method.regMap.containsKey(inst1)){
                    //has register value
                    System.out.println("\tMOVE v0 PLUS " +T.curr_method.regMap.get(inst1)+ " 8 " );
                    _ret = (R)("HALLOCATE v0 ");
                }else{
                    //has been spilled
                    System.out.println("\tALOAD v0 SPILLEDARG " + T.curr_method.spillMap.get(inst1));
                    System.out.println("\tMOVE v0 PLUS v0 8 ");
                    _ret = (R)("HALLOCATE v0 ");
                }
            }else{
                //Integer literal or label
                _ret = (R)(inst0 + " " + inst1);
            }
        }
        return _ret;
    }

    /**
    * f0 -> Operator()
    * f1 -> Temp()
    * f2 -> SimpleExp()
    */
    public R visit(BinOp n, A argu) {
        R _ret=null;
        String inst0 = "";
        String inst1 = "";
        String inst2= "";
        inst0 = (String)n.f0.accept(this, argu);
        inst1 = (String)n.f1.accept(this, argu);
        
        if(argu.toString().equals("1")){
            T.curr_method.curr_block.addUse(inst1);    
        }

        inst2 = (String)n.f2.accept(this, argu);
        
        //Returns instruction itself
        if(argu.toString().equals("2")){
            if(T.curr_method.regMap.containsKey(inst1)){
                //mapped temp1
                if(inst2.startsWith("TEMP")){
                    //Temporary
                    if(T.curr_method.regMap.containsKey(inst2)){
                        //mapped temp2
                        _ret = (R)(inst0 + " " + T.curr_method.regMap.get(inst1) + " " + T.curr_method.regMap.get(inst2) + " ");
                    }
                    else{
                        //temp 2 is spilt
                        System.out.println("\tALOAD v1 SPILLEDARG " + T.curr_method.spillMap.get(inst2));
                        _ret = (R)(inst0 + " " +T.curr_method.regMap.get(inst1) + " v1 ");
                    }
                }
                else{
                    //Integer literal
                    _ret = (R)(inst0 + " " + T.curr_method.regMap.get(inst1) + " " + inst2 + " ");
                }
            }else{
                //spilt temp1
                if(inst2.startsWith("TEMP")){
                    //Temporary
                    if(T.curr_method.regMap.containsKey(inst2)){
                        //mapped temp2
                        System.out.println("\tALOAD v1 SPILLEDARG "+  T.curr_method.spillMap.get(inst1));
                        _ret = (R)(inst0 + " v1 " +T.curr_method.regMap.get(inst2) + " ");
                    }

                    else{
                        //both are spilt :(
                        System.out.println("\tALOAD v0 SPILLEDARG " + T.curr_method.spillMap.get(inst1));
                        System.out.println("\tALOAD v1 SPILLEDARG " + T.curr_method.spillMap.get(inst2));
                        _ret = (R)(inst0 + " v0 v1 ");
                    }
                }
                else{
                    //Integer literal
                    System.out.println("\tALOAD v1 SPILLEDARG "+  T.curr_method.spillMap.get(inst1));
                    _ret = (R)(inst0 + " v1 " +inst2 + " ");

                }
            }
        }
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
    public R visit(Operator n, A argu) {
        R _ret=null;
        _ret = n.f0.accept(this, argu);
        return _ret;
    }

    /**
    * f0 -> Temp()
    *       | IntegerLiteral()
    *       | Label()
    */
    public R visit(SimpleExp n, A argu) {
        R _ret=null;
        _ret = n.f0.accept(this, argu);
        if(n.f0.which == 0){
            //save temp as a use
            if(argu.toString().equals("1")){T.curr_method.curr_block.addUse((String)_ret);}
        }
        return _ret;
    }

    /**
    * f0 -> "TEMP"
    * f1 -> IntegerLiteral()
    */
    public R visit(Temp n, A argu) {
        R _ret=null;
        String tmp = "";
        String val = "";
        
        tmp = (String)n.f0.accept(this, argu);
        val = (String)n.f1.accept(this, argu);
        //This temp was used somwehere
        _ret = (R)(tmp+val);
        if(argu.toString().equals("1")){
            if(!T.curr_method.allTemps.contains(tmp+val)){
                T.curr_method.allTemps.add(tmp+val);
            } 
        }
        return _ret;
    }

    /**
    * f0 -> <INTEGER_LITERAL>
    */
    public R visit(IntegerLiteral n, A argu) {
        R _ret=null;
        _ret = n.f0.accept(this, argu);
        return _ret;
    }

    /**
    * f0 -> <IDENTIFIER>
    */
    public R visit(Label n, A argu) {
        R _ret=null;
        _ret = n.f0.accept(this, argu);
        return _ret;
    }

}
