package sample;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.util.Collections;

import static java.util.stream.IntStream.range;

public class Controller {

    @FXML
    private TextField sz;

    @FXML
    private TextArea txtA;

    @FXML
    private TextArea txtB;

    @FXML
    private TextArea txtC;

    private static int N;

    private static int[][] A;
    private static double[][] L;
    private static double[][] U;

    private static WorkerThread[] threads;

    @FXML
    private void buttonClickAction() throws Exception {

        if (!sz.getText().equals("")) {
            removeRed(sz);
            N = Integer.parseInt(sz.getText());  // columns L

            threads = new WorkerThread[N]; // (N * N) -- total count of threads

            A = new int[N][N];
            L = new double[N][N];
            U = new double[N][N];

            A = readMatrix(txtA, A);

//            luDecomposition(A);
        } else {
            setRed(sz);
        }
    }

    private void luDecomposition(int[][] a) {

        // DISPLAY RESULT

        String result = "";
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                result += String.format("%8d", L[i][j]);
            }
            result += '\n';
        }

        txtB.setEditable(true);
        txtB.setText(result);

        result = "";
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                result += String.format("%8d", U[i][j]);
            }
            result += '\n';
        }

        txtC.setEditable(true);
        txtC.setText(result);

    }

    private int[][] readMatrix(TextArea textArea, int[][] matrix) throws Exception {
        int k = 0;
        for (String line : textArea.getText().split("\\n")) {
            String[] words = line.split("\\s+");
            if (words.length != matrix[0].length) {
                throw new Exception("invalid row length");
            }
            for (int i = 0; i < matrix[0].length; i++) {
                matrix[k][i] = Integer.parseInt(words[i]);
            }
            k++;
        }

        return matrix;
    }

//    private void matrixMultiplication() {
//
//        //creates 9 Worker threads. Each thread Calculates a Matrix Value and sets it to U matrix
//        for (int i = 0; i < N; i++) {
//            for (int j = 0; j < N; j++) {
//                threads[i][j] = new WorkerThread(i, j, A, L, U);
//                threads[i][j].start();
//            }
//        }
//    }


    // STYLE

    private void setRed(TextField tf) {
        ObservableList<String> styleClass = tf.getStyleClass();

        if (!styleClass.contains("tferror")) {
            styleClass.add("tferror");
        }
    }

    private void removeRed(TextField tf) {
        ObservableList<String> styleClass = tf.getStyleClass();
        styleClass.removeAll(Collections.singleton("tferror"));
    }

    static double dotProduct(double[] a, double[] b) {
        return range(0, a.length).mapToDouble(i -> a[i] * b[i]).sum();
    }

    static double[][] matrixMul(double[][] A, double[][] B) {
        double[][] result = new double[A.length][B[0].length];
        double[] aux = new double[B.length];

        for (int j = 0; j < B[0].length; j++) {

            for (int k = 0; k < B.length; k++)
                aux[k] = B[k][j];

            for (int i = 0; i < A.length; i++)
                result[i][j] = dotProduct(A[i], aux);
        }
        return result;
    }

    static double[][] pivotize(double[][] m) {
        int n = m.length;
        double[][] id = range(0, n).mapToObj(j -> range(0, n)
                .mapToDouble(i -> i == j ? 1 : 0).toArray())
                .toArray(double[][]::new);

        for (int i = 0; i < n; i++) {
            double maxm = m[i][i];
            int row = i;
            for (int j = i; j < n; j++)
                if (m[j][i] > maxm) {
                    maxm = m[j][i];
                    row = j;
                }

            if (i != row) {
                double[] tmp = id[i];
                id[i] = id[row];
                id[row] = tmp;
            }
        }
        return id;
    }

    static double[][][] lu(double[][] A) {
        double[][] P = pivotize(A);
        double[][] A2 = matrixMul(P, A);

        for (int j = 0; j < N; j++) {
            L[j][j] = 1;
            for (int i = 0; i < j + 1; i++) {
                double s1 = 0;
                for (int k = 0; k < i; k++)
                    s1 += U[k][j] * L[i][k];
                U[i][j] = A2[i][j] - s1;
            }
            for (int i = j; i < N; i++) {
                double s2 = 0;
                for (int k = 0; k < j; k++)
                    s2 += U[k][j] * L[i][k];
                L[i][j] = (A2[i][j] - s2) / U[j][j];
            }
        }
        return new double[][][]{L, U, P};
    }
}
