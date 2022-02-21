package com.mo.biz;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mo.strategy.WeightRandom;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.util.Pair;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

/**
 * Created by mo on 2022/2/19
 */
@Slf4j
public class WeightRandomTest {

    private List<Pair<String, Integer>> list;
    private WeightRandom<String, Integer> random;


    @Test
    public void random() {
        Map<String, Integer> countMap = Maps.newHashMap();
        //分配10000次
        for (int i = 0; i < 10000; i++) {
            String randomKey = random.random();
            countMap.put(randomKey, countMap.getOrDefault(randomKey, 0) + 1);
        }

        for (Pair<String, Integer> pair : list) {
            log.info("{}:{}", pair.getKey(), countMap.get(pair.getKey()));
        }
    }

    @Before
    public void init() {
        list = Lists.newArrayList();
        list.add(new Pair<>("A", 1));
        list.add(new Pair<>("B", 2));
        list.add(new Pair<>("C", 3));
        list.add(new Pair<>("D", 4));
        //list.add(new Pair<>("E", 0));//0无效

        this.random = new WeightRandom(list);
    }
}
