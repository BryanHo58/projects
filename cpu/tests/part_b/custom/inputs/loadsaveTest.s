addi sp, sp, -12
addi t0, x0, 1328
mul t0, t0, t0
srai t0, t0, 2
sw t0, 0(sp)
sw t0, 4(sp)
sw t0, 8(sp)
lw s0, 0(sp)
lh s1, 4(sp)
lb a0, 8(sp)
add t1, s0, s1
add t1, t1, a0
lw s0, 4(sp)
lw s1, 8(sp)
addi sp, sp, 12
