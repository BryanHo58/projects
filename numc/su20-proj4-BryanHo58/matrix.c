#include "matrix.h"
#include <stddef.h>
#include <stdio.h>
#include <stdlib.h>
#include <omp.h>

// Include SSE intrinsics
#if defined(_MSC_VER)
#include <intrin.h>
#elif defined(__GNUC__) && (defined(__x86_64__) || defined(__i386__))
#include <immintrin.h>
#include <x86intrin.h>
#endif

/* Below are some intel intrinsics that might be useful
 * void _mm256_storeu_pd (double * mem_addr, __m256d a)
 * __m256d _mm256_set1_pd (double a)
 * __m256d _mm256_set_pd (double e3, double e2, double e1, double e0)
 * __m256d _mm256_loadu_pd (double const * mem_addr)
 * __m256d _mm256_add_pd (__m256d a, __m256d b)
 * __m256d _mm256_sub_pd (__m256d a, __m256d b)
 * __m256d _mm256_fmadd_pd (__m256d a, __m256d b, __m256d c)
 * __m256d _mm256_mul_pd (__m256d a, __m256d b)
 * __m256d _mm256_cmp_pd (__m256d a, __m256d b, const int imm8)
 * __m256d _mm256_and_pd (__m256d a, __m256d b)
 * __m256d _mm256_max_pd (__m256d a, __m256d b)
*/

/* Generates a random double between low and high */
double rand_double(double low, double high) {
    double range = (high - low);
    double div = RAND_MAX / range;
    return low + (rand() / div);
}

/* Generates a random matrix */
void rand_matrix(matrix *result, unsigned int seed, double low, double high) {
    srand(seed);
    for (int i = 0; i < result->rows; i++) {
        for (int j = 0; j < result->cols; j++) {
            set(result, i, j, rand_double(low, high));
        }
    }
}

/*
 * Allocates space for a matrix struct pointed to by the double pointer mat with
 * `rows` rows and `cols` columns. You should also allocate memory for the data array
 * and initialize all entries to be zeros. `parent` should be set to NULL to indicate that
 * this matrix is not a slice. You should also set `ref_cnt` to 1.
 * You should return -1 if either `rows` or `cols` or both have invalid values, or if any
 * call to allocate memory in this function fails. Return 0 upon success.
 */
int allocate_matrix(matrix **mat, int rows, int cols) {
    /* TODO: YOUR CODE HERE */
    if ((rows <= 0) || (cols <= 0)){
      PyErr_SetString(PyExc_TypeError, "Invalid dimensions");
      return -1;
    }
    *mat = (matrix *) malloc(sizeof(matrix));
    // check for malloc error, throw reference error
    if (*mat == NULL) {
      return -1;
    }
    (*mat)->rows = rows;
    (*mat)->cols = cols;
    (*mat)->data = (double *) calloc(rows * cols, sizeof(double));
    if (((*mat)->data) == NULL) {
      return -1;
    }
    (*mat)->ref_cnt = 1;
    (*mat)->parent = NULL;
    return 0;
}

/*
 * Allocates space for a matrix struct pointed to by `mat` with `rows` rows and `cols` columns.
 * Its data should point to the `offset`th entry of `from`'s data (you do not need to allocate memory)
 * for the data field. `parent` should be set to `from` to indicate this matrix is a slice of `from`.
 * You should return -1 if either `rows` or `cols` or both are non-positive or if any
 * call to allocate memory in this function fails. Return 0 upon success.
 */
int allocate_matrix_ref(matrix **mat, matrix *from, int offset, int rows, int cols) {
    /* TODO: YOUR CODE HERE */
    if ((rows <= 0) || (cols <= 0)){
      PyErr_SetString(PyExc_TypeError, "Invalid dimensions");
      return -1;
    }
    *mat = (matrix *) malloc(sizeof(matrix));
    // check for malloc error, throw reference error
    if (*mat == NULL) {
      return -1;
    }
    (*mat)->rows = rows;
    (*mat)->cols = cols;
    (*mat)->data = from->data + offset;
    from->ref_cnt = from->ref_cnt + 1;
    (*mat)->ref_cnt = from->ref_cnt;
    (*mat)->parent = from;
    return 0;
}

