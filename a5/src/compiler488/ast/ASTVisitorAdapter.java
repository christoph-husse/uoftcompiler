package compiler488.ast;

import compiler488.ast.decl.ArrayDeclaration;
import compiler488.ast.decl.Declaration;
import compiler488.ast.decl.ForwardDeclaration;
import compiler488.ast.decl.ParameterDeclaration;
import compiler488.ast.decl.RoutineDeclaration;
import compiler488.ast.decl.VariableDeclaration;
import compiler488.ast.expn.AndOperator;
import compiler488.ast.expn.ArrayAccess;
import compiler488.ast.expn.BinaryArithmeticOperator;
import compiler488.ast.expn.BinaryBooleanOperator;
import compiler488.ast.expn.BinaryOperator;
import compiler488.ast.expn.BoolConst;
import compiler488.ast.expn.ComparisonOperator;
import compiler488.ast.expn.Const;
import compiler488.ast.expn.DivisionOperator;
import compiler488.ast.expn.EqualOperator;
import compiler488.ast.expn.Expression;
import compiler488.ast.expn.FunctionInvocation;
import compiler488.ast.expn.GreaterEqualOperator;
import compiler488.ast.expn.GreaterOperator;
import compiler488.ast.expn.Identifier;
import compiler488.ast.expn.IntConst;
import compiler488.ast.expn.LessEqualOperator;
import compiler488.ast.expn.LessOperator;
import compiler488.ast.expn.MinusOperator;
import compiler488.ast.expn.MultiplicationOperator;
import compiler488.ast.expn.NotEqualOperator;
import compiler488.ast.expn.OrOperator;
import compiler488.ast.expn.PlusOperator;
import compiler488.ast.expn.StringConst;
import compiler488.ast.expn.UnaryMinus;
import compiler488.ast.expn.UnaryNot;
import compiler488.ast.expn.UnaryOperator;
import compiler488.ast.expn.VariableAccess;
import compiler488.ast.stmt.Assignment;
import compiler488.ast.stmt.ExitStatement;
import compiler488.ast.stmt.GetInvocation;
import compiler488.ast.stmt.IfStatement;
import compiler488.ast.stmt.LoopStatement;
import compiler488.ast.stmt.ProcedureInvocation;
import compiler488.ast.stmt.PutInvocation;
import compiler488.ast.stmt.ResultStatement;
import compiler488.ast.stmt.ReturnStatement;
import compiler488.ast.stmt.Scope;
import compiler488.ast.stmt.Statement;
import compiler488.ast.type.BooleanType;
import compiler488.ast.type.IntegerType;
import compiler488.ast.type.Type;

public class ASTVisitorAdapter implements ASTVisitor
{

	private boolean throwOnUnhandled = true;

	public ASTVisitorAdapter()
	{
	}

	public ASTVisitorAdapter(boolean throwOnUnhandled)
	{
		this.throwOnUnhandled = throwOnUnhandled;
	}

	public void visit(AndOperator expr)
	{
		visit((BinaryBooleanOperator) expr);
	}

	public void visit(ArrayAccess expr)
	{
		visit((VariableAccess) expr);
	}

	public void visit(Assignment expr)
	{
		visit((Statement) expr);
	}

	public void visit(ASTNode expr)
	{
		if (throwOnUnhandled)
			throw new RuntimeException(
				"Unhandled node "+expr.getClass().getSimpleName() +
				" (id "+expr.getId()+")");
	}

	public void visit(BinaryArithmeticOperator expr)
	{
		visit((BinaryOperator) expr);
	}

	public void visit(BinaryBooleanOperator expr)
	{
		visit((BinaryOperator) expr);
	}

	public void visit(BinaryOperator expr)
	{
		visit((Expression) expr);
	}

	public void visit(BoolConst expr)
	{
		visit((Const) expr);
	}

	public void visit(ComparisonOperator expr)
	{
		visit((BinaryOperator) expr);
	}

	public void visit(Const expr)
	{
		visit((Expression) expr);
	}

	public void visit(DivisionOperator expr)
	{
		visit((BinaryArithmeticOperator) expr);
	}

	public void visit(EqualOperator expr)
	{
		visit((ComparisonOperator) expr);
	}

	public void visit(ExitStatement expr)
	{
		visit((Statement) expr);
	}

	public void visit(Expression expr)
	{
		visit((ASTNode) expr);
	}

	public void visit(FunctionInvocation expr)
	{
		visit((Expression) expr);
	}

	public void visit(GetInvocation expr)
	{
		visit((Statement) expr);
	}

	public void visit(GreaterEqualOperator expr)
	{
		visit((ComparisonOperator) expr);
	}

	public void visit(GreaterOperator expr)
	{
		visit((ComparisonOperator) expr);
	}

	public void visit(Identifier expr)
	{
		visit((Expression) expr);
	}

	public void visit(IfStatement expr)
	{
		visit((Statement) expr);
	}

	public void visit(IntConst expr)
	{
		visit((Const) expr);
	}

	public void visit(LessEqualOperator expr)
	{
		visit((ComparisonOperator) expr);
	}

	public void visit(LessOperator expr)
	{
		visit((ComparisonOperator) expr);
	}

	public void visit(LoopStatement expr)
	{
		visit((Statement) expr);
	}

	public void visit(MinusOperator expr)
	{
		visit((BinaryArithmeticOperator) expr);
	}

	public void visit(MultiplicationOperator expr)
	{
		visit((BinaryArithmeticOperator) expr);
	}

	public void visit(NotEqualOperator expr)
	{
		visit((ComparisonOperator) expr);
	}

	public void visit(OrOperator expr)
	{
		visit((BinaryBooleanOperator) expr);
	}

	public void visit(PlusOperator expr)
	{
		visit((BinaryArithmeticOperator) expr);
	}

	public void visit(ProcedureInvocation expr)
	{
		visit((Statement) expr);
	}

	public void visit(PutInvocation expr)
	{
		visit((Statement) expr);
	}

	public void visit(ResultStatement expr)
	{
		visit((Statement) expr);
	}

	public void visit(ReturnStatement expr)
	{
		visit((Statement) expr);
	}

	public void visit(Scope expr)
	{
		visit((Statement) expr);
	}

	public void visit(Statement expr)
	{
		visit((ASTNode) expr);
	}

	public void visit(StringConst expr)
	{
		visit((Const) expr);
	}

	public void visit(UnaryMinus expr)
	{
		visit((UnaryOperator) expr);
	}

	public void visit(UnaryNot expr)
	{
		visit((UnaryOperator) expr);
	}

	public void visit(UnaryOperator expr)
	{
		visit((Expression) expr);
	}

	public void visit(VariableAccess expr)
	{
		visit((Expression) expr);
	}

	public void visit(Declaration decl)
	{
		visit((ASTNode) decl);
	}

	public void visit(ArrayDeclaration arr)
	{
		visit((Declaration) arr);
	}

	public void visit(ForwardDeclaration fwd)
	{
		visit((Declaration) fwd);
	}

	public void visit(ParameterDeclaration param)
	{
		visit((Declaration) param);
	}

	public void visit(RoutineDeclaration routine)
	{
		visit((Declaration) routine);
	}

	public void visit(VariableDeclaration var)
	{
		visit((Declaration) var);
	}

	public void visit(Type type)
	{
		visit((ASTNode) type);
	}

	public <E extends ASTNode> void visit(ASTList<E> list)
	{
		visit((ASTNode) list);
	}

	public void visit(BooleanType type)
	{
		visit((Type) type);
	}

	public void visit(IntegerType type)
	{
		visit((Type) type);
	}
}
