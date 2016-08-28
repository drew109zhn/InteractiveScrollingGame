import java.awt.event.KeyEvent;
import java.util.Random;

public class DrewZhongGame
{
  // set to false to use your code, true will run in DEMO mode (using MattGame.class
  private static final boolean DEMO = false;           
  public MattGame dg;
  
  // Game window should be wider than tall:   H_DIM < W_DIM   
  private static  int H_DIM = 10;   // # of cells vertically by default: height of game
  private static  int W_DIM = 15;  // # of cells horizontally by default: width of game
  private static  int U_ROW = 0;
  
  private Grid grid;
  private int userRow;
  private int msElapsed;
  private int timesGet;
  private int timesAvoid;
  private static Random randomGen = new Random();
  
  private int pauseTime = 150;
  private boolean pause=false;
  private int divides;
  
  public DrewZhongGame()
  {
    init(H_DIM, W_DIM, U_ROW);
  }
  
  public DrewZhongGame(int hdim, int wdim, int uRow)
  {
    this.H_DIM=hdim;
    this.W_DIM=wdim;
    this.U_ROW=uRow;
    init(H_DIM, W_DIM, U_ROW);
    // needs to be completed!

  }
  
  private void init(int hdim, int wdim, int uRow) {  
    grid = new Grid(hdim, wdim);   
    
    //look at the various Grid constructors to see what else you can do!
    //Example:
    //grid = new Grid(hdim, wdim, Color.MAGENTA);   
    ///////////////////////////////////////////////
    userRow = uRow;
    msElapsed = 0;
    timesGet = 0;
    timesAvoid = 0;
    updateTitle();
    grid.setImage(new Location(userRow, 0), "user.gif");
  }
  
  public void play()
  {
    System.out.println("Welcome to Crazy 2 minutes");//Explaination of the game
    System.out.println("Try to play the game for 2 minutes, collide with A will lose a life, collide with G will gain a life");
    System.out.println("When your life is zero, you lose, when the time is up, you win");
    System.out.println("Use , and . to adjust difficulty level and p to pause. Enjoy!!");
    while (!isGameOver())
    {
      grid.pause(pauseTime);
      handleKeyPress();
      if ((msElapsed-divides) % (3 * pauseTime) == 0)//Every number divides zero
      {
        scrollLeft();
        populateRightEdge();
      }
      updateTitle();
      if (pause==false)
      {msElapsed += pauseTime;}
    }
  }
  
  public void handleKeyPress()
  {
    int key = grid.checkLastKeyPressed();
    //use Java constant names for key presses
    //http://docs.oracle.com/javase/7/docs/api/constant-values.html#java.awt.event.KeyEvent.VK_DOWN
    if (key == KeyEvent.VK_UP && userRow!=0 && pause==false)
    {
      Location old=new Location(userRow,0);
      userRow-=1;
      handleCollision (new Location(userRow,0));
      Location update=new Location(userRow,0);
      grid.setImage(old,null);
      grid.setImage(update,"user.gif");
    }
    else if (key == KeyEvent.VK_DOWN && userRow!=H_DIM-1 && pause==false)
    {
      Location old=new Location(userRow,0);
      userRow+=1;
      handleCollision (new Location(userRow,0));
      Location update=new Location(userRow,0);
      grid.setImage(old,null);
      grid.setImage(update,"user.gif");
    }

    else if (key == KeyEvent.VK_Q)
      System.exit(0);
    
    // use the 'T' key to help you with implementing speed up/slow down/pause
    else if (key == KeyEvent.VK_T) 
    {
      boolean interval = (msElapsed % (3 * pauseTime) == 0);
      System.out.println("pauseTime " + pauseTime + " msElapsed reset " + msElapsed 
                        + " interval " + interval);
    }
    else if (pauseTime<200 && key == KeyEvent.VK_COMMA){
      divides=msElapsed;
      pauseTime+=10;
    }
    else if (pauseTime>30 && key == KeyEvent.VK_PERIOD){
      divides=msElapsed;
      pauseTime-=10;
    }
    else if (key == KeyEvent.VK_P)
    {
      if (pause==false)
        pause=true;
      else
        pause=false;
    }
    }      
  
