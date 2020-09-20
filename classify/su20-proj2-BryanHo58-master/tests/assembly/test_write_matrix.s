.import ../../src/write_matrix.s
.import ../../src/utils.s

.data
m0: .word 1, 2, 3, 4, 5, 6, 7, 8, 9 # MAKE CHANGES HERE TO TEST DIFFERENT MATRICES
file_path: .asciiz "outputs/test_write_matrix/student_write_matrix_outputs.bin"

.text
main:
    # Write the matrix to a file
    la a0, file_path
    la a1, m0
    li a2, 3
    li a3, 3

    addi sp, sp, -4
    sw ra, 0(sp)

    jal write_matrix

    lw ra, 0(sp)
    addi sp, sp, 4



    # Exit the program
    jal exit
