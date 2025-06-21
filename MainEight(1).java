package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import java.io.*;

/**
 * ��������
 */
public class MainEight {
    	public static void main(String[] args) {
	//��ʼ����Ϸ����
    	GameFrame frame = new GameFrame();		// ������Ϸ������
       	GamePanel panel = new GamePanel(frame);	       // ������Ϸ�����
        	frame.add(panel);	// �������ӵ�����
        	frame.setVisible(true);	 // ��ʾ����

	//��ӭ��Ϣ������ģʽѡ����ʾ
	Object[] options={"���˶�ս","�˻���ս"};
	int choice=JOptionPane.showOptionDialog(frame,
		"��ӭ������������Ϸ!\n��ѡ����Ϸģʽ:",
		"��ӭ��",
		JOptionPane.DEFAULT_OPTION,
		JOptionPane.INFORMATION_MESSAGE,
		null,
		options,
		options[0]);

	//�����û�ѡ��������Ϸģʽ
	if(choice==0){
		panel.setGameMode("human");	//���˶�սģʽ
	}else{
		panel.setGameMode("ai");	//�˻���սģʽ
	}

	//��ʾ����ָ��
	JOptionPane.showMessageDialog(frame, 
                    "��Ϸ����ָ��:\n" +
                    "1. ������̽�����������\n" +
                    "2. �ڷ����У���������\n" +
                    "3. ���ںᡢ����б�������������߻�ʤ\n"+
	"4.ʹ�ò˵����Ա���/������Ϸ�����塢�������̷��",
	"����ָ��",
	JOptionPane.INFORMATION_MESSAGE);
	}
}

/**
 * ��Ϸ��������
 */
class GameFrame extends JFrame {
	private static final long serialVersionUID = 1L;

	public GameFrame() {
        		setTitle("������");                     // ���ô��ڱ���
        		setSize(620, 670);                     // ���ô��ڴ�С
        		getContentPane().setBackground(new Color(209, 146, 17));	 // ���ñ�����ɫ(ľ�ʻ�ɫ)
        		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	 // ���ùرհ�ť��Ϊ
        		setLocationRelativeTo(null);           // ���ھ�����ʾ
        		setResizable(false);                   // ��ֹ�������ڴ�С
   	}
}

/**
 * ��Ϸ������� - ����������Ϸ�߼�
 */
class GamePanel extends JPanel implements ActionListener {

	public void setGameMode(String mode){
		this.gameMode=mode;
		resetGame();	//������Ϸ״̬
		String modeName=mode.equals("human")?"���˶�ս":"�˻���ս";
		JOptionPane.showMessageDialog(mainFrame,"��ѡ��"+modeName+"ģʽ\n"+
			(mode.equals("human")?"��λ������������������":"����ִ�����У�AIִ��"),"��Ϸģʽ",
			JOptionPane.INFORMATION_MESSAGE);
	}

 	// ��չ��ɫѡ�����6����ɫ����11��ѡ��
    	private static final Color[] BOARD_COLORS = {
        	// ľ��ɫϵ
        	new Color(209, 146, 17),   // ԭľɫ
        	new Color(222, 184, 135),  // ǳľɫ
        	new Color(139, 69, 19),    // ����ɫ
        	new Color(255, 228, 196),  // �װ�ɫ
        	new Color(210, 180, 140), // �ƺ�ɫ
        
        	// ����ɫϵ
        	new Color(100, 149, 237),  // ��ɫ
        	new Color(144, 238, 144),  // ǳ��ɫ
        	new Color(255, 182, 193),  // ��ɫ
        	new Color(220, 220, 220),  // ��ɫ
       	new Color(255, 215, 0),    // ��ɫ
        	new Color(147, 112, 219)   // ��ɫ
    	};
    
    	private Color currentBoardColor = BOARD_COLORS[0]; // ��ǰ������ɫ
    
    	private static final long serialVersionUID = 1L;
    
    	// ��Ϸ���
    	private JMenuBar jmb;                      // �˵���
   	private GameFrame mainFrame;               // ����������
     	// ��Ϸ����
    	private static final int ROWS = 15;               // ��������
    	private static final int COLS = 15;               // ��������
    
    	// ��Ϸ״̬����
    	private String gameFlag = "start";         // ��Ϸ״̬(start/end)
    	private String gameMode = "human";         // ��Ϸģʽ(human/ai)
    	private static boolean isBlackTurn = true;        // ��ǰ�غ�(true=�ڷ�/false=�׷�)
	private static int chessSize=0;//�����ܸ���
	private static int [][] chessIndexArr=new int[256][2];//chessIndexArr[i][0]Ϊ��i�����ӵ���λ�ã�chessIndexArr[i][0]Ϊ��i�����ӵ���λ��
    
