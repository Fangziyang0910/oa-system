package com.whaler.oasys.security;

import java.time.Duration;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtManager {
    @Value("${security.jwt.secretKey}")
    private String secretKey;

    private Duration expiration=Duration.ofDays(999);

    /**
     * 生成JWT令牌。
     * 
     * @param userId 用户ID，用于设置JWT的主题（subject）。
     * @return 返回生成的JWT字符串。
     */
    public String generate(Long userId){
        // 计算令牌的过期时间
        Date expiryDate=new Date(
            System.currentTimeMillis()+expiration.toMillis()
        );
        // 使用JWT构建器创建JWT，设置主题、过期时间、签发时间以及签名算法和密钥
        return Jwts.builder()
            .setSubject(userId.toString()) // 设置主题
            .setExpiration(expiryDate) // 设置过期时间
            .setIssuedAt(new Date()) // 设置签发时间
            .signWith(
                SignatureAlgorithm.HS512, // 使用HS512签名算法
                secretKey // 使用预定义的密钥进行签名
            )
            .compact(); // 生成并返回JWT字符串
    }

    /**
     * 解析JWT令牌并返回其中的声明信息。
     * 
     * @param token 待解析的JWT令牌字符串。
     * @return 如果解析成功，返回包含JWT声明信息的Claims对象；如果解析失败或令牌为空，返回null。
     */
    public Claims parse(String token){
        // 检查令牌字符串是否为空
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        Claims claims=null;
        try{
            // 使用JWT解析器解析令牌，并获取声明信息
            claims=Jwts.parser()
                .setSigningKey(secretKey) // 设置用于签名的密钥
                .parseClaimsJws(token) // 解析JWT并返回ClaimsJws对象
                .getBody(); // 获取Claims主体
        }catch(JwtException e){
            // 记录JWT解析异常
            log.error("token解析失败:{}", e.toString());
        }
        return claims;
    }
}
