package vn.elite.fundamental.java.rmi;

import java.rmi.Naming;

public class RmiClient {
    public static void main(String[] args) throws Exception {
        IRmiServer obj = (IRmiServer) Naming.lookup("//localhost/RmiServer");
        System.out.println(obj.getMessage());
    }
}
