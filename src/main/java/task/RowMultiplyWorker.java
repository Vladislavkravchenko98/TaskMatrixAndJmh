package task;

// Создадим класс Thread, который реализует интерфейс Runnable
public class RowMultiplyWorker implements Runnable {

    private final int[][] result;
    private int[][] matrixA;
    private int[][] matrixB;
    private final int row;

    public RowMultiplyWorker(int[][] result, int[][] matrixA, int[][] matrixB, int row) {
        this.result = result;
        this.matrixA = matrixA;
        this.matrixB = matrixB;
        this.row = row;
    }

    @Override
    public void run() {
        for (int i = 0; i < matrixB[0].length; i++) {
            result[row][i] = 0;
            for (int j = 0; j < matrixA[row].length; j++) {
                result[row][i] += matrixA[row][j] * matrixB[j][i];

            }
        }
    }
}
