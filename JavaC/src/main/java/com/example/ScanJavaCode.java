package com.example;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Android 自定义代码扫描 本地java文件
 */
public class ScanJavaCode {

    public static void main(String[] args) {
        javaParser();
    }

    // 如果应用程序的工作目录是：C:\ Users\ username\ Documents\ JavaProject
    // 则相对路径 FileInputStream fis = new FileInputStream("src/com/example/test.txt");
    // 对应全路径 C:\ Users\ username\ Documents\ JavaProject\ src\ com\ example\ test.txt

    /**
     * Javaparser 是一个通过生成语法树来读取或操作java代码的库。
     * https://github.com/javaparser/javaparser
     * JavaParser还可以根据AST生成Java代码。
     */
    public static void javaParser() {
        try {
            // D:\SmartProject\APIdemo\app\src\main\java\com\example\apidemo\service
            // com.example.apidemo.service.TestService.java
            // src/main/java/com/example/apidemo/service/
            // 替换成你要扫描的 Java 文件路径
            FileInputStream file = new FileInputStream("D:\\SmartProject\\APIdemo\\app\\src\\main" +
                    "\\java\\com\\example\\apidemo\\service\\TestService.java");
            CompilationUnit cu = StaticJavaParser.parse(file);

            new VoidVisitorAdapter<Void>() {
                @Override
                public void visit(MethodDeclaration n, Void arg) {
                    super.visit(n, arg);
                    System.out.println("sjh1" + " Found method: " + n.getNameAsString());
                }
            }.visit(cu, null);

        } catch (FileNotFoundException e) {
            System.out.println("sjh1" + " javaParser err: " + e.getMessage());
            e.printStackTrace();
        }

    }

}
