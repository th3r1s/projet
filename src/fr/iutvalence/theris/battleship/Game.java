package fr.iutvalence.theris.battleship;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import fr.iutvalence.theris.battleship.boats.AircraftCarrier;
import fr.iutvalence.theris.battleship.boats.Battleship;
import fr.iutvalence.theris.battleship.boats.Boat;
import fr.iutvalence.theris.battleship.boats.Cruiser;
import fr.iutvalence.theris.battleship.boats.Destroyer;
import fr.iutvalence.theris.battleship.boats.Submarine;

/**
 * @author Robin
 * Une partie
 */
public class Game {

	/**
	 * Plateau du joueur 1
	 */
	private Board boardPlayer1;
	
	/**
	 * Plateau du joueur 2
	 */
	private Board boardPlayer2;
	
	/**
	 * Joueur 1
	 */
	private Player player1;
	
	/**
	 * Joueur 2
	 */
	private Player player2;
	
	/**
	 * Nombre de tours, permet de savoir qui doit jouer
	 */
	private int turn;
	
	private List<Boat> player1Boats;
	private List<Boat> player2Boats;
	
	/**
	 * Constructeur d'une partie.
	 * 
	 * @param player1 TODO
	 * @param player2 TODO2
	 */
	public Game(Player player1, Player player2) {
		this.player1 = player1;
		this.player2 = player2;
		
		boardPlayer1 = new Board();
		boardPlayer2 = new Board();
		
		player1Boats = new ArrayList<Boat>();
		player2Boats = new ArrayList<Boat>();
		
		turn = 0;
	}
	
	/**
	 * M�thode d'une partie jou�e
	 */
	public void play(){
		Scanner scan  = new Scanner(System.in);
		init(scan);
		Tuple<Boolean, String> tuple;
		while(!(tuple = end()).getFirstElement()){	
			Player currentPlayer = (turn%2 == 0 ? player1 : player2);
			Board currentBoard = (turn%2 == 0 ? boardPlayer1 : boardPlayer2);
			Board currentFoeBoard = (turn%2 == 1 ? boardPlayer1 : boardPlayer2);
			
			System.out.println("Sur quelle ligne tirer");
			int row = scan.nextInt();
			System.out.println("Sur quelle colonne tirer ?");
			int col = scan.nextInt();
			Location location;
			if(col > -1 && col < currentBoard.getColumns() && row > -1 && row < currentBoard.getRows())
				location = new Location(row, col);
			else{
				System.out.println("Position en dehors du tableau");
				continue;
			}
			
			(currentPlayer.equals(player1) ? boardPlayer2 : boardPlayer1).hit(location);
			turn++;
			System.out.println(currentPlayer.getNickname());
			System.out.println();
			System.out.println(currentBoard.toString(false));
			System.out.println(currentFoeBoard.toString(true));			
		}
		System.out.println(tuple.getSecondElement() + " a perdu la partie");
		scan.close();
	}

	/**
	 * Initialisation d'une partie : pose des bateaux
	 */
	public void init(Scanner scan) {
		
		while (turn != 10){
			Player currentPlayer = (turn%2 == 0 ? player1 : player2);
			Board currentBoard = (turn%2 == 0 ? boardPlayer1 : boardPlayer2);
			List<Boat> currentList = (turn%2 == 0 ? player1Boats : player2Boats);
			
			System.out.println(currentPlayer.getNickname() + " joue.");
			
			System.out.println("Ligne o� poser le bateau");
			int row = scan.nextInt();
			System.out.println("Colonne o� poser le bateau");
			int col = scan.nextInt();
			String dummy = scan.nextLine();
			Location location;
			if (col > -1 && col < currentBoard.getColumns() && row > -1
					&& row < currentBoard.getRows())
				location = new Location(row, col);
			else {
				System.out.println("Position en dehors du tableau");
				continue;
			}

			System.out.println("Direction du bateau {NS, SN, WE, EW}");
			Direction direction;
			try {
				direction = Direction.valueOf(scan.nextLine().toUpperCase().trim());
			} catch (IllegalArgumentException e) {
				System.out.println("Mauvaise direction");
				continue;
			}
			
			System.out
					.println("Type de bateau {Aircraft, BattleShip, Cruiser, Destroyer, Submarine}");
			String type = scan.nextLine();
			Boat boat;

			switch (type.toLowerCase().trim()) {
			case "destroyer":
				boat = new Destroyer();
				break;
			case "cruiser":
				boat = new Cruiser();
				break;
			case "aircraft":
				boat = new AircraftCarrier();
				break;
			case "submarine":
				boat = new Submarine();
				break;
			case "battleship":
				boat = new Battleship();
				break;
			default:
				System.err.println("Mauvais choix de bateau");
				continue;
			}
			
			boolean validBoats = true;
			for(Boat b : currentList){
				if(b.getClass().getName().equalsIgnoreCase(boat.getClass().getName())){
					System.err.println("Le bateau de ce type est d�j� pr�sent sur le plateau");
					validBoats = false;
					break;
				}	
			}
			if (!validBoats) continue;
			
			// TODO Ne doit pas cr�er de bateaux sur d'autres bateaux => � faire dans Board.createBoat
			
			if(currentBoard.createBoat(direction, location,(Boat) boat)){
				turn++;
				currentList.add(boat);
				System.out.println(currentBoard.toString(false));
			}
			
		}
	}
	
	/**
	 * @return
	 * Fin d'une partie
	 */
	public Tuple<Boolean, String> end(){
		
		Tuple<Boolean, String> tuple = new Tuple<Boolean, String>(false, "");
		
		boolean b1 = false;
		for(Boat boat : player1Boats){
			if(!boat.isSinked())
				b1 = true;
		}
		
		boolean b2 = false;	
		for(Boat boat : player2Boats){
			if(!boat.isSinked())
				b2 = true;
		}
		
		if(!b1 || !b2){
			tuple.setFirstElement(true);
			if(b1)
				tuple.setSecondElement("Joueur 2");
			if(b2)
				tuple.setSecondElement("Joueur 1");
		}
		return tuple;
	}

}
