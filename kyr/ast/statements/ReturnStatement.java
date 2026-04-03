package kyr.ast.statements;

import kyr.ast.Type;
import kyr.ast.declarations.FunctionDeclaration;
import kyr.ast.expressions.Expression;
import kyr.exceptions.SemanticError;
import kyr.symtable.SymbolTable;

/**
 * Represents a 'return' statement in the AST.
 * Verifies that the return occurs within a function context, checks type consistency,
 * and generates the MIPS epilogue to restore the stack and return control to the caller.
 */
public class ReturnStatement extends Statement {
    private Expression expression;
    private FunctionDeclaration function;

    public ReturnStatement(Expression expression, int n) {
        super(n);
        this.expression = expression;
    }

    @Override
    public void analyzeSemantics() throws SemanticError {
        SymbolTable st = SymbolTable.getInstance();

        // Ensure the statement is located inside a function
        if (!st.isInFunction()) {
            throw new SemanticError("Line " + lineNumber +
                    " : 'return' statement can only be used inside a function.");
        }

        // SAVE the reference to the current function context
        this.function = st.getCurrentFunction();

        expression.analyzeSemantics();

        // Verify that the expression type matches the function's declared return type
        Type expectedType = st.getCurrentFunctionType();
        Type actualType = expression.getType();

        if (!expectedType.equals(actualType)) {
            throw new SemanticError("Line " + lineNumber +
                    " : Return type mismatch. Expected " + expectedType +
                    " but got " + actualType);
        }

        // Notify the SymbolTable that a return statement was encountered
        st.setReturnSeen();
    }

    @Override
    public String toMIPS() {
        StringBuilder sb = new StringBuilder();

        // 1. Evaluate the return expression (result stored in $v0)
        sb.append(expression.toMIPS());

        if (function == null) {
            throw new RuntimeException("Internal error: function context not set during semantic analysis");
        }

        // 2. MIPS Epilogue: Restore the caller's environment
        sb.append("""
            # --- Epilogue ---
            lw $ra, -8($fp)           # Restore $ra (before changing $fp/$sp)
            lw $t0, -4($fp)           # Read old $fp into temporary register $t0
            move $sp, $fp             # Restore $sp to the base of the current frame
            move $fp, $t0             # Restore the caller's $fp
            jr $ra                    # Return to caller
        """);

        return sb.toString();
    }
}