/*
 * You need to make sure that you only free `mat->data` if `mat` is not a slice and has no existing slices,
 * or if `mat` is the last existing slice of its parent matrix and its parent matrix has no other references
 * (including itself). You cannot assume that mat is not NULL.
 */
void deallocate_matrix(matrix *mat) {
    /* TODO: YOUR CODE HERE */
    if (mat == NULL){
      return;
    }
    if (mat->parent == NULL){
      (mat->ref_cnt)--;
      if (mat->ref_cnt == 0){
        free(mat->data);
        free(mat);
      }
      return;
    }
    else {
      (mat->parent->ref_cnt)--;
      if (mat->parent->ref_cnt == 0){
        free(mat->parent->data);
        free(mat->parent);
      }
    }
    free(mat);
    return;
}

/*
 * Returns the double value of the matrix at the given row and column.
 * You may assume `row` and `col` are valid.
 */
double get(matrix *mat, int row, int col) {
    /* TODO: YOUR CODE HERE */
    double *result = mat->data + (row * mat->cols) + col;
    return *result;
}

/*
 * Sets the value at the given row and column to val. You may assume `row` and
 * `col` are valid
 */
void set(matrix *mat, int row, int col, double val) {
    /* TODO: YOUR CODE HERE */
    double *index = mat->data + (row * mat->cols) + col;
    *index = val;
}

/*
 * Sets all entries in mat to val
 */
void fill_matrix(matrix *mat, double val) {
    /* TODO: YOUR CODE HERE */
    if (mat == NULL){
      return;
    }
    __m256d value = _mm256_set1_pd(val);
    int total = mat->rows * mat->cols;
    #pragma omp parallel for
      for (int i = 0; i < total / 16 * 16; i += 16){
        _mm256_storeu_pd(mat->data + i + 0, value);
        _mm256_storeu_pd(mat->data + i + 4, value);
        _mm256_storeu_pd(mat->data + i + 8, value);
        _mm256_storeu_pd(mat->data + i + 12, value);
      }
    for (int i = total / 16 * 16; i < total; i++){
      *(mat->data + i) = val;
    }
}

/*
 * Store the result of adding mat1 and mat2 to `result`.
 * Return 0 upon success and a nonzero value upon failure.
 */
int add_matrix(matrix *result, matrix *mat1, matrix *mat2) {
    /* TODO: YOUR CODE HERE */
    if ((mat1 == NULL) || (mat2 == NULL) || (result == NULL)){
      return 69;
    }
    if ((mat1->rows != mat2->rows) || (mat1->cols != mat2->cols)){
      return 69;
    }
    if ((mat1->rows != result->rows) || (mat1->cols != result->cols)){
      return 69;
    }
    int total = mat1->rows * mat1->cols;
    __m256d sum1, sum2, sum3, sum4;
    #pragma omp parallel for private(sum1, sum2, sum3, sum4)
      for (int i = 0; i < total / 16 * 16; i += 16){
        sum1 = _mm256_add_pd(_mm256_loadu_pd(mat1->data + i + 0), _mm256_loadu_pd(mat2->data + i + 0));
        sum2 = _mm256_add_pd(_mm256_loadu_pd(mat1->data + i + 4), _mm256_loadu_pd(mat2->data + i + 4));
        sum3 = _mm256_add_pd(_mm256_loadu_pd(mat1->data + i + 8), _mm256_loadu_pd(mat2->data + i + 8));
        sum4 = _mm256_add_pd(_mm256_loadu_pd(mat1->data + i + 12), _mm256_loadu_pd(mat2->data + i + 12));
        _mm256_storeu_pd(result->data + i + 0, sum1);
        _mm256_storeu_pd(result->data + i + 4, sum2);
        _mm256_storeu_pd(result->data + i + 8, sum3);
        _mm256_storeu_pd(result->data + i + 12, sum4);
      }
    for (int i = total / 16 * 16; i < total; i++){
      *(result->data + i) = *(mat1->data + i) + *(mat2->data + i);
    }
    return 0;

}

/*
 * Store the result of subtracting mat2 from mat1 to `result`.
 * Return 0 upon success and a nonzero value upon failure.
 */
