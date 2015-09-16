import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


class client extends JFrame{
	private JTextField userText;
	private JTextArea chatWindow;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private String message = "";
	private String serverIP;
	private Socket connection;
	
	client(String host){
		super("I AM SO COOL!!! :D");
		serverIP = host;
		userText = new JTextField();
		userText.setEditable(false);
		userText.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				sendMessage(e.getActionCommand());
				userText.setText("");
			}
		});
		
		add(userText,BorderLayout.NORTH);
		chatWindow = new JTextArea();
		add(new JScrollPane(chatWindow),BorderLayout.CENTER);
		setSize(300,150);
		setVisible(true);
		
	}
	
	void startRunning(){
		try{
			connectToServer();
			setupStreams();
			whileChatting();
		}catch(EOFException bleh){
			showMessage("\n Client Terminated Connection");
		}catch(IOException blehbleh){
			blehbleh.printStackTrace();
		}finally{
			closeCrap();
		}
	}
	
	private void connectToServer() throws IOException{
		showMessage("Attempting connection ... \n");
		connection = new Socket(InetAddress.getByName(serverIP),6789);
		showMessage("Connected to: "+connection.getInetAddress().getHostName());
	}
	
	private void setupStreams() throws IOException{
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		showMessage("\n Dude your streams are now good to go!!! \n");
	}
	
	private void whileChatting() throws IOException{
		ableToType(true);
		do{
			try{
				message = (String) input.readObject();
				showMessage("\n "+message);
			}catch(ClassNotFoundException butt){
				showMessage("\n i dont know that object type!! ");
			}
		}while(!message.equals("SERVER - END"));
	}
	
	private void closeCrap(){
		showMessage("\n closing crap down!! ");
		ableToType(false);
		
		try{
			output.close();
			input.close();
			connection.close();
		}catch(IOException fml){
			fml.printStackTrace();
		}
	}
	
	private void sendMessage(String message){
		try{
			output.writeObject("CLIENT -"+message);
			output.flush();
			showMessage("\n CLIENT - "+ message);
			
		}catch(IOException butt){
			chatWindow.append("\n something messed up sending message hoss");
		}
	}
	
	private void showMessage(final String m){
		SwingUtilities.invokeLater(
				new Runnable(){
					public void run(){
						chatWindow.append(m);
					}
				});
	}
	
	private void ableToType(final boolean tof){
		SwingUtilities.invokeLater(
				new Runnable(){
					public void run(){
						userText.setEditable(tof);
					}
				});
	}
}

public class chatClient{
	public static void main(String[] args) {
		client charlie = new client("127.0.0.1");
		charlie.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		charlie.startRunning();
		
		
	}
}
