#  CS61CPU
## Introduction
Project 3 for CS61C was to create a working CPU that could operate on a set ISA and execute those instructions on a given set of registers. Although very daunting at first, working with Logisim and utilizing logic circuitry allowed me to break down the problem into smaller circuits and then connect everything together.

## CPU
The main CPU circuit was built following the diagram from lecture, having a 32-bit instruction passed through various gates and subunits to change values in memory and in the allocated registers. The controls for the CPU were sent by control selectors offered by the Control Logic subunit.

The CPU was **2-staged pipelined** by separating the **Instruction Fetch** stage from the **Instruction Decode,  Execution, Memory Access, and Write Back** stages. This was done by adding a register that would receive the IF instruction as input and output that same instruction the next cycle into the rest of the CPU and the Control Logic.

Special modifications to the CPU were added to account for specific instructions, which will be discussed later.

## Memory Unit
This was supplied by the 61C staff, but essentially it functioned by taking in a word address (potentially aligned to any of bytes 0 through 3) and outputting that 4 byte set for load instructions or storing the inputted write data into that 4 byte set for save instructions.

## Regfile
The regifile subunit was a collection of registers for the given 9 registers of the project (x0, ra, sp, t0, t1	, t2, s0, s1, and a0). The register read half of the regfile took in the rs1 and rs2 queries and outputted values of the corresponding registers. The write back half of the regfile took in the write back data and control selector RegWEn (0 for no overwrite, 1 for overwrite) and sent the data to the corresponding rd register.

## ALU

The ALU subunit had separated circuitry for each of the arithmetic and logic operations. Taking two input values, the ALU would conduct all operations with that given pair of numbers, and select the output of one of them as the output of the entire unit, chosen by a MUX and a 3-bit ALUSel control selector. This offered a total of 16 possible ALU operations.

## Immediate Generator

The immediate generator took bits from the instruction and constructed 5 possible immediates that corresponded to the 5 immediate-involving formats (I, S, SB, U, and UJ). A MUX and a 3-bit ImmSel control selector then chose the immediate needed for the given instruction

## Branch Comparator

Specifically for the SB format instructions, the Comparator took in 3 inputs: the 2 register values and the control selector BrUn, which dictated whether the comparison was to be signed or not. 2 separate branches were created that offered BrEq and BrLt (equal and less than values) as outputs, one for signed and for unsigned comparisons, which were MUXed and selected by BrUn. BrEq and BrLt would then be fed back into the Control Logic.

## Control Logic

The Control Logic subunit is the bread and butter of the CPU and guides the instruction as it passes through. It is also responsible for handling all the control selectors and dictating how data is passed through.

Initially, the OPcode is extracted from the instruction and compared to the opcodes of all the possible RISC-V formats (R, load-type I, arithmetic-type I, jalr I-type S, SB, auipc U-type, lui U-type, and UJ), which fed into 9 corresponding labels, setting the match to 1 and the others to 0. From these format labels, the **ImmSel** was chosen that matched to the instruction's label and OPcode via MUX.

**ASel**, which dictated whether the first input for the ALU was rs1 or the program counter, was set to 1 if the instruction was SB type, auipc, or jal, as those were the only instructions that directly did arithmetic on the PC.

**BSel** dictated whether the second input for the ALU was rs2 or the immediate generated from the instruction. Since only R format instructions used rs2 (the register value), it was simply a negation of the R format label (if the instruction was R format, BSel would be 0, else it is 1).

Similarly, **MemRW**, which dictated whether the rs2 data passed into the memory unit would be written into memory, was simply dictated by whether the instruction was S type or not.

**WBSel** dictated what the write back data would be, either the loaded data from the memory, the ALU output, or simply PC + 4. Load I-type instructions were set to WBSel 0 (the memory output), jal and jalr instructions were set to 2 (PC + 4), and every other instruction was set to 1 (ALU output).

**RegWEn** (detailed before) is set to 1 if the instruction required data to be written to the regfile. Therefore, all R, load I-type, auipc U-type, lui U-type, and jump UJ-type instructions will set RegWEn to 1, while the default for all other instructions would be 0 (registers are not overwritten).

