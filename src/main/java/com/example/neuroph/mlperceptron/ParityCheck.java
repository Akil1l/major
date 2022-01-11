package com.example.neuroph.mlperceptron;

import com.example.neuroph.util.Utils;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.core.events.LearningEvent;
import org.neuroph.core.events.LearningEventListener;
import org.neuroph.core.learning.LearningRule;
import org.neuroph.core.learning.SupervisedLearning;
import org.neuroph.nnet.learning.BackPropagation;
import org.neuroph.util.TransferFunctionType;

import java.util.Random;

public class ParityCheck implements LearningEventListener {

    public static void main(String[] args) {
        new ParityCheck().run();
    }

    public static double[] int2double(int i) {
        double[] re = new double[32];
        for (int j = 0; j < 32; j++) {
            re[j] = (i >> j) & 1;
        }
        return re;
    }

    public static String networkOutputDisplay(double[] networkOutput) {
        if (((int) networkOutput[3]) == 1) return "正偶数";
        if (((int) networkOutput[2]) == 1) return "负偶数";
        if (((int) networkOutput[1]) == 1) return "正奇数";
        if (((int) networkOutput[0]) == 1) return "负奇数";
        return "未知";
    }

    public static String correctClassify(int i) {
        if (i > 0 && i % 2 == 0) {
            return "正偶数";
        } else if (i < 0 && i % 2 == 0) {
            return "负偶数";
        } else if (i > 0 && i % 2 != 0) {
            return "正奇数";
        } else if (i < 0 && i % 2 != 0) {
            return "负奇数";
        }
        return "0";
    }

    /**
     * 0001  正偶数
     * 0010  负偶数
     * 0100  正奇数
     * 1000  负奇数
     *
     * @param i
     * @return
     */
    public static double[] int2prop(int i) {
        //正偶数
        double[] pe = {0d, 0d, 0d, 1d};
        //负偶数
        double[] ne = {0d, 0d, 1d, 0d};
        //正奇数
        double[] po = {0d, 1d, 0d, 0d};
        //负奇数
        double[] no = {1d, 0d, 0d, 0d};
        if (i > 0 && i % 2 == 0) {
            return pe;
        } else if (i < 0 && i % 2 == 0) {
            return ne;
        } else if (i > 0 && i % 2 != 0) {
            return po;
        } else if (i < 0 && i % 2 != 0) {
            return no;
        }
        return pe;
    }

    public void run() {

        DataSet trainingSet = new DataSet(32, 4);
        //对2000条数据进行训练
        for (int i = 0; i < 2000; i++) {
            int in = new Random().nextInt();
            trainingSet.add(new DataSetRow(int2double(in), int2prop(in)));
        }

        // 创建神经网络 32个输入 10个神经元隐层 4个
        MlPerceptron myMlPerceptron = new MlPerceptron(TransferFunctionType.SIGMOID, 32, 10, 4);

        LearningRule learningRule = myMlPerceptron.getLearningRule();
        learningRule.addListener(this);
        //设置误差
        ((SupervisedLearning) learningRule).setMaxError(0.0001d);

        // learn the training set
        System.out.println("Training neural network...");
        myMlPerceptron.learn(trainingSet);

        // test perceptron
        System.out.println("Testing trained neural network");
        testNeuralNetwork(myMlPerceptron);

    }

    public static void testNeuralNetwork(NeuralNetwork neuralNet) {
        int badcount = 0;
        int COUNT = 50000;
        //判断50000个整数的类别
        for (int i = 0; i < COUNT; i++) {
            int in = new Random().nextInt();
            double[] inputnumber = int2double(in);
            neuralNet.setInput(inputnumber);
            neuralNet.calculate();
            double[] networkOutput = neuralNet.getOutput();
            //转为 只包含0和1 的输出，取最活跃的神经网络为1
            networkOutput = Utils.competition(networkOutput);
            //神经网络给出的分类
            String networkOutputDisplay = networkOutputDisplay(networkOutput);
            //该数字正确的分类
            String cc = correctClassify(in);
            System.out.print(in + " " + networkOutputDisplay + " ");
            if (i % 50 == 0) {
                System.out.println();
            }
            //如果神经网络分类错误，则记录错误数
            if (!cc.equals(networkOutputDisplay)) {
                badcount++;
                System.out.print("判别错误:" + in);
                System.out.print(" correctClassify=" + cc);
                System.out.println(" networkOutputDisplay=" + networkOutputDisplay);
            }
        }
        //给出网络分类的正确率
        System.out.println();
        System.out.println("正确率：" + (COUNT - badcount * 1.0) / COUNT * 100.0 + "%");
    }

    @Override
    public void handleLearningEvent(LearningEvent event) {
        BackPropagation bp = (BackPropagation) event.getSource();
        System.out.println(bp.getCurrentIteration() + ". iteration : " + bp.getTotalNetworkError());
    }
}

