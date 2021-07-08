package main;

import analyser.StaticAnalyser;
import es.*;
import ptg.ObjectNode;
import ptg.PointsToGraph;
import resolver.SummaryResolver;
import resolver.ReworkedResolver;
import soot.PackManager;
import soot.Scene;
import soot.options.Options;
import soot.tagkit.BytecodeOffsetTag;
import soot.SootMethod;
import soot.Transform;
import soot.jimple.*;
import soot.jimple.internal.*;
import soot.*;
import utils.GetListOfNoEscapeObjects;
import utils.Stats;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.*;
import java.io.*;

import static utils.KillCallerOnly.kill;


public class Main {

	public static void main(String[] args) {

		GetSootArgs g = new GetSootArgs();
		String[] sootArgs = g.get(args);
		if (sootArgs == null) {
			System.out.println("Unable to generate args for soot!");
			return;
		}
		StaticAnalyser staticAnalyser = new StaticAnalyser();
		CHATransform prepass = new CHATransform();
		PackManager.v().getPack("wjap").add(new Transform("wjap.pre", prepass));
		PackManager.v().getPack("jtp").add(new Transform("jtp.sample", staticAnalyser));
		long analysis_start = System.currentTimeMillis();
		Options.v().parse(sootArgs);
		Scene.v().loadNecessaryClasses();

		// SootClass s = Scene.v().getSootClass("spec.jbb.JBBmain");
		// System.err.println(s.getMethods());
		// System.out.println("Application Classes: "+Scene.v().getApplicationClasses());
		//SootClass s = Scene.v().getSootClass("spec.jbb.JBBmain");
		//System.err.println(s.getMethods());
		// System.out.println("Application Classes: "+Scene.v().getApplicationClasses());

		PackManager.v().runPacks();
		// soot.Main.main(sootArgs);
		long analysis_end = System.currentTimeMillis();
		System.out.println("Static Analysis is done!");
		System.out.println("Time Taken:"+(analysis_end-analysis_start)/1000F);

		
		boolean useNewResolver = true;
		long res_start = System.currentTimeMillis();
		// printSummary(staticAnalyser.summaries);
		// printCFG();

		// sr.resolve(staticAnalyser.summaries, staticAnalyser.ptgs);
		// long res_end = System.currentTimeMillis();
		// System.out.println("Resolution is done");
		// System.out.println("Time Taken:"+(res_end-res_start)/1000F);
		// HashMap<SootMethod, HashMap<ObjectNode, EscapeStatus>> resolved = kill(sr.solvedSummaries);
		// printAllInfo(StaticAnalyser.ptgs, resolved, args[4]);
		// saveStats(sr.existingSummaries, resolved, args[4]);

		// printResForJVM(sr.solvedSummaries, args[2], args[4]);

		// Resolver sr;
		if(useNewResolver) {
			ReworkedResolver sr = new ReworkedResolver(staticAnalyser.summaries,
											staticAnalyser.ptgs,
											staticAnalyser.noBCIMethods);
			long res_end = System.currentTimeMillis();
			System.out.println("Resolution is done");
			System.out.println("Time Taken in phase 1:"+(analysis_end-analysis_start)/1000F);
			System.out.println("Time Taken in phase 2:"+(res_end-res_start)/1000F);
	
			// System.out.println(staticAnalyser.summaries.size()+ " "+staticAnalyser.ptgs.size());
			
			
			
			HashMap<SootMethod, HashMap<ObjectNode, EscapeStatus>> resolved = (HashMap) kill(sr.solvedSummaries);
			//printAllInfo(StaticAnalyser.ptgs, staticAnalyser.summaries, args[4]);
			boolean debug = false;
			for(SootClass cl: Scene.v().getClasses())
			{
				debug = cl.getName().equals("A$B$C");
				for(SootMethod method: cl.getMethods())
				{
					if(!method.hasActiveBody() || !StaticAnalyser.ptgs.containsKey(method))
						continue;//unanalysed methods.
					if(debug)
					{
						System.out.println(method.getActiveBody().toString());
					}
					PointsToGraph ptg = StaticAnalyser.ptgs.get(method);
					JimpleBody body = (JimpleBody) method.getActiveBody();
					for(Unit u: body.getUnits())
					{
						Stmt s1 = (Stmt) u;
						if(s1 instanceof JAssignStmt)
						{
							JAssignStmt s = (JAssignStmt)s1;
							Value lhs = s.getLeftOp();
							Value rhs = s.getRightOp();
							if(lhs instanceof JInstanceFieldRef)
							{
								BytecodeOffsetTag tg = (BytecodeOffsetTag)s.getTag("BytecodeOffsetTag");
								int offset = -1;
								if(tg != null)
									offset = tg.getBytecodeOffset();//putfield opcode
								// Value v = ((JInstanceFieldRef)lhs).getBase();
								// if(v instanceof Para)
								Local l = (Local)((JInstanceFieldRef)lhs).getBase();
								Set<Integer> pt_set = new HashSet<Integer>();
								if(ptg.vars.containsKey(l))
								{
									for(ObjectNode o: ptg.vars.get(l))
									{
										pt_set.add(o.ref);
									}
									if(ptg.escape_map.containsKey(offset))
									{
										Set<Integer> existing_set = ptg.escape_map.get(offset);
										pt_set.addAll(existing_set);
									}
									ptg.escape_map.put(offset,pt_set);
								}
							}
							else if(rhs instanceof JInstanceFieldRef)
							{
								int offset = -1;
								int mini = 2000000000;
								ArrayList<Integer> bci_list = new ArrayList<Integer>();
								for (ValueBox ub: rhs.getUseBoxes()) 
								{
										// if (!ub.getClass().toString().equals("class soot.jimple.internal.JAssignStmt$LinkedRValueBox") )
										// continue;
									//if(debug)
										//System.out.printf("Box %s got here!\n",ub.getClass().toString());
									BytecodeOffsetTag tg = (BytecodeOffsetTag) ub.getTag("BytecodeOffsetTag");
									if (tg != null){
										offset = tg.getBytecodeOffset();
										mini = Math.min(mini,offset);
										bci_list.add(offset);
										if(debug)
											System.out.printf("I have %d\n",offset);
									}
								}
								Local l = (Local)((JInstanceFieldRef)rhs).getBase();
								Set<Integer> pt_set = new HashSet<Integer>();
								if(debug)
								{
									System.out.printf("Getfield offset is %d\n",mini);
									System.out.printf("Local is %s,%s\n,",l.toString(),l.getName());
								}

								if(ptg.vars.containsKey(l))
								{
									for(ObjectNode o: ptg.vars.get(l))
									{
										pt_set.add(o.ref);
									}
									if(ptg.escape_map.containsKey(mini))
									{
										Set<Integer> existing_set = ptg.escape_map.get(mini);
										pt_set.addAll(existing_set);
									}
									ptg.escape_map.put(mini,pt_set);
								}
							}
							else if(rhs instanceof InvokeExpr)
							{
								InvokeExpr ie = (InvokeExpr)rhs;
								Set<Integer> es_set = new HashSet<Integer>();
								//boolean constructor_call = false;
								if(ie instanceof JSpecialInvokeExpr)
								{
									JSpecialInvokeExpr se = (JSpecialInvokeExpr)ie;
									List<Value> arguments = se.getArgs();
									Value v = se.getBase();
									es_set.addAll(ptg.get_reachable(v));
									arguments.forEach(v1 -> es_set.addAll(ptg.get_reachable(v1)));
								}
								else if(ie instanceof JVirtualInvokeExpr)
								{
									JVirtualInvokeExpr ve = (JVirtualInvokeExpr)ie;
									Value v = ve.getBase();
									es_set.addAll(ptg.get_reachable(v));
									List<Value> arguments = ve.getArgs();
									arguments.forEach(v1 -> es_set.addAll(ptg.get_reachable(v1)));
								}
								else if(ie instanceof JInterfaceInvokeExpr)
								{
									JInterfaceInvokeExpr ine =(JInterfaceInvokeExpr)ie;
									Value v = ine.getBase();
									es_set.addAll(ptg.get_reachable(v));
									List<Value> arguments = ine.getArgs();
									arguments.forEach(v1 -> es_set.addAll(ptg.get_reachable(v1)));
								}
								else if(ie instanceof JStaticInvokeExpr)
								{
									StaticInvokeExpr sie = (StaticInvokeExpr)ie;
									List<Value> arguments = sie.getArgs();
									arguments.forEach(v -> es_set.addAll(ptg.get_reachable(v)));
								}
								int offset = -1;
								for(ValueBox v: s.getUseAndDefBoxes())
								{
									if(debug)
										System.out.printf("ValueBox has type %s\n",v.getClass().toString());
									//if(!v.getClass().toString().equals("class soot.jimple.internal.JAssignStmt$LinkedRValueBox"))
										//continue;//filter out the InvokeExpr. Testing this is key.									
									BytecodeOffsetTag tg = (BytecodeOffsetTag)v.getTag("BytecodeOffsetTag");
									if(tg != null)
									{
										offset = tg.getBytecodeOffset();
										if(debug)
											System.out.printf("Found offset %d\n",offset);
										//break;
									}
								}
								if(debug)
									System.out.printf("Offset for InvokeExpr is %d\n",offset);
								if(ptg.function_map.containsKey(offset))
								{
									Set<Integer> existing_set = ptg.function_map.get(offset);
									es_set.addAll(existing_set);
								}
								ptg.function_map.put(offset,es_set);//keep these seperate!
							}
						}
						else if(s1 instanceof JInvokeStmt)
						{
							//void function call.
							InvokeStmt i = (InvokeStmt)s1;
							InvokeExpr ie = i.getInvokeExpr();
							Set<Integer> es_set = new HashSet<Integer>();
							boolean is_constructor_call = false;
							if(ie instanceof JSpecialInvokeExpr)
							{
								JSpecialInvokeExpr se = (JSpecialInvokeExpr)ie;
								String m_name = transformFuncSignature(se.getMethod().getBytecodeSignature());
								//System.out.printf("Method invoked is %s\n",m_name);
								Value v = se.getBase();
								is_constructor_call = (m_name.indexOf("<init>") != -1);
								es_set.addAll(ptg.get_reachable(v));
								List<Value> arguments = se.getArgs();
								arguments.forEach(v1 -> es_set.addAll(ptg.get_reachable(v1)));
							}
							else if(ie instanceof JVirtualInvokeExpr)
							{
								JVirtualInvokeExpr ve = (JVirtualInvokeExpr)ie;
								Value v = ve.getBase();
								es_set.addAll(ptg.get_reachable(v));
								List<Value> arguments = ve.getArgs();
								arguments.forEach(v1 -> es_set.addAll(ptg.get_reachable(v1)));
							}
							else if(ie instanceof JInterfaceInvokeExpr)
							{
								JInterfaceInvokeExpr ine =(JInterfaceInvokeExpr)ie;
								Value v = ine.getBase();
								es_set.addAll(ptg.get_reachable(v));
								List<Value> arguments = ine.getArgs();
								arguments.forEach(v1 -> es_set.addAll(ptg.get_reachable(v1)));
							}
							else if(ie instanceof JStaticInvokeExpr)
							{
								StaticInvokeExpr sie = (StaticInvokeExpr)ie;
								List<Value> arguments = sie.getArgs();
								arguments.forEach(v -> es_set.addAll(ptg.get_reachable(v)));
							}
							int offset = -1;//default value: do not add this.
							BytecodeOffsetTag tg = (BytecodeOffsetTag)i.getTag("BytecodeOffsetTag");
							if(tg != null)
								offset = tg.getBytecodeOffset();
							if(is_constructor_call)
							{
								Value v = ((SpecialInvokeExpr)ie).getBase();
								if(v instanceof Local)
								{
									Set<ObjectNode> c_set = ptg.vars.get((Local)v);
									if(c_set != null && c_set.size() == 1)
									{
										//System.out.println("Constructor call detected with one candidate!\n");
										ptg.constructor_map.put(offset,((ObjectNode)c_set.toArray()[0]).ref);
									}
								}
							}
							ptg.function_map.put(offset,es_set);
						}
					}
					//if(debug)
						//System.out.printf("Vars PTG is %s\n",ptg.vars.toString());
				}
			}
			printAllInfo(StaticAnalyser.ptgs, resolved, args[4]);
	
			saveStats(sr.existingSummaries, resolved, args[4], staticAnalyser.ptgs);
	
			printResForJVM(sr.solvedSummaries, args[2], args[4]);
		}
		else {
			SummaryResolver sr = new SummaryResolver();
			sr.resolve(staticAnalyser.summaries, staticAnalyser.ptgs);
			long res_end = System.currentTimeMillis();
			System.out.println("Resolution is done");
			System.out.println("Time Taken:"+(res_end-res_start)/1000F);
	
			// System.out.println(staticAnalyser.summaries.size()+ " "+staticAnalyser.ptgs.size());
			
			
			HashMap<SootMethod, HashMap<ObjectNode, EscapeStatus>> resolved = (HashMap) kill(sr.solvedSummaries);
			//printAllInfo(StaticAnalyser.ptgs, staticAnalyser.summaries, args[4]);
			
			printAllInfo(StaticAnalyser.ptgs, resolved, args[4]);
	
			saveStats(sr.existingSummaries, resolved, args[4], staticAnalyser.ptgs);
	
			printResForJVM(sr.solvedSummaries, args[2], args[4]);
		}
	}

