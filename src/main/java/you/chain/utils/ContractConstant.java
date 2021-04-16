package you.chain.utils;

public class ContractConstant {
    /**
     * 跨链交易事件--from
     */
    public static final String Transfer = "0x13f2f0d38c63b6308d54d1afccc009dcc157b09334feafa719b4e7be5cdcf18a";
    /**
     * 跨链交易事件--to
     */
    public static final String OrderConsumed = "0xa039486e3c492cf79efc221eee763d755f469f2dbc949e3566a6ac83be359132";
    /**
     * 跨链完成事件
     */
    public static final String Transferred = "0x07d3d03b7c9aa3c2948f49bb16ff9942e614a908c1d105a2fd002411b0bc4f96";
    /**
     * 订单取消事件
     */
    public static final String OrderCanceled = "0xe80032aa342fca89e793b118de6282fa9ce532d9ca0aaf8aad1aba02a27c0454";

    /**
     * 合约函数--发起跨链交易
     */
    public static final String exchange = "exchange";
    /**
     * 合约函数--取消订单
     */
    public static final String cancelOrder = "cancelOrder";
    /**
     * 合约函数--完成订单
     */
    public static final String completeOrder = "completeOrder";
    /**
     * 合约函数--批量完成订单
     */
    public static final String completeOrders = "completeOrders";
    /**
     * 合约函数--执行跨链交易
     */
    public static final String consumeOrder = "consumeOrder";

    /**
     * consumeOrder 方法ID --跨链交易
     */
    public static final String consumeOrderMethodId = "0x942301f0";
    /**
     * completeOrder 方法ID --完成交易
     */
    public static final String completeOrderMethodId = "0xb6adaaff";
    /**
     * exchange 方法ID --发起交易
     */
    public static final String exchangeMethodId = "0xbdf109b3";


    /**
     * 以太坊链
     */
    public static final Integer ETH = 1;
    /**
     * 火币链
     */
    public static final Integer HECO = 2;
    /**
     * 币安链
     */
    public static final Integer BSC = 3;
    /**
     * 波场链
     */
    public static final Integer TRX = 4;

}
