//
// Created by sjh on 2024/12/28.
//

//#ifndef APIDEMO_MYLOG_H
//#define APIDEMO_MYLOG_H
//
//#endif //APIDEMO_MYLOG_H


// 各种打印占位符的格式
// https://blog.csdn.net/wenzhi20102321/article/details/136419367


#include <android/log.h>

#define LOG_TAG "myJniTag"


#define LOGD(...) ((void)__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__))
#define LOGI(...) ((void)__android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__))
#define LOGW(...) ((void)__android_log_print(ANDROID_LOG_WARN, LOG_TAG, __VA_ARGS__))
#define LOGE(...) ((void)__android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__))


