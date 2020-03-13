package com.example.compiler;

import com.google.auto.service.AutoService;
import java.io.IOException;
import java.io.Writer;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

/**
 * 注解处理器流程：
 * APT可以扫描源码中的所有注解，并将相关的注解回调到注解处理器中的process() 方法中，我们依据这些注解来提取信息，并生成代码，然后添加到源码中。
 * 但如果生成的代码中也有注解，那么仅扫描一次源码肯定是有问题的。为了能避免这一问题，APT 至少会扫描两次源码。如果第二次扫描后继续生成有注解的代码（生成文件），
 * 那么会再次扫描，直到不再出现新注解（新文件吧）。上一次process生成的任何文件会作为下一次process的输入。所以，这个流程可以无限递归。
 * 因此，在生成的代码中如果需要添加注解，要注意避免出现死循环。
 *
 * ps：注解处理器只需要在编译的时候使用，并不需要打包到APK中
 * @date 2020/3/12
 */
// @AutoService(Processor.class)向javac注册我们这个自定义的注解处理器，这样在javac编译时，才会调用到我们这个自定义的注解处理器方法。
// AutoService这里主要是用来生成 META-INF/services/javax.annotation.processing.Processor文件的。如果不加上这个注解，那么你需要自己进行手动配置进行注册
@AutoService(Processor.class)
public class MyProcessor extends AbstractProcessor {
    private static Messager messager;   // Messager可在编译时Console窗口输出日志，ERROR级别会中断编译。
    private Filer filer;                // 可创建三种文件类型：源文件、class文件和辅助资源文件。
    private Elements elementUtils;

