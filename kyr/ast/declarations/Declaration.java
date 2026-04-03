package kyr.ast.declarations;

import kyr.ast.ASTNode;
import kyr.exceptions.SemanticError;

import java.util.ArrayList;
import java.util.List;

public class Declaration extends ASTNode {
    private List<VariableDeclaration> vars = new ArrayList<>();

    public Declaration(int n) {
        super(n);
    }

    public void add(VariableDeclaration v){
        vars.add(v);
    }

    public int size() {
        return vars.size();
    }

    public List<VariableDeclaration> getVars() {
        return vars;
    }

    @Override
    public void analyzeSemantics() throws SemanticError {
        for (VariableDeclaration v : vars) {
            v.analyzeSemantics();
        }
    }

    @Override
    public String toMIPS() {
        StringBuilder sb = new StringBuilder();
        for (VariableDeclaration v : vars) {
            sb.append(v.toMIPS());
        }
        return sb.toString();
    }
}