int sub_matrix(matrix *result, matrix *mat1, matrix *mat2) {
    /* TODO: YOUR CODE HERE */
    if ((mat1 == NULL) || (mat2 == NULL) || (result == NULL)){
      return 69;
    }
    if ((mat1->rows != mat2->rows) || (mat1->cols != mat2->cols)){
      return 69;
    }
    if ((mat1->rows != result->rows) || (mat1->cols != result->cols)){
      return 69;
    }
    int total = mat1->rows * mat1->cols;
    __m256d sum1, sum2, sum3, sum4;
    #pragma omp parallel for private(sum1, sum2, sum3, sum4)
      for (int i = 0; i < total / 16 * 16; i += 16){
        sum1 = _mm256_sub_pd(_mm256_loadu_pd(mat1->data + i + 0), _mm256_loadu_pd(mat2->data + i + 0));
        sum2 = _mm256_sub_pd(_mm256_loadu_pd(mat1->data + i + 4), _mm256_loadu_pd(mat2->data + i + 4));
        sum3 = _mm256_sub_pd(_mm256_loadu_pd(mat1->data + i + 8), _mm256_loadu_pd(mat2->data + i + 8));
        sum4 = _mm256_sub_pd(_mm256_loadu_pd(mat1->data + i + 12), _mm256_loadu_pd(mat2->data + i + 12));
        _mm256_storeu_pd(result->data + i + 0, sum1);
        _mm256_storeu_pd(result->data + i + 4, sum2);
        _mm256_storeu_pd(result->data + i + 8, sum3);
        _mm256_storeu_pd(result->data + i + 12, sum4);
      }
    for (int i = total / 16 * 16; i < total; i++){
      *(result->data + i) = *(mat1->data + i) - *(mat2->data + i);
    }
    return 0;
}

