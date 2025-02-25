package handlers;

import es.ConditionalValue;
import es.EscapeStatus;
import ptg.ObjectNode;
import ptg.ObjectType;
import ptg.PointsToGraph;
import soot.Local;
import soot.PrimType;
import soot.Unit;
import soot.Value;
import soot.SootMethod;
import soot.jimple.ParameterRef;
import soot.jimple.ThisRef;
import soot.jimple.internal.JCaughtExceptionRef;
import soot.jimple.internal.JIdentityStmt;

import java.util.Map;

public class JIdentityStmtHandler {
	public static void handle(SootMethod m, Unit u, PointsToGraph ptg, Map<ObjectNode, EscapeStatus> summary) {
		JIdentityStmt stmt = (JIdentityStmt) u;
		Value lhs = stmt.getLeftOp();
		if (lhs.getType() instanceof PrimType) return;
		Value rhs = stmt.getRightOp();
		ObjectNode obj = null, cvobj = null;
		if (rhs instanceof ParameterRef) {
			/*
			 * ParameterRef example:
			 * r0 := @parameter0: java.lang.String[]
			 */
			int val = -1*(((ParameterRef)rhs).getIndex()+2);
			obj = new ObjectNode(val, ObjectType.parameter);
			ptg.forcePutVar((Local) lhs, obj);
			cvobj = new ObjectNode(val, ObjectType.argument);
		} else if (rhs instanceof ThisRef) {
			/*
			 * ThisRef example:
			 * r0 := @this: Test
			 */
			// -1 will be used to represent 'this'
			obj = new ObjectNode(-1, ObjectType.parameter);
			ptg.forcePutVar((Local) lhs, obj);
			cvobj = new ObjectNode(-1, ObjectType.argument);
		} else if (rhs instanceof JCaughtExceptionRef && lhs instanceof Local) {
			// System.err.println("[JIdentitiyStmtHandler] Warning: caughtexception is assigned to "+lhs.toString());
			obj = new ObjectNode(-1, ObjectType.external);
			ptg.forcePutVar((Local) lhs, obj);
			EscapeStatus e = new EscapeStatus();
			e.setEscape();
			summary.put(obj, e);
		} else {
			try {
				ThisRef r = (ThisRef) rhs;
			} catch (Exception e) {
				String ex = "[JIdentitiyStmtHandler] Unidentified " + u.toString();
				//System.out.println(ex);
				throw e;
			}
		}
		if (cvobj != null) {
			ConditionalValue cv = new ConditionalValue(null, cvobj, true);
			summary.put(obj, new EscapeStatus(cv));
		}
	}
}
