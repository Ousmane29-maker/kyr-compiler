package kyr.ast.expressions;

import kyr.ast.Type;
import kyr.ast.declarations.VariableDeclaration;
import kyr.exceptions.SemanticError;
import kyr.symtable.SymbolTable;

/**
 * Represents a reference to a variable (using its name) within an expression.
 * Resolves the variable in the symbol table and handles MIPS code generation
 * for both global and local memory access.
 */
public class VariableReference extends Expression {
    private String name;
    private VariableDeclaration var;

    public VariableReference(String name, int n) {
        super(n);
        this.name = name;
    }

    @Override
    public void analyzeSemantics() throws SemanticError {
        // Search for the variable across scopes (from most local to most global)
        var = SymbolTable.getInstance().findVariable(name, lineNumber);
    }

    @Override
    public String toMIPS() {
        if (var.isGlobal()) {
            // GLOBAL: Use $s7 (the saved $fp from the main entry point)
            return String.format("    lw $v0, %d($s7)    # load %s (global)\n",
                    var.getOffset(), name);
        } else {
            // LOCAL: Use the current frame pointer $fp
            return String.format("    lw $v0, %d($fp)    # load %s (local)\n",
                    var.getOffset(), name);
        }
    }

    @Override
    public Type getType() {
        return var.getType();
    }

    @Override
    public String toString() {
        return name;
    }
}