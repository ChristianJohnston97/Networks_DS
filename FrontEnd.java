import java.io.*;
import java.net.*;
import java.lang.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;

public class FrontEnd
{
  private ServerInterface stub;
  private ArrayList<ServerInterface> servers;



  public FrontEnd()
  {
    servers = getServerList();

    for(int i=0; i<servers.size(); i++)
    {
      ServerInterface server = servers.get(i);
      try {
        Registry registry = LocateRegistry.getRegistry("localhost", 37001);
        // Lookup the remote object "stub" from registry
        // and create a stub for it

        server.dummyMethod();
        System.out.println("Connected to server stub: " + server.getStub());
        stub = server;
        break;
      }
      catch (Exception e) {
        System.err.println("Client exception: " + e.toString());
        e.printStackTrace();
      }
    }
  }


  public static void main(String args[]) throws Exception
  {
    FrontEnd myFrontEnd = new FrontEnd();
    ServerSocket welcomeSocket = new ServerSocket(14000);


    String firstGame;
    String secondGame;
    String thirdGame;

    String host = (args.length < 1) ? null : args[0];

    Socket connectionSocket = welcomeSocket.accept();


    BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));

    DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());

    while(true)
    {

      String s = inFromClient.readLine();
      int t = Integer.parseInt(s);

      switch (t) {
        case 1:  s = inFromClient.readLine();
        System.out.println("1st Game ordered : " + s + "\n");
        firstGame = s;

        s = inFromClient.readLine();
        if(s.equals("done"))
        {
          secondGame = null;
          thirdGame = null;
        }
        else{
          System.out.println("2nd Game ordered : " + s + "\n");
          secondGame = s;

          s = inFromClient.readLine();
          if(s.equals("done"))
          {
            thirdGame = null;
          }
          else{
            System.out.println("3rd Game ordered : " + s + "\n");
            thirdGame = s;
          }
        }

        boolean response = myFrontEnd.placeOrder(firstGame, secondGame, thirdGame);

        if (response = true)
        {
          System.out.println("Order succesful!");
        }
        else{
          System.out.println("Order not succesful!");
        }
        break;


        case 2:  String order = myFrontEnd.getOrder();
        System.out.println("Order -  " + order);
        outToClient.writeBytes(order + "\n");
        break;

        case 3:  order = myFrontEnd.getOrder();
        outToClient.writeBytes(order + "\n" );

        s = inFromClient.readLine();
        int i = Integer.parseInt(s);
        boolean response3 = myFrontEnd.cancelOrder(i);
        if (response3 = true)
        {
          outToClient.writeBytes("Order deleted!" + "\n" );
        }
        else{
          outToClient.writeBytes("Order not deleted!" + "\n" );
        }
        break;
      }
    }
  }


  public String getOrder()
  {
    try{
      ArrayList<Order> orderHistory = stub.orderHistory();

      String orderString = "";
      for(Order order : orderHistory)
      {
        orderString += order.toString();
      }
      return orderString;
    }

    catch (Exception e) {
      //System.err.println("Client exception: " + e.toString());
      //e.printStackTrace();

      stub = connectNewServer();
      if(stub == null)
      {
        System.err.println("No more servers!");
      }
      else
      {
        try{
          ArrayList<Order> orderHistory = stub.orderHistory();

          String orderString = "";
          for(Order order : orderHistory)
          {
            orderString += order.toString();
          }
          return orderString;
        }

        catch (Exception exc) {
          System.err.println("Client exception: " + exc.toString());
          exc.printStackTrace();
          return "";
        }
      }
    }
    return "";
  }


  public boolean placeOrder(String firstGame, String secondGame, String thirdGame)
  {
    System.out.println("place order result: " );
    try
    {
      boolean test = stub.placeOrder(firstGame, secondGame, thirdGame);
    }
    catch (Exception e) {
      System.err.println("Client exception: " + e.toString());
      e.printStackTrace();

      // ***IMPROVEMENT TO DISTRIBUTED SYSTEM, SEE BELOW ***

      connectNewServer();
      try{
        boolean test = stub.placeOrder(firstGame, secondGame, thirdGame);
      }
      catch (Exception exc) {
        // don't actually want to print these errors?
        System.err.println("Client exception: " + exc.toString());
        exc.printStackTrace();
      }
    }
    return true;
  }

  public boolean cancelOrder(int i)
  {
    try{
      stub.cancelOrder(i);
    }
    catch (Exception e) {
      //System.err.println("Client exception: " + e.toString());
      //e.printStackTrace();

      connectNewServer();
      try{
        stub.cancelOrder(i);
      }
      catch (Exception exc) {
        System.err.println("Client exception: " + exc.toString());
        exc.printStackTrace();
      }
    }
    return true;
  }

  public static ArrayList<ServerInterface> getServerList()
  {
    ArrayList<ServerInterface> serverList = new ArrayList<ServerInterface>();
    try{
      Registry registry = LocateRegistry.getRegistry("localhost", 37001);
      for(String name : registry.list())
      {
        try{
          ServerInterface serverStub = (ServerInterface) registry.lookup(name);
          serverStub.dummyMethod();
          serverList.add(serverStub);
        }
        catch (Exception exc) {
          System.err.println("Client exception: " + exc.toString());
          exc.printStackTrace();
        }
      }
    }
    catch (Exception exc) {
      System.err.println("Client exception: " + exc.toString());
      exc.printStackTrace();
    }

      return serverList;
    }


  //****** IMPROVEMENTS TO DISTRIBUTED SYSTEM ******

  // Method to promote backup server
  // unbind primary server
  // loop through list of servers (by now old primary has been removed)
  // try and set one of backup servers to primary server, i.e. rename to server1
  // rebind this new server.
  // does't matter which server is promoted.


  // When any service called by client, i.e add an order, request order history
  // or delete an order. Front end attempts to connect primary server,
  // if unavailable, promotes backup server to primary server and continues.

  public ServerInterface connectNewServer()
  {
    try{
      Registry registry = LocateRegistry.getRegistry("localhost", 37001);
      ArrayList<ServerInterface> serverList = FrontEnd.getServerList();
      //unbind primary server
      registry.unbind("server1");
      // attempt to conenct to any of the backup servers
      for(ServerInterface server: serverList)
      {
        try{
          // set stub of backup server to server 1, i.e. make primary
          server.setStub("server1");
          // bind new primary
          registry.rebind("server1", server);
          System.out.println("New primary server found");
          return server;
        }
        catch (Exception e) {
          System.err.println("Server exception: " + e.toString());
          e.printStackTrace();
        }
      }
      return null;
    }
    catch (Exception e) {
      System.err.println("Server exception: " + e.toString());
      e.printStackTrace();
      return null;
    }
  }
}