	static void printCFG() {
		try {
			FileWriter f = new FileWriter("cfg1.txt");
			f.write(Scene.v().getCallGraph().toString());
			f.write(CHATransform.getCHA().toString());
			f.close();
		}
		catch( Exception e) {
			System.err.println(e);
		}
	}

	static void printSummary(Map<SootMethod, HashMap<ObjectNode, EscapeStatus>> existingSummaries) {
		try {
            FileWriter f = new FileWriter("sum1.txt");
			// f.write(existingSummaries.toString());
			for (SootMethod sm: existingSummaries.keySet()) {
				HashMap<ObjectNode, EscapeStatus> hm = existingSummaries.get(sm);
				int hash = 0;
				List<ObjectNode> lobj = new ArrayList<>(hm.keySet());
				Collections.sort(lobj, new Comparator<ObjectNode>(){
					public int compare(ObjectNode a, ObjectNode b)
						{
							return a.toString().compareTo(b.toString());
						}
				});
				f.write(sm.toString()+": ");
				for (ObjectNode obj: lobj)
				{
					EscapeStatus es = hm.get(obj);
					List<EscapeState> les = new ArrayList<>(es.status);
					Collections.sort(les,  new Comparator<EscapeState>(){
						public int compare(EscapeState a, EscapeState b)
							{
								return a.toString().compareTo(b.toString());
							}
					});
					f.write(les+" ");
					// hash ^= es.status.size();
					// if (es instanceof ConditionalValue)
				}
				f.write("\n");
				
			}
            f.close();
        }
        catch(Exception e) {
            System.err.println(e);
        }
    }

