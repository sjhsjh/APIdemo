
cmake_minimum_required(VERSION 3.4.1)

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

add_library( # Sets the name of the library.  设置生成so文件名
        Java2C
        # Sets the library as a shared library.
        SHARED
        # Provides a relative path to your source file(s).  要编译的C/C++文件
        src/main/jni/Java2Cplus.cpp
        src/main/jni/JavaToC.c)



## 设置构建本地库所需的最小版本的cbuild。
#cmake_minimum_required(VERSION 3.4.1)
## 创建并命名一个库，将其设置为静态
## 或者共享，并提供其源代码的相对路径。
## 您可以定义多个库，而cbuild为您构建它们。
## Gradle自动将共享库与你的APK打包。
#add_library( native-lib       #设置库的名称。即SO文件的名称，生产的so文件为“libnative-lib.so”,  在加载的时候“System.loadLibrary("native-lib");”
#        SHARED            # 将库设置为共享库。
#        native-lib.cpp    # 提供一个源文件的相对路径
#        helloJni.cpp      # 提供同一个SO文件中的另一个源文件的相对路径
#        )
## 搜索指定的预构建库，并将该路径存储为一个变量。因为cbuild默认包含了搜索路径中的系统库，所以您只需要指定您想要添加的公共NDK库的名称。cbuild在完成构建之前验证这个库是否存在。
#find_library(log-lib   # 设置path变量的名称。
#        log       #  指定NDK库的名称 你想让CMake来定位。
#        )
##指定库的库应该链接到你的目标库。您可以链接多个库，比如在这个构建脚本中定义的库、预构建的第三方库或系统库。
#target_link_libraries( native-lib    # 指定目标库中。与 add_library的库名称一定要相同
#        ${log-lib}    # 将目标库链接到日志库包含在NDK。
#        )
##如果需要生产多个SO文件的话，写法如下
#add_library( natave-lib       # 设置库的名称。另一个so文件的名称
#        SHARED           # 将库设置为共享库。
#        nataveJni.cpp    # 提供一个源文件的相对路径
#        )
#target_link_libraries( natave-lib     #指定目标库中。与 add_library的库名称一定要相同
#        ${log-lib}     # 将目标库链接到日志库包含在NDK。
#        )