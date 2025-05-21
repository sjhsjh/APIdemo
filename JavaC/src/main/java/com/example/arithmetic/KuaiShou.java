package com.example.arithmetic;

import java.util.ArrayList;

/**
 * 快手算法：文件树构建。
 */
public class KuaiShou {

    public static void main(String[] args) {
        ArrayList<String> list = new ArrayList<String>();
        list.add("/a/b/c");
        list.add("/a/b/d");
        list.add("/x/b/c");
        generate(list);
    }

    public static class Node {
        public String nodeName = "";
        public String path = "";    //   /a/b/c
        // public ArrayList<String> pathNames= new ArrayList<String>();    // Node节点包含a b c
        public ArrayList<Node> childList = new ArrayList<Node>();

        // root节点是否包含 /a/b/c   a节点是否包含  /b/c
        public Node findPath(String pathName) {
            for (int i = 0; i < childList.size(); i++) {
                // if(childList.get(i).pathNames.isEmpty()){
                //     continue;
                // }
                // 都取路径的第一个文件名来校验 是否相同
                String[] arr = childList.get(i).path.split("/");
                if (arr == null || arr.length == 0) {
                    return null;
                }
                String[] arr2 = pathName.split("/");
                if (arr2 == null || arr2.length == 0) {
                    return null;
                }

                if (arr[0] == arr2[0]) {
                    return childList.get(i);
                }
            }
            return null;
        }

        @Override
        public String toString() {
            return "Node{" +
                    "path='" + path + '\'' +
                    ", childList=" + childList +
                    '}';
        }
    }

    /**
     * 给定任意的一条字符串路径，生成的是一条文件路径！！
     */
    public static void generate(ArrayList<String> paths) {
        Node rootNode = new Node();
        rootNode.path = "root";

        for (int i = 0; i < paths.size(); i++) {
            addPath(rootNode, paths.get(i));
        }
        System.out.println(rootNode);
    }

    /**
     * 将叶节点路径path  加入到当前rootNode中
     */
    public static void addPath(Node rootNode, String path) {
        // for (int i = 0; i < paths.size(); i++) {
        Node n = rootNode.findPath(path);   // root节点是否包含 /a/b/c   a节点是否包含  /b/c
        if (n == null) {
            Node node = new Node();     // a     b

            String str = path.substring(1, path.length());  // 删 /
            int index = str.indexOf("/");
            if (index > 0) {
                String newPath = str.substring(index, str.length());
                node.path = newPath;   //  /b/c  /c
                rootNode.childList.add(node);

                addPath(node, newPath);
            } else {
                node.path = path;
                rootNode.childList.add(node);
            }
        } else { // a节点已包含  /b/c
            String str = path.substring(1, path.length());  // 删 /
            int index = str.indexOf("/");
            if (index > 0) {
                String newPath = str.substring(index, str.length());
                n.path = newPath;   //  /b/c  /c
                rootNode.childList.add(n);

                addPath(n, newPath);
            } else {
                n.path = path;
                rootNode.childList.add(n);
            }
        }


        // System.out.println(arr);

        // }

    }
}
