package com.github.chenhaiyangs.gateway.service.handler.runtime.token.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
import java.util.Date;

/**
 * jwtToken认证组件
 * @author chenhaiyang
 */
@Slf4j
public class JwtVertify {
    /**
     * Issuer
     */
    private static final String ISSUR="easyGateWay";
    /**
     * subject
     */
    private static final String SUBJECT="user";
    /**
     * 用户Id
     */
    private static final String KEY_USER_ID="userId";


    /**
     * 创建JWT token
     * @param userId userId
     * @param expire 超时时间，单位秒
     * @return token
     * @throws IllegalArgumentException e
     * @throws UnsupportedEncodingException e
     */
    public static String createJwt(String userId,int expire) throws IllegalArgumentException, UnsupportedEncodingException{
        Algorithm al = Algorithm.HMAC256("secretkey");
        return JWT.create()
                .withIssuer(ISSUR)
                .withSubject(SUBJECT)
                .withClaim(KEY_USER_ID, userId)
                .withExpiresAt(new Date(System.currentTimeMillis()+(expire+3600)*1000))
                .sign(al);
    }

    /**
     * 验证jwtToken的正确性
     * @param token token
     * @param userId userId
     * @return 验证结果
     */
    public static boolean verifyJwt(String token,String userId) {
        try {
            if(token==null){
                log.info("token不存在！");
                return false;
            }
            Algorithm algorithm = Algorithm.HMAC256("secretkey");
            JWTVerifier verifier = JWT.require(algorithm)
                    .build();
            DecodedJWT jwt = verifier.verify(token);

            Claim claim = jwt.getClaim(KEY_USER_ID);
            String userIdResult = claim.asString();
            String issuer = jwt.getIssuer();
            String subject = jwt.getSubject();

            log.info("issuer:{},subject:{},userId:{}",issuer,subject,userIdResult);

            return ISSUR.equals(issuer) && SUBJECT.equals(subject) && userId.equals(userIdResult);
        }catch (JWTVerificationException e) {
            log.error("jwt认证失败，{}",e.getMessage(),e);
            return false;
        }catch (Exception e){
            log.error("jwt认证发生未知异常:{}",e.getMessage(),e);
            return false;
        }
    }
}
