package com.example.arithmetic;

public class QiYeWechat {

    /**
     * 企业微信 合并有序链表
     */
    class LinkNode {
        private int val;
        private LinkNode next;

        // 题目（数据结构）：合并两个从小到大链表的链表，使用的算法越快越好。
        // 比如：L1={1,3,5}, L2={2,4}, L1.merge(L2)后，L1={1,2,3,4,5}。注意：最后结果是在L1上。
        public void merge(LinkNode node) {
            // TODO 请完成实现部分
            this.merge2(this, node);
        }

        public LinkNode merge2(LinkNode node1, LinkNode node2) {
            // TODO 请完成实现部分
            if (node1 == null) {
                return node2;
            }
            if (node2 == null) {
                return node1;
            }
            LinkNode node11 = node1;
            LinkNode node22 = node2;
            LinkNode p = null;
            LinkNode head = null;

            while (node11 != null && node22 != null) {
                if (node11.val < node22.val) {
                    if (p == null) {
                        head = node11;      // node1 也可以
                        p = node11;         // node1 也可以
                        node11 = node11.next;
                    } else {
                        p.next = node11;
                        p = p.next;
                        node11 = node11.next;
                    }
                } else {
                    if (p == null) {
                        head = node22;
                        p = node22;
                        node22 = node22.next;
                    } else {
                        p.next = node22;
                        p = p.next;
                        node22 = node22.next;
                    }
                }
            }
            // 别漏了拼尾部！
            if (node11 == null) {
                p.next = node22;
            }
            if (node22 == null) {
                p.next = node11;
            }

            return head;
        }
    }
}
