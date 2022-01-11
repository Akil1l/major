package com.example.majon;

import org.neuroph.core.data.DataSet;
import org.neuroph.core.learning.LearningRule;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.BackPropagation;
import org.neuroph.util.TransferFunctionType;
import org.springframework.util.StringUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @program: demo
 * @description: Majon
 * @author: Akil
 * @create: 2021-02-20 17:37
 **/

public class MaJon {
    public static void main(String[] args) {
        DataSet trainingSet = new DataSet(138, 27);
        MultiLayerPerceptron myPerceptron = null;
        try {
            myPerceptron = (MultiLayerPerceptron) MultiLayerPerceptron.load(new FileInputStream("Major.neet"));
        } catch (FileNotFoundException e) {
            myPerceptron = new MultiLayerPerceptron(TransferFunctionType.SIGMOID, 138, 111, 64, 27);
        }
        BackPropagation rule = myPerceptron.getLearningRule();
        rule.setMaxIterations(100);
//                rule.setLearningRate(0.1);
        // 训练神经网络
        System.out.println("Training neural network...");
        myPerceptron.learn(trainingSet);
        myPerceptron.save("Major.neet");
        // 测试神经网络
        System.out.println("Testing trained neural network");
    }

    public static void sort(double[] arr) {
        for (int i = 1; i < arr.length; i++) {  //第一层for循环,用来控制冒泡的次数
            for (int j = 0; j < arr.length - 1; j++) { //第二层for循环,用来控制冒泡一层层到最后
                //如果前一个数比后一个数大,两者调换 ,意味着泡泡向上走了一层
                if (arr[j] > arr[j + 1]) {
                    double temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                }
            }
        }
    }

    public static double[] int2double(int i) {
        double[] re = new double[6];
        for (int j = 0; j < 6; j++) {
            re[5 - j] = (i >> j) & 1;
        }
        return re;
    }

    private static String toString(HashMap<Integer, Integer> handCard) {
        AtomicReference<String> huKey = new AtomicReference<>("");
        handCard.forEach((key, value) -> {
            if (value != 0 && key > 10) {
                do {
                    huKey.updateAndGet(v1 -> v1.concat(String.valueOf(key)));
                    value--;
                } while (value > 0);
            }
        });
        return huKey.get();
    }

    public static HashMap<Integer, Integer> generateHandCard() {
        HashMap<Integer, Integer> handCard = new HashMap<>();
        int card = 11;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++, card++) {
                handCard.put(card, 0);
            }
            card = i == 0 ? 21 : 31;
        }
        return handCard;
    }

    private static Integer[] sortCards(List<Integer> handCard) {
        Integer[] array = handCard.toArray(new Integer[0]);
        int index;
        Integer temp;
        for (int i = 1; i < array.length; i++) {
            temp = array[i];
            for (index = i; index > 0 && (temp < array[index - 1]); index--) {
                array[index] = array[index - 1];
            }
            array[index] = temp;
        }
        return array;
    }

    private static List<Integer> getCardsMount() {
        ArrayList<Integer> cardsMount = new ArrayList<>();
        int card = 11;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++, card++) {
                for (int k = 0; k < 4; k++) {
                    cardsMount.add(card);
                }
            }
            card = (i == 0 ? 21 : 31);
        }
        return cardsMount;
    }
}
