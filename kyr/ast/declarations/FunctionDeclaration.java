package kyr.ast.declarations;

import kyr.ast.Sequence;
import kyr.ast.Type;
import kyr.ast.ASTNode;
import kyr.exceptions.SemanticError;
import kyr.symtable.SymbolTable;
import java.util.List;
import java.util.ArrayList;

/**
 * Represents a function declaration in the AST.
 * Handles semantic analysis of the function body and MIPS code generation (prologue/epilogue).
 */
public class FunctionDeclaration extends ASTNode {
    private final Type returnType;
    private final String name;
    private final Declaration parameters;
    private final Declaration localVariables;
    private final Sequence body;

    public FunctionDeclaration(Type returnType, String name, Declaration parameters, Declaration localVariables, Sequence body, int n) {
        super(n);
        this.returnType = returnType;
        this.name = name;
        this.parameters = parameters;
        this.localVariables = localVariables;
        this.body = body;
    }

    public String getName() {
        return name;
    }

    public Type getReturnType() {
        return returnType;
    }

    public Type getType() {
        return returnType;
    }

    public Declaration getParameters() {
        return parameters;
    }

    public List<VariableDeclaration> getParametersList() {
        if (parameters == null) {
            return new ArrayList<>();
        }
        return parameters.getVars();
    }

    public Declaration getLocalVariables() {
        return localVariables;
    }

    public int getArity() {
        return parameters == null ? 0 : parameters.size();
    }

    /**
     * Performs semantic analysis on the function's internal content.
     */
    public void analyzeSemanticsBodyOnly() throws SemanticError {
        SymbolTable st = SymbolTable.getInstance();

        st.enterFunction(this);

        // Parameters are already added by enterFunction()
        // We only need to add local variables to the scope
        if (localVariables != null) {
            localVariables.analyzeSemantics();
        }

        if (body != null) {
            body.analyzeSemantics();
        }

        // Check if a return statement was encountered within the function
        boolean hasReturn = st.exitFunction();

        if (!hasReturn) {
            throw new SemanticError("Line " + lineNumber +
                    " : Function `" + name + "` must contain a return statement.");
        }
    }

    @Override
    public void analyzeSemantics() throws SemanticError {
        analyzeSemanticsBodyOnly();
    }

    @Override
    public String toMIPS() {
        String label = getLabel();
        int frameSize = getFrameSize();
        StringBuilder code = new StringBuilder();

        code.append(String.format("""
            %s:
                # --- Prologue ---
                sw $fp, -4($sp)           # Save old FP at -4
                sw $ra, -8($sp)           # Save RA at -8
                move $fp, $sp             # Set new FP
                subi $sp, $sp, %d         # Allocate the new stack frame
            """, label, frameSize + 8)); // +8 to include space for $ra and $fp

        List<VariableDeclaration> params = getParametersList();
        int numArgs = params.size();

        // 1. Save the first 4 parameters ($a0-$a3) into the local frame
        for (int i = 0; i < Math.min(numArgs, 4); i++) {
            VariableDeclaration param = params.get(i);
            code.append(String.format(
                    "    sw $a%d, %d($fp)    # Local storage for param %s\n",
                    i, param.getOffset(), param.getName()));
        }

        // 2. Handle extra parameters (index 4 and above) passed via stack
        for (int i = 4; i < numArgs; i++) {
            VariableDeclaration param = params.get(i);
            // Calculate the positive offset relative to FP (the caller's SP)
            int callerStackOffset = (numArgs - 1 - i) * 4;
            code.append(String.format("""
                lw $t0, %d($fp)           # Load extra arg index %d from caller's stack
                sw $t0, %d($fp)           # Copy it into the local frame (%s)
            """, callerStackOffset, i, param.getOffset(), param.getName()));
        }

        if (body != null) {
            code.append("\n    # --- Function Body ---\n");
            code.append(body.toMIPS());
        }

        return code.toString();
    }

    public String getLabel() {
        return "func_" + name + "_" + getArity();
    }

    public int getNumberLine() {
        return lineNumber;
    }

    /**
     * Calculates the stack frame size in bytes.
     * Total = 8 (for $fp and $ra) + 4 * num_parameters + 4 * num_local_variables
     */
    public int getFrameSize() {
        int numParams = getArity();
        int numLocals = localVariables == null ? 0 : localVariables.size();
        // 8 = space for $fp + $ra
        return 8 + (numParams + numLocals) * 4;
    }
}