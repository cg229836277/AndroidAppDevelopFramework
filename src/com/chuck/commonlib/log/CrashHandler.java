package com.chuck.commonlib.log;  
  
import java.io.File;  
import java.io.FileOutputStream;  
import java.io.FilenameFilter;  
import java.io.PrintWriter;  
import java.io.StringWriter;  
import java.io.Writer;  
import java.lang.Thread.UncaughtExceptionHandler;  
import java.lang.reflect.Field;  
import java.util.Arrays;  
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;  
import java.util.TreeSet;  

import com.chuck.commonlib.http.HttpDownloadManager;
import com.chuck.commonlib.http.HttpRequestData;
import com.chuck.commonlib.util.FileUtil;
import com.chuck.commonlib.util.StringUtil;

import android.content.Context;  
import android.content.pm.PackageInfo;  
import android.content.pm.PackageManager;  
import android.content.pm.PackageManager.NameNotFoundException;  
import android.os.Build;  
import android.os.Environment;
import android.os.Looper;  
import android.util.Log;  
import android.widget.Toast;  
  
/** 
 *  
 *  
 * UncaughtExceptionHandler：线程未捕获异常控制器是用来处理未捕获异常的。  
 *                           如果程序出现了未捕获异常默认情况下则会出现强行关闭对话框 
 *                           实现该接口并注册为程序中的默认未捕获异常处理  
 *                           这样当未捕获异常发生时，就可以做些异常处理操作 
 *                           例如：收集异常信息，发送错误报告 等。 
 *  
 * UncaughtException处理类,当程序发生Uncaught异常的时候,由该类来接管程序,并记录发送错误报告. 
 */  
public class CrashHandler implements UncaughtExceptionHandler {  
    /** Debug Log Tag */  
    public static final String TAG = "CrashHandler";  
    /** 是否开启日志输出, 在Debug状态下开启, 在Release状态下关闭以提升程序性能 */  
    public static final boolean DEBUG = true;  
    /** CrashHandler实例 */  
    private static CrashHandler INSTANCE;  
    /** 程序的Context对象 */  
    private Context mContext;  
    /** 系统默认的UncaughtException处理类 */  
    private Thread.UncaughtExceptionHandler mDefaultHandler;  
      
    /** 使用Properties来保存设备的信息和错误堆栈信息 */  
    private Properties mDeviceCrashInfo = new Properties();  
    private static final String VERSION_NAME = "versionName";  
    private static final String VERSION_CODE = "versionCode";  
    private static final String STACK_TRACE = "STACK_TRACE";  
    /** 错误报告文件的扩展名 */  
    private static final String CRASH_REPORTER_EXTENSION = ".cr";  
   
    private static final String CRASH_LOG_FILE_PATH = Environment.getExternalStorageDirectory() +
    		File.separator + "crash" + File.separator;
    private String uploadLogFileUrl = null;
      
    /** 保证只有一个CrashHandler实例 */  
    private CrashHandler() {  
    }  
  
    /** 获取CrashHandler实例 ,单例模式 */  
    public static CrashHandler getInstance() {  
        if (INSTANCE == null)  
            INSTANCE = new CrashHandler();  
        return INSTANCE;  
    }  
      
    /**
     * 
     * 
     * @author admin
     * @date 2015-5-11 上午11:33:40
     * @param ctx
     * @param uploadFileUrl log日志上传地址,为空的话保存在本地/mnt/sdcard/crash
     */
    public void init(Context ctx , String uploadFileUrl) {  
        mContext = ctx;  
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();  
        Thread.setDefaultUncaughtExceptionHandler(this);  
        
        if(!StringUtil.isEmpty(uploadFileUrl)){
        	uploadLogFileUrl = uploadFileUrl;
        }
    }  
      
    /** 
     * 当UncaughtException发生时会转入该函数来处理 
     */  
    @Override  
    public void uncaughtException(Thread thread, Throwable ex) {  
        if (!handleException(ex) && mDefaultHandler != null) {  
            // 如果用户没有处理则让系统默认的异常处理器来处理  
            mDefaultHandler.uncaughtException(thread, ex);  
        } else {   
            //android.os.Process.killProcess(android.os.Process.myPid());  
            System.exit(0);  
        }  
    }  
  
    /** 
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成. 开发者可以根据自己的情况来自定义异常处理逻辑 
     *  
     * @param ex 
     * @return true:如果处理了该异常信息;否则返回false 
     */  
    private boolean handleException(Throwable ex) {  
        if (ex == null) {  
            return true;  
        }  
        final String msg = ex.getLocalizedMessage();  
        // 使用Toast来显示异常信息  
//        new Thread() {  
//            @Override  
//            public void run() {  
//                // Toast 显示需要出现在一个线程的消息队列中  
//                Looper.prepare();  
//                Toast.makeText(mContext, "程序出错啦:" + msg, Toast.LENGTH_LONG).show();  
//                Looper.loop();  
//            }  
//        }.start();  
        // 收集设备信息  
        collectCrashDeviceInfo(mContext);  
        // 保存错误报告文件  
        String crashFileName = saveCrashInfoToFile(ex);  
        // 发送错误报告到服务器  
        sendCrashReportsToServer(mContext);  
        return true;  
    }  
  
