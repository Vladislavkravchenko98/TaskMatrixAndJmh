package task;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import java.util.Date;
import java.util.concurrent.TimeUnit;

//@BenchmarkMode — определяем режим измерения производительности. Например, Mode.Throughput измеряет количество
// операций в единицу времени, а Mode.AverageTime — средняя продолжительность выполнения метода.
//
//@OutputTimeUnit — задаём единицу измерения времени для результатов бенчмарка.
//
//@Warmup — указываем количество прогревочных итераций и их продолжительность.
//
//@Measurement — определяем количество измерительных итераций и их продолжительность.
//
//@Fork — указываем нужное количестве разветвлений (повторных выполнений) для каждого бенчмарка.
//
//@Threads — определяем количество потоков, которые будут использоваться для выполнения бенчмарка.
//
//@Timeout — максимальная продолжительность выполнения для каждой итерации бенчмарка.

//@State(Scope.Thread)
public class Matrix {

    public static int[][] matrixA;
    public static int[][] matrixB;

    public Matrix() {
        //var rows = (int) (Math.random() * 5) + 1;
        //var cols = (int) (Math.random() * 5) + 1;
        var rows = 2000;
        var cols = 2000;
        matrixA = inputMatrix(rows, cols);
        matrixB = inputMatrix(cols, rows);
    }

    public static void main(String[] args) throws Exception {
        //var rows = 2000;
        //var cols = 2000;
        //matrixA = inputMatrix(rows, cols);
        //matrixB = inputMatrix(cols, rows);

        /*matrixA = new int[][] {
                {1, 2},
                {-3, 2}
        };
        matrixB = new int[][] {
                {-4, 1, 3},
                {1, 3, -2}
        }; */

        Options opt = new OptionsBuilder()
                .include(Matrix.class.getSimpleName())
                //.warmupIterations(3).measurementIterations(5).forks(1)
                .build();

        new Runner(opt).run();

        //usualMultiplication();
        //optimizedMultiplication();
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.MILLISECONDS)
    @Measurement(iterations = 3, time = 1, timeUnit = TimeUnit.MILLISECONDS)
    @Fork(value = 2, warmups = 1)
    //@Threads(4)
    // Обычное умножение матриц
    public static void usualMultiplication() {
        Date start = new Date();
        var matrixResult = new int[matrixA.length][matrixB[0].length];
        for (int i = 0; i < matrixA.length; i++) {
            for (int j = 0; j < matrixB[0].length; j++) {
                for (int k = 0; k < matrixB.length; k++) {
                    matrixResult[i][j] += matrixA[i][k] * matrixB[k][j];
                }
            }
        }
        Date end = new Date();
        System.out.println("\n usual Time taken in milli seconds: " + (end.getTime() - start.getTime()));
        //printMatrix(matrixResult);
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.MILLISECONDS)
    @Measurement(iterations = 3, time = 1, timeUnit = TimeUnit.MILLISECONDS)
    @Fork(value = 2, warmups = 1)
    //@Threads(4)
    // Оптимизированное умножение матриц
    public static void optimizedMultiplication() {
        Date start = new Date();
        var transposedB = transposition(matrixB);
        var matrixResult = new int[matrixA.length][transposedB.length];
        for (int i = 0; i < matrixA.length; i++) {
            for (int j = 0; j < transposedB.length; j++) {
                for (int k = 0; k < matrixA[0].length; k++) {
                    matrixResult[i][j] += matrixA[i][k] * transposedB[j][k];
                }
            }
        }
        Date end = new Date();
        System.out.println("\n optimized Time taken in milli seconds: " + (end.getTime() - start.getTime()));
        //printMatrix(matrixResult);
    }


