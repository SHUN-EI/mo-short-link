package com.mo.strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by mo on 2022/2/18
 * 数据表分表的表位配置
 */
public class ShardingTableConfig {

    /**
     * 存储数据表位置编号
     */
    private static final List<String> tableSuffixList = new ArrayList<>();

    private static Random random = new Random();

    //配置启用那些表的后缀 short_link_0,short_link_a
    static {
        tableSuffixList.add("0");
        tableSuffixList.add("a");
    }

    /**
     * 获取随机数据表的后缀
     *
     * @return
     */
    public static String getRandomTableSuffix() {
        int index = random.nextInt(tableSuffixList.size());
        return tableSuffixList.get(index);
    }

}
