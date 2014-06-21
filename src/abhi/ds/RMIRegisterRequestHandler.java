/**
 * 
 */
package abhi.ds;


import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.*;
import java.util.*;


/**
 * @author abhisheksharma, dkrew0213
 *
 */
public class RMIRegisterRequestHandler implements Runnable {

	private RMIRegistry rmiRegistry = null;
	private Socket requestSocket = null;
	
	public RMIRegisterRequestHandler (Socket requestSocket, RMIRegistry registry)
	{
		this.requestSocket = requestSocket;
		this.rmiRegistry = registry;
	}
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()ww
	 */
	@Override
	public void run() {
		// TODO 
		//People will come here to register services
		
		//People will come here to do lookup i.e. request the RemoteReference of Objects they are looking for.
		
		//@Doug: So we need those 2 kinds of Signals

		if(requestSocket.isConnected()){
			System.out.println("RMI : Executing the request");
			ObjectInputStream sockIn;
				try {
					sockIn = new ObjectInputStream(requestSocket.getInputStream());
					BaseSignal baseSignal;
					baseSignal = (BaseSignal) sockIn.readObject();
					switch (baseSignal.getSignalType()) {
						case Bind:
							bind((BindSignal) baseSignal);
							break;
						case Rebind:
							rebind((RebindSignal) baseSignal);
							break;
						case LookUp:
							lookup((LookupSignal) baseSignal);
							break;
						default:
							break;
					}	
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch ( ClassNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
		}
		
		

				
				
	}
	
	private void bind(BindSignal bindSignal){
		RemoteRef remote_Ref = bindSignal.getRemote_Ref();
		
		try {
			if(! this.rmiRegistry.getRegistryMap().containsKey(remote_Ref.getRegister_Name())){
				this.rmiRegistry.getRegistryMap().put(remote_Ref.getRegister_Name(), remote_Ref);
				
				HelperUtility.sendSignal(requestSocket, new AckSignal());
				System.out.println("Binding Successful.");
				System.out.println(this.rmiRegistry.getRegistryMap());
			} else {
				System.out.println("Failed : Register name is already in use.");
				HelperUtility.sendSignal(requestSocket, new RemoteExceptionSignal("REBIND : Cannot bind the register name is already in use."));
			}
		} catch (RuntimeException | IOException e) {
			
			System.out.println("Error in the connection.");
		} 
	
		
	
		
	}
	private void rebind(RebindSignal rebindSignal){
		RemoteRef remote_Ref = rebindSignal.getRemote_Ref();
		
		try {
			if( this.rmiRegistry.getRegistryMap().containsKey(remote_Ref.getRegister_Name())){
				this.rmiRegistry.getRegistryMap().put(remote_Ref.getRegister_Name(), remote_Ref);
				HelperUtility.sendSignal(requestSocket, new AckSignal());
				System.out.println("Rebinding Successful.");
			} else {
				System.out.println("Failed : Register name does not exist.");
				HelperUtility.sendSignal(requestSocket, new RemoteExceptionSignal("Cannot rebind the register name dose not exist."));
			}
				
		} catch (RuntimeException | IOException e) {
			
			System.out.println("Error in the connection.");
		} 
		
			
	}
	private void lookup(LookupSignal lookupSignal){
		String look_up_name = lookupSignal.getLookup_Name();
		
		try {
			if( this.rmiRegistry.getRegistryMap().containsKey(look_up_name)){
				System.out.println("Lookup Successful.");
				RemoteRef remote_Ref = this.rmiRegistry.getRegistryMap().get(look_up_name);
				HelperUtility.sendSignal(requestSocket, new AckLookupSignal(remote_Ref));
				
				
			} else {
				System.out.println("Failed : Register name does not exist.");
				HelperUtility.sendSignal(requestSocket, new RemoteExceptionSignal("There are no remote reference object to look up."));
			}		
		} catch (RuntimeException | IOException e) {
			
			System.out.println("Error in the connection.");
		} 
	
	}

}
