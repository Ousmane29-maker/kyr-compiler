package kyr.ast.expressions;

import kyr.ast.Type;
import kyr.ast.declarations.VariableDeclaration;
import kyr.symtable.SymbolTable;

public class VariableReference extends Expression{
    private String name;

    public VariableReference(String name, int n) {
        super(n);
        this.name = name;
    }

    @Override
    public void analyzeSemantics() {
        SymbolTable.getInstance().find(name); // throw exception if the variable isn't declared
    }

    @Override
    public String toMIPS() {
        VariableDeclaration var = SymbolTable.getInstance().find(name);
        return String.format("""
                    lw $v0, %d($fp)            # load %s
                """, var.getOffset(), name);
    }

    @Override
    public Type getType() {
        return SymbolTable.getInstance().find(name).getType();
    }

    @Override
    public String toString(){
        return name ;
    }
}
