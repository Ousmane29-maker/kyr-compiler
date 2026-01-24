package kyr.ast.expressions;

public class BooleanConstant extends Constant{
    public BooleanConstant(String text, int n) {
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
