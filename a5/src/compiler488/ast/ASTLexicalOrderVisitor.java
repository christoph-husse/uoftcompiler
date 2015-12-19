package compiler488.ast;

public class ASTLexicalOrderVisitor extends ASTVisitorAdapter
{
	private IASTNodeCallback nodeCallback;

	public ASTLexicalOrderVisitor()
	{
	}

	public ASTLexicalOrderVisitor(IASTNodeCallback nodeCallback)
	{
		setCallback(nodeCallback);
	}

	public void setCallback(IASTNodeCallback nodeCallback)
	{
		this.nodeCallback = nodeCallback;
	}

	@Override
	public void visit(ASTNode node)
	{
		for (ASTNode child : node.getChildren())
		{
			if (nodeCallback != null)
				nodeCallback.onVisit(node);

			child.accept(this);
		}
	}
}
