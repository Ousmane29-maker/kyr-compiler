# Kyr Compiler

This project is a tiny educational compiler for the **Kyr** language, written in **Java** using **JFlex** and **JavaCup**. The compiler generates **MIPS assembly code**.

This version corresponds to **Kyr-0**, the first kernel of the language.

---

## Kyr0 – Supported Features

In Kyr0, the compiler supports only **print instructions**.

### Language Syntax

A Kyr0 program has the following structure:

#### Example 1: Printing a String

**Kyr Source Code:**

```kyr
debut
    ecrire "Hello world !";
fin
```

**Generated MIPS Assembly:**

```mips
.data
    vrai: .asciiz "vrai"
    faux: .asciiz "faux"
    uniqueLabel0: .asciiz "Hello world !"

.text
main:
    la $a0, uniqueLabel0
    li $v0, 4           # syscall code for printing string
    syscall

end:
    li $v0, 10          # syscall code for program termination
    syscall
```

---

#### Example 2: Printing a Boolean

**Kyr Source Code:**

```kyr
debut
    ecrire vrai;
fin
```

**Generated MIPS Assembly:**

```mips
.data
    vrai: .asciiz "vrai"
    faux: .asciiz "faux"

.text
main:
    li $a0, 1           # load boolean value (1 for true)
    li $v0, 1           # syscall code for printing integer
    syscall

end:
    li $v0, 10          # syscall code for program termination
    syscall
```

---

## Project Structure

- **Lexical Analysis:** JFlex
- **Syntax Analysis:** JavaCup
- **Code Generation:** MIPS assembly
- **Target Architecture:** MIPS processor

---

## Usage

1. Write your Kyr0 source code with `.kyr` extension
2. Compile using the Kyr compiler
3. Run the generated MIPS assembly code in a MIPS simulator (e.g., MARS, SPIM)

---

## Future Extensions

Future versions of Kyr will include:
- Variable declarations and assignments
- Arithmetic operations
- Conditional statements
- Loops
- Functions