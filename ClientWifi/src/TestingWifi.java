import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JFrame;

public class TestingWifi {

	/**
	 * @param args
	 */ 
	private static Socket socket;
    
	private static final int SERVERPORT = 6000;
	private static final String SERVER_IP = "192.168.1.73";
	static ServerBoard frame;

	public static void main(String[] args) {

		new Thread(new ClientThread()).start();

		// opens the window where the messages will be received and sent
		frame = new ServerBoard();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);  

	}

	public static void sendMessage(String str) {
		if (socket != null) {
			try {
				PrintWriter out = new PrintWriter(new BufferedWriter(
						new OutputStreamWriter(socket.getOutputStream())), true);
				out.println(str);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	static class ClientThread implements Runnable {

		@Override
		public void run() {

			try {
				InetAddress serverAddr = InetAddress.getByName(SERVER_IP);
				socket = new Socket(serverAddr, SERVERPORT);
				
				
				BufferedReader input = new BufferedReader(new InputStreamReader(
						socket.getInputStream()));
				
				while(true){
				String read = input.readLine();
				if (read != null)
					frame.addText(read);
				}
			} catch (UnknownHostException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

		}
	}

}
