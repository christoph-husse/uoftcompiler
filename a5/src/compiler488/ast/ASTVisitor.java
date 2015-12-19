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

public interface ASTVisitor
{
	public void visit(ASTNode expr);

	public void visit(AndOperator expr);

	public void visit(ArrayAccess expr);

	public void visit(BinaryArithmeticOperator expr);

	public void visit(BinaryBooleanOperator expr);

	public void visit(BinaryOperator expr);

	public void visit(BoolConst expr);

	public void visit(ComparisonOperator expr);

	public void visit(Const expr);

	public void visit(DivisionOperator expr);

	public void visit(EqualOperator expr);

	public void visit(Expression expr);

	public void visit(FunctionInvocation expr);

	public void visit(GreaterEqualOperator expr);

	public void visit(GreaterOperator expr);

	public void visit(Identifier expr);

	public void visit(IntConst expr);

	public void visit(LessEqualOperator expr);

	public void visit(LessOperator expr);

	public void visit(MinusOperator expr);

	public void visit(MultiplicationOperator expr);

	public void visit(NotEqualOperator expr);

	public void visit(OrOperator expr);

	public void visit(PlusOperator expr);

	public void visit(StringConst expr);

	public void visit(UnaryMinus expr);

	public void visit(UnaryNot expr);

	public void visit(UnaryOperator expr);

	public void visit(VariableAccess expr);

	public void visit(Assignment expr);

	public void visit(ExitStatement expr);

	public void visit(GetInvocation expr);

	public void visit(IfStatement expr);

	public void visit(LoopStatement expr);

	public void visit(ProcedureInvocation expr);

	public void visit(PutInvocation expr);

	public void visit(ResultStatement expr);

	public void visit(ReturnStatement expr);

	public void visit(Scope expr);

	public void visit(Statement expr);

	public void visit(Declaration decl);

	public void visit(ArrayDeclaration arr);

	public void visit(ForwardDeclaration fwd);

	public void visit(ParameterDeclaration param);

	public void visit(RoutineDeclaration expr);

	public void visit(VariableDeclaration expr);

	public void visit(Type type);

	public <E extends ASTNode> void visit(ASTList<E> list);

	public void visit(BooleanType type);

	public void visit(IntegerType type);

}
