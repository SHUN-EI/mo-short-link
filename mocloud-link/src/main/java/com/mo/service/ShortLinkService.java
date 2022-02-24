package com.mo.service;

import com.mo.model.EventMessage;
import com.mo.request.ShortLinkAddRequest;
import com.mo.utils.JsonData;
import com.mo.vo.ShortLinkVO;

/**
 * Created by mo on 2022/2/21
 */
public interface ShortLinkService {

    String createShortLinkCode(String link);

    ShortLinkVO parseShortLinkCode(String shortLinkCode);

    JsonData createShortLink(ShortLinkAddRequest request);

    Boolean handlerAddShortLink(EventMessage eventMessage);
}
