package kyr.ast.declarations;

import kyr.ast.ASTNode;
import kyr.ast.declarations.VariableDeclaration;

import java.util.ArrayList;
import java.util.List;

public class Declaration extends ASTNode {
    List<VariableDeclaration> vars = new ArrayList<>();

    public Declaration(int n) {
        super(n);
    }

    public void add(VariableDeclaration v){
        vars.add(v);
    }
    @Override
    public void analyzeSemantics() {

    }

    @Override
    public String toMIPS() {
       return "";
    }

}
