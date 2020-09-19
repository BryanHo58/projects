"""
Feel free to add more test classes and/or tests that are not provided by the skeleton code!
Make sure you follow these naming conventions: https://docs.pytest.org/en/reorganize-docs/goodpractices.html#test-discovery
for your new tests/classes/python files or else they might be skipped.
"""
from utils import *
import numc as nc

"""
For each operation, you should write tests to test correctness on matrices of different sizes.
Hint: use rand_dp_nc_matrix to generate dumbpy and numc matrices with the same data and use
      cmp_dp_nc_matrix to compare the results
"""
class TestAddCorrectness:
    def test_small_add(self):
        mat1 = nc.Matrix(2, 2, [1, 2, 3, 4])
        mat2 = nc.Matrix(2, 2, [1, 2, 3, 4])
        mat3 = mat1 + mat2
        assert(mat3[0][0] == 2)
        assert(mat3[0][1] == 4)
        assert(mat3[1][0] == 6)
        assert(mat3[1][1] == 8)
        assert(mat3.shape == (2, 2))
        mat4 = nc.Matrix(1, 2, [1, 2])
        mat5 = nc.Matrix(2, 2, [1, 2, 3, 4])
        try:
            mat3 = mat4 + mat5
        except (TypeError):
            print("success add\n")

    def test_medium_add(self):
        # TODO: YOUR CODE HERE
        pass

    def test_medium_add(self):
        # TODO: YOUR CODE HERE
        mat1 = nc.Matrix(10000, 10000, 17);
        mat2 = nc.Matrix(10000, 10000, 17);
        mat3 = mat1 + mat2;
        assert(mat3[0][0] == 34)
        assert(mat3[9399][999] == 34)
        assert(mat3[9999][9999] == 34)


class TestSubCorrectness:
    def test_small_sub(self):
        mat1 = nc.Matrix(2, 2, [1, 2, 3, 4])
        mat2 = nc.Matrix(2, 2, [1, 2, 3, 4])
        mat3 = mat1 - mat2
        assert(mat3[0][0] == 0)
        assert(mat3[0][1] == 0)
        assert(mat3[1][0] == 0)
        assert(mat3[1][1] == 0)
        assert(mat3.shape == (2, 2))
        mat4 = nc.Matrix(1, 2, [1, 2])
        mat5 = nc.Matrix(2, 2, [1, 2, 3, 4])
        try:
            mat3 = mat4 + mat5
        except (TypeError):
            print("success sub\n")

    def test_medium_sub(self):
        # TODO: YOUR CODE HERE
        pass

    def test_large_sub(self):
        # TODO: YOUR CODE HERE
        mat1 = nc.Matrix(10000, 10000, 17);
        mat2 = nc.Matrix(10000, 10000, 16);
        mat3 = mat1 - mat2;
        assert(mat3[0][0] == 1)
        assert(mat3[9399][999] == 1)
        assert(mat3[9999][9999] == 1)

class TestAbsCorrectness:
    def test_small_abs(self):
        # TODO: YOUR CODE HERE
        mat1 = nc.Matrix(2, 2, [1, 2, 3, 4])
        mat2 = abs(mat1)
        assert(mat2[0][0] == 1)
        assert(mat2[0][1] == 2)
        assert(mat2[1][0] == 3)
        assert(mat2[1][1] == 4)
        assert(mat2.shape == (2, 2))
        mat3 = nc.Matrix(2, 2, [1, -2, 3, -4])
        mat4 = abs(mat3)
        assert(mat4[0][0] == 1)
        assert(mat4[0][1] == 2)
        assert(mat4[1][0] == 3)
        assert(mat4[1][1] == 4)
        assert(mat4.shape == (2, 2))

    def test_medium_abs(self):
        # TODO: YOUR CODE HERE
        pass

    def test_large_abs(self):
        # TODO: YOUR CODE HERE
        mat1 = nc.Matrix(10000, 10000, -17)
        mat2 = abs(mat1)
        assert(mat2[0][0] == 17)
        assert(mat2[0][5124] == 17)
        assert(mat2[1324][2344] == 17)
        assert(mat2[9999][9999] == 17)
        assert(mat2.shape == (10000, 10000))

