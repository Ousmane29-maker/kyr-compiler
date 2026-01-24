package kyr.ast.declarations;

import kyr.OffsetManager;
import kyr.ast.Type;

public class VariableDeclaration extends Declaration {
    private Type type;
    private String name;
    private int offset;
    public VariableDeclaration(Type type, String name, int n) {
        super(n);
        this.type = type;
        this.name = name ;
        this.offset = OffsetManager.getInstance().allocate();

    }


    public String getName() {
        return name;
    }

    public String toMIPS_Address() {
        return null ; // Ex: " li $v0, 56-> sw 0($fp)"
    }
}
