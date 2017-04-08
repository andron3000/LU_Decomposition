package sample;

public class WorkerThread extends Thread{
    private int row;
    private int col;
    private int [][] A;
    private int [][] L;
    private int [][] U;

    public WorkerThread(int row, int col, int[][] A,
                        int[][] L, int[][] U) {
        this.row = row;
        this.col = col;
        this.A = A;
        this.L = L;
        this.U = U;
    }

    public void run() {
        for (int k = 0; k < A[0].length; k++) {
            U[row][col] += A[row][k] * L[k][col];
        }
    }
}