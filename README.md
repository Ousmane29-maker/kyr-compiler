# Kyr Compiler

This project is a tiny educational compiler for the **Kyr** language, written in **Java** using **JFlex** and **JavaCup**. The compiler generates **MIPS assembly code**.

This version corresponds to **Kyr1**, the second kernel of the language.

---

## Kyr1 – Supported Features

Kyr1 extends Kyr0 with:
- **Variable declarations** (integer and boolean types)
- **Assignment statements**
- **Variable references** in expressions
- **Type checking** (semantic analysis)
- **Print instructions** (strings, integers, booleans, variables)

Kyr2 >>>
- **Arithmetic operations** (Done)
- **Conditional Branching**
- **Loops**

### Language Syntax

#### Example: Variable Declarations and Assignments

**Kyr Source Code:**
```kyr
variables
    entier x
    entier y
    entier resultat
    booleen test
debut
    x = 10;
    y = 4;

    // --- ARITHMETIC TEST ---
    // Expected result: 14 (10 + (4 * 2) / 2)
    // Verifies that multiplication and division have higher precedence than addition
    ecrire x + y * 2 / 2;
    ecrire "\n";

    // Expected result: 14 ((10 + 4) * 2 / 2)
    // Parentheses override default precedence (result is 14 by coincidence here, funny...)
    ecrire (x + y) * 2 / 2;
    ecrire "\n";

    // --- COMPARISON TEST ---
    // Expected result: vrai (1) because 10 > 4
    ecrire x > y;
    ecrire "\n";

    // Expected result: faux (0) because 10 is not equal to 4
    ecrire x == y;
    ecrire "\n";

    // --- COMPLEX LOGIC TEST ---
    // (10 > 5) AND (4 <= 4) -> true AND true -> true 
    test = (x > 5) et (y <= 4);
    ecrire test;
    ecrire "\n";

    // NOT (10 == 10) OR (4 > 10) -> NOT true OR false -> false OR false -> false 
    ecrire non (x == 10) ou (y > x);
    ecrire "\n";
fin
```

**Output:**
```
14
14
vrai
faux
vrai
faux
```