/*
 * Store the result of multiplying mat1 and mat2 to `result`.
 * Return 0 upon success and a nonzero value upon failure.
 * Remember that matrix multiplication is not the same as multiplying individual elements.
 */
 int mul_matrix(matrix *result, matrix *mat1, matrix *mat2) {
    /* TODO: YOUR CODE HERE */
    if ((mat1 == NULL) || (mat2 == NULL) || (result == NULL)){
      return 69;
    }
    if ((mat1->cols != mat2->rows) || (result->rows != mat1->rows) || (result->cols != mat2->cols)){
      return 69;
    }
    fill_matrix(result, 0);
    int bigI = mat1->rows;
    int bigJ = mat2->cols;
    int bigK = mat1->cols;
    double *A = mat1->data;
    double *B = mat2->data;
    double *C = result->data;
    __m256d b1, b2, b3, b4;
    __m256d s1, s2, s3, s4;
    __m256d p1, p2, p3, p4;
    __m256d q1, q2, q3, q4;
    __m256d r1, r2, r3, r4;
    __m256d t1, t2, t3, t4;
    double mult, sum;
    double m1, m2, m3, m4;
    int i, j, k, l;
    __m256d entries1[bigJ/4], entries2[bigJ/4], entries3[bigJ/4], entries4[bigJ/4];

    #pragma omp parallel for private(b1, b2, b3, b4, s1, s2, s3, s4, p1, p2, p3, p4, i, j, k, mult, sum, l, entries1, entries2, entries3, entries4, q1, q2, q3, q4, r1, r2, r3, r4, t1, t2, t3, t4, m1, m2, m3, m4)
      for (i = 0; i < bigI / 4 * 4; i += 4) {
        memset(entries1, 0, bigJ/4*sizeof(__m256d));
        memset(entries2, 0, bigJ/4*sizeof(__m256d));
        memset(entries3, 0, bigJ/4*sizeof(__m256d));
        memset(entries4, 0, bigJ/4*sizeof(__m256d));

        for (k = 0; k < bigK / 4 * 4; k += 4) { // goes through rows mat2 and cols mat1
          p1 = _mm256_set1_pd(*(A + k + (i+0)*bigK));
          p2 = _mm256_set1_pd(*(A + k + (i+1)*bigK));
          p3 = _mm256_set1_pd(*(A + k + (i+2)*bigK));
          p4 = _mm256_set1_pd(*(A + k + (i+3)*bigK));

          q1 = _mm256_set1_pd(*(A + k+1 + (i+0)*bigK));
          q2 = _mm256_set1_pd(*(A + k+1 + (i+1)*bigK));
          q3 = _mm256_set1_pd(*(A + k+1 + (i+2)*bigK));
          q4 = _mm256_set1_pd(*(A + k+1 + (i+3)*bigK));

          r1 = _mm256_set1_pd(*(A + k+2 + (i+0)*bigK));
          r2 = _mm256_set1_pd(*(A + k+2 + (i+1)*bigK));
          r3 = _mm256_set1_pd(*(A + k+2 + (i+2)*bigK));
          r4 = _mm256_set1_pd(*(A + k+2 + (i+3)*bigK));

          t1 = _mm256_set1_pd(*(A + k+3 + (i+0)*bigK));
          t2 = _mm256_set1_pd(*(A + k+3 + (i+1)*bigK));
          t3 = _mm256_set1_pd(*(A + k+3 + (i+2)*bigK));
          t4 = _mm256_set1_pd(*(A + k+3 + (i+3)*bigK));

          for (j = 0; j < bigJ / 4* 4; j += 4){
            b1 = _mm256_loadu_pd(B + j + (k+0)*bigJ);
            b2 = _mm256_loadu_pd(B + j + (k+1)*bigJ);
            b3 = _mm256_loadu_pd(B + j + (k+2)*bigJ);
            b4 = _mm256_loadu_pd(B + j + (k+3)*bigJ);

            s1 = entries1[j/4];
            s2 = entries2[j/4];
            s3 = entries3[j/4];
            s4 = entries4[j/4];

            s1 = _mm256_fmadd_pd(p1, b1, s1);
            s2 = _mm256_fmadd_pd(p2, b1, s2);
            s3 = _mm256_fmadd_pd(p3, b1, s3);
            s4 = _mm256_fmadd_pd(p4, b1, s4);

            s1 = _mm256_fmadd_pd(q1, b2, s1);
            s2 = _mm256_fmadd_pd(q2, b2, s2);
            s3 = _mm256_fmadd_pd(q3, b2, s3);
            s4 = _mm256_fmadd_pd(q4, b2, s4);

            s1 = _mm256_fmadd_pd(r1, b3, s1);
            s2 = _mm256_fmadd_pd(r2, b3, s2);
            s3 = _mm256_fmadd_pd(r3, b3, s3);
            s4 = _mm256_fmadd_pd(r4, b3, s4);

            s1 = _mm256_fmadd_pd(t1, b4, s1);
            s2 = _mm256_fmadd_pd(t2, b4, s2);
            s3 = _mm256_fmadd_pd(t3, b4, s3);
            s4 = _mm256_fmadd_pd(t4, b4, s4);

            entries1[j/4] = s1;
            entries2[j/4] = s2;
            entries3[j/4] = s3;
            entries4[j/4] = s4;
          }
          for (l = 0; l < 4; l++){
            m1 = get(mat1, i+l, k+0);
            m2 = get(mat1, i+l, k+1);
            m3 = get(mat1, i+l, k+2);
            m4 = get(mat1, i+l, k+3);
            for (j = bigJ / 4 * 4; j < bigJ; j++) {
              sum = get(result, i + l, j);
              mult = get(mat2, k+0, j) * m1;
              sum += mult;
              mult = get(mat2, k+1, j) * m2;
              sum += mult;
              mult = get(mat2, k+2, j) * m3;
              sum += mult;
              mult = get(mat2, k+3, j) * m4;
              sum += mult;
              set(result, i + l, j, sum);
            }
          }
        }
        for (k = bigK / 4 * 4; k < bigK; k++) {
          p1 = _mm256_set1_pd(*(A + k + (i+0)*bigK));
          p2 = _mm256_set1_pd(*(A + k + (i+1)*bigK));
          p3 = _mm256_set1_pd(*(A + k + (i+2)*bigK));
          p4 = _mm256_set1_pd(*(A + k + (i+3)*bigK));
          for (j = 0; j < bigJ / 4* 4; j += 4){
            b1 = _mm256_loadu_pd(B + j + (k+0)*bigJ);

            s1 = entries1[j/4];
            s2 = entries2[j/4];
            s3 = entries3[j/4];
            s4 = entries4[j/4];

            s1 = _mm256_fmadd_pd(p1, b1, s1);
            s2 = _mm256_fmadd_pd(p2, b1, s2);
            s3 = _mm256_fmadd_pd(p3, b1, s3);
            s4 = _mm256_fmadd_pd(p4, b1, s4);

            entries1[j/4] = s1;
            entries2[j/4] = s2;
            entries3[j/4] = s3;
            entries4[j/4] = s4;
          }
          for (l = 0; l < 4; l++){
            m1 = get(mat1, i+l, k);
            for (j = bigJ / 4 * 4; j < bigJ; j++) {
              sum = get(result, i + l, j);
              mult =  m1 * get(mat2, k, j) ;
              sum += mult;
              set(result, i + l, j, sum);
            }
          }
        }
        for (j = 0; j < bigJ/4 *4; j+= 4 ){
          _mm256_storeu_pd(C + j + (i+0)*bigJ, entries1[j/4]);
          _mm256_storeu_pd(C + j + (i+1)*bigJ, entries2[j/4]);
          _mm256_storeu_pd(C + j + (i+2)*bigJ, entries3[j/4]);
          _mm256_storeu_pd(C + j + (i+3)*bigJ, entries4[j/4]);
        }
      }
    #pragma omp parallel for private(i, j, k, mult, sum, m1)
      for (i = bigI / 4 * 4; i < bigI; i++){
        for (k = 0; k < bigK; k++){
          m1 = get(mat1, i, k);
          for (j = 0; j < bigJ; j++){
            mult = m1 * get(mat2, k, j);
            sum = get(result, i, j) + mult;
            set(result, i, j, sum);
          }
        }
      }
    return 0;
}

