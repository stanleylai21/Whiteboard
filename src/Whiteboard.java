import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Whiteboard extends JFrame implements ModelListener{
	private Canvas c;
	private Server server;
	private Client client;
	boolean serverEnabled = false;
	public Whiteboard() {
		c = new Canvas();
		c.setLayout(new GridBagLayout());
		c.setPreferredSize(new Dimension(400,400));
		c.setMaximumSize(new Dimension(400,400));
		c.setBackground(Color.WHITE);
		
		ShapeTable model = new ShapeTable();
		model.setCanvas(c);
        JTable table = new JTable(model);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		
		JLabel firstLabel = new JLabel("   Add Shape:   ");
		JButton rectButton = new JButton("Rect");
		rectButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				DRectModel d = new DRectModel();
				d.addModelListener(model);
				addWhiteboardListener(d);
				c.addShape(d);
				c.repaint();
				if (serverEnabled)
				{
					Server.Payload payload = server.packageData(d.getIndex(),"add",d);
					server.sendData(payload);
				}
			}
		});
		JButton ovalButton = new JButton("Oval");
		ovalButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				DOvalModel o = new DOvalModel();
				o.addModelListener(model);
				addWhiteboardListener(o);
				c.addShape(o);
				c.repaint();
				if (serverEnabled)
				{
					Server.Payload payload = server.packageData(o.getIndex(),"add",o);
					server.sendData(payload);
				}
			}
		});
		
		JButton lineButton = new JButton("Line");
		lineButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				DLineModel l = new DLineModel();
				l.addModelListener(model);
				addWhiteboardListener(l);
				c.addShape(l);
				c.repaint();
				if (serverEnabled)
				{
					Server.Payload payload = server.packageData(l.getIndex(),"add",l);
					server.sendData(payload);
				}
			}
		});
		JButton textButton = new JButton("Text");
		
		Box box = Box.createHorizontalBox();
		box.add(firstLabel);
		box.add(rectButton);
		box.add(ovalButton);
		box.add(lineButton);
		box.add(textButton);
		
		
		
		JLabel secondLabel = new JLabel("   Set Color:   ");
		JButton colorButton = new JButton("Color Changer");
		colorButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (c.getSelectedShape()!=null)
				{
					Color d = JColorChooser.showDialog(null, "test", c.getSelectedShape().getDShapeModel().getColor());
					if (d!=null)
					{
						c.getSelectedShape().getDShapeModel().setColor(d);
					}
				}
			}
		});
		Box box2 = Box.createHorizontalBox();
		box2.add(secondLabel);
		box2.add(colorButton);
		
		JLabel thirdLabel = new JLabel("   Modify Text:   ");
		JTextField text = new JTextField();
		text.setMaximumSize(new Dimension(1000,25));
		text.setEditable(false);
		text.getDocument().addDocumentListener(new DocumentListener()
		{
			public void changedUpdate(DocumentEvent arg0) {}
			public void insertUpdate(DocumentEvent arg0) {
				if (c.getSelectedShape() instanceof DText)
				{
					((DTextModel)c.getSelectedShape().getDShapeModel()).setText(text.getText());
				}
			}
			public void removeUpdate(DocumentEvent arg0) {
				if (c.getSelectedShape() instanceof DText)
				{
					((DTextModel)c.getSelectedShape().getDShapeModel()).setText(text.getText());
				}
			}
			
		});
		GraphicsEnvironment e = GraphicsEnvironment.getLocalGraphicsEnvironment();
		Font[] allFonts = e.getAllFonts();
		JComboBox<Font> fontBox = new JComboBox<Font>(allFonts);
		fontBox.setRenderer(new DefaultListCellRenderer()
				{
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus)
			{
				if (value != null)
				{
					Font font = (Font) value;
					value = font.getName();
				}
				return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			}
				});
		fontBox.setMaximumSize(new Dimension(25,25));
		fontBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				if (c.getSelectedShape() instanceof DText)
				{
					((DTextModel)c.getSelectedShape().getDShapeModel()).setFontName(((Font)fontBox.getSelectedItem()).getName());
					c.repaint();
				}
			}
		});
		Box box3 = Box.createHorizontalBox();
		box3.add(thirdLabel);
		box3.add(text);
		box3.add(fontBox);
		
		textButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				DTextModel t = new DTextModel();
				t.addModelListener(model);
				addWhiteboardListener(t);
				c.addShape(t);
				c.repaint();
				if (serverEnabled)
				{
					Server.Payload payload = server.packageData(t.getIndex(),"add",t);
					server.sendData(payload);
				}
				text.setEditable(true);
				text.setText(t.getText());
				Font font = new Font(t.getFontName(), Font.PLAIN, 1);
				fontBox.setSelectedItem(font);
			}
		});
		
		JLabel fourthLabel = new JLabel("   Edit Selected Shape:   ");
		JButton forwardButton = new JButton("Move to Front");
		forwardButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				if (c.getSelectedShape() != null)
				{
					if (serverEnabled)
					{
						Server.Payload payload = server.packageData(c.getSelectedShape().getDShapeModel().getIndex(),"front",c.getSelectedShape().getDShapeModel());
						server.sendData(payload);
					}
					c.getShapes().remove(c.getShapes().indexOf(c.getSelectedShape()));
					c.getShapes().add(c.getSelectedShape());
					for (int i = 0;i<c.getShapes().size();i++)
					{
						c.getShapes().get(i).getDShapeModel().setIndex(i);
					}
					c.repaint();
					c.getSt().refigureRows();
				}
			}
		});
		JButton backButton = new JButton("Move to Back");
		backButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				if (c.getSelectedShape() != null)
				{
					if (serverEnabled)
					{
						Server.Payload payload = server.packageData(c.getSelectedShape().getDShapeModel().getIndex(),"back",c.getSelectedShape().getDShapeModel());
						server.sendData(payload);
					}
					c.getShapes().remove(c.getShapes().indexOf(c.getSelectedShape()));
					c.getShapes().add(0,c.getSelectedShape());
					for (int i = 0;i<c.getShapes().size();i++)
					{
						c.getShapes().get(i).getDShapeModel().setIndex(i);
					}
					c.repaint();
					c.getSt().refigureRows();
				}
			}
		});
		JButton removeButton = new JButton("Remove Shape");
		removeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				if (c.getSelectedShape() != null)
				{
					if (serverEnabled)
					{
						Server.Payload payload = server.packageData(c.getSelectedShape().getDShapeModel().getIndex(),"remove",c.getSelectedShape().getDShapeModel());
						server.sendData(payload);
					}
					c.getShapes().remove(c.getShapes().indexOf(c.getSelectedShape()));
					c.setSelectedShape(null);
					for (int i = 0;i<c.getShapes().size();i++)
					{
						c.getShapes().get(i).getDShapeModel().setIndex(i);
					}
					c.repaint();
					c.getSt().refigureRows();
				}
			}
		});
		Box box4 = Box.createHorizontalBox();
		box4.add(fourthLabel);
		box4.add(forwardButton);
		box4.add(backButton);
		box4.add(removeButton);
		
        JScrollPane scrollpane = new JScrollPane(table); 
        scrollpane.setPreferredSize(new Dimension(600,300)); 
		
        JLabel fifthLabel = new JLabel("   Save/Load:   ");
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e)
        	{
        		DShapeModel[] d = new DShapeModel[c.getShapes().size()];
        		for (int i = 0;i < c.getShapes().size();i++)
        		{
        			d[i] = c.getShapes().get(i).getDShapeModel();
        		}
        		JFileChooser chooser = new JFileChooser();
        		chooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
    		    FileNameExtensionFilter filter = new FileNameExtensionFilter(
    		        "Whiteboard XML", "xml");
    		    chooser.setFileFilter(filter);
    		    int saveVal = chooser.showSaveDialog(null);
    		    if(saveVal == JFileChooser.APPROVE_OPTION) 
    		    {
    		    	try {
    		    		FileOutputStream fos = new FileOutputStream(chooser.getSelectedFile().getCanonicalPath() + ".xml");
						//FileOutputStream fos = new FileOutputStream(chooser.getSelectedFile().getName() + ".xml");
						XMLEncoder encoder = new XMLEncoder(fos);
					    encoder.writeObject(d);
					    encoder.close();
					    fos.close();
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
    		    }
        	}
        });
        JButton loadButton = new JButton("Open");
        loadButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e)
        	{
        		JFileChooser chooser = new JFileChooser();
        		chooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
    		    FileNameExtensionFilter filter = new FileNameExtensionFilter(
    		        "Whiteboard XML", "xml");
    		    chooser.setFileFilter(filter);
    		    int loadVal = chooser.showOpenDialog(null);
    		    if(loadVal == JFileChooser.APPROVE_OPTION) 
    		    {
    		    	FileInputStream fis;
					try {
						fis = new FileInputStream(chooser.getSelectedFile().getCanonicalPath());
						XMLDecoder decoder = new XMLDecoder(fis);
					    DShapeModel[] d = (DShapeModel[])decoder.readObject();
					    decoder.close();
					    fis.close();
					    if (serverEnabled)
						{
							for (int i = c.getShapes().size()-1;i>=0;i--)
							{
								Server.Payload payload = server.packageData(i,"remove",c.getShapes().get(i).getDShapeModel());
								server.sendData(payload);
							}
						}
						c.getShapes().clear();
						for (DShapeModel model2:d)
						{
							model2.addModelListener(model);
							c.addShape(model2);
							addWhiteboardListener(model2);
							model2.updateModelAfterMouseRelease();
							if (serverEnabled)
							{
								Server.Payload payload = server.packageData(model2.getIndex(),"add",model2);
								server.sendData(payload);
							}
						}
						c.repaint();
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
    		    }
        	}
        });
        JButton pictureButton = new JButton("Save as PNG");
        pictureButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e)
        	{
        		JFileChooser chooser = new JFileChooser();
        		chooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
    		    FileNameExtensionFilter filter = new FileNameExtensionFilter(
    		        "Whiteboard PNG", "png");
    		    chooser.setFileFilter(filter);
    		    int saveVal = chooser.showSaveDialog(null);
    		    if(saveVal == JFileChooser.APPROVE_OPTION) 
    		    {
    		    	c.setSelectedShape(null);
    		    	BufferedImage bi = new BufferedImage(c.getWidth(), c.getHeight(), BufferedImage.TYPE_INT_ARGB); 
    		    	Graphics g = bi.createGraphics();
    		    	c.paint(g);
    		    	g.dispose();
    		    	try
    		    	{
    		    		ImageIO.write(bi,"png",new File(chooser.getSelectedFile().getCanonicalPath() + ".png"));
    		    	}
    		    	catch (Exception e2) {}
    		    }
        	}
        });
        
        Box box5 = Box.createHorizontalBox();
        box5.add(fifthLabel);
        box5.add(saveButton);
        box5.add(loadButton);
        box5.add(pictureButton);
        
        JLabel sixthLabel = new JLabel("   Networking:   ");
        JButton serverButton = new JButton("Start Server");
        JButton clientButton = new JButton("Start Client");
        JLabel statusLabel = new JLabel();
        statusLabel.setVisible(false);
        serverButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e)
        	{
        		int input = 0;
        			try {
        				String stringInput = JOptionPane.showInputDialog("Enter a port number: (default is 48585) ");
        					if (stringInput.isEmpty())
            				{
            					stringInput = "48585";
            				}
        					input = Integer.parseInt(stringInput);
        					if (input < 0 || input > 65535)
                			{
                				statusLabel.setText("   Server connection failed!");
                				statusLabel.setVisible(true);
                			}
        					else {
                				serverButton.setEnabled(false);
                        		clientButton.setEnabled(false);
                        		statusLabel.setText("   Server Mode Enabled: Port " + input);
                        		statusLabel.setVisible(true);
                        		setUpServer(input);
                        		serverEnabled = true;
                			}
        			}
        			catch(NumberFormatException n)
        			{
        				statusLabel.setText("   Server connection failed!");
        				statusLabel.setVisible(true);
        				
        			}
        	}
        });
        clientButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e)
        	{
        		int input = 0;
        		String ip = "";
        		try {
    				String stringInput = JOptionPane.showInputDialog("Enter an IP Address and port number: (default is 127.0.0.1:48585)");
    				if (stringInput.isEmpty())
    				{
    					stringInput = "127.0.0.1:48585";
    				}
    				String[] hello = stringInput.split(":");
    				ip = hello[0];
    				input = Integer.parseInt(hello[1]);
        			if (input < 0 || input > 65535)
        			{
        				statusLabel.setText("   Client connection failed!");
        				statusLabel.setVisible(true);
        			}
        			else {
        				setUpClient(ip + ":" + input);
        				rectButton.setEnabled(false);
                		ovalButton.setEnabled(false);
                		lineButton.setEnabled(false);
                		textButton.setEnabled(false);
                		colorButton.setEnabled(false);
                		text.setEnabled(false);
                		fontBox.setEnabled(false);
                		forwardButton.setEnabled(false);
                		backButton.setEnabled(false);
                		removeButton.setEnabled(false);
                		saveButton.setEnabled(false);
                		loadButton.setEnabled(false);
                		pictureButton.setEnabled(false);
                		serverButton.setEnabled(false);
                		clientButton.setEnabled(false);
                		statusLabel.setText("   Client Mode Enabled: Port " + input);
                		statusLabel.setVisible(true);
                		MouseListener[] mouseListeners = c.getMouseListeners();
                		for (MouseListener mouseListener : mouseListeners) {
                		    c.removeMouseListener(mouseListener);
                		}
                		MouseMotionListener[] mouseListeners2 = c.getMouseMotionListeners();
                		for (MouseMotionListener mouseMotionListener : mouseListeners2) {
                		    c.removeMouseMotionListener(mouseMotionListener);
                		}
        			}
    			}
    			catch(ConnectException|SocketTimeoutException|UnknownHostException e2)
    			{
    				statusLabel.setText("   Client connection failed!");
    				statusLabel.setVisible(true);
    				e2.printStackTrace();
    			} catch (IOException e1) {
					e1.printStackTrace();
				}
        	}
        });
        Box box6 = Box.createHorizontalBox();
        box6.add(sixthLabel);
        box6.add(serverButton);
        box6.add(clientButton);
        box6.add(statusLabel);
        
		Box finalBox = Box.createVerticalBox();
		finalBox.add(box);
		finalBox.add(Box.createVerticalStrut(10));
		finalBox.add(box2);
		finalBox.add(Box.createVerticalStrut(10));
		finalBox.add(box3);
		finalBox.add(Box.createVerticalStrut(10));
		finalBox.add(box4);
		finalBox.add(Box.createVerticalStrut(10));
		finalBox.add(box5);
		finalBox.add(Box.createVerticalStrut(10));
		finalBox.add(box6);
		finalBox.add(Box.createVerticalStrut(10));
		finalBox.add(scrollpane);
		for (Component comp:finalBox.getComponents())
		{
			((JComponent)comp).setAlignmentX(Box.LEFT_ALIGNMENT);
		}
		c.setTf(text);
		c.setCb(fontBox);
		model.setCanvas(c);
		model.setShapes(c.getShapes());
		c.setSt(model);
		setLayout(new BorderLayout());
		add(finalBox, BorderLayout.WEST);
		add(c, BorderLayout.CENTER);
	}
	public static void main(String[] args)
	{
		Whiteboard wh = new Whiteboard();
		wh.setTitle("Whiteboard");
		wh.setDefaultCloseOperation(EXIT_ON_CLOSE);
		wh.setSize(900,900);
		wh.pack();
		wh.setVisible(true);
	}
	public void setUpServer(int portNum)
    {
		server = new Server(portNum);
		server.setC(c);
        Thread serverThread = new Thread(server);
        serverThread.start();
    }
	public void setUpClient(String hostPort) throws IOException
    {
        try {
        	client = new Client(hostPort);
        	client.setC(c);
        	Thread clientThread = new Thread(client);
            clientThread.start();
        } 
        catch (ConnectException|SocketTimeoutException|UnknownHostException eee)
        {
        	throw eee;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
	public void addWhiteboardListener(DShapeModel model)
	{
		model.addModelListener(this);
	}
	public void modelChanged(DShapeModel model) {
		if(serverEnabled)
		{
			Server.Payload payload = server.packageData(model.getIndex(),"change",model);
			server.sendData(payload);
		}
	}
}
