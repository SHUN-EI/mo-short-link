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


    /**
     * 获取数据库的库位
     * 短链码的hashCode 取模 数据库数量——> 生成库位
     *
     * @param code
     * @return
     */
    public static String getRandomDBPrefix(String code) {

        int hashCode = code.hashCode();
        //hashCode可能有负数,这里取绝对值
        int index = Math.abs(hashCode) % dbPrefixList.size();

        return dbPrefixList.get(index);
    }
}
