package kyr.ast;

import kyr.ast.declarations.Declaration;
import kyr.ast.declarations.Functions;
import kyr.symtable.SymbolTable;

public class Program extends ASTNode {
    protected Sequence sequence;
    protected Declaration declaration;

    protected Functions functions;

    public Program(Declaration d, Sequence s) {
        super(-1);
        sequence = s;
        declaration = d;
    }

    public Program(Sequence s) {
        super(-1);
        sequence = s;
    }

    public Program(Declaration d, Functions f, Sequence s) {
        super(-1);
        declaration = d;
        functions = f;
        sequence = s;
    }


    @Override
    public void analyzeSemantics() {
        if (declaration != null) {
            declaration.analyzeSemantics();
        }
        if (functions != null) {
            functions.analyzeSemantics();
        }
        if (sequence != null) {
            sequence.analyzeSemantics();
        };
    }

    @Override
    public String toMIPS() {
        StringBuilder sb = new StringBuilder("");
        sb.append("""
                .data
                    vrai: .asciiz "vrai"
                    faux: .asciiz "faux"
                 """);
        sb.append(SymbolTable.getInstance().toMIPS_DataSegment());
        sb.append("""
                .text
                main:
                """);
        sb.append(SymbolTable.getInstance().toMIPS_Allocation_Variables());
        sb.append(sequence.toMIPS());
        sb.append("""
                end:
                    li $v0, 10                # terminate execution
                    syscall
                """);

        if (functions != null) {
            sb.append("\n# --- functions ---\n");
            sb.append(functions.toMIPS());
        }
        return sb.toString();
    }
}