    	// ��Ϸ����
    	public Pointer[][] pointers = new Pointer[ROWS][COLS]; // ���̸�������
    	private Random random = new Random();      // �����������(����AI)

    /**
     * ���캯��
     * @param mainFrame ����������
     */
    	public GamePanel(GameFrame mainFrame) {
        		this.setLayout(null);                  // ��ʹ�ò��ֹ�����
        		this.setOpaque(false);                 // ����͸������
        		this.mainFrame = mainFrame;            // ��������������
        
        		// ��ʼ����Ϸ
        		createMenu();                          // �����˵�
        		createMouseListener();                 // ����������
        		createPointers();                      // ��ʼ�����̸���
    	}

    /**
     * ��ʼ���������и���
     */
    	private void createPointers() {
        	int x, y, start = 26;                 // start=������ʼ����(�����߾�)
        	for (int i = 0; i < ROWS; i++) {
            		for (int j = 0; j < COLS; j++) {
                			x = i * 40 + start;            // ����ÿ�����ӵ�x����(40=���Ӽ��)
                			y = j * 40 + start;            // ����ÿ�����ӵ�y����
                		pointers[i][j] = new Pointer(i, j, x, y); // �������Ӷ���
            		}
  	}
    }

    /**
     * ������������
     */
    private void createMouseListener() {
        	MouseAdapter mouseAdapter = new MouseAdapter() {
           	 /**
           	  * ����ƶ��¼� - ������ʾ����ָʾ��
            	 */
            	public void mouseMoved(MouseEvent e) {
                		if (!"start".equals(gameFlag)) return; // ��Ϸδ��ʼʱ������
                
               			int x = e.getX();              // ��ȡ���x����
                			int y = e.getY();              // ��ȡ���y����
                
                		// �������и��ӣ���������ͣ
                		for (Pointer[] row : pointers) {
                    		for (Pointer pointer : row) {
                        			pointer.setShow(pointer.isPoint(x, y)); // ����ָʾ����ʾ״̬
                    		}
                		}
                		repaint();                     // �ػ����
            	}

            /**
             * ������¼� - �����������
             */
            	public void mouseClicked(MouseEvent e) {
                		// ��Ϸδ��ʼ�����һغ�ʱ������
                		if (!"start".equals(gameFlag) || (!isBlackTurn && "ai".equals(gameMode))) return;
                
                		int x = e.getX();              // ��ȡ���x����
                		int y = e.getY();              // ��ȡ���y����
                
                		// �������и��ӣ������λ��
                		for (Pointer[] row : pointers) {
                    		for (Pointer pointer : row) {
                        			if (pointer.isPoint(x, y)) {  // �ҵ�������ĸ���
                            				if (pointer.getState() == 0) {  // �������Ϊ��
                                					// ���ݵ�ǰ�غ�����������ɫ(1=��/2=��)
                                					pointer.setState(isBlackTurn ? 1 : 2); 

						//�洢����˳��Ϊ�ڼ������ӣ�
						chessIndexArr[chessSize][0]=pointer.i;
						chessIndexArr[chessSize][1]=pointer.j;
						chessSize++;
                                
                                					if (checkWin(pointer)) {  // ����Ƿ��ʤ
                                    					gameFlag = "end";    // ��Ϸ����
                                    					JOptionPane.showMessageDialog(mainFrame, 
                                        					(isBlackTurn ? "�ڷ�" : "�׷�") + "��ʤ!");
                                					} else {
                                    					isBlackTurn = !isBlackTurn; // �л��غ�
                                    					// AIģʽ���ֵ�AIʱ�Զ�����
                                    					if ("ai".equals(gameMode) && !isBlackTurn) {
                                        						aiMove();
                                    					}
                                					}
                                					repaint();     // �ػ����
                            				}
                            				return;            // ��������˳�
                        			}
                    		}
                		}
            	}
        };
        
        // ע����������
        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);
    }

    /**
     * AI���巽��
     * ʵ�ֶ༶���ߣ�
     * 1. ���AI�ܷ�ֱ�ӻ�ʤ
     * 2. ��ֹ��Ҽ�����ʤ
     * 3. Ѱ�����Ž���/����λ��
     * 4. ���ѡ�����λ��
     */
    private void aiMove() {
        	// 1. ���AI�Ƿ��л�ʤ����
        	Point winMove = findWinningMove(2); // 2�������(AI)
        	if (winMove != null) {
            		placeAIPiece(winMove.x, winMove.y);
            		return;
        	}

        	// 2. ��ֹ��һ�ʤ
        	Point blockMove = findWinningMove(1); // 1�������(���)
        	if (blockMove != null) {
            		placeAIPiece(blockMove.x, blockMove.y);
            		return;
        	}

        	// 3. Ѱ�����Ų���λ��
        	Point bestMove = findStrategicMove();
        	if (bestMove != null) {
            		placeAIPiece(bestMove.x, bestMove.y);
            		return;
        	}

        	// 4. ���ѡ�����λ��
        	placeRandomMove();
    }

    /**
     * Ѱ�һ�ʤ���ᣨ��ָ���������ͣ�
     * @param pieceType 1=����(���) 2=����(AI)
     * @return ���γ�������λ�ã�û���򷵻�null
     */
    private Point findWinningMove(int pieceType) {
        	for (int i = 0; i < ROWS; i++) {
            		for (int j = 0; j < COLS; j++) {
                			if (pointers[i][j].getState() == 0) { // ��λ��
                    			pointers[i][j].setState(pieceType); // ģ������
                    			if (checkWin(pointers[i][j])) {
                        				pointers[i][j].setState(0); // �ָ���λ��
                        				return new Point(i, j);
                    			}
                    			pointers[i][j].setState(0); // �ָ���λ��
                			}
            		}
        	}
        	return null;
    }

    /**
     * Ѱ��ս��λ�ã�����������
     * @return �������λ��
     */
    private Point findStrategicMove() {
        	int[][] scoreMap = new int[ROWS][COLS]; // ���־���
        
        	// ����ÿ����λ�ļ�ֵ
        	for (int i = 0; i < ROWS; i++) {
            		for (int j = 0; j < COLS; j++) {
                			if (pointers[i][j].getState() == 0) {
                    			// ������ֵ��AI�İ��ӣ�
                    			int attackScore = evaluatePosition(i, j, 2); 
                    			// ���ؼ�ֵ����ҵĺ��ӣ�
                    			int defendScore = evaluatePosition(i, j, 1);
                    			// �ۺ����֣�����Ȩ���Ըߣ�
                    			scoreMap[i][j] = attackScore * 3 + defendScore * 4;
                			}
            		}
        	}
        
        	// �ҳ���߷�λ��
        	return findBestPosition(scoreMap);
    }

    /**
     * λ����������
     * @param x ������
     * @param y ������
     * @param pieceType ��������
     * @return ��λ�õļ�ֵ����
     */
    private int evaluatePosition(int x, int y, int pieceType) {
        	int score = 0;
        	int[][] directions = {{1,0}, {0,1}, {1,1}, {1,-1}}; // �ĸ�����
        
        	for (int[] dir : directions) {
            		int count = 1; // ��ǰ����������
            		int empty = 0;  // ���˿�λ
            
            		// ������
            		for (int step = 1; step <= 4; step++) {
                			int nx = x + dir[0] * step;
                			int ny = y + dir[1] * step;
                			if (nx < 0 || nx >= ROWS || ny < 0 || ny >= COLS) break;
                
                			if (pointers[nx][ny].getState() == pieceType) {
                    			count++;
                			} else if (pointers[nx][ny].getState() == 0) {
                    			empty++;
                    			break;
                			} else {
                    			break;
                			}
            		}
            
            		// ������
            		for (int step = 1; step <= 4; step++) {
                			int nx = x - dir[0] * step;
                			int ny = y - dir[1] * step;
                			if (nx < 0 || nx >= ROWS || ny < 0 || ny >= COLS) break;
                
                			if (pointers[nx][ny].getState() == pieceType) {
                    			count++;
               			} else if (pointers[nx][ny].getState() == 0) {
                    			empty++;
                    			break;
                			} else {
                    			break;
                			}
            		}
            
            		// �����������Ϳ�λ�������
            		if (count >= 5) score += 100000; // ��Ӯ
            		else if (count == 4 && empty >= 1) score += 10000; // ����
            		else if (count == 3 && empty >= 1) score += 1000;  // ����
            		else if (count == 2 && empty >= 1) score += 100;   // ���
            		else if (count == 1 && empty >= 1) score += 10;    // ��һ
        	}
        
        	return score;
    }

    /**
     * �����־����ҳ����λ��
     * @param scoreMap ���־���
     * @return ���λ������
     */
    private Point findBestPosition(int[][] scoreMap) {
        	int maxScore = 0;
        	List<Point> bestPoints = new ArrayList<>();
        
        	// �ҳ�������߷�λ��
        	for (int i = 0; i < ROWS; i++) {
            		for (int j = 0; j < COLS; j++) {
                			if (scoreMap[i][j] > maxScore) {
                    			maxScore = scoreMap[i][j];
                    			bestPoints.clear();
                    			bestPoints.add(new Point(i, j));
                			} else if (scoreMap[i][j] == maxScore) {
                    			bestPoints.add(new Point(i, j));
                			}
            		}
        	}
        
        	// ���ѡ��һ����߷�λ�ã�����̶�ģʽ��
        	return bestPoints.isEmpty() ? null : 
               	bestPoints.get(new Random().nextInt(bestPoints.size()));
    }

    /**
     * AI���Ӳ����ʤ��
     */
    private void placeAIPiece(int x, int y) {
        	pointers[x][y].setState(2); // 2�������(AI)

	//����AI�����λ��
	chessIndexArr[chessSize][0]=x;	//��¼������
	chessIndexArr[chessSize][1]=y;	//��¼������
	chessSize++;	//������������

        	if (checkWin(pointers[x][y])) {
            		gameFlag = "end";
            		JOptionPane.showMessageDialog(mainFrame, "�׷�(AI)��ʤ!");
        	}
        	isBlackTurn = true; // �л��غ�
        	repaint();
    }

    /**
     * ���ѡ�����λ�ã���Χ�����ӵ�λ�ã�
     */
    private void placeRandomMove() {
        	List<Point> validPoints = new ArrayList<>();
        
        	// �ռ����к���λ�ã���Χ3x3��Χ�������ӣ�
        	for (int i = 0; i < ROWS; i++) {
            		for (int j = 0; j < COLS; j++) {
                			if (pointers[i][j].getState() == 0 && hasNeighbor(i, j, 2)) {
                    			validPoints.add(new Point(i, j));
                			}
            		}
        	}
        
        	// ���û�к���λ�ã�ѡ�������λ
        	if (validPoints.isEmpty()) {
            		for (int i = 0; i < ROWS; i++) {
                			for (int j = 0; j < COLS; j++) {
                    			if (pointers[i][j].getState() == 0) {
                        				validPoints.add(new Point(i, j));
                    			}
                			}
            		}
        	}
        
        	// ���ѡ������
        	if (!validPoints.isEmpty()) {
            		Point p = validPoints.get(new Random().nextInt(validPoints.size()));
            		placeAIPiece(p.x, p.y);
        	}
    }

    /**
     * ���ָ��λ����Χ�Ƿ�������
     * @param range ��鷶Χ��1=���ڣ�2=��Χ����ȣ�
     */
    private boolean hasNeighbor(int x, int y, int range) {
        	for (int i = Math.max(0, x - range); i <= Math.min(ROWS - 1, x + range); i++) {
            		for (int j = Math.max(0, y - range); j <= Math.min(COLS - 1, y + range); j++) {
                			if (i == x && j == y) continue;
                			if (pointers[i][j].getState() != 0) return true;
            		}
        	}
        	return false;
    }

    /**
     * ����Ƿ��ʤ
     * @param pointer ��ǰ���ӵĸ���
     * @return �Ƿ��ʤ
     */
    private boolean checkWin(Pointer pointer) {
        	int i = pointer.getI();                // ��ȡ������
        	int j = pointer.getJ();               // ��ȡ������
        	int state = pointer.getState();        // ��ȡ����״̬
        	if (state == 0) return false;          // ��λ��ֱ�ӷ���false

        	// ����ĸ������Ƿ���������
        	return countConsecutive(i, j, 1, 0) >= 5 ||   // ˮƽ����
               	countConsecutive(i, j, 0, 1) >= 5 ||   // ��ֱ����
               	countConsecutive(i, j, 1, 1) >= 5 ||   // �Խ���(\)
               	countConsecutive(i, j, 1, -1) >= 5;    // �Խ���(/)
    }

    /**
     * ͳ��������ͬ���ӵ�����
     * @param i ��ʼ��
     * @param j ��ʼ��
     * @param di �з���(1/0/-1)
     * @param dj �з���(1/0/-1)
     * @return ������������
     */
    private int countConsecutive(int i, int j, int di, int dj) {
        	int state = pointers[i][j].getState();  // ��ǰ����״̬
        	int count = 1;                         // ������(�����Լ�)

        	// ������
        	for (int step = 1; step < 5; step++) {
            		int ni = i + di * step;           // ��������
            		int nj = j + dj * step;           // ��������
            		// �����߽�����Ӳ�ƥ����ֹͣ
            		if (ni < 0 || ni >= ROWS || nj < 0 || nj >= COLS || 
                			pointers[ni][nj].getState() != state) {
                			break;
            		}
            		count++;                          // ���Ӽ���
        	}

        	// ������
        	for (int step = 1; step < 5; step++) {
            		int ni = i - di * step;           // ��������(������)
            		int nj = j - dj * step;           // ��������(������)
            		// �����߽�����Ӳ�ƥ����ֹͣ
            		if (ni < 0 || ni >= ROWS || nj < 0 || nj >= COLS || 
                			pointers[ni][nj].getState() != state) {
                			break;
            		}
            		count++;                          // ���Ӽ���
        	}

        	return count;                         // ��������
    }

    /**
     * ������Ϸ����
     */
    public void paint(Graphics g) {
        	super.paint(g);                      // ���ø�����Ʒ���
        	drawGrid(g);                         // ������������
        	draw5point(g);                       // ���������λ��
        	drawPointer(g);                      // ��������ָʾ��
        	drawPieces(g);                       // ������������
    }

    /**
     * ������������
     */
    public void drawPieces(Graphics g) {
        	for (Pointer[] row : pointers) {
            		for (Pointer pointer : row) {
                			if (pointer.getState() == 1) {  // ����
                    			g.setColor(Color.BLACK);
                    			g.fillOval(pointer.getX() - 15, pointer.getY() - 15, 30, 30);
                			} else if (pointer.getState() == 2) {  // ����
                    			g.setColor(Color.WHITE);
                    			g.fillOval(pointer.getX() - 15, pointer.getY() - 15, 30, 30);
                    			g.setColor(Color.BLACK);   // ����߿�
                    			g.drawOval(pointer.getX() - 15, pointer.getY() - 15, 30, 30);
                			}
            		}
        	}
    }

    /**
     * ��������ָʾ��������ģʽ
     */
    private void drawPointer(Graphics g) {
        	for (Pointer[] row : pointers) {
            		for (Pointer pointer : row) {
                			pointer.draw(g);             // ����ÿ�����ӵĻ��Ʒ���
            		}
        	}
    }

    /**
     * ���������λ��(��Ԫ����λ)
     */ private void draw5point(Graphics g) {
        	g.setColor(Color.BLACK);
        	int[] xs = {142, 462, 142, 462, 302};  // ������x����
        	int[] ys = {142, 142, 462, 462, 302};  // ������y����
        	for (int i = 0; i < 5; i++) {
           		g.fillArc(xs[i], ys[i], 8, 8, 0, 360); // ����ʵ��Բ��
        	}
    }

   /**
 * ������������
 */
