package compiler488.semantics;

import compiler488.ast.ASTList;
import compiler488.ast.decl.ArrayDeclaration;
import compiler488.ast.decl.Declaration;
import compiler488.ast.decl.ParameterDeclaration;
import compiler488.ast.decl.RoutineDeclaration;
import compiler488.ast.decl.VariableDeclaration;
import compiler488.ast.expn.ArrayAccess;
import compiler488.ast.expn.BinaryOperator;
import compiler488.ast.expn.Expression;
import compiler488.ast.expn.FunctionInvocation;
import compiler488.ast.expn.UnaryMinus;
import compiler488.ast.expn.UnaryNot;
import compiler488.ast.expn.VariableAccess;
import compiler488.ast.stmt.Assignment;
import compiler488.ast.stmt.ExitStatement;
import compiler488.ast.stmt.GetInvocation;
import compiler488.ast.stmt.IfStatement;
import compiler488.ast.stmt.PutInvocation;
import compiler488.ast.stmt.ResultStatement;
import compiler488.ast.stmt.Statement;
import compiler488.ast.type.BooleanType;
import compiler488.ast.type.IntegerType;
import compiler488.ast.type.Type;
import compiler488.symbol.SymbolEntry;

public class Semantics_Jack extends SemanticASTVisitor
{
	@Override
	protected void initialize()
	{
		// Any global state for your checks can be initialized here!
		// You can already access the AST at this point using getAST()...
	}

	private void reportBinaryOperandError(String side, BinaryOperator expr)
	{
		Type opType = expr.getOperandType();
		String errcode = "S31";
		
		if ( opType == null )
		{
			beginError()
			.print("[ERROR (S32)]: type of both operands must be the same!")
			.newLine()
			.visualizeASTNode(expr)
			.submit();
			return;
		}
		else if ( opType instanceof BooleanType )
		{
			errcode = "S30";
		}
		
		beginError()
		.print("[ERROR ("+errcode+")]: type of "+side+" operand must be "+
				opType.toStringWithArticle()+"!")
		.newLine()
		.visualizeASTNode(expr)
		.submit();
	}

	private void reportConditionError(Statement stmt)
	{
		beginError()
		.print("[ERROR (S30)]: type of condition must be a boolean!")
		.newLine()
		.visualizeASTNode(stmt)
		.submit();
	}
	
	@Override
	public void visit(IfStatement expr) {
		if (!(expr.getCondition().getType() instanceof BooleanType))
		{
			reportConditionError(expr);
		}
		
		super.visit(expr);
	}
	
	@Override
	public void visit(GetInvocation expr) {
		int i = 1;
		for ( VariableAccess var : expr.getInputs() )
		{
			if (!(var.getType() instanceof IntegerType))
			{
				beginError()
				.print("[ERROR (S31)]: argument "+i+" of get invocation must be an integer!")
				.newLine()
				.visualizeASTNode(expr)
				.submit();
			}
			i++;
		}
		super.visit(expr);
	}

	@Override
	public void visit(PutInvocation expr) {
		int i = 1;
		for ( Expression oexp : expr.getOutputs() )
		{
			if (oexp.getType() instanceof BooleanType)
			{
				beginError()
				.print("[ERROR (S31)]: argument "+i+" of put invocation cannot be a boolean!")
				.newLine()
				.visualizeASTNode(expr)
				.submit();
			}
			i++;
		}
		// TODO Auto-generated method stub
		super.visit(expr);
	}

	@Override
	public void visit(ExitStatement expr) {
		Expression when = expr.getWhen();
		
		if ( when != null && !(when.getType() instanceof BooleanType))
		{
			reportConditionError(expr);
		}
		
		super.visit(expr);
	}

	@Override
	public void visit(ArrayAccess expr) {
		Expression index = expr.getIndex();
		
		if ( !(expr.getDeclaration() instanceof ArrayDeclaration) )
		{
			beginError()
			.print("[ERROR (S38)]: "+expr.getName().getIdent()+" is " +
					"not an array variable!")
			.newLine()
			.visualizeASTNode(expr)
			.submit();
		}
		else if (index == null || !(index.getType() instanceof IntegerType))
		{
			beginError()
			.print("[ERROR (S31)]: type of array index must be an integer!")
			.newLine()
			.visualizeASTNode(expr)
			.submit();
		}
		
		super.visit(expr);
	}
	
	// TODO: parameters can only be scalar?
	@Override
	public void visit(VariableAccess expr) {
		Declaration decl = expr.getDeclaration();
		
		if ( ( decl instanceof ArrayDeclaration && 
				!(expr instanceof ArrayAccess) )
				|| ( !(decl instanceof VariableDeclaration) && 
						!(decl instanceof ParameterDeclaration) ) )
		{
			beginError()
			.print("[ERROR (S37)]: "+expr.getName().getIdent()+" is " +
					"not a scalar variable!")
			.newLine()
			.visualizeASTNode(expr)
			.submit();
		}
		
		super.visit(expr);
	}

