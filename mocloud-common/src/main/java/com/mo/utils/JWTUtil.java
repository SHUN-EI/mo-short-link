package com.mo.utils;

import com.mo.enums.BizCodeEnum;
import com.mo.exception.BizException;
import com.mo.model.LoginUserDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

/**
 * Created by mo on 2021/4/21
 */
@Slf4j
public class JWTUtil {

    /**
     * token过期时间，默认为7天,方便测试，改为70天
     */
    private static final long EXPIRED = 1000 * 60 * 60 * 24 * 7 * 10;

    /**
     * 加密的密钥
     */
    private static final String SECRET = "moshortlink666";

    /**
     * 令牌前缀
     */
    private static final String TOKEN_PREFIX = "mo-short-link";

    /**
     * subject,颁布者
     */
    private static final String SUBJECT = "waynemo";


    /**
     * 根据用户信息，生成token令牌
     *
     * @param user
     * @return
     */
    public static String generateJsonWebToken(LoginUserDTO user) {

        if (null == user) {
            throw new BizException(BizCodeEnum.ACCOUNT_UNREGISTER);
        }

        String token = Jwts.builder().setSubject(SUBJECT)
                //playload配置
                .claim("id", user.getId())
                .claim("account_no", user.getAccountNo())
                .claim("user_name", user.getUsername())
                .claim("phone", user.getPhone())
                .claim("mail", user.getMail())
                .claim("head_img", user.getHeadImg())
                .claim("auth", user.getAuth())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRED))
                .signWith(SignatureAlgorithm.HS256, SECRET)
                .compact();

        token = TOKEN_PREFIX + token;

        return token;
    }

    /**
     * 校验token的方法
     *
     * @param token
     * @return
     */
    public static Claims checkJWT(String token) {

        try {
            final Claims claims = Jwts.parser().setSigningKey(SECRET)
                    .parseClaimsJws(token.replace(TOKEN_PREFIX, ""))
                    .getBody();

            return claims;

        } catch (Exception e) {
            log.info("jwt token 解密失败");
            return null;
        }
    }
}
