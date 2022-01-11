package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.demo.entity.CardHu;
import com.example.demo.mapper.CardHuMapper;
import com.example.demo.mapper.CmGuoMapper;
import com.example.demo.mapper.SessionMapper;
import com.example.demo.service.CardHuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.neuroph.util.Utils;
import com.github.pagehelper.page.PageMethod;
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

import javax.transaction.Transactional;
import java.io.FileInputStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author Akil
 * @since 2021-02-23
 */
@Service
public class CardHuServiceImpl extends ServiceImpl<CardHuMapper, CardHu> implements CardHuService {
    private final SessionMapper sessionMapper;
    private final CmGuoMapper cmGuoMapper;
    private static final Integer max = 8;
    private static Integer tempMulti = 1;
    private static Integer passNum;
    private static Integer card;
    private static Integer order;
    private static Double rate = 0.1;
    private static Boolean flag = true;
    private static Double preError = 0.1;
    private static AtomicBoolean hu = new AtomicBoolean(false);
    private static List<Integer> cardMount;
    private static Map<String, String> m = new HashMap<>();
    private static Map<String, Integer> map = new HashMap<>();
    private static Map<Integer, Integer> score = new HashMap<>();
    private static Map<Integer, Map<String, Integer>> dateSets = new HashMap<>();
    private static Map<Integer, List<Integer[]>> playersShowInformation;
    private static Map<Integer, List<Integer>> playersAction;
    private static Map<Integer, Map<Integer, Integer>> playersHandCard;
    private static Map<Integer, Function> functionMap = new HashMap<>();
    private static DataSet trainingSet = new DataSet(138, 27);
    Scanner scanner = new Scanner(System.in);

    @Override
    public String learn() {
        new MaJonDeepLearning().run();
        return null;
    }

    @Override
    public Object pause() {
        flag=!flag;
        return null;
    }

    public class MaJonDeepLearning implements LearningEventListener {
        BackPropagation rule;

        public void run() { // 训练数据
            if (trainingSet.isEmpty()) {
                int size = 40000;
                int totalPage = cmGuoMapper.getCount() / size + 1;
                for (int j = 1; j <= totalPage; j++) {
                    PageMethod.startPage(j, size);
                    Map<String, Object> map = sessionMapper.getDataSet();
                    // 训练数据
                    map.forEach((k, v) -> {
                        double[] info = new double[138];
                        double[] card = new double[27];
                        String[] split = k.split(",");
                        for (int i = 0; i < 138; i++) {
                            info[i] = Double.parseDouble(split[i]);
                        }
                        int i = Integer.parseInt(v.toString());
                        card[i - 10 - i / 10] = 1;
                        trainingSet.add(new DataSetRow(info, card));
                    });
                }
            }
            // 138个输入数据，277/46个神经元隐层，27个输出
            MultiLayerPerceptron myPerceptron = null;
            do {
                try {
                    myPerceptron = (MultiLayerPerceptron) MultiLayerPerceptron.load(new FileInputStream("Major.neet"));
                } catch (Exception e) {
                    myPerceptron = new MultiLayerPerceptron(TransferFunctionType.SIGMOID, 138, 111, 64, 27);
                }
                LearningRule learningRule = myPerceptron.getLearningRule();
                rule = myPerceptron.getLearningRule();
                rate = rule.getLearningRate();
                rule.setMaxIterations(10);
                learningRule.addListener(this);
                // 训练神经网络
                System.out.println("Training neural network...");
                myPerceptron.learn(trainingSet);
                myPerceptron.save("Major.neet");
                // 测试神经网络
                System.out.println("Testing trained neural network");
                testNeuralNetwork(myPerceptron, trainingSet);
            } while (flag);
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
                System.out.println("Input: " + infoDecode(testSetRow.getInput()));
                Integer output = cardDecode(Utils.competition(networkOutput));
                Integer require = cardDecode(desiredOutput);
                System.out.println(" Output: " + output);
                System.out.println(" Require: " + require);
                if (output == require) i++;
            }
            System.out.println("准确率:" + (double) i / s * 100 + "%");
        }

