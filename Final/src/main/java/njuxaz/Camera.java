package njuxaz;

import javafx.scene.Node;

import java.util.ArrayList;
import java.util.HashMap;

public class Camera {

    private HashMap<GameObject,Showable> creatureCreatureViewMap=new HashMap<GameObject, Showable>() ;



    public void add(GameObject obj){
        CreatureView view=null;
        if(obj instanceof Calabash)
            view = new CreatureView(obj, "/" + new Integer(((Calabash) obj).rank).toString() + ".png", Controller.SIZE);
        else if(obj instanceof Grandpa)
            view=new CreatureView(obj,"/grandpa.jpg",Controller.SIZE);
        else if(obj instanceof Snake)
            view=new CreatureView(obj,"/snake.jpg",Controller.SIZE);
        else if(obj instanceof Scorpion)
            view=new CreatureView(obj,"/scorpion.jpg",Controller.SIZE);
        else if(obj instanceof LittleMonster)
            view=new CreatureView(obj,"/littlemonster.jpg",Controller.SIZE);

        creatureCreatureViewMap.put(obj,view);
    }

    public void updateView() {
        for (HashMap.Entry<GameObject, Showable> entry : creatureCreatureViewMap.entrySet()) {
            GameObject obj=entry.getKey();
            Showable view=entry.getValue();
            if (obj.isAttacked()) {
                view.showEffect();
                view.setHealthBarLength((double) obj.getHp() / 100);
                obj.setAttacked(false);
            }
            if (obj.hasMoved()) {
                view.setX(obj.getX() * view.getSize());
                view.setY(obj.getY() * view.getSize());
                obj.setMoved(false);
            }
            if (obj.getHp() == 0)
                view.vanish();
        }
    }

    public ArrayList<Node> getView(){
        ArrayList<Node> res=new ArrayList<>();
        for(Showable view:creatureCreatureViewMap.values()){
            res.addAll(view.getComponents());
        }
        return res;
    }

}
