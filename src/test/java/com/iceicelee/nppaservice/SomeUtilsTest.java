package com.iceicelee.nppaservice;

import com.iceicelee.nppaservice.utils.EncryptUtils;
import org.junit.jupiter.api.Test;

/**
 * @author: Yao Shuai
 * @date: 2021/4/13 17:56
 */
public class SomeUtilsTest {

    @Test
    public void test26ToDecimal() {
        int result = (int) EncryptUtils.twentySix2Decimal("1hba9k");
        System.out.println(result);
    }
}