/*
 * Store the result of raising mat to the (pow)th power to `result`.
 * Return 0 upon success and a nonzero value upon failure.
 * Remember that pow is defined with matrix multiplication, not element-wise multiplication.
 */
int pow_matrix(matrix *result, matrix *mat, int pow) {
    /* TODO: YOUR CODE HERE */
    if ((mat == NULL) || (result == NULL)){
      return 69;
    }
    if ((mat->rows != mat->cols) || (result->rows != result->cols) || (mat->rows != result->rows) || (mat->cols != result->cols)){
      return 69;
    }
    if (pow < 0) {
      return 69;
    }
    int total = mat->rows * mat->cols;
    matrix *a = NULL;
    matrix *b = NULL;
    matrix *c = NULL;
    matrix *d = NULL;
    allocate_matrix(&b, result->rows, result->cols);
    fill_matrix(b, 0);
    #pragma omp parallel for
      for (int i = 0; i < result->rows / 4 * 4; i += 4) {
        set(b, i + 0, i + 0, 1);
        set(b, i + 1, i + 1, 1);
        set(b, i + 2, i + 2, 1);
        set(b, i + 3, i + 3, 1);
      }
    for (int i = result->rows / 4 * 4; i < result->rows; i++) {
      set(b, i, i, 1);
    }
    if (pow == 0){

    } else {
      allocate_matrix(&a, result->rows, result->cols);
      allocate_matrix(&c, result->rows, result->cols);
      #pragma omp parallel for
        for (int i = 0; i < total / 16 * 16; i += 16){
          _mm256_storeu_pd(a->data + i + 0, _mm256_loadu_pd(mat->data + i + 0));
          _mm256_storeu_pd(a->data + i + 4, _mm256_loadu_pd(mat->data + i + 4));
          _mm256_storeu_pd(a->data + i + 8, _mm256_loadu_pd(mat->data + i + 8));
          _mm256_storeu_pd(a->data + i + 12, _mm256_loadu_pd(mat->data + i + 12));
        }
      for (int i = total / 16 * 16; i < total; i++){
        *(a->data + i) = *(mat->data + i);
      }
      int current = 0;
      while (pow != 0){
        current = pow % 2;
        if (current == 1) {
          alt_mul_matrix(c, b, a);
          d = b;
          b = c;
          c = d;
        }
        pow = pow >> 1;
        if (pow != 0) {
          alt_mul_matrix(c, a, a);
          d = a;
          a = c;
          c = d;
        }
      }
    }
    result->data = b->data;
    return 0;
}

/*
 * Store the result of element-wise negating mat's entries to `result`.
 * Return 0 upon success and a nonzero value upon failure.
 */
