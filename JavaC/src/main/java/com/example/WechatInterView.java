package com.example;


import java.util.ArrayList;

/**
 * 给定一个非负整数数组，a1, a2, ..., an, 和一个目标数，S。现在你有两个符号 + 和 -。对于数组中的任意一个整数，
 * 你都可以从 + 或 -中选择一个符号添加在前面。
 * 返回可以使最终数组和为目标数 S 的所有添加符号的方法数。
 * <p>
 * 样例
 * [输入]
 * nums: [1, 1, 1, 1, 1],    S: 3
 * [输出]
 * 5
 */
public class WechatInterView {

    public static void main(String[] args) {
        // int[] arr = new int[5];
        // int[] arr2 = {1, 1, 1, 1, 1};        // 3
        // int[] arr2 = {9,7,0,3,9,8,6,5,7,6};  // 2
        // int[] arr2 = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};   // 0
        int[] arr2 = {42,24,30,14,38,27,12,29,43,42,5,18,0,1,12,44,45,50,21,47};   // 38

        int res = getKinds(arr2, 0);
        System.out.println("getkinds res = " + res);

        // Collections.sort(debugList);
        // System.out.println("debugList = " + debugList);

        // for (int i = 0; i < debugStrList.size(); i++) {
        //     System.out.println("expre=  " + debugStrList.get(i));
        // }


        // double result = Math.pow(2, 20);
        // System.out.println("result = " + result);   // 1048576
    }

    private static ArrayList debugList = new ArrayList<Integer>();
    private static ArrayList debugStrList = new ArrayList<String>();

    /**
     * 穷举，正确 但 超时
     */
    public static int getKinds(int[] arr, int S) {
        if (arr.length == 0) {
            return 0;
        }
        int len = arr.length;
        int times = 1;
        for (int i = 0; i < len; i++) {
            times = times * 2;
        }

        int result = 0;
        int sum = 0;

        for (int i = 0; i < times; i++) {
            sum = 0;
            String expression = "";
            for (int j = 0; j < len; j++) {
                // int deta = arr[j]% (10*len) ;     // 二进制和十进制 搞错了

                // int deta = 0;                   // (2 * j)也错了
                // if (j == 0) {
                //     deta = i / 1 % 2;
                // } else {
                //     deta = i / (2 * j) % 2;
                // }

                int mi = (int) Math.pow(2, j);      // 位操作。除以2是右移；
                int deta = i / mi % 2;

                // System.out.println("       deta = " + deta);

                if (deta == 1) {
                    sum += arr[j];
                    // expression += "+" + arr[j];
                } else {
                    sum -= arr[j];
                    // expression += "-" + arr[j];
                }

            }

            // // debug
            // System.out.println("sum = " + sum);
            // debugList.add(sum);
            // expression += "=" + sum;
            // debugStrList.add(expression);

            if (sum == S) {
                result++;
            }
        }


        return result;
    }


}
