package kyr.ast.expressions;

import kyr.ast.Type;
import kyr.ast.declarations.FunctionDeclaration;
import kyr.exceptions.SemanticError;
import kyr.symtable.SymbolTable;

import java.util.List;

/**
 * Represents a function call expression in the AST.
 * Handles argument evaluation, MIPS argument passing (registers $a0-$a3 and stack),
 * and stack cleanup after the call.
 */
public class FunctionCall extends Expression {
    private final String name;
    private final List<Expression> arguments;
    private FunctionDeclaration fun;

    public FunctionCall(String name, List<Expression> arguments, int n) {
        super(n);
        this.name = name;
        this.arguments = arguments;
    }

    @Override
    public void analyzeSemantics() throws SemanticError {
        // Analyze all arguments passed to the function
        for (Expression e : arguments) {
            e.analyzeSemantics();
        }

        // Find the function with the matching name and arity (handling overloading)
        fun = SymbolTable.getInstance()
                .findFunction(name, arguments.size(), lineNumber);

        // Type checking for arguments could be added here if required by the language spec
    }

    @Override
    public String toMIPS() {
        StringBuilder code = new StringBuilder();
        int numArgs = arguments.size();

        code.append("    # Function call: " + name + "\n");

        // 1. Evaluate and push ALL arguments onto the stack
        for (int i = 0; i < numArgs; i++) {
            code.append(arguments.get(i).toMIPS());
            code.append("    subi $sp, $sp, 4\n");
            code.append("    sw $v0, 0($sp)\n");
        }

        // 2. Load the first 4 arguments into registers $a0-$a3
        int inReg = Math.min(numArgs, 4);
        for (int i = 0; i < inReg; i++) {
            // Calculate the offset to find the argument on the stack
            int offset = (numArgs - 1 - i) * 4;
            code.append(String.format("    lw $a%d, %d($sp)\n", i, offset));
        }

        // 3. Perform the jump and link to the function label
        code.append(String.format("    jal %s\n", fun.getLabel()));

        // 4. Clean up the ENTIRE stack occupied by arguments after returning
        if (numArgs > 0) {
            code.append(String.format("    addi $sp, $sp, %d    # Cleanup of %d args\n",
                    numArgs * 4, numArgs));
        }

        return code.toString();
    }

    @Override
    public Type getType() {
        return fun.getReturnType();
    }

    @Override
    public String toString() {
        return name + "(" + arguments.size() + " args)";
    }
}