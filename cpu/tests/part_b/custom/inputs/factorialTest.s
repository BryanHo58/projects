addi s0, x0, 8
addi sp, sp, -4
sw s0, 0(sp)
addi s0, sp, 0

main:
    add t0, s0, x0
    lw a0, 0(t0)
    jal ra, factorial
    jal x0, Finish

factorial:
    addi t0, x0, 1
    add t1, a0, x0

loop:
    beq t1, x0, exit
    mul t0, t0, t1
    addi t1, t1, -1
    jal x0 loop

exit:
    add a0, x0, t0
    jalr x0 ra 0

Finish:
addi sp, sp, 4
