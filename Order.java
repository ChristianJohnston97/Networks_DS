import java.io.*;
import java.net.*;
import java.util.*;

public class Order implements Serializable
{
  public Order(String firstGame, String secondGame, String thirdGame)
  {
    this.firstGame = firstGame;
    this.secondGame = secondGame;
    this.thirdGame = thirdGame;
  }

String firstGame;
String secondGame;
String thirdGame;

public String toString() {
        return "First Game: " + this.firstGame + " "
                + "Second Game: " + this.secondGame + " "
                + "Third Game: " + this.thirdGame + (char)31;
}
}
