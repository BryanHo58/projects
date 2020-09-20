.globl write_matrix

.text
# ==============================================================================
# FUNCTION: Writes a matrix of integers into a binary file
#   If any file operation fails or doesn't write the proper number of bytes,
#   exit the program with exit code 1.
# FILE FORMAT:
#   The first 8 bytes of the file will be two 4 byte ints representing the
#   numbers of rows and columns respectively. Every 4 bytes thereafter is an
#   element of the matrix in row-major order.
# Arguments:
#   a0 (char*) is the pointer to string representing the filename
#   a1 (int*)  is the pointer to the start of the matrix in memory
#   a2 (int)   is the number of rows in the matrix
#   a3 (int)   is the number of columns in the matrix
# Returns:
#   None
#
# If you receive an fopen error or eof,
# this function exits with error code 53.
# If you receive an fwrite error or eof,
# this function exits with error code 54.
# If you receive an fclose error or eof,
# this function exits with error code 55.
# ==============================================================================
write_matrix:

    # Prologue
    addi sp, sp, -28
    sw ra, 0(sp)
    sw s0, 4(sp) # filename / dimensions holder
    sw s1, 8(sp) # matrix pointer
    sw s2, 12(sp) # number of rows
    sw s3, 16(sp) # number of columns
    sw s4, 20(sp) # total elements
    sw s5, 24(sp) # file descriptor


    mv s0, a0
    mv s1, a1
    mv s2, a2
    mv s3, a3

    mv a1, a0
    li a2, 1
    jal fopen # sets a0 to file descriptor, a0 becomes file descriptor
    li t6, -1
    beq t6, a0, fopenWriteError
    mv s5, a0

    # write dimensions
    addi sp, sp, -8
    sw s2, 0(sp)
    sw s3, 4(sp)
    mv a1, s5
    mv a2, sp
    li a3, 2
    li a4, 4
    jal fwrite # returns a0
    li t6 2
    bne a0, t6, fwriteWriteError
    addi sp, sp, 8

    # write elements
    mul s4, s2, s3 # total number of elements
    mv a1, s5
    mv a2, s1
    mv a3, s4
    li a4, 4
    jal fwrite
    bne a0, s4, fwriteWriteError

    #close file
    mv a1, s5
    jal fclose
    li t6, -1
    beq t6, a0, fcloseWriteError

    # Epilogue
    lw ra, 0(sp)
    lw s0, 4(sp) # filename
    lw s1, 8(sp) # matrix pointer
    lw s2, 12(sp) # number of rows
    lw s3, 16(sp) # number of columns
    lw s4, 20(sp) # total elements
    lw s5, 24(sp) # file descriptor

    addi sp, sp, 28

    ret


fopenWriteError:
    li a1, 53
    j exit2

fwriteWriteError:
    li a1, 54
    j exit2

fcloseWriteError:
    li a1, 55
    j exit2
