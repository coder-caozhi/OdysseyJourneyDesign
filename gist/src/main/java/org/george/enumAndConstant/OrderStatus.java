package org.george.enumAndConstant;

/**
 * 订单状态相关规范（接口只做定义，不做实现）
 */
public interface OrderStatus {

    //TODO：接口里面定义枚举类
    /**
     * 订单状态枚举（放在接口内部，语义高度收敛）
     */
    enum Type implements OrderStatus {
        /**
         * 待支付
         */
        PENDING_PAY(1, "待支付"),

        /**
         * 已支付
         */
        PAID(2, "已支付"),

        /**
         * 已取消
         */
        CANCELED(9, "已取消");

        private final int code;
        private final String desc;

        Type(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public int code() {
            return code;
        }

        public String desc() {
            return desc;
        }
    }
}