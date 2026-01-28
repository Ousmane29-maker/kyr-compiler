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

### Language Syntax

#### Example: Variable Declarations and Assignments

**Kyr Source Code:**
```kyr
variables
entier i
entier j
booleen flag
debut
i = 1;
j = 2;
flag = vrai;
ecrire "i=";
ecrire i;
ecrire "\n";
ecrire "j=";
ecrire j;
ecrire "\n";
ecrire "flag=";
ecrire flag;
fin
```

**Output:**
```
i=1
j=2
flag=vrai
```

**Generated MIPS Assembly:**
```mips
.data
    vrai: .asciiz "vrai"
    faux: .asciiz "faux"
    uniqueLabel0: .asciiz "i="
    uniqueLabel1: .asciiz "\n"
    uniqueLabel2: .asciiz "j="
    uniqueLabel3: .asciiz "\n"
    uniqueLabel4: .asciiz "flag="
.text
main:
    move $fp, $sp             # initialize frame pointer
    subi $sp, $sp, 12         # allocate 12 bytes (3 variables)
    li $v0, 1
    sw $v0, 0($fp)            # i = 1
    li $v0, 2
    sw $v0, 4($fp)            # j = 2
    li $v0, 1
    sw $v0, 8($fp)            # flag = vrai
    la $v0, uniqueLabel0
    move $a0, $v0
    li $v0, 4                 # set the syscall code for printing
    syscall
    lw $v0, 0($fp)            # load i
    move $a0, $v0
    li $v0, 1                 # set the syscall code for printing
    syscall
    la $v0, uniqueLabel1
    move $a0, $v0
    li $v0, 4                 # set the syscall code for printing
    syscall
    la $v0, uniqueLabel2
    move $a0, $v0
    li $v0, 4                 # set the syscall code for printing
    syscall
    lw $v0, 4($fp)            # load j
    move $a0, $v0
    li $v0, 1                 # set the syscall code for printing
    syscall
    la $v0, uniqueLabel3
    move $a0, $v0
    li $v0, 4                 # set the syscall code for printing
    syscall
    la $v0, uniqueLabel4
    move $a0, $v0
    li $v0, 4                 # set the syscall code for printing
    syscall
    lw $v0, 8($fp)            # load flag
     la $a0, faux
     beqz $v0, uniqueLabel5
     la $a0, vrai
     uniqueLabel5:
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
- Arithmetic operations
- Conditional statements
- Loops
- Functions