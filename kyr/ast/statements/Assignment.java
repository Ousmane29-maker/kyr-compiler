package kyr.ast.statements;

import kyr.ast.declarations.VariableDeclaration;
import kyr.ast.expressions.Expression;
import kyr.ast.expressions.VariableReference;
import kyr.exceptions.SemanticError;
import kyr.symtable.SymbolTable;

public class Assignment extends Statement{
    private Expression exp;
    private String variableName ;
    public Assignment(String variableName, Expression e, int n) {
        super(n);
        this.variableName = variableName ;
        this.exp = e;
    }

    @Override
    public void analyzeSemantics() throws SemanticError {
        VariableDeclaration var = SymbolTable.getInstance().find(variableName);
        exp.analyzeSemantics();
        if (!var.getType().equals(exp.getType())) {
            throw new SemanticError("Incompatible type - " + variableName +
                    " (" + var.getType() + ") cannot be assigned " +
                    exp.getType());
        }
    }

    @Override
    public String toMIPS() {
        VariableDeclaration var = SymbolTable.getInstance().find(variableName);
        StringBuilder sb = new StringBuilder();
        sb.append(exp.toMIPS());
        sb.append(String.format("""
                    sw $v0, %d($fp)            # %s = %s
                """, var.getOffset(), variableName, exp.toString()));
        return sb.toString();
    }
}
