package kyr.ast.expressions;

import kyr.ast.ASTNode;
import kyr.ast.Type;

public abstract class Expression extends ASTNode {
    protected Expression(int n) {
        super(n);
    }

    public abstract Type getType();

    @Override
    public String toString() {
        return "[expression]";  // default value hahahahh
    }

}
