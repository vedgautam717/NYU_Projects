# Performance Modelling - RISC-V processor

This project will require you to implement cycle-accurate simulators of a 32 - bit RISC-V processor in C++ or
Python. The skeleton code for the assignment is given in le (NYU_RV 32 I_ 6913 .cpp or
NYU_RV 32 I_ 6913 .py).

The simulators should take in two les as inputs: imem.text and dmem.txt les
The simulator should give out the following:

```
● cycle by cycle state of the register le (RFOutput.txt)
● Cycle by cycle microarchitectural state of the machine (StateResult.txt)
● Resulting dmem data after the execution of the program (DmemResult.txt)
```
The imem.txt le is used to initialize the instruction memory and the dmem.txt le is used to initialize the
data memory of the processor. Each line in the les contain a byte of data on the instruction or the data
memory and both the instruction and data memory are byte addressable. This means that for a 32 bit
processor, 4 lines in the imem.txt le makes one instruction. Both instruction and data memory are in
“Big-Endian” format (the most signicant byte is stored in the smallest address).

The instructions to be supported by the processor are categorized into the following types:

The simulator should support the following set of instructions.

```
Mnemonic Type Full Name Psuedocode Details
ADD R Addition rd = rs 1 + rs 2 Store the result of rs 1 + rs 2 in register rd.
```
```
SUB R Subtraction rd = rs 1 - rs 2 Store the result of rs 1 - rs 2 in register rd.
```
```
XOR R Bitwise XOR rd = rs 1 ^ rs 2 Store the result of rs 1 ^ rs 2 in register rd.
```
```
OR R Bitwise OR rd = rs 1 | rs 2 Store the result of rs 1 | rs 2 in register rd.
```

```
AND R Bitwise AND rd = rs 1 & rs 2 Store the result of rs 1 & rs 2 in register rd.
```
```
ADDI I Add Immediate rd = rs 1 + sign_ext(imm)
```
```
Add the sign-extended immediate to
register rs 1 and store in rd. Overow bits ignored.
```
```
XORI I XOR Immediate rd = rs 1 ^ sign_ext(imm)
```
```
Bitwise XOR the sign-extended immediate to
register rs 1 and store result in rd.
```
```
ORI I OR Immediate rd = rs 1 | sign_ext(imm)
```
```
Bitwise OR the sign-extended immediate to
register rs 1 and store result in rd.
```
```
ANDI I AND Immediate rd = rs 1 & sign_ext(imm)
```
```
Bitwise AND the sign-extended immediate to
register rs 1 and store result in rd.
```
```
JAL J Jump and Link
```
```
rd = PC + 4 ;
PC = PC + sign_ext(imm)
```
```
Jump to PC = PC + sign_ext(imm) and store the
current PC + 4 in rd.
```
```
BEQ B Branch if equal
```
```
PC = (rs 1 == rs 2 )? PC +
sign_ext(imm) : PC + 4
```
```
Take the branch (PC = PC + sign_ext(imm)) if rs 1 is
equal to rs 2.
```
```
BNE B Branch if not equal
```
```
PC = (rs 1 != rs 2 )? PC +
sign_ext(imm) : PC + 4
```
```
Take the branch (PC = PC + sign_ext(imm)) if rs 1 is
not equal to rs 2.
```
```
LW I Load Word
```
```
rd = mem[rs 1 +
signa(imm)][ 31 : 0 ]
```
```
Load 32 - bit value at memory address [rs 1 +
signPext(imm)] and store it in rd.
```
```
SW S Store Word
```
```
data[rs 1 +
sign_ext(imm)][ 31 : 0 ] =
rs 2
```
```
Store the 32 bits of rs 2 to memory address [rs 1 value
+ sign_ext(imm)].
```
```
HALT - Halt execution
```
**Instruction encoding:**

```
Mnemonic
```
```
Bit Fields
31 : 27 26 : 25 24 : 20 19 : 15 14 : 12 11 : 7 6 : 0
ADD 0000000 rs 2 rs 1 000 rd 0110011
SUB 0100000 rs 2 rs 1 000 rd 0110011
XOR 0000000 rs 2 rs 1 100 rd 0110011
```

