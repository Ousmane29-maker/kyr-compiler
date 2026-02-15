package kyr.ast.expressions;

import kyr.ast.Type;
import kyr.exceptions.SemanticError;

public class UnaryExpression extends Expression {
    private String operator;
    private Expression operand;

    public UnaryExpression(String operator, Expression operand, int line) {
        super(line);
        this.operator = operator;
        this.operand = operand;
    }

    @Override
    public Type getType() {
        if (operator.equals("non")) {
            return Type.BOOLEAN;
        } else if (operator.equals("-")) {
            return Type.INTEGER;
        }
        throw new SemanticError("Line "+lineNumber+ " : Unknown unary operator: " + operator);
    }

    @Override
    public void analyzeSemantics() {
        operand.analyzeSemantics();

        if (operator.equals("non") && !operand.getType().equals(Type.BOOLEAN)) {
            throw new SemanticError("Line "+lineNumber+ " : `non` requires boolean operand");
        } else if (operator.equals("-") && !operand.getType().equals(Type.INTEGER)) {
            throw new SemanticError("Line "+lineNumber+ " : Unary `-` requires integer operand");
        }
    }

    @Override
    public String toMIPS() {
        // We use XORI with 1 to flip only the last bit.
        // This ensures that 0 becomes 1 and 1 becomes 0
        StringBuilder sb = new StringBuilder();
        sb.append(operand.toMIPS());  // result in $v0

        if (operator.equals("non")) {
            sb.append("    xori $v0, $v0, 1         # logical NOT : flip boolean (0<->1)\n");
        } else if (operator.equals("-")) {
            sb.append("    neg $v0, $v0         # negate: $v0 = -$v0\n");
        }

        return sb.toString();
    }

    @Override
    public String toString() {
        return operator + " " + operand.toString();
    }

}