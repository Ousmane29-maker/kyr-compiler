package kyr.symtable;

import java.util.*;
import kyr.ast.declarations.*;
import kyr.ast.expressions.StringConstant;
import kyr.exceptions.SemanticError;
import kyr.ast.Type;

/**
 * SymbolTable manages variable and function scopes using a stack-based approach.
 * It handles nested scopes, function contexts, and MIPS offset calculation.
 */
public class SymbolTable {

    private static SymbolTable instance = null;

    public static SymbolTable getInstance() {
        if (instance == null) instance = new SymbolTable();
        return instance;
    }

    protected ArrayList<StringConstant> strings = new ArrayList<StringConstant>();

    // Stacks to manage nested scopes
    private Stack<HashMap<String, VariableDeclaration>> variablesStack = new Stack<>();
    private Stack<HashMap<String, FunctionDeclaration>> functionsStack = new Stack<>();
    private Stack<Type> returnTypeStack = new Stack<>();
    private Stack<Boolean> hasReturnStack = new Stack<>();

    private FunctionDeclaration currentFunction = null;

    private SymbolTable() {
        // Create global scope on initialization
        push();
    }

    /* SCOPE MANAGEMENT (push/pop) */

    /**
     * Enters a new block (creates a new scope).
     * Called during 'debut' or when entering a function.
     */
    public void push() {
        variablesStack.push(new HashMap<>());
        functionsStack.push(new HashMap<>());
        returnTypeStack.push(null);
        hasReturnStack.push(false);
    }

    /**
     * Exits a block (destroys the current scope).
     * Called during 'fin' or when exiting a function.
     */
    public void pop() {
        if (variablesStack.size() > 1) {  // Always keep at least the global scope
            variablesStack.pop();
            functionsStack.pop();
            returnTypeStack.pop();
            hasReturnStack.pop();
        }
    }

    /* FUNCTIONS */

    private String functionKey(String name, int arity) {
        return name + "#" + arity;
    }

    /**
     * Adds a function to the CURRENT scope (top of the stack).
     */
    public void addFunction(FunctionDeclaration f) throws SemanticError {
        String key = functionKey(f.getName(), f.getArity());

        HashMap<String, FunctionDeclaration> currentFunctions = functionsStack.peek();

        if (currentFunctions.containsKey(key)) {
            throw new SemanticError("Line " + f.getNumberLine() +
                    " : Function `" + f.getName() +
                    "` with " + f.getArity() + " parameter(s) already declared.");
        }
        currentFunctions.put(key, f);
    }

    /**
     * Searches for a function from the top of the stack down to the global scope.
     */
    public FunctionDeclaration findFunction(String name, int arity, int lineNumber)
            throws SemanticError {

        String key = functionKey(name, arity);

        // Search from top (local) to bottom (global)
        for (int i = functionsStack.size() - 1; i >= 0; i--) {
            HashMap<String, FunctionDeclaration> scope = functionsStack.get(i);
            if (scope.containsKey(key)) {
                return scope.get(key);
            }
        }

        throw new SemanticError("Line " + lineNumber +
                " : Undeclared function `" + name +
                "` with " + arity + " parameter(s).");
    }

    /* VARIABLES */

    /**
     * Adds a variable to the CURRENT scope and calculates its MIPS stack offset.
     */
    public void add(VariableDeclaration var) throws SemanticError {
        HashMap<String, VariableDeclaration> currentVariables = variablesStack.peek();

        if (currentVariables.containsKey(var.getName())) {
            throw new SemanticError("Line " + var.getNumberLine() +
                    " : Variable `" + var.getName() + "` already declared in this scope.");
        }

        int offset;

        if (variablesStack.size() == 1) {
            // GLOBAL SCOPE: Decrement by 4 starting from -4
            offset =  -4 - currentVariables.size() * 4;
        } else {
            // LOCAL SCOPE (function)
            // Stack frame layout:
            // sw $ra, -8($sp)  <- Offset -8
            // sw $fp, -4($sp)  <- Offset -4
            // The first available local variable must start at -12
            offset = -12 - currentVariables.size() * 4;
        }

        var.setOffset(offset);
        currentVariables.put(var.getName(), var);
    }

    /**
     * Searches for a variable from the current scope up to the global scope
     * (from most local to most global).
     */
    public VariableDeclaration findVariable(String name, int lineNumber)
            throws SemanticError {

        for (int i = variablesStack.size() - 1; i >= 0; i--) {
            HashMap<String, VariableDeclaration> scope = variablesStack.get(i);
            if (scope.containsKey(name)) {
                return scope.get(name);
            }
        }

        throw new SemanticError("Line " + lineNumber +
                " : Undeclared variable `" + name + "`.");
    }

