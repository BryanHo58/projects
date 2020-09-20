.globl classify

.text
classify:
    # =====================================
    # COMMAND LINE ARGUMENTS
    # =====================================
    # Args:
    #   a0 (int)    argc
    #   a1 (char**) argv
    #   a2 (int)    print_classification, if this is zero,
    #               you should print the classification. Otherwise,
    #               this function should not print ANYTHING.
    # Returns:
    #   a0 (int)    Classification
    #
    # If there are an incorrect number of command line args,
    # this function returns with exit code 49.
    #
    # Usage:
    #   main.s -m -1 <M0_PATH> <M1_PATH> <INPUT_PATH> <OUTPUT_PATH>

    li t6, 5
    bne t6, a0, commandlineError


    addi sp, sp, -52
    sw ra, 0(sp)
    sw s0, 4(sp) # argc
    sw s1, 8(sp) # argv
    sw s2, 12(sp) # print classification
    sw s3, 16(sp) # dimensions m0
    sw s4, 20(sp) # dimensions m1
    sw s5, 24(sp) # m0
    sw s6, 28(sp) # m1
    sw s7, 32(sp) # dimensions input
    sw s8, 36(sp) # input matrix
    sw s9, 40(sp) # output matrix
    sw s10, 44(sp) # number of elements
    sw s11, 48(sp) #output

    mv s0, a0
    mv s1, a1
    mv s2, a2

	# =====================================
    # LOAD MATRICES
    # =====================================
    # Load pretrained m0

    addi a0, x0, 8
    jal malloc #sets a0 pointer to matrix
    beq a0, x0, mallocClassifyError
    mv s3, a0

    lw a0, 4(s1)
    mv a1, s3
    addi a2, s3, 4
    jal read_matrix # a0 returns pointer to Matrix
    mv s5, a0

    # Load pretrained m1

    addi a0, x0, 8
    jal malloc #sets a0 pointer to matrix
    beq a0, x0, mallocClassifyError
    mv s4, a0

    lw a0, 8(s1)
    mv a1, s4
    addi a2, s4, 4
    jal read_matrix # a0 returns pointer to Matrix
    mv s6, a0

    # Load input matrix

    addi a0, x0, 8
    jal malloc #sets a0 pointer to matrix
    beq a0, x0, mallocClassifyError
    mv s7, a0

    lw a0, 12(s1)
    mv a1, s7
    addi a2, s7, 4
    jal read_matrix # a0 returns pointer to Matrix
    mv s8, a0


    # =====================================
    # RUN LAYERS
    # =====================================
    # 1. LINEAR LAYER:    m0 * input
    # 2. NONLINEAR LAYER: ReLU(m0 * input)
    # 3. LINEAR LAYER:    m1 * ReLU(m0 * input)

    # Linear Layer 1
    li t6, 4
    lw t5, 0(s3)
    lw t4, 4(s7)
    mul t3, t5, t4
    mv s10, t3
    mul t3, t3, t6
    mv a0, t3
    jal malloc #sets a0 pointer to matrix
    beq a0, x0, mallocClassifyError
    mv s9, a0

    mv a0, s5
    lw a1, 0(s3)
    lw a2, 4(s3)
    mv a3, s8
    lw a4, 0(s7)
    lw a5, 4(s7)
    mv a6, s9
    jal matmul

    # s9 is now m0*input

    # Non Linear Layer (ReLU)
    mv a0, s9
    mv a1, s10
    jal relu # a0 is now relu'd - s9

    #Linear Layer
    li t6, 4
    lw t5, 0(s4)
    lw t4, 4(s7)
    mul t3, t5, t4
    mv s10, t3
    mul t3, t3, t6
    mv a0, t3
    jal malloc #allocates memory for new output
    beq a0, x0, mallocClassifyError
    mv s11, a0 # output

    mv a0, s6
    lw a1, 0(s4)
    lw a2, 4(s4)
    mv a3, s9
    lw a4, 0(s3)
    lw a5, 4(s7)
    mv a6, s11
    jal matmul
    # s11 now output

    # =====================================
    # WRITE OUTPUT
    # =====================================
    # Write output matrix
    lw a0, 16(s1)
    mv a1, s11
    lw a2, 0(s4)
    lw a3, 4(s7)
    jal write_matrix

    # =====================================
    # CALCULATE CLASSIFICATION/LABEL
    # =====================================
    # Call argmax

    mv a0, s11
    mv a1, s10
    jal argmax
    # a0 is index
    mv s0, a0

    # Print classification
    bne s2, x0, endClassification
    mv a1, s0
    jal print_int

    # Print newline afterwards for clarity
    li a1, 10
    jal print_char

endClassification:
    mv a0, s3
    jal free
    mv a0, s4
    jal free
    mv a0, s5
    jal free
    mv a0, s6
    jal free
    mv a0, s7
    jal free
    mv a0, s8
    jal free
    mv a0, s9
    jal free
    mv a0, s11
    jal free


    mv a0, s0

    lw ra, 0(sp)
    lw s0, 4(sp) # argc
    lw s1, 8(sp) # argv
    lw s2, 12(sp) # print classification
    lw s3, 16(sp) # dimensions m0
    lw s4, 20(sp) # dimensions m1
    lw s5, 24(sp) # m0
    lw s6, 28(sp) # m1
    lw s7, 32(sp) # dimensions input
    lw s8, 36(sp) # input matrix
    lw s9, 40(sp) # output matrix
    lw s10, 44(sp) # number of elements
    lw s11, 48(sp) # output
    addi sp, sp, 52
    ret

mallocClassifyError:
    jal free
    li a1, 48
    j exit2

commandlineError:
    li a1, 49
    j exit2
