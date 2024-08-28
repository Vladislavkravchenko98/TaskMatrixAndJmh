package task;

import java.util.ArrayList;
import java.util.List;

public class ParallelThreadsCreator {
    // создадим класс для создания 10 потоков одновременно, потому что если мы создадим 2000 потоков для матрицы 2000x2000,
    // то приложение зависнет. Поэтому мы будем использовать группу из 10 потоков и позволим им завершить работу, а затем
    // снова инициализируем следующие 10 потоков до тех пор, пока не будет завершено умножение каждой строки.
    public static void multiply(int[][] matrixA, int[][] matrixB, int[][] matrixResult) {
        List<Thread> threads = new ArrayList<>();
        int rows = matrixA.length;
        for (int i = 0; i < rows; i++) {
            RowMultiplyWorker task = new RowMultiplyWorker(matrixResult, matrixA, matrixB, i);
            Thread thread = new Thread(task);  thread.start();  threads.add(thread);
            if (threads.size() % 10 == 0) {
                waitForThreads(threads);
            }
        }
    }

    private static void waitForThreads(List<Thread> threads) {
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        threads.clear();
    }
}
