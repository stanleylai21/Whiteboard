import java.io.*;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.beans.XMLDecoder;


public class Client implements Runnable{
	Socket socket;
	private Canvas c;
	public Client(String hostnameAndPort) throws UnknownHostException, IOException
	{
		try {
		String hostPort[] = hostnameAndPort.split(":"); 
		String localhost = hostPort[0];
		int port = Integer.parseInt(hostPort[1]);
		socket = new Socket();
		socket.connect(new InetSocketAddress(localhost, port), 2000);
		}
		catch (ConnectException|UnknownHostException|SocketTimeoutException ce)
		{
			throw ce;
		}
	}
	public void setC(Canvas c) {
		this.c = c;
	}
	public void run()
	{
		try {
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			while (true) {
				String xmlString = (String) in.readObject();
				String hello2[] = xmlString.split(":");
			    int index = Integer.parseInt(hello2[0]);
			    String command = hello2[1];
			    String xml = hello2[2];
			    handleInput(index, command, xml);
			}
		}
		catch (SocketException s) {
			s.printStackTrace();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try {
				socket.close();
				System.exit(1);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	public void handleInput(int index, String command, String xml)
	{
		XMLDecoder decoder = new XMLDecoder(new ByteArrayInputStream(xml.getBytes()));
        DShapeModel model = (DShapeModel) decoder.readObject();
        if (command.equals("add"))
        {
        	model.addModelListener(c.getSt());
        	c.addShape(model);
        	c.setSelectedShape(null);
    		c.repaint();
            decoder.close();
        }
        else if (command.equals("remove"))
        {
        	c.getShapes().remove(index);
			c.setSelectedShape(null);
			for (int i = 0;i<c.getShapes().size();i++)
			{
				c.getShapes().get(i).getDShapeModel().setIndex(i);
			}
			c.repaint();
			c.getSt().refigureRows();
        }
        else if (command.equals("front"))
        {
        	DShape temp = c.getShapes().get(index);
        	c.getShapes().remove(index);
			c.getShapes().add(temp);
			for (int i = 0;i<c.getShapes().size();i++)
			{
				c.getShapes().get(i).getDShapeModel().setIndex(i);
			}
			c.repaint();
			c.getSt().refigureRows();
        }
        else if (command.equals("back"))
        {
        	DShape temp = c.getShapes().get(index);
        	c.getShapes().remove(index);
			c.getShapes().add(0,temp);
			for (int i = 0;i<c.getShapes().size();i++)
			{
				c.getShapes().get(i).getDShapeModel().setIndex(i);
			}
			c.repaint();
			c.getSt().refigureRows();
        }
        else if (command.equals("change"))
        {
        	c.getShapes().get(index).getDShapeModel().mimic(model);
        }
        else
        {
        	System.out.println("Unrecognized command!");
        }
	}
}