        private String infoDecode(double[] networkOutput) {
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < networkOutput.length; i++) {
                double card = networkOutput[i];
                if (i < 18 && card != 0) {
                    result.append((int) (card * 150 + 10.5));
                } else {
                    result.append((int) (card * 150 + 0.5));
                }
                if (i == networkOutput.length - 1) return result.toString();
                result.append(",");
            }
            return null;
        }

        private Integer cardDecode(double[] input) {
            for (int i = 0; i < input.length; i++) {
                if (input[i] != 0) return i / 9 + i + 11;
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
            if (error - preError > 0) rule.setLearningRate(rate * 0.9);
            preError=error;
            System.out.println("error:" + error);
            System.out.println("rate:" + bp.getLearningRate());
            Neuron neuron = (Neuron) bp.getNeuralNetwork().getOutputNeurons().get(0);
            for (Connection conn : neuron.getInputConnections()) {
                System.out.println(conn.getWeight().value);
            }
        }
    }

    public CardHuServiceImpl(CmGuoMapper cmGuoMapper, SessionMapper sessionMapper) {
        this.cmGuoMapper = cmGuoMapper;
        this.sessionMapper = sessionMapper;
    }

    {
        functionMap.put(0, new Function() {
            @Override
            public Object apply(Object o) {
                if (hu.get()) return null;
                Integer player = ((Integer[]) o)[0];
                System.out.println("玩家" + player + "正在碰" + card);
                Map<Integer, Integer> handCard = playersHandCard.get(player);
                System.out.println(toArray(handCard));
                handCard.merge(card, -2, Integer::sum);
                handCard.put(-card, -1);
                playersShowInformation.get(player).add(new Integer[]{card, order + 10});
//                Integer discard = getRandom(handCard);
//                System.out.println("玩家" + player + "碰了" + card + ",准备打出" + discard);
                System.out.println(toArray(handCard));
                order = player;
                disCheck(0, player);
                return null;
            }
        });
        functionMap.put(1, new Function() {
            @Override
            public Object apply(Object o) {
                Integer[] args = (Integer[]) o;
                Integer player = args[0];
                Map<Integer, Integer> handCard = playersHandCard.get(player);
                handCard.merge(card, 1, Integer::sum);
                handCard.put(0, order);
                handCard.put(1, args[3]);
                oneColor(handCard);
                Integer price = Integer.min(max, args[3] * handCard.get(2) * tempMulti);
                score.merge(player, price, Integer::sum);
                score.merge(order, -price, Integer::sum);
                playersShowInformation.get(player).add(new Integer[]{card, order + 20});
                passNum++;
                order = player;
                return null;
            }
        });
        functionMap.put(2, new Function() {
            @Override
            public Object apply(Object o) {
                if (hu.get()) return null;
                Integer[] args = (Integer[]) o;
                Integer player = args[0];
                Integer gang = args[1];
                Integer index = args[2];
                Map<Integer, Integer> handCard = playersHandCard.get(player);
                handCard.put(gang, 0);
                handCard.put(-gang, index);
                tempMulti *= 2;
                if (index.equals(player) && card.equals(gang)) {
                    for (Map.Entry<Integer, Map<Integer, Integer>> entry : playersHandCard.entrySet()) {
                        Integer k = entry.getKey();
                        Map<Integer, Integer> v = entry.getValue();
                        if (v.get(0) == null && !k.equals(player)) {
                            v.merge(card, 1, Integer::sum);
                            if (check(v, k) != null) {
                                functionMap.get(1).apply(new Integer[]{k});
                                v.merge(card, -1, Integer::sum);
                                return null;
                            }
                            v.merge(card, -1, Integer::sum);
                        }
                    }
                    allPayTemp(1, player);
                } else if (index == 4) {
                    allPayTemp(2, player);
                } else {
                    playersHandCard.get(player).merge(player + 6, 2, Integer::sum);
                    playersHandCard.get(index).merge(player + 6, -2, Integer::sum);
                }
                handCard.merge(2, 2, (a, b) -> a * b);
                playersShowInformation.get(player).add(new Integer[]{card, order + 30});
                order = (player + 3) % 4;
                return null;
            }
        });
    }

    private void disCheck(Integer infoType, Integer player) {
        int discard;
        boolean flag;
        do {
            showInformation(player);
            System.out.println("请玩家" + order + "选择牌出牌");
            // TODO: 2021/3/2
            Map<Integer, Integer> handCard = playersHandCard.get(player);
//            List<Integer> integers = toArray(handCard);
//            discard = integers.get((int) (Math.random() * integers.size()));
            double[] doubles = toDouble(order);
            discard = discard(order);
            dateSets.get(order).put(toString(doubles), discard);
//            if (player==0){
            //  "[0.09090909090909091, 0.13636363636363635, 0.1590909090909091, 0.18181818181818182, 0.20454545454545456, 0.25, 0.2727272727272727, 0.2727272727272727, 0.36363636363636365, 0.4318181818181818, 0.4318181818181818, 0.4318181818181818, 0.5, 0.5, 0.0, 0.0, 0.0, 0.0, 0.29545454545454547, -0.36590909090909096, -0.440909090909091, 0.7863636363636364, -0.465909090909091, -0.615909090909091, -0.315909090909091, 0.49090909090909096, -0.315909090909091, -0.615909090909091, -0.7159090909090909, 0.41590909090909095, -0.14090909090909093, -0.2159090909090909, -0.740909090909091, -0.790909090909091, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.06818181818181818, -0.11590909090909095, -0.29090909090909095, -0.14090909090909093, 0.265909090909091, -0.265909090909091, -0.16590909090909092, -0.5659090909090909, -0.36590909090909096, 0.29090909090909095, -0.765909090909091, 0.8454545454545456, -0.815909090909091, 0.24090909090909093, 0.6590909090909091, -0.665909090909091, 0.4090909090909091, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.5227272727272727, -0.640909090909091, -0.765909090909091, -0.7159090909090909, -0.390909090909091, 0.4318181818181818, -0.540909090909091, -0.540909090909091, 0.815909090909091, 0.36590909090909096, 0.615909090909091, 0.690909090909091, -0.515909090909091, 0.440909090909091, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.29545454545454547, 0.16590909090909092, -0.41590909090909095, -0.440909090909091, 0.515909090909091, 0.36590909090909096, -0.465909090909091, 0.5659090909090909, -0.790909090909091, 0.790909090909091, 0.540909090909091, 0.665909090909091, -0.24090909090909093, 0.8159090909090909, -0.265909090909091, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0]"
//                discard = scanner.nextInt();
//            }
            infoType = discard != card ? -infoType : infoType;
            flag = discard(discard, order, infoType);
            if (!flag) {
                System.out.println("出牌失败,没有这张牌");
            }
        } while (!flag);
    }

