package com.efl.javafx.desktop;

import com.efl.javafx.desktop.utils.SceneTool;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.ResourceBundle;

/**
 * @author EFL-tjl
 */
public enum FxmlView {
    /**
     * 主界面
     */
    MAIN {
        @Override
        public String title() {
            return getStringFromResourceBundle("app.title") + " V" + getStringFromResourceBundle("app.version");
        }

        @Override
        public String fxml() {
            return "/fxml/main.fxml";
        }

    },

    /**
     * 设置界面
     */
    SETTING_MACHINE {
        @Override
        public String title() {
            return getStringFromResourceBundle("setting.title");
        }

        @Override
        public String fxml() {
            return "/fxml/machineSettings.fxml";
        }
    },

    /**
     * 设置界面
     */
    USER_MANAGE {
        @Override
        public String title() {
            return "用户后台管理";
        }

        @Override
        public String fxml() {
            return "/fxml/machineSettings.fxml";
        }
    },

    /**
     * 设备运转信息页面
     */
    DEVICE_REPORT{
        @Override
        public String title(){return "Device Report";}

        @Override
        public String fxml(){return "/fxml/device_report.fxml";}
    },
    /**
     * 关于界面
     */
    INFORMATION {
        @Override
        public String title() {
            return "关于打印机";
        }

        @Override
        public String fxml() {
            return "/fxml/about.fxml";
        }
    },

    /**
     * 预置模型窗口
     */
    PRE_MODELS {
        @Override
        public String title() {
            return "加载预置模型";
        }

        @Override
        public String fxml() {
            return "/fxml/pre_models.fxml";
        }
    },

    /**
     * expert window
     */
    EXPERT {
        @Override
        public String title() {
            return "expert";
        }

        @Override
        public String fxml() {
            return "/fxml/expert_operate.fxml";
        }
    },

    /**
     * 不同区域时间设置
     */
    REGIONS_TIME_SET {
        @Override
        public String title() {
            return "区域时间设置";
        }

        @Override
        public String fxml() {
            return "/fxml/detach/regionsTimeSet.fxml";
        }
    },

    /**
     * update window
     */
    UPDATE {
        @Override
        public String title() {
            return "打印机升级";
        }

        @Override
        public String fxml() {
            return "/fxml/update.fxml";
        }
    };

    public Stage stage;

    public abstract String title();

    public abstract String fxml();

    public String getStringFromResourceBundle(String key) {
        return ResourceBundle.getBundle("application").getString(key);
    }

    public void initStage(Stage primaryStage) {
        if (primaryStage == null) {
            stage = new Stage();
            setMode();
        } else {
            stage = primaryStage;
        }
        Parent root = SceneTool.loadFXML(fxml());
        if (root == null) {
            return;
        }
        final Scene rootScene = new Scene(root);

        stage.setTitle(title());
        stage.getIcons().add(SceneTool.ICON_IMAGE);
        stage.setScene(rootScene);
        System.out.println(title() + " ready");
    }

    public void showStage(String title) {
        if (stage != null) {
            stage.setTitle(title() + title);
            stage.show();
        }
    }

    public void showStageByPreTitle(String title) {
        if (stage != null) {
            stage.setTitle(title + title());
            stage.show();
        }
    }

    public void showStage() {
        if (stage != null) {
            stage.show();
        }
    }

    public void closeStage() {
        if (stage != null) {
            stage.close();
        }
    }

    private void setMode() {
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(FxmlView.MAIN.stage);
    }

    public static Stage initializeUndecoratedStage(String fxml) {
        Stage loginStage = new Stage();
        Parent root = SceneTool.loadFXML(fxml);
        if (root == null) {
            return null;
        }
        final Scene rootScene = new Scene(root);
        //不带最小化/最大化/关闭等操作系统平台装饰，也没有标题栏
        loginStage.initStyle(StageStyle.UNDECORATED);
        //loginStage.getIcons().add(SceneTool.ICON_IMAGE);
        loginStage.setScene(rootScene);
        loginStage.show();
        return loginStage;
    }
}