    /** 
     * 收集程序崩溃的设备信息 
     *  
     * @param ctx 
     */  
    public void collectCrashDeviceInfo(Context ctx) {  
        try {   
            PackageManager pm = ctx.getPackageManager();  
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);  
            if (pi != null) {  
                mDeviceCrashInfo.put(VERSION_NAME, pi.versionName == null ? "not set" : pi.versionName);  
                mDeviceCrashInfo.put(VERSION_CODE, pi.versionCode);  
            }  
        } catch (NameNotFoundException e) {  
            Log.e(TAG, "Error while collect package info", e);  
        }  
        // 使用反射来收集设备信息.在Build类中包含各种设备信息,  
        // 例如: 系统版本号,设备生产商 等帮助调试程序的有用信息  
        // 返回 Field 对象的一个数组，这些对象反映此 Class 对象所表示的类或接口所声明的所有字段  
        Field[] fields = Build.class.getDeclaredFields();  
        for (Field field : fields) {  
            try {  
                // setAccessible(boolean flag)  
                // 将此对象的 accessible 标志设置为指示的布尔值。  
                // 通过设置Accessible属性为true,才能对私有变量进行访问，不然会得到一个IllegalAccessException的异常  
                field.setAccessible(true);  
                mDeviceCrashInfo.put(field.getName(), field.get(null));  
                if (DEBUG) {  
                    Log.d(TAG, field.getName() + " : " + field.get(null));  
                }  
            } catch (Exception e) {  
                Log.e(TAG, "Error while collect crash info", e);  
            }  
        }  
    }  
      
    /** 
     * 保存错误信息到文件中 
     *  
     * @param ex 
     * @return 
     */  
    private String saveCrashInfoToFile(Throwable ex) {  
        Writer info = new StringWriter();  
        PrintWriter printWriter = new PrintWriter(info);  
        // printStackTrace(PrintWriter s)  
        // 将此 throwable 及其追踪输出到指定的 PrintWriter  
        ex.printStackTrace(printWriter);  
        // getCause() 返回此 throwable 的 cause；如果 cause 不存在或未知，则返回 null。  
        Throwable cause = ex.getCause();  
        while (cause != null) {  
            cause.printStackTrace(printWriter);  
            cause = cause.getCause();  
        }  
  
        // toString() 以字符串的形式返回该缓冲区的当前值。  
        String result = info.toString();  
        printWriter.close();  
        mDeviceCrashInfo.put(STACK_TRACE, result);  
  
        try {  
            long timestamp = System.currentTimeMillis();  
            String exceptionTime = String.valueOf(timestamp);
            String fileName = "crash-" + exceptionTime + CRASH_REPORTER_EXTENSION;         
            File file = new File(CRASH_LOG_FILE_PATH);
            if(!file.exists()){
            	file.mkdirs();
            }
            // 保存文件  
            FileOutputStream trace = new FileOutputStream(CRASH_LOG_FILE_PATH + fileName); 
            String collectInfo = mDeviceCrashInfo.toString();
//            mDeviceCrashInfo.store(trace, null);    
            trace.write(collectInfo.getBytes());
            trace.flush();  
            trace.close();  
            return CRASH_LOG_FILE_PATH + fileName;  
        } catch (Exception e) {  
            Log.e(TAG, "an error occured while writing report file...", e);  
        }  
        return null;  
    }  
      
    /** 
     * 把错误报告发送给服务器,包含新产生的和以前没发送的. 
     *  
     * @param ctx 
     */  
    private void sendCrashReportsToServer(Context ctx) {  
        String[] crFiles = getCrashReportFiles(ctx);  
        if (crFiles != null && crFiles.length > 0) {  
            TreeSet<String> sortedFiles = new TreeSet<String>();  
            sortedFiles.addAll(Arrays.asList(crFiles));  
  
            for (String fileName : sortedFiles) {  
                File cr = new File(CRASH_LOG_FILE_PATH + fileName);  
                postReport(cr);  
                cr.delete();// 删除已发送的报告  
            }  
        }  
    }  
  
    /** 
     * 获取错误报告文件名 
     *  
     * @param ctx 
     * @return 
     */  
    private String[] getCrashReportFiles(Context ctx) {  
        File filesDir = new File(CRASH_LOG_FILE_PATH);  
        // 实现FilenameFilter接口的类实例可用于过滤器文件名  
        FilenameFilter filter = new FilenameFilter() {  
            // accept(File dir, String name)  
            // 测试指定文件是否应该包含在某一文件列表中。  
            public boolean accept(File dir, String name) {  
                return name.endsWith(CRASH_REPORTER_EXTENSION);  
            }  
        };  
        // list(FilenameFilter filter)  
        // 返回一个字符串数组，这些字符串指定此抽象路径名表示的目录中满足指定过滤器的文件和目录  
        return filesDir.list(filter);  
    }  
  
    private void postReport(File file) {  
    	if(FileUtil.isFileExist(file, false) && !StringUtil.isEmpty(uploadLogFileUrl)){
    		Map<String , Object> paramsMap = new HashMap<String, Object>();
    		paramsMap.put("file", file);
    		HttpRequestData.httpPostUploadData(uploadLogFileUrl, paramsMap);
    	}   	
    }  
  
    /** 
     * 在程序启动时候, 可以调用该函数来发送以前没有发送的报告 
     */  
    public void sendPreviousReportsToServer() {  
        sendCrashReportsToServer(mContext);  
    }  
} 