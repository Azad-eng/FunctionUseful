package com.efl.javafx.desktop.utils;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.Border;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author EFL-tjl
 */
public class SceneTool {

    public static final Image ICON_IMAGE = new Image(SceneTool.class.getResource("/8601Logo-24.png").toString());
    public static Stage stage;

    public static void closeNotification(){
        if (stage.isShowing()){
            stage.close();
        }
    }

    public static ArrayList<Node> getControlList(Pane container) {
        ArrayList<Node> result = new ArrayList<>();
        container.getChildren().forEach(node -> {
            result.add(node);
            if (node instanceof Pane) {
                result.addAll(getControlList((Pane) node));
            }
        });
        return result;
    }

    public static void setPaneVisible(boolean visible, Pane pane) {
        getControlList(pane).forEach( control -> {
            control.setVisible(visible);
            control.setManaged(visible);
        });
        pane.setVisible(visible);
        pane.setManaged(visible);
    }

    public static void setControlVisible(boolean visible, Control... controls) {
        for (Control control : controls) {
            control.setVisible(visible);
            control.setManaged(visible);
        }
    }

    public static void updateList(ComboBox<String> comboBox, String directory, String suffix) {
        File configParent = new File(directory);
        String selected = comboBox.getValue();
        //清除列表时，如果选择不为空，则会触发一次onAction
        comboBox.getItems().clear();
        if (configParent.exists() && configParent.isDirectory()) {
            for (String fileName : Objects.requireNonNull(configParent.list())) {
                if (fileName.endsWith(suffix)) {
                    String item = fileName.substring(0, fileName.length() - suffix.length());
                    comboBox.getItems().add(item);
                }
            }
        } else {
            if (!configParent.mkdirs()) {
                System.out.println("创建路径生成参数文件夹失败：" + directory);
            } else {
                System.out.println("创建路径生成参数文件夹成功：" + directory);
            }
        }
        //UI操作进入此方法更新列表，弹出列表后，恢复选择。后台删除某个参数选项后，不能恢复选择
        if (comboBox.getItems().contains(selected)) {
            comboBox.setValue(selected);
        } else {
            comboBox.setValue(null);
        }
    }

    public static String confirmParaSaveName(ComboBox<String> configComboBox, boolean confirmName) {
        String name = configComboBox.getValue();
        if (confirmName || name == null || configComboBox.getItems().size() == 0) {
            return confirmName(name, configComboBox.getItems(), "参数", null, "参数保存");
        }
        return name;
    }

    /**
     * 确认名称，类似于文件保存时的对话框，会将已有的展示出来，并提示覆盖。
     * 可以用来确认任务名称，参数名称等
     * @param defaultName 默认的名称
     * @param nameList 已有的列表
     * @param type 确认类型，如【任务】【参数】
     * @param exceptName 排除的名称，即结果不可为此
     * @return 确认后的名称
     */
    public static String confirmName(String defaultName, List<String> nameList, String type, String exceptName, String dialogTitle) {
        Dialog<String> dialog = new Dialog<>();
        if (dialogTitle == null) {
            dialog.setTitle("请确认" + type + "名称");
        } else {
            dialog.setTitle(dialogTitle);
        }
        dialog.setHeaderText("请输入新" + type + "名");

        ButtonType uploadButtonType = new ButtonType("确认", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(uploadButtonType, ButtonType.CANCEL);

        Node uploadButton = dialog.getDialogPane().lookupButton(uploadButtonType);
        uploadButton.setDisable(true);

        TextField newNameTextField = new TextField();
        Label nameErrorInfoLabel = new Label("名称不能包含：/ \\ < > * | : ? \"");
        nameErrorInfoLabel.setTextFill(Color.RED);
        nameErrorInfoLabel.managedProperty().bind(nameErrorInfoLabel.visibleProperty());
        newNameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            //任务名不能为空，也不能与远程打印时的任务名相同
            boolean notIllegalName = newValue == null || !FileTool.isMatchedRegex(newValue, "[^\\s\\\\/:\\*\\?\\\"<>\\|](\\x20|[^\\s\\\\/:\\*\\?\\\"<>\\|])*");
            uploadButton.setDisable(notIllegalName || newValue.equals(exceptName));
            if (newValue != null && newValue.length() > 0) {
                nameErrorInfoLabel.setVisible(notIllegalName);
            } else {
                nameErrorInfoLabel.setVisible(false);
            }
        });
        newNameTextField.setText(defaultName);
        HBox hBox = new HBox(new Label("新" + type + "名："), newNameTextField);
        hBox.setAlignment(Pos.CENTER_LEFT);

        ListView<String> taskListView = new ListView<>();
        taskListView.getItems().addAll(nameList);
        taskListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        taskListView.getSelectionModel().select(defaultName);
        taskListView.scrollTo(defaultName);
        taskListView.getSelectionModel().getSelectedItems().addListener((ListChangeListener<String>) c -> {
            newNameTextField.setText(taskListView.getSelectionModel().getSelectedItem());
            Platform.runLater(() -> {
                newNameTextField.requestFocus();
                newNameTextField.selectAll();
            });
        });
        taskListView.setPrefHeight(240);

