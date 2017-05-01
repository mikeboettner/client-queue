package cosc_311_project_1;
import java.util.Queue;
import java.util.LinkedList;

/**
 * @author      Michael Boettner
 * @version     1.0
 * ClientQueue class using Queue storage of Client objects, with methods to
 * check for new Clients to be added, update the queue as time passes, and
 * various get methods.
 */
public class ClientQueue {
    
    /**
    * Queue to hold Client objects
    */
    private final Queue<Client> myQueue;
    
    /**
    * Stores the total number of clients actually served
    */
    private int numClientsServed;
    
    /**
    * Stores the total combined time that all client's had to wait in line,
    * excluding the clients who dropped out of line
    */
    private int totalWaitTime;
    
    /**
    * Stores the total number of clients who dropped out of line (those who
    * waited 5 or more minutes)
    */
    private int numClientsDropped;
    
    /**
    * Stores the total combined amount of time that servers were idle
    */
    private int totalServerIdleTime;
    
    /**
    * Stores the calculated arrival rate for the distribution of expected
    * clients
    */
    private final double arrivalRate;
    
    /**
    * Constructor initializes new ClientQueue
    * 
    * @param arrivalRate The arrival rate for new customers entering queue
    */
    public ClientQueue(double arrivalRate)
    {
        myQueue = new LinkedList<>();
        this.arrivalRate = arrivalRate;
        numClientsServed = 0;
        totalWaitTime = 0;
        numClientsDropped = 0;
        totalServerIdleTime = 0;
    }
    
    /**
    * Uses the calculated arrival rate to determine whether to add a new
    * Client to the queue
    * 
    * @param clock The current clock time
    * @param printAll boolean for abbreviated verbose output
    */
    public void checkNewArrival(int clock, boolean printAll)
    {
        //if a random decimal falls inside the arrival rate, add new Client
        if(Math.random() < arrivalRate)
        {
            myQueue.add(new Client(clock));
            if(printAll)
            {
                System.out.println("     Client #" +
                        (Client.getCurrentIDvalue() - 1)
                            + " entered the line.");
            }
        }
    }
    
    /**
    * Called when it is time to process the next Client in the queue 
    * 
    * @param clock The current clock time
    * @param currentServer The current available server
    * @param printAll boolean for abbreviated verbose output
    * 
    * @return int, a calculated clock time, which will be set to be the
    * currentServer's timeDone
    */
    public int update(int clock, int currentServer, boolean printAll)
    {
        if(!isEmpty())
        {   
        //remove the first client from queue to be served
        Client clientBeingServed = myQueue.remove();
        
        /*calculate the current customer's waiting time by subtracting
        *their arrival time from the current time
        */
        clientBeingServed.timeWaiting = clock - clientBeingServed.arrivalTime;
        
            /*
            *if the client had been waiting 5 or more minutes in line then
            *they dropped out. Do not serve. Increment dropped client counter.
            */
            if(clientBeingServed.timeWaiting >= 5)
            {
                numClientsDropped++;
                if(printAll)
                {
                    System.out.println("     ! Client #" +
                            clientBeingServed.customerID +
                                " dropped out of line. ");
                }
                
                //re-call update method to serve the next customer in line
                update(clock, currentServer, printAll);
            }
            //otherwise the client gets served
            else
            {
                //increment summary counters
                numClientsServed++;
                totalWaitTime += clientBeingServed.timeWaiting;
                
                if(printAll)
                {
                    System.out.println("     Client #" +
                        clientBeingServed.customerID
                        + " served by server #" + (int)(currentServer + 1)
                            + ". They waited " + clientBeingServed.timeWaiting
                                + " minute(s) in line, and it takes "
                                    + clientBeingServed.serviceTime +
                                        " minute(s) to serve them.");
                } 
            //returns the actual clock time that this customer will be done
            return clock + clientBeingServed.getProcessingTime();
            }
        }
        //else, return the current clock time
        return clock;
    }
    
    /**
     * @return boolean telling us whether or not the queue is empty
     */
    public boolean isEmpty()
    {
        return myQueue.isEmpty();
    }
    
    /**
     * @return Total accumulated wait time for all served clients
     */
    public int getTotalWait()
    {
        return totalWaitTime;
    }
    
    /**
     * @return Calculated average wait time for all served clients
     */
    public double getAverageWait()
    {
        return ((double)totalWaitTime / numClientsServed);
    }
    
    /**
     * @return Accumulated number of clients served
     */
    public int getNumClientsServed()
    {
        return numClientsServed;
    }
    
    /**
     * @return Accumulated number of clients who dropped out of line
     */
    public int getNumClientsDropped()
    {
        return numClientsDropped;
    }
    
    /**
     * @return The size of the queue
     */
    public int getSize()
    {
        return myQueue.size();
    }
    
    /**
     * Increments the total server idle time by one, for each clock tick and 
     * for each server where the server is not serving a client
     */
    public void incrementIdleTime()
    {
        totalServerIdleTime++;
    }
    
    /**
     * @return Accumulated server idle time
     */
    public int getServerIdleTime()
    {
        return totalServerIdleTime;
    }
}