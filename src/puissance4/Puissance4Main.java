package puissance4;

import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class Puissance4Main {

	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
		Scanner scanner = new Scanner(System.in);
		String choix = "";
		String difficulte = "";
		String modeDeJeu = "";
		boolean partieChargee = false;
		System.out.println("Voulez-vous charger une partie ? \n -o pour oui \n -n pour non");
		choix = scanner.nextLine();
		if (!"o".equals(choix) && !"n".equals(choix)) {
			while (!"o".equals(choix) && !"n".equals(choix)) {
				System.out.println(
						"La valeur indiquée n'est pas valide. Voulez-vous charger une partie ? \n -o pour oui \n -n pour non");
				choix = scanner.nextLine();
			}
		}

		if (choix.equals("o")) {
			partieChargee = true;
			modeDeJeu = accederSauvegardeMode();
		} else {
			System.out.println(
					"Voulez-vous jouer contre un joueur ou l'ordinateur ? \n -j pour joueur contre joueur  \n -f pour ordinateur contre joueur difficulté facile \n -i pour ordinateur contre joueur difficulté intermédiaire \n -d pour ordinateur contre joueur difficulté difficile");
			modeDeJeu = scanner.nextLine();
			if (!"j".equals(modeDeJeu) && !"f".equals(modeDeJeu) && !"i".equals(modeDeJeu) && !"d".equals(modeDeJeu)) {
				while (!"j".equals(modeDeJeu) && !"f".equals(modeDeJeu) && !"i".equals(modeDeJeu)
						&& !"d".equals(modeDeJeu)) {
					System.out.println(
							"Le mode de jeu sélectionné n'est pas valide. \n -j pour joueur contre joueur  \n -f pour ordinateur contre joueur difficulté facile \n -i pour ordinateur contre joueur difficulté intermédiaire \n -d pour ordinateur contre joueur difficulté difficile");
					modeDeJeu = scanner.nextLine();
				}
			}
		}

		if ("j".equals(modeDeJeu)) {
			joueurContreJoueur(partieChargee);
		} else if ("f".equals(modeDeJeu)) {
			joueurContreOrdinateurFacile(partieChargee);
		} else if ("i".equals(modeDeJeu)) {
			joueurContreOrdinateurIntermediaire(partieChargee);
		} else if ("d".equals(modeDeJeu)) {
			joueurContreOrdinateurDifficile(partieChargee);
		}
	}

	/**
	 * Procédure permettant de lancer une partie en mode joueur contre ordinateur de
	 * diffculté difficile en implémentant l'algorithme MinMax
	 *
	 * @param partieChargee : booléen indiquant si l'on veut charger une partie ou
	 *                      non
	 * @throws UnsupportedEncodingException
	 * @throws FileNotFoundException
	 */

	public static void joueurContreOrdinateurDifficile(boolean partieChargee)
			throws FileNotFoundException, UnsupportedEncodingException {
		Scanner scanner = new Scanner(System.in);
		creerSauvegardeMode("d");
		int tour = 0;
		char joueur = '1';
		boolean gagnant = false;

		char[][] grilleJeu = new char[6][7];
		if (partieChargee) {
			grilleJeu = accederSauvegardeGrille();
			tour = accederSauvegardeTour();
			joueur = accederSauvegardeJoueur();
		} else {
			// on initialise la grille
			for (int ligne = 0; ligne < grilleJeu.length; ligne++) {
				for (int col = 0; col < grilleJeu[0].length; col++) {
					grilleJeu[ligne][col] = ' ';
				}
			}
		}
		// début d'un tour joué, on commence une boucle qui se ferme quand il y a un
		// gagnant ou que 42 tours sont joués (nombre de cases du tableau)
		while (gagnant == false && tour <= 42) {
			creerSauvegardeGrille(grilleJeu);
			creerSauvegardeJoueur(joueur);
			creerSauvegardeTour(tour);
			int colonneSelectionnee;
			ArrayList<Integer> actionsTour = new ArrayList<Integer>();
			actionsTour = colonnesJouables(grilleJeu);

			System.out.println();
			afficherGrille(grilleJeu);
			if (joueur == '1') {
				System.out.print("Choisissez une colonne : ");
				colonneSelectionnee = scanner.nextInt();
				if (!actionsTour.contains(colonneSelectionnee)) {
					while (!actionsTour.contains(colonneSelectionnee)) {
						System.out.print("Le joueur " + joueur
								+ " a choisi une colonne non valide, choisissez une nouvelle colonne entre 0 et 6 (compris) : ");
						colonneSelectionnee = scanner.nextInt();
					}
				}
			} else {
				System.out.println("Appuyez sur entrée pour faire jouer l'ordinateur : ");
				scanner.nextLine();
				scanner.nextLine();
				// on déclare un tableau de 2 index qui va recevoir la colonne correspondant au
				// meilleur coup possible pour ce tour et le score correspondant
				int minmax[] = minMaxAlgorithme(grilleJeu, 6, true);
				colonneSelectionnee = minmax[0];
				// System.out.println("score : " + minmax[1]);
			}

			// on fait tomber le pion
			faireTomberPion(grilleJeu, colonneSelectionnee, joueur);

			// on détermine s'il y a un vainqueur
			gagnant = estGagnant(joueur, grilleJeu);

			// on change de joueur après qu'une colonne soit sélectionnée
			if (joueur == '1') {
				joueur = '2';
			} else {
				joueur = '1';
			}

			tour++;
		}

		afficherGrille(grilleJeu);

		if (gagnant) {
			if (joueur == '2') {
				System.out.println("Le joueur a gagné");
			} else {
				System.out.println("L'ordinateur a gagné");
			}
		} else {
			System.out.println("Egalité");
		}
	}

	/**
	 * Procédure permettant de jouer contre l'ordinateur en mode intermédiaire :
	 * l'ordinateur va examiner les grilles
	 * 
	 * @param partieChargee : booléen indiquant si l'on veut charger une partie ou
	 *                      non
	 * @throws UnsupportedEncodingException
	 * @throws FileNotFoundException
	 */

	public static void joueurContreOrdinateurIntermediaire(boolean partieChargee)
			throws FileNotFoundException, UnsupportedEncodingException {
		Scanner scanner = new Scanner(System.in);

		creerSauvegardeMode("i");

		int tour = 0;
		char joueur = '1';
		boolean gagnant = false;

		char[][] grilleJeu = new char[6][7];
		if (partieChargee) {
			grilleJeu = accederSauvegardeGrille();
			tour = accederSauvegardeTour();
			joueur = accederSauvegardeJoueur();
		} else {
			// on initialise la grille
			for (int ligne = 0; ligne < grilleJeu.length; ligne++) {
				for (int col = 0; col < grilleJeu[0].length; col++) {
					grilleJeu[ligne][col] = ' ';
				}
			}
		}

		// début d'un tour joué, on commence une boucle qui se ferme quand il y a un
		// gagnant ou que 42 tours sont joués (nombre de cases du tableau)
		while (gagnant == false && tour <= 42) {
			creerSauvegardeGrille(grilleJeu);
			creerSauvegardeJoueur(joueur);
			creerSauvegardeTour(tour);
			int colonneSelectionnee;
			// on crée une collection des actions faisables ce tour
			ArrayList<Integer> actionsTour = new ArrayList<Integer>();
			actionsTour = colonnesJouables(grilleJeu);

			System.out.println();
			// on affiche la grille
			afficherGrille(grilleJeu);
			// si le joueur joue
			if (joueur == '1') {
				// on lui demande la colonne dans laquelle il veut jouer
				System.out.print("Choisissez une colonne : ");
				colonneSelectionnee = scanner.nextInt();
				// si la colonne qu'il sélectionne n'est pas contenue dans les actions
				if (!actionsTour.contains(colonneSelectionnee)) {
					while (!actionsTour.contains(colonneSelectionnee)) {
						System.out.print("Le joueur " + joueur
								+ " a choisi une colonne non valide, choisissez une nouvelle colonne entre 0 et 6 (compris) : ");
						colonneSelectionnee = scanner.nextInt();
					}
				}
			} else {
				System.out.println("Appuyez sur entrée pour faire jouer l'ordinateur : ");
				scanner.nextLine();
				scanner.nextLine();
				// on appelle la fonction meilleurCoup pour sélectionner la meilleure colonne
				// possible ce tourf
				colonneSelectionnee = meilleurCoup(grilleJeu, joueur);
			}

			// on fait tomber le pion
			faireTomberPion(grilleJeu, colonneSelectionnee, joueur);

			// on détermine s'il y a un vainqueur
			gagnant = estGagnant(joueur, grilleJeu);

			// on change de joueur après qu'une colonne soit sélectionnée
			if (joueur == '1') {
				joueur = '2';
			} else {
				joueur = '1';
			}

			tour++;
		}

		afficherGrille(grilleJeu);

		if (gagnant) {
			if (joueur == '2') {
				System.out.println("Le joueur a gagné");
			} else {
				System.out.println("L'ordinateur a gagné");
			}
		} else {
			System.out.println("Egalité");
		}
	}

	/**
	 * Procédure permettant de lancer une partie en mode joueur contre ordinateur de
	 * diffculté facile
	 * 
	 * @param partieChargee : booléen indiquant si l'on veut charger une partie ou
	 *                      non
	 * @throws UnsupportedEncodingException
	 * @throws FileNotFoundException
	 */

	public static void joueurContreOrdinateurFacile(boolean partieChargee)
			throws FileNotFoundException, UnsupportedEncodingException {
		Scanner scanner = new Scanner(System.in);

		creerSauvegardeMode("f");

		int tour = 0;
		char joueur = '1';
		boolean gagnant = false;

		char[][] grilleJeu = new char[6][7];
		if (partieChargee) {
			grilleJeu = accederSauvegardeGrille();
			tour = accederSauvegardeTour();
			joueur = accederSauvegardeJoueur();
		} else {
			// on initialise la grille
			for (int ligne = 0; ligne < grilleJeu.length; ligne++) {
				for (int col = 0; col < grilleJeu[0].length; col++) {
					grilleJeu[ligne][col] = ' ';
				}
			}
		}

		// début d'un tour joué, on commence une boucle qui se ferme quand il y a un
		// gagnant ou que 42 tours sont joués (nombre de cases du tableau)
		while (gagnant == false && tour <= 42) {
			creerSauvegardeGrille(grilleJeu);
			creerSauvegardeJoueur(joueur);
			creerSauvegardeTour(tour);
			int colonneSelectionnee;
			ArrayList<Integer> actionsTour = new ArrayList<Integer>();
			actionsTour = colonnesJouables(grilleJeu);

			System.out.println();
			afficherGrille(grilleJeu);
			if (joueur == '1') {
				System.out.print("Choisissez une colonne : ");
				colonneSelectionnee = scanner.nextInt();
				if (!actionsTour.contains(colonneSelectionnee)) {
					while (!actionsTour.contains(colonneSelectionnee)) {
						System.out.print("Le joueur " + joueur
								+ " a choisi une colonne non valide, choisissez une nouvelle colonne entre 0 et 6 (compris) : ");
						colonneSelectionnee = scanner.nextInt();
					}
				}
			} else {
				System.out.println("Appuyez sur entrée pour faire jouer l'ordinateur : ");
				scanner.nextLine();
				scanner.nextLine();
				int indexActionsTour = (int) (Math.random() * actionsTour.size());
				colonneSelectionnee = actionsTour.get(indexActionsTour);
			}
			
			if(joueur == '1') {
				//input joueur
			} else {
				//input ordinateur
			}

			// on fait tomber le pion
			faireTomberPion(grilleJeu, colonneSelectionnee, joueur);

			// on détermine s'il y a un vainqueur
			gagnant = estGagnant(joueur, grilleJeu);

			// on change de joueur après qu'une colonne soit sélectionnée
			if (joueur == '1') {
				joueur = '2';
			} else {
				joueur = '1';
			}

			tour++;
		}
		afficherGrille(grilleJeu);

		if (gagnant) {
			if (joueur == '2') {
				System.out.println("Le joueur a gagné");
			} else {
				System.out.println("L'ordinateur a gagné");
			}
		} else {
			System.out.println("Egalité");
		}
	}

	/**
	 * Procédure permettant de lancer une partie en mode joueur contre joueur
	 * 
	 * @param partieChargee : booléen indiquant si l'on veut charger une partie ou
	 *                      non
	 * @throws UnsupportedEncodingException
	 * @throws FileNotFoundException
	 */

	public static void joueurContreJoueur(boolean partieChargee)
			throws FileNotFoundException, UnsupportedEncodingException {

		Scanner scanner = new Scanner(System.in);
		creerSauvegardeMode("j");

		int tour = 0;
		char joueur = '1';
		boolean gagnant = false;

		char[][] grilleJeu = new char[6][7];
		if (partieChargee) {
			grilleJeu = accederSauvegardeGrille();
			tour = accederSauvegardeTour();
			joueur = accederSauvegardeJoueur();
		} else {
			// on initialise la grille
			for (int ligne = 0; ligne < grilleJeu.length; ligne++) {
				for (int col = 0; col < grilleJeu[0].length; col++) {
					grilleJeu[ligne][col] = ' ';
				}
			}
		}

		// début d'un tour joué, on commence une boucle qui se ferme quand il y a un
		// gagnant ou que 42 tours sont joués (nombre de cases du tableau)
		while (gagnant == false && tour <= 42) {
			creerSauvegardeGrille(grilleJeu);
			creerSauvegardeJoueur(joueur);
			creerSauvegardeTour(tour);
			int colonneSelectionnee;
			ArrayList<Integer> actionsTour = new ArrayList<Integer>();
			actionsTour = colonnesJouables(grilleJeu);

			System.out.println();
			afficherGrille(grilleJeu);

			System.out.print("Joueur " + joueur + ", choisissez une colonne : ");
			colonneSelectionnee = scanner.nextInt();

			if (!actionsTour.contains(colonneSelectionnee)) {
				while (!actionsTour.contains(colonneSelectionnee)) {
					System.out.print("Le joueur " + joueur
							+ " a choisi une colonne non valide, choisissez une nouvelle colonne entre 0 et 6 (compris) : ");
					colonneSelectionnee = scanner.nextInt();
				}
			}

			// on fait tomber le pion
			faireTomberPion(grilleJeu, colonneSelectionnee, joueur);

			// on détermine s'il y a un vainqueur
			gagnant = estGagnant(joueur, grilleJeu);

			// on change de joueur après qu'une colonne soit sélectionnée
			if (joueur == '1') {
				joueur = '2';
			} else {
				joueur = '1';
			}

			tour++;
		}
		afficherGrille(grilleJeu);

		if (gagnant) {
			if (joueur == '2') {
				System.out.println("Le joueur 1 a gagné");
			} else {
				System.out.println("Le joueur 2 a gagné");
			}
		} else {
			System.out.println("Egalité");
		}
	}

	/**
	 * procédure qui permet d'afficher la grille de jeu
	 *
	 * @param : tableau de caractères qui va permettre d'afficher la grille
	 */

	public static void afficherGrille(char[][] grille) {
		System.out.println(" 0 1 2 3 4 5 6");
		System.out.println("---------------");

		// boucle qui parcourt les lignes
		for (int ligne = 0; ligne < grille.length; ligne++) {
			System.out.print("|");

			// boucle qui parcourt les colonnes.
			for (int col = 0; col < grille[0].length; col++) {
				System.out.print(grille[ligne][col]);
				System.out.print("|");
			}
			System.out.println();
			System.out.println("---------------");
		}
		System.out.println(" 0 1 2 3 4 5 6");
		System.out.println();
	}

	/**
	 * fonction permettant de vérifier s'il y un vainqueur dans la grille en
	 * vérifiant si 4 pions sont alignés verticalement, horizontalement ou
	 * diagonalement (haut et bas)
	 * 
	 * @param joueur : caractère representant le joueur pour lequel on veut vérifier
	 *               s'il a gagné ou non
	 * @param grille : tableau 2D de caractères représentant la grille de jeu
	 * @return booléen indiquant si le joueur donné en paramètre a gagné ou non
	 */

	public static boolean estGagnant(char joueur, char[][] grille) {
		// on vérifie si 4 pions sont alignés horizontalement
		for (int ligne = 0; ligne < grille.length; ligne++) {
			for (int col = 0; col < grille[0].length - 3; col++) {
				if (grille[ligne][col] == joueur && grille[ligne][col + 1] == joueur && grille[ligne][col + 2] == joueur
						&& grille[ligne][col + 3] == joueur) {
					return true;
				}
			}
		}
		// on vérifie si 4 pions sont alignés verticalement
		for (int ligne = 0; ligne < grille.length - 3; ligne++) {
			for (int col = 0; col < grille[0].length; col++) {
				if (grille[ligne][col] == joueur && grille[ligne + 1][col] == joueur && grille[ligne + 2][col] == joueur
						&& grille[ligne + 3][col] == joueur) {
					return true;
				}
			}
		}
		// on vérifie si 4 pions sont alignés diagonalement vers le haut
		for (int ligne = 3; ligne < grille.length; ligne++) {
			for (int col = 0; col < grille[0].length - 3; col++) {
				if (grille[ligne][col] == joueur && grille[ligne - 1][col + 1] == joueur
						&& grille[ligne - 2][col + 2] == joueur && grille[ligne - 3][col + 3] == joueur) {
					return true;
				}
			}
		}
		// on vérifie si 4 pions sont alignés diagonalement vers le bas
		for (int ligne = 0; ligne < grille.length - 3; ligne++) {
			for (int col = 0; col < grille[0].length - 3; col++) {
				if (grille[ligne][col] == joueur && grille[ligne + 1][col + 1] == joueur
						&& grille[ligne + 2][col + 2] == joueur && grille[ligne + 3][col + 3] == joueur) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * fonction permettant de retourner un entier représentant un score en fonction
	 * du nombre de caractères contenus dans une collection de caractères qui
	 * représente une suite de 4 cases de la grille (horizontale, verticale ou
	 * diagonale)
	 * 
	 * @param listeCases : collection de 4 caractères représetant une suite de 4
	 *                   cases verticale, horizontale ou diagonale de la grille
	 * @param joueur     : caractère représentant le joueur pour lequel on veut
	 *                   retourner le score
	 * @return entier représentant un score pour une suite de 4 cases de la grille
	 */

	public static int evaluation4Cases(ArrayList<Character> listeCases, char joueur) {
		// on initialise le score et le nombre de case que l'on va compter pour le
		// joueur que l'on a entré en paramètre, le joueur opposé et le nombre de cases
		// vides
		int score = 0;
		int nCasesJoueur = 0;
		int nCasesJoueurOpposition = 0;
		int nCasesVides = 0;

		// on initialise une variable joueur opposé en fonction du joueur que l'on a
		// entré en paramètre
		char joueurOpposition = '1';
		if (joueur == '1') {
			joueurOpposition = '2';
		}

		// on parcourt la collection de caratères pour compter le nombre de cases par
		// joueur et les cases vides
		for (char c : listeCases) {
			if (c == joueur) {
				nCasesJoueur++;
			} else {
				if (c == joueurOpposition) {
					nCasesJoueurOpposition++;
				} else {
					if (c == ' ') {
						nCasesVides++;
					}
				}
			}
		}

		// on donne un score en fonction du nombre de cases compté

		// si on compte 4 cases pour le joueur entré en paramètre on incrémente le score
		// avec une grande valeur (coup gagnant)
		if (nCasesJoueur == 4) {
			score += 100;
			// si on compte 3 cases pour le joueur entré en paramètre et une case vide on
			// incrémente le score avec une plus petite valeur
		} else if (nCasesJoueur == 3 && nCasesVides == 1) {
			score += 5;
			// si on compte 2 cases pour le joueur entré en paramètre et 2 cases vides avec
			// une encore plus petite valeur
		} else if (nCasesJoueur == 2 && nCasesVides == 2) {
			score += 2;
			// si on compte 3 cases pour le joueur entré en paramètre et 1 cases vide on
			// soustrait une relativement grande valeur au score (le joueur adverse va
			// gagner)
		} else if (nCasesJoueurOpposition == 3 && nCasesVides == 1) {
			score -= 10;
		}
		return score;
	}

	/**
	 * fonction indiquant le score d'une grille donnée en fonction des différents
	 * alignements verticaux, horizontaux et diagonales de la totalité de la grille
	 * 
	 * @param grille : tableau 2D de caractères représentant la grille de jeu
	 * @param joueur : caractère représentant le joueur pour lequel on veut vérifier
	 *               le score de la grille
	 * @return entier représentant le score d'une grille donnée
	 */

	public static int scoreGrille(char[][] grille, char joueur) {

		int score = 0;
		// score horizontal
		// on parcourt les lignes de la grille
		for (int ligne = 0; ligne < grille.length; ligne++) {
			// pour chaque ligne un crée un tableau contenant toutes les valeurs de la ligne
			// (7 colonnes)
			char tableauLigne[] = grille[ligne];
			// on parcourt les colonnes du tableau
			for (int col = 0; col < grille[0].length - 3; col++) {
				// pour chaque valeur du tableau créé au-dessus jusqu'à la 4ème on crée une
				// collection contenant la valeur dans laquelle on se situe dans cette itération
				// de la
				// boucle et les 3 valeurs du tableau suivantes
				ArrayList<Character> listeHorizontale = new ArrayList<Character>();
				listeHorizontale.add(tableauLigne[col]);
				listeHorizontale.add(tableauLigne[col + 1]);
				listeHorizontale.add(tableauLigne[col + 2]);
				listeHorizontale.add(tableauLigne[col + 3]);
				// on incrémente le score avec la fonction evaluation4Cases qui retourne
				// elle-même un score en fonction des valeurs de la collection
				score += evaluation4Cases(listeHorizontale, joueur);
			}

		}

		// score vertical
		// on parcourt les colonnes de la grille
		for (int col = 0; col < grille[0].length; col++) {
			// pour chaque colonne on crée un tableau contentant toutes les valeurs de la
			// colonne
			char tableauColonne[] = new char[grille.length];
			for (int ligne = 0; ligne < grille.length; ligne++) {
				tableauColonne[ligne] = grille[ligne][col];
			}
			// on parcourt les lignes de la grille
			for (int ligne = 0; ligne < grille.length - 3; ligne++) {
				// pour chaque valeur du tableau créé au-dessus jusqu'à la 4ème on crée une
				// collection contenant la valeur dans laquelle on se situe dans cette itération
				// de la
				// boucle et les 3 valeurs du tableau suivantes
				ArrayList<Character> listeVerticale = new ArrayList<Character>();
				listeVerticale.add(tableauColonne[ligne]);
				listeVerticale.add(tableauColonne[ligne + 1]);
				listeVerticale.add(tableauColonne[ligne + 2]);
				listeVerticale.add(tableauColonne[ligne + 3]);
				// on incrémente le score avec la fonction evaluation4Cases qui retourne
				// elle-même un score en fonction des valeurs de la collection
				score += evaluation4Cases(listeVerticale, joueur);
			}
		}

		// score diagonale haut
		// on parcourt les lignes de la grille jusqu'à sa 4e valeur
		for (int ligne = 0; ligne < grille.length - 3; ligne++) {
			// on parcourt les colonnes de la grille jusqu'à sa 4e valeur
			for (int col = 0; col < grille[0].length - 3; col++) {
				//
				ArrayList<Character> listeDiagonaleHaut = new ArrayList<Character>();
				for (int i = 0; i < 4; i++) {
					listeDiagonaleHaut.add(grille[ligne + i][col + i]);
				}
				// on incrémente le score avec la fonction evaluation4Cases qui retourne
				// elle-même un score en fonction des valeurs de la collection
				score += evaluation4Cases(listeDiagonaleHaut, joueur);
			}
		}

		// score diagonale bas
		// on parcourt les lignes de la grille jusqu'à sa 4e valeur
		for (int ligne = 0; ligne < grille.length - 3; ligne++) {
			// on parcourt les colonnes de la grille jusqu'à sa 4e valeur
			for (int col = 0; col < grille[0].length - 3; col++) {
				// on crée une collection pour chaque diagonale de 4 du tableau allant vers le
				// bas du
				// tableau possible
				ArrayList<Character> listeDiagonaleBas = new ArrayList<Character>();
				for (int i = 0; i < 4; i++) {
					listeDiagonaleBas.add(grille[ligne + 3 - i][col + i]);
				}
				// on incrémente le score avec la fonction evaluation4Cases qui retourne
				// elle-même un score en fonction des valeurs de la collection
				score += evaluation4Cases(listeDiagonaleBas, joueur);
			}
		}
		return score;
	}

	/**
	 * fonction permettant de retourner en entier représentant la colonne pour
	 * laquelle le coup du joueur donné en paramètre sera le plus favorable
	 * 
	 * @param grille : tableau 2D de caractère représentant la grille de jeu
	 * @param joueur : caractère représentant le joueur pour lequel on veut le
	 *               meilleur coup pour ce tour
	 * @return entier représentant la colonne pour laquelle le coup ce tour sera le
	 *         plus favorable pour le joueur donné en paramètre
	 */

	public static int meilleurCoup(char[][] grille, char joueur) {

		// on initialise le score à une grande valeur négative car le score que l'on va
		// obtenir avec la fonction scoreGrille peut être négative
		int meilleurScore = -100000000;

		// on crée une collection contenant les colonnes jouables pour le tour dans
		// lequel on se situe
		ArrayList<Integer> actions = new ArrayList<Integer>();
		actions = colonnesJouables(grille);

		// on initialise la meilleure colonne jouable à une valeur aléatoire
		int indexActionsTour = (int) (Math.random() * actions.size());
		int meilleureColonne = actions.get(indexActionsTour);

		// pour chaque action possible pour le prochain tour
		for (int colonne : actions) {
			// on crée une copie de la grille donnée en paramètre
			char[][] copieGrille = new char[grille.length][];
			for (int i = 0; i < grille.length; i++) {
				copieGrille[i] = new char[grille[i].length];
				for (int j = 0; j < copieGrille[i].length; j++) {
					copieGrille[i][j] = grille[i][j];
				}
			}

			// on fait tomber le pion dans la colonne dans laquelle on se situe dans la
			// boucle
			faireTomberPion(copieGrille, colonne, joueur);
			// on vérifie le score pour la grille que l'on a créée et dans laquelle on a
			// fait tomber le pion
			int score = scoreGrille(copieGrille, joueur);
			// si le score obtenu est supérieur au meilleurScore que l'on avait déjà on
			// change les valeurs du meilleur score et de la meilleur colonne
			if (score > meilleurScore) {
				meilleurScore = score;
				meilleureColonne = colonne;
			}
		}
		return meilleureColonne;
	}

	/**
	 * fonction permettant de déterminer si l'on se situe dans un noeud final dans
	 * la fonction algorithmeMinMax Un noeud final représente une fin de partie, il
	 * y a donc 3 possiblités de fins de partie : l'ordinateur gagne, le joueur
	 * gagne, ou il y a égalité
	 * 
	 * @param grille : tableau 2D de caractères représentant la grille de jeu
	 * @return booléen indiquant si l'on se situe dans un noeud final ou non
	 */

	public static boolean estNoeudFinal(char[][] grille) {
		return estGagnant('1', grille) || estGagnant('2', grille) || colonnesJouables(grille).size() == 0;
	}

	/**
	 * fonction récursive implémentant l'algorithme minimax la fonction va retourner
	 * récursivement un tableau d'entier avec 2 valeurs : la colonne qui correspond
	 * au meilleur choix et la score qui correspond à la grille dans laquelle on met
	 * le pion
	 * 
	 * @param grille           : tableau 2D de caractères représentant la grille de
	 *                         jeu
	 * @param profondeur       : entier représentant le nombre d'itérations
	 *                         récursives on veut que la fonction produise, plus ce
	 *                         nombre est grand, plus l'ordinateur fera un choix
	 *                         pertinent
	 * @param joueur           : caractère représentant le joueur avec lequel on
	 *                         veut commencer les actions récursives
	 * @param joueurAMaximiser : booléen représentant si on veut maximiser les
	 *                         actions du joueur entré en paramètre ou si l'on veut
	 *                         les minimiser
	 * @return tableau d'entier retournant la meilleure colonne dans laquelle on
	 *         peut jouer et le score correspondant
	 */

	public static int[] minMaxAlgorithme(char[][] grille, int profondeur, boolean joueurAMaximiser) {
		ArrayList<Integer> actions = new ArrayList<Integer>();

		actions = colonnesJouables(grille);
		boolean noeudFinal = estNoeudFinal(grille);

		int colonneScore[] = new int[2];

		if (profondeur == 0 || noeudFinal) {
			if (noeudFinal) {
				if (estGagnant('2', grille)) {
					colonneScore[0] = -1;
					colonneScore[1] = 1000000;
					return colonneScore;
				} else if (estGagnant('1', grille)) {
					colonneScore[0] = -1;
					colonneScore[1] = -1000000;
					return colonneScore;
				} else {
					colonneScore[0] = -1;
					colonneScore[1] = 0;
					return colonneScore;
				}
			} else {
				colonneScore[0] = -1;
				colonneScore[1] = scoreGrille(grille, '2');
				return colonneScore;
			}
		}

		if (joueurAMaximiser) {
			int score = -999999999;
			int indexActionsTour = (int) (Math.random() * actions.size());
			int colonne = actions.get(indexActionsTour);
			for (int col : actions) {
				char[][] copieGrille = new char[grille.length][];
				for (int i = 0; i < grille.length; i++) {
					copieGrille[i] = new char[grille[i].length];
					for (int j = 0; j < copieGrille[i].length; j++) {
						copieGrille[i][j] = grille[i][j];
					}
				}
				faireTomberPion(copieGrille, col, '2');
				int nouveauScore = minMaxAlgorithme(copieGrille, profondeur - 1, false)[1];
				if (nouveauScore > score) {
					score = nouveauScore;
					colonne = col;
				}
			}
			colonneScore[0] = colonne;
			colonneScore[1] = score;
			return colonneScore;

		} else {
			int score = 999999999;
			int indexActionsTour = (int) (Math.random() * actions.size());
			int colonne = actions.get(indexActionsTour);
			for (int col : actions) {
				char[][] copieGrille = new char[grille.length][];
				for (int i = 0; i < grille.length; i++) {
					copieGrille[i] = new char[grille[i].length];
					for (int j = 0; j < copieGrille[i].length; j++) {
						copieGrille[i][j] = grille[i][j];
					}
				}
				faireTomberPion(copieGrille, col, '1');
				int nouveauScore = minMaxAlgorithme(copieGrille, profondeur - 1, true)[1];
				if (nouveauScore < score) {
					score = nouveauScore;
					colonne = col;
				}
			}
			colonneScore[0] = colonne;
			colonneScore[1] = score;
			return colonneScore;
		}
	}

	/**
	 * procédure permettant de modifier la grille de jeu en faisant 'tomber' le
	 * caractère donné en paramètre dans la colonne donnée en paramètre
	 * 
	 * @param grille  : tableau 2D de caractère représentant la grille de jeu
	 * @param colonne : entier représentant la colonne de la grille dans laquelle on
	 *                veut jouer
	 * @param joueur  : caractère représentant le joueur pour lequel on va faire
	 *                tomber le pion
	 */

	public static void faireTomberPion(char[][] grille, int colonne, char joueur) {
		for (int ligne = grille.length - 1; ligne >= 0; ligne--) {
			if (grille[ligne][colonne] == ' ') {
				grille[ligne][colonne] = joueur;
				break;
			}
		}
	}

	/**
	 * fonction retournant une collection contenant des entiers représentant une
	 * liste des colonnes dans lesquelles on peut mettre un pion
	 *
	 * @param grille : tableau 2D de caractères représentant la grille de jeu
	 * @return collection d'entiers représentant les colonnes dans lesquelles on
	 *         peut jouer
	 */

	public static ArrayList<Integer> colonnesJouables(char[][] grille) {
		ArrayList<Integer> actions = new ArrayList<Integer>();
		for (int j = 0; j <= grille.length; j++)
			if (grille[0][j] == ' ') {
				actions.add(j);
			}
		return actions;
	}

	/**
	 * fontions permettant de sauvegarder et de charger la grille, le mode de jeu,
	 * le joueur qui devait jouer et le nombre de tours joués
	 */

	public static void creerSauvegardeGrille(char[][] grille)
			throws FileNotFoundException, UnsupportedEncodingException {
		PrintWriter writer = new PrintWriter("sauvegarde.txt", "UTF-8");
		for (int ligne = 0; ligne < grille.length; ligne++) {
			for (int col = 0; col < grille[0].length; col++) {
				writer.println(Character.getNumericValue(grille[ligne][col]));
			}
		}
		writer.close();
	}

	public static void creerSauvegardeTour(int tour) throws FileNotFoundException, UnsupportedEncodingException {
		PrintWriter writer = new PrintWriter("tour.txt", "UTF-8");
		writer.println(tour);
		writer.close();
	}

	public static void creerSauvegardeJoueur(char joueur) throws FileNotFoundException, UnsupportedEncodingException {
		PrintWriter writer = new PrintWriter("joueur.txt", "UTF-8");
		writer.println(joueur);
		writer.close();
	}

	public static void creerSauvegardeMode(String mode) throws FileNotFoundException, UnsupportedEncodingException {
		PrintWriter writer = new PrintWriter("modeDeJeu.txt", "UTF-8");
		writer.println(mode);
		writer.close();
	}

	public static int accederSauvegardeTour() throws FileNotFoundException {
		int data = 0;
		Scanner fScn = new Scanner(new File("tour.txt"));
		while (fScn.hasNextLine()) {
			data = Integer.parseInt(fScn.nextLine());
		}
		return data;
	}

	public static char accederSauvegardeJoueur() throws FileNotFoundException {
		char data = '1';
		Scanner fScn = new Scanner(new File("joueur.txt"));
		while (fScn.hasNextLine()) {
			data = fScn.nextLine().charAt(0);
		}
		return data;
	}

	public static String accederSauvegardeMode() throws FileNotFoundException {
		String data = "";
		Scanner fScn = new Scanner(new File("modeDeJeu.txt"));
		while (fScn.hasNextLine()) {
			data = fScn.nextLine();
		}
		return data;
	}

	public static char[][] accederSauvegardeGrille() throws FileNotFoundException {
		char data[][] = new char[6][7];
		Scanner fScn = new Scanner(new File("sauvegarde.txt"));
		char caseTableau = ' ';

		int colonne = 0;
		int ligne = 0;
		while (fScn.hasNextLine()) {
			String ligneTxt = fScn.nextLine();
			if (ligneTxt.equals("-1")) {
				caseTableau = ' ';
			} else {
				caseTableau = ligneTxt.charAt(0);
			}
			data[ligne][colonne] = caseTableau;
			colonne++;
			if (colonne > 6) {
				colonne = 0;
				ligne = ligne + 1;
			}
		}
		return data;
	}

}
