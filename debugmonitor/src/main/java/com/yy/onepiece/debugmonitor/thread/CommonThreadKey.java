/*
 * Copyright (C) 2015-2016 Jacksgong(blog.dreamtobe.cn)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.yy.onepiece.debugmonitor.thread;

/**
 * 线程前缀，小写用于线程归类
 * Some common key of thread.
 */
public interface CommonThreadKey {
    interface OpenSource {
        String OKHTTP = "OkHttp";
        String RETROFIT = "Retrofit";
        String CRASHLYTICS = "Crashlytics";
        String LEAKCANARY = "LeakCanary";
        String RX_JAVA = "Rx";
        String PICASSO = "Picasso";
        String FILEDOWNLOADER = "FileDownloader";
        String OKIO = "okio";
        String GLIDE = "glide";
    }

    interface System {
        String MAIN = "main";
        String CHROME = "Chrome";
        String ASYNC_TASK = "AsyncTask";
        String BINDER = "Binder";
        String FINALIZER = "Finalizer";
        String WIFI = "WiFi";
        String RENDER_THREAD = "RenderThread";
        String HEAP_TASK_DAEMON = "HeapTaskDaemon";
        String REFERENCE_QUEUE_DAEMON = "ReferenceQueueDaemon";
        String FINALIZER_DAEMON = "FinalizerDaemon";
        String FINALIZER_WATCHDOG_DAEMON = "FinalizerWatchdogDaemon";
        String JDWP = "JDWP";
    }

    interface Media {
        String AUDIO = "Audio";
        String MEDIA = "Media";
        String EXO_PLAYER = "ExoPlayer";

    }

    interface Others {
        String QUEUE = "Queue";
        String THREAD_DEBUGGER = "ThreadDebugger";
        String THREAD_DEFAULT_PREFIX = "Thread-";
    }
}
