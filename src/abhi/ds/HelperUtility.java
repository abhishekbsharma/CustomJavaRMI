package abhi.ds;



import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class HelperUtility {
	
// Helper Utility Class hold Systemic constants and Common utility functions that will be used by all other things in the system

	
//Send an object over the Wire	
public static void sendSignal(Socket socket, Object object) throws RuntimeException, IOException
{
	    if (socket == null) {
	        throw new RuntimeException("Invalid Socket.");
	      }
	      ObjectOutputStream objOut = null;
	      try 
	      {
	    	  objOut = new ObjectOutputStream(socket.getOutputStream());
	    	  objOut.writeObject(object);
	      } 
	      catch (IOException e) 
	      {
	        e.printStackTrace();
	        throw e;
	      }
}

//Receive and Object over the Wire
public static Object receiveSignal(Socket socket) throws RuntimeException, IOException{
    if (socket == null) {
      throw new RuntimeException("Invalid Socket.");
    }
    ObjectInputStream objIn = null;
    Object signal = null;
    try 
    {
    	objIn = new ObjectInputStream(socket.getInputStream());
    	signal = objIn.readObject();
    } 
    catch (IOException e) 
    {
      System.out.println("Error while Receiving Signal.");
      System.out.println("Please check the connection information and try again.");
      throw e;
    } 
    catch (ClassNotFoundException e) 
    {
      e.printStackTrace();
    }

    return signal;
 }


}
