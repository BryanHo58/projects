addi t0, x0, 1
addi a0, x0, 1
add s0, x0, x0
One:
beq t0, a0, Two
addi s0, s0, 1
addi a0, t0, -1
Two:
addi a0, a0, 1
bne t0, a0, Three
addi s0, s0, 2
Three:
blt t0, a0, Four
addi s0, s0, 3
addi a0, a0, -1
Four:
bge a0, t0, Five
addi s0, s0, 5
addi a0, x0, 3
Five:
addi a0, a0, -3
bltu, t0, a0, Six
addi s0, s0, 7
Six:
bgeu a0, t0, Seven
addi s0, s0, 11
addi s1, x0, 29
beq, s1, s0, Done
Seven:
addi a0, t0, -1
bge t0, a0, One
Done:
