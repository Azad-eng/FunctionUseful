package com.efl.javafx.desktop.app.controller;

import com.efl.javafx.desktop.FxmlView;
import com.efl.javafx.desktop.MachineConfig;
import com.efl.javafx.desktop.dbserver.service.UserService;
import com.efl.javafx.desktop.dbserver.service.impl.UserServiceImpl;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author EFL-tjl
 */
public class LoginController implements Initializable {
    @FXML
    private TextField userNameInput, passwordInput;
    @FXML
    private Button loginButton;
    @FXML
    private Label loginResultInfo;

    public static String text;
    public static LoginController controller;
    private static Stage signInStage;

    private UserService userService = UserServiceImpl.userServiceImpl;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setLoginResultInfo(null);
        controller = this;
    }

    @FXML
    private void login() {
        String userName = userNameInput.getText();
        String password = passwordInput.getText();
        setLoginResultInfo(null);
        if (userName.isEmpty()) {
            setLoginResultInfo("请输入用户名");
            return;
        }
        if (password.isEmpty()) {
            setLoginResultInfo("请输入密码");
            return;
        }
        System.out.println("userService" + userService);
        if (userService.findByUserName(userName) == null) {
            setLoginResultInfo("该账户不存在，请联系管理员或相关技术人员");
            return;
        }
        if (userService.find(userName, password) == null) {
            setLoginResultInfo("密码错误，请重新输入");
            passwordInput.requestFocus();
            return;
        }
        String title;
        if (userName.equals("efl")) {
            setLoginResultInfo("登陆成功，即将进入超级管理员主界面");
            title = "Efl：";
            MachineConfig.USER = 0;
        } else if (userName.equals("admin")) {
            setLoginResultInfo("登陆成功，即将进入管理员主界面");
            title = "Admin：";
            MachineConfig.USER = 1;
        } else {
            setLoginResultInfo("登陆成功，即将进入用户主页面");
            title = userName;
            MachineConfig.USER = 2;
        }
        FxmlView.MAIN.showStageByPreTitle(title);
        PreloaderController.controller.getLoginStage().close();
    }

    private void setLoginResultInfo(String text) {
        if (text != null) {
            loginResultInfo.setVisible(true);
            loginResultInfo.setText(text);
        } else {
            loginResultInfo.setVisible(false);
        }
    }


    @FXML
    private void createAccount() {
        setLoginResultInfo(null);
        if (signInStage == null || !signInStage.isShowing()) {
            signInStage = FxmlView.initializeUndecoratedStage("/fxml/signIn.fxml");
        }
    }
}
