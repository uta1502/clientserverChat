import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.JFrame;

class Server extends JFrame{
	private JTextField userText;
	private JTextArea chatWindow;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private ServerSocket server;
	private Socket connection;
	
	 Server(){
		super("Utkarsh's Instant messenger!! ");
		userText = new JTextField();
		userText.setEditable(false);
		userText.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event){
			sendMessage(event.getActionCommand());	
			userText.setText("");
			}
		});
		add(userText,BorderLayout.NORTH);
		chatWindow = new JTextArea();
		add(new JScrollPane(chatWindow));
		setSize(300,150);
		setVisible(true);
	}
	
	public void startRunning(){
		try{
			server = new ServerSocket(6789,100);
			while(true){
				try{
					waitForConnection();
					setupStreams();
					whileChatting();
					
				}catch(EOFException bleh){
					showMessage("\n Server ended the connection!!");
				}finally{
					closeCrap();
				}
			}
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	private void waitForConnection() throws IOException{
		showMessage("Waiting for someone to connect.. \n");
		connection = server.accept();
		showMessage("Now connected to " + connection.getInetAddress().getHostName());
		
	}
	
	private void setupStreams() throws IOException{
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		showMessage("\n Streams are now setup!! ");
	}
	
	private void whileChatting() throws IOException{
		String message = "You are now Connected!!";
		sendMessage(message);
		ableToType(true);
		do{
			try{
				message = (String) input.readObject();
				showMessage("\n "+ message);
			}catch(ClassNotFoundException poop){
				showMessage("\n idk wtf that user sent !!");
			}
			
		}while(!message.equals("CLIENT - END"));
	}
	
	private void closeCrap(){
		showMessage("\n Closing Connection.... \n");
		ableToType(false);
		try{
			output.close();
			input.close();
			connection.close();
		}catch(IOException fart){
			fart.printStackTrace();
		}
	}
	
	private void sendMessage(String message){
		try{
			output.writeObject("SERVER - "+message);
			output.flush();
			showMessage("\n SEVER - "+ message);
		}catch(Exception ass){
			chatWindow.append("\n ERROR: DUDE I CAN'T SEND THAT MESSAGE!!!!");
			
		}
	}
	
	private void showMessage(final String text){
		SwingUtilities.invokeLater(
				new Runnable(){
					public void run(){
						chatWindow.append(text);
					}
				}
				);
	}
	
	private void ableToType(final boolean tof){
		SwingUtilities.invokeLater(
				new Runnable(){
					public void run(){
						userText.setEditable(tof);
						
					}
				}
					);
	}
}

public class chatServer {
	public static void main(String[] args) {
		Server sally = new Server();
		sally.startRunning();
		sally.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		sally.startRunning();
	}
}
