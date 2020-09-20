.globl dot

.text
# =======================================================
# FUNCTION: Dot product of 2 int vectors
# Arguments:
#   a0 (int*) is the pointer to the start of v0
#   a1 (int*) is the pointer to the start of v1
#   a2 (int)  is the length of the vectors
#   a3 (int)  is the stride of v0
#   a4 (int)  is the stride of v1
# Returns:
#   a0 (int)  is the dot product of v0 and v1
#
# If the length of the vector is less than 1,
# this function exits with error code 5.
# If the stride of either vector is less than 1,
# this function exits with error code 6.
# =======================================================
dot:

    # Prologue
    addi sp, sp, -16
    sw s0, 0(sp) # concurrent sum
    sw s1, 4(sp) # int counter
    sw s2, 8(sp) # dot
    sw ra, 12(sp)


    mv s0, x0
    addi s1, x0, 4

    mv t0, a0 # current pointer v0
    mv t1, a1 # current pointer v1
    mv t2, x0 # counter
    mv t3, x0 # current value v0
    mv t4, x0 # current value v1
    mv t5, x0 # stride jump v0
    mv t6, x0 # stride jump v1


    blt x0, a2, exit_test_stride
    addi a1, x0, 5
    j exit2

exit_test_stride:
    mul t5, s1, a3
    blt x0, a3, exit_test_stride2
    addi a1, x0, 6
    j exit2

exit_test_stride2:
    mul t6, s1, a4
    blt x0, a4, loop_start
    addi a1, x0, 6
    j exit2

loop_start:
    addi t2, t2, 1
    lw t3, 0(t0)
    lw t4, 0(t1)
    mul s2, t3, t4
    add s0, s0, s2

loop_continue:
    beq t2, a2, loop_end
    add t0, t0, t5
    add t1, t1, t6
    j loop_start


loop_end:
    mv a0, s0

    lw s0, 0(sp)
    lw s1, 4(sp)
    lw s2, 8(sp)
    lw ra, 12(sp)
    addi sp, sp, 16

    # Epilogue


    ret
