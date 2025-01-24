package kr.co.less.consent.yjh.edu;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.os.Environment;
import kr.co.clipsoft.util.EFromViewer;

public class CustomUncaughtExceptionHandler implements UncaughtExceptionHandler {
    private Context context;
    private UncaughtExceptionHandler defaultUEH;

    public CustomUncaughtExceptionHandler(Context context) {
        this.context = context;
        this.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        // Get current timestamp
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        // Create log message
        StringBuilder report = new StringBuilder();
        report.append("Date: ").append(timestamp).append("\n");
        report.append("Thread: ").append(thread.getName()).append("\n");
        report.append("Exception: ").append(throwable.toString()).append("\n");
        report.append("Stack Trace:\n");
        for (StackTraceElement element : throwable.getStackTrace()) {
            report.append(element.toString()).append("\n");
        }

        // Write the log to a file
        writeLogToFile(report.toString());

        // Pass the exception to the default handler (optional)
        if (defaultUEH != null) {
            defaultUEH.uncaughtException(thread, throwable);
        }
    }

    private void writeLogToFile(String log) {
        EFromViewer.writeLog("writeLogToFile");

        // Android 10 이상에서 외부 파일 접근 방식 변경
        File logFile = new File(Environment.getExternalStorageDirectory() + "/arum_log/crash.txt");

        try (FileWriter fileWriter = new FileWriter(logFile, true); 
             PrintWriter printWriter = new PrintWriter(fileWriter)) {
            // 로그 메시지 작성
            printWriter.println(log);
            printWriter.flush(); // 강제로 기록
        } catch (IOException e) {
            // 예외 발생 시 처리
            EFromViewer.writeLog("Error writing to UncaughtExcep log file: " + e.getMessage());
        }
    }
}
