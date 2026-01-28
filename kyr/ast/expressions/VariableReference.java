package kyr.ast.expressions;

import kyr.ast.Type;
import kyr.ast.declarations.VariableDeclaration;
import kyr.exceptions.SemanticError;
import kyr.symtable.SymbolTable;

public class VariableReference extends Expression{
    private String name;
    VariableDeclaration var ;

    public VariableReference(String name, int n) {
        super(n);
        this.name = name;
        var = SymbolTable.getInstance().find(name, n); // throw exception if the variable isn't declared
    }

    @Override
    public void analyzeSemantics() {
        if (!var.isInitialized()) {
            throw new SemanticError("Line "+lineNumber+ " : Variable `" + name + "` used before initialization");
        }

    }

    @Override
    public String toMIPS() {
        return String.format("""
                    lw $v0, %d($fp)            # load %s
                """, var.getOffset(), name);
    }

    @Override
    public Type getType() {
        return var.getType();
    }

    @Override
    public String toString(){
        return name ;
    }
}
