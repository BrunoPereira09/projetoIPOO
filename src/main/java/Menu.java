import java.util.InputMismatchException;
import java.util.Scanner;
/**
 * Classe que representa o menu principal do jogo.
 * A classe Menu lida com a exibição do menu de opções ao usuário e permite ao jogador escolher entre iniciar um novo jogo,
 * visualizar os últimos 10 vencedores (ainda não implementado), ou sair do jogo.
 */
public class Menu {
    /**
     * Inicia o menu do jogo e aguarda a escolha do usuário.
     * O usuário pode escolher entre iniciar um novo jogo, ver os últimos 10 vencedores ou sair do jogo.
     * A opção escolhida é processada, e se a escolha for válida, a ação correspondente é executada.
     * Caso uma escolha inválida seja feita, o menu é reapresentado até que uma opção válida seja selecionada.
     */
    public static void start(){
        Scanner sc = new Scanner(System.in);
        boolean choiceMade = false;
        while (!choiceMade) {
            System.out.println("MineSweeper Game");
            System.out.println("----------------");
            System.out.println("1. New Game");
            System.out.println("2. Last 10 Wins");
            System.out.println("3. Exit Game");
            System.out.print("Option> ");
            try {
                int choice = sc.nextInt();
                switch (choice) {
                    case 1:
                        newGame();
                        choiceMade = true;
                        break;
                    case 2:
                        System.out.println("Last 10 winners:");
                        for (String winner : Player.userInfo()) {
                            if (winner != null) {
                                System.out.println(winner);
                            }
                        }
                        break;
                    case 3:
                        System.out.println("Exiting...");
                        System.exit(0);
                    default:
                        System.out.println("Invalid option, please choose a number between 1 and 3.\n");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid option, please choose a valid number.\n");
                sc.next(); // limpa o valor que está no scanner para que o ciclo while não repita.
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
    /**
     * Inicia um novo jogo de MineSweeper.
     * Um novo tabuleiro é criado com as dimensões e o número de bombas especificados.
     * O jogador é solicitado a fornecer seu nome e então pode começar a jogar.
     */
    public static void newGame() throws InterruptedException {

        Board board1 = new Board(9, 9, 10, 10); //Cria o tabuleiro
        board1.initializeGame(); //Inicializa o tabuleiro
        board1.fillBombs(); //Preenche o tabuleiro com bombas

        Player player1 = new Player(board1); //Cria o jogador
        String nome = player1.setNome(); //Define o nome do jogador
        player1.playerTimer();
        board1.printBoard(); //Imprime o tabuleiro
        player1.commands(nome); //Processa os comandos do jogador
    }
}