	private static void printAllInfo(Map<SootMethod, PointsToGraph> ptgs,
									 Map<SootMethod, HashMap<ObjectNode, EscapeStatus>> summaries, String opDir) {

		Path p_opDir = Paths.get(opDir);
		TreeMap<String,Integer> method_counter = new TreeMap<String,Integer>(new Comparator<String>(){
			@Override
			public int compare(String a,String b)
			{
				int x = a.compareTo(b);
				if(x < 0)
					return -1;
				else if(x > 0)
					return 1;
				else
					return 0; 
			}
		});
		for(Map.Entry<SootMethod,PointsToGraph> entry: ptgs.entrySet())
		{
			String s = entry.getKey().getDeclaringClass().toString();
			if(!method_counter.containsKey(s))
				method_counter.put(s,1);
			else
			{
				int x = method_counter.get(s);
				method_counter.put(s,x+1);
			}
		}
		for(Map.Entry<String,Integer> entry: method_counter.entrySet())
		{
			Path p_opFile = Paths.get(p_opDir.toString() + "/" + entry.getKey() + ".info");
			try {
				Files.write(p_opFile, String.format("%d\n",entry.getValue()).getBytes(StandardCharsets.UTF_8),
						Files.exists(p_opFile) ? StandardOpenOption.APPEND : StandardOpenOption.CREATE);
			} catch (Exception e) {
				// TODO: handle exception
				System.out.println("Unable to write info of " + entry.getKey() + " to file " + p_opFile.toString());
				e.printStackTrace();
			}
		}
		for (Map.Entry<SootMethod, PointsToGraph> entry : ptgs.entrySet()) {
			SootMethod method = entry.getKey();
			PointsToGraph ptg = entry.getValue();
			Path p_opFile = Paths.get(p_opDir.toString() + "/" + method.getDeclaringClass().toString() + ".info");
//			System.out.println("Method "+method.toString()+" appends to "+p_opFile);
			StringBuilder output = new StringBuilder();
			output.append(String.format("%s\n",transformFuncSignature(method.getBytecodeSignature())));
			// for(Type t: method.getParameterTypes())
			// {
			// 	output.append(String.format("%s\n",t.toString()));
			// }
			output.append("PTG:\n");
			output.append(ptg.toString());
			HashMap<ObjectNode,EscapeStatus> m = summaries.get(method);
			String summary_res = "";
			int sum_c = 0;
			for(Map.Entry<ObjectNode,EscapeStatus> e: m.entrySet())
			{
				if(!e.getValue().doesEscape())
				{
					sum_c++;
					summary_res += String.format("%d ",e.getKey().ref);
				}
			}
			output.append(String.format("\nSummary: %d\n%s\n",sum_c,summary_res));
			//output.append(summaries.get(method).toString() + "\n");
			try {
				Files.write(p_opFile, output.toString().getBytes(StandardCharsets.UTF_8),
						Files.exists(p_opFile) ? StandardOpenOption.APPEND : StandardOpenOption.CREATE);
			} catch (Exception e) {
				// TODO: handle exception
				System.out.println("Unable to write info of " + method.toString() + " to file " + p_opFile.toString());
				e.printStackTrace();
			}
		}
	}
	static String transformFuncSignature(String inputString) {
		StringBuilder finalString = new StringBuilder();
		for(int i=1;i<inputString.length()-1;i++) {
			if(inputString.charAt(i) == '.')
				finalString.append('/');
			else if(inputString.charAt(i) == ':')
				finalString.append('.');
			else if(inputString.charAt(i) == ' ')
				continue;
			else finalString.append(inputString.charAt(i));
		}
		return finalString.toString();
	}
	static void printResForJVM(Map<SootMethod, HashMap<ObjectNode, EscapeStatus>> summaries, String ipDir, String opDir) {
		// Open File
		Path p_ipDir = Paths.get(ipDir);
		Path p_opDir = Paths.get(opDir);

		Path p_opFile = Paths.get(p_opDir.toString() + "/" + p_ipDir.getFileName() + ".res");

		StringBuilder sb = new StringBuilder();
		for (Map.Entry<SootMethod, HashMap<ObjectNode, EscapeStatus>> entry : summaries.entrySet()) {
			SootMethod method = entry.getKey();
			HashMap<ObjectNode, EscapeStatus> summary = entry.getValue();
			sb.append(transformFuncSignature(method.getBytecodeSignature()));
			sb.append(" ");
			sb.append(GetListOfNoEscapeObjects.get(summary));
			sb.append("\n");
		}
		try {
			System.out.println("Trying to write to:" + p_opFile);
			Files.write(p_opFile, sb.toString().getBytes(StandardCharsets.UTF_8),
					Files.exists(p_opFile) ? StandardOpenOption.APPEND : StandardOpenOption.CREATE);
			System.out.println("Results have been written.");
		} catch (IOException e) {
			System.out.println("There is an exception"+e);
			e.printStackTrace();
		}
	}

	static void saveStats(Map<SootMethod, HashMap<ObjectNode, EscapeStatus>> unresolved,
						  Map<SootMethod, HashMap<ObjectNode, EscapeStatus>>resolved,
						  String opDir,
						  Map<SootMethod, PointsToGraph> ptg) {
		Stats beforeResolution = new Stats(unresolved, ptg);
		System.out.println("calculating stats for solvedsummaries");
		Stats afterResolution = new Stats(resolved, null);
		Path p_opFile = Paths.get(opDir + "/stats.txt");
		StringBuilder sb = new StringBuilder();
		sb.append("Before resolution:\n"+beforeResolution);
		sb.append("\nAfter resolution:\n"+afterResolution);
		sb.append("\n");
		try {
			System.out.println("Trying to write to:" + p_opFile);
			Files.write(p_opFile, sb.toString().getBytes(StandardCharsets.UTF_8),
					Files.exists(p_opFile) ? StandardOpenOption.APPEND : StandardOpenOption.CREATE);
			System.out.println("Stats have been written.");
		} catch (IOException e) {
			System.out.println("There is an exception"+e);
			e.printStackTrace();
		}

	}

}
