import com.jfoenix.controls.*;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;

import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.*;
import java.util.Optional;


public class Main extends Application {

    public static final String WIN_TITLE = "MPV Sub Conf";
    public static final String TEXT_DROP_FONT_HELP = "Drop Subtitle Font Here !\n(or default will be Used.)";
    public static final String ICON_DRAG_AND_DROP_UNICODE = "\uf0c5";
    public static final String ICON_FONT_UNICONDE = "\uf035";
    public static final String ICON_BORDER_UNICODE = "\uf2d0";
    public static final String ICON_POS_UNICODE = "\uf13d";
    public static final String ICON_MOVIE_UNICODE = "\uf008";
    public static final String ICON_SUBTITLE_UNICODE = "\uf0f6";

    public static final int TEXT_DROP_RATIO = 430;
    public static final int TEXT_MENU_RATIO = 480;
    public static final int DROP_HEIGHT_RATIO = 4;
    public static final int FONT_HEIGHT_RATIO = 12;
    public static final int DROP_TITLE_HEIGHT_RATIO = 8;
    public static final int ICON_MENU_HEIGHT_RATIO = 25;

    public static final int SPACING = 25;

    private String fontPath = "";
    private String fontFileName = "";

    private GridPane gridMenuLayout;
    private GridPane gridSubMenuLayout;
    private GridPane gridPlayLayout;


    public static final String FLAG_SUB_FILE = "--sub-file=";
    public static final String FLAG_SUB_FONT_FILE = "--sub-font=";
    public static final String FLAG_SUB_FONT_SIZE = "--sub-font-size=";
    public static final String FLAG_SUB_FONT_COLOR = "--sub-color=";
    public static final String FLAG_SUB_FONT_BORDER_COLOR = "--sub-border-color=";
    public static final String FLAG_SUB_FONT_BORDER_SIZE = "--sub-border-size=";
    public static final String FLAG_SUB_ALIGN = "--sub-align-y=";


    public static final String CMD_MV_CONF_FILE = "mv ./mpv.conf /etc/mpv/mpv.conf";


    private String resultForFile = "";
    private String result = "";

    private JFXTextField fontSizeField;
    private JFXColorPicker fontColorPicker;
    private JFXTextField borderSizeField;
    private JFXColorPicker borderColorPicker;
    private ToggleGroup toggleGroupSubPos;

    private String moviePath = "";
    private String movieName;
    private String subtitlePath = "";
    private String subtitleName = "";

    private StackPane stackPaneMainLayout;

    @Override
    public void start(Stage stage) {


        VBox mainLayout = new VBox();
        stackPaneMainLayout = new StackPane(mainLayout);

        gridMenuLayout = getConfedGridpane();
        gridSubMenuLayout = getConfedGridpane();
        gridPlayLayout = getConfedGridpane();

        setFillBackground(mainLayout, Color.web(ColorUtils.def_bg));

        fontSizePart();
        fontColorPart();
        borderSizePart();
        borderColorPart();
        subPos();
        selectMovie();
        selectSubtitle();

        mainLayout.getChildren().addAll(dragDropPart(), gridMenuLayout, gridSubMenuLayout, saveConf(), gridPlayLayout, playMovie());
        Scene scene = new Scene(stackPaneMainLayout, ScreenUtil.getAppWidth(), ScreenUtil.getAppHeight());
        stage.setResizable(false);
        stage.setScene(scene);
        stage.setTitle(WIN_TITLE);
        stage.show();
    }


    public VBox dragDropPart() {

        VBox dropPartLayout = new VBox();

        final Animation getDarker = new Transition() {
            {
                setCycleDuration(Duration.millis(200));
                setInterpolator(Interpolator.EASE_BOTH);
            }

            @Override
            protected void interpolate(double frac) {
                Color color = new Color(0.605, 0.605, 0.605, 0 + frac);
                setFillBackground(dropPartLayout, color);
            }
        };

        final Animation getLighter = new Transition() {
            {
                setCycleDuration(Duration.millis(200));
                setInterpolator(Interpolator.EASE_OUT);
            }

            @Override
            protected void interpolate(double frac) {
                Color color = new Color(0.61, 0.61, 0.61, 1 - frac);
                setFillBackground(dropPartLayout, color);

            }
        };

        FontAwesomeLabel icon = new FontAwesomeLabel(ScreenUtil.getAppWidth() / DROP_TITLE_HEIGHT_RATIO);
        icon.setText(ICON_DRAG_AND_DROP_UNICODE);
        Text help = new Text(TEXT_DROP_FONT_HELP);
        help.setFill(Color.web(ColorUtils.def_text_color));
        help.setScaleX(ScreenUtil.getAppWidth() / TEXT_DROP_RATIO);
        help.setScaleY(ScreenUtil.getAppWidth() / TEXT_DROP_RATIO);

        dropPartLayout.setSpacing(SPACING);
        setBottomBorder(dropPartLayout);
        setFillBackground(dropPartLayout, Color.web(ColorUtils.def_bg));
        dropPartLayout.setAlignment(Pos.CENTER);
        dropPartLayout.getChildren().addAll(icon, help);
        dropPartLayout.setPrefHeight(ScreenUtil.getAppHeight() / DROP_HEIGHT_RATIO);

        dropPartLayout.setOnDragOver(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {
                final Dragboard db = event.getDragboard();

                if (db.hasFiles()) {
                    event.acceptTransferModes(TransferMode.ANY);
                } else
                    event.consume();
            }
        });

