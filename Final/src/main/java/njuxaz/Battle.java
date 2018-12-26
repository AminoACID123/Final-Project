package njuxaz;
import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.*;

import static java.lang.Thread.sleep;

class CreatureStatus implements Serializable{
    int x;
    int y;
    boolean attacked;
    boolean alive;
    boolean moved;
    int hp;
    CreatureStatus(Creature c){
        this.x=c.getX();
        this.y=c.getY();
        this.attacked=c.isAttacked();
        this.alive=c.isAlive();
        this.moved=c.hasMoved();
        this.hp=c.getHp();
    }
}

class Log implements Serializable{
    Vector<Creature> initialCreatures=new Vector<>();

    Vector<Vector<CreatureStatus>> info=new Vector<>();

}


public class Battle extends Thread implements Serializable {
    enum MODE{REPLAY,PLAY};
    static final int num=9;
    MODE mode=MODE.PLAY;
    Space battlefield;
    int numberOfRound=1;
    int objectSize;
    private boolean finished=false;
    private Camp winner;
    private Map<GameObject,Showable> creatureCreatureViewMap;
    static Camp currentSide;
    static Object[] round;
    private Camera camera=new Camera();
    public boolean isFinished(){return finished;}
    private boolean checkFinish(){
        int goodCount=0;
        int badCount=0;
        for(Creature c:creatures) {
            if (c.getSide() == Camp.GOOD && !c.isDead())
                goodCount++;
            else if(c.getSide() == Camp.BAD && !c.isDead())
                badCount++;
        }
        if(goodCount==0) {
            winner=Camp.BAD;
            finished=true;
            return true;
        }
        else if(badCount==0){
            winner=Camp.GOOD;
            finished=true;
            return true;
        }
        return false;
    }

    private Log history=new Log();


    public Camera getCamera(){return camera;}

    private Vector<Creature> creatures;
	
	public Vector<Creature> getCreatures(){return creatures;}

    public Log getHistory(){return history;}

    void changeRound(){
        if(currentSide==Camp.GOOD)
            currentSide=Camp.BAD;
        else
            currentSide=Camp.GOOD;
    }
    public void setMode(MODE m){mode=m;}
    public void setHistory(Log log){history=log;}

    public Battle(Log history){
        this.history=history;
        mode=MODE.REPLAY;
    }
    public Battle(int cols,int rows,int size){

        round=new Object[2];
        round[0]=new Object();
        round[1]=new Object();
        currentSide=Camp.GOOD;
        objectSize=size;
        battlefield=new Space(rows,cols);
    }

    public boolean getFinishedProperty(){return finished;}
    public Camp getWinner(){return winner;}
    public void init(){
        if(mode==MODE.PLAY) {
            creatures = new Vector<>();
            SnakeFormation snakeFormation = new SnakeFormation(7);
            WingFormation wingFormation = new WingFormation(num);


            //初始化葫芦娃阵营
            Grandpa grandpa = new Grandpa(battlefield, 0, 4);
            creatures.add(grandpa);
            Grandpa grandpa2 = new Grandpa(battlefield, 0, 4);
            history.initialCreatures.add(grandpa2);
  //          camera.add(grandpa);
            for (int i = 0; i < 7; i++) {
                Calabash calabash = new Calabash(battlefield, CalabashRank.values()[i], snakeFormation.pattern.get(i).x + 2, snakeFormation.pattern.get(i).y + 2);
                creatures.add(calabash);
                Calabash c2 = new Calabash(battlefield, CalabashRank.values()[i], snakeFormation.pattern.get(i).x + 2, snakeFormation.pattern.get(i).y + 2);
                history.initialCreatures.add(c2);
 //               camera.add(calabash);
            }

            //初始化蛇精阵营
            Snake snake=new Snake(battlefield,18,6);
            creatures.add(snake);
            Snake snake2=new Snake(battlefield,18,6);
			history.initialCreatures.add(snake2);
//            camera.add(snake2);
            Scorpion scorpion = new Scorpion(battlefield, wingFormation.pattern.get(0).x + 12, wingFormation.pattern.get(0).y);
            creatures.add(scorpion);
            Scorpion s2 = new Scorpion(battlefield, wingFormation.pattern.get(0).x + 12, wingFormation.pattern.get(0).y);
           history.initialCreatures.add(s2);
//            camera.add(scorpion);
            for (int i = 1; i < num; i++) {
                LittleMonster monster = new LittleMonster(battlefield, wingFormation.pattern.get(i).x + 12, wingFormation.pattern.get(i).y);
                creatures.add(monster);
                LittleMonster m2 = new LittleMonster(battlefield, wingFormation.pattern.get(i).x + 12, wingFormation.pattern.get(i).y);
                history.initialCreatures.add(m2);
//              camera.add(monster);
            }
            battlefield.creaturesLogin(creatures);
        }
        else{
            creatures=history.initialCreatures;
 //           for(Creature c:creatures)
 //               camera.add(c);
        }
    }

	public void initViews(){
		for(Creature c:creatures)
             camera.add(c);
	}

    private void replay() {

        for(Vector<CreatureStatus> round:history.info){
            try {
                sleep(1000);
            }catch (InterruptedException e){}
            for(int i=0;i<round.size();i++){
                creatures.get(i).setStatus(round.get(i));
            }
            camera.updateView();
        }

    }

    public void run(){
		
        if(mode == MODE.REPLAY){
            replay();
            return;
        }

        for (Creature c:creatures)
            c.start();
        try {
            while (true) {
                sleep(1000);
                history.info.add(new Vector<CreatureStatus>());
                for(int i=0;i<creatures.size();i++)
                    history.info.get(history.info.size()-1).add(new CreatureStatus(creatures.get(i)));
                camera.updateView();
                if(checkFinish()) {
                    end();
                    return;
                }

                changeRound();
                if(currentSide==Camp.GOOD){
                    synchronized (round[Camp.GOOD.ordinal()]){
                        round[Camp.GOOD.ordinal()].notifyAll();
                    }
                }
                else{
                    synchronized (round[Camp.BAD.ordinal()]){
                        round[Camp.BAD.ordinal()].notifyAll();
                    }
                }
            }
        }
        catch (Exception e){}
    }
    public void end(){
        if(mode==MODE.PLAY)
            for (Creature c:creatures)
                c.interrupt();
        this.interrupt();

    }

}

