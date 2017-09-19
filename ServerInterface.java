import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.*;

public interface ServerInterface extends Remote {
    public boolean placeOrder(String game1, String game2, String game3) throws RemoteException;
    public ArrayList<Order> orderHistory() throws RemoteException;
    public boolean cancelOrder(int i) throws RemoteException;
    public String getStub() throws RemoteException;
    public void dummyMethod() throws RemoteException;
    public void setStub(String stubName) throws RemoteException;
    public void getUpToSpeed() throws RemoteException;
}