        dropPartLayout.setOnDragEntered(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                getDarker.play();
            }
        });

        dropPartLayout.setOnDragExited(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                getLighter.play();
            }
        });

        dropPartLayout.setOnDragDropped(new EventHandler<DragEvent>() {

            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();
                boolean success = false;

                if (db.hasFiles()) {
                    String fileName = null;
                    File file = db.getFiles().get(0);
                    fontPath = file.getAbsolutePath();
                    fileName = file.getName();
                    fontFileName = fileName.substring(0, fileName.indexOf("."));
                    System.out.println(fontFileName);
                    System.out.println(fontPath);
                    help.setText(fileName);
                    success = true;
                }

                event.setDropCompleted(success);
                event.consume();
            }
        });

        return dropPartLayout;
    }

    private void fontSizePart() {
        FontAwesomeLabel icon = new FontAwesomeLabel(ScreenUtil.getAppWidth() / ICON_MENU_HEIGHT_RATIO);
        icon.setText(ICON_FONT_UNICONDE);


        gridMenuLayout.add(icon, 1, 1);

        Text help = new Text("Font Size : ");
        help.setFill(Color.web(ColorUtils.def_text_color));

        gridMenuLayout.add(help, 4, 1);

        fontSizeField = new JFXTextField();
        fontSizeField.setPromptText("Default Value : 55");
        fontSizeField.setFocusTraversable(false);
        gridMenuLayout.add(fontSizeField, 6, 1);
    }


    private void fontColorPart() {
        Text help = new Text("Font Color : ");
        help.setFill(Color.web(ColorUtils.def_text_color));

        gridMenuLayout.add(help, 4, 2);

        fontColorPicker = new JFXColorPicker(Color.WHITE);
        gridMenuLayout.add(fontColorPicker, 6, 2);
    }


    private void borderSizePart() {
        FontAwesomeLabel icon = new FontAwesomeLabel(ScreenUtil.getAppWidth() / ICON_MENU_HEIGHT_RATIO);
        icon.setText(ICON_BORDER_UNICODE);


        gridMenuLayout.add(icon, 1, 3);
        Text borderHelp = new Text("Border Size : ");
        borderHelp.setFill(Color.web(ColorUtils.def_text_color));

        gridMenuLayout.add(borderHelp, 4, 3);

        borderSizeField = new JFXTextField();
        borderSizeField.setPromptText("Default Value : 3");
        borderSizeField.setFocusTraversable(false);
        gridMenuLayout.add(borderSizeField, 6, 3);
    }

    private void borderColorPart() {
        Text help = new Text("Border Color : ");
        help.setFill(Color.web(ColorUtils.def_text_color));

        gridMenuLayout.add(help, 4, 4);
        borderColorPicker = new JFXColorPicker(Color.BLACK);
        gridMenuLayout.add(borderColorPicker, 6, 4);
    }


    private void subPos() {

        FontAwesomeLabel icon = new FontAwesomeLabel(ScreenUtil.getAppWidth() / ICON_MENU_HEIGHT_RATIO);
        icon.setText(ICON_POS_UNICODE);
        gridMenuLayout.add(icon, 1, 6);

        Text subPosHelp = new Text("Subtitle Position : ");
        subPosHelp.setFill(Color.web(ColorUtils.def_text_color));

        gridMenuLayout.add(subPosHelp, 4, 6);

        toggleGroupSubPos = new ToggleGroup();
        JFXRadioButton bottom = new JFXRadioButton("Bottom");
        bottom.setSelected(true);
        bottom.setUserData("bottom");
        bottom.setPadding(new Insets(10));
        bottom.setToggleGroup(toggleGroupSubPos);

        JFXRadioButton cetner = new JFXRadioButton("Center");
        cetner.setPadding(new Insets(10));
        cetner.setUserData("center");
        cetner.setToggleGroup(toggleGroupSubPos);

        JFXRadioButton top = new JFXRadioButton("Top");
        top.setPadding(new Insets(10));
        top.setToggleGroup(toggleGroupSubPos);
        top.setUserData("top");

        gridSubMenuLayout.add(bottom, 4, 1);
        gridSubMenuLayout.add(cetner, 5, 1);
        gridSubMenuLayout.add(top, 6, 1);


    }

    private HBox saveConf() {
        HBox hbox = new HBox();
        hbox.setPadding(new Insets(10, 10, 10, 10));
        JFXButton saveButton = new JFXButton("MAKE CONFIGURE File");
        saveButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                    saveConfigure();
            }
        });
        getConfedButton(saveButton);
        hbox.setAlignment(Pos.CENTER);
        setBottomBorder(hbox);
        hbox.getChildren().add(saveButton);
        return hbox;
    }

    private void selectMovie() {
        FontAwesomeLabel icon = new FontAwesomeLabel(ScreenUtil.getAppWidth() / ICON_MENU_HEIGHT_RATIO);
        icon.setText(ICON_MOVIE_UNICODE);
        gridPlayLayout.add(icon, 1, 1);

        Text movieHelp = new Text("Select a Movie :");
        gridPlayLayout.add(movieHelp, 4, 1);


        Label movieNameLabel = new Label();
        movieNameLabel.setText("Movie Name");
        movieNameLabel.setMaxWidth(ScreenUtil.getAppWidth() / 4);
        gridPlayLayout.add(movieNameLabel, 5, 1);


        JFXButton selectMovieButton = new JFXButton("SELECT");
        selectMovieButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                selectMovieCallback();
                movieNameLabel.setText(movieName);
            }
        });
        getConfedButton(selectMovieButton);
        gridPlayLayout.add(selectMovieButton, 10, 1);
    }


    private void selectSubtitle() {
        FontAwesomeLabel icon = new FontAwesomeLabel(ScreenUtil.getAppWidth() / ICON_MENU_HEIGHT_RATIO);
        icon.setText(ICON_SUBTITLE_UNICODE);
        gridPlayLayout.add(icon, 1, 2);

        Text movieHelp = new Text("Select a Subtitle :");
        gridPlayLayout.add(movieHelp, 4, 2);


        Label subtitleNameLabel = new Label();
        subtitleNameLabel.setMaxWidth(ScreenUtil.getAppWidth() / 4);
        subtitleNameLabel.setText("Subtitle Name");
        gridPlayLayout.add(subtitleNameLabel, 5, 2);


        JFXButton selectSubtitleButton = new JFXButton("SELECT");
        selectSubtitleButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                selectSubtitleCallback();
                subtitleNameLabel.setText(subtitleName);
            }
        });
        getConfedButton(selectSubtitleButton);
        gridPlayLayout.add(selectSubtitleButton, 10, 2);
    }

    private HBox playMovie() {
        HBox hbox = new HBox();
        hbox.setPadding(new Insets(20, 10, 10, 10));
        JFXButton saveButton = new JFXButton("PLAY MOVIE");
        saveButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                playCallback();
            }
        });
        getConfedButton(saveButton);
        hbox.setAlignment(Pos.CENTER);
        setBottomBorder(hbox);
        hbox.getChildren().add(saveButton);
        return hbox;
    }


    public static void main(String[] args) {
        launch(args);
    }

    private void setFillBackground(Pane pane, Color color) {
        pane.setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));
    }

    private void getConfedButton(Button btn) {
        btn.setStyle("-fx-background-color: #444; -fx-text-fill: white;");

    }

    private void setFontFileName() {
        if (!fontFileName.equals("")) {
            resultForFile += FLAG_SUB_FONT_FILE + fontFileName + "\n";
            result += FLAG_SUB_FONT_FILE + fontFileName + " ";
        }
    }

    private void setSubPosition() {
        String subPos = "bottom";
        try {
            String toggleRes = toggleGroupSubPos.getSelectedToggle().getUserData().toString();
            if (toggleRes != null)
                subPos = toggleRes;
        } catch (NullPointerException e) {
        }

        resultForFile += FLAG_SUB_ALIGN + subPos + "\n";
        result += FLAG_SUB_ALIGN + subPos + " ";
    }

    private void setFontSize() {
        String fontSize = fontSizeField.getText();

        if (isNumeric(fontSize) && !fontSize.equals("55")) {
            resultForFile += FLAG_SUB_FONT_SIZE + fontSize + "\n";
            result += FLAG_SUB_FONT_SIZE + fontSize + " ";
        }
    }

    private void setFontColor() {
        Color fontColor = fontColorPicker.getValue();

        if (!fontColor.equals(Color.WHITE)) {
            resultForFile += FLAG_SUB_FONT_COLOR + fontColor.getRed() + "/" + fontColor.getGreen() + "/" + fontColor.getBlue() + "\n";
            result += FLAG_SUB_FONT_COLOR + fontColor.getRed() + "/" + fontColor.getGreen() + "/" + fontColor.getBlue() + " ";
        }
    }

    private void setBorderSize() {
        String borderSize = borderSizeField.getText();
        if (isNumeric(borderSize) && !borderSize.equals("3")) {
            resultForFile += FLAG_SUB_FONT_BORDER_SIZE + borderSize + "\n";
            result += FLAG_SUB_FONT_BORDER_SIZE + borderSize + " ";
        }
    }

    private void setBorderColor() {
        Color borderColor = borderColorPicker.getValue();
        if (!borderColor.equals(Color.BLACK)) {
            resultForFile += FLAG_SUB_FONT_BORDER_COLOR + borderColor.getRed() + "/" + borderColor.getGreen() + "/" + borderColor.getBlue() + "\n";
            result += FLAG_SUB_FONT_BORDER_COLOR + borderColor.getRed() + "/" + borderColor.getGreen() + "/" + borderColor.getBlue() + " ";
        }
    }

    private void setArgs() {
        result = "";
        setBorderColor();
        setBorderSize();
        setFontColor();
        setFontSize();
        setSubPosition();
        setFontFileName();
    }

    private boolean saveConfigure() {
        setArgs();
        Writer writer = null;
        try {
            File confFile = new File("/tmp/mpv.conf");
            if(!confFile.exists())
                confFile.createNewFile();

            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(confFile), "utf-8"));
            writer.write(resultForFile);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Configuration Created Successfully!");
            alert.setContentText("configuration file is availabel at /tmp/mpv.conf\n"+
                    "append this file to /etc/mpv/mpv.conf \nthen configuration works permanently.");
            alert.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("can't create or write to file");
        } finally {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            catch(NullPointerException e) {
                e.printStackTrace();
            }
        }
        return false;
    }


    private GridPane getConfedGridpane() {
        GridPane gridPlayLayout = new GridPane();
        gridPlayLayout.setHgap(10);
        gridPlayLayout.setVgap(10);
        gridPlayLayout.setPadding(new Insets(0, 10, 0, 10));
        return gridPlayLayout;
    }



    private void selectMovieCallback() {
        FileChooser fc = new FileChooser();
        File selectedFile = fc.showOpenDialog(null);
        if (selectedFile != null) {
            moviePath = selectedFile.getAbsolutePath();
            movieName = selectedFile.getName();
        }
        System.out.println(moviePath);
    }

    private void selectSubtitleCallback() {
        FileChooser fc = new FileChooser();
        File selectedFile = fc.showOpenDialog(null);
        if (selectedFile != null) {
            subtitlePath = selectedFile.getAbsolutePath();
            subtitleName = selectedFile.getName();
        }
        System.out.println(subtitlePath);
    }

    private void playCallback() {
        setArgs();
        System.out.println(result);
        String cmd;
        if (subtitlePath.equals(""))
            cmd = "mpv " + moviePath + " " + result;
        else
            cmd = "mpv " + moviePath + " " + FLAG_SUB_FILE + subtitlePath + " " + result;
        System.out.println(cmd);


        new ExecCommand(cmd);

    }

    private Optional<String> getRootPassDialog() {
        PasswordDialog passPrompet = new PasswordDialog();
        Optional<String> res = passPrompet.showAndWait();
        return res;
    }

    public boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
    }

   private JFXDialogLayout getDialogLayout(String header, String content, String buttonText) {
        JFXDialogLayout dialogLayout = new JFXDialogLayout();
        dialogLayout.setHeading(new Text(header));
        dialogLayout.setBody(new Text(content));
        JFXButton acceptButton = new JFXButton(buttonText);

       acceptButton.setStyle("-fx-background-color: #077a08; -fx-text-fill: white;");
       dialogLayout.setActions(acceptButton);
       return dialogLayout;
   }

    private void setBottomBorder(Node node) {
        node.setStyle("-fx-border-color: #ccc; -fx-border-width: 0 0 1 0;");
    }
}
