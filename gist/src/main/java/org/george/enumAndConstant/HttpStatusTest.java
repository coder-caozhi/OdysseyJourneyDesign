package org.george.enumAndConstant;

import java.util.Arrays;


public class HttpStatusTest {
    public static void main(String[] args) {
        String isOk = validate();
        System.out.println("isOk = " + isOk);
        HttpStatus ok = HttpStatus.OK;
        // 编译器为每个枚举类型自动添加一些有用的方法
        System.out.println("ok.name() = " + ok.name());
        System.out.println("ok.ordinal() = " + ok.ordinal()); // 索引从0开始
        System.out.println("ok.name() = " + ok.name());
        HttpStatus[] httpStatuses = HttpStatus.values();
        System.out.println("httpStatuses = " + Arrays.toString(httpStatuses));
    }

    private static String validate() {
        HttpStatus ok = HttpStatus.OK;
        int code = ok.getCode();
        if (code == 200) {
            return ok.getMessage();
        } else {
            return "httpStatus is error";
        }
    }


}
