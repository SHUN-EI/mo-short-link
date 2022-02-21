package com.mo.strategy;

import com.google.common.collect.Lists;
import org.apache.commons.math3.util.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mo on 2022/2/19
 * 数据库分库的库位配置-权重配置
 */
public class ShardingDBWeightConfig {


    /**
     * 存储数据库位置编号
     */
    private static final List<Pair<String, Integer>> dbPrefixList = Lists.newArrayList();


    //配置启用那些库的前缀, ds0,ds1,dsa,再加上每个库的权重
    static {
        dbPrefixList.add(new Pair("0", 1));
        dbPrefixList.add(new Pair("1", 2));
        dbPrefixList.add(new Pair("a", 3));

    }

    /**
     * 获取随机数据库的前缀
     *
     * @return
     */
    public static String getRandomDBPrefix() {
        //带权重随机数
        WeightRandom<String, Integer> random = new WeightRandom<>(dbPrefixList);
        return random.random();
    }


}
