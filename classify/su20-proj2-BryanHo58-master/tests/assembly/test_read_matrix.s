.import ../../src/read_matrix.s
.import ../../src/utils.s

.data
file_path: .asciiz "inputs/test_read_matrix/test_input.bin"

.text
main:
    # Read matrix into memory
    la a0, file_path
    addi sp, sp, -12
    mv a1, sp
    addi a2, sp, 4
    sw ra, 8(sp)

    sw a1, 0(sp)
    sw a2, 4(sp)

    jal read_matrix

    lw a1, 0(sp)
    lw a2, 4(sp)

    # Print out elements of matrix
    jal print_int_array


    # Terminate the program
    lw ra, 8(sp)
    addi sp, sp, 8
    jal exit
