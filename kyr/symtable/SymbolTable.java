package kyr.symtable;

import kyr.OffsetManager;
import kyr.ast.declarations.VariableDeclaration;
import kyr.ast.expressions.StringConstant;
import kyr.exceptions.SemanticError;

import java.util.ArrayList;
import java.util.HashMap;

public class SymbolTable {
    static final SymbolTable instance = new SymbolTable(); // singleton pattern
    protected ArrayList<StringConstant> strings = new ArrayList<StringConstant>();
    protected HashMap<String, VariableDeclaration> variables = new HashMap<>();

    private SymbolTable() {
    }

    public static SymbolTable getInstance() {
        return instance;
    }

    public void add(StringConstant c) {
        strings.add(c);
    }

    public void add(VariableDeclaration v) throws SemanticError {
        if(variables.containsKey(v.getName()))
            throw new SemanticError("Variable `"+v.getName()+"` already declared.");
        variables.put(v.getName(), v);
    }


    public String toMIPS_DataSegment() {
        StringBuilder sb = new StringBuilder("");
        for (StringConstant c : strings)
            sb.append(c.toMIPS_DataSegment());

        return sb.toString();
    }

    public int getTotalVariableSize() {
        return OffsetManager.getInstance().getCurrentOffset();
    }
    public String toMIPS_Allocation_Variables() {
        int totalSize = getTotalVariableSize();
        int nbVars = variables.size();
        if (totalSize == 0) {
            return "";  // No variables
        }
        return String.format("""
                    move $fp, $sp            # frame pointer
                    subi $sp, $sp, %d            # %d * 4 octets
                """, totalSize, nbVars) ;
    }
}

