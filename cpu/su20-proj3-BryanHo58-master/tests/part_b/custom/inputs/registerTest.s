addi sp, sp, -32
sw x0, 0(sp)
sw ra, 4(sp)
sw sp, 8(sp)
sw t0, 12(sp)
sw t1, 16(sp)
sw s0, 20(sp)
sw s1, 24(sp)
sw a0, 28(sp)
addi a0, x0, 69
lw x0, 0(sp)
lw ra, 4(sp)
lw sp, 8(sp)
lw t0, 12(sp)
lw t1, 16(sp)
lw s0, 20(sp)
lw s1, 24(sp)
lw a0, 28(sp)
addi sp, sp, 32