int neg_matrix(matrix *result, matrix *mat) {
    /* TODO: YOUR CODE HERE */
    if ((mat == NULL) || (result == NULL)){
      return 69;
    }
    if ((mat->rows != result->rows) || (mat->cols != result->cols)) {
      return 69;
    }
    __m256d neg = _mm256_set1_pd(-1);
    int total = mat->rows * mat->cols;
    __m256d op1, op2, op3, op4;
    #pragma omp parallel for private(op1, op2, op3, op4)
      for (int i = 0; i < total / 16 * 16; i += 16){
        op1 = _mm256_mul_pd(_mm256_loadu_pd(mat->data + i + 0), neg);
        op2 = _mm256_mul_pd(_mm256_loadu_pd(mat->data + i + 4), neg);
        op3 = _mm256_mul_pd(_mm256_loadu_pd(mat->data + i + 8), neg);
        op4 = _mm256_mul_pd(_mm256_loadu_pd(mat->data + i + 12), neg);
        _mm256_storeu_pd(result->data + i + 0, op1);
        _mm256_storeu_pd(result->data + i + 4, op2);
        _mm256_storeu_pd(result->data + i + 8, op3);
        _mm256_storeu_pd(result->data + i + 12, op4);
      }
    for (int i = total / 16 * 16; i < total; i++){
      *(result->data + i) = *(mat->data + i) * -1;
    }
    return 0;
}

/*
 * Store the result of taking the absolute value element-wise to `result`.
 * Return 0 upon success and a nonzero value upon failure.
 */
int abs_matrix(matrix *result, matrix *mat) {
    /* TODO: YOUR CODE HERE */
    if ((mat == NULL) || (result == NULL)){
      return 69;
    }
    if ((mat->rows != result->rows) || (mat->cols != result->cols)) {
      return 69;
    }
    __m256d zero = _mm256_set1_pd(0);
    int total = mat->rows * mat->cols;
    __m256d op1, op2, op3, op4;
    __m256d og1, og2, og3, og4;
    #pragma omp parallel for private(op1, op2, op3, op4, og1, og2, og3, og4)
      for (int i = 0; i < total / 16 * 16; i += 16){
        og1 = _mm256_loadu_pd(mat->data + i + 0);
        op1 = _mm256_max_pd(_mm256_sub_pd(zero, og1), og1);
        og2 = _mm256_loadu_pd(mat->data + i + 4);
        op2 = _mm256_max_pd(_mm256_sub_pd(zero, og2), og2);
        og3 = _mm256_loadu_pd(mat->data + i + 8);
        op3 = _mm256_max_pd(_mm256_sub_pd(zero, og3), og3);
        og4 = _mm256_loadu_pd(mat->data + i + 12);
        op4 = _mm256_max_pd(_mm256_sub_pd(zero, og4), og4);
        _mm256_storeu_pd(result->data + i + 0, op1);
        _mm256_storeu_pd(result->data + i + 4, op2);
        _mm256_storeu_pd(result->data + i + 8, op3);
        _mm256_storeu_pd(result->data + i + 12, op4);
      }
    for (int i = total / 16 * 16; i < total; i++){
      double value = *(mat->data + i);
      *(result->data + i) = (value > 0) ? value : -value;
    }//
    return 0;
}


