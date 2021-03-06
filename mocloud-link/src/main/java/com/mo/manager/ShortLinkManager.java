package com.mo.manager;

import com.mo.model.ShortLinkDO;

/**
 * Created by mo on 2022/2/19
 */
public interface ShortLinkManager {

    Integer addShortLink(ShortLinkDO shortLinkDO);

    ShortLinkDO findByShortLinCode(String shortLinkCode);

    Integer del(ShortLinkDO shortLinkDO);

    Integer update(ShortLinkDO shortLinkDO);
}