    public static int[][] transposition(int[][]matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        int[][] transposed = new int[cols][rows];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                transposed[j][i] = matrix[i][j];
            }
        }
        return transposed;
     }

    public static int[][] inputMatrix(int rows, int cols) {
        var matrix = new int[rows][cols];
        for (var i = 0; i < rows; i++) {
            for (var j = 0; j < cols; j++) {
                matrix[i][j] = (int)(Math.random()*6)+1;
            }
        }
        return matrix;
    }

    //Код для выведения матриц
    public static void printMatrix(int[][] matrix) {
        for (int i = 0; i< matrix.length; i++) {
            System.out.println("");
            for (int j = 0; j < matrix[0].length; j++) {
                System.out.print(matrix[i][j] + " ");
            }
        }
        System.out.println("");
    }

}

/*
------------------------------------------------------------
!!!!!!!!!!!!!!!!!!!!!!!!Для матриц размером 2000x2000!!!!!!!!!!!!!!!!!!!!!!!
 usual Time taken in milli seconds: 51319
 optimized Time taken in milli seconds: 4016
------- usual with JMH (2000x2000)
# JMH version: 1.37

# Run progress: 50,00% complete, ETA 00:01:18
# Warmup Fork: 1 of 1
# Warmup Iteration   1: 54295,457 ms/op
# Warmup Iteration   2: 54743,692 ms/op
# Warmup Iteration   3: 54515,343 ms/op
Iteration   1: 54008,495 ms/op
Iteration   2: 54950,508 ms/op
Iteration   3: 55302,945 ms/op

# Run progress: 66,67% complete, ETA 00:03:23
# Fork: 1 of 2
# Warmup Iteration   1: 52954,515 ms/op
# Warmup Iteration   2: 53960,206 ms/op
# Warmup Iteration   3: 54177,210 ms/op
Iteration   1: 53146,784 ms/op
Iteration   2: 53086,791 ms/op
Iteration   3: 54161,922 ms/op

# Run progress: 83,33% complete, ETA 00:02:26
# Fork: 2 of 2
# Warmup Iteration   1: 51796,763 ms/op
# Warmup Iteration   2: 54491,188 ms/op
# Warmup Iteration   3: 58515,556 ms/op
Iteration   1: 56318,138 ms/op
Iteration   2: 55910,036 ms/op
Iteration   3: 61546,754 ms/op

Result "task.Matrix.usualMultiplication":
  55695,071 ±(99.9%) 8896,306 ms/op [Average]
  (min, avg, max) = (53086,791, 55695,071, 61546,754), stdev = 3172,509
  CI (99.9%): [46798,764, 64591,377] (assumes normal distribution)
------- optimized with JMH (2000x2000)
# Run progress: 0,00% complete, ETA 00:00:00
# Warmup Fork: 1 of 1
# Warmup Iteration   1: 4910,483 ms/op
# Warmup Iteration   2: 4395,999 ms/op
# Warmup Iteration   3: 3988,137 ms/op
Iteration   1: 4055,465 ms/op
Iteration   2: 4027,166 ms/op
Iteration   3: 4088,722 ms/op

# Run progress: 16,67% complete, ETA 00:02:12
# Fork: 1 of 2
# Warmup Iteration   1: 4348,062 ms/op
# Warmup Iteration   2: 4237,879 ms/op
# Warmup Iteration   3: 4036,714 ms/op
Iteration   1: 3995,963 ms/op
Iteration   2: 4015,766 ms/op
Iteration   3: 4076,259 ms/op

# Run progress: 33,33% complete, ETA 00:01:44
# Fork: 2 of 2
# Warmup Iteration   1: 4335,337 ms/op
# Warmup Iteration   2: 4438,530 ms/op
# Warmup Iteration   3: 4456,666 ms/op
Iteration   1: 4140,981 ms/op
Iteration   2: 4284,481 ms/op
Iteration   3: 4135,997 ms/op


Result "task.Matrix.optimizedMultiplication":
  4108,241 ±(99.9%) 294,329 ms/op [Average]
  (min, avg, max) = (3995,963, 4108,241, 4284,481), stdev = 104,960
  CI (99.9%): [3813,913, 4402,570] (assumes normal distribution)
------------------------------------------------------------
!!!!!!!!!!!!!!!!!!!Для матриц размером 1000x2000 и 2000x1000!!!!!!!!!!!!!!!!
------- usual with JMH (1000x2000 и 2000x1000)
# Run progress: 0,00% complete, ETA 00:00:00
# Warmup Fork: 1 of 1
# Warmup Iteration   1: 9556,026 ms/op
# Warmup Iteration   2: 9886,481 ms/op
# Warmup Iteration   3: 10583,982 ms/op
Iteration   1: 11168,277 ms/op
Iteration   2: 10142,782 ms/op
Iteration   3: 10053,772 ms/op

# Run progress: 33,33% complete, ETA 00:02:04
# Fork: 1 of 2
# Warmup Iteration   1: 10363,633 ms/op
# Warmup Iteration   2: 10026,262 ms/op
# Warmup Iteration   3: 10439,223 ms/op
Iteration   1: 10483,210 ms/op
Iteration   2: 10512,708 ms/op
Iteration   3: 10460,787 ms/op

# Run progress: 66,67% complete, ETA 00:01:02
# Fork: 2 of 2
# Warmup Iteration   1: 10334,321 ms/op
# Warmup Iteration   2: 9927,287 ms/op
# Warmup Iteration   3: 10179,891 ms/op
Iteration   1: 10093,461 ms/op
Iteration   2: 10156,665 ms/op
Iteration   3: 10150,833 ms/op


Result "task.Matrix.usualMultiplication":
  10309,611 ±(99.9%) 546,007 ms/op [Average]
  (min, avg, max) = (10093,461, 10309,611, 10512,708), stdev = 194,711
  CI (99.9%): [9763,604, 10855,618] (assumes normal distribution)

# Run complete. Total time: 00:03:07

Benchmark                   Mode  Cnt      Score     Error  Units
Matrix.usualMultiplication  avgt    6  10309,611 ± 546,007  ms/op

Process finished with exit code 0

------- optimized with JMH (1000x2000 и 2000x1000)
# Run progress: 0,00% complete, ETA 00:00:00
# Warmup Fork: 1 of 1
# Warmup Iteration   1: 1822,444 ±(99.9%) 1359,777 ms/op
# Warmup Iteration   2: 1587,560 ±(99.9%) 512,533 ms/op
# Warmup Iteration   3: 1294,902 ±(99.9%) 271,364 ms/op
Iteration   1: 1566,591 ±(99.9%) 749,857 ms/op
Iteration   2: 1359,718 ±(99.9%) 322,667 ms/op
Iteration   3: 1508,752 ±(99.9%) 247,796 ms/op

# Run progress: 33,33% complete, ETA 00:00:58
# Fork: 1 of 2
# Warmup Iteration   1: 1359,914 ±(99.9%) 404,987 ms/op
# Warmup Iteration   2: 1372,696 ±(99.9%) 273,213 ms/op
# Warmup Iteration   3: 1475,643 ±(99.9%) 630,258 ms/op
Iteration   1: 1498,821 ±(99.9%) 403,999 ms/op
Iteration   2: 1520,928 ±(99.9%) 172,867 ms/op
Iteration   3: 1415,017 ±(99.9%) 202,114 ms/op

# Run progress: 66,67% complete, ETA 00:00:28
# Fork: 2 of 2
# Warmup Iteration   1: 1459,903 ±(99.9%) 619,711 ms/op
# Warmup Iteration   2: 1328,448 ±(99.9%) 293,832 ms/op
# Warmup Iteration   3: 1658,715 ±(99.9%) 548,248 ms/op
Iteration   1: 1523,343 ±(99.9%) 666,509 ms/op
Iteration   2: 1423,830 ±(99.9%) 209,187 ms/op
Iteration   3: 1373,529 ±(99.9%) 711,698 ms/op


Result "task.Matrix.optimizedMultiplication":
  1459,244 ±(99.9%) 177,517 ms/op [Average]
  (min, avg, max) = (1373,529, 1459,244, 1523,343), stdev = 63,304
  CI (99.9%): [1281,727, 1636,762] (assumes normal distribution)

# Run complete. Total time: 00:01:25

Benchmark                       Mode  Cnt     Score     Error  Units
Matrix.optimizedMultiplication  avgt    6  1459,244 ± 177,517  ms/op

Process finished with exit code 0

------------------------------------------------------------
!!!!!!!!!!!!!!!!!!!Для матриц размером 600x1000 и 1000x6000!!!!!!!!!!!!!!!!!!!!!
------- usual with JMH (600x1000 и 1000x6000)
# Run progress: 0,00% complete, ETA 00:00:00
# Warmup Fork: 1 of 1
# Warmup Iteration   1: 866,401 ms/op
# Warmup Iteration   2: 816,398 ms/op
# Warmup Iteration   3: 630,087 ms/op
Iteration   1: 606,018 ms/op
Iteration   2: 600,982 ms/op
Iteration   3: 619,660 ms/op

# Run progress: 33,33% complete, ETA 00:00:10
# Fork: 1 of 2
# Warmup Iteration   1: 798,596 ms/op
# Warmup Iteration   2: 822,466 ms/op
# Warmup Iteration   3: 678,382 ms/op
Iteration   1: 742,178 ms/op
Iteration   2: 723,540 ms/op
Iteration   3: 663,874 ms/op

# Run progress: 66,67% complete, ETA 00:00:05
# Fork: 2 of 2
# Warmup Iteration   1: 862,119 ms/op
# Warmup Iteration   2: 836,830 ms/op
# Warmup Iteration   3: 634,663 ms/op
Iteration   1: 655,895 ms/op
Iteration   2: 625,856 ms/op
Iteration   3: 651,019 ms/op

Result "task.Matrix.usualMultiplication":
  677,060 ±(99.9%) 127,414 ms/op [Average]
  (min, avg, max) = (625,856, 677,060, 742,178), stdev = 45,437
  CI (99.9%): [549,647, 804,474] (assumes normal distribution)

------- optimized with JMH (600x1000 и 1000x6000)
# Run progress: 0,00% complete, ETA 00:00:00
# Warmup Fork: 1 of 1
# Warmup Iteration   1: 236,532 ±(99.9%) 286,400 ms/op
# Warmup Iteration   2: 212,484 ±(99.9%) 192,663 ms/op
# Warmup Iteration   3: 193,516 ±(99.9%) 79,532 ms/op
Iteration   1: 218,991 ±(99.9%) 133,243 ms/op
Iteration   2: 208,196 ±(99.9%) 130,478 ms/op
Iteration   3: 217,796 ±(99.9%) 110,250 ms/op

# Run progress: 33,33% complete, ETA 00:00:10
# Fork: 1 of 2
# Warmup Iteration   1: 251,656 ±(99.9%) 127,457 ms/op
# Warmup Iteration   2: 242,272 ±(99.9%) 72,605 ms/op
# Warmup Iteration   3: 255,066 ±(99.9%) 58,082 ms/op
Iteration   1: 236,583 ±(99.9%) 124,123 ms/op
Iteration   2: 210,687 ±(99.9%) 89,246 ms/op
Iteration   3: 191,874 ±(99.9%) 123,357 ms/op

# Run progress: 66,67% complete, ETA 00:00:05
# Fork: 2 of 2
# Warmup Iteration   1: 232,882 ±(99.9%) 280,825 ms/op
# Warmup Iteration   2: 219,017 ±(99.9%) 112,796 ms/op
# Warmup Iteration   3: 203,500 ±(99.9%) 62,331 ms/op
Iteration   1: 195,376 ±(99.9%) 69,595 ms/op
Iteration   2: 209,889 ±(99.9%) 87,347 ms/op
Iteration   3: 193,019 ±(99.9%) 36,156 ms/op


Result "task.Matrix.optimizedMultiplication":
  206,238 ±(99.9%) 47,803 ms/op [Average]
  (min, avg, max) = (191,874, 206,238, 236,583), stdev = 17,047
  CI (99.9%): [158,435, 254,040] (assumes normal distribution)
 */