        final VBox vBox = new VBox();
        vBox.setSpacing(5);
        vBox.setAlignment(Pos.CENTER_LEFT);
        vBox.getChildren().addAll(new Label("已有" + type + "："), taskListView, nameErrorInfoLabel, hBox);
        dialog.getDialogPane().setContent(vBox);
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(ICON_IMAGE);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == uploadButtonType) {
                return newNameTextField.getText();
            }
            return null;
        });

        //初始化
        Platform.runLater(() -> {
            newNameTextField.requestFocus();
            newNameTextField.selectAll();
        });

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String newName = result.get();
            if (nameList.contains(newName) &&
                    !SceneTool.getConfirmAlert("请确认", "覆盖此" + type + "：" + newName, "已存在此" + type)) {
                return null;
            } else {
                return newName;
            }
        }
        return null;
    }


    public static void deleteSelectedProfile(ComboBox<String> configComboBox, String directory) {
        String name = configComboBox.getValue();
        if (name != null) {
            if (getConfirmAlert("参数删除", "确认删除此组参数：", name)) {
                File deleteFile = new File(directory, name + FileTool.INI_SUFFIX);
                if (!deleteFile.delete()) {
                    System.out.println("参数删除失败：" + deleteFile.getAbsolutePath());
                }
                updateList(configComboBox, deleteFile.getParent(), FileTool.INI_SUFFIX);
                //删除后默认选择第一个
                configComboBox.getSelectionModel().select(0);
            }
        }
    }

    /**
     * 从spinner中获取值
     * 如果用户在框中输入了数字但没有按下Enter键，导致值实际上未能设置，此方法将对实际值进行更新
     * 如果出现异常，spinner的值将被重置为0
     * 此方法可能会触发spinner的valueProperty Listener
     * @param spinner 需要获取数值的spinner
     * @return spinner的值
     */
    public static Double getDoubleSpinnerValue(Spinner<Double> spinner, double defaultValue) {
        String numStr = spinner.getEditor().getText();
        Double temp = defaultValue;
        StringConverter<Double> converter = spinner.getValueFactory().getConverter();
        try {
            temp = converter.fromString(numStr);
        } catch (Exception ignore) {
        }
        if (temp == null) {
            temp = defaultValue;
        }
        spinner.getEditor().setText(converter.toString(temp));
        spinner.getValueFactory().setValue(temp);
        return temp;
    }

    public static Parent loadFXML(String fxmlPath) {
        Parent root = null;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(SceneTool.class.getResource(fxmlPath));
            root = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return root;
    }

    public static void showErrorAlert(String title, String header, String content) {
        showAlert(Alert.AlertType.ERROR, title, header, content);
    }

    public static void showWarnAlert(String title, String header, String content) {
        showAlert(Alert.AlertType.WARNING, title, header, content);
    }

    public static void showInfoAlert(String title, String header, String content) {
        showAlert(Alert.AlertType.INFORMATION, title, header, content);
    }

    public static boolean getWarnAlert(String title, String header, String content) {
        return getAlert(Alert.AlertType.WARNING, title, header, content);
    }

    public static boolean getInfoAlert(String title, String header, String content) {
        return getAlert(Alert.AlertType.INFORMATION, title, header, content);
    }

    public static boolean getConfirmAlert(String title, String header, String content) {
        return getAlert(Alert.AlertType.CONFIRMATION, title, header, content);
    }

    private static void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        if (title != null) {
            alert.setTitle(title);
        }
        alert.setHeaderText(header);
        alert.setContentText(content);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(ICON_IMAGE);
        alert.show();
    }

    public static boolean getAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        if (title != null) {
            alert.setTitle(title);
        }
        alert.setHeaderText(header);
        alert.setContentText(content);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(ICON_IMAGE);
        stage.setOnCloseRequest(Event::consume);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    public static Optional<ButtonType> getOptionConfirmAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, content, ButtonType.YES, ButtonType.NO);
        if (title != null) {
            alert.setTitle(title);
        }
        alert.setHeaderText(header);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(ICON_IMAGE);
        stage.setOnCloseRequest(event -> stage.close());
        return alert.showAndWait();
    }

    public static void showHyperLinkAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(header);
        if (title != null) {
            alert.setTitle(title);
        }
        DialogPane dialogPane = alert.getDialogPane();

        Hyperlink reportDirLink = new Hyperlink();
        reportDirLink.setText(content);
        reportDirLink.setBorder(Border.EMPTY);
        reportDirLink.setOnAction(event -> {
            try {
                Runtime.getRuntime().exec("explorer.exe /ls, \"" + content + "\"");
                alert.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        dialogPane.setContent(reportDirLink);
        Stage stage = (Stage) dialogPane.getScene().getWindow();
        stage.getIcons().add(ICON_IMAGE);
        alert.showAndWait();
    }
}
