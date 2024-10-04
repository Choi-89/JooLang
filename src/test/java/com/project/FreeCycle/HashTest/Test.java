package com.project.FreeCycle.HashTest;

import com.project.FreeCycle.Util.HashUtil;
import org.assertj.core.api.Assertions;


public class Test {

    @org.junit.jupiter.api.Test
    public void test() {
        String phoneNum1 = "01012345678";
        String phoneNum2 = "010-1234-5678";

        String cleanPhoneNum = phoneNum2.replaceAll("-", "");

        String hashedPhoneNum1 = HashUtil.hashPhoneNumber(phoneNum1);
        String hashedPhoneNum2 = HashUtil.hashPhoneNumber(cleanPhoneNum);

        Assertions.assertThat(hashedPhoneNum1).isEqualTo(hashedPhoneNum2);
    }
}
