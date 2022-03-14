package com.bx.im;

import com.bx.im.proto.ChatMsgProto;
import com.bx.im.util.JwtUtil;
import com.google.protobuf.Timestamp;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Test {

    public static void main(String[] args) {
        // testProtoTimestamp();
        // testProtoBufToBuilder();

        // testJJWT();
        // testJJWTExpiration();
        // testJwtUtil();
    }

    private static int testTryCatch() {
        try {
            System.out.println("tyr block");
            double a = 1 / 0;
        } catch (Exception e) {
            System.out.println("catch block");
            return 1;
        } finally {
            System.out.println("finally block");
            return 2;
        }
    }

    private static void testJwtUtil() {
        String jwt = JwtUtil.generateJWT(66L);
        System.out.println(jwt);

        System.out.println(JwtUtil.verifyJWT(jwt));
        System.out.println(JwtUtil.parseUid(jwt));
    }

    private static void testLocalDateTIme() {
        // LocalDate date = LocalDate.parse("2022-02-27T06:42:10.667Z");
        ;
        String str = "2022-02-28T11:08:20+08:00";
        // System.out.println(LocalDateTime.parse("2022-02-27T06:42:10.667Z", DateTimeFormatter.ISO_DATE_TIME));
        System.out.println(LocalDateTime.parse(str, DateTimeFormatter.ISO_DATE_TIME));
    }

    private static void testProtoTimestamp() {
        Date date = new Date();
        Timestamp timestamp = Timestamp.newBuilder().setSeconds(date.getTime()).build();
        Date date2 = new Date(timestamp.getSeconds());
        System.out.println(date);
        System.out.println(date2);

    }

    /*
    * ProtoBuf的对象是不可更改的，要根据已有的对象创建新对象可以通过 toBuilder() 方法获取 builder ，在原来的属性上构造新对象
    * */
    private static void testProtoBufToBuilder() {
        ChatMsgProto.ChatMsg chatMsg = ChatMsgProto.ChatMsg.newBuilder().setFromUid(250L).setContent("holy shit").build();
        System.out.println(chatMsg.toString());
        ChatMsgProto.ChatMsg chatMsg1 = chatMsg.toBuilder()
                // 在原有的属性上设置新的属性
                .setMsgSeq(666L).build();
        System.out.println(chatMsg1);
    }


    private static void testJJWT() {
        Map<String, Object> claims = new HashMap<>();
        String secret = "Holy Shit";
        claims.put("uid", 66L);
        claims.put("name", "张三");
        String jwt = Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 1000))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
        System.out.println(jwt);

        // 错误的jwt
        String jws = "eyJhbGciOiJIUzI1NiJ9.eyJ1aWQiOjY2LCJuYW1lIjoi5byg5LiJIiwiZXhwIjoxNjQ2MDQ5NDAxfQ.D_kFM4RNbZjNI1KSIvOpXa2c892zQnVNI6rjLY79Jeccc";
        // 若token错误，会抛出io.jsonwebtoken.SignatureException异常
        Claims body = Jwts.parser().setSigningKey(secret).parseClaimsJws(jwt).getBody();
        System.out.println(body);
    }

    private static void testJJWTExpiration() {
        // Map<String, Object> claims = new HashMap<>();
        String secret = "Holy Shit";
        // claims.put("uid", 66L);
        // claims.put("name", "张三");
        // String jwt = Jwts.builder()
        //         .setClaims(claims)
        //         .setExpiration(new Date())
        //         .signWith(SignatureAlgorithm.HS256, secret)
        //         .compact();
        // System.out.println(jwt);

        /*
        * jwt若过期，会抛出 io.jsonwebtoken.ExpiredJwtException 异常
        * */
        String jws = "eyJhbGciOiJIUzI1NiJ9.eyJ1aWQiOjY2LCJuYW1lIjoi5byg5LiJIiwiZXhwIjoxNjQ2MDQ4NjgyfQ.RN26MvmhecMYcVUmLhSjejOnLdFpJjHp_lX8myIjrQY";
        Claims body = Jwts.parser().setSigningKey(secret).parseClaimsJws(jws).getBody();
        System.out.println(body);
    }
}
