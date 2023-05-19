package com.efl.javafx.desktop.utils;

import com.efl.javafx.desktop.FxmlView;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.URLDecoder;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author EFL-tjl
 */
@Slf4j
public class FileTool {
    public static final String USER_HOME;
    public static final String APP_DATA_PARENT;
    public static String TASK_PARENT;
    public static final String TPMS_PARENT;
    public static final String DEVICE_REPORT_PARENT;
    public static final String LOG_FILE_PARENT;
    public static final String REMOTE_PRINT_TASK_NAME = "RemoteTask";
    public static final String INI_SUFFIX = ".ini";

    static {
        String userHome = System.getProperty("user.home");
        try {
            userHome = URLDecoder.decode(userHome, "utf-8");
        } catch (UnsupportedEncodingException e) {
            log.error("系统路径decode异常", e);
        }
        USER_HOME = userHome;
        APP_DATA_PARENT = userHome + "\\AppData\\Local\\EFL\\" + ResourceBundle.getBundle("application").getString("app.title") + "\\";
        TASK_PARENT = APP_DATA_PARENT + "Task\\";
        TPMS_PARENT = FileTool.APP_DATA_PARENT + "TPMS\\";
        DEVICE_REPORT_PARENT = APP_DATA_PARENT + "device_report\\";
        LOG_FILE_PARENT = APP_DATA_PARENT+ "log_printer\\";

        File data = new File(APP_DATA_PARENT);
        if (!data.exists() && !data.mkdirs()) {
            log.error("无法存储临时数据，请检查系统权限设置!");
        }

        File reportDir = new File(DEVICE_REPORT_PARENT);
        if (!reportDir.exists() && !reportDir.mkdirs()) {
            log.error("无法创建device_report缓存目录");
        }
        //将原来的记录文件转移到新的位置
        File[] files = new File(FileTool.APP_DATA_PARENT).listFiles();
        if (files != null) {
            for (File file : files) {
                String fileName = file.getName();
                if (fileName.endsWith(".xlsx") && fileName.startsWith("device_report")) {
                    if (file.renameTo(new File(reportDir, fileName))) {
                        log.info("旧光强记录文件已转移：" + fileName);
                    } else {
                        log.warn("旧光强记录文件转移失败：" + fileName);
                    }
                }
            }
        }
    }

    /**
     * 选中文件夹直接保存文件（无需输入文件名）
     */
    public static File directoryChooser(String initialFileDir){
        DirectoryChooser dir = new DirectoryChooser();
        dir.setTitle("保存文件");
        //上一次的文件夹目录不为空则设把它设为文件目录选择器的初始目录，否则不设置，即默认初始目录为桌面
        if (initialFileDir != null) {
            File file = new File(initialFileDir);
            if (file.exists()) {
                dir.setInitialDirectory(file.getParentFile());
            }
        }
        return dir.showDialog(FxmlView.MAIN.stage);
    }

    /**
     * 指定文件夹内输入文件名保存文件或指定文件覆盖保存
     */
    public static File saveFile(String extension, String path) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("保存文件");
        initFileChooser(extension, path, fileChooser);
        return fileChooser.showSaveDialog(FxmlView.MAIN.stage);
    }

    public static File chooseFile(String extension, String path) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("打开文件");
        initFileChooser(extension, path, fileChooser);
        return fileChooser.showOpenDialog(FxmlView.MAIN.stage);
    }

    private static void initFileChooser(String extension, String path, FileChooser fileChooser) {
        if (path != null) {
            File dir = new File(path);
            if (dir.exists()) {
                if (dir.isDirectory()) {
                    fileChooser.setInitialDirectory(dir);
                } else {
                    fileChooser.setInitialDirectory(dir.getParentFile());
                    String name = dir.getName();
                    int endIndex = name.lastIndexOf(".");
                    if (endIndex != -1) {
                        name = name.substring(0, endIndex);
                    }
                    fileChooser.setInitialFileName(name);
                }
            }
        }
        if (extension != null) {
            FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter(
                    extension.toUpperCase() + " files (*." + extension + ")",
                    "*." + extension);
            fileChooser.getExtensionFilters().add(filter);
        }
    }

    public static boolean copyFileUsingFileChannels(File source, File dest) {
        try (FileChannel inputChannel = new FileInputStream(source).getChannel();
             FileChannel outputChannel = new FileOutputStream(dest).getChannel()) {
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
            return true;
        } catch (IOException e) {
            log.error(source.getPath() + "文件复制失败:" + dest.getPath(), e);
            return false;
        }
    }

    public static String readTextFile(File file) {
        if (file != null && file.exists()) {
            long fileLength = file.length();
            byte[] fileContent = new byte[(int) fileLength];
            try (FileInputStream in = new FileInputStream(file)) {
                in.read(fileContent);
                return new String(fileContent, StandardCharsets.UTF_8);
            } catch (IOException e) {
                log.error("text file read fail:" + file.getPath(), e);
                return null;
            }
        }
        return null;
    }

    public static boolean saveTextFile(File file, String content) {
        if (file != null && file.exists()) {
            try (FileOutputStream out = new FileOutputStream(file)) {
                out.write(content.getBytes(StandardCharsets.UTF_8));
                return true;
            } catch (IOException e) {
                log.error("text file save fail:" + file.getPath(), e);
                return false;
            }
        }
        return false;
    }

    /**
     * 删除文件或文件夹
     * @param file 被删除的文件或文件夹
     * @return 是否成功删除
     */
    public static boolean deleteFile(File file) {
        if (!file.exists()) {
            return true;
        }
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (!deleteFile(f)) {
                        return false;
                    }
                }
            }
        }
        return file.delete();
    }


    /**
     * 获取文件行数
     */
    public static int getFileLineNum(String filePath){
        int lineNumber;
        try (LineNumberReader lineNumberReader = new LineNumberReader(new FileReader(filePath))) {
            lineNumberReader.skip(Long.MAX_VALUE);
            lineNumber = lineNumberReader.getLineNumber();
            return lineNumber + 1;
        } catch (IOException e){
            return -1;
        }
    }

    /**
     * @param str 字符串
     * @param regex 正则表达式
     * @return 字符串是否匹配正则表达式
     */
    public static boolean isMatchedRegex(String str, String regex) {
        return str.matches(regex);
    }

    /**
     * @return 返回Matcher对象
     */
    public static Matcher getMatcher(String str, String regex) {
        Pattern p = Pattern.compile(regex);
        return p.matcher(str);
    }
}