	@Override
	public void visit(BinaryOperator expr) {
		// TODO Auto-generated method stub
		Type left = expr.getLeft().getType();
		Type right = expr.getRight().getType();
		Type opType = expr.getOperandType();
		
		//System.out.println(left + " " + right);
		if (opType == null)
		{
			if ( !Type.equals(left, right) )
			{
				reportBinaryOperandError(null, expr);
			}
		}
		else if (left == null || !Type.equals(left, opType))
		{
			//System.out.println(expr.getLeft().getClass() + " " + expr.getLeft());
			reportBinaryOperandError("left", expr);
		}
		else if (right == null || !Type.equals(right, opType))
		{
			//System.out.println( expr.getRight().getClass() + " " + expr.getRight());
			reportBinaryOperandError("right", expr);
		}
		
		super.visit(expr);
	}

	@Override
	public void visit(ResultStatement expr) {
		RoutineDeclaration routine = expr.getParentRoutine();
		Type rettype = null;
		Expression retval = null;
		
		if ( routine == null )
		{
			// S51 deals with this
			return;
		}
		else
		{
			rettype = routine.getReturnType();
			retval = expr.getValue();
		}
			
		if ( rettype == null )
		{
			beginError()
			.print("[ERROR (S35)]: cannot use result within a procedure (did you mean return?)")
			.newLine()
			.visualizeASTNode(expr)
			.submit();
		}
		else if ( !Type.equals(rettype, retval.getType()) )
		{		
			beginError()
			.print("[ERROR (S35)]: type of result value must be "+
					rettype.toStringWithArticle()+"!")
			.newLine()
			.visualizeASTNode(expr)
			.submit();
		}
		
		super.visit(expr);
	}

	@Override
	public void visit(FunctionInvocation expr) {
		String funcName = expr.getName().getIdent();
		SymbolEntry entry = expr.resolveSymbol(funcName);
		RoutineDeclaration decl;
		ASTList<Expression> args = expr.getArguments();
		ASTList<ParameterDeclaration> params;
		
		if ( entry != null )
		{
			if ( entry.getDeclaration() instanceof RoutineDeclaration )
			{
				decl = (RoutineDeclaration) entry.getDeclaration();
				params = decl.getParameters();
				
				if ( args.size() != params.size() )
				{
					beginError()
					.print("[ERROR (S43)]: number of arguments different " +
							"from number of parameters declared for routine '"
							+funcName+"'!")
					.newLine()
					.visualizeASTNode(expr)
					.submit();
				}
				else
				{
					for ( int i = 0; i < args.size(); i++ )
					{
						Expression argi = args.at(i);
						ParameterDeclaration parami = params.at(i);
						
						if ( !Type.equals(argi.getType(), parami.getType()))
						{
							//System.out.println(i+": "+argi.getType());
							beginError()
							.print("[ERROR (S36)]: cannot pass "+argi.getType()
									+" argument to " + parami.getType() +
									" parameter '" + parami.getName().getIdent()
									+ "' for routine '"+funcName+"'!")
							.newLine()
							.visualizeASTNode(expr)
							.submit();
						}
					}
				}
			}
			else
			{
				// technically S40/S41 here
			}
		}
		else
		{
			beginError()
			.print("[ERROR (S36)]: invoking undeclared routine '"+funcName+"'!")
			.newLine()
			.visualizeASTNode(expr)
			.submit();
		}
		
		super.visit(expr);
	}

	@Override
	public void visit(Assignment expr) {
		Expression src = expr.getSource();
		Expression dst = expr.getDest();
		
		//System.out.println(src.getType() + " " + dst.getType());
		if ( !Type.equals(src.getType(), dst.getType()) )
		{
			beginError()
			.print("[ERROR (S34)]: cannot assign "+src.getType()+" value to a "
					+dst.getType()+" variable!")
			.newLine()
			.visualizeASTNode(expr)
			.submit();
		}
		
		super.visit(expr);
	}

	@Override
	public void visit(UnaryMinus expr) {
		if (!(expr.getOperand().getType() instanceof IntegerType))
		{
			beginError()
			.print("[ERROR (S31)]: type of unary operand must be an integer!")
			.newLine()
			.visualizeASTNode(expr)
			.submit();
		}
		super.visit(expr);
	}

	@Override
	public void visit(UnaryNot expr) {
		if (!(expr.getOperand().getType() instanceof BooleanType))
		{
			beginError()
			.print("[ERROR (S30)]: type of unary operand must be a boolean!")
			.newLine()
			.visualizeASTNode(expr)
			.submit();
		}
		super.visit(expr);
	}
}
