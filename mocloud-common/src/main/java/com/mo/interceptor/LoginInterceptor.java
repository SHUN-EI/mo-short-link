package com.mo.interceptor;

import com.mo.enums.BizCodeEnum;
import com.mo.model.LoginUserDTO;
import com.mo.utils.CommonUtil;
import com.mo.utils.JWTUtil;
import com.mo.utils.JsonData;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by mo on 2022/2/15
 * 登录拦截器
 */
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {

    public static ThreadLocal<LoginUserDTO> threadLocal = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //放行OPTIONS方法，前后端分离情况下，会有些OPTIONS方法来试探网络
        if (HttpMethod.OPTIONS.toString().equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpStatus.NO_CONTENT.value());
            return true;
        }

        String token = request.getHeader("token");
        if (null == token) {
            token = request.getParameter("token");
        }

        if (StringUtils.isNotBlank(token)) {
            Claims claims = JWTUtil.checkJWT(token);

            if (null == claims) {
                //用户未登录
                CommonUtil.sendJsonMessage(response, JsonData.buildResult(BizCodeEnum.ACCOUNT_UNLOGIN));
                return false;
            }

            Long id = Long.valueOf(claims.get("id").toString());
            Long accountNo = Long.valueOf(claims.get("account_no").toString());
            String headImg = (String) claims.get("head_img");
            String username = (String) claims.get("user_name");
            String mail = (String) claims.get("mail");
            String phone = (String) claims.get("phone");
            String auth = (String) claims.get("auth");

            LoginUserDTO loginUserDTO = new LoginUserDTO(id, accountNo, username, headImg, mail, phone, auth);

            //通过 attribute传递用户信息
            //request.setAttribute("LoginUserDTO", userDTO);

            //通过threadLocal 传递用户登录信息
            threadLocal.set(loginUserDTO);
            return true;
        }

        //token为空
        CommonUtil.sendJsonMessage(response, JsonData.buildResult(BizCodeEnum.ACCOUNT_UNLOGIN));
        return false;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        threadLocal.remove();
    }
}
