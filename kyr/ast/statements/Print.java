package kyr.ast.statements;

import kyr.LabelFactory;
import kyr.ast.expressions.*;

public class Print extends Statement {
    protected Expression exp;

    public Print(Expression e, int n) {
        super(n);
        exp = e;
    }

    @Override
    public void analyzeSemantics() {
        exp.analyzeSemantics();
    }

    @Override
    public String toMIPS() {
        if (BooleanConstant.class.isInstance(exp)){
            String label = LabelFactory.newLabel();
            return exp.toMIPS() + String.format("""
                        la $a0, faux
                        beqz $v0, %s
                        la $a0, vrai
                        %s:
                        li $v0, 4                 # set the syscall code for printing
                        syscall
                   """, label, label);
        }
        return exp.toMIPS() + String.format("""
                    move $a0, $v0
                    li $v0, %d                 # set the syscall code for printing
                    syscall
                """, StringConstant.class.isInstance(exp) ? 4 : 1);
    }
}
