package org.george.judge;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ArgumentJudgement {
    public static void main(String[] args) {
        // ==========================
        // 1. 对象判空（commons-lang3 最优雅）
        // ==========================
        Object obj = null;

        // jdk自带工具类
        boolean aNull = Objects.isNull(obj);

        //==========
        // commons-lang3 的 ObjectUtils 提供了更丰富的对象判空方法，适用于各种类型的对象
        //==========
        // 判断对象是否为 null
        boolean objIsNull = ObjectUtils.isEmpty(obj);

        // 判断对象非 null
        boolean objIsNotEmpty = ObjectUtils.isNotEmpty(obj);

        // 非空校验（为空抛异常）
        ObjectUtils.requireNonEmpty(obj, "对象不能为 null");


        // ==========================
        // 2. 字符串判空（最常用！！！）
        // ==========================
        String str = null;
        String blankStr = "   ";
        String emptyStr = "";

        // null / "" / "   " 全部算空（自动防NPE）
        // 非空（不是null、不是空、不是空白）
        //ObjectUtils.isEmpty()全能型判断工具，适用于各种类型的对象（字符串、集合、数组等），但对于字符串来说，空格不算空
        boolean strIsEmptyOrBlank = ObjectUtils.isEmpty(str);

        boolean strIsBlank = StringUtils.isBlank(str);
        boolean strNotBlank = StringUtils.isNotBlank(str);

        // 仅判断 "" 和 null，不判断空格
        boolean strIsEmpty = StringUtils.isEmpty(str);
        boolean strNotEmpty = StringUtils.isNotEmpty(str);

        // ==========================
        // 3. 数组判空
        // ==========================
        String[] arr = null;

        // null 或 空数组
        boolean arrIsEmpty = ArrayUtils.isEmpty(arr);

        // 非空数组
        boolean arrNotEmpty = ArrayUtils.isNotEmpty(arr);


        // ==========================
        // 4. 集合 List/Set 判空
        // ==========================
        List<String> list = null;

        // null 或 空集合
        boolean collIsEmpty = org.apache.commons.collections4.CollectionUtils.isEmpty(list);

        // 非空集合
        boolean collNotEmpty = org.apache.commons.collections4.CollectionUtils.isNotEmpty(list);


        // ==========================
        // 5. Map 判空
        // ==========================
        Map<String, String> map = null;
        Map<String, String> emptyMap = new HashMap<>();

        // null 或 空Map
        boolean mapIsEmpty = MapUtils.isEmpty(map);

        // 非空Map
        boolean mapNotEmpty = MapUtils.isNotEmpty(map);

        // 6. spring断言工具
        Assert.isTrue(1 > 2, "条件不成立，抛出异常");

        Object object = null;
        Assert.notNull(object, "对象不能为 null");

        // 空串、空格、null 都会报错
        Assert.hasText(str, "用户名不能为空或空白");

        // 数组集合断言
        Assert.notEmpty(arr, "数组不能为空");
        Assert.notEmpty(list, "集合不能为空");

    }
}