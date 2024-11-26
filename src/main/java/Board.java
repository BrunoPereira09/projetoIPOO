import java.util.Random;
/**
 * Classe que representa o tabuleiro do jogo MineSweeper.
 * A classe Board lida com a criação e manipulação do tabuleiro, incluindo a colocação de bombas e revelação de células.
 * Ela também implementa a lógica para interações do jogador, como abrir células,
 * colocar bandeiras e verificar se o jogo foi ganho ou perdido.
 */
public class Board {
    //Atributos
    private int rows; //Linhas
    private int cols; //Colunas
    private String[][] board; //Tabuleiro
    private boolean[][] bombs; //Se o valor for true, a celula do tabuleiro tem uma bomba.
    private int amountBombs; //Quantidade de bombas
    private boolean[][] checked; //Se o valor for true, a celula do tabuleiro foi verificada e revelada
    private int positionsWithoutBombs; //Quantidade de celulas sem bombas disponiveis
    private int totalFlags; //Quantidade de bandeiras
    private static boolean isGameOver = false; //Se o valor for true, o jogo acabou
    /**
     * Construtor que inicializa um tabuleiro com o número de linhas, colunas e bombas especificadas.
     * @param rows o número de linhas do tabuleiro
     * @param cols o número de colunas do tabuleiro
     * @param amountBombs o número total de bombas a serem colocadas no tabuleiro
     * @param totalFlags o número de bandeiras permitidas
     */
    public Board(int rows, int cols, int amountBombs, int totalFlags) {
        this.rows = rows;
        this.cols = cols;
        this.amountBombs = amountBombs;
        this.totalFlags = totalFlags;
        this.positionsWithoutBombs = rows * cols - amountBombs;
    }
    /**
     * Retorna o estado atual do jogo (se acabou ou não).
     * (utilizado como getter)
     * @return verdadeiro se o jogo acabou, caso contrário falso.
     */
    public static boolean isGameOver() {
        return isGameOver;
    }

    public static void setIsGameOver(boolean gameOver) {
        Board.isGameOver = gameOver;
    }

