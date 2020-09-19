#  numc
## Introduction
Project 4 for CS61C involved creating an equivalent of numpy in C, creating optimal matrix functions to allow for quick maffs through the C-Python interface.

## Task 1: Matrix Operations
The first portion of the project centered around writing the basic matrix functions (allocate, add, multiply, etc.) To start, I began with the extremely naive algorithms for each of the operations (individual cell additions for matrix add, the basic O(n^3) ijk loop for multiply, etc.). The set and get functions simply dug into the matrix data and found the address and value at the given index. These are equivalent to the dumbpy functions that the staff provided, allowing to initially test for correct mathematical operations regardless of speed.

Another large part of task 1 was to implement deallocate_matrix, which involved controlling memory operations and references for sliced matrices. This involved checking parent and child matrices and their individual ref_cnts, only deallocating the parent or child under certain conditions.

## Task 2: numc Setup File
This involved creating up the numc library setup file to be used across the C-Python interface. This was not too difficult, as it involved importing the setup and extension libraries that have already been established and correctly setting the modules and flags.

## Task 3: C-Python Interface
The interface is responsible for communication between the Python input, the C operations, and the Python output. By establishing a matrix structure (Matrix61c) and treating it as a PyObject, we can create matrix objects from the Python input, which are them subsequently passed into the C matrix functions written earlier. From there, the new data is then placed into a new Matrix61c object, which will then be presented as output.

Additionally, each matrix operation is mapped to their respective C matrix function as well as their respective PyNumberMethod/Python instance method. This means that using (ma1 + mat2) in python will execute Matrix61c_add operation within the interface, which then subsequently runs the matrix_add basic C function and runs the data back up for outputting.

## Task 4: Speedup
The final (and most important) portion of the project is the speedup of all my functions, which brought me back to the task 1 operations. Here, I used a variety of techniques I learned in class to attempt to speed up general operations, including Loop Unrolling (and tail case cleanup), SSE intrinsics, and OMP parallelism. For pretty much every operation, I used a combination of all 3, but will going into some detail on a select few.

**Get/Set**: These are extremely basic, and because the naive algorithm simply looks at an address value for an individual index, there really isn't much to do to improve it.

**Add/Sub/Neg/Abs/Fill**: These functions are a step above get and set, and so they require more work to be done as some calculations are needed, and thus some of the speedup techniques can be applied. The primary focus here was SSE intrinsics, which allowed me to conduct operations on 4 values at the same time. Thus, for all of these functions, I looped through the entire matrix 4 values (16 values using loop unrolling) at a time. Slap OMP on top of this, and this allowed for a general speedup of 5x for these simple arithmetic function.

**Multiply**: A step above is the multiply operation, which naively requires O(N^3) time and was probably the most difficult to implement. However, using a similar method to the arithmetic functions, I was able to loop (with parallelism) through the entire result matrix 4 rows at time, evaluating all the values in each cell of each row at the same time by unrolling ijk (which I rearranged to ikj for better cache access) 64 times (4 for each i, j, k). This resulted in a speedup of 275x (!!!) for matrix multiplications, which I gotta say is pretty sexy.

**Power**: A step above multiply is the power operation, which would naively conduct the multiply operation n times (where n = power). However, by implementing exponentiation by squaring, I only had to conduct the multiply operation log2(n) times, allowing for a much faster operation. Because multiply is already optimized and exponentiation by squaring is linear (cannot be parallelized), I cannot really add any spicy speedup tricks besides the optimal algorithm. However, from what I learned from testing, is that my matrix multiply is amazing at extremely large matrices (we're talking thousands of rows and columns) but is suboptimal on medium to small matrices. This led me to create an alternate matrix multiply which used the same concept as my normal multiply but with less overhead and "spiciness" in an attempt to appeal to smaller matrices. This alternate matrix multiply is then solely used in power, allowing me to achieve the power benchmark.

