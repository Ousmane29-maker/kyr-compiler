package kyr.ast.declarations;

import kyr.OffsetManager;
import kyr.ast.Type;

public class VariableDeclaration extends Declaration {
    private Type type;
    private String name;
    private int offset;
    boolean initialized = false;

    public VariableDeclaration(Type type, String name, int n) {
        super(n);
        this.type = type;
        this.name = name ;
        this.offset = OffsetManager.getInstance().allocate();

    }

    public Type getType() {
        return type;
    }

    public int getOffset() {
        return offset;
    }


    public String getName() {
        return name;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public int getNumberLine(){
        return  lineNumber ;
    }

}