    /**
     * Inicializa o tabuleiro do jogo as matrizes de bomba e células verificadas.
     * A cada célula é atribuído o símbolo inicial "■  ".
     * Será o estado inicial do jogo.
     */
    public void initializeGame() {
        checked = new boolean[rows][cols];
        board = new String[rows][cols];
        bombs = new boolean[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                board[i][j] = "■  ";
            }
        }
    }
    /**
     * Exibe o tabuleiro atual na consola.
     */
    public void printBoard() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                System.out.print(board[i][j]);
            }
            System.out.println("| " + (i + 1));
        }
        for (int i = 0; i < rows; i++) {
            System.out.print((i + 1) + "| ");
        }
        System.out.println();
    }
    /**
     * Preenche o tabuleiro com as bombas aleatoriamente, sem repetir posições.
     */
    public void fillBombs() {
        Random random = new Random();
        int bombsPlaced = 0;
        while (bombsPlaced < amountBombs) {
            int bombRow = random.nextInt(rows);
            int bombCol = random.nextInt(cols);
            if (!bombs[bombRow][bombCol]) {
                bombs[bombRow][bombCol] = true;
                bombsPlaced++;
            }
        }
    }
    /**
     * Fornece uma dica ao jogador, revelando uma célula aleatória sem bomba, com número de bombas adjacentes maior que 0
     * e que não tenha sido aberta ainda.
     * Utilizado no Player para o comando /hint
     */
    public void hint() {
        Random random = new Random();
        while (true) {
            int hintRow = random.nextInt(rows);
            int hintCol = random.nextInt(cols);
            int bombCount = countBombs(hintRow, hintCol);
            if (!bombs[hintRow][hintCol] && !checked[hintRow][hintCol] && bombCount > 0) {
                board[hintRow][hintCol] = bombCount + "  ";
                checked[hintRow][hintCol] = true;
                break;
            }
        }
    }
    /**
     * Valida se uma posição (linha e coluna) fornecida está dentro dos limites do tabuleiro.
     * Esta função é chamada no Player para validar as coordenadas fornecidas pelo jogador.
     * @param row a linha a ser validada
     * @param col a coluna a ser validada
     * @return verdadeiro se a posição for válida, falso caso contrário
     */
    public boolean validatePosition(int row, int col) { // a mudar
        try {
            if (row < 0 || row > 8) {
                System.out.println("Invalid row! Please enter a number between 1 and 9.");
                return false;
            }
        } catch (Exception e) {
            System.out.println("Invalid row! Please enter a valid number.");
        }
        try {
            if (col < 0 || col > 8) {
                System.out.println("Invalid column! Please enter a number between 1 and 9.");
                return false;
            }
        } catch (Exception e) {
            System.out.println("Invalid column! Please enter a valid number.");
        }
        return true;
    }
    /**
     * Inicia a jogada de abrir uma célula no tabuleiro.
     * Se uma bomba for revelada, o jogo é perdido. Se o jogador abrir todas as células sem bomba e
     * adivinhar onde estão as bombas ao colocar bandeiras, o jogo é ganho.
     * @param row a linha da célula a ser aberta
     * @param col a coluna da célula a ser aberta
     * A linha e coluna a serem abertas devem ser fornecidas pelo jogador com o comando /open <row> <col>
     */
    public void playGame(int row, int col) {
        if (bombs[row][col]) {
            revealBombs();
            isGameOver = true;
            System.out.println("You lose! time: " + Player.getTimer() + "\nReturning to menu...\n");
            Menu.start();
        } else {
            int guessedBombs = checkFlag(row, col);
            revealAround(row, col);
            printBoard();
            System.out.printf("\rElapsed time: %s\n", Player.getTimer());
            if (positionsWithoutBombs == 0 && guessedBombs == amountBombs) {
                revealBombs();
                isGameOver = true;;
                System.out.println("You win! time: " + Player.getTimer() + "\nReturning to menu...\n");
                Menu.start();
            }
        }
    }

    /**
     * Conta o número de bombas ao redor de uma célula.
     * @param row a linha da célula
     * @param col a coluna da célula
     * @return o número de bombas ao redor da célula
     */
    public int countBombs(int row, int col) {
        int count = 0;
        int[] directions = {-1, 0, 1};
        for (int dirRow : directions) {
            for (int dirCol : directions) {
                int newRow = row + dirRow;
                int newCol = col + dirCol;
                if (newRow >= 0 && newRow < rows && newCol >= 0 && newCol < cols && bombs[newRow][newCol]) {
                    count++;
                }
            }
        }
        return count;
    }
    /**
     * Revela as células ao redor de uma célula aberta, caso não tenha bomba e esteja dentro dos limites do tabuleiro.
     * Se a célula aberta não tiver bombas ao redor, a recursão se propaga para as células adjacentes.
     * Assim criando um algoritmo de busca em profundidade ao utilizar a recursão.
     * @param row a linha da célula
     * @param col a coluna da célula
     */
    public void revealAround(int row, int col) {
        if (row < 0 || row >= rows || col < 0 || col >= cols || checked[row][col] || bombs[row][col]) {
            return;
        }
        checked[row][col] = true;
        positionsWithoutBombs--;
        int bombCount = countBombs(row, col);
        board[row][col] = bombCount + "  ";
        if (board[row][col].equals("0  ")) {
            board[row][col] = "□  ";
        }
        if (bombCount == 0) {
            int[] directions = {-1, 0, 1};
            for (int dirRow : directions) {
                for (int dirCol : directions) {
                    if (dirRow != 0 || dirCol != 0) {
                        revealAround(row + dirRow, col + dirCol); //chama recursivamente a função
                    }
                }
            }
        }
    }
    /**
     * Revela todas as bombas no tabuleiro.
     * Utilizada quando o jogador perde o jogo ou utiliza o comando /cheat
     */
    public void revealBombs() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (bombs[i][j]) {
                    board[i][j] = "B  ";
                }
                System.out.print(board[i][j]);
            }
            System.out.println("| " + (i + 1));
        }
        for (int i = 0; i < rows; i++) {
            System.out.print((i + 1) + "| ");
        }
        System.out.println();
    }
    /**
     * Coloca ou remove uma bandeira na célula especificada.
     * Caso o jogador coloque ou remova uma bandeira em uma posição invalida será mostrado uma mensagem de erro.
     * Caso o jogador tente colocar mais bandeiras do que o permitido, uma mensagem de erro será exibida.
     * Caso o jogador tente remover uma bandeira de uma posição sem bandeira, uma mensagem de erro sera exibida.
     * Caso o jogador tente colocar uma bandeira em uma posição que já possui bandeira, a bandeira será removida.
     * @param row a linha da célula onde a bandeira será colocada ou removida
     * @param col a coluna da célula onde a bandeira será colocada ou removida
     */
    public void placeFlag(int row, int col) {
        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            System.out.println("Coordenadas inválidas!");
            return;
        }
        if (totalFlags == 0) {
            System.out.println("Limite de flags atingido!");
        } else if (board[row][col].equals("■  ")) {
            board[row][col] = "#  ";
            totalFlags--;
        } else if (board[row][col].equals("#  ")) {
            board[row][col] = "■  ";
            totalFlags++;
        } else {
            System.out.println("Posição inválida!");
        }
    }
    /**
     * Verifica se uma bandeira foi colocada corretamente em uma célula com bomba.
     * @param row a linha da célula
     * @param col a coluna da célula
     * @return o valor de bandeiras adivinhadas corretamente.
     */
    public int checkFlag(int row, int col) {
        int guessed = 0;
        if (board[row][col].equals("#  ") && bombs[row][col]) {
            guessed++;
        }
        return guessed;
    }
}