**Generated MIPS Assembly:**
```mips
.data
    vrai: .asciiz "vrai"
    faux: .asciiz "faux"
    uniqueLabel0: .asciiz "\n"
    uniqueLabel1: .asciiz "\n"
    uniqueLabel2: .asciiz "\n"
    uniqueLabel3: .asciiz "\n"
    uniqueLabel4: .asciiz "\n"
    uniqueLabel5: .asciiz "\n"
.text
main:
    move $fp, $sp             # initialize frame pointer
    subi $sp, $sp, 16         # allocate 16 bytes (4 variables)
    li $v0, 10
    sw $v0, 0($fp)            # x = 10
    li $v0, 4
    sw $v0, -4($fp)            # y = 4
    li $v0, 2
    sw $v0, 0($sp)             # push right operand (2)
    subi $sp, $sp, 4
    li $v0, 2
    sw $v0, 0($sp)             # push right operand (2)
    subi $sp, $sp, 4
    lw $v0, -4($fp)            # load y
    addi $sp, $sp, 4           # pop right operand into $v1
    lw $v1, 0($sp)             # pop right operand (2) into $v1
    mul $v0, $v0, $v1          # (y * 2)
    addi $sp, $sp, 4           # pop right operand into $v1
    lw $v1, 0($sp)             # pop right operand (2) into $v1
    div $v0, $v1               # division: (y * 2 / 2)
    mflo $v0                   # get quotient
    sw $v0, 0($sp)             # push right operand (y * 2 / 2)
    subi $sp, $sp, 4
    lw $v0, 0($fp)            # load x
    addi $sp, $sp, 4           # pop right operand into $v1
    lw $v1, 0($sp)             # pop right operand (y * 2 / 2) into $v1
    add $v0, $v0, $v1          # (x + y * 2 / 2)
    move $a0, $v0
    li $v0, 1                 # set the syscall code for printing
    syscall
    la $v0, uniqueLabel0
    move $a0, $v0
    li $v0, 4                 # set the syscall code for printing
    syscall
    li $v0, 2
    sw $v0, 0($sp)             # push right operand (2)
    subi $sp, $sp, 4
    li $v0, 2
    sw $v0, 0($sp)             # push right operand (2)
    subi $sp, $sp, 4
    lw $v0, -4($fp)            # load y
    sw $v0, 0($sp)             # push right operand (y)
    subi $sp, $sp, 4
    lw $v0, 0($fp)            # load x
    addi $sp, $sp, 4           # pop right operand into $v1
    lw $v1, 0($sp)             # pop right operand (y) into $v1
    add $v0, $v0, $v1          # (x + y)
    addi $sp, $sp, 4           # pop right operand into $v1
    lw $v1, 0($sp)             # pop right operand (2) into $v1
    mul $v0, $v0, $v1          # (x + y * 2)
    addi $sp, $sp, 4           # pop right operand into $v1
    lw $v1, 0($sp)             # pop right operand (2) into $v1
    div $v0, $v1               # division: (x + y * 2 / 2)
    mflo $v0                   # get quotient
    move $a0, $v0
    li $v0, 1                 # set the syscall code for printing
    syscall
    la $v0, uniqueLabel1
    move $a0, $v0
    li $v0, 4                 # set the syscall code for printing
    syscall
    lw $v0, -4($fp)            # load y
    sw $v0, 0($sp)             # push right operand (y)
    subi $sp, $sp, 4
    lw $v0, 0($fp)            # load x
    addi $sp, $sp, 4           # pop right operand into $v1
    lw $v1, 0($sp)             # pop right operand (y) into $v1
    sgt $v0, $v0, $v1          # check if (x > y)
    la $a0, faux
    beqz $v0, uniqueLabel6
    la $a0, vrai
    uniqueLabel6:
    li $v0, 4                 # set the syscall code for printing
    syscall
    la $v0, uniqueLabel2
    move $a0, $v0
    li $v0, 4                 # set the syscall code for printing
    syscall
    lw $v0, -4($fp)            # load y
    sw $v0, 0($sp)             # push right operand (y)
    subi $sp, $sp, 4
    lw $v0, 0($fp)            # load x
    addi $sp, $sp, 4           # pop right operand into $v1
    lw $v1, 0($sp)             # pop right operand (y) into $v1
    seq $v0, $v0, $v1          # check if (x == y)
    la $a0, faux
    beqz $v0, uniqueLabel7
    la $a0, vrai
    uniqueLabel7:
    li $v0, 4                 # set the syscall code for printing
    syscall
    la $v0, uniqueLabel3
    move $a0, $v0
    li $v0, 4                 # set the syscall code for printing
    syscall
    li $v0, 4
    sw $v0, 0($sp)             # push right operand (4)
    subi $sp, $sp, 4
    lw $v0, -4($fp)            # load y
    addi $sp, $sp, 4           # pop right operand into $v1
    lw $v1, 0($sp)             # pop right operand (4) into $v1
    sle $v0, $v0, $v1          # check if (y <= 4)
    sw $v0, 0($sp)             # push right operand (y <= 4)
    subi $sp, $sp, 4
    li $v0, 5
    sw $v0, 0($sp)             # push right operand (5)
    subi $sp, $sp, 4
    lw $v0, 0($fp)            # load x
    addi $sp, $sp, 4           # pop right operand into $v1
    lw $v1, 0($sp)             # pop right operand (5) into $v1
    sgt $v0, $v0, $v1          # check if (x > 5)
    addi $sp, $sp, 4           # pop right operand into $v1
    lw $v1, 0($sp)             # pop right operand (y <= 4) into $v1
    and $v0, $v0, $v1          # logical: (x > 5 et y <= 4)
    sw $v0, -12($fp)            # test = x > 5 et y <= 4
    lw $v0, -12($fp)            # load test
    la $a0, faux
    beqz $v0, uniqueLabel8
    la $a0, vrai
    uniqueLabel8:
    li $v0, 4                 # set the syscall code for printing
    syscall
    la $v0, uniqueLabel4
    move $a0, $v0
    li $v0, 4                 # set the syscall code for printing
    syscall
    lw $v0, 0($fp)            # load x
    sw $v0, 0($sp)             # push right operand (x)
    subi $sp, $sp, 4
    lw $v0, -4($fp)            # load y
    addi $sp, $sp, 4           # pop right operand into $v1
    lw $v1, 0($sp)             # pop right operand (x) into $v1
    sgt $v0, $v0, $v1          # check if (y > x)
    sw $v0, 0($sp)             # push right operand (y > x)
    subi $sp, $sp, 4
    li $v0, 10
    sw $v0, 0($sp)             # push right operand (10)
    subi $sp, $sp, 4
    lw $v0, 0($fp)            # load x
    addi $sp, $sp, 4           # pop right operand into $v1
    lw $v1, 0($sp)             # pop right operand (10) into $v1
    seq $v0, $v0, $v1          # check if (x == 10)
    xori $v0, $v0, 1         # logical NOT : flip boolean (0<->1)
    addi $sp, $sp, 4           # pop right operand into $v1
    lw $v1, 0($sp)             # pop right operand (y > x) into $v1
    or $v0, $v0, $v1           # logical: (non x == 10 ou y > x)
    la $a0, faux
    beqz $v0, uniqueLabel9
    la $a0, vrai
    uniqueLabel9:
    li $v0, 4                 # set the syscall code for printing
    syscall
    la $v0, uniqueLabel5
    move $a0, $v0
    li $v0, 4                 # set the syscall code for printing
    syscall
end:
    li $v0, 10                # terminate execution
    syscall

```

---

## Semantic Checks

Kyr1 performs the following semantic validations:
- Variables must be declared before use
- No duplicate variable declarations
- Type compatibility in assignments (integer = integer, boolean = boolean)
- Variables must be initialized before being read 

---

## Project Structure
```
kyr/
├── ast/                    # Abstract Syntax Tree nodes
│   ├── declarations/       # Variable declarations
│   ├── expressions/        # Constants, variable references
│   └── statements/         # Assignments, print statements
├── parser/                 # JavaCup grammar and generated parser
├── scanner/                # JFlex lexical analyzer
├── symtable/               # Symbol table for variables
├── tests/                  # tests and MIPS code generated
└── exceptions/             # Lexical, syntax, and semantic errors
```

**Technologies:**
- **Lexical Analysis:** JFlex
- **Syntax Analysis:** JavaCup
- **Code Generation:** MIPS assembly
- **Target Architecture:** MIPS processor

---

## Usage

1. Write your Kyr source code with `.kyr` extension
2. Compile using the Kyr compiler:
```bash
   java -jar kyr1.jar program.kyr
```
3. If compilation succeeds, a `program.mips` file is generated
4. Run the generated MIPS assembly in a simulator (MARS, SPIM, QtSpim)

---

## Error Messages

The compiler produces one of the following outputs:

- `COMPILATION OK` – Code generated successfully
- `ERREUR LEXICALE : line X : message` – Invalid token
- `ERREUR SYNTAXIQUE : line X : message` – Grammar violation
- `ERREUR SEMANTIQUE : line X : message` – Type error, undeclared variable, etc.

---

## Future Extensions

Future versions of Kyr will include:
- Conditional statements
- Loops
- Functions