private void drawGrid(Graphics g) {
    	// �������䱳������������������
    	GradientPaint gradient = new GradientPaint(
        		0, 0, currentBoardColor.brighter(), // ��ʼ��ɫ��������
        		getWidth(), getHeight(), currentBoardColor.darker()); // ������ɫ��������
        
    	// ���ý��仭��
    	((Graphics2D)g).setPaint(gradient);
    	// �������̱���������2���ر߿�
    	g.fillRect(25, 25, 562, 562);
    
    	// ���ÿ����ʹ������ƽ��
    	Graphics2D g2d = (Graphics2D)g;
    	g2d.setRenderingHint(
        	RenderingHints.KEY_ANTIALIASING, 
        	RenderingHints.VALUE_ANTIALIAS_ON);
    
    	// ���ð�͸����ɫ������
    	g2d.setColor(new Color(0, 0, 0, 150)); // ͸����150/255
    	g2d.setStroke(new BasicStroke(1.5f));  // �߿�1.5����
    
    	int start = 26;  // ��ʼ����
    	int end = 586;   // ��������
    	int dis = 40;    // ���Ӽ��
    
    	// ���ƺ���
    	for (int i = 0; i <= ROWS; i++) {
        		int y = start + i * dis;
        		g2d.drawLine(start, y, end, y);
    	}
    
    	// ��������
    	for (int i = 0; i <= COLS; i++) {
        		int x = start + i * dis;
        		g2d.drawLine(x, start, x, end);
    	}
}

