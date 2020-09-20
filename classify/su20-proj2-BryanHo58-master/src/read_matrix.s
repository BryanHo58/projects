.globl read_matrix

.text
# ==============================================================================
# FUNCTION: Allocates memory and reads in a binary file as a matrix of integers
#   If any file operation fails or doesn't read the proper number of bytes,
#   exit the program with exit code 1.
# FILE FORMAT:
#   The first 8 bytes are two 4 byte ints representing the # of rows and columns
#   in the matrix. Every 4 bytes afterwards is an element of the matrix in
#   row-major order.
# Arguments:
#   a0 (char*) is the pointer to string representing the filename
#   a1 (int*)  is a pointer to an integer, we will set it to the number of rows
#   a2 (int*)  is a pointer to an integer, we will set it to the number of columns
# Returns:
#   a0 (int*)  is the pointer to the matrix in memory
#
# If you receive an fopen error or eof,
# this function exits with error code 50.
# If you receive an fread error or eof,
# this function exits with error code 51.
# If you receive an fclose error or eof,
# this function exits with error code 52.
# ==============================================================================
read_matrix:

    # Prologue
    addi sp, sp, -44
    sw ra, 0(sp)
    sw a0, 4(sp) # filename
    sw a1, 8(sp) # number rows pointer
    sw a2, 12(sp) # number columns pointer
    sw s0, 16(sp) # total number of elements
    sw s1, 20(sp) # total bytes
    sw s2, 24(sp) # heaped matrix read
    sw s3, 28(sp) # heaped matrix dimensions
    sw s4, 32(sp) # file descriptor
    sw s5, 36(sp) # number rows
    sw s6, 40(sp) # number columns

    addi a0, x0, 8
    jal malloc #sets a0 pointer to matrix
    beq a0, x0, mallocReadError
    mv s3, a0

    lw a1, 4(sp)
    li a2, 0
    jal fopen # sets a0 to file descriptor, a0 becomes file descriptor
    li t6, -1
    beq t6, a0, fopenReadError
    mv s4, a0

    # read first two integers (dimensions)
    mv a1, s4
    mv a2, s3
    li a3, 8
    jal fread # read first 8 bytes, a0 = number of bytes read
    li t6, 8
    bne t6, a0, freadReadError

    #s3 now has dimensions
    lw s5, 0(s3) # number rows
    lw s6, 4(s3) # number columns
    mul s0, s5, s6 # total number elements
    li t6, 4
    mul s1, s0, t6 # total number bytes

    # create malloc for matrix read
    mv a0, s1
    blt s5, x0, mallocReadError
    blt s6, x0, mallocReadError
    jal malloc #sets a0 pointer to matrix
    beq a0, x0, mallocReadError
    mv s2, a0  #s2 is read matrix

    # rest of matrix
    mv a1, s4
    mv a2, s2
    mv a3, s1
    jal fread # last bytes of file, a0 = number of bytes read
    bne s1, a0, freadReadError

    #close file
    mv a1, s4
    jal fclose
    li t6, -1
    beq t6, a0, fcloseReadError

    lw ra, 0(sp)
    lw a0, 4(sp) # filename
    lw a1, 8(sp) # number rows pointer
    lw a2, 12(sp) # number columns pointer

    mv a0, s2
    sw s5, 0(a1)
    sw s6, 0(a2)

    lw s0, 16(sp) # total number of elements
    lw s1, 20(sp) # total bytes
    lw s2, 24(sp) # heaped matrix read
    lw s3, 28(sp) # heaped matrix dimensions
    lw s4, 32(sp) # file descriptor
    lw s5, 36(sp) # number rows
    lw s6, 40(sp) # number columns

    addi sp, sp, 44
    
    # Epilogue

    ret

mallocReadError:
    jal free
    li a1, 48
    j exit2

fopenReadError:
    li a1, 50
    j exit2

freadReadError:
    li a1, 51
    j exit2

fcloseReadError:
    li a1, 52
    j exit2