//    private Integer getRandom(HashMap<Integer, Integer> handCard) {
//        List<Integer> integers = toArray(handCard);
//        return integers.get((new Random().nextInt(integers.size())));
//    }

    @Override
    public String start() {
        do {
            initial();
            //发牌
            playersHandCard.forEach((k, v) -> {
                for (int i = 5; i < 10; i++) {
                    v.put(i, 0);
                }
                for (int i = 0; i < 13; i++) {
                    v.merge(cardMount.remove(0), 1, Integer::sum);
                }
            });
            //定缺
            //第一张为定缺,10m,20s,30p
            playersShowInformation.forEach((k, v) -> {
                Map<Integer, Integer> handCard = playersHandCard.get(k);
                if (k == 0) {
                    handCard.merge(cardMount.get(0), 1, Integer::sum);
                }
                System.out.println(toArray(handCard));
                System.out.println("请玩家" + k + "定缺");
                // TODO: 2021/3/2
//            int chose = scanner.nextInt();
                int chose = deficiency(k);
                v.add(new Integer[]{chose, 7});
                playersHandCard.get(k).put(4, chose);
            });
            playersHandCard.get(0).merge(cardMount.get(0), -1, Integer::sum);
            while (!cardMount.isEmpty() && passNum != 3) {
                order = (++order) % 4;
                Map<Integer, Integer> handCard = playersHandCard.get(order);
                if (handCard.get(0) == null) {
                    System.out.println("牌山余牌:" + cardMount.size() + "张");
                    card = cardMount.remove(0);
                    handCard.merge(card, 1, Integer::sum);
                    if (check(handCard, order) != null) {
                        System.out.println("确认自摸 1 ");
//                    if (scanner.nextInt() == 1) {
                        if (true) {
                            if (cardMount.size() == 0) {
                                tempMulti *= 2;
                            }
                            if (cardMount.size() == 55) {
                                tempMulti *= 1024;
                            }
                            handCard.put(0, order);
                            oneColor(handCard);
                            Integer price = Integer.min(max, handCard.get(1) * handCard.get(2) * tempMulti) + 1;
                            passNum++;
                            allPay(price, order);
                            playersShowInformation.get(order).add(new Integer[]{card, order + 20});
                        }
                    } else {
                        Map<Integer, Integer> gang = gang(handCard);
                        if (!gang.isEmpty()) {
                            tempMulti = 1;
                            int chose = 0;
                            Integer[] keys = gang.keySet().toArray(new Integer[0]);
                            System.out.println("玩家" + order + "进牌:" + card);
                            if (gang.size() != 1) {
                                // TODO: 2021/3/2
                                showInformation(order);
                                System.out.println(keys);
//                            chose = scanner.nextInt();
                                chose = 0;
                            }
                            System.out.println("请玩家" + order + "选择杠牌");
                            //"不开杠 -1 "
//                        chose = scanner.nextInt();
                            if (chose != -1) {
                                Integer key = keys[chose];
                                functionMap.get(2).apply(new Integer[]{order, key, gang.get(key)});
                                continue;
                            }
                        }
//                    Integer discard = getRandom(handCard);
//                    System.out.println("玩家" + order + "准备打出" + discard);
                        System.out.println("玩家" + order + "进牌:" + card);
                        disCheck(1, order);
                    }
                }
            }
            if (passNum != 3) {
                playersHandCard.values().stream().filter(v -> v.get(0) == null).forEach((v -> {
                    String jiaoString = cmGuoMapper.getJiao(toString(v));
                    if (jiaoString != null) {
                        String[] allJiao = jiaoString.split(",");
                        int cardValue = 1;
                        for (String j : allJiao) {
                            int jiao = Integer.parseInt(j);
                            v.merge(jiao, 1, Integer::sum);
                            Integer value = cmGuoMapper.selectById(toString(v)).getCardValue();
                            v.merge(jiao, -1, Integer::sum);
                            cardValue = Integer.max(cardValue, value);
                        }
                        oneColor(v);
                        cardValue = Integer.min(cardValue * v.get(2), max);
                        v.put(3, cardValue);
                    } else {
                        System.out.println("查出无叫!");
                        Integer deficiency = v.get(4);
                        for (int i = 1; i < 10; i++) {
                            v.merge(5, v.get(deficiency + i), Integer::sum);
                        }
                    }
                }));
                playersHandCard.entrySet().stream().filter(entry -> entry.getValue().get(3) == null && entry.getValue()
                        .get(0) == null).forEach((entry -> {
                    Integer key = entry.getKey();
                    Map<Integer, Integer> value = entry.getValue();
                    Integer flowerPig = value.get(5);
                    for (int i = 0; i < 4; i++) {
                        playersHandCard.get(i).put(6 + key, 0);
                        Integer price = playersHandCard.get(i).get(3);
                        if (price != null) {
                            price = flowerPig == 0 ? price : max;
                            score.merge(i, price, Integer::sum);
                            score.merge(key, -price, Integer::sum);
                        }
                    }
                }));
            }
            playersHandCard.forEach((k, v) -> {
                for (int i = 6; i < 10; i++) {
                    score.merge(k, v.get(i), Integer::sum);
                }
            });
            score.forEach((k, v) -> {
                if (v > 0) {
                    cmGuoMapper.insertDateSet(dateSets.get(k));
                }
            });
        } while (true);
    }

    private Map<Integer, Integer> gang(Map<Integer, Integer> handCard) {
        HashMap<Integer, Integer> tempMap = new HashMap<>();
        handCard.forEach((k, v) -> {
            if (k > 10) {
                if (v == 4) {
                    tempMap.put(k, 4);
                } else if (v == -1 && handCard.get(-k) == 1) {
                    tempMap.put(-k, order);
                }
            }
        });
        return tempMap;
    }

    private void oneColor(Map<Integer, Integer> handCards) {
        List<Integer> list = toArray(handCards);
        if (list.size() != 14) {
            Integer temp = list.get(0);
            for (Integer integer : list) {
                if (integer / 10 != temp / 10) {
                    return;
                }
            }
            for (Map.Entry<Integer, Integer> entry : handCards.entrySet()) {
                Integer k = entry.getKey();
                if (k < -10 && -k / 10 != temp / 10) {
                    return;
                }
            }
            handCards.merge(2, 4, (a, b) -> a * b);
        }

    }

    private void allPay(Integer price, Integer index) {
        for (int i = 0; i < 4; i++) {
            if (i != index && playersHandCard.get(i).get(0) == null) {
                score.merge(index, price, Integer::sum);
                score.merge(i, -price, Integer::sum);
            }
        }
    }

    private void allPayTemp(Integer price, Integer index) {
        for (int i = 0; i < 4; i++) {
            if (i != index && playersHandCard.get(i).get(0) == null) {
                playersHandCard.get(i).merge(index + 6, -price, Integer::sum);
                playersHandCard.get(index).merge(index + 6, price, Integer::sum);
            }
        }
    }

    @Override
    public Boolean discard(Integer card, Integer index, Integer infoType) {
        Map<Integer, Integer> handCard = playersHandCard.get(index);
        List<Integer> list = toArray(handCard);
        Integer integer = handCard.get(card);
        if (card < 10 || integer == null || integer == 0) {
            return false;
        }
        CardHuServiceImpl.card = card;
        handCard.merge(card, -1, Integer::sum);
        System.out.println("玩家" + index + "弃牌:" + card);
        System.out.println(toArray(handCard));
        playersShowInformation.get(index).add(new Integer[]{card, infoType});
        //弃牌的碰胡杠牌判定
        hu.set(false);
        playersHandCard.forEach((k, v) -> {
            playersAction.get(k).clear();
            if (v.get(0) == null && !k.equals(order)) {
                v.merge(card, 1, Integer::sum);
                if (check(v, k) != null) {
                    playersAction.get(k).add(1);
                }
                v.merge(card, -1, Integer::sum);
                Integer num = v.get(card);
                Integer deficiency = playersShowInformation.get(k).get(0)[0];
                if (num > 1 && (deficiency > card || card > deficiency + 10)) {
                    playersAction.get(k).add(0);
                    if (num == 3) {
                        playersAction.get(k).add(2);
                    }
                }
            }
        });
        //碰胡杠选择
        playersAction.forEach((k, v) -> {
            if (!v.isEmpty()) {
                // TODO: 2021/3/4
                Integer type = v.get(v.size() - 1);
                if (type == 1) {
                    hu.set(true);
                }
                showInformation(k);
                System.out.println(v);
                System.out.println("当前牌为" + card);
                System.out.println("请玩家" + k + "选择碰胡杠操作");
                v.clear();
//                type = scanner.nextInt();
                v.add(type);
            }
        });
        for (int i = 1; i < 4; i++) {
            int k = (index + i) % 4;
            List<Integer> action = playersAction.get(k);
            handCard = playersHandCard.get(k);
            if (!action.isEmpty()) {
                List<Integer> v = action;
                Integer type = v.get(0);
//                "过牌 -1 "
                Function function = functionMap.get(type);
                if (function != null) {
                    function.apply(new Integer[]{k, card, order, handCard.get(1)});
                }
            }
        }
        tempMulti = 1;
        return true;
    }

    @Override
    public String operate(Integer card, Integer index, Integer type) {
        for (int j = 0; j < 2830962; j += 100000) {
            List<Object> cardHus = cmGuoMapper.selectObjs(new QueryWrapper<CardHu>().select("card_key").last("limit " + j + " , 100000"));
            cardHus.stream().map(Object::toString).forEach(cardHu -> {
                Map<Integer, Integer> tempMap = generateHandCard();
                for (int i = 0; i < cardHu.length(); i += 2) {
                    Integer integer = Integer.valueOf(cardHu.substring(i, i + 2));
                    tempMap.merge(integer, 1, Integer::sum);
                }
                tempMap.forEach((k, v) -> {
                    if (v != 0) {
                        tempMap.merge(k, -1, Integer::sum);
                        String key = toString(tempMap);
                        m.merge(key, String.valueOf(k), (v1, v2) -> v1 + "," + v2);
                        tempMap.merge(k, 1, Integer::sum);
                    }
                });
            });
        }
        List<CardHu> list = new ArrayList<>();
        for (Map.Entry<String, String> entry : m.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (list.size() == 100000) {
                cmGuoMapper.ting(list);
                list = new ArrayList<>();
            }
            list.add(CardHu.builder().cardKey(key).cardJiao(value).build());
        }
        cmGuoMapper.ting(list);
        return null;
    }

    @Override
    public String chose(Integer card, Integer index, Boolean peng, Boolean hu, Boolean gang) {
        return null;
    }

    @Override
    @Transactional
    public String generateCardKey() {
        Map<Integer, Integer> handCard = generateHandCard();
        for (int i = 0; i < 5; i++) {
            generateTarget(handCard, true, i, 0, 11);
        }
        generateNaNa(handCard, 0);
        List<CardHu> list = map.entrySet().stream().map(entry -> CardHu.builder()
                .cardKey(entry.getKey()).cardValue(entry.getValue()).build()).collect(Collectors.toList());
        int size = list.size();
        for (int i = 0, j; i < size; i += 100000) {
            j = Math.min(i + 100000, size);
            List<CardHu> cardHus = list.subList(i, j);
            cmGuoMapper.generateCardKey(cardHus);
        }
        return null;
    }

    private void generateNaNa(Map<Integer, Integer> handCard, int currentTarget) {
        for (int i = 11; i < 30; i++) {
            HashMap<Integer, Integer> clone = (HashMap<Integer, Integer>) ((HashMap<Integer, Integer>) handCard).clone();
            if (i % 10 == 0) i++;
            Integer currentNum = clone.get(i);
            if (currentNum != 4) {
                clone.put(i, currentNum + 2);
                currentTarget++;
                if (currentTarget == 7) {
                    currentTarget--;
                    AtomicInteger price = new AtomicInteger(4);
                    AtomicReference<String> huKey = new AtomicReference<>("");
                    AtomicReference<String> huKey2 = new AtomicReference<>("");
                    AtomicReference<String> huKey3 = new AtomicReference<>("");
                    clone.forEach((key, value) -> {
                        if (value != 0) {
                            if (value == 4) {
                                price.updateAndGet(v -> v * 2);
                            }
                            do {
                                int key1 = key > 20 ? key + 10 : key;
                                huKey.updateAndGet(v1 -> v1.concat(String.valueOf(key)));
                                huKey2.updateAndGet(v1 -> v1.concat(String.valueOf(key + 10)));
                                huKey3.updateAndGet(v1 -> v1.concat(String.valueOf(key1)));
                                value--;
                            } while (value > 0);
                        }
                    });
                    int m = 0, s = 0;
                    for (int j = 11; j < 30; j++) {
                        if (j % 10 == 0) j++;
                        if (j < 20) {
                            m += clone.get(j);
                        } else {
                            s += clone.get(j);
                        }
                    }
                    if (m == 14 || s == 14) {
                        price.updateAndGet(v -> v * 4);
                    }
                    map.put(huKey.get(), price.get());
                    map.put(huKey2.get(), price.get());
                    map.put(huKey3.get(), price.get());
                } else {
                    generateNaNa(clone, currentTarget);
                    currentTarget--;
                }
            }
        }
    }

    private void getHuKey(HashMap<Integer, Integer> handCard, int target) {
        handCard.forEach((k, v) -> {
            if (v < 3 && k < 30) {
                AtomicReference<String> huKey = new AtomicReference<>("");
                AtomicReference<String> huKey2 = new AtomicReference<>("");
                AtomicReference<String> huKey3 = new AtomicReference<>("");
                AtomicInteger price = new AtomicInteger(2);
                if (target == 0) {
                    price.updateAndGet(p -> p * 2);
                }
                handCard.put(k, v + 2);
                handCard.forEach((key, value) -> {
                    if (value != 0) {
                        if (value == 4) {
                            price.updateAndGet(p -> p * 2);
                        }
                        do {
                            int key1 = key > 20 ? key + 10 : key;
                            huKey.updateAndGet(v1 -> v1.concat(String.valueOf(key)));
                            huKey2.updateAndGet(v1 -> v1.concat(String.valueOf(key + 10)));
                            huKey3.updateAndGet(v1 -> v1.concat(String.valueOf(key1)));
                            value--;
                        } while (value > 0);
                    }
                });
                int m = 0, s = 0;
                for (int i = 11; i < 30; i++) {
                    if (i % 10 == 0) i++;
                    if (i < 20) {
                        m += handCard.get(i);
                    } else {
                        s += handCard.get(i);
                    }
                }
                if (m == 14 || s == 14) {
                    price.updateAndGet(p -> p * 4);
                }
                map.put(huKey.get(), price.get());
                map.put(huKey2.get(), price.get());
                map.put(huKey3.get(), price.get());
                handCard.put(k, v);
            }
        });
    }

    private void generateTarget(Map<Integer, Integer> handCard, boolean flag, int target, int currentTarget, int start) {
        int newTarget;
        for (int i = start; i < 30; i++) {
            newTarget = currentTarget;
            HashMap<Integer, Integer> clone = (HashMap<Integer, Integer>) ((HashMap<Integer, Integer>) handCard).clone();
            if (flag) {
                if (i % 10 == 0) i++;
                newTarget = generateKe(clone, i, target, currentTarget);
                if (i == 29) {
                    flag = false;
                    i = 10;
                }
//            } else {
//                if ((i + 2) % 10 == 0) {
//                    i += 3;
//                }
//                newTarget = generateSun(clone, i, target, currentTarget);
            }
            if (newTarget != currentTarget && newTarget != target) {
                generateTarget(clone, true, target, newTarget, 11);
            }
            if (newTarget == target) {
                getHuKey(clone, target);
            }
        }
    }

    private static Integer generateSun(HashMap<Integer, Integer> handCard, int i, int target, int currentTarget) {
        if (currentTarget == target || i == 31) return currentTarget;
        Integer num1 = handCard.get(i);
        Integer num2 = handCard.get(i + 1);
        Integer num3 = handCard.get(i + 2);
        if (num1 < 4 && num2 < 4 && num3 < 4) {
            for (int j = 0; j < 3; j++) {
                handCard.put(i + j, handCard.get(i + j) + 1);
            }
            return ++currentTarget;
        }
        return currentTarget;
    }

    private static Integer generateKe(HashMap<Integer, Integer> handCard, int i, int target, int currentTarget) {
        if (currentTarget == target) return currentTarget;
        if (handCard.get(i) < 2) {
            handCard.put(i, handCard.get(i) + 3);
            return ++currentTarget;
        }
        return currentTarget;
    }

    /*手牌生成
    参数key详解:
    0: 胡牌判定 v=自身为自摸
    1: 胡牌基础倍率
    2: 额外倍率 天胡1024倍 杠2倍(不包含带根) 杠上开花2倍 海底2倍 自摸加一倍底
    3: 听牌判定 null 为未听牌 值为最大听牌倍率
    4: 定缺信息 10m20s30p
    5: 花猪判定 >0为花猪
    5: 0
    11~19 m -19~-11 v=-1代表m碰出来的的明刻 0~3为 杠 档v=自身代表扒杠 n=4代表暗杠
    21~29 p -29~-21 v=-1代表p碰出来的的明刻 0~3为 杠 档v=自身代表扒杠 n=4代表暗杠
    31~39 s -39~-31 v=-1代表s碰出来的的明刻 0~3为 杠 档v=自身代表扒杠 n=4代表暗杠
     */
    public static Map<Integer, Integer> generateHandCard() {
        HashMap<Integer, Integer> handCard = new HashMap<>();
        int card = 11;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++, card++) {
                handCard.put(card, 0);
            }
            card = i == 0 ? 21 : 31;
        }
        handCard.put(2, 1);
        return handCard;
    }

    private static List<Integer> generateCardsMount() {
        ArrayList<Integer> cardsMount = new ArrayList<>();
        int card = 11;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++, card++) {
                for (int k = 0; k < 4; k++) {
                    cardsMount.add(card);
                }
            }
            card = i == 0 ? 21 : 31;
        }
        Collections.shuffle(cardsMount);
        return cardsMount;
    }

    private static void initial() {
        order = 3;
        passNum = 0;
        tempMulti = 1;
        cardMount = generateCardsMount();
        //手牌初始化
        playersHandCard = new HashMap<>();
        playersShowInformation = new HashMap<>();
        //弃牌初始化
        playersAction = new HashMap<>();
        for (int i = 0; i < 4; i++) {
            score.put(i, 0);
            dateSets.put(i, new HashMap<>());
            playersAction.put(i, new ArrayList<>());
            playersHandCard.put(i, generateHandCard());
            playersShowInformation.put(i, new ArrayList<>());
        }
    }

    private void showInformation(Integer player) {
        Map<Integer, Integer> handCard = playersHandCard.get(player);
        playersShowInformation.forEach((k, v) -> {
            v.forEach(arr -> System.out.print(Arrays.toString(arr) + " "));
            System.out.println("");
        });
        System.out.println(toArray(handCard));
    }


    private Integer check(Map<Integer, Integer> handCard, Integer player) {
        String key = toString(handCard);
        HashMap<Integer, Integer> clone = (HashMap<Integer, Integer>) ((HashMap<Integer, Integer>) handCard).clone();
        CardHu cardHu = cmGuoMapper.selectById(key);
        if (cardHu != null) {
            Integer cardValue = cardHu.getCardValue();
            if (key.length() != 14) {
                handCard.forEach((k, v) -> {
                    if (k < -10) {
                        clone.merge(k, 3, Integer::sum);
                    }
                });
                cardValue = cmGuoMapper.selectById(toString(clone)).getCardValue();
            }
            System.out.println("玩家" + player + "可以胡牌,牌型:" + toArray(handCard) + " ,倍率为" + cardValue);
            handCard.put(1, cardValue);
            return cardValue;
        } else {
            return null;
        }
    }

    private List<Integer> toArray(Map<Integer, Integer> handCard) {
        ArrayList<Integer> list = new ArrayList<>();
        handCard.forEach((key, value) -> {
            if (value != 0 && key > 10) {
                do {
                    list.add(key);
                    value--;
                } while (value > 0);
            }
        });
        return list;
    }

    private String toString(Map<Integer, Integer> handCard) {
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

    private String toString(double[] doubles) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < doubles.length; i++) {
            result = result.append(doubles[i]);
            if (i == doubles.length - 1) return result.toString();
            result.append(",");
        }
        return null;
    }

    public int discard(int player) {
        Map<Integer, Integer> handCard = playersHandCard.get(player);
        Integer deficiency = handCard.get(4);
        for (int t = 2; t < 11; t++) {
            int i = t % 2 == 0 ? 10 - t / 2 : t / 2;
            int key = deficiency + i;
            if (handCard.get(key) != 0) {
                return key;
            }
        }
        for (int t = 2; t < 11; t++) {
            int j = t % 2 == 0 ? 10 - t / 2 : t / 2;
            for (int i = 10; i < 40; i += 10) {
                int key = i + j;
                if (handCard.get(key) == 1) {
                    int keyR1 = key - 1 >= deficiency && key - 1 < deficiency + 10 || (key - 1) % 10 == 0 ? 5 : key - 1;
                    int keyA1 = key + 1 >= deficiency && key + 1 < deficiency + 10 || (key + 1) % 10 == 0 ? 5 : key + 1;
                    int keyR2 = key - 2 >= deficiency && key - 2 < deficiency + 10
                            || (key - 1) % 10 == 0 || (key - 2) % 10 == 0 ? 5 : key - 2;
                    int keyA2 = key + 2 >= deficiency && key + 2 < deficiency + 10
                            || (key + 1) % 10 == 0 || (key + 2) % 10 == 0 ? 5 : key + 2;
                    if (i != deficiency && handCard.get(keyR1) == 0 && handCard.get(keyA1) == 0
                            && handCard.get(keyR2) == 0 && handCard.get(keyA2) == 0) {
                        return key;
                    }
                }
            }
        }
        for (int t = 2; t < 11; t++) {
            int j = t % 2 == 0 ? 10 - t / 2 : t / 2;
            for (int i = 10; i < 40; i += 10) {
                int key = i + j;
                if (handCard.get(key) == 1) {
                    int keyR1 = key - 1 >= deficiency && key - 1 < deficiency + 10 || (key - 1) % 10 == 0 ? 5 : key - 1;
                    int keyA1 = key + 1 >= deficiency && key + 1 < deficiency + 10 || (key + 1) % 10 == 0 ? 5 : key + 1;
                    int keyR2 = key - 2 >= deficiency && key - 2 < deficiency + 10
                            || (key - 1) % 10 == 0 || (key - 2) % 10 == 0 ? 5 : key - 2;
                    int keyA2 = key + 2 >= deficiency && key + 2 < deficiency + 10
                            || (key + 1) % 10 == 0 || (key + 2) % 10 == 0 ? 5 : key + 2;
                    if (i != deficiency && handCard.get(keyR1) == 0 && handCard.get(keyA1) == 0
                            && handCard.get(keyR2) != 0 && handCard.get(keyA2) != 0) {
                        return key;
                    }
                }
            }
        }
        List<Integer> integers = toArray(handCard);
        return integers.get((int) (Math.random() * integers.size()));
    }

    public int deficiency(int player) {
        Map<Integer, Integer> handCard = playersHandCard.get(player);
        List<Integer> mCards = new ArrayList<>();
        List<Integer> sCards = new ArrayList<>();
        List<Integer> pCards = new ArrayList<>();
        handCard.forEach((k, v) -> {
            if (10 < k && k < 20 && v != 0) {
                do {
                    mCards.add(k);
                    v--;
                } while (v != 0);
            }
            if (20 < k && k < 30 && v != 0) {
                do {
                    sCards.add(k);
                    v--;
                } while (v != 0);
            }
            if (k > 30 && v != 0) {
                do {
                    pCards.add(k);
                    v--;
                } while (v != 0);
            }
        });
        int m = mCards.size();
        int s = sCards.size();
        int p = pCards.size();
        mCards.add(10);
        sCards.add(20);
        pCards.add(30);
        List<Integer> min = mCards;
        List<Integer> minMS = s > m ? mCards : sCards;
        List<Integer> minSP = s < p ? sCards : pCards;
        int sizeMS = minMS.size() - 1;
        int sizeSP = minSP.size() - 1;
        if (sizeMS != sizeSP) {
            min = sizeMS < sizeSP ? minMS : minSP;
        } else {
            if (sizeMS == 1) {
                int absMS = Math.abs(5 - minMS.get(0));
                int absSP = Math.abs(5 - minSP.get(0));
                if (absMS != absSP) {
                    min = absMS > absSP ? minMS : minSP;
                } else {
                    min = Math.random() > 0.5 ? minMS : minSP;
                }
            }
            if (sizeMS == 2) {
                int ddMS = minMS.get(0).equals(minMS.get(1)) ? 1 : 0;
                int ddSP = minSP.get(0).equals(minSP.get(1)) ? 1 : 0;
                if (ddMS != ddSP) {
                    min = ddMS < ddSP ? minMS : minSP;
                } else {
                    int absMS = Math.abs(5 - minMS.get(0));
                    int absSP = Math.abs(5 - minSP.get(0));
                    if (absMS != absSP) {
                        min = absMS < absSP ? minMS : minSP;
                    } else {
                        min = Math.random() > 0.5 ? minMS : minSP;
                    }
                }
            }
            if (sizeMS == 3) {
                int valueMS = cardValue(minMS);
                int valueSP = cardValue(minSP);
                if (valueMS != valueSP) {
                    min = valueMS < valueSP ? minMS : minSP;
                } else {
                    min = Math.random() > 0.5 ? minMS : minSP;
                }
            }
        }
        return min.get(min.size() - 1);
    }

    private int cardValue(List<Integer> cards) {
        int size = cards.size();
        int value = 0;
        if (size == 1) {
            value = -Math.abs(5 - cards.get(0));
        } else if (size == 2) {
            value = (cards.get(0).equals(cards.get(1)) ? 5 : 0) + Math.abs(5 - cards.get(0));
        } else if (size == 3) {
            if (cards.get(0).equals(cards.get(1)) && cards.get(1).equals(cards.get(2))) {
                value = 30 + Math.abs(5 - cards.get(0));
            } else if (cards.get(0) + 1 == (cards.get(1)) && cards.get(1) + 1 == (cards.get(2))) {
                value = 29;
            } else {
                for (int i = 1; i < 3; i++) {
                    if (cards.get(0).equals(cards.get(i))) {
                        int j = i == 1 ? 2 : 1;
                        value = (Math.abs(5 - cards.get(0)) + 2) * 4 - Math.abs(5 - cards.get(j));
                    }
                    if (cards.get(0) + 1 == (cards.get(i))) {
                        value = 3;
                    }
                    if (cards.get(0) + 2 == (cards.get(i))) {
                        value += 1;
                    }
                }
            }
        } else {
            if (cards.get(0).equals(cards.get(1)) && cards.get(1).equals(cards.get(2)) && cards.get(2).equals(cards.get(3))) {
                value = 100;
            } else if (cards.get(0).equals(cards.get(1)) && cards.get(1).equals(cards.get(2))
                    || cards.get(1).equals(cards.get(2)) && cards.get(2).equals(cards.get(3))) {
                value = 30 + Math.abs(5 - cards.get(1));
            } else if (cards.get(0) + 1 == (cards.get(1)) && cards.get(1) + 1 == (cards.get(2))
                    || cards.get(1) + 1 == (cards.get(2)) && cards.get(2) + 1 == (cards.get(3))) {
                value = 29;
            } else if (cards.get(0).equals(cards.get(1)) && cards.get(2).equals(cards.get(3))) {
                value = 28;
            } else if (cards.get(0).equals(cards.get(1)) || cards.get(1).equals(cards.get(2))
                    || cards.get(2).equals(cards.get(3))) {
                value = 20 + Math.abs(5 - cards.get(0));
            } else if (cards.get(0) + 1 == (cards.get(1)) && cards.get(2) + 1 == (cards.get(3))) {
                value = 10;
            } else if (cards.get(0) + 1 == (cards.get(1)) || cards.get(1) + 1 == (cards.get(2))
                    || cards.get(2) + 1 == (cards.get(3))) {
                value = 5;
            } else if (cards.get(0) + 2 == (cards.get(1)) && cards.get(1) + 2 == (cards.get(2))
                    || cards.get(1) + 2 == (cards.get(2)) && cards.get(2) + 2 == (cards.get(3))) {
                value = 3;
            }
        }
        return value;
    }

    public double[] toDouble(int player) {
        final int[] i = {0};
        double[] result = new double[138];
        Map<Integer, Integer> handCard = playersHandCard.get(player);
        handCard.forEach((key, value) -> {
            if (value != 0 && key > 10) {
                do {
                    int d = (key - 10) * 1000 / 150;
                    result[i[0]] = (double) d / 1000;
                    value--;
                    i[0]++;
                } while (value > 0);
            }
            if (key < -10) {
                if (value == -1) {
                    for (int j = 0; j < 3; j++) {
                        int d = (-key - 10) * 1000 / 150;
                        result[i[0]] = (double) d / 1000;
                        i[0]++;
                    }
                } else {
                    for (int j = 0; j < 4; j++) {
                        int d = (-key - 10) * 1000 / 150;
                        result[i[0]] = (double) d / 1000;
                        i[0]++;
                    }
                }
            }
        });
        for (int j = 0; j < 4; j++) {
            int k = (j + player) % 4;
            List<Integer[]> v = playersShowInformation.get(k);
            i[0] = 18 + k * 30;
            v.forEach(info -> {
                Integer card = info[0];
                Integer type = info[1];
                if (type <= 0) {
                    int d = (card - 10) * 1000 / 150;
                    result[i[0]] = (double) d / 1000;
                } else if (type == 1) {
                    int d = (card + 20) * 1000 / 150;
                    result[i[0]] = (double) d / 1000;
                } else if (type > 10 && type < 20) {
                    int d = (card + 50) * 1000 / 150;
                    result[i[0]] = (double) d / 1000;
                } else if (type > 30) {
                    int d = (card + 80) * 1000 / 150;
                    result[i[0]] = (double) d / 1000;
                } else {
                    int d = (card + 110) * 1000 / 150;
                    result[i[0]] = (double) d / 1000;
                }
                i[0]++;
            });
        }
        return result;
    }

}