/**
 * �����˵�����
 */
private Font createFont() {
    	return new Font("����", Font.BOLD, 18); // ʹ�����壬�Ӵ֣�18��
}

/**
 * ������Ϸ�˵�
 */
private void createMenu() {
    	jmb = new JMenuBar();  // �����˵���
    	Font tFont = createFont();  // ��ȡ�˵�����

    	// 1. ����"��Ϸ"�˵�
    	JMenu gameMenu = new JMenu("��Ϸ");
    	gameMenu.setFont(tFont);
    
    	// 2. ����"���̷��"�Ӳ˵�
    	JMenu colorMenu = new JMenu("���̷��");
    	colorMenu.setFont(new Font("����", Font.BOLD, 18));
    	colorMenu.setIcon(new ColorIcon(Color.GRAY, 16, 16));
    
    	String[] colorNames = {"����ԭľ","ǳɫľ��","���ظ���","�װ׼�Լ","�ƺֵ���",
                          	"��ɫ����","��������","�����۲�","�ִ��ҵ�","�ݻ���ɫ","��������"};
    
    	for (int i = 0; i < BOARD_COLORS.length; i++) {
        	JMenuItem colorItem = new JMenuItem(colorNames[i]);
        	colorItem.setFont(new Font("����", Font.BOLD, 18));
        	colorItem.setIcon(new ColorIcon(BOARD_COLORS[i], 12, 12));
        	colorItem.setActionCommand("color_" + i);
        	colorItem.addActionListener(this);
        	colorMenu.add(colorItem);
        
        	if (i == 4 || i == 7) {
            		colorMenu.addSeparator();
        	}
    }
    
    // 3. �����Ϸ�˵���
    JMenuItem newHumanGame = new JMenuItem("���˶�ս");
    newHumanGame.setFont(tFont);
    newHumanGame.setActionCommand("human");
    
    JMenuItem newAIGame = new JMenuItem("�˻���ս");
    newAIGame.setFont(tFont);
    newAIGame.setActionCommand("ai");
    
    JMenuItem restart = new JMenuItem("���¿�ʼ");
    restart.setFont(tFont);
    restart.setActionCommand("restart");

    JMenuItem Save=new JMenuItem("����");
    Save.setFont(tFont);
    Save.setActionCommand("Save");
    
    JMenuItem Continue=new JMenuItem("����");
    Continue.setFont(tFont);
    Continue.setActionCommand("Continue");
	
    JMenuItem Back=new JMenuItem("����");
    Back.setFont(tFont);
    Back.setActionCommand("Back");

    JMenuItem exit = new JMenuItem("�˳�");
    exit.setFont(tFont);
    exit.setActionCommand("exit");
    
    gameMenu.add(newHumanGame);
    gameMenu.add(newAIGame);
    gameMenu.add(restart);
    gameMenu.add(Save);
    gameMenu.add(Continue);
    gameMenu.add(Back);
    gameMenu.add(exit);
    
    // 4. ����"����"�˵�
    JMenu helpMenu = new JMenu("����");
    helpMenu.setFont(tFont);
    
    JMenuItem help = new JMenuItem("��������");
    help.setFont(tFont);
    help.setActionCommand("help");
    
    JMenuItem about = new JMenuItem("����");
    about.setFont(tFont);
    about.setActionCommand("about");
    
    helpMenu.add(help);
    helpMenu.add(about);
    
    // ��װ�˵���
    jmb.add(gameMenu);
    jmb.add(colorMenu);
    jmb.add(helpMenu);
    
    mainFrame.setJMenuBar(jmb);
    
    // Ϊ�˵�������¼�����
    newHumanGame.addActionListener(this);
    newAIGame.addActionListener(this);
    restart.addActionListener(this);
    exit.addActionListener(this);
    Save.addActionListener(this);
    Continue.addActionListener(this);
    Back.addActionListener(this);
    help.addActionListener(this);
    about.addActionListener(this);
}

