package nachos.kernel.devices;

import java.util.ArrayList;

import nachos.kernel.userprog.UserThread;
import nachos.machine.Machine;
import nachos.machine.NachosThread;

public class ConsoleManager {
    
    private ArrayList<ConsoleDriver> availConsoles;
    
    public ConsoleManager()
    {
	availConsoles = new ArrayList<ConsoleDriver>();
	for(int i = 1; i < Machine.NUM_CONSOLES; i++)
	{
	    ConsoleDriver drive = new ConsoleDriver(Machine.getConsole(i));
	    availConsoles.add(drive);
	}
    }

    public ConsoleDriver getConsole()
    {
	for(int i = 0; i < availConsoles.size(); i++)
	{
	    ConsoleDriver hold = availConsoles.get(i);
	    if(hold.getUsed() == 0)
	    {
		availConsoles.remove(i);
		UserThread calling = (UserThread)NachosThread.currentThread();
		calling.setConsoleDriver(hold);
		hold.setUsed(1);
		return hold;
	    }
	}
	return null;
    }
    
    public void freeConsole(ConsoleDriver driver)
    {
	driver.setUsed(0);
	availConsoles.add(driver);
    }
}
