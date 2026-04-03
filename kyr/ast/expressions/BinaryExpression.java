package kyr.ast.expressions;

import kyr.ast.Type;
import kyr.exceptions.SemanticError;

public class BinaryExpression extends Expression {
    private Expression left;
    private String operator;
    private Expression right;

    private static int cpt = 0;


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

        sb.append(right.toMIPS());

        sb.append("""
        subi $sp, $sp, 4
        sw $v0, 0($sp)
    """);

        sb.append(left.toMIPS());

        sb.append("""
        lw $v1, 0($sp)
        addi $sp, $sp, 4
    """);


        switch (operator) {

            case "+":
                sb.append("add $v0, $v0, $v1\n");
                break;

            case "-":
                sb.append("sub $v0, $v0, $v1\n");
                break;

            case "*":
                sb.append("mul $v0, $v0, $v1\n");
                break;

            case "/": {
                int id = cpt++;
                sb.append(String.format("""
                        beq $v1, $zero, div_by_zero_%d
                        div $v0, $v1
                        mflo $v0
                        j div_end_%d
                div_by_zero_%d:
                        li $v0, 0
                div_end_%d:
                    """, id, id, id, id));
                                break;
            }

            case "%": {
                int id = cpt++;
                                sb.append(String.format("""
                        beq $v1, $zero, mod_by_zero_%d
                        div $v0, $v1
                        mfhi $v0
                        j mod_end_%d
                mod_by_zero_%d:
                        li $v0, 0
                mod_end_%d:
                    """, id, id, id, id));
                                break;
            }


            case "<":
                sb.append("slt $v0, $v0, $v1\n");
                break;

            case ">":
                sb.append("sgt $v0, $v0, $v1\n");
                break;

            case "<=":
                sb.append("""
                sgt $v0, $v0, $v1
                xori $v0, $v0, 1
            """);
                break;

            case ">=":
                sb.append("""
                slt $v0, $v0, $v1
                xori $v0, $v0, 1
            """);
                break;

            case "==":
                sb.append("""
                xor $v0, $v0, $v1
                sltiu $v0, $v0, 1
            """);
                break;

            case "!=":
                sb.append("""
                xor $v0, $v0, $v1
                sne $v0, $v0, $zero
            """);
                break;

            case "et":   // AND
                sb.append("and $v0, $v0, $v1\n");
                break;

            case "ou":   // OR
                sb.append("or $v0, $v0, $v1\n");
                break;
        }

        return sb.toString();
    }

    @Override
    public String toString() {
        return left.toString() + " " + operator + " " + right.toString();
    }

}