// ��ɫͼ���ڲ���
private class ColorIcon implements Icon {
    	private final Color color;
    	private final int width;
    	private final int height;

    	public ColorIcon(Color color, int width, int height) {
        	this.color = color;
        	this.width = width;
        	this.height = height;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        	g.setColor(color);
        	g.fillRect(x, y, width, height);
        	g.setColor(Color.BLACK);
        	g.drawRect(x, y, width, height);
    }

    @Override
    public int getIconWidth() { return width; }

    @Override
    public int getIconHeight() { return height; }
}
    /**
     * �����¼�����
     * ���ܣ�����˵������¼�
     */
    @Override
    public void actionPerformed(ActionEvent e) {
	 String command = e.getActionCommand();// ��ȡ�����ַ���
        
        // ������ɫѡ������
        if (command.startsWith("color_")) {
            // ����������ȡ��ɫ����
            int colorIndex = Integer.parseInt(command.substring(6));
            currentBoardColor = BOARD_COLORS[colorIndex]; // ���µ�ǰ��ɫ
            
            // ���½��汳��
            mainFrame.getContentPane().setBackground(currentBoardColor);
            // ǿ��ˢ��UI���
            SwingUtilities.updateComponentTreeUI(mainFrame); 
            
            // ��ʾ������ʾ
            JOptionPane.showMessageDialog(mainFrame, 
                "���л�Ϊ " + ((JMenuItem)e.getSource()).getText() + " ���",
                "��ʾ", 
                JOptionPane.INFORMATION_MESSAGE);
                
            repaint(); // �ػ�����
            return;
        }
                switch (command) {
            	case "human":                    // ���˶�սģʽ
                		gameMode = "human";
                		resetGame();
		JOptionPane.showMessageDialog(mainFrame,
			"���л�Ϊ���˶�սģʽ\n��λ������������������","ģʽ�л�",
			JOptionPane.INFORMATION_MESSAGE);
                		break;
            	case "ai":                       // �˻���սģʽ
                		gameMode = "ai";
                		resetGame();
		JOptionPane.showMessageDialog(mainFrame,
			"���л�Ϊ�˻���սģʽ\n����ִ�����У�AIִ��","ģʽ�л�",
			JOptionPane.INFORMATION_MESSAGE);
                		break;
            	case "restart":                  // ���¿�ʼ��Ϸ
                		resetGame();
                		break;
	case "Save":	//������Ϸ
		saveGame();
		break;
	case "Continue":	//������Ϸ
		readGame();
		repaint();
		break;
	case "Back":	//����
		undoMove();
		break;
            	case "exit":                     // �˳���Ϸ
                		System.exit(0);
                		break;
            	case "help":                     // ��ʾ������Ϣ
                		JOptionPane.showMessageDialog(mainFrame, 
                    	"����˵��:\n" +
                    	"1. ������̽�����������\n" +
                    	"2. �ڷ����У���������\n" +
                    	"3. ���ںᡢ����б�������������߻�ʤ\n"+
		"4.ʹ�ò˵����Ա���/������Ϸ�����塢�������̷��");
                		break;
            	case "about":                   // ��ʾ������Ϣ
                		JOptionPane.showMessageDialog(mainFrame, 
                    	"��������Ϸ v1.0\n" +
                    	"ʵ�����˶�ս���˻���ս����ģʽ");
                		break;
        	}
    }
	public static void saveGame(){
		JFileChooser jfc=new JFileChooser();//�������û����ļ�ϵͳ��ѡ���ļ���Ŀ¼
		int saveDialog=jfc.showSaveDialog(null);
		if(saveDialog==JFileChooser.APPROVE_OPTION){//�ж��û��Ƿ����˶Ի����еġ����桱��ť
			File file=jfc.getSelectedFile();//�����û����ļ�ѡ��Ի���ѡ�е��ļ�
			try{
				FileWriter fileWriter=new FileWriter(file);//���ڰ��ַ�������д�뵽�ļ���
				BufferedWriter bufferedWriter=new BufferedWriter(fileWriter);//ͨ�����ڲ�ά��һ��������������ַ�д���Ч�ʡ�
//�����Խ�����ַ����ݴ浽�������У������������˻����ֶ�ˢ�»�����������flush������ʱ���ٽ��������е��ַ�һ����д�뵽Ŀ��������������ļ���Ӧ����������С�
//�����ֱ��ʹ�õײ���ַ����������FileWriter)����ַ�������д�룬�ڴ����ַ�д��ĳ����£�ʹ��BufferWriter�ܹ�����ʵ�ʵ�����д������������Ӷ���������

				//���������Ϸ״̬
				bufferedWriter.write(chessSize+"#"+isBlackTurn);//д��chessSize��isBlackTurn�м���#����
				bufferedWriter.newLine();//����

				//д��chessIndexArr������
				//��������λ��
				for(int i=0;i<chessSize;i++){
					String str=chessIndexArr[i][0]+"#"+chessIndexArr[i][1];
					bufferedWriter.write(str);
					bufferedWriter.newLine();
				}

				bufferedWriter.close();//�ر��ļ�
				System.out.println("�ѱ���"+file.getPath());
			}catch(IOException e){
				throw new RuntimeException(e);
			}
		}
	}
	public static int stringToInt(String str){
		int temp=0;
		for(int i=0;i<str.length();i++){
			temp=temp*10+(int)(str.charAt(i)-'0');
		}
		return temp;
	}
	public void readGame(){
		//���ļ�
		JFileChooser jfc=new JFileChooser();
		int openDialog=jfc.showOpenDialog(null);
		if(openDialog==JFileChooser.APPROVE_OPTION){
			File file=jfc.getSelectedFile();
			try{
				FileReader fileReader=new FileReader(file);
				BufferedReader bufferedReader=new BufferedReader(fileReader);

				//��������
				resetGame();
				//����һ��
				String str=bufferedReader.readLine();
				String[] gameState=str.split("#");//���ַ����ָ�������#Ϊ�ָ����ָ�
				//����һ�����ݸ���chessSize��isBlackTurn
				chessSize=Integer.parseInt(gameState[0]);
				isBlackTurn=Boolean.parseBoolean(gameState[1]);

				//����chessIndexArr�����е�����
				for(int i=0;i<chessSize;i++){
					str=bufferedReader.readLine();
					String[]pos=str.split("#");
					int row=Integer.parseInt(pos[0]);
					int col=Integer.parseInt(pos[1]);
					pointers[row][col].setState((i%2==0)?1:2);
					chessIndexArr[i][0]=row;
					chessIndexArr[i][1]=col;
				}

				bufferedReader.close();//�ر��ļ�
				System.out.println("�Ѷ���"+file.getPath());
				gameFlag="start";//�ָ���Ϸ״̬
			}catch(IOException e){
				throw new RuntimeException(e);
			}
		}
	}
	/**
     * ������Ϸ״̬
     */
    private void resetGame() {
        	// �����������
        	for (Pointer[] row : pointers) {
            		for (Pointer pointer : row) {
                			pointer.setState(0);          // 0��ʾ��λ��
            		}
        	}
        	isBlackTurn = true;                  // ����Ϊ�ڷ�����
        	gameFlag = "start";                  // ������Ϸ״̬Ϊ��ʼ
        	repaint();                           // �ػ����
    }
	private void undoMove(){
		//�����������ӿɻ�
		if(chessSize==0){
			JOptionPane.showMessageDialog(mainFrame,"����ɻ�!");
			return;
		}
		//��ȡ���һ�����λ��
		int lastRow=chessIndexArr[chessSize-1][0];
		int lastCol=chessIndexArr[chessSize-1][1];

		//�����λ�õ�����
		pointers[lastRow][lastCol].setState(0);
		chessSize--;

		//�л��غ�
		isBlackTurn=!isBlackTurn;

		//������˻�ģʽ�ҵ�ǰ��AI�غϣ��׷�������Ҫ�ٻ�һ�����Ƴ�AI����һ����
		if("ai".equals(gameMode)&&!isBlackTurn&&chessSize>0){
			lastRow=chessIndexArr[chessSize-1][0];
			lastCol=chessIndexArr[chessSize-1][1];
			pointers[lastRow][lastCol].setState(0);
			chessSize--;
			isBlackTurn=true;//�л�����һغ�
		}
		
		//������Ϸ״̬Ϊ������
		gameFlag="start";

		repaint();
	}
}

