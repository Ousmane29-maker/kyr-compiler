package kyr.ast.statements;

import kyr.LabelFactory;
import kyr.ast.Sequence;
import kyr.ast.expressions.Expression;
import kyr.ast.Type;
import kyr.exceptions.SemanticError;

public class If extends Statement {
    private final Expression condition;
    private final Sequence thenBlock;
    private final Sequence elseBlock;

    // Constructor for  IF THEN ELSE
    public If(Expression condition, Sequence thenBlock, Sequence elseBlock, int line) {
        super(line);
        this.condition = condition;
        this.thenBlock = thenBlock;
        this.elseBlock = elseBlock;
    }

    // Constructor for IF THEN
    public If(Expression condition, Sequence thenBlock, int line) {
        this(condition, thenBlock, null, line);
    }

    @Override
    public void analyzeSemantics() {
        condition.analyzeSemantics();
        if (!condition.getType().equals(Type.BOOLEAN)) {
            throw new SemanticError("Line " + lineNumber + " : Condition must be boolean.");
        }
        thenBlock.analyzeSemantics();
        if (elseBlock != null) {
            elseBlock.analyzeSemantics();
        }
    }

    @Override
    public String toMIPS() {
        StringBuilder sb = new StringBuilder();
        String elseLabel = LabelFactory.newLabel();
        String endLabel =  LabelFactory.newLabel();

        sb.append("    # --- IF START  ---\n");

        sb.append(condition.toMIPS());

        //  Branch : if $v0 == 0 (false),  jump to th else block
        sb.append("    beqz $v0, ").append(elseLabel).append("    # if condition is false, jump to else\n");

        // THEN block (if true)
        sb.append("    # THEN block\n");
        sb.append(thenBlock.toMIPS());
        // jump
        sb.append("    j ").append(endLabel).append("             # skip else block\n");

        sb.append(elseLabel).append(":\n");
        if (elseBlock != null) {
            sb.append("    # ELSE block\n");
            sb.append(elseBlock.toMIPS());
        }

        // ENDIF label
        sb.append(endLabel).append(":\n");
        sb.append("    # --- IF END ---\n");

        return sb.toString();
    }
}