  public void populateRightEdge()
  {
    if (pause==false)
    {
    int a=(grid.getNumCols())/2;
    int count=0;
    for (int b=0;b<grid.getNumRows();b++)
    {
    int k=randomGen.nextInt(15);
    if (k==1||k==3||k==10||k==15 && count!=a)//ensure there are atmost half of the row numbers of avoids
    {
      grid.setImage(new Location(b,grid.getNumCols()-1),"avoid.gif");
      count+=1;
    }
    else if (k==10 || k==7)
    {
      grid.setImage(new Location(b,grid.getNumCols()-1),"get.gif");
    }
    else
    {
      grid.setImage(new Location(b,grid.getNumCols()-1),null);
    }
    }
    }
   
  }
  public void scrollLeft()
  {
    if (pause==false)
    {
    int i=0;
    while (i<grid.getNumCols()-1)
      {  
       int j=0;
        while (j<grid.getNumRows())
        {
          if (grid.getImage(new Location(j,i))!="user.gif")
          {grid.setImage(new Location(j,i),grid.getImage(new Location(j,i+1)));
          }
          else
          {handleCollision (new Location(j,i+1));}
          j++;
         }
        i++;
    }
    for (int k=0;k<grid.getNumRows();k++)
    {
      grid.setImage(new Location(k,i),null);
    }
    }
  }
  
  public void handleCollision(Location loc)
  {
    if (grid.getImage(loc)=="get.gif"){
      timesGet+=1;
    }
    else if (grid.getImage(loc)=="avoid.gif"){
      timesAvoid+=1;
    }          
  }
  
  public int getScore()
  {
      return msElapsed/1000;
  }
  public String difficulty()
  {
    if (pauseTime==30)
      return "Impossible";
    else if (pauseTime>30 && pauseTime<60)
      return "Hard";
    else if (pauseTime>=60 && pauseTime<90)
      return "Medium";
    else
      return "Easy";
  }
  
  public void updateTitle()
  {
    int Chance=4-timesAvoid+timesGet;
    
    grid.setTitle("Time: "+getScore()+" Target: 120 "+"life Remaining: "+Chance+" Difficulty level: "+difficulty());
  }
  
  public boolean isGameOver()
  {
   if (4-timesAvoid+timesGet==0)
   {
     System.out.println("Sorry you lose, you might need a pair of glassess");
     return true;
   }
   else if (msElapsed/1000==120)
   {
     System.out.println("You win!");
     return true;
   }
    return false;
  }
  
  public static void test()
  {
    if (DEMO) {       // reference game: 
                      //   - start by playing the demo version to get a handle on what 
                      //   - go back to the demo anytime you don't know what your next step is
                      //     or details about it are not concrete
                      //         figure out according to the game play 
                      //         (the sequence of display and action) how the functionality
                      //         you are implementing next is supposed to operate
                      // It's critical to have a plan for each piece of code: follow, understand
                      // and study the assignment description details; and explore the basic game. 
                      // You should always know what you are doing (your current, small goal) before
                      // implementing that piece or talk to us. 

      System.out.println("Running the demo: DEMO=" + DEMO);
      //default constructor   (4 by 10)
      MattGame game = new MattGame();
      // try the two parameter constructor with MattGame
      // MattGame game = new MattGame(10, 20, 0);
      game.play();
    
    } else {
      System.out.println("Running student game: DEMO=" + DEMO);
      //This code runs when DEMO is false!
      
      //test 1: with parameterless constructor
      DrewZhongGame game = new DrewZhongGame(30,40,0);
      
      //test 2: with constructor specifying grid size, you need to implement this!
      //it should work as long as height < width
      //Game game = new Game(10, 20, 4);
      
      game.play();
    }
  }
  
  public static void main(String[] args)
  {
    test();
  }
}