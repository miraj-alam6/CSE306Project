package nachos.kernel.userprog;

import nachos.Debug;
import nachos.kernel.Nachos;
import nachos.kernel.userprog.test.ProgTest;
import nachos.kernel.userprog.test.StallingsTest;

//This class has all the functions that will let us do the stallings test.

public class StallingsHelper {
    int count = 1;
    public int programCount = 0; 
    public int maxProgramCount = 8;
    int makeANewProgramCount = 0;
    int makeANewProgramCountMax = 10000;
    //randomValue is added to offset to get the random # of ticks
    //offset is calculated first however by counting how many "flips"
    //of the "unfair coin" are needed
    //This is called on every interrupt
    public void masterHelper(){
	//The bulk of this function will be creating a program,
	 if(programCount >= maxProgramCount){
	     return;
	 }
	
	 makeANewProgramCount += 1000 * Math.random();
	 if(makeANewProgramCount > makeANewProgramCountMax ){
	     makeANewProgramCount = 0;
	 }
	 else{
	     return;
	 }
	 int randomValue = ((int)(Math.random() * 100) + 1);
	 int offset = 0;
	 //This is basically an unfair coin where  heads has 10 percent chance
	 //of happening, and tails has 90 % chance of happening
	 //Which is why I keep checking if if it is less than 90, because 90 %
	 //chance
	 int failedFlips = 0;
	 int coinFlip = ((int)(Math.random() * 100) + 1);
	 while(coinFlip < 90){
	     failedFlips++;
	     coinFlip = ((int)(Math.random() * 100) + 1);
	 }
	 offset = 100 * failedFlips;
	 
	 Nachos.randomTicks = offset + randomValue;
	 programCount++;
	 new ProgTest("test/hw3testprog4", count++);
	// Debug.println('+', "His palms are sweat" + randomValue + "," + offset +"," + Nachos.randomTicks);
	 //Debug.println('+',"Time is " + Nachos.totalTime);
    }
}