```
OR 0000000 rs 2 rs 1 110 rd 0110011
AND 0000000 rs 2 rs 1 111 rd 0110011
ADDI imm[ 11 : 0 ] rs 1 000 rd 0010011
XORI imm[ 11 : 0 ] rs 1 100 rd 0010011
ORI imm[ 11 : 0 ] rs 1 110 rd 0010011
ANDI imm[ 11 : 0 ] rs 1 111 rd 0010011
JAL imm[ 20 | 10 : 1 | 11 | 19 : 12 ] rd 1101111
BEQ imm[ 12 | 10 : 5 ] rs 2 rs 1 000 imm[ 4 : 1 | 11 ] 1100011
BNE imm[ 12 | 10 : 5 ] rs 2 rs 1 001 imm[ 4 : 1 | 11 ] 1100011
LW imm[ 11 : 0 ] rs 1 000 rd 0000011
SW imm[ 11 : 0 ] rs 2 rs 1 010 imm[ 4 : 0 ] 0100011
HALT x x x xxx x 1111111
```
The simulator should have the following ve stages in its pipeline:
**● Instruction Fetch:** Fetches instruction from the instructionmemory using PC value as address.
**● Instruction Decode/ Register Read:** Decodes the instructionusing the format in the table above
and generates control signals and data signals after reading from the register le.
**● Execute:** Perform operations on the data as directedby the control signals.
**● Load/ Store:** Perform memory related operations.
**● Writeback:** Write the result back into the destinationregister. Remember that R 0 in RISC-V can
only contain the value 0.

Each stage must be preceded by a group of ip-ops to store the data to be passed on to the next stage in the
next cycle. Each stage should contain a nop bit to represent if the stage should be inactive in the following
cycle.

The simulator must be able to deal with two types of hazards.

```
1. RAW Hazards: RAW hazards are dealt with using either only forwarding (if possible) or, if not,
using stalling + forwarding. Use EX-ID forwarding and MEM-ID forwarding appropriately.
2. Control Flow Hazards: The branch conditions are resolved in the ID/RF stage of the pipeline.
```
The simulator deals with branch instructions as follows:

```
1. Branches are always assumed to be NOT TAKEN. That is, when a beq is fetched in the IF stage, the
PC is speculatively updated as PC+ 4.
2. Branch conditions are resolved in the ID/RF stage.
```

```
3. If the branch is determined to be not taken in the ID/RF stage (as predicted), then the pipeline
proceeds without disruptions. If the branch is determined to be taken, then the speculatively
fetched instruction is discarded and the nop bit is set for the ID/RR stage for the next cycle. Then
the new instruction is fetched in the next cycle using the new branch PC address.
```
Tasks:

1 ) Draw the schematic for a single stage processor and ll in your code in the to run the simulator. ( 20
points)
2 ) Draw the schematic for a ve stage pipelined processor and ll in your code to run the simulator. The
processor should be able ot take care of RAW and control hazards by stalling and forwarding. ( 20
points)
3 ) Measure and report average CPI, Total execution cycles, and Instructions per cycle for both these cores
by adding performance monitors to your code. (Submit code and print results to console or a le.) ( 5
points)
4 ) Compare the results from both the single stage and the ve stage pipelined processor implementations
and explain why one is better than the other. ( 5 points)
5 ) What optimizations or features can be added to improve performance? (Extra credit 1 point)

Your work will be evaluated against the 10 test cases, 3 of which will be revealed one week before the
deadline. ( 50 points - 5 points each)

Useful References:
● More details on the full ISA specication can be found at
https://riscv.org/wp-content/uploads/ 2019 / 12 /riscv-spec- 20191213 .pdf

```
● bitset library for C++:https://en.cppreference.com/w/cpp/utility/bitset
```
```
● g++:https://gcc.gnu.org/onlinedocs/gcc- 3. 3. 6 /gcc/G_ 002 b_ 002 b-and-GCC.html
```
```
● python:https://www.python.org/downloads/
```
