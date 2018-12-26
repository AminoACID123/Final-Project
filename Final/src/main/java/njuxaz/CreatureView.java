package njuxaz;
import javafx.animation.FadeTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.beans.property.*;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;


interface Showable{
    int getSize();
    void setX(int x);
    void setY(int y);
    void setHealthBarLength(double ratio);
    void showEffect();
    void vanish();
    ArrayList<Node> getComponents();
}



public class CreatureView implements Showable{
    private int size;
    ImageView objectImage=new ImageView();
    ImageView attackEffect=new ImageView();
    Rectangle healthBar=new Rectangle();
    CreatureView(GameObject c,String imageURL,int size){
        this.size=size;
        objectImage.setImage(new Image(imageURL));
        attackEffect.setImage(new Image("/fire.gif"));
        attackEffect.setVisible(false);
        objectImage.setX(c.getX()*size);
        objectImage.setY(c.getY()*size);
        objectImage.setFitHeight(size);
        objectImage.setFitWidth(size);


        attackEffect.setX(c.getX()*size+size);
        attackEffect.setY(c.getY()*size);
        attackEffect.setFitHeight(size);
        attackEffect.setFitWidth(size);

        healthBar.setX(c.getX()*size);
        healthBar.setY(c.getY()*size);
        healthBar.setFill(Color.LIGHTGREEN);
        healthBar.setVisible(true);
        healthBar.setWidth(size);
        healthBar.setHeight(10);
    }
    public int getSize(){return size;}
    public void setX(int x){

            /*
            objectImage.xProperty().setValue(x);
            healthBar.xProperty().setValue(x);
            */

            objectImage.setX(x);
            healthBar.setX(x);
    }
    public void setY(int y){
        objectImage.yProperty().setValue(y);
        healthBar.yProperty().setValue(y);

    }

    public void setHealthBarLength(double ratio){
        healthBar.setWidth(size*ratio);
    }
    public void vanish(){
        FadeTransition ft = new FadeTransition(Duration.millis(500), objectImage);
        ft.setFromValue(1.0);
        ft.setToValue(0.0);
        ft.setCycleCount(0);
        ft.setAutoReverse(false);
        ft.play();
        ft.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                objectImage.setVisible(false);
            }
        });
    }
    public void showEffect(){

        attackEffect.setVisible(true);
        attackEffect.setX(objectImage.getX());
        attackEffect.setY(objectImage.getY());


        FadeTransition ft1 = new FadeTransition(Duration.millis(400),attackEffect);
        ft1.setFromValue(0.0);
        ft1.setToValue(0.8);
        ft1.setCycleCount(0);
        ft1.setAutoReverse(false);
        FadeTransition ft2 = new FadeTransition(Duration.millis(400),attackEffect);
        ft2.setFromValue(0.8);
        ft2.setToValue(0.0);
        ft2.setCycleCount(0);
        ft2.setAutoReverse(false);

        SequentialTransition st=new SequentialTransition(ft1,ft2);
        st.setCycleCount(0);
        st.setAutoReverse(false);
        st.play();
        st.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                attackEffect.setVisible(false);
            }
        });
    }
    public ArrayList<Node> getComponents(){
        ArrayList<Node> res=new ArrayList<>();
        res.add(objectImage);
        res.add(attackEffect);
        res.add(healthBar);
        return res;
    }
}
