.globl matmul

.text
# =======================================================
# FUNCTION: Matrix Multiplication of 2 integer matrices
# 	d = matmul(m0, m1)
#   The order of error codes (checked from top to bottom):
#   If the dimensions of m0 do not make sense,
#   this function exits with exit code 2.
#   If the dimensions of m1 do not make sense,
#   this function exits with exit code 3.
#   If the dimensions don't match,
#   this function exits with exit code 4.
# Arguments:
# 	a0 (int*)  is the pointer to the start of m0
#	a1 (int)   is the # of rows (height) of m0
#	a2 (int)   is the # of columns (width) of m0
#	a3 (int*)  is the pointer to the start of m1
# 	a4 (int)   is the # of rows (height) of m1
#	a5 (int)   is the # of columns (width) of m1
#	a6 (int*)  is the pointer to the the start of d
# Returns:
#	None (void), sets d = matmul(m0, m1)
# =======================================================
matmul:

    # Error checks

    # Prologue
    addi sp, sp, -32
    sw s0, 0(sp) # row m0
    sw s1, 4(sp) # col m0
    sw s2, 8(sp) # row m1
    sw s3, 12(sp) # col m1
    sw s4, 16(sp) # pointer m2
    sw s5, 20(sp) # original pointer m1
    sw ra, 24(sp)
    sw s6, 28(sp) # row jump m0

    addi t4, x0, 4

    mv s0, a1
    mv s1, a2
    mv s2, a4
    mv s3, a5
    mv s4, a6
    mv s5, a3
    mul s6, t4, s1

    sub t0, a0, s6 # pointer for m0
    mv t1, x0 # counter m0
    mv t2, a3 # pointer for m1
    mv t3, x0 # counter m1

exit_test_m0:
    mv t4, s0
    bge t4, x0, exit_test_m0s
    addi a1, x0, 2
    j exit2

exit_test_m0s:
    mv t4, s1
    bge t4, x0, exit_test_m1
    addi a1, x0, 2
    j exit2

exit_test_m1:
    mv t4, s2
    bge t4, x0 exit_test_m1s
    addi a1, x0, 3
    j exit2

exit_test_m1s:
    mv t4, s3
    bge t4, x0, exit_test_dim
    addi a1, x0, 3
    j exit2

exit_test_dim:
    beq s1, s2, outer_loop_start
    addi a1, x0, 4
    j exit2


outer_loop_start:

    beq t1, s0, outer_loop_end

    mv a2, s1 # sets length of each dot product
    addi a3, x0, 1 # sets stride m0 to 1
    mv a4, s3 # sets stride m1 to col m1
    add t0, t0, s6 # increment m0 pointer
    addi t1, t1, 1 # increment m0 counter
    addi t2, s5, -4 # set m1 back to original col -4
    mv t3, x0 # set m1 counter to 0

inner_loop_start:

    beq t3, s3, outer_loop_start
    addi t3, t3, 1
    addi t2, t2, 4

    addi sp, sp, -16
    sw t0, 0(sp)
    sw t1, 4(sp)
    sw t2, 8(sp)
    sw t3, 12(sp)

    mv a0, t0
    mv a1, t2

    jal ra dot

inner_loop_end:
    lw t0, 0(sp)
    lw t1, 4(sp)
    lw t2, 8(sp)
    lw t3, 12(sp)
    addi sp, sp, 16

    sw a0, 0(s4)
    addi s4, s4, 4

    j inner_loop_start




outer_loop_end:


    # Epilogue


    lw s0, 0(sp) # row m0
    lw s1, 4(sp) # col m0
    lw s2, 8(sp) # row m1
    lw s3, 12(sp) # col m1
    lw s4, 16(sp) # pointer m2
    lw s5, 20(sp) # original pointer m1
    lw ra, 24(sp)
    lw s6, 28(sp)
    addi sp, sp, 32

    ret
