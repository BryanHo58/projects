addi t0, x0, 2
add t0, t0, t0
addi t1, x0, 7
sub t0, t0, t1
sll t0, t0, t1
slli t0, t0, 3
mul t0, t0, t0
slti t2, t0, 2034
slli s0, t1, 6
slt s1, t0, s0
xor t2, s1, t0
xori t2, t2, 1250
addi t2, t2 2001
or t0, t2, t0
mul s1, s1, t0
ori t0, t0, -2047
sra t0, t0, t1
srai s1, t0, 5
divu t0, t0, t2
andi t0, t2, -685
mul t0, t0, t0
mulh t1, t0, t0
andi t0, t0, -685
mulhu t2, t0, t0
srl t2, t2, s1
remu s0, t2, t1
addi t1, t1, -875
and a0, t1, s0
srli a0, a0, 3
