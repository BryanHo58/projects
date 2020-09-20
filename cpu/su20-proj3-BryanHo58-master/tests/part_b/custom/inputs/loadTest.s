addi sp, sp, -4
addi t0, x0, 1328
mul t0, t0, t0
mul t0, t0, t0
addi, t0, t0, 1328
sw t0, 0(sp)
lb t1, 0(sp)
lb s0, 1(sp)
lb s1, 2(sp)
lb, a0, 3(sp)
add t0, t1, s0
add t0, t0, s1
add t0, t0, a0
lh s0, 0(sp)
lh s1, 2(sp)
add t1, s0, s1
addi sp, sp, 4
