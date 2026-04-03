package kyr.ast.declarations;

import kyr.ast.ASTNode;
import kyr.symtable.SymbolTable;

import java.util.ArrayList;
import java.util.List;

public class Functions extends ASTNode {
    private final List<FunctionDeclaration> functions = new ArrayList<>();

    public Functions(int n) {
        super(n);
    }

    public void add(FunctionDeclaration f) {
        functions.add(f);
    }

    public List<FunctionDeclaration> getAll() {
        return functions;
    }

    @Override
    public void analyzeSemantics() throws kyr.exceptions.SemanticError {

        // PASS 1: register all functions (allow forward calls)
        for (FunctionDeclaration f : functions) {
            SymbolTable.getInstance().addFunction(f);
        }

        // PASS 2: analyze bodies
        for (FunctionDeclaration f : functions) {
            f.analyzeSemanticsBodyOnly();
        }
    }

    @Override
    public String toMIPS() {
        StringBuilder sb = new StringBuilder();
        for (FunctionDeclaration f : functions) {
            sb.append(f.toMIPS());
        }
        return sb.toString();
    }
}
