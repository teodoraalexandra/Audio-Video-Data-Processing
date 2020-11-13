package controller;

class MatrixOperations {
    // Used for Lab2 - classic multiplication for 2 matrices
    static int[][] multiplyMatrices(int[][] A, double[][] B) {
        int[][] result = new int[8][8];

        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++)
                result[i][j] = (int) (A[i][j] * B[i][j]);

        return result;
    }

    // Used for Lab2 - classic division between 2 matrices
    static int[][] divideMatrices(int[][] A, double[][] B) {
        int[][] result = new int[8][8];
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++) {
                result[i][j] = (int) (A[i][j] / B[i][j]);
            }

        return result;
    }
}
