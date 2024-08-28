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
        Options opt = new OptionsBuilder()
                .include(Matrix.class.getSimpleName())
                //.warmupIterations(3).measurementIterations(5).forks(1)
                .build();

        new Runner(opt).run();

        usualMultiplication();
        optimizedMultiplication();
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.MILLISECONDS)
    @Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.MILLISECONDS)
    @Fork(value = 2, warmups = 1)
    //@Threads(4)
    // Обычное умножение матриц
    public static void usualMultiplication() {
        //Date start = new Date();
        var matrixResult = new int[matrixA.length][matrixB[0].length];
        for (var i = 0; i < matrixResult.length; i++) {
            for (var j = 0; j < matrixResult[0].length; j++) {
                matrixResult[i][j] = 0;
                for (var k = 0; k < matrixA[0].length; k++) {
                    matrixResult[i][j] += matrixA[i][k] * matrixB[k][j];
                }
            }
        }
        //Date end = new Date();
        //System.out.println("\n usual Time taken in milli seconds: " + (end.getTime() - start.getTime()));
        //printMatrix(matrixResult);
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.MILLISECONDS)
    @Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.MILLISECONDS)
    @Fork(value = 2, warmups = 1)
    //@Threads(4)
    // Оптимизированное умножение матриц. Мы создадим поток для каждой строки в матрице, который будет выполнять
    // умножение параллельно, что позволит сократить время обработки.
    public static void optimizedMultiplication() {
        //Date start = new Date();
        var matrixResult = new int[matrixA.length][matrixB[0].length];
        ParallelThreadsCreator.multiply(matrixA, matrixB, matrixResult);
        //Date end = new Date();
        //System.out.println("\n optimized Time taken in milli seconds: " + (end.getTime() - start.getTime()));
        //printMatrix(matrixResult);
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
Для матриц небольшого размера:

# Run progress: 0,00% complete, ETA 00:00:00
# Warmup Fork: 1 of 1
# Warmup Iteration   1: 3698525,000 ±(99.9%) 33820930,602 ns/op
# Warmup Iteration   2: 1175525,000 ±(99.9%) 1096422,744 ns/op
# Warmup Iteration   3: 1368725,000 ±(99.9%) 1709127,512 ns/op
Iteration   1: 894850,000 ±(99.9%) 4035490,427 ns/op
Iteration   2: 1863650,000 ±(99.9%) 8128878,814 ns/op
Iteration   3: 1142700,000 ±(99.9%) 1505674,203 ns/op
Iteration   4: 1287425,000 ±(99.9%) 4056624,864 ns/op
Iteration   5: 1246975,000 ±(99.9%) 3986636,606 ns/op

# Run progress: 16,67% complete, ETA 00:00:04
# Fork: 1 of 2
# Warmup Iteration   1: 1308925,000 ±(99.9%) 2360902,655 ns/op
# Warmup Iteration   2: 1158725,000 ±(99.9%) 1876170,420 ns/op
# Warmup Iteration   3: 1242325,000 ±(99.9%) 1958145,080 ns/op
Iteration   1: 1132825,000 ±(99.9%) 3270404,191 ns/op
Iteration   2: 1112200,000 ±(99.9%) 2096317,208 ns/op
Iteration   3: 1107750,000 ±(99.9%) 2982044,708 ns/op
Iteration   4: 1045650,000 ±(99.9%) 1886362,194 ns/op
Iteration   5: 1129825,000 ±(99.9%) 2399561,762 ns/op

# Run progress: 33,33% complete, ETA 00:00:03
# Fork: 2 of 2
# Warmup Iteration   1: 1863975,000 ±(99.9%) 3577564,757 ns/op
# Warmup Iteration   2: 1552575,000 ±(99.9%) 4371229,838 ns/op
# Warmup Iteration   3: 1515950,000 ±(99.9%) 7369602,805 ns/op
Iteration   1: 1451550,000 ±(99.9%) 1640653,530 ns/op
Iteration   2: 1714425,000 ±(99.9%) 2352882,832 ns/op
Iteration   3: 1742275,000 ±(99.9%) 3384661,263 ns/op
Iteration   4: 1462300,000 ±(99.9%) 1901932,043 ns/op
Iteration   5: 1345425,000 ±(99.9%) 3581079,063 ns/op

Result "task.Matrix.optimizedMultiplication":
  1324422,500 ±(99.9%) 392498,601 ns/op [Average]
  (min, avg, max) = (1045650,000, 1324422,500, 1742275,000), stdev = 259613,523
  CI (99.9%): [931923,899, 1716921,101] (assumes normal distribution)

# Run progress: 50,00% complete, ETA 00:00:02
# Warmup Fork: 1 of 1
# Warmup Iteration   1: 6228,285 ±(99.9%) 19894,508 ns/op
# Warmup Iteration   2: 5296,982 ±(99.9%) 3653,654 ns/op
# Warmup Iteration   3: 5307,230 ±(99.9%) 2124,729 ns/op
Iteration   1: 6730,769 ±(99.9%) 3715,126 ns/op
Iteration   2: 5199,306 ±(99.9%) 3550,477 ns/op
Iteration   3: 5003,333 ±(99.9%) 2494,547 ns/op
Iteration   4: 4740,935 ±(99.9%) 4929,484 ns/op
Iteration   5: 9865,378 ±(99.9%) 81766,480 ns/op

# Run progress: 66,67% complete, ETA 00:00:01
# Fork: 1 of 2
# Warmup Iteration   1: 6030,245 ±(99.9%) 2201,946 ns/op
# Warmup Iteration   2: 7287,077 ±(99.9%) 8854,958 ns/op
# Warmup Iteration   3: 7009,375 ±(99.9%) 6470,847 ns/op
Iteration   1: 6905,000 ±(99.9%) 7100,863 ns/op
Iteration   2: 7434,583 ±(99.9%) 9252,626 ns/op
Iteration   3: 6178,750 ±(99.9%) 13065,622 ns/op
Iteration   4: 8214,444 ±(99.9%) 37529,152 ns/op
Iteration   5: 8208,154 ±(99.9%) 36246,267 ns/op

# Run progress: 83,33% complete, ETA 00:00:00
# Fork: 2 of 2
# Warmup Iteration   1: 7300,170 ±(99.9%) 8002,104 ns/op
# Warmup Iteration   2: 9390,000 ±(99.9%) 11341,973 ns/op
# Warmup Iteration   3: 8137,863 ±(99.9%) 6674,857 ns/op
Iteration   1: 9159,821 ±(99.9%) 35299,465 ns/op
Iteration   2: 8116,667 ±(99.9%) 3048,748 ns/op
Iteration   3: 8185,417 ±(99.9%) 2283,971 ns/op
Iteration   4: 10414,861 ±(99.9%) 5225,165 ns/op
Iteration   5: 8415,069 ±(99.9%) 6068,803 ns/op

Result "task.Matrix.usualMultiplication":
  8123,277 ±(99.9%) 1758,067 ns/op [Average]
  (min, avg, max) = (6178,750, 8123,277, 10414,861), stdev = 1162,852
  CI (99.9%): [6365,210, 9881,343] (assumes normal distribution)

# Run complete. Total time: 00:00:05
------------------------------------------------------------------------------------------------------------------------
Для больших матриц 2000x2000
usual Time taken in milli seconds: 52850
optimized Time taken in milli seconds: 16032


# Run progress: 0,00% complete, ETA 00:00:00
# Warmup Fork: 1 of 1
# Warmup Iteration   1: 15821,458 ms/op
# Warmup Iteration   2: 16129,848 ms/op
# Warmup Iteration   3: 16162,994 ms/op
Iteration   1: 16201,444 ms/op
Iteration   2: 16173,996 ms/op
Iteration   3: 16129,737 ms/op
Iteration   4: 16152,519 ms/op
Iteration   5: 16088,972 ms/op

# Run progress: 16,67% complete, ETA 00:10:49
# Fork: 1 of 2
# Warmup Iteration   1: 15747,639 ms/op
# Warmup Iteration   2: 16070,525 ms/op
# Warmup Iteration   3: 15842,698 ms/op
Iteration   1: 15726,068 ms/op
Iteration   2: 15845,307 ms/op
Iteration   3: 15739,208 ms/op
Iteration   4: 15746,220 ms/op
Iteration   5: 15987,608 ms/op

# Run progress: 33,33% complete, ETA 00:08:35
# Fork: 2 of 2
# Warmup Iteration   1: 15815,918 ms/op
# Warmup Iteration   2: 16213,786 ms/op
# Warmup Iteration   3: 15839,194 ms/op
Iteration   1: 15799,037 ms/op
Iteration   2: 15892,232 ms/op
Iteration   3: 15873,054 ms/op
Iteration   4: 15983,810 ms/op
Iteration   5: 15920,949 ms/op


Result "task.Matrix.optimizedMultiplication":
  15851,349 ±(99.9%) 147,095 ms/op [Average]
  (min, avg, max) = (15726,068, 15851,349, 15987,608), stdev = 97,294
  CI (99.9%): [15704,255, 15998,444] (assumes normal distribution)

# JMH version: 1.37
# Warmup: 3 iterations, 1 ms each
# Measurement: 5 iterations, 1 ms each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: task.Matrix.usualMultiplication

# Run progress: 50,00% complete, ETA 00:06:25
# Warmup Fork: 1 of 1
# Warmup Iteration   1: 51070,037 ms/op
# Warmup Iteration   2: 53128,631 ms/op
# Warmup Iteration   3: 53196,430 ms/op
Iteration   1: 52347,027 ms/op
Iteration   2: 52297,313 ms/op
Iteration   3: 52383,848 ms/op
Iteration   4: 52245,688 ms/op
Iteration   5: 52289,240 ms/op

# Run progress: 66,67% complete, ETA 00:06:42
# Fork: 1 of 2
# Warmup Iteration   1: 50210,004 ms/op
# Warmup Iteration   2: 51980,588 ms/op
# Warmup Iteration   3: 51937,111 ms/op
Iteration   1: 50690,320 ms/op
Iteration   2: 50684,419 ms/op
Iteration   3: 50735,735 ms/op
Iteration   4: 51153,568 ms/op
Iteration   5: 51112,353 ms/op

# Run progress: 83,33% complete, ETA 00:04:03
# Fork: 2 of 2
# Warmup Iteration   1: 51952,530 ms/op
# Warmup Iteration   2: 53106,905 ms/op
# Warmup Iteration   3: 53049,856 ms/op
Iteration   1: 53063,133 ms/op
Iteration   2: 52968,673 ms/op
Iteration   3: 53071,142 ms/op
Iteration   4: 53212,224 ms/op
Iteration   5: 52985,552 ms/op


Result "task.Matrix.usualMultiplication":
  51967,712 ±(99.9%) 1759,873 ms/op [Average]
  (min, avg, max) = (50684,419, 51967,712, 53212,224), stdev = 1164,047
  CI (99.9%): [50207,839, 53727,585] (assumes normal distribution)

# Run complete. Total time: 00:27:19
 */