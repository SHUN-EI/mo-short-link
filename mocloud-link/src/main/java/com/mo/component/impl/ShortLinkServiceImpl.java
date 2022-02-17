package com.mo.component.impl;

import com.mo.component.ShortLinkService;
import com.mo.utils.CommonUtil;
import org.springframework.stereotype.Service;

/**
 * Created by mo on 2022/2/17
 */
@Service
public class ShortLinkServiceImpl implements ShortLinkService {

    /**
     * 生成短链码
     *
     * @param link
     * @return
     */
    @Override
    public String createShortLinkCode(String link) {

        long murmurHash32 = CommonUtil.murmurHash32(link);
        //进制转换 10进制转62进制
        String code = CommonUtil.encodeToBase62(murmurHash32);

        return code;
    }
}
