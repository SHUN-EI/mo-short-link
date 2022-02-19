package com.mo.strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by mo on 2022/2/18
 * 数据库分库的库位配置
 */
public class ShardingDBConfig {


    /**
     * 存储数据库位置编号
     */
    private static final List<String> dbPrefixList = new ArrayList<>();

    private static Random random = new Random();

    //配置启用那些库的前缀, ds0,ds1,dsa
    static {
        dbPrefixList.add("0");
        dbPrefixList.add("1");
        dbPrefixList.add("a");
    }

    /**
     * 获取随机数据库的前缀
     *
     * @return
     */
    public static String getRandomDBPrefix() {

        int index = random.nextInt(dbPrefixList.size());
        return dbPrefixList.get(index);
    }
}
