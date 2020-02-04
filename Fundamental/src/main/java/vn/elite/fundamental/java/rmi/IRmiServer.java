package vn.elite.fundamental.java.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IRmiServer extends Remote {
    String getMessage() throws RemoteException;
}
