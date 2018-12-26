package njuxaz;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Background {
    private Image image;
    private int rows;
    private int cols;
    private int size;
    private int element1=13;
    private int element2=15;
    public Background(String url,int rows,int cols,int size){
        image=new Image(url);
        this.rows=rows;
        this.cols=cols;
        this.size=size;
    }

    public void draw(GraphicsContext gc){
        for(int x=0;x<cols;x++)
            for(int y=0;y<rows;y++){
                if((x+y)%2==0)
                    gc.drawImage(image,1*32,0*32,32,32,x*size,y*size,size,size);
                else
                    gc.drawImage(image,3*32,0*32,32,32,x*size,y*size,size,size);
            }
    }
}
