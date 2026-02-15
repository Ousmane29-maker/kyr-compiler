package kyr.ast.expressions;

import kyr.ast.Type;
import kyr.exceptions.SemanticError;

public class BinaryExpression extends Expression {
    private Expression left;
    private String operator;
    private Expression right;

    public BinaryExpression(Expression left, String operator, Expression right, int line) {
        super(line);
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    @Override
    public Type getType() {
        if (operator.matches("[+\\-*/%]")) {
            return Type.INTEGER;
        }
        if (operator.matches("(<|<=|>|>=|==|!=|et|ou)")) {
            return Type.BOOLEAN;
        }
        throw new SemanticError("Line "+lineNumber+ " : Unknown operator: " + operator);
    }

    @Override
    public void analyzeSemantics() {
        left.analyzeSemantics();
        right.analyzeSemantics();

        if (operator.matches("[+\\-*/%]")) {
            if (!left.getType().equals(Type.INTEGER) || !right.getType().equals(Type.INTEGER)) {
                throw new SemanticError("Line "+lineNumber+ " : Arithmetic operator requires integer operands");
            }
        } else if (operator.matches("(<|<=|>|>=)")) {
            if (!left.getType().equals(Type.INTEGER) || !right.getType().equals(Type.INTEGER)) {
                throw new SemanticError("Line "+lineNumber+ " : Comparison operator requires integer operands");
            }
        } else if (operator.equals("==") || operator.equals("!=")) {
            //same type needed
            if (!left.getType().equals(right.getType())) {
                throw new SemanticError("Line "+lineNumber+ " : Equality operator requires same types");
            }
        } else if (operator.equals("et") || operator.equals("ou")) {
            if (!left.getType().equals(Type.BOOLEAN) || !right.getType().equals(Type.BOOLEAN)) {
                throw new SemanticError("Line "+lineNumber+ " : Logical operator requires boolean operands");
            }
        }
    }

    @Override
    public String toMIPS() {
        StringBuilder sb = new StringBuilder();

        // 1. Evaluate the RIGHT operand into $v0
        sb.append(right.toMIPS());

        // 2. Push the result of RIGHT onto the stack
        sb.append(String.format("    sw $v0, 0($sp)             # push right operand (%s)\n", right.toString()));
        sb.append("    subi $sp, $sp, 4\n");

        // 3. Evaluate the LEFT operand into $v0
        sb.append(left.toMIPS());

        // 4. Pop the RIGHT operand from the stack into $v1
        sb.append("    addi $sp, $sp, 4           # pop right operand into $v1\n");
        sb.append(String.format("    lw $v1, 0($sp)             # pop right operand (%s) into $v1\n", right.toString()));

        // 5. Do the operation: $v0 = (LEFT) op (RIGHT)
        // Now: $v0 contains LEFT, $v1 contains RIGHT
        String fullExpr = '('+this.toString()+')'; // "left op right"

        switch (operator) {
            // --- Arithmetic Operators ---
            case "+":
                sb.append(String.format("    add $v0, $v0, $v1          # %s\n", fullExpr));
                break;
            case "-":
                sb.append(String.format("    sub $v0, $v0, $v1          # %s\n", fullExpr));
                break;
            case "*":
                sb.append(String.format("    mul $v0, $v0, $v1          # %s\n", fullExpr));
                break;
            case "/":
                sb.append(String.format("    div $v0, $v1               # division: %s\n", fullExpr));
                sb.append("    mflo $v0                   # get quotient\n");
                break;
            case "%":
                sb.append(String.format("    div $v0, $v1               # modulo: %s\n", fullExpr));
                sb.append("    mfhi $v0                   # get remainder\n");
                break;

            // --- Relational Operators ---
            case "<":
                sb.append(String.format("    slt $v0, $v0, $v1          # check if %s\n", fullExpr));
                break;
            case "<=":
                sb.append(String.format("    sle $v0, $v0, $v1          # check if %s\n", fullExpr));
                break;
            case ">":
                sb.append(String.format("    sgt $v0, $v0, $v1          # check if %s\n", fullExpr));
                break;
            case ">=":
                sb.append(String.format("    sge $v0, $v0, $v1          # check if %s\n", fullExpr));
                break;
            case "==":
                sb.append(String.format("    seq $v0, $v0, $v1          # check if %s\n", fullExpr));
                break;
            case "!=":
                sb.append(String.format("    sne $v0, $v0, $v1          # check if %s\n", fullExpr));
                break;

            // --- Logical Operators ---
            case "et":
                sb.append(String.format("    and $v0, $v0, $v1          # logical: %s\n", fullExpr));
                break;
            case "ou":
                sb.append(String.format("    or $v0, $v0, $v1           # logical: %s\n", fullExpr));
                break;
        }

        return sb.toString();
    }

    @Override
    public String toString() {
        return left.toString() + " " + operator + " " + right.toString();
    }

}