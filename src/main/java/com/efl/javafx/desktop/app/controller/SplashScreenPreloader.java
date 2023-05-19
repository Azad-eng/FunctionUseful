package com.efl.javafx.desktop.app.controller;

import com.efl.javafx.desktop.utils.SceneTool;
import javafx.application.Preloader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * @author EFL-tjl
 */
public class SplashScreenPreloader extends Preloader {

    private static Stage stage;

    @Override
    public void init() throws Exception {
        System.out.println("preloader init");
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        //while (!CheckKey.check()) {
        //    if (!SceneTool.getConfirmAlert(null, "未找到加密锁，重试？", "请插入加密锁")) {
        //        System.exit(0);
        //    }
        //}
        System.out.println("preloader start");
        stage = primaryStage;
        //启动页面不带最小化/最大化/关闭等操作系统平台装饰，也没有标题栏
        stage.initStyle(StageStyle.UNDECORATED);
        stage.getIcons().add(SceneTool.ICON_IMAGE);
        stage.setScene(createPreloaderScene());
        stage.show();
    }

    /*@Override
    public void handleProgressNotification(ProgressNotification info) {
        System.out.println("handleProgressNotification=" + info.getProgress());
        bar.setProgress(info.getProgress());
    }*/

   /* @Override
    public void handleStateChangeNotification(StateChangeNotification info) {
        if (info.getType() == StateChangeNotification.Type.BEFORE_START) {
            System.out.println("hide preloader");
            stage.hide();
        }
    }*/

    @Override
    public void handleApplicationNotification(PreloaderNotification info) {
        if (info instanceof ProgressNotification) {
            //提取应用程序发送过来的进度值
            double v = ((ProgressNotification) info).getProgress();
            System.out.println("handleApplicationNotification="+v);
            PreloaderController.controller.setProgress(v);
        //} else if (info instanceof StateChangeNotification) {
        //    stage.close();
        //    FxmlView.MAIN.showStage();
        }
    }

    private Scene createPreloaderScene() {
        Parent root = SceneTool.loadFXML("/fxml/preloader.fxml");
        if (root == null) {
            return null;
        }
        return new Scene(root);
    }

    //public static void showMainStage() {
    //    FxmlView.MAIN.showStage();
    //    stage.close();
    //}

    public static void closeStage() {
        stage.close();
    }
}