class TestNegCorrectness:
    def test_small_neg(self):
        # TODO: YOUR CODE HERE
        mat1 = nc.Matrix(2, 2, [1, 2, 3, 4])
        mat2 = -mat1
        assert(mat2[0][0] == -1)
        assert(mat2[0][1] == -2)
        assert(mat2[1][0] == -3)
        assert(mat2[1][1] == -4)
        assert(mat2.shape == (2, 2))
        mat3 = nc.Matrix(2, 2, [1, -2, 3, -4])
        mat4 = -mat3
        assert(mat4[0][0] == -1)
        assert(mat4[0][1] == 2)
        assert(mat4[1][0] == -3)
        assert(mat4[1][1] == 4)
        assert(mat4.shape == (2, 2))

    def test_medium_neg(self):
        # TODO: YOUR CODE HERE
        pass

    def test_large_neg(self):
        # TODO: YOUR CODE HERE
        mat1 = nc.Matrix(10000, 10000, 17)
        mat2 = -mat1
        assert(mat2[0][0] == -17)
        assert(mat2[0][5124] == -17)
        assert(mat2[1324][2344] == -17)
        assert(mat2[9999][9999] == -17)
        assert(mat2.shape == (10000, 10000))

class TestMulCorrectness:
    def test_small_mul(self):
        mat1 = nc.Matrix(2, 2, [1, 2, 3, 4])
        mat2 = nc.Matrix(2, 2, [1, 2, 3, 4])
        mat3 = mat1 * mat2
        assert(mat3[0][0] == 7)
        assert(mat3[0][1] == 10)
        assert(mat3[1][0] == 15)
        assert(mat3[1][1] == 22)
        assert(mat3.shape == (2, 2))
        mat3 = mat3 * nc.Matrix(2, 2, [1, 0, 0, 1])
        assert(mat3[0][0] == 7)
        assert(mat3[0][1] == 10)
        assert(mat3[1][0] == 15)
        assert(mat3[1][1] == 22)

        mat4 = nc.Matrix(1, 2, [1, 2])
        mat5 = nc.Matrix(2, 2, [1, 2, 3, 4])
        mat6 = mat4 * mat5
        assert(mat6[0][0] == 7)
        assert(mat6[0][1] == 10)
        assert(mat6.shape == (1, 2))
        mat7 = nc.Matrix(1, 2, [1, 2])
        mat8 = nc.Matrix(1, 2, [1, 2])
        try:
            mat3 = mat7 * mat8
        except (TypeError):
            print("success mult\n")


    def test_medium_mul(self):
        # TODO: YOUR CODE HERE
        mat1 = nc.Matrix(2, 2, [1, 1, 1, 0])
        mat3 = mat1*mat1
        assert(mat3[0][0] == 2)
        assert(mat3[0][1] == 1)
        assert(mat3[1][0] == 1)
        assert(mat3[1][1] == 1)
        mat3 = nc.Matrix(2, 2, [2, 1, 1, 1])
        mat3 = mat3*mat3
        assert(mat3[0][0] == 5)
        assert(mat3[0][1] == 3)
        assert(mat3[1][0] == 3)
        assert(mat3[1][1] == 2)
        mat3 = mat3*mat3
        assert(mat3[0][0] == 34)
        assert(mat3[0][1] == 21)
        assert(mat3[1][0] == 21)
        assert(mat3[1][1] == 13)
        mat1 = nc.Matrix(2,2, [1,0,0,1])
        mat3 = nc.Matrix(2,2, [1,1,1,0])
        mat3 = mat3*mat1
        assert(mat3[0][0] == 1)
        assert(mat3[0][1] == 1)
        assert(mat3[1][0] == 1)
        assert(mat3[1][1] == 0)
        mat1 = nc.Matrix(2, 2, [1, 1, 1, 0])
        mat3 = nc.Matrix(2, 2, [1, 1, 1, 0])
        mat3 = mat3*mat1
        assert(mat3[0][0] == 2)
        assert(mat3[0][1] == 1)
        assert(mat3[1][0] == 1)
        assert(mat3[1][1] == 1)
    def test_large_mul(self):
        # TODO: YOUR CODE HERE
        z =12
        mat1 = nc.Matrix(z, z, 1)
        mat2 = nc.Matrix(z, z, 1)
        mat3 = mat1*mat2
        x = []
        for i in range(z):
            for j in range(z):
                assert(mat3[i][j] == z)

