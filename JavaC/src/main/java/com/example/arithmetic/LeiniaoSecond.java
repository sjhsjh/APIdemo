package com.example.arithmetic;

// 雷鸟二面算法
public class LeiniaoSecond {

    // 有两个app，各自有一个1tb的相同的文件在本地，第一个app的文件有1字节的破坏，第二个app的文件正常。
    // 现在需要找出第一个app的文件的损坏位置，并通过第二个app传输没破坏的内容给第一个app，如何做才能实现流量传输最小？
    // 先告诉我方案，等我确认后再用java代码实现

    String fileContentString = "aaa";

    public Long findIndex(){
        if(fileContentString == null || fileContentString.isEmpty()){
            return -1L;
        }

        // String currentStr = fileContentString;
        return getIndex(fileContentString);

        // return -1L;     // no different
    }

    public Long getIndex(String currentStr){
        // int middleLen = currentStr.length() / 2;
        // String leftStr = currentStr.substring(0, middleLen);
        // String rightStr = currentStr.substring(middleLen, currentStr.length());
        //
        // Long array[] = new Long[2];
        // array[0] = leftStr.hashCode();  //md5
        // array[1] = rightStr.hashCode();  //md5
        //
        // Long startIndex = fileContentString.indexOf(leftStr);   // toLong
        // Long endIndex = fileContentString.indexOf(rightStr);    // toLong
        // if (startIndex == -1 || endIndex == -1) {
        //     return -1L;
        // }
        // Long arrayIndex = rpcArrayIndex(array,startIndex , endIndex);
        // if (arrayIndex != -1L) {
        //     if (arrayIndex == -10) {
        //         getIndex(leftStr);
        //     } else if (arrayIndex == -11) {
        //         getIndex(rightStr);
        //     } else if (arrayIndex > 0) {
        //         return arrayIndex;
        //     }
        //
        // }

        return -1L;
    }

    // -10:left
    // -11:right
    //-1:all same
    public Long rpcArrayIndex(Long array[], Long startIndex, Long endIndex) {
        // todo  正数就是找到index
        return -1L;
    }
//////////////////////////////////////////////////////////////////
//     修正后的完整代码：
    public class Hi {

        String fileContentString = "aaa";

        public Long findIndex(){
            if(fileContentString == null || fileContentString.isEmpty()){
                return -1L;
            }
            return getIndex(fileContentString, 0L);
        }

        public Long getIndex(String currentStr, Long baseOffset){
            // 递归终止条件：已经缩小到1字节——————结束条件应该是长度为2的时候，做最后一次rpc判断。然后长度为1就是没找到，return -1.
            if (currentStr.length() <= 1) {
                return baseOffset;
            }

            int middleLen = currentStr.length() / 2;
            String leftStr = currentStr.substring(0, middleLen);
            String rightStr = currentStr.substring(middleLen);

            Long array[] = new Long[2];
            array[0] = (long) leftStr.hashCode();  // 实际应该用MD5
            array[1] = (long) rightStr.hashCode();  // 实际应该用MD5

            Long startIndex = baseOffset;  // 左半部分在原文件中的起始位置
            Long endIndex = baseOffset + middleLen;  // 右半部分在原文件中的起始位置

            Long arrayIndex = rpcArrayIndex(array, startIndex, endIndex);

            if (arrayIndex == -10L) {
                // 左半部分有损坏，递归检查左半部分
                return getIndex(leftStr, baseOffset);
            } else if (arrayIndex == -11L) {
                // 右半部分有损坏，递归检查右半部分
                return getIndex(rightStr, baseOffset + middleLen);
            } else if (arrayIndex >= 0) {
                // 服务器直接返回了损坏位置
                return arrayIndex;
            }

            return -1L;  // 没有找到损坏
        }

        // RPC 服务端方法声明
        // -10: 左半部分损坏
        // -11: 右半部分损坏
        // -1: 完全相同（无损坏）
        // >=0: 直接返回损坏位置
        public Long rpcArrayIndex(Long array[], Long startIndex, Long endIndex) {
            // 服务端实现：比对哈希值
            return -1L;
        }
    }




}
