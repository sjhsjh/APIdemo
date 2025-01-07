package com.example.arithmetic;

import java.util.HashSet;

public class TencentGame {

    /**
     * 找到一个字符串中的最长不重复子串，例如abca返回abc
     * https://leetcode.cn/problems/wtcaE1
     */
    public static void main(String[] args) {
        // System.out.println("=======结果: " +    repeatStr("abca"));
        System.out.println("=======结果: " + findStr("abcabcbb"));
        System.out.println("=======结果: " + lengthOfLongestSubstring("abcabcbb"));
        System.out.println("=======结果: " + lengthOfLongestSubstring("au"));

    }

    // 双指针解法
    // 双指针起始位置、右指针移动、左指针移动、两指针相遇、右指针移动直至到达尾部
    // 不可能含有最长不重复子串的的起始字符，就不会继续去算他实际比较小的值了。滑动窗口省的是这部分的计算。
    public static int lengthOfLongestSubstring(String s) {
        int maxLen = 0;
        HashSet<Character> set = new HashSet<>();

        for (int right = 0, left = 0; right < s.length(); right++) {    // 右指针 右移
            // abc   c     bcbb
            // left  right
            while (set.contains(s.charAt(right))) {
                set.remove(s.charAt(left));   // 左指针 右移
                left++;
            }
            set.add(s.charAt(right));     // 无重复字符则一直拼接 + 记录最长长度

            // while (true) {
            //     if (set.contains(s.charAt(right))) {
            //         set.remove(s.charAt(left));   // 左指针 右移
            //         left++;
            //     } else {
            //         set.add(s.charAt(right));
            //         break;
            //     }
            // }

            // while (!set.add(s.charAt(right))) {
            //     set.remove(s.charAt(left++));
            // }
            maxLen = Math.max(maxLen, right - left + 1);
        }
        return maxLen;
    }

    // 双循环暴力解
    public static int findStr(String input) {
        if (input == null || input.isEmpty()) {
            return 0;
        }
        if (input.trim().isEmpty()) {    // 处理全空格的情况
            return 1;
        }
        // if (input.length() == 1) {    // 处理长度为1的情况
        //     return 1;
        // }
        HashSet<Character> set = new HashSet<Character>();
        int tempMaxLen = 0;
        int finalMaxLen = 0;

        for (int i = 0; i < input.length(); i++) {

            for (int j = i; j < input.length(); j++) {
                if (!set.contains(input.charAt(j))) {
                    set.add(input.charAt(j));
                    tempMaxLen++;
                    // 可能找到尾部都没有重复字符！！
                    if (tempMaxLen > finalMaxLen) {
                        finalMaxLen = tempMaxLen;
                    }
                } else {
                    // 遇到重复字符 "abcabcbb"
                    if (tempMaxLen > finalMaxLen) {
                        finalMaxLen = tempMaxLen;
                        // System.out.println(i + "====" +finalMaxLen);
                    }
                    // tempMaxLen = 0;  // 清除临时数据
                    // set.clear();
                    break;
                }
            }

            tempMaxLen = 0;  // 清除临时数据
            set.clear();
        }


        return finalMaxLen;
    }

    // 现场写的有缺陷的：
    // 找到一个字符串中的最长不重复子串，例如abca返回abc
    public static String repeatStr(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }
        HashSet<Character> set = new HashSet<Character>();

        String res = "";
        int maxLen = 0;
        String maxStr = "";

        int finalMaxLen = 0;
        String finalMaxStr = "";

        for (int i = 0; i < input.length(); i++) {
            set.clear();

            for (int j = i; j < input.length(); j++) {
                if (!set.contains(input.charAt(j))) {
                    set.add(input.charAt(j));
                    res = res + input.charAt(j);
                    maxLen++;
                } else {
                    maxStr = res;
                    if (maxLen > finalMaxLen) {   // 更新最长字符串信息
                        finalMaxLen = maxLen;
                        finalMaxStr = res;
                        maxLen = 0;     // 清空临时数据
                        maxStr = "";
                        res = "";
                    }
                    // return res;
                }
            }
        }

        return finalMaxStr;
    }

}