/**
 * ���̸����� - ��ʾ�����ϵ�ÿ�������
 */
class Pointer {
    	public int i;                          // ������
    	public int j;                          // ������
    	private int x;                          // ��Ļx����
    	private int y;                          // ��Ļy����
    	private int h = 36;                     // ָʾ����С
    	private boolean show = false;           // �Ƿ���ʾָʾ��
    	private int state = 0;                  // ״̬(0=��/1=��/2=��)

    /**
     * ���캯��
     * @param i ������
     * @param j ������
     * @param x ��Ļx����
     * @param y ��Ļy����
     */
    public Pointer(int i, int j, int x, int y) {
        	this.i = i;
        	this.j = j;
        	this.x = x;
        	this.y = y;
    }

    /**
     * ����ָʾ��(�����ͣʱ��ʾ�ĺ�ɫ����)
     */
    public void draw(Graphics g) {

        	if (show) {                         // �����Ҫ��ʾ
            		g.setColor(Color.RED);          // ���ú�ɫ
            		Graphics2D g2d = (Graphics2D) g; // ת��Ϊ2D��ͼ
            		g2d.setStroke(new BasicStroke(2.0f)); // ����������ϸ
            
            		// �ĸ�������
            		int[] xPoints = {x-h/2, x+h/2, x+h/2, x-h/2};
            		int[] yPoints = {y-h/2, y-h/2, y+h/2, y+h/2};
            
            		// �����ĸ��ǵĶ�����
            		for (int k = 0; k < 4; k++) {
                			int next = (k + 1) % 4;     // ��һ��������
                			g2d.drawLine(xPoints[k], yPoints[k], 
                             		xPoints[k] + (xPoints[next]-xPoints[k])/4, 
                             		yPoints[k] + (yPoints[next]-yPoints[k])/4);
            		}
        	}
    }

    /**
     * �жϵ��Ƿ��ڸ��ӷ�Χ��
     * @param x ��x����
     * @param y ��y����
     * @return �Ƿ��ڷ�Χ��
     */
    public boolean isPoint(int x, int y) {
        	// ��������Ƿ��ڸ��Ӿ��η�Χ��
        	return x > this.x - h/2 && y > this.y - h/2 && 
               		x < this.x + h/2 && y < this.y + h/2;
    }

    // ===== Getter��Setter���� =====
    public boolean isShow() { return show; }
    public void setShow(boolean show) { this.show = show; }
    public int getState() { return state; }
    public void setState(int state) { this.state = state; }
    public int getI() { return i; }
    public int getJ() { return j; }
    public int getX() { return x; }
    public int getY() { return y; }
}