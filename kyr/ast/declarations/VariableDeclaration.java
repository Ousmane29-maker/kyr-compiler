package kyr.ast.declarations;

import kyr.ast.Type;
import kyr.ast.ASTNode;
import kyr.symtable.SymbolTable;
import kyr.exceptions.SemanticError;

/**
 * Represents a variable declaration in the AST.
 * The memory offset is assigned during the semantic analysis phase via the SymbolTable.
 */
public class VariableDeclaration extends ASTNode {
    private Type type;
    private String name;
    private int offset;
    private boolean isGlobal;

    public VariableDeclaration(Type type, String name, int n) {
        super(n);
        this.type = type;
        this.name = name;
        // The offset will be calculated during analyzeSemantics() by the SymbolTable
        this.offset = 0;
    }

    public Type getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getNumberLine() {
        return lineNumber;
    }

    @Override
    public void analyzeSemantics() throws SemanticError {
        SymbolTable st = SymbolTable.getInstance();
        // Determine and save if the variable is global based on the current scope context
        this.isGlobal = !st.isInFunction();
        st.add(this);
    }

    public boolean isGlobal() {
        return isGlobal;
    }

    @Override
    public String toMIPS() {
        // Space allocation is handled by the function prologue or by the main entry point
        return "";
    }

}