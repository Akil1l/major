package com.example.neuroph.demo;


import com.example.neuroph.preceptron.Adaline;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.core.events.LearningEvent;
import org.neuroph.core.events.LearningEventListener;
import org.neuroph.core.learning.IterativeLearning;
import org.neuroph.core.learning.LearningRule;
import org.neuroph.nnet.learning.LMS;

/**
 * @program: demo
 * @description: AdalineDemo
 * @author: Akil
 * @create: 2021-03-18 17:28
 **/

public class AdalineDemo implements LearningEventListener {
    public final static int CHAR_WIDTH = 5;
    public final static int CHAR_HEIGHT = 7;
    public static String[][] DIGITS = {
            {
                    " ··· ",
                    "·   ·",
                    "·   ·",
                    "·   ·",
                    "·   ·",
                    "·   ·",
                    " ··· "},
            {
                    "  ·  ",
                    " ··  ",
                    "· ·  ",
                    "  ·  ",
                    "  ·  ",
                    "  ·  ",
                    "  ·  ",},
            {
                    " ··· ",
                    "·   ·",
                    "    ·",
                    "   · ",
                    "  ·  ",
                    " ·   ",
                    "·····",},
            {
                    " ··· ",
                    "·   ·",
                    "    ·",
                    " ··· ",
                    "    ·",
                    "·   ·",
                    " ··· ",},
            {
                    "   · ",
                    "  ·· ",
                    " · · ",
                    "·  · ",
                    "·····",
                    "   · ",
                    "   · ",},
            {
                    "·····",
                    "·    ",
                    "·    ",
                    "···· ",
                    "    ·",
                    "·   ·",
                    " ··· ",},
            {
                    " ··· ",
                    "·   ·",
                    "·    ",
                    "···· ",
                    "·   ·",
                    "·   ·",
                    " ··· ",},
            {
                    "·····",
                    "    ·",
                    "    ·",
                    "   · ",
                    "  ·  ",
                    " ·   ",
                    "·    ",},
            {
                    " ··· ",
                    "·   ·",
                    "·   ·",
                    " ··· ",
                    "·   ·",
                    "·   ·",
                    " ··· ",},
            {
                    " ··· ",
                    "·   ·",
                    "·   ·",
                    " ····",
                    "    ·",
                    "·   ·",
                    " ··· ",},
    };
    public static String[][] TEST = {
            {
                    " ··· ",
                    "·   ·",
                    "·   ·",
                    "·   ·",
                    "·   ·",
                    "·   ·",
                    " ·   "},
            {
                    "     ",
                    " ··  ",
                    "· ·  ",
                    "  ·  ",
                    "  ·  ",
                    "  ·  ",
                    "  ·  ",},
            {
                    " ··· ",
                    "·   ·",
                    "    ·",
                    "     ",
                    "  ·  ",
                    " ·   ",
                    "·····",},
            {
                    " ··· ",
                    "··  ·",
                    "    ·",
                    " ··· ",
                    "    ·",
                    "·   ·",
                    " ··· ",},
            {
                    "   · ",
                    "  ·· ",
                    " · · ",
                    "· ·· ",
                    "·····",
                    "   · ",
                    "   · ",},
            {
                    "·····",
                    "· ·  ",
                    "·    ",
                    "···· ",
                    "    ·",
                    "·   ·",
                    " ··· ",},
            {
                    " ··· ",
                    "·   ·",
                    "·    ",
                    " ··· ",
                    "·   ·",
                    "·   ·",
                    " ··· ",},
            {
                    "·····",
                    "    ·",
                    "    ·",
                    "   · ",
                    "  ·· ",
                    " ·   ",
                    "·    ",},
            {
                    " ··· ",
                    "·   ·",
                    "· · ·",
                    " ··· ",
                    "·   ·",
                    "·   ·",
                    " ··· ",},
            {
                    " ··· ",
                    "·   ·",
                    "·   ·",
                    " ··· ",
                    "    ·",
                    "·   ·",
                    " ··· ",},
    };

    public static double[] image2data(String[] image) {
        double[] input = new double[CHAR_WIDTH * CHAR_HEIGHT];
        for (int row = 0; row < CHAR_HEIGHT; row++) {
            for (int col = 0; col < CHAR_WIDTH; col++) {
                int index = (row * CHAR_WIDTH) + col;
                char ch = image[row].charAt(col);
                input[index] = ch == '·' ? 1 : -1;
            }
        }
        return input;
    }

    public static DataSetRow CreateTrainDataRow(String[] image, int idealValue) {
        double[] output = new double[DIGITS.length];
        for (int i = 0; i < output.length; i++) output[i] = -1;
        double[] input = image2data(image);
        output[idealValue] = 1;
        DataSetRow dsr = new DataSetRow(input, output);
        return dsr;
    }

    public static void main(String[] args) {
        new AdalineDemo().run();
    }

    private void run() {
        Adaline ada = new Adaline(CHAR_WIDTH * CHAR_HEIGHT, DIGITS.length);
        DataSet ds = new DataSet(CHAR_WIDTH * CHAR_HEIGHT, DIGITS.length);
        for (int i = 0; i < DIGITS.length; i++) {
            ds.add(CreateTrainDataRow(DIGITS[i], i));
        }
        LearningRule learningRule = ada.getLearningRule();
        learningRule.addListener(this);
        ada.learn(ds);
        for (int i = 0; i < TEST.length; i++) {
            ada.setInput(image2data(TEST[i]));
            ada.calculate();
            printDigit(TEST[i]);
            System.out.println(maxIndex(ada.getOutput()));
            System.out.println();

        }
    }

    private static void printDigit(String[] digit) {
        for(String s:digit){
            System.out.println(s);
        }
    }

    public static int maxIndex(double[] data) {
        int result = -1;
        for (int i = 0; i < data.length; i++) {
            if (result == -1 || data[i] > data[result]) {
                result = i;
            }
        }
        return result;
    }

    @Override
    public void handleLearningEvent(LearningEvent event) {
        IterativeLearning bp = (IterativeLearning) event.getSource();
        System.out.println("iterate:" + bp.getCurrentIteration());
        System.out.print("TotalNetworkError:");
        System.out.println(((LMS) bp.getNeuralNetwork().getLearningRule()).getTotalNetworkError());
    }

}