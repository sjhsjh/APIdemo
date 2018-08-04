package com.example.apidemo.utils;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 
 * <br>类描述: 日志类
 * <br>功能详细描述: 支持设置输出到文件的方式{@link #LOG_TYPE}，以及最低优先级{@link #LOG_LEVEL_MIN}来过滤日志。
 * 日志文件输出到sd卡目录下的{@link #LOG_DIR_NAME}中，如果在该目录下创建文件{@link #NO_LOG_FILE_NAME}可以在当前
 * 日志文件超过限制{@link #LOG_COUNT_LIMIT_PER_FILE}后不再输出日志。
 * 
 * 默认的用法是使用各种静态方法，跟{@link android.util.Log}一致，使用者只需更改import的类为本类即可。
 * 还可以创建一个实例，通过构造方法{@link #NLog(String)}指定写日志时的文件名前缀（默认为nlog），以区分其他模块的日志
 * （如果不想使用TAG来区分）。然后调用各种非静态的方法来输出日志，例如{@link #dns(String, String)}。
 * 
 * @author  dengweiming
 * @date  [2013-8-13]
 */
public class NLog {
	private static final String TAG = "NLog";

    /**
     * Priority constant for the println method; useNLog.v.
     */
    public static final int VERBOSE = 2;

    /**
     * Priority constant for the println method; useNLog.d.
     */
    public static final int DEBUG = 3;

    /**
     * Priority constant for the println method; useNLog.i.
     */
    public static final int INFO = 4;

    /**
     * Priority constant for the println method; useNLog.w.
     */
    public static final int WARN = 5;

    /**
     * Priority constant for the println method; useNLog.e.
     */
    public static final int ERROR = 6;

    /**
     * Priority constant for the println method.
     */
    public static final int ASSERT = 7;


    private static final String PRIORITYS_STRING[] = {
		" ",
    	" ",
    	"V",
    	"D",
    	"I",
    	"W",
    	"E",
    	"A",
    };

	/** 按平常的方式，输出到日志缓冲区 */
	private static final int LOG_NORMAL = 1;
	/** 输出到日志文件 */
	private static final int LOG_TO_FILE = 2;
	/** 输出日志到缓存区和文件 */
	private static final int LOG_BOTH_BUFF_SD = 3;

	//配置项
	private boolean mLogSwitch = true;							//日志开关
	private static final int LOG_TYPE = LOG_NORMAL;			//日志输出类型 LOG_BOTH_BUFF_SD
	private static final int LOG_LEVEL_MIN = VERBOSE;			//允许通过的最小日志等级
	private static final String LOG_DIR_NAME = "debug_log";	//日志输出目录，位于sd卡目录下，根据项目修改
	private static final String NO_LOG_FILE_NAME = "nolog";		//在日志输出目录下创建这个文件，在当前日志文件超过限制时禁止继续输出
	private static final int LOG_COUNT_LIMIT_PER_FILE = 20000;	//每个文件的日志输出限制
	private static String mLogDirPath;// = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();	//  /storage/emulated/0/Android/data/com.example.apidemo/files

	private final Object mLOCK = new Object();
	private File mCurFile;
	private int mLogCount;
	private String mLogFilePrefix;
	private boolean mFirstLog = true;
	//CHECKSTYLE IGNORE 1 LINES
	private static final NLog sInstance = new NLog("nlog");

	/**
	 * 非静态的log
	 * @param logFilePrefix	日志文件名称前缀，以便区分
	 */
	public NLog(String logFilePrefix) {
		mLogFilePrefix = logFilePrefix == null ? "" : logFilePrefix;
	}

	public static void setLogDirPath(String logDirPath) {
		if(!TextUtils.isEmpty(logDirPath)){
			mLogDirPath = logDirPath;
		}
	}

	public void setLogSwitch(boolean logSwitch) {
		mLogSwitch = logSwitch;
	}

	/**
	 * 要构造对象使用log，用处不大.
     */
	public int vns(String tag, String msg) {
		return println_native_non_static(LOG_ID_MAIN, VERBOSE, tag, msg);
	}
	
    public int dns(String tag, String msg) {
        return println_native_non_static(LOG_ID_MAIN, DEBUG, tag, msg);
    }
    
    public int dns(String tag, String msg, Throwable tr) {
    	return println_native_non_static(LOG_ID_MAIN, DEBUG, tag, msg + '\n' + getStackTraceString(tr));
    }
    
    public int ins(String tag, String msg) {
    	return println_native_non_static(LOG_ID_MAIN, INFO, tag, msg);
    }
    
    public int wns(String tag, String msg) {
    	return println_native_non_static(LOG_ID_MAIN, WARN, tag, msg);
    }
    
    public int ens(String tag, String msg) {
    	return println_native_non_static(LOG_ID_MAIN, ERROR, tag, msg);
    }
    
    public int ens(String tag, String msg, Throwable tr) {
    	return println_native_non_static(LOG_ID_MAIN, ERROR, tag, msg + '\n' + getStackTraceString(tr));
    }


	public static int v(String msg) {
		return println_native(LOG_ID_MAIN, VERBOSE, TAG, msg);
	}

    /**
     * Send a {@link #VERBOSE} log message.
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static int v(String tag, String msg) {
        return println_native(LOG_ID_MAIN, VERBOSE, tag, msg);
    }

    /**
     * Send a {@link #VERBOSE} log message and log the exception.
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr An exception to log
     */
    public static int v(String tag, String msg, Throwable tr) {
        return println_native(LOG_ID_MAIN, VERBOSE, tag, msg + '\n' + getStackTraceString(tr));
    }

	public static int d(String msg) {
		return println_native(LOG_ID_MAIN, DEBUG, TAG, msg);
	}

    /**
     * Send a {@link #DEBUG} log message.
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static int d(String tag, String msg) {
        return println_native(LOG_ID_MAIN, DEBUG, tag, msg);
    }

    /**
     * Send a {@link #DEBUG} log message and log the exception.
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr An exception to log
     */
    public static int d(String tag, String msg, Throwable tr) {
        return println_native(LOG_ID_MAIN, DEBUG, tag, msg + '\n' + getStackTraceString(tr));
    }

	public static int i(String msg) {
		return println_native(LOG_ID_MAIN, INFO, TAG, msg);
	}

    /**
     * Send an {@link #INFO} log message.
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static int i(String tag, String msg) {
        return println_native(LOG_ID_MAIN, INFO, tag, msg);
    }

    /**
     * Send a {@link #INFO} log message and log the exception.
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr An exception to log
     */
    public static int i(String tag, String msg, Throwable tr) {
        return println_native(LOG_ID_MAIN, INFO, tag, msg + '\n' + getStackTraceString(tr));
    }

	public static int w(String msg) {
		return println_native(LOG_ID_MAIN, WARN, TAG, msg);
	}

    /**
     * Send a {@link #WARN} log message.
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static int w(String tag, String msg) {
        return println_native(LOG_ID_MAIN, WARN, tag, msg);
    }

    /**
     * Send a {@link #WARN} log message and log the exception.
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr An exception to log
     */
    public static int w(String tag, String msg, Throwable tr) {
        return println_native(LOG_ID_MAIN, WARN, tag, msg + '\n' + getStackTraceString(tr));
    }

    /*
     * Send a {@link #WARN} log message and log the exception.
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param tr An exception to log
     */
    public static int w(String tag, Throwable tr) {
        return println_native(LOG_ID_MAIN, WARN, tag, getStackTraceString(tr));
    }

	public static int e(String msg) {
		return println_native(LOG_ID_MAIN, ERROR, TAG, msg);
	}

    /**
     * Send an {@link #ERROR} log message.
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static int e(String tag, String msg) {
        return println_native(LOG_ID_MAIN, ERROR, tag, msg);
    }

    /**
     * Send a {@link #ERROR} log message and log the exception.
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr An exception to log
     */
    public static int e(String tag, String msg, Throwable tr) {
        return println_native(LOG_ID_MAIN, ERROR, tag, msg + '\n' + getStackTraceString(tr));
    }

    /**
     * Handy function to get a loggable stack trace from a Throwable
     * @param tr An exception to log
     */
    public static String getStackTraceString(Throwable tr) {
        if (tr == null) {
            return "";
        }

        // This is to reduce the amount of log spew that apps do in the non-error
        // condition of the network being unavailable.
        Throwable t = tr;
        while (t != null) {
            if (t instanceof UnknownHostException) {
                return "";
            }
            t = t.getCause();
        }

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        tr.printStackTrace(pw);
        return sw.toString();
    }

    /**
     * Low-level logging call.
     * @param priority The priority/type of this log message
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @return The number of bytes written.
     */
    public static int println(int priority, String tag, String msg) {
        return println_native(LOG_ID_MAIN, priority, tag, msg);
    }

    /** @hide */ public static final int LOG_ID_MAIN = 0;
    /** @hide */ public static final int LOG_ID_RADIO = 1;
    /** @hide */ public static final int LOG_ID_EVENTS = 2;
    /** @hide */ public static final int LOG_ID_SYSTEM = 3;

    /** @hide */ 
    //CHECKSTYLE IGNORE 1 LINES
    public static int println_native(int bufID, int priority, String tag, String msg) {
    	return sInstance.println_native_non_static(bufID, priority, tag, msg);
    }

    /**
	 * 管理输出日志到缓存区和sd卡.每次启动对应一个日志文件.
     */
	@SuppressLint("SimpleDateFormat")
	//CHECKSTYLE IGNORE 1 LINES
	private int println_native_non_static(int bufID, int priority, String tag, String msg) {
		if(!mLogSwitch){
			return 0;
		}
		if(TextUtils.isEmpty(mLogDirPath)){
			android.util.Log.println(priority, tag, msg);
			android.util.Log.println(NLog.ERROR, TAG, "mLogDirPath empty. ");
			return 0;
		}
		if (mFirstLog) {
			mFirstLog = false;
			ins(TAG, "====================\n======Log init======\n====================");	// 注意tag是“NLog”而不是传入参数的tag。
		}
		switch (LOG_TYPE) {
			case LOG_NORMAL:
				return android.util.Log.println(priority, tag, msg);
			case LOG_TO_FILE:
				break;
			case LOG_BOTH_BUFF_SD:
				android.util.Log.println(priority, tag, msg);
				break;
			default :
				break;
		}
		if (priority < VERBOSE || priority > ASSERT) {
			throw new IllegalArgumentException("wrong priority =" + priority);
		}
		if (priority < LOG_LEVEL_MIN) {
			return 0;
		}
		String sdStateString = android.os.Environment.getExternalStorageState();
		if (!sdStateString.equals(android.os.Environment.MEDIA_MOUNTED)) {
			// 没有可读可写权限
			android.util.Log.println(NLog.ERROR, TAG, "no write permission. ");
			return 0;
		}

		//格式化输出信息
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		Date date = new Date(System.currentTimeMillis());
		String dateString = dateFormat.format(date);

		msg = dateString + ": " + PRIORITYS_STRING[priority] + "/" + tag + ": " + msg + "\n";

		synchronized (mLOCK) {	// 注意不要在该函数段内使用NLog，否则就是死循环!
			if (mLogCount % LOG_COUNT_LIMIT_PER_FILE == 0) {	// 创建日志文件
				//超过行数限制时创建新日志文件
				mLogCount = 0;
				// 创建文件夹
				String dirName = mLogDirPath + File.separator + LOG_DIR_NAME;
				File dir = new File(dirName);
				try {
					if (dir.exists() && !dir.isDirectory()) {
						dir.delete();
					}
					if (!dir.exists()) {
						boolean result = dir.mkdirs();
						if(!result){
							dir = null;
							android.util.Log.println(NLog.ERROR, TAG, "mkdirs fail. ");
							return 0;
						}
					}
				} catch (SecurityException e) {
					dir = null;
					android.util.Log.println(NLog.ERROR, TAG, "SecurityException = " + e.getMessage());
					return 0;
				}
				
				if ((new File(dirName + File.separator + NO_LOG_FILE_NAME)).exists()) {
					mCurFile = null;
					return 0;
				}
				// 每次进入程序都创建 文件名带有时刻的日志文件
				dateString = dateString.replace(':', '-').replace(' ', '_');
				String sCurFileName = dirName + File.separator + mLogFilePrefix + "_" + dateString + ".txt";

				mCurFile = new File(sCurFileName);
				if (!mCurFile.exists()) {
					try {
						mCurFile.createNewFile();
					} catch (IOException e) {
						android.util.Log.println(NLog.ERROR, TAG, "e = " + e);
						mCurFile = null;
						e.printStackTrace();
						return 0;
					}
				}

			}

			mLogCount++;
			if (mCurFile == null) {
				return 0;
			}
			try {
				//写数据
				FileOutputStream outputStream = new FileOutputStream(mCurFile, true);
				outputStream.write(msg.getBytes());		// 文件被删除后outputStream.write仍能创建文件并继续写入数据。
				outputStream.close();
			} catch (Exception e) {
				// 若文件夹被删除则报错FileNotFoundException。
				e.printStackTrace();
			}
		}
		return 1;
	}
}
