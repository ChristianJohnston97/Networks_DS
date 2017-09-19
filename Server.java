import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class Server implements ServerInterface {


  private ArrayList<Order> orderHistoryArray = new ArrayList<Order>();
  private String stubName;
  private static final String primaryName = "server1";

  public Server(String stubName)
  {
    this.stubName = stubName;
    getUpToSpeed();
  }

  public boolean placeOrder(String firstGame, String secondGame, String thirdGame)
  {
    ArrayList<ServerInterface> serverList = FrontEnd.getServerList();
    if(stubName.equals(primaryName))
    {
      for(ServerInterface server: serverList)
      {
        try{

          if(!server.getStub().equals(stubName))
          {
            server.placeOrder(firstGame, secondGame, thirdGame);
          }
        }
        catch (Exception e) {
          System.err.println("Client exception: " + e.toString());
          //e.printStackTrace();
        }
      }
    }
    Order order = new Order(firstGame, secondGame, thirdGame);
    orderHistoryArray.add(order);
    return true;
  }


  public ArrayList<Order> orderHistory()
  {
    return orderHistoryArray;
  }


  public boolean cancelOrder(int i)
  {
    ArrayList<ServerInterface> serverList = FrontEnd.getServerList();
    if(stubName.equals(primaryName))
    {
      for(ServerInterface server: serverList)
      {
        try{
          if(!server.getStub().equals(stubName))
          {
            server.cancelOrder(i);
          }
        }
        catch (Exception e) {
          //System.err.println("Client exception: " + e.toString());
          //e.printStackTrace();
        }
      }
    }
    orderHistoryArray.remove(i-1);
    return true;
  }


  public static void main(String args[]) {


    if(args.length < 1)
    {
      System.out.println("Please enter server name");
      System.out.println("If this is primary server please name as: server1");
      System.out.println("If backup server please name as anything");
    }
    else{
      try {
        String stubName = args[0];
        // Create server object
        Server obj = new Server(stubName);
        // Create remote object stub from server object
        ServerInterface stub = (ServerInterface) UnicastRemoteObject.exportObject(obj, 0);

        // Get registry
        Registry registry = LocateRegistry.getRegistry("localhost", 37001);

        // Bind the remote object's stub in the registry
        registry.rebind(stubName, stub);


        // Write ready message to console
        System.err.println("Server ready");
      } catch (Exception e) {
        System.err.println("Server exception: " + e.toString());
        e.printStackTrace();
      }
    }
  }

  public String getStub()
  {
    return stubName;
  }

  public void setStub(String stubName)
  {
    this.stubName=stubName;
  }

  public void dummyMethod()
  {
    System.out.println("");
  }

  //****** IMPROVEMENTS TO DISTRIBUTED SYSTEM ******


//The server has been modified that whenever a server starts it
//checks for other existing serves to sync with.
//This goes through all the servers in the RMI registry,
//looking at all the servers in the registry to see if they are working.
//Then gets the order history
// this ensure consistency.


  // if the failed primary server recovers,
  // allow this server to run as part of the distributed system again.
  // basically add back into the distributed system and updates its information.



  public void getUpToSpeed()
  {
    ArrayList<ServerInterface> serverList = FrontEnd.getServerList();

    for(ServerInterface server: serverList)
    {
      try{
        orderHistoryArray = server.orderHistory();
        break;
      }
      catch (Exception e) {
        System.err.println("Server exception: " + e.toString());
        e.printStackTrace();
      }
    }
  }
}
