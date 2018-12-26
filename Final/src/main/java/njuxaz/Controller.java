package njuxaz;


import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;

import javafx.scene.Group;

import javafx.scene.Node;
import javafx.scene.Scene;

import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.*;



public class Controller {
    static final int COLS = 20;
    static final int ROWS = 10;
    static final int SIZE=100;





    @FXML
    protected void NewGameAction(ActionEvent event) throws Exception {

        Group root = new Group();
        Scene scene = new Scene(root, COLS*SIZE, ROWS*SIZE-10);
        Battle battle=new Battle(COLS,ROWS,SIZE);
        battle.init();
		battle.initViews();
        ImageView background=new ImageView();
		
		
        background.setImage(new Image("/Background.jpg"));

        background.setX(0);background.setY(0);
        background.setFitHeight(ROWS*SIZE);
        background.setFitWidth(COLS*SIZE);
        root.getChildren().add(background);
        for(Node n:battle.getCamera().getView()) {
            root.getChildren().add(n);
        }
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode() == KeyCode.SPACE){
                    battle.start();
               }
            }
        });
        Stage battleStage=new Stage();
        battleStage.setResizable(false);
        battleStage.setTitle("葫芦娃大战妖精");
        battleStage.getIcons().add(new Image("/2.png"));
        battleStage.setScene(scene);
        Main.currentStage.hide();

        battleStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                battle.end();
                if (battle.isFinished()){
                    FileChooser fileChooser = new FileChooser();
                    File file = fileChooser.showSaveDialog(battleStage);
                    try {
                        if (file != null) {
                            ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(file));
                            os.writeObject(battle.getHistory());
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Error");
                        alert.setHeaderText("保存失败");
                        alert.setContentText("保存失败");
                        alert.show();
                    }
                }
                Main.currentStage.show();
            }
        });
        battleStage.show();
    }


    @FXML
    protected void LoadGameAction(ActionEvent event){
        FileChooser fileChooser=new FileChooser();
        File file=fileChooser.showOpenDialog(Main.currentStage);
        try {
            if (file != null) {
                ObjectInputStream os = new ObjectInputStream(new FileInputStream(file));
                Log gameLog;
                gameLog=(Log)os.readObject();
                Battle battle=new Battle(gameLog);
                battle.init();
				battle.initViews();
                os.close();

                Stage battleStage=new Stage();

                Group root = new Group();
                Scene scene = new Scene(root, COLS*SIZE, ROWS*SIZE-10);
                ImageView background=new ImageView();
                background.setImage(new Image("/Background.jpg"));
                background.setX(0);background.setY(0);
                background.setFitHeight(ROWS*SIZE);
                background.setFitWidth(COLS*SIZE);
                root.getChildren().add(background);
                for(Node n:battle.getCamera().getView()) {
                    root.getChildren().add(n);
                }
                battleStage.setResizable(false);
                battleStage.setTitle("葫芦娃大战妖精");
                battleStage.getIcons().add(new Image("/2.png"));
                battleStage.setScene(scene);


                battleStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                    @Override
                    public void handle(WindowEvent event) {
                        battle.end();
                        Main.currentStage.show();
                    }
                });
                battle.start();
                battleStage.show();
                Main.currentStage.hide();
            }
        }catch (Exception e){
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("打开失败");
            alert.setContentText("打开失败");
            alert.show();
        }
    }

    @FXML
    protected void GameExitAction(ActionEvent event){
        Main.currentStage.hide();
    }
}