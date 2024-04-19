package com.whaler.oasys.security;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.whaler.oasys.Main;

import io.jsonwebtoken.Claims;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Main.class})
public class JwtManagerTest {
    @Autowired
    private JwtManager jwtManager;
    @Test
    public void testGenerate() {
        String token = jwtManager.generate(1L);
        System.out.println(token);
        // eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwiZXhwIjoxNzk5ODIyNjQxLCJpYXQiOjE3MTM1MDkwNDF9.ttlfCi9hrQrTldggyN_pimihfnOvnFsJv9DyL59_9hwgVKUXfrWrs0T96pTtV5jZ8-2d7RiiRbFLUfEv_GCHbQ
    }

    @Test
    public void testParse() {
        String token = jwtManager.generate(1L);
        Claims claims = jwtManager.parse(token);
        UserContext.setCurrentUserId(
            Long.parseLong(
                claims.getSubject()
            )
        );
        System.out.println(UserContext.getCurrentUserId().toString());

        try {
            jwtManager.parse(null);
        }catch(Exception e) {
            e.printStackTrace();
        }

        try {
            jwtManager.parse(token.concat("a"));
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
}
