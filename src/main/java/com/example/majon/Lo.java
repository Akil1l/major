package com.example.majon;

import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @program: demo
 * @description: hu
 * @author: Akil
 * @create: 2021-02-22 15:36
 **/

public class Lo {
    private static HashSet<String> cardMount = new HashSet<>();

    public static void main(String[] args) {
        int target = 0;
        HashMap<Integer, Integer> handCard = generateHandCard();
        generateTarget(handCard, true, target, 1);
        System.out.println(cardMount.size());
    }

    private static void getHuKey(HashMap<Integer, Integer> handCard) {
        handCard.forEach((k, v) -> {
            if (v < 3 && k > 10) {
                AtomicReference<String> huKey = new AtomicReference<>("");
                handCard.put(k, v + 2);
                handCard.forEach((key, value) -> {
                    if (value != 0) {
                        do {
                            huKey.updateAndGet(v1 -> v1.concat(String.valueOf(key)));
                            value--;
                        } while (value > 0);
                    }
                });
                handCard.put(k, v);
                String s = huKey.get();
                if (s.length()!=28){
                    System.out.println("error");
                }
                cardMount.add(s);
            }
        });
    }

    private static void generateTarget(HashMap<Integer, Integer> handCard, boolean flag, int target, int start) {
        int newTarget;
        for (int i = start; i < 30; i++) {
            newTarget = target;
            HashMap<Integer, Integer> clone = (HashMap<Integer, Integer>) handCard.clone();
            if (flag) {
                if (i % 10 == 0) i++;
                newTarget = generateKe(clone, i, target);
                if (i == 29) {
                    flag = false;
                    i = 1;
                }
            } else {
                if ((i + 2) % 10 == 0) {
                    i += 3;
                }
                newTarget = generateSun(clone, i, target);
            }
            if (newTarget != target && newTarget != 4) {
                generateTarget(clone, true, newTarget, 1);
            }
            if (newTarget == 4) {
                getHuKey(clone);
            }
        }
    }

    private static Integer generateSun(HashMap<Integer, Integer> handCard, int i, int target) {
        if (target == 4 || i == 31 || i < 10) return target;
        Integer num1 = handCard.get(i);
        Integer num2 = handCard.get(i + 1);
        Integer num3 = handCard.get(i + 2);
        if (num1 < 4 && num2 < 4 && num3 < 4) {
            for (int j = 0; j < 3; j++) {
                handCard.put(i + j, handCard.get(i + j) + 1);
            }
            return ++target;
        }
        return target;
    }

    private static Integer generateKe(HashMap<Integer, Integer> handCard, int i, int target) {
        if (target == 4 || i < 10) return target;
        if (handCard.get(i) < 2) {
            handCard.put(i, handCard.get(i) + 3);
            return ++target;
        }
        return target;
    }

    public static HashMap<Integer, Integer> generateHandCard() {
        HashMap<Integer, Integer> handCard = new HashMap<>();
        int card = 1;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++, card++) {
                handCard.put(card, 0);
            }
            card = i == 0 ? 11 : 21;
        }
        return handCard;
    }
}
