package com.example.arithmetic;

import java.util.ArrayList;
import java.util.Stack;

public class Pdd {

    public static void main(String[] args) {
        System.out.println(change("This is a cat!"));

    }

    // 国内pdd一面。最大子数组和
    // 示例 1：
    // 输入：nums = [-2,1,-3,4,-1,2,1,-5,4]
    // 输出：6
    // 解释：连续子数组 [4,-1,2,1] 的和最大，为 6 。

    // 子数组的元素和等于 《前缀和》 与《最小前缀和》的差！！
    public int maxSubArray(int[] nums) {
        if (nums.length == 1) {
            return nums[0];
        }
        // 初始化为int类型最小值
        int max = Integer.MIN_VALUE;
        int minSum = 0;  // 最小前缀和。注意第一个元素的“最小前缀和”是0！不是自身
        int preSum = 0;  // 前缀和
        for (int i = 0; i < nums.length; i++) {
            if (preSum < minSum) {
                minSum = preSum;      // 注意子数组不能为空！即要在加当前数值之前先算出“最小前缀和”！！
            }
            preSum += nums[i];

            int deta = preSum - minSum;
            if (deta > max) {
                max = deta;
            }

        }
        return max;
    }

    // 较难理解的方法
    public static int maxSubArray2(int[] nums) {
        if (nums.length == 1) {
            return nums[0];
        }
        // 初始化为int类型最小值
        int max = Integer.MIN_VALUE;    //当前的最大的子序和
        int sum = 0;
        for (int i = 0; i < nums.length; i++) {
            sum += nums[i];     // sum的值会变，因此不完全算是 前缀和
            // 记录最大数值
            // max = Math.max(sum, max);
            if (sum > max) {
                max = sum;
            }
            // 最关键和难理解的地方！！！如果sum < 0，重新开始找子序串
            // 如果和小于0,就重置为0，因为任何数加上一个负数一定小于当前数值
            if (sum < 0) {
                sum = 0;
            }
        }

        return max;
    }

    public class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode() {
        }

        TreeNode(int val) {
            this.val = val;
        }

        TreeNode(int val, TreeNode left, TreeNode right) {
            this.val = val;
            this.left = left;
            this.right = right;
        }
    }

    // Temu 二面 ：求二叉树高度
    // 1、不能用递归
    // 2、要用深度优先算法（含递归or栈）
    // 即要使用 栈实现先根遍历
    public static int getDepth(TreeNode head) {
        if(head == null)    return 0;
        int dep = 1;
        Stack<TreeNode> stack = new Stack<>();
        TreeNode treeNode = head;
        // 先左节点，再右节点，然后出栈根节点
        while (treeNode != null || !stack.isEmpty()) {
            while (treeNode != null) {
                // 入栈根节点 并 遍历左节点
                stack.push(treeNode);
                treeNode = treeNode.left;
                if (stack.size() > dep) {
                    dep = stack.size();
                }
            }
            if (!stack.isEmpty()) {
                // 出栈根节点 来 遍历右节点
                TreeNode pop = stack.pop();
                treeNode = pop.right;
            }
        }

        return dep;
    }

    // Temu 一面
    // 单词逆序
    // This is a cat!
    // Cat a is this!
    public static String change(String str) {
        if (str == null || str.isEmpty()) {
            return "";
        }
        ArrayList<String> list = new ArrayList<String>();

        String temp = "";
        String last = "";
        for (int i = 0; i < str.length(); i++) {
            Character x = str.charAt(i);
            if ((x >= 'a' && x <= 'z') || (x >= 'A' && x <= 'Z')) {
                temp += x;
            } else if (x == ' ') {
                list.add(temp);
                temp = "";
            } else {
                list.add(temp);
                temp = "";
                last = x + "";
            }
        }

        String res = "";
        for (int i = list.size() - 1; i >= 0; i--) {
            if (i != list.size() - 1) {
                res += ' ';
            }
            res += list.get(i);
        }
        res += last;

        String res2 = res.toLowerCase();
        String res3 = res2.substring(0, 1).toUpperCase() + res2.substring(1);

        // Character y = res2.charAt(0);
        // if ((y >= 'a' && y <= 'z') ) {
        //     res2.substring(1, )
        //     res2.charAt(0)  = (char) (y+32);
        // }
        return res3;
    }

}