**BrUn** was simply set accordingly if the instruction was SB-format and required an unsigned comparison (for this project, only bltu and bgeu). This was checked for by checking if the instruction was SB-format (given the label as described above) and checking the func3 of the instruction with all the possible branch instructions. Here, branch instruction labels were also generated in the exact same process as the opcode-format matching above. These were used to specify the given branch instruction (ex. beq). Following suit, BrEq and BrLt were checked to see whether the branch was successful (ie the given branch instruction returned True for its comparison). For example, if the instruction was beq, it would check if BrEq returned 1 (the registers were equal). 

If the branch instruction found that its comparison was successful, then **PCSel** would be set 1. This selector dictates whether the next instruction in the CPU is to be PC + 4 (0 for a non-jump instruction) or to the ALU output (1 for a successful branch instruction).  PCSel is also set to 1 for all jump type instructions (jal and jalr), changing the next PC to be the corresponding ALU output for those instructions.

The final control selector, **ALUSel**, was probably the most difficult to implement. As described in the ALU subunit section, the ALUSel variable would decide which arithmetic/logic operation is outputted. Since the vast majority of instructions used addition (rs1 + offset, PC + offset, rs1 + immediate, etc.), it is "defaulted" to select the addition operation. However, three instructions types required special consideration (arithmetic R-, arithmetic I-, and lui U-format instructions).

If the instruction was lui, then the operation would simply be "bsel" which would just output the second input, since lui only considers the immedate (without rs1). For the arithmetic instructions, special attention was given to the func3 field of the instruction. Since the func7s for the given arithmetic R operations were any of 0x00, 0x01, and 0x20 (3 possible "suboperations"), the instructions func3 and func7 could be used in tandem to extract the specific operation requested. In essence, each func3 "family of operations" had operations with unique func7s, which specified those suboperations. Thus, the combination of func3 and func7 allowed for the selection of the specific operation to be sent to the ALU. The arithmetic I-type instructions did not have the luxury of having a func7, so they only could only choose from one operation from each func3 family, thus allowing me to create a "default" for each func3 family that both arithmetic R- and I-type instructions could point to. The notable exception to this is for shift I-type instructions, whose immediates only require 5 bits instead of 12 and leave room for a "pseudo func7," allowing the control logic to choose which specific shift I-type operation is requested (logical vs arithmetic) and with the same process as with arithmetic R operations.

## Special CPU Modifications
Because some instructions required logic outside the scope of control logic, special subcircuitry was added to the CPU.

Since the memory unit outputted only 4-byte values, special accommodation was needed for lh and lb operations. The CPU utilized a subcircuit, in a similar process to the control logic, would find out whether the instruction was lh and lb, and altered the write back data accordingly, setting the output to either a sign extended half of the memory's output for lh or a sign extended quarter of the memory's output for lb. For lw and all other operations, the write back data is unaltered.

For swlt, a comparison is needed to decide if storage is to be done. Another subcircuit was added that compared the instruction's offset to rs2, and if the comparison was successful (signed less than), then the storage would be allowed (MemRW would remain at 1). However, if the comparison failed, then MemRW would be blocked and a 0 would be inputted instead, preventing the memory from being overwritten. By default, though, MemRW is not blocked, so all other operations are unaffected.

Finally, for successful branch and jump instructions, care is needed to ensure that the next pipelined instruction is killed in favor of the jumped-to/branched-to instruction. This is because, with the 2-stage setup of the CPU, the current instruction at a given PC (the branch/jump instruction) will be followed by the instruction fetch of the instruction at PC + 4 (the instruction directly after) under normal conditions. As described in the CPU section, the fetched (2nd) instruction will be placed into an instruction register, from which the current (1st) instruction was initially outputted. To fix this jumping problem, a MUX was added behind the instruction register to choose from the fetched instruction and a nop instruction. Here I chose, 0x00000013, which encodes for "addi x0, x0, 0." The MUX is selected by PCSel (which tells if a jump / successful branch is taken as detailed above). If PCSel is 0, then the 2nd instruction will flow through, indicating that a branch failed or a non-jump instruction was made, and things will continue normally. If PCSel is 1, then the 2nd instruction will be changed to the nop, as a successful branch or jump was made. If this is the case, then after the successful branch/jump instruction finishes, the fetched instruction is switched with the nop, which will execute while the jumped-to/branched-to instruction will be fetched and passed into the instruction register in the next cycle (since PCSel had been set to 1 for the 1st instruction). This new instruction will then follow the nop as the 3rd instruction, and the CPU will continue normally from thereafter.
