.globl argmax

.text
# =================================================================
# FUNCTION: Given a int vector, return the index of the largest
#	element. If there are multiple, return the one
#	with the smallest index.
# Arguments:
# 	a0 (int*) is the pointer to the start of the vector
#	a1 (int)  is the # of elements in the vector
# Returns:
#	a0 (int)  is the first index of the largest element
#
# If the length of the vector is less than 1,
# this function exits with error code 7.
# =================================================================
argmax:

    # Prologue
    addi sp, sp, -8
    sw s0, 0(sp) # used for largest value
    sw s1, 4(sp) # used for smallest index of largest value

    mv s0, x0 # largest value
    mv s1, x0 # smallest index of largest value

    mv t0, a0 # pointer
    mv t1, a1 # counter (items left)
    mv t2, x0 # current index
    mv t3, x0 # current value
    mv t4, x0 # index calc

    blt x0, t1, loop_start
    addi a1, x0, 7
    j exit2

loop_start:
    addi t1, t1, -1
    lw t3, 0(t0)
    beq t2, x0, loop_edit
    blt s0, t3, loop_edit
    j loop_continue

loop_edit:
    mv s0, t3
    mv s1, t2

loop_continue:
    beq t1, x0, loop_end
    addi t0, t0, 4
    addi t2, t2 1
    j loop_start


loop_end:

    # Epilogue
    mv a0, s1

    lw s0, 0(sp)
    lw s1, 4(sp)
    addi sp, sp, 8
    ret
