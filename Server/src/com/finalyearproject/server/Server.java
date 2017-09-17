package com.finalyearproject.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.finalyearproject.gui.Main;

public class Server implements Runnable {
	public ServerSocket serverSocket;
	Socket server;
	DataInputStream in;
	DataOutputStream out;
	Thread t;
	int port;
	Main frame;

	public Server(Main frame, int port) throws IOException {
		this.frame = frame;
		this.port = port;
		serverSocket = new ServerSocket(port);
		serverSocket.setSoTimeout(10000000);
		t = new Thread(this);
		t.start();
	}

	public void writeData(String data) throws IOException {
		out.writeUTF(data);
	}

	public String readData() throws IOException {
		return in.readUTF();
	}

	@Override
	public void run() {
		try {
			System.out.println("Listening to .. " + port);
			frame.startedservertext.setText("Server started on " + port);
			frame.connectionStatusText.setText("You are not connected to any of the client yet");

			server = serverSocket.accept();
			in = new DataInputStream(server.getInputStream());
			out = new DataOutputStream(server.getOutputStream());
			System.out.println("Connected to " + server.getRemoteSocketAddress());
			frame.connectionStatusText.setText("Communicating with " + server.getRemoteSocketAddress());
			frame.clientDetailsText.setText("<html>Successfully established a communication channel <br/>with "
					+ server.getRemoteSocketAddress() + "</html>");

			String msgReceived = "";
			while (!msgReceived.equals("STOP")) {
				msgReceived = readData();
				if (!msgReceived.equals("STOP")) {
					if (frame.ecc != null && frame.b1 != null && frame.b2 != null) {
						frame.btnDecrypt.setEnabled(true);
					}
					
					frame.currentMessage.setText(msgReceived);
					frame.replyMessage.setEnabled(true);
					frame.btnReply.setEnabled(true);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			frame.startedservertext.setText("<html>Error while starting server on port. <br/>" + e + "</html>");
			frame.serverPort.setEnabled(true);
			frame.serverstart.setEnabled(true);
		}

	}
}
