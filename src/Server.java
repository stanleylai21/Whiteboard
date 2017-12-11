import java.beans.XMLEncoder;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.io.PrintWriter;
import java.io.Serializable;

public class Server implements Runnable {
	private ObjectOutputStream outputToClient;
	private PrintWriter out;
	private int portNumber;
	private ServerSocket listener;
	private Socket socket;
	private ArrayList<Socket> sockets;
	private ArrayList<ObjectOutputStream> outputs = new ArrayList<ObjectOutputStream>();
	private Canvas c;
	public Server(int portNum)
	{
		if(portNum != 0)
		{
			portNumber = portNum;
		}
		else
		{
			portNumber = 0;
		}	
		sockets = new ArrayList<Socket>();
	}
	public void setC(Canvas c) {
		this.c = c;
	}
	public void run()
	{
		try {		
			listener = new ServerSocket(portNumber);
			while(true)
			{
				try {
					socket = listener.accept();
					sockets.add(socket);
					outputToClient = new ObjectOutputStream(socket.getOutputStream());
					outputs.add(outputToClient);
					for (DShape d:c.getShapes())
					{
						Payload payload = packageData(d.getDShapeModel().getIndex(),"add",d.getDShapeModel());
						String message = payload.toString();
						outputToClient.writeObject(message);
                		outputToClient.flush();
					}
				} catch(EOFException e){
					e.printStackTrace();
				} 
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally
		{
			System.exit(1);
			closeConnection();
			closeServer();
		}
	}
	private void closeConnection()
	{
		try {		
			socket.close();
		} catch(IOException e)
		{
			e.printStackTrace();
		} 
	}
	private void closeServer()
	{
		try {
			out.close();
			listener.close();		
		} 
		catch(IOException e)
		{
			e.printStackTrace();
		} 
	}
	public Payload packageData(int modelID, String command, DShapeModel model) 
	{	
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		XMLEncoder xmlEncoder = new XMLEncoder(baos);
		xmlEncoder.writeObject(model);
		xmlEncoder.close();
		String xmlModel = baos.toString();
		Payload payload = new Payload(modelID, command, xmlModel);	
		return payload;
	}
	
	public void sendData(Payload payload)
	{
		try {
			for (int i = 0; i<outputs.size();i++)
			{
				ObjectOutputStream output = outputs.get(i);
				String message = payload.toString();
				output.writeObject(message);
				output.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public class Payload
	{
		int id;
		String command;
		String xmlModel;	
		Payload(int id, String command, String xmlModel)
		{
			this.id = id;
			this.command = command;
			this.xmlModel = xmlModel;
		}
		public String toString()
		{
			String output = "";
			output += id;
			output += ":";
			output += command;
			output += ":";
			output += xmlModel;
			return output;
		}
	}
}
