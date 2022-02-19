package com.mo.manager;

import com.mo.model.ShortLinkDO;

/**
 * Created by mo on 2022/2/19
 */
public interface ShortLinkManager {

    int addShortLink(ShortLinkDO shortLinkDO);

    ShortLinkDO findByShortLinCode(String shortLinkCode);

    int del(String shortLinkCode,Long accountNo);

}
