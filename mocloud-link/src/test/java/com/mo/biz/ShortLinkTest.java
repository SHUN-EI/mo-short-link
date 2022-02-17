package com.mo.biz;

import com.google.common.hash.Hashing;
import com.mo.LinkApplication;
import com.mo.component.ShortLinkService;
import com.mo.utils.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Random;

/**
 * Created by mo on 2022/2/17
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = LinkApplication.class)
@Slf4j
public class ShortLinkTest {

    @Autowired
    private ShortLinkService shortLinkService;

    /**
     * 测试生成短链码
     */
    @Test
    public void testCreatShortLinkCode() {
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            int num1 = random.nextInt(10);
            int num2 = random.nextInt(10000000);
            int num3 = random.nextInt(10000000);

            String originalUrl = num1 + "mo" + num2 + ".com" + num3;
            String shortLinkCode = shortLinkService.createShortLinkCode(originalUrl);
            log.info("originalUrl:" + originalUrl + ", shortLinkCode=" + shortLinkCode);
        }
    }

    /**
     * 测试MurmurHash算法
     */
    @Test
    public void testMurmurHash(){

        for(int i=0; i<5; i++){

            String originalUrl = "https://xdclass.net?id="+ CommonUtil.generateUUID()+"pwd="+CommonUtil.getStringNumRandom(7);

            long murmur3_32 = Hashing.murmur3_32().hashUnencodedChars(originalUrl).padToLong();
            log.info("murmur3_32={}",murmur3_32);
        }
    }
}
