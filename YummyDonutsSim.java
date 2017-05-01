import cosc_311_project_1.ClientQueue;
import java.util.Scanner;

/**
 * @author      Michael Boettner
 * @version     1.0
 * YummyDonutsSim class contains main method to run a queue simulation which
 * allows a user to input data about store operations, and receive output
 * relating to the efficiency of the serving staff on hand. 
 */
public class YummyDonutsSim {
    
    /**
    * Keyboard input
    */
    static Scanner keyboard = new Scanner(System.in);
    
    /**
    * Queue to store Client objects
    */
    private ClientQueue myClientQueue;
    
    /**
    * For verbose data output. User-specified at runtime.
    */
    private boolean printAll = false;
    
    /**
    * If user chooses verbose output, this constant is used to break the clock
    * loop at a certain number of cycles. (16 per project guidelines)
    */
    private final int ABBREVIATED_MINUTES = 16;
    
    /**
    * Average number of clients served per day. User-specified at runtime. 
    * (100 per project guidelines)
    */
    private int avgClientsPerDay;
    
    /**
    * Number of hours to run the simulation. User-specified at runtime.
    * (8, per project guidelines)
    */
    private int hoursInWorkDay;
    
    /**
    * Number of clock ticks per hour. User-specified at runtime. (60 per the
    * project rubric, which divides the simulation and calculations into
    * minute-long intervals)
    */
    private int clockTicksPerHour;
    
    /**
    * Number of servers at the shop counter servicing clients. User-specified
    * at runtime. (1, 2, or 3 per project guidelines)
    */
    private int numServers;
    
    /**
    * Decimal used for distribution. Represents the expected number
    * of clients entering the queue per clock tick
    */
    private double arrivalRate;
    
    /**
    * The current time in minutes since the shop opened for the day. This will
    * control the main simulation loop and much of the processes happening
    * inside it.
    */
    private int clock = 0;
    
    /**
    * The total number of clock ticks for the simulation. (calculated by
    * multiplying hours in a work day by clock ticks per hour)
    */
    private int totalTime;
    
    /**
    * Array which will be designated to size numServers. Used to keep track of
    * the time when each individual server will be finished serving their
    * current client.
    */
    private int timeDone[];
    
     /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        System.out.println(

            "      ___\n" +
            "     /   \\\n" +
            "    |  O  |\n" +
            "     \\___/\n" +
            "  Yummy Donuts\n" +
            "Queue Simulation\n");
        
        /**
        * New simulation instance
        */
        YummyDonutsSim sim = new YummyDonutsSim();
        sim.enterData();
        sim.runSimulation();   
    }
    
    /**
     * Allows user to enter simulation inputs during runtime
     */
    private void enterData()
    {
        System.out.print("Number of servers at the counter: ");
        numServers = keyboard.nextInt();
        
        timeDone = new int[numServers];
        
        System.out.print("Hours in a work day: ");
        hoursInWorkDay = keyboard.nextInt();
        
        System.out.print("Average number of clients per day: ");
        avgClientsPerDay = keyboard.nextInt();
        
        System.out.print("Number of clock ticks per hour: ");
        clockTicksPerHour = keyboard.nextInt();
        
        System.out.print("Provide verbose output? (y/n): ");
        String verboseOutput = keyboard.next();
        if(verboseOutput.charAt(0) == 'y' || verboseOutput.charAt(0) == 'Y')
        {
            printAll = true;
        }
        
        System.out.println(); //line return for spaced output

        //calculated number of clock ticks. (Project guidelines = 8 * 60)
        totalTime = hoursInWorkDay * clockTicksPerHour;
        
        //calculated arrival rate per clock tick. (Proj. guid. = 100 / 480)
        arrivalRate = (double)avgClientsPerDay / (double)totalTime;
        
        /**
        * ClientQueue instance
        */
        myClientQueue = new ClientQueue(arrivalRate);
    }
    
    /**
     * Primary control for running the simulation, outputting minute-by-minute
     * output and the total simulation summary
     */
    private void runSimulation()
    {
        //execute a loop once for each simulated minute        
        for(clock = 0; clock < totalTime; clock++)
        {   
            if(printAll)
            {
                System.out.println("Clock: " + clock);
            }
            
            //check for incoming clients on each clock tick
            myClientQueue.checkNewArrival(clock, printAll);
            
            //cycles through all servers to check availability
            for(int currentServer = 0; currentServer < numServers;
                    currentServer++)
            {
                /*
                *if the queue is empty and the current server has finished
                *with their customer, this means that they are idle
                */
                if((myClientQueue.isEmpty())
                        && (timeDone[currentServer] <= clock))
                {
                    myClientQueue.incrementIdleTime();

                    if(printAll)
                    {
                        System.out.println("     Server #" +
                            (int)(currentServer + 1) + " is idle.");   
                    }    
                }
                /*
                *else if the current server has finished with their customer
                *then they begin serving the next person in line
                */
                else if(clock >= timeDone[currentServer])
                    startServe(currentServer);
                /*
                *if neither of these test true, that means that the current
                *server is still busy with a customer, so we do not make a 
                *call to startServe method
                */
            }
            if(printAll)
            {
                System.out.println("     Queue size: " +
                        myClientQueue.getSize());
                //break if running the verbose abbreviated simulation
                if(clock == ABBREVIATED_MINUTES - 1)
                    break;
            }
        }
        
        //simulation summary output
        System.out.println("\nResults of Simulation:");
        System.out.println("     Clients who entered the shop: "
                + (myClientQueue.getNumClientsServed()
                    + myClientQueue.getNumClientsDropped()
                        + myClientQueue.getSize()));
        System.out.println("     Number of clients served: "
                + myClientQueue.getNumClientsServed());
        System.out.println("     Number of clients dropped out of line: "
                + myClientQueue.getNumClientsDropped());        
        System.out.print("     Average client wait time: ");
        System.out.printf("%.2f", myClientQueue.getAverageWait());
        System.out.print(" minutes\n");
        System.out.println("     Total client wait time: "
                + myClientQueue.getTotalWait() + " minutes");
        System.out.println("     Total server idle time: "
                + myClientQueue.getServerIdleTime() + " minutes");
        System.out.println("     Final size of queue: "
                + myClientQueue.getSize());
    }
    
    /**
     * Initiates an update to the queue, where the next client will 
     * potentially be served.
     * @param currentServer The available server who will be helping the next
     * client
     */
    private void startServe(int currentServer)
    {
        //set time the particular server will be done with current client
        timeDone[currentServer] = myClientQueue.update(clock, currentServer,
                printAll);
    }
}