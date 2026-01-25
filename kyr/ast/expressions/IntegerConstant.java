package kyr.ast.expressions;

import kyr.ast.Type;

public class IntegerConstant extends Constant {
    public IntegerConstant(String text, int n) {
        super(text, n);
    }

    @Override
    public void analyzeSemantics() {

    }

    @Override
    public String toMIPS() {
        return String.format("""
                    li $v0, %s
                """, cst);
    }

    @Override
    public Type getType() {
        return Type.INTEGER;
    }
}
