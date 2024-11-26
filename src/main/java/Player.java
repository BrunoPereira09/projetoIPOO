import java.util.Scanner;
/**
 * Classe que representa um jogador no jogo.
 * Ela interage com o tabuleiro do jogo para executar comandos.
 */
public class Player {

    // Atributos
    private static int totalPlayers = 1;
    private Board board;
    private static int timer = 0;
    private static String[] last10Winners = new String[10];
    private static String name;
    /**
     * Constrói um objeto Player associado ao tabuleiro do jogo.
     * @param board o objeto Board associado ao jogador
     */
    public Player(Board board) {
        this.board = board;
    }

    public static String getTimer() {
        int seconds = timer;
        int minutes = seconds / 60;
        int hours = minutes / 60;
        return String.format("%02dh%02dm%02ds", hours, minutes % 60, seconds % 60);
    }

    /**
     * Define o nome do jogador, pedindo a entrada do usuário.
     * Se o nome for vazio, o nome é definido como "Anonymous" seguido pelo número do jogador.
     * @return o nome do jogador
     */
    public String setNome() {
        name = "";
        Scanner sc = new Scanner(System.in);
        while (true) {
            try {
                System.out.print("Username> ");
                name = sc.nextLine();
                break;
            } catch (Exception e) {
                System.out.println("Invalid input! Please enter a valid name.\n");
                sc.next();
            }
        }
        if (name.isEmpty()) {
            name = "Anonymous " + (totalPlayers); ;
        }
        totalPlayers++;
        return name;
    }
    /**
     * Processa os comandos do jogador durante o jogo. Os comandos disponíveis incluem:
     * <ul>
     *   <li>/help - Exibe uma lista de comandos disponíveis.</li>
     *   <li>/quit - Sai do jogo e retorna ao menu.</li>
     *   <li>/open <linha> <coluna> - Abre uma célula nas coordenadas especificadas.</li>
     *   <li>/flag <linha> <coluna> - Marca uma célula nas coordenadas especificadas. Se já estiver marcada, a marcação é removida.</li>
     *   <li>/hint <linha> <coluna> - Revela uma célula aleatória sem bomba.</li>
     *   <li>/cheat - Revela todas as bombas.</li>
     * </ul>
     */
    public void commands(String nome) {;
        Scanner sc = new Scanner(System.in);
        System.out.println("Welcome, " + nome + "! To see the list of available commands, type /help.");
        while (!Board.isGameOver()) {
            System.out.print("Option> ");
            String[] command = new String[3];
            command = sc.nextLine().split(" ");
            switch (command[0]) {
                case "/help":
                    System.out.println("Available commands:");
                    System.out.println("/help - Displays a list of available commands.");
                    System.out.println("/quit - Quits the game.");
                    System.out.println("/open <row> <column> - Opens a cell at the specified coordinates.");
                    System.out.println("/flag <row> <column> - Flags a cell at the specified coordinates. If the cell is already flagged, it will be unflagged.");
                    System.out.println("/hint <row> <column> - Reveals a random cell without a bomb.");
                    System.out.println("/cheat - Reveals the entire board.");
                    break;
                case "/quit":
                    System.out.println("Exiting to the menu...");
                    Menu.start();
                    break;
                case "/open":
                    int row = Integer.parseInt(command[1]) - 1;
                    int col = Integer.parseInt(command[2]) - 1;
                    if (board.validatePosition(row, col)) {
                        board.playGame(row, col);
                    }
                    break;
                case "/flag":
                    int flagRow = Integer.parseInt(command[1]) - 1;
                    int flagCol = Integer.parseInt(command[2]) - 1;
                    if (board.validatePosition(flagRow, flagCol)) {
                        board.placeFlag(flagRow, flagCol);
                        board.printBoard();
                    }
                    break;
                case "/hint":
                    board.hint();
                    board.printBoard();
                    break;
                case "/cheat":
                    board.revealBombs();
                    break;
                case "/win":
                    System.out.println("You win! time: " + Player.getTimer() + "\nReturning to menu...\n");
                    Board.setIsGameOver(true);

                    Menu.start();
                    break;
                default:
                    System.out.println("Invalid command! To see the list of available commands, type /help.");
            }
        }
    }

    public void playerTimer() {
         Thread t = new Thread(() -> {
             while (!Board.isGameOver()) {
                 try {
                     Thread.sleep(1000);
                 } catch (InterruptedException e) {
                     throw new RuntimeException(e);
                 }
                 timer++;
             }
         });
         t.start();
    }

    public static String[] userInfo() {
        int currentIndex = 0;
        if (currentIndex < last10Winners.length) {
            last10Winners[currentIndex] = name + " --> " + timer;
            currentIndex++;
            return last10Winners;
        }
        return null;
    }
}
