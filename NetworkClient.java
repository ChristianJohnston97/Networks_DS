import java.io.*;
import java.net.*;

class NetworkClient
//differentiate between multiple clients??
{
  public static void main(String argv[]) throws Exception
  {

    //Create input stream
    BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));

    // i) placing an order, where each order can contain up to three items,
    // ii) retrieving order history, and
    // iii) cancelling an order.

    //Create client socket, connect to server
    Socket clientSocket = new Socket("localhost", 14000);
    System.out.println("Connected to server!");


    //Create output stream attached to socket
    DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());

    //Create input stream attached to socket
    BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));


    int t;
    boolean quits = false;
    while(!quits)
    {
      System.out.println("Please select a service!");
      System.out.println("If you would like to place an order please type 1");
      System.out.println("If you would like to retrieve order history please type 2");
      System.out.println("If you would like to cancel an order please type 3");
      System.out.println("If you would like to exit please type 4");
      String s = inFromUser.readLine();
      t = Integer.parseInt(s);
      if (t == 1 || t == 2 || t == 3 || t == 4)
      {
        outToServer.writeBytes(t + "\n");
      }
      else
      {
        //how to get this to loop back to the beginning
        //throw an error?
        System.out.println("Please select a service!");
      }



      switch (t) {
        case 1:  System.out.println("Please place an order, where each order can contain up to three items,");

        System.out.println("Please enter first game");
        s = inFromUser.readLine();
        System.out.println("1st item ordered: " + s);
        outToServer.writeBytes(s + "\n");

        System.out.println("Would you like to add another game to order? Yes or No ");
        s = inFromUser.readLine();
        if (s.equals("Yes") || s.equals("yes"))
        {
          System.out.println("Please enter 2nd game");
          s = inFromUser.readLine();
          System.out.println("2nd item ordered: " + s);
          outToServer.writeBytes(s + "\n");

          System.out.println("Would you like to add another game to order? Yes or No");
          s = inFromUser.readLine();
          if (s.equals("Yes") || s.equals("yes"))
          {
            System.out.println("Please enter 3rd game");
            s = inFromUser.readLine();
            System.out.println("3rd item ordered: " + s);
            outToServer.writeBytes(s + "\n");
            System.out.println("Your order has been placed!");
          }
          else
          {
            System.out.println("Your order has been placed!");
            s = "done";
            outToServer.writeBytes(s + "\n");
          }
        }
        else
        {
          System.out.println("Your order has been placed!");
          s = "done";
          outToServer.writeBytes(s + "\n");
        }
        break;

        case 2:  s = inFromServer.readLine();
        s = s.replace("" + (char)31, "\n");
        System.out.println("Order History: " + "\n" + s);
        break;

        case 3:  s = inFromServer.readLine();
        System.out.println("Please select which order you would like to cancel");
        System.out.println("Please type a number for which order to delete, i.e top of list is order 1");
        s = inFromUser.readLine();
        outToServer.writeBytes(s + "\n");
        s = inFromServer.readLine();
        System.out.println(s);
        break;

        case 4:
        quits = true;
        break;

        default: System.out.println("Please select a service!");
        break;
      }
    }
  }
}
