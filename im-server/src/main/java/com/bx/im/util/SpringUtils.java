package com.bx.im.util;

import com.bx.im.util.exception.ExceptionCodeEnum;
import com.bx.im.util.exception.IMException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Random;

public class SpringUtils {
    private static final int MAX_SEQ_NUMBER = 10;
    /**
     * 检查发出请求的用户（token中的uid）跟请求中的用户id参数是否一致，不一致则抛出异常
     * @param uid
     */
    public static void checkUser(Long uid) {
        Long uidInToken = getUidInToken();
        if (uidInToken == null || !uidInToken.equals(uid))
            throw new IMException(ExceptionCodeEnum.OPERATION_ILLEGAL);
    }

    /**
     * 在拦截器中，解析jwt后会把其中的uid放入request的属性中，从中取出
     * @return
     */
    public static Long getUidInToken() {
        HttpServletRequest request =
                ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
        Long uidInToken = (Long) request.getAttribute(IMConstant.TOKEN_UID_KEY);
        return uidInToken;
    }

    /**
     * 生成一个指定长度的只包含数字的随机字符串
     * @param length
     * @return
     */
    public static String randomNumberSeq(int length) {
        StringBuilder builder = new StringBuilder(10);
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int nextInt = random.nextInt(MAX_SEQ_NUMBER);
            builder.append(nextInt);
        }

        return builder.toString();
    }
}