    /**
     * Checks if a variable exists specifically in the CURRENT scope.
     */
    public boolean existsInCurrentScope(String name) {
        return variablesStack.peek().containsKey(name);
    }

    /* FUNCTION CONTEXT */

    /**
     * Enters a function context:
     * - push() a new scope
     * - Registers the return type
     * - Adds parameters as local variables
     */
    public void enterFunction(FunctionDeclaration f) throws SemanticError {
        push();  // New scope for the function

        this.currentFunction = f;

        // Register return type in the stack
        returnTypeStack.pop();  // Remove the null added by push()
        returnTypeStack.push(f.getType());

        hasReturnStack.pop();
        hasReturnStack.push(false);

        // Add parameters as local variables
        for (VariableDeclaration param : f.getParametersList()) {
            add(param);
        }
    }

    public FunctionDeclaration getCurrentFunction() {
        return currentFunction;
    }

    /**
     * Exits a function context:
     * - pop() the function scope
     * - Returns true if a 'retourne' (return) statement was encountered
     */
    public boolean exitFunction() {
        boolean hadReturn = hasReturnStack.peek();
        this.currentFunction = null;
        pop();  // Destroy the function scope
        return hadReturn;
    }

    /**
     * Marks that a return statement has been seen in the current function.
     */
    public void setReturnSeen() {
        hasReturnStack.pop();
        hasReturnStack.push(true);
    }

    /**
     * Returns the expected return type of the current function.
     */
    public Type getCurrentFunctionType() {
        return returnTypeStack.peek();
    }

    /**
     * Checks if the compiler is currently processing inside a function.
     */
    public boolean isInFunction() {
        return returnTypeStack.peek() != null;
    }

    /* DEBUG */

    public void printScopes() {
        System.out.println("\n=== SYMBOL TABLE (Stack of Scopes) ===");
        System.out.println("Depth: " + variablesStack.size() + " scope(s)\n");

        for (int i = variablesStack.size() - 1; i >= 0; i--) {
            System.out.println("Scope[" + i + "] " + (i == 0 ? "(GLOBAL)" : "(LOCAL)") + ":");

            HashMap<String, VariableDeclaration> vars = variablesStack.get(i);
            if (vars.isEmpty()) {
                System.out.println("  (no variables)");
            } else {
                for (Map.Entry<String, VariableDeclaration> entry : vars.entrySet()) {
                    System.out.println("  - " + entry.getKey() + " : " + entry.getValue().getType());
                }
            }

            HashMap<String, FunctionDeclaration> funcs = functionsStack.get(i);
            if (!funcs.isEmpty()) {
                System.out.println("  Functions:");
                for (String key : funcs.keySet()) {
                    System.out.println("    - " + key);
                }
            }
            System.out.println();
        }
        System.out.println("======================================\n");
    }

    /* STRING CONSTANTS */
    public void add(StringConstant c) {
        strings.add(c);
    }

    /* MIPS CODE GENERATION */

    /**
     * Generates the .data segment for string constants.
     */
    public String toMIPS_DataSegment() {
        StringBuilder sb = new StringBuilder("");
        for (StringConstant c : strings)
            sb.append(c.toMIPS_DataSegment());

        return sb.toString();
    }

    /**
     * Allocates space for GLOBAL variables on the stack.
     * Uses $fp (frame pointer) for access, similar to Kyr2.
     */
    public String toMIPS_Allocation_Variables() {
        if (variablesStack.isEmpty()) {
            return "";
        }

        HashMap<String, VariableDeclaration> globalVars = variablesStack.firstElement();
        int nbVars = globalVars.size();
        int totalSize = nbVars * 4;

        if (totalSize == 0) {
            return "";
        }

        return String.format("""
                move $fp, $sp             # initialize frame pointer
                move $s7, $sp             # save for global access
                subi $sp, $sp, %d         # allocate %d bytes (%d variables)
            """, totalSize, totalSize, nbVars);
    }

    /**
     * Returns the total count of global variables.
     */
    public int getGlobalVariableCount() {
        if (variablesStack.isEmpty()) {
            return 0;
        }
        return variablesStack.firstElement().size();
    }

    /**
     * Returns the total size (in bytes) of global variables.
     */
    public int getGlobalVariablesSize() {
        return getGlobalVariableCount() * 4;
    }
}