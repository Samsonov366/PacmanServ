package Serveur;
import java.io.DataInputStream;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;



import javax.print.attribute.standard.PrinterLocation;


public class Serveur {

	private ServerSocket serverSocket;
	private Socket clientSocket;
	private DataInputStream in;
	private DataOutputStream out;
	public static ArrayList<ClientHandler> handlers;
	boolean connectionOk;
	Connection connexion = null;
	Statement statement = null;
	ResultSet resultat = null;
	
	public static final int port = 2000;  


	public Serveur() {
		try {
			
			// Créé un serveur socket avec le port spécifié
			this.serverSocket = new ServerSocket(port);
			System.out.println("Server OK.");
			this.handlers = new ArrayList<>();
		} catch (BindException e) {
			System.out.println("Port déjà pris");

			
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void jeu() {
		System.out.println("Waiting for clients...");

		while(true) {
			try {
				this.clientSocket = this.serverSocket.accept();
				System.out.println("Connection accepted !");

				// Retourne l'input stream du socket 
				this.in = new DataInputStream(this.clientSocket.getInputStream());
				
				// Retourne l'output stream du socket 
				this.out = new DataOutputStream(this.clientSocket.getOutputStream());

				this.out.writeUTF("Client connected");

					//Envoi connectionOk
					this.out.writeBoolean(true);

					ClientHandler ch = new ClientHandler(this.clientSocket);

					Thread t = new Thread(ch);
					t.start();

					handlers.add(ch);
				

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
	}

	public static void quitterServeur() {
		for(ClientHandler ch : handlers) {
			if(ch.getClientSocket() == null) {
				handlers.remove(ch);
			}
		}
	}
}
