package com.mo.service;

import com.mo.vo.ShortLinkVO;

/**
 * Created by mo on 2022/2/21
 */
public interface ShortLinkService {

    String createShortLinkCode(String link);

    ShortLinkVO parseShortLinkCode(String shortLinkCode);
}
