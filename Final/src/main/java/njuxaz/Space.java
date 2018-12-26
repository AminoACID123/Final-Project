package njuxaz;

import java.util.*;

class Position{
    private boolean empty;
    public Position(int x,int y){
        empty=true;
    }
    public boolean isEmpty(){
        return empty;
    }
    public void creatureEnter(){
        empty=false;
    }
    public void creatureLeave(){
        empty=true;
    }
}




public  class Space {
    private int rows,columns;
    private Position map[][];
    private Vector<Creature> creatures;

    Space(int r,int c){
        rows=r;
        columns=c;
        map=new Position[columns][rows];
        for(int i=0;i<columns;i++)
            for(int j=0;j<rows;j++)
                map[i][j]=new Position(i,j);
    }


    public void creaturesLogin(Vector <Creature> m){
        this.creatures=m;
        for (Creature C:creatures)
            map[C.x][C.y].creatureEnter();
    }

    public Vector<Creature> getCreatures(){return creatures;}

    public Position positionAt(int x,int y){
        return map[x][y];
    }

}

