package njuxaz;
import static org.junit.Assert.*;
import org.junit.Test;

public class MyTest {
    @Test
    public void battleTest(){

        Battle battle=new Battle(Controller.COLS,Controller.ROWS,Controller.SIZE);
        battle.init();
		System.out.println("Test begin");
		int i=1;
		for(Creature c:battle.getCreatures()){

			assertTrue(c.getX()>=0 && c.getY()>=0 && c.getX()<Controller.COLS && c.getY()<Controller.ROWS);
		
			System.out.println("Creature"+i+" Checked!");
			i++;
		}
    }	
}

