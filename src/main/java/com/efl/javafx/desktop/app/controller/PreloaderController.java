package com.efl.javafx.desktop.app.controller;

import com.efl.javafx.desktop.FxmlView;
import com.jfoenix.controls.JFXProgressBar;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author EFL-tjl
 */
public class PreloaderController implements Initializable {

    @FXML
    private Label progressLabel;
    @FXML
    private JFXProgressBar progressBar;
    public static String text;
    public static PreloaderController controller;

    private Stage loginStage;
    public Stage getLoginStage() {
        return loginStage;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        controller = this;
    }

    public void setProgress(double value) {
        if (value >= 0 && value <= 1) {
            progressBar.setProgress(value);
            if (value == 1) {
                //SplashScreenPreloader.showMainStage();
                SplashScreenPreloader.closeStage();
                loginStage = FxmlView.initializeUndecoratedStage("/fxml/login.fxml");
            }
        }
        if (text != null) {
            progressLabel.setText(text);
        }
    }
}