class TestPowCorrectness:
    def test_small_pow(self):
        # TODO: YOUR CODE HERE
        mat1 = nc.Matrix(2, 2, [1, 1, 1, 0])
        mat2 = mat1**0
        #assert(mat2 == (0,0))
        assert(mat2[0][0] == 1)
        assert(mat2[0][1] == 0)
        assert(mat2[1][0] == 0)
        assert(mat2[1][1] == 1)

        assert(mat2.shape == (2, 2))
        mat3 = mat1**1
        assert(mat3[0][0] == 1)
        assert(mat3[0][1] == 1)
        assert(mat3[1][0] == 1)
        assert(mat3[1][1] == 0)
        assert(mat3.shape == (2, 2))

        mat4 = mat1**3
        assert(mat4[0][0] == 3)
        assert(mat4[0][1] == 2)
        assert(mat4[1][0] == 2)
        assert(mat4[1][1] == 1)
        assert(mat4.shape == (2, 2))
        mat5 = mat1**10
        assert(mat5[0][0] == 89)
        assert(mat5[0][1] == 55)
        assert(mat5[1][0] == 55)
        assert(mat5[1][1] == 34)
        assert(mat5.shape == (2, 2))
        mat6 = mat1**30
        mat7 = mat1**100
        mat8 = mat1**200
        mat9 = mat1**3000
        assert(mat9.shape == (2, 2))
        mat10 = mat1**20
        assert(mat10[0][0] == 10946)
        assert(mat10[0][1] == 6765)
        assert(mat10[1][0] == 6765)
        assert(mat10[1][1] == 4181)
        mat10 = mat1**21
        assert(mat10[0][0] == 17711)
        assert(mat10[0][1] == 10946)
        assert(mat10[1][0] == 10946)
        assert(mat10[1][1] == 6765)

    def test_medium_pow(self):
        # TODO: YOUR CODE HERE
        pass

    def test_large_pow(self):
        # TODO: YOUR CODE HERE
        pass


class TestGetCorrectness:
    def test_get(self):
        # TODO: YOUR CODE HERE
        mat1 = nc.Matrix(2, 2, [1, 1, 1, 0])
        x = mat1.get(0, 0)
        y = mat1.get(0, 1)
        z = mat1.get(1, 0)
        a = mat1.get(1, 1)
        assert(x == 1)
        assert(y == 1)
        assert(z == 1)
        assert(a == 0)

class TestSetCorrectness:
    def test_set(self):
        # TODO: YOUR CODE HERE
        mat1 = nc.Matrix(2, 2, [1, 1, 1, 0])
        mat1.set(0, 0, 69.0)
        mat1.set(0, 1, 70.0)
        mat1.set(1, 0, 71)
        mat1.set(1, 1, 72)
        assert(mat1[0][0] == 69)
        assert(mat1[0][1] == 70)
        assert(mat1[1][0] == 71)
        assert(mat1[1][1] == 72)

class TestIndexCorrectness:
    def test_small_index(self):
        mat1 = nc.Matrix(3, 3)
        mat2 = nc.Matrix(3, 3, 1)
        mat3 = nc.Matrix([[1, 2, 3], [4, 5, 6]])
        mat4 = nc.Matrix(1, 2, [4, 5])
        #assert(mat1[0] == [[0.0], [0.0], [0.0]])
        assert(mat1[0][1] == 0)
        mat1[0][1] = 5
        #assert(mat1[0] == [[0.0], [0.0], [0.0]])
        mat1[0] = [4, 5, 6]
        #assert(mat1[0] == [[0.0], [0.0], [0.0]])
        mat2[1][1] = 2
        mat2_slice = mat2[0] # [[1.0], [1.0], [1.0]]
        mat2_slice[0] = 5

class TestFillCorrectness:
    def test_big_fill(self):
        mat1 = nc.Matrix(10000, 10000, 12)
        assert(mat1[0][0] == 12)
        assert(mat1[9399][999] == 12)
        assert(mat1[9999][9999] == 12)
