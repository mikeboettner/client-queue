package cosc_311_project_1;

/**
 * @author      Michael Boettner
 * @version     1.0
 * Client class allows for new Client creation and initialization of data
 */
public class Client {
    
    /**
    * The clock time the client entered the line
    */
    protected final int arrivalTime;
    
    /**
    * Calculated number of clock ticks it will take to serve this particular
    * client. (randomly generated from 1 to 5 clock ticks)
    */
    protected final int serviceTime;
    
    /**
    * The number of clock ticks that the client has been waiting in line.
    * This will be determined in the ClientQueue class when the client is 
    * removed from the queue, by calculating current clock time minus the 
    * client's arrivalTime.
    */
    protected int timeWaiting;
    
    /**
    * Unique identifying number for each client
    */
    protected final int customerID;
    
    /**
    * Used to assign each client's customerID
    */
    private static int customerIDIncrementer = 1;
    
    /**
    * Constructor initializes new Client
    * 
    * @param  clock The current clock time
    */
    Client(int clock)
    {
        arrivalTime = clock;
        //randomly calculates a number from 1 - 5
        serviceTime = (int)Math.ceil(Math.random() * 5);
        timeWaiting = 0;
        customerID = customerIDIncrementer++;
    }
    
    /**
     * Get method
     *
     * @return int containing the amount of time it will take to serve this
     * client
     */
    protected int getProcessingTime()
    {
        return serviceTime;
    }
    
    /**
     * Get method
     *
     * @return int containing the ID number of the newest client joining the
     * queue
     */
    protected static int getCurrentIDvalue()
    {
        return customerIDIncrementer;
    }
}