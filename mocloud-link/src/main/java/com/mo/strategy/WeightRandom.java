package com.mo.strategy;

import com.google.common.base.Preconditions;
import org.apache.commons.math3.util.Pair;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by mo on 2022/2/19
 * 加权随机数
 * 加权库表位算法
 */
public class WeightRandom<K, V extends Number> {

    private TreeMap<Double, K> weightMap = new TreeMap<>();

    /**
     * 如有4个元素A、B、C、D，权重分别为1、2、3、4，
     * 随机结果中A:B:C:D的比例要为1:2:3:4
     * 累加每个元素的权重A(1)-B(3)-C(6)-D(10)，
     * 则4个元素的的权重管辖区间分别为[0,1)、[1,3)、[3,6)、[6,10)。
     * 然后随机出一个[0,10)之间的随机数。落在哪个区间，则该区间之后的元素即为按权重命中的元素
     *
     * @param list
     */
    public WeightRandom(List<Pair<K, V>> list) {
        Preconditions.checkNotNull(list, "list can NOT be null!");
        list.forEach(pair -> {
            Preconditions.checkArgument(pair.getValue().doubleValue() > 0, String.format("非法权重值：pair=%s", pair));

            //统一转为double
            double lastWeight = this.weightMap.size() == 0 ? 0 : this.weightMap.lastKey().doubleValue();
            //权重累加
            this.weightMap.put(pair.getValue().doubleValue() + lastWeight, pair.getKey());
        });
    }

    public K random() {
        double randomWeight = this.weightMap.lastKey() * Math.random();
        SortedMap<Double, K> tailMap = this.weightMap.tailMap(randomWeight, false);
        return this.weightMap.get(tailMap.firstKey());
    }
}
