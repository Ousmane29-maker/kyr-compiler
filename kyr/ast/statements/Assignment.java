package kyr.ast.statements;

import kyr.ast.declarations.VariableDeclaration;
import kyr.ast.expressions.Expression;
import kyr.exceptions.SemanticError;
import kyr.symtable.SymbolTable;

/**
 * Represents an assignment statement in the AST.
 * Handles type checking and MIPS code generation for storing expression results
 * into global or local memory locations.
 */
public class Assignment extends Statement {
    private Expression exp;
    private String variableName;
    private VariableDeclaration var;

    public Assignment(String variableName, Expression e, int n) {
        super(n);
        this.exp = e;
        this.variableName = variableName;
        this.var = null;
    }

    @Override
    public void analyzeSemantics() throws SemanticError {
        // Resolve the variable in the symbol table
        var = SymbolTable.getInstance().findVariable(variableName, lineNumber);

        // Analyze the right-hand side expression
        exp.analyzeSemantics();

        // Verify that the variable type matches the expression type
        if (!var.getType().equals(exp.getType())) {
            throw new SemanticError("Line " + lineNumber +
                    " : Incompatible type - Variable `" + variableName +
                    "` (" + var.getType() + ") cannot be assigned " +
                    exp.getType());
        }
    }

    @Override
    public String toMIPS() {
        StringBuilder sb = new StringBuilder();

        // 1. Evaluate the expression (result will be in $v0)
        sb.append(exp.toMIPS());

        // 2. Store the result into the variable's memory address
        if (var.isGlobal()) {
            // GLOBAL: Use $s7 (the main entry point's frame pointer)
            sb.append(String.format("    sw $v0, %d($s7)    # %s = ... (global)\n",
                    var.getOffset(), variableName));
        } else {
            // LOCAL: Use the current frame pointer $fp
            sb.append(String.format("    sw $v0, %d($fp)    # %s = ... (local)\n",
                    var.getOffset(), variableName));
        }

        return sb.toString();
    }
}