package Serveur;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

import Command.AgentAction;
import Model.Maze;
import Model.PacmanGame;

public class ClientHandler implements Runnable{

	private Socket clientSocket;
	private DataInputStream in;
	private DataOutputStream out;

	private PacmanGame pacmanGame;
	private Maze maze;

	private boolean sendMaze;

	public ClientHandler(Socket clientSocket) {
		// TODO Auto-generated constructor stub
		try {
			this.clientSocket = clientSocket;
			
			//Retourne l'input stream du socket */
			this.in = new DataInputStream(this.clientSocket.getInputStream());
			
			//Retourne l'output stream du socket */
			this.out = new DataOutputStream(this.clientSocket.getOutputStream());

			this.pacmanGame = new PacmanGame();
			sendMaze = false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

		Thread ecoute = new Thread(new Runnable() {

			@Override
			public void run() {
				
				
				String map= "contestClassic.lay";
				

				while(true) {
					try {
						// Recupère l'action effectuée sur la commande 
						String action = in.readUTF();

						if(action.equals("MAP")) {
							
							
							System.out.println("MAP");

							
							sendMaze = false;
							
							// Recupère la map envoyée par l'utilisateur 
							 map = in.readUTF();

							try {
								pacmanGame.setMapName("layouts/" + map);
								pacmanGame.setLabyrinth(new Maze(pacmanGame.getMapName()));
								pacmanGame.init();

								ObjectOutputStream oos = new ObjectOutputStream(out);
								maze = pacmanGame.getLabyrinth();
								oos.writeObject(maze);	
								
							} catch (EOFException e) {
								e.printStackTrace();
							}catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						if(action.equals("RESTART")) {
							
							System.out.println("BOUTON RESTART");

							sendMaze = false;
							
							try {
								pacmanGame.setMapName("layouts/" + map);
								pacmanGame.setLabyrinth(new Maze(pacmanGame.getMapName()));
								pacmanGame.init();

								ObjectOutputStream oos = new ObjectOutputStream(out);
								maze = pacmanGame.getLabyrinth();
								oos.writeObject(maze);	
								
							} catch (EOFException e) {
								e.printStackTrace();
							}catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
							
						}else if(action.equals("START")) {
							
							System.out.println("START");
							sendMaze = true;
							pacmanGame.start();
							pacmanGame.launch();
							
						}else if(action.equals("STEP")) {
							
							System.out.println("STEP");
							
							pacmanGame.step();
							ObjectOutputStream oos = new ObjectOutputStream(out);
							maze = pacmanGame.getLabyrinth();
							oos.writeObject(maze);	
							
						}else if(action.equals("PAUSE")) {
							System.out.println("PAUSE");
							sendMaze = false;
							pacmanGame.stop();
							
						}else if(action.equals("EXIT")) {
							Serveur.quitterServeur();
							break;
							
						}else if(action.equals("LEFT")) {
							pacmanGame.getJoueur1().setAgentAction(new AgentAction(Maze.WEST));
							
						}else if(action.equals("DOWN")) {
							pacmanGame.getJoueur1().setAgentAction(new AgentAction(Maze.SOUTH));
							
						}else if(action.equals("RIGHT")) {
							pacmanGame.getJoueur1().setAgentAction(new AgentAction(Maze.EAST));
							
						}else if(action.equals("UP")) {
							pacmanGame.getJoueur1().setAgentAction(new AgentAction(Maze.NORTH));
						}
						
					}catch(SocketException e) {
						System.out.println("Client disconnected");
						Serveur.quitterServeur();
						break;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						break;
					} 
				}

			}});



		Thread envoi = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				while(true) {
					try {
						synchronized (pacmanGame.getLock()) {
							pacmanGame.getLock().wait();

							ObjectOutputStream oos = new ObjectOutputStream(out);
							maze = pacmanGame.getLabyrinth();

							if(pacmanGame.isGhostScarred()) {
								maze.setScarred(true);
							}else{
								maze.setScarred(false);
							}

							oos.writeObject(maze);
						}
					}catch(SocketException e) {
						System.out.println("Client Disconnected");
						break;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						break;
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
				}

			}});

		ecoute.start();
		envoi.start();
	}

	public Socket getClientSocket() {
		return clientSocket;
	}

	public void setClientSocket(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}
}


