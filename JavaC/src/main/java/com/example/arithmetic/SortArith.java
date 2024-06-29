package com.example.arithmetic;

public  class SortArith {

    public static void main(String[] args) {
        int[] arr = {3, 2, 1, 5, 6, 4};
        // int[] arr = {3,1,2,4};
        findKthLargest(arr, 2);

    }

    /**
     * 虾皮 算法
     * #215 数组中的第K个最大元素	中等
     */
    public static int findKthLargest(int[] nums, int k) {
        int len = nums.length;
        if (len <= 0) {
            return -1;
        }
        if (len < k) {
            return -1;
        }
        // ps:闭区间
        quicksort(nums, 0, nums.length - 1);

        for (int i = 0; i < len; i++) {
            System.out.print(nums[i] + "--");
        }

        return nums[len - k];
    }

    /**
     * 快排
     */
    public static void quicksort(int[] nums, int start, int end) {
        // ps:递归退出条件
        if (start >= end) {
            return;
        }
        // 分治 + 递归 搞掂
        int baseIndex = partition(nums, start, end);
        // System.out.println("baseIndex = " + baseIndex);
        // for (int i = 0; i < nums.length; i++) {
        //     System.out.print(nums[i] + "  ");
        // }
        // System.out.println("");

        quicksort(nums, start, baseIndex - 1);
        quicksort(nums, baseIndex + 1, end);

    }

    public static int partition(int[] nums, int start, int end) {
        int baseIndex = start;
        int baseValue = nums[start];

        // i指针扫描到更小的值则 base指针"加1"，再将i和base的内容"互换"。
        for (int i = start; i <= end; i++) {
            if (nums[i] < baseValue) {
                baseIndex++;
                int tmp = nums[i];
                nums[i] = nums[baseIndex];
                nums[baseIndex] = tmp;
            }
        }

        int tmp = nums[baseIndex];
        nums[baseIndex] = nums[start];
        nums[start] = tmp;


        return baseIndex;
    }

}
