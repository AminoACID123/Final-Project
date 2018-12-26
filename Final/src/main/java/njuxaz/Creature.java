package njuxaz;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.io.Serializable;
import java.util.Vector;
import java.util.concurrent.*;

enum Camp{GOOD,BAD};

interface GameObject{
    int getX();
    int getY();
    int getHp();
    boolean isDead();
    boolean isAttacked();
    boolean hasMoved();
    void setAttacked(boolean f);
    Camp getSide();
    void setMoved(boolean f);

}


public class Creature extends Thread implements GameObject, Serializable {
    Showable view;
    protected int atk;
    protected int defense;
    protected int speed=1;
    protected int hp=100;
    protected int x;
    protected int y;
    protected int targetDistance;
    protected boolean attacked;
    protected boolean canAttack=false;
    protected Creature target=null;
    protected boolean alive=true;
    protected boolean moved=false;
    transient protected Space space;
    protected Camp side;

    public Creature(){}
    public Creature(Creature c){
        this.x=c.x;
        this.y=c.y;
        this.attacked=c.attacked;
        this.alive=c.alive;
        this.moved=c.moved;
        this.hp=c.hp;
    }
    public int getX(){return x;}
    public int getY(){return y;}
    public int getHp(){return hp;}
    public boolean hasMoved(){return moved;}
    public boolean isAttacked(){return attacked;}
    public void setAttacked(boolean f){attacked=f;}
    public Camp getSide(){return side;}
    public boolean isDead(){return !alive;}
    public void setMoved(boolean f){moved=false;}
    public void setStatus(CreatureStatus c){
        this.x=c.x;
        this.y=c.y;
        this.attacked=c.attacked;
        this.alive=c.alive;
        this.moved=c.moved;
        this.hp=c.hp;
    }

    protected void move() {
        if(target == null || target.isDead())return;
        int xStep = 0;
        int yStep = 0;
        int distance = Math.abs(target.getX() - x) + Math.abs(target.getY() - y);
        if (distance == 1) {
            canAttack = true;
            return;
        }
        xStep=target.getX()-x;
        yStep=target.getY()-y;
        if(Math.abs(xStep)>=speed)
            xStep=speed*Math.abs(xStep)/xStep;
        if(Math.abs(yStep)>=speed)
            yStep=speed*Math.abs(yStep)/yStep;
        if(xStep*yStep!=0)
            if(Math.random()>0.5)
                yStep=0;
            else
                xStep=0;
        Position dst = space.positionAt(x + xStep, y + yStep);
        synchronized (dst) {
            if (dst.isEmpty()) {
                space.positionAt(x, y).creatureLeave();
                dst.creatureEnter();
                x += xStep;
                y += yStep;
                moved=true;
            }
        }
    }
    protected void pickTarget(){
        Vector<Creature> creatures=space.getCreatures();
        int minDistance=1000;
        for(Creature enemy:creatures){
            if(enemy.getSide()==side)
                continue;;
            int distance=Math.abs(enemy.getX()-x)+Math.abs(enemy.getY()-y);
            if(!enemy.isDead() && (distance<minDistance || (distance==minDistance&&enemy.getY()!=y))){
                minDistance=distance;
                target=enemy;
            }
        }
        targetDistance=minDistance;
    }
    protected void attack(){
        if(targetDistance==1)
            synchronized (target) {
                if(!target.isDead()) {
                    target.beAttacked(this);
                }
            }
    }
    public void beAttacked(Creature c){
        attacked=true;
        hp-= (double)c.atk*(100-defense)/100;
        if(hp<=0){
            hp=0;
            alive=false;
        }
    }



    public void bindView(CreatureView view){this.view=view;}
    @Override
    public void run(){
        try {
            while (true) {
                if(!alive) {
                    space.positionAt(x,y).creatureLeave();
                    return;
                }
                if (Battle.currentSide == side) {
                    pickTarget();
                    move();
                    attack();
                }
                synchronized (Battle.round[side.ordinal()]) {
                    Battle.round[side.ordinal()].wait();
                }
            }
        }catch (Exception e){}
    }

}




class LittleMonster extends Creature {
    public LittleMonster(Space space,int x,int y){
        this.x=x;
        this.y=y;
        side=Camp.BAD;
        this.space=space;
        atk=15;
        defense=15;
        speed = 2;
    }
}

class Scorpion extends Creature {
    public Scorpion(Space space, int x,int y){
        this.x=x;
        this.y=y;
        this.space=space;
        atk=40;
        defense=25;
        side=Camp.BAD;
    }
}

enum CalabashRank{
    FIRST(1,"老大",'赤',30,40),
    SECOND(2,"老二",'橙',50,30),
    THIRD(3,"老三",'黄',20,50),
    FORTH(4,"老四",'绿',30,30),
    FIFTH(5,"老五",'青',20,30),
    SIXTH(6,"老六",'蓝',40,30),
    SEVENTH(7,"老七",'紫',30,30);
    int id;
    String name;
    char color;
    int attack;
    int defence;
    CalabashRank(int a,String b , char c,int atk,int def){
        id=a;
        name=b;
        color=c;
        attack=atk;
        defence=def;
    }
}

class Calabash extends Creature {
    int rank;
    char color;
    Calabash(Space space, CalabashRank r,int x,int y){
        rank=r.id;
        color=r.color;
        atk=r.attack;
        defense=r.defence;
        this.x=x;
        this.y=y;
        side=Camp.GOOD;
        this.space=space;
    }
}

class Grandpa extends Creature {
   Grandpa(Space space, int x,int y){
       this.x=x;
       this.y=y;
       this.space=space;
       atk=10;
       defense=5;
       hp=100;
       side=Camp.GOOD;
    }
    protected void move(){}
}
class Snake extends Creature{
    Snake(Space space, int x,int y){
        this.x=x;
        this.y=y;
        this.space=space;
        atk=20;
        defense=15;
        hp=10;
        side=Camp.BAD;
    }
    protected void move(){}
    protected void attack(){
        synchronized (target) {
            if(!target.isDead()) {
                target.beAttacked(this);
            }
        }
    }
    protected void pickTarget(){
        Vector<Creature> creatures=space.getCreatures();
        int i=(int)(Math.random()*creatures.size());
        while(creatures.get(i).getSide()!=Camp.GOOD || creatures.get(i).isDead())
            i= (int)(Math.random()*creatures.size());
        target=creatures.get(i);
    }
}
