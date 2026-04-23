package org.george.debug;

/**
 * 复杂多层异常堆栈练习
 * 模拟真实业务：controller -> service -> dao -> 工具类 多层报错 + 嵌套异常
 */
public class ComplexStackTraceDemo {

    public static void main(String[] args) {
        System.out.println("=== 开始执行业务流程 ===");
        // 第一层：模拟接口入口
        try {
            controllerBusiness();
        } catch (Exception e) {
            // 打印完整异常堆栈
            e.printStackTrace();
        }
    }

    // 第1层：模拟 Controller 控制层
    public static void controllerBusiness() {
        serviceOrder();
    }

    // 第2层：模拟 Service 业务层
    public static void serviceOrder() {
        // 包装一层业务异常，包裹底层原始异常
        try {
            daoQueryData();
        } catch (Exception e) {
            throw new RuntimeException("业务层：查询订单数据失败", e);
        }
    }

    // 第3层：模拟 Dao 数据访问层
    public static void daoQueryData() {
        utilCalculate(0);
    }

    // 第4层：底层工具类，真正抛出原始异常
    public static void utilCalculate(int num) {
        // 除0算术异常，真正的根源错误
        int res = 100 / num;
        System.out.println("计算结果：" + res);
    }
}