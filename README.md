# FINAL PROJECT：葫芦娃大战妖精
---
## （1）游戏说明
启动游戏，显示初始界面。  
点击“新游戏”开始游戏。按空格键游戏开始，双方阵营开始行动。一局游戏结束后关闭窗口会弹出对话框，用户可保存回放。（游戏为结束情况下关闭窗口不会保存回放）。
点击“加载游戏”，从文件资源管理器中选择回放记录文件，确定后即可开始回放。  
点击“退出游戏”，退出。  
## （2）设计思路
主要类为Creature、CreatureView、Battle、Space、Camera  
运行流程：Battle类中初始化各种游戏元素，每个Creature为一个线程，游戏开始执行start方法，每回合结束Camera会更新Creature对应的CreatureView用于显示

（a）生物相关：  
![avatar](/images/1.png)
Creature为所有生物的基类，它同时也继承Thread。其中包含：  
变量：生物体的各项属性（包括位置信息），生物体所在Space  
方法：move(),pickTarget(),attack(),对应于生物体的移动、选择目标、攻击，run（）方法中调用这三个方法，实现生物体行为的控制。Grandpa、Snake中重写了这三个方法，通过动态绑定机制使得老爷爷蛇精的战斗行为区别于一般生物体（一般生物体可以移动，攻击与之相邻的敌人，蛇精不会移动但可以全局随机攻击，爷爷不会移动也只能攻击相邻的敌人，弱小可怜又无助）  
Creature实现了GameObject接口，GameObject中声明了若干获取游戏对象关键信息（位置、血量、是否受到攻击，GUI显示依赖这些属性）的方法。


（b）战场空间  
Space类中存放了一个Position类型的二维数组，Position是最小空间单位，它用于记录一个位置上是否有生物体站立。

（c）生物视图CreatureView  
用于GUI显示，其中保存了一个生物体的图片、血条（矩形）、受到攻击的特效gif，CreatureView实现了Showable接口中申明的用于修改这些GUI组件的方法。  

（d）Camera类  
根据GameObject类型对象的信息修改对应的Showable类型对象，这里体现了GameObject和Showable接口的作用，Camera不用知道Creature及CreatureView中的具体方法及实现，只要通过接口中声明的方法就可完成相应的更新。  

（e）Battle类  
Battle负责战斗逻辑的控制，协调Creature、Camera等各元素，使得战斗正常进行。战斗流程为：首先所有生物体执行start()方法，双方阵营交替行动，一方行动完成后，调用Camera的updateView方法根据生物体状态更新GUI组件。当有一方阵营存活数量为0时战斗结束。  
## （3）几个主要问题的解决
（a）线程同步问题  
**不同阵营间行动协调：** Battle类中定义了两个Object类型的对象（记为obj1，obj2），同时也记录当前行动方阵营。葫芦娃阵营的生物在执行完此回合的行动后，通过：  
```
synchronized (Battle.obj1) {
	Battle.obj1.wait();
}
```  
阻塞，（妖精阵营在obj2上阻塞）  
Battle类的run方法中，每隔一段时间（足够一方阵营行动完成）通过执行obj1/obj2.notifyAll()方法唤醒被阻塞的另一方阵营的所有生物，使得战斗继续。  
**生物体之间行动协调：**这里主要考虑避免多个生物体站上同一位置的问题。之前所述，Space类中存放了Position类的二维数组，每个生物体的move方法中，先获取其所在的Position对象position，在synchronized（position）块中判断position是否为空，是则移动成功。如此，多个生物就不会站上同一位置。  
  
（b）保存回放问题  
采用序列化保存回放。每一方行动结束后，所有生物的关键信息会保存到一个Log类型的变量history中，战斗结束，将history序列化保存至文件。下次开始回放时，将history反序列化，每回合通过history内的信息更新各生物体状态，再调用Camera的updateView方法实现回放。