int alt_mul_matrix(matrix *result, matrix *mat1, matrix *mat2) {
    /* TODO: YOUR CODE HERE */
    if ((mat1 == NULL) || (mat2 == NULL) || (result == NULL)){
      return 69;
    }
    if ((mat1->cols != mat2->rows) || (result->rows != mat1->rows) || (result->cols != mat2->cols)){
      return 69;
    }
    fill_matrix(result, 0);
    int bigI = mat1->rows;
    int bigJ = mat2->cols;
    int bigK = mat1->cols;
    double *A = mat1->data;
    double *B = mat2->data;
    double *C = result->data;
    __m256d b1, b2, b3, b4;
    __m256d s1, s2;
    __m256d p1, p2;
    __m256d q1, q2;
    __m256d r1, r2;
    __m256d t1, t2;
    double mult, sum;
    double m1, m2, m3, m4;
    int i, j, k, l;
    __m256d entries1[bigJ/4], entries2[bigJ/4];
    int check;
    int loop;
    int forL = bigI / 2 * 2;

    #pragma omp parallel for private(loop, check, b1, b2, b3, b4, s1, s2, p1, p2, i, j, k, mult, sum, l, entries1, entries2, q1, q2, r1, r2, t1, t2, m1, m2, m3, m4)
      for (i = 0; i <= forL; i += 2) {
        check = 1;
        loop = 2;
        if (i == forL) {
          if (i == bigI) {
            continue;
          } else {
            check = 0;
            loop = 1;
          }
        }
        memset(entries1, 0, bigJ/4*sizeof(__m256d));
        if (check) {
          memset(entries2, 0, bigJ/4*sizeof(__m256d));
        }

        for (k = 0; k < bigK / 4 * 4; k += 4) { // goes through rows mat2 and cols mat1
          p1 = _mm256_set1_pd(*(A + k + (i+0)*bigK));

          q1 = _mm256_set1_pd(*(A + k+1 + (i+0)*bigK));

          r1 = _mm256_set1_pd(*(A + k+2 + (i+0)*bigK));

          t1 = _mm256_set1_pd(*(A + k+3 + (i+0)*bigK));

          if (check) {
            p2 = _mm256_set1_pd(*(A + k + (i+1)*bigK));
            q2 = _mm256_set1_pd(*(A + k+1 + (i+1)*bigK));
            r2 = _mm256_set1_pd(*(A + k+2 + (i+1)*bigK));
            t2 = _mm256_set1_pd(*(A + k+3 + (i+1)*bigK));
          }

          for (j = 0; j < bigJ / 4* 4; j += 4){
            b1 = _mm256_loadu_pd(B + j + (k+0)*bigJ);
            b2 = _mm256_loadu_pd(B + j + (k+1)*bigJ);
            b3 = _mm256_loadu_pd(B + j + (k+2)*bigJ);
            b4 = _mm256_loadu_pd(B + j + (k+3)*bigJ);

            s1 = entries1[j/4];


            s1 = _mm256_fmadd_pd(p1, b1, s1);


            s1 = _mm256_fmadd_pd(q1, b2, s1);


            s1 = _mm256_fmadd_pd(r1, b3, s1);


            s1 = _mm256_fmadd_pd(t1, b4, s1);


            entries1[j/4] = s1;


            if (check) {
              s2 = entries2[j/4];
              s2 = _mm256_fmadd_pd(p2, b1, s2);
              s2 = _mm256_fmadd_pd(q2, b2, s2);
              s2 = _mm256_fmadd_pd(r2, b3, s2);
              s2 = _mm256_fmadd_pd(t2, b4, s2);
              entries2[j/4] = s2;
            }
          }
          for (l = 0; l < loop; l++){
            m1 = get(mat1, i+l, k+0);
            m2 = get(mat1, i+l, k+1);
            m3 = get(mat1, i+l, k+2);
            m4 = get(mat1, i+l, k+3);
            for (j = bigJ / 4 * 4; j < bigJ; j++) {
              sum = get(result, i + l, j);
              mult = get(mat2, k+0, j) * m1;
              sum += mult;
              mult = get(mat2, k+1, j) * m2;
              sum += mult;
              mult = get(mat2, k+2, j) * m3;
              sum += mult;
              mult = get(mat2, k+3, j) * m4;
              sum += mult;
              set(result, i + l, j, sum);
            }
          }
        }
        for (k = bigK / 4 * 4; k < bigK; k++) {
          p1 = _mm256_set1_pd(*(A + k + (i+0)*bigK));
          if (check) {
            p2 = _mm256_set1_pd(*(A + k + (i+1)*bigK));
          }
          for (j = 0; j < bigJ / 4* 4; j += 4){
            b1 = _mm256_loadu_pd(B + j + (k+0)*bigJ);

            s1 = entries1[j/4];


            s1 = _mm256_fmadd_pd(p1, b1, s1);


            entries1[j/4] = s1;


            if (check) {
              s2 = entries2[j/4];
              s2 = _mm256_fmadd_pd(p2, b1, s2);
              entries2[j/4] = s2;
            }
          }
          for (l = 0; l < loop; l++){
            m1 = get(mat1, i+l, k);
            for (j = bigJ / 4 * 4; j < bigJ; j++) {
              sum = get(result, i + l, j);
              mult =  m1 * get(mat2, k, j) ;
              sum += mult;
              set(result, i + l, j, sum);
            }
          }
        }
        for (j = 0; j < bigJ/4 *4; j+= 4 ){
          _mm256_storeu_pd(C + j + (i+0)*bigJ, entries1[j/4]);
          if (check){
            _mm256_storeu_pd(C + j + (i+1)*bigJ, entries2[j/4]);
          }
        }
      }

    return 0;
}
