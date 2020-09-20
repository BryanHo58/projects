addi t0, x0, -8
addi sp, sp, -12
addi t1, x0, 1328
sw t1, 0(sp)
swlt t0, -8(sp)
lw t1, 0(sp)
addi t0, t0, -1
swlt t0, -8(sp)
lw s1, 0(sp)
addi t0, t0, 2
swlt t0, -8(sp)
lw a0, 0(sp)
add s0, t1, a0
add s0, s0, s1
addi sp, sp, 12
