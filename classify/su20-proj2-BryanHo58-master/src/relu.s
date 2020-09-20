.globl relu

.text
# ==============================================================================
# FUNCTION: Performs an inplace element-wise ReLU on an array of ints
# Arguments:
# 	a0 (int*) is the pointer to the array
#	a1 (int)  is the # of elements in the array
# Returns:
#	None
#
# If the length of the vector is less than 1,
# this function exits with error code 8.
# ==============================================================================
relu:
    # Prologue
    mv t0, a0 # pointer
    mv t1, a1 # counter

    blt x0, t1, loop_start

    addi a1, x0, 8
    j exit2

loop_start:
    addi t1, t1, -1
    lw t2, 0(t0)
    bge t2, x0, loop_continue
    mv t2, x0
    sw t2, 0(t0)


loop_continue:
    beq t1, x0, loop_end
    addi t0, t0, 4
    j loop_start


loop_end:

    # Epilogue


	ret
