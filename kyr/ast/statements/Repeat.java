package kyr.ast.statements;

import kyr.LabelFactory;
import kyr.ast.Sequence;
import kyr.ast.expressions.Expression;
import kyr.ast.Type;
import kyr.exceptions.SemanticError;
public class Repeat extends Statement {
    private final Sequence body;
    private final Expression condition;

    public Repeat(Sequence body, Expression condition, int line) {
        super(line);
        this.body = body;
        this.condition = condition;
    }

    @Override
    public void analyzeSemantics() {
        body.analyzeSemantics();
        condition.analyzeSemantics();
        if (!condition.getType().equals(Type.BOOLEAN)) {
            throw new SemanticError("Line " + lineNumber + " : Condition of `jusqua` must be boolean.");
        }
    }

    @Override
    public String toMIPS() {
        StringBuilder sb = new StringBuilder();
        String startLabel = LabelFactory.newLabel();

        sb.append("# --- Repeat Loop Start ---\n");
        sb.append(startLabel).append(":\n");

        //loop body
        sb.append(body.toMIPS());

        // condition evaluation
        sb.append(condition.toMIPS());

        // If $v0 == 0 (false), LOOP BACK to the top. damnnnnn chill !
        sb.append("    beqz $v0, ").append(startLabel).append("    # repeat if condition is false\n");

        sb.append("# --- Repeat Loop End ---\n");
        return sb.toString();
    }
}