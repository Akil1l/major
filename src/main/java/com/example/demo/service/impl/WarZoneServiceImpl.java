package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.demo.entity.WarZone;
import com.example.demo.mapper.WarZoneMapper;
import com.example.demo.service.WarZoneService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.neuroph.util.Utils;
import org.neuroph.core.Connection;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.Neuron;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.core.events.LearningEvent;
import org.neuroph.core.events.LearningEventListener;
import org.neuroph.core.learning.LearningRule;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.BackPropagation;
import org.neuroph.util.TransferFunctionType;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author Akil
 * @since 2021-04-09
 */
@Service
public class WarZoneServiceImpl extends ServiceImpl<WarZoneMapper, WarZone> implements WarZoneService {
    private static Double rate;
    private static Double preError = 0.1;
    private static DataSet trainingSet = new DataSet(4, 3);
    List<WarZone> warZones;
    private final WarZoneMapper warZoneMapper;

    public WarZoneServiceImpl(WarZoneMapper warZoneMapper) {
        this.warZoneMapper = warZoneMapper;
    }

    @Override
    public Object learn(Integer index) {
        rate = 0.1;
        warZones = warZoneMapper.selectList(new QueryWrapper<WarZone>().eq("type", index));
        new WarZoneDeepLearning(index).run();
        return null;
    }

    @Override
    public Integer test(WarZone warZone) {
        MultiLayerPerceptron load = null;
        try {
            load = (MultiLayerPerceptron) MultiLayerPerceptron.load(new FileInputStream("warZone_" + warZone.getType() + ".neet"));
        } catch (FileNotFoundException e) {
            load = new MultiLayerPerceptron(TransferFunctionType.SIGMOID, 4, 9,13,7, 3);
            e.printStackTrace();
        }
        double[] input = inputFormat(warZone);
        load.setInput(input);
        load.calculate();
        double[] networkOutput = load.getOutput();
        return outputDecode(Utils.competition(networkOutput));
    }

    private double[] inputFormat(WarZone warZone) {
        double[] input = new double[4];
        input[0] = (double) warZone.getDay() / 84;
        input[1] = (warZone.getAshHill() - 24) / 17;
        input[2] = (warZone.getThunderPlains() - 24) / 17;
        input[3] = (warZone.getHellPortal() - 24) / 17;
        return input;
    }

    private Integer outputDecode(double[] input) {
        for (int i = 0; i < input.length; i++) {
            if (input[i] != 0) return i;
        }
        return null;
    }

    public class WarZoneDeepLearning implements LearningEventListener {
        BackPropagation rule;
        Integer index;

        public WarZoneDeepLearning(Integer index) {
            this.index = index;
        }

        public void run() { // 训练数据
            // 训练数据
            warZones.forEach(warZone -> {
                double[] input = inputFormat(warZone);
                double[] output = new double[3];
                output[warZone.getException()] = 1;
                trainingSet.add(new DataSetRow(input, output));
            });
            // 138个输入数据，277/46个神经元隐层，27个输出
            MultiLayerPerceptron myPerceptron = null;
            try {
                myPerceptron = (MultiLayerPerceptron) MultiLayerPerceptron.load(new FileInputStream("warZone_" + index + ".neet"));
            } catch (Exception e) {
                myPerceptron = new MultiLayerPerceptron(TransferFunctionType.LINEAR, 4, 12, 7, 3);
            }
            LearningRule learningRule = myPerceptron.getLearningRule();
            rule = myPerceptron.getLearningRule();
            rate = rule.getLearningRate();
            rule.setMaxError(0.001);
            learningRule.addListener(this);
            // 训练神经网络
            System.out.println("Training neural network...");
            myPerceptron.learn(trainingSet);
            myPerceptron.save("warZone_" + index + ".neet");
            // 测试神经网络
            System.out.println("Testing trained neural network");
            testNeuralNetwork(myPerceptron, trainingSet);
        }

        public void testNeuralNetwork(NeuralNetwork neuralNet, DataSet testSet) {
            int s = 0;
            int i = 0;
            for (DataSetRow testSetRow : testSet.getRows()) {
                s++;
                neuralNet.setInput(testSetRow.getInput());
                neuralNet.calculate();
                double[] networkOutput = neuralNet.getOutput();
                double[] desiredOutput = testSetRow.getDesiredOutput();
                System.out.println("Input: " + inputDecode(testSetRow.getInput()));
                Integer output = outputDecode(Utils.competition(networkOutput));
                Integer require = outputDecode(desiredOutput);
                System.out.println(" Output: " + output);
                System.out.println(" Require: " + require);
                if (output.equals(require)) i++;
            }
            System.out.println("准确率:" + (double) i / s * 100 + "%");
        }

        private String inputDecode(double[] networkOutput) {
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < networkOutput.length; i++) {
                double card = networkOutput[i];
                result.append((int) card);
                if (i == networkOutput.length - 1) return result.toString();
                result.append(",");
            }
            return null;
        }

        @Override
        public void handleLearningEvent(LearningEvent event) {
            System.out.println("============");
            System.out.println(event.getClass().toString());
            BackPropagation bp = (BackPropagation) event.getSource();
            System.out.println("iterate:" + bp.getCurrentIteration());
            double error = bp.getTotalNetworkError();
            if (error - preError > 0) {
                rate *= 0.9;
                rule.setLearningRate(rate);
            }
            preError = error;
            System.out.println("error:" + error);
            System.out.println("rate:" + bp.getLearningRate());
            Neuron neuron = (Neuron) bp.getNeuralNetwork().getOutputNeurons().get(0);
            for (Connection conn : neuron.getInputConnections()) {
                System.out.println(conn.getWeight().value);
            }
        }
    }
}