    /**
     * 编译期间init()会自动被注解处理工具调用，并传入ProcessingEnviroment参数，通过该参数可以获取到很多有用的工具类: Elements , Types , Filer等等
     */
    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        messager = processingEnv.getMessager();
        filer = processingEnv.getFiler();
        elementUtils = processingEnv.getElementUtils();
        messager.printMessage(Diagnostic.Kind.OTHER, "MyProcessor init. ");
    }

    /**
     * 该函数用于指定该自定义注解处理器是注册给哪些注解的(Annotation),
     * 注解(Annotation)指定必须是完整的包名+类名(eg:com.example.MyAnnotation)
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        // return super.getSupportedAnnotationTypes();
        Set<String> set = new HashSet<>();
        set.add(AutoBindClass.class.getCanonicalName());     // getCanonicalName是类的完整名称
        set.add(AutoBindField.class.getCanonicalName());

        return set;
    }

    @Override
    public Set<String> getSupportedOptions() {
        return super.getSupportedOptions();
    }

    /**
     * getSupportedSourceVersion():用于指定你的java版本，一般返回：SourceVersion.latestSupported()。
     * 当然你也可以指定具体java版本如 return SourceVersion.RELEASE_7;
     */
    @Override
    public SourceVersion getSupportedSourceVersion() {
        // return super.getSupportedSourceVersion();
        return SourceVersion.latestSupported();
    }

    /**
     * Annotation Processor扫描出的结果会存储进roundEnvironment中，可以在这里获取到注解内容，编写你的操作逻辑。
     * 注意process()函数中不能直接进行异常抛出,否则运行Annotation Processor的进程会异常崩溃,然后弹出一大堆让人捉摸不清的堆栈调用日志.
     */
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        // [com.example.compiler.AutoBindClass, com.example.compiler.AutoBindField]。 与getSupportedAnnotationTypes相同
        print("==============process 1 ================" + set);
        // print("==============process 2 roundEnvironment=====" + roundEnvironment);
        // print("==============process processingOver==" +  roundEnvironment.processingOver());     // 循环处理是否完成

        // 得到所有注解 @AutoBindClass 的Element集合
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(AutoBindClass.class);
        print("==============AutoBindClass elements================" + elements);    // [com.example.apidemo.MainActivity, com.example.apidemo.view.MyView]
        // if (elements == null || elements.size() == 0) {
        //     return false;
        // }
        for (Element element : elements) {
            if (element.getKind() != ElementKind.CLASS) {
                continue;
            }
            TypeElement typeElement = (TypeElement) element;
            List<? extends Element> childElementList =  typeElement.getEnclosedElements();     // element.getEnclosedElements();// 获取子元素


            print("====AutoBindClass elements1 ================" + element.getKind()
                    + "  " + element.getModifiers()     // [public]
                    + "  " +  childElementList);    // Student(),name,sex,nickName,getName(),setName(java.lang.String),getSex(),setSex(java.lang.String),getNickName(),setNickName(java.lang.String),toString()
        }



        Set<? extends Element> elements2 = roundEnvironment.getElementsAnnotatedWith(AutoBindField.class);
        print("==============AutoBindField elements================" + elements2);      // [name]
        for (Element element : elements2) {
            if (element.getKind() != ElementKind.FIELD) {
              continue;
            }

            VariableElement bindViewElement = (VariableElement) element;
            // 获取注解的成员 变量名
            String filedName = bindViewElement.getSimpleName().toString();
            // 获取注解的成员 变量类型
            TypeMirror typeMirror = bindViewElement.asType();
            print("==============element typeMirror================" + typeMirror);     // java.lang.String

            // 获取注解元数据
            AutoBindField annotation = element.getAnnotation(AutoBindField.class);
            String value = annotation.value();
            print("===AutoBindField element annotation value================" + value);     // nameX

            // 获取元素所在包名
            PackageElement packageElement = elementUtils.getPackageOf(element);
            String pkName = packageElement.getQualifiedName().toString();
            print("====AutoBindField element pkName================" + pkName);             //  com.example.apidemo.data

            // 获取包装类类型
            TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
            String enclosingQualifiedName = enclosingElement.getQualifiedName().toString();     // 全名
            String enclosingSimpleName = enclosingElement.getSimpleName().toString();           // 简称
            print("===AutoBindField element getQualifiedName===" + enclosingQualifiedName
                    + " getSimpleName==" +  enclosingSimpleName);

        }


        Set<? extends Element> elements3 = roundEnvironment.getElementsAnnotatedWith(AutoBindMethod.class);
        print("==============AutoBindMethod elements================" + elements3);     // [getSex()]
        for (Element element : elements3) {
            if (element.getKind() != ElementKind.METHOD) {
                continue;
            }
            // if (!element.getModifiers().contains(Modifier.PUBLIC)) {
            //     messager.printMessage(Diagnostic.Kind.ERROR, "Subscriber method must be public", element);
            //     return true;
            // }
            //
            ExecutableElement execElement = (ExecutableElement) element;
            // element.getEnclosingElement();// 获取父元素。  方法的父元素是类
            TypeElement classElement = (TypeElement) execElement.getEnclosingElement();


            print("====AutoBindMethod elements3 ================" + element.getKind()
                    + "  " + element.getModifiers()     // [public]，不加修饰符时不是[package]而是 []
                    + "  " +  classElement);            // com.example.apidemo.data.Student
        }

        // MethodSpec
        // elementUtils.

        print("-\n"); // 换行符被删不能打印
        // generateFile("com.qq.TestFile", "package com.qq;\npublic class TestFile {\n}");

        // 返回值表示这些注解是否已由当前Processor处理。
        // 返回 true 代表该注解被当前 processor 消耗，其他注解处理器就不会再处理这些注解（甚至process方法也不执行）。即控制该注解是否需要被多个注解处理器重复处理
        return true;
    }

    /**
     * 其生成路径：app\build\generated\source\apt\debug\[pkg]\[name].java
     * @see com.qq.TestFile
     */
    private void generateFile(String className, String content) {
        try {
            JavaFileObject file = filer.createSourceFile(className);
            Writer writer = file.openWriter();
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            // javax.annotation.processing.FilerException: Attempt to recreate a file for type com.qq.TestFile
            // e.printStackTrace();
        }
    }

    private void print(CharSequence msg) {
        messager.printMessage(Diagnostic.Kind.OTHER, msg);
    }
}
