package kyr.ast.expressions;

import kyr.LabelFactory;

public class BoolConstant extends Constant{
    public BoolConstant(String text, int n) {
        super(text, n);
    }
    @Override
    public void analyzeSemantics() {
    }

    @Override
    public String toMIPS() {
        return String.format("""
                    li $v0, %d
                """, cst.equals("vrai") ? 1 : 0);
    }
}
