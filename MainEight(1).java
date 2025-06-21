package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import java.io.*;

/**
 * 主启动类
 */
public class MainEight {
    	public static void main(String[] args) {
	//初始化游戏界面
    	GameFrame frame = new GameFrame();		// 创建游戏主窗口
       	GamePanel panel = new GamePanel(frame);	       // 创建游戏主面板
        	frame.add(panel);	// 将面板添加到窗口
        	frame.setVisible(true);	 // 显示窗口

	//欢迎消息，增加模式选择提示
	Object[] options={"人人对战","人机对战"};
	int choice=JOptionPane.showOptionDialog(frame,
		"欢迎来到五子棋游戏!\n请选择游戏模式:",
		"欢迎您",
		JOptionPane.DEFAULT_OPTION,
		JOptionPane.INFORMATION_MESSAGE,
		null,
		options,
		options[0]);

	//根据用户选择设置游戏模式
	if(choice==0){
		panel.setGameMode("human");	//人人对战模式
	}else{
		panel.setGameMode("ai");	//人机对战模式
	}

	//显示操作指南
	JOptionPane.showMessageDialog(frame, 
                    "游戏操作指南:\n" +
                    "1. 点击棋盘交叉点放置棋子\n" +
                    "2. 黑方先行，轮流下棋\n" +
                    "3. 先在横、竖、斜方向连成五子者获胜\n"+
	"4.使用菜单可以保存/加载游戏、悔棋、更换棋盘风格",
	"操作指南",
	JOptionPane.INFORMATION_MESSAGE);
	}
}

/**
 * 游戏主窗口类
 */
class GameFrame extends JFrame {
	private static final long serialVersionUID = 1L;

	public GameFrame() {
        		setTitle("五子棋");                     // 设置窗口标题
        		setSize(620, 670);                     // 设置窗口大小
        		getContentPane().setBackground(new Color(209, 146, 17));	 // 设置背景颜色(木质黄色)
        		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	 // 设置关闭按钮行为
        		setLocationRelativeTo(null);           // 窗口居中显示
        		setResizable(false);                   // 禁止调整窗口大小
   	}
}

/**
 * 游戏主面板类 - 包含所有游戏逻辑
 */
class GamePanel extends JPanel implements ActionListener {

	public void setGameMode(String mode){
		this.gameMode=mode;
		resetGame();	//重置游戏状态
		String modeName=mode.equals("human")?"人人对战":"人机对战";
		JOptionPane.showMessageDialog(mainFrame,"已选择"+modeName+"模式\n"+
			(mode.equals("human")?"两位玩家轮流点击棋盘下棋":"您将执黑先行，AI执白"),"游戏模式",
			JOptionPane.INFORMATION_MESSAGE);
	}

 	// 扩展颜色选项（新增6种颜色，共11种选择）
    	private static final Color[] BOARD_COLORS = {
        	// 木质色系
        	new Color(209, 146, 17),   // 原木色
        	new Color(222, 184, 135),  // 浅木色
        	new Color(139, 69, 19),    // 深棕色
        	new Color(255, 228, 196),  // 米白色
        	new Color(210, 180, 140), // 黄褐色
        
        	// 新增色系
        	new Color(100, 149, 237),  // 蓝色
        	new Color(144, 238, 144),  // 浅绿色
        	new Color(255, 182, 193),  // 粉色
        	new Color(220, 220, 220),  // 灰色
       	new Color(255, 215, 0),    // 金色
        	new Color(147, 112, 219)   // 紫色
    	};
    
    	private Color currentBoardColor = BOARD_COLORS[0]; // 当前棋盘颜色
    
    	private static final long serialVersionUID = 1L;
    
    	// 游戏组件
    	private JMenuBar jmb;                      // 菜单栏
   	private GameFrame mainFrame;               // 主窗口引用
     	// 游戏常量
    	private static final int ROWS = 15;               // 棋盘行数
    	private static final int COLS = 15;               // 棋盘列数
    
    	// 游戏状态变量
    	private String gameFlag = "start";         // 游戏状态(start/end)
    	private String gameMode = "human";         // 游戏模式(human/ai)
    	private static boolean isBlackTurn = true;        // 当前回合(true=黑方/false=白方)
	private static int chessSize=0;//棋子总个数
	private static int [][] chessIndexArr=new int[256][2];//chessIndexArr[i][0]为第i个棋子的行位置，chessIndexArr[i][0]为第i个棋子的列位置
    
    	// 游戏数据
    	public Pointer[][] pointers = new Pointer[ROWS][COLS]; // 棋盘格子数组
    	private Random random = new Random();      // 随机数生成器(用于AI)

    /**
     * 构造函数
     * @param mainFrame 主窗口引用
     */
    	public GamePanel(GameFrame mainFrame) {
        		this.setLayout(null);                  // 不使用布局管理器
        		this.setOpaque(false);                 // 设置透明背景
        		this.mainFrame = mainFrame;            // 保存主窗口引用
        
        		// 初始化游戏
        		createMenu();                          // 创建菜单
        		createMouseListener();                 // 设置鼠标监听
        		createPointers();                      // 初始化棋盘格子
    	}

    /**
     * 初始化棋盘所有格子
     */
    	private void createPointers() {
        	int x, y, start = 26;                 // start=棋盘起始坐标(留出边距)
        	for (int i = 0; i < ROWS; i++) {
            		for (int j = 0; j < COLS; j++) {
                			x = i * 40 + start;            // 计算每个格子的x坐标(40=格子间距)
                			y = j * 40 + start;            // 计算每个格子的y坐标
                		pointers[i][j] = new Pointer(i, j, x, y); // 创建格子对象
            		}
  	}
    }

    /**
     * 创建鼠标监听器
     */
    private void createMouseListener() {
        	MouseAdapter mouseAdapter = new MouseAdapter() {
           	 /**
           	  * 鼠标移动事件 - 用于显示落子指示器
            	 */
            	public void mouseMoved(MouseEvent e) {
                		if (!"start".equals(gameFlag)) return; // 游戏未开始时不处理
                
               			int x = e.getX();              // 获取鼠标x坐标
                			int y = e.getY();              // 获取鼠标y坐标
                
                		// 遍历所有格子，检测鼠标悬停
                		for (Pointer[] row : pointers) {
                    		for (Pointer pointer : row) {
                        			pointer.setShow(pointer.isPoint(x, y)); // 设置指示器显示状态
                    		}
                		}
                		repaint();                     // 重绘界面
            	}

            /**
             * 鼠标点击事件 - 处理玩家落子
             */
            	public void mouseClicked(MouseEvent e) {
                		// 游戏未开始或非玩家回合时不处理
                		if (!"start".equals(gameFlag) || (!isBlackTurn && "ai".equals(gameMode))) return;
                
                		int x = e.getX();              // 获取点击x坐标
                		int y = e.getY();              // 获取点击y坐标
                
                		// 遍历所有格子，检测点击位置
                		for (Pointer[] row : pointers) {
                    		for (Pointer pointer : row) {
                        			if (pointer.isPoint(x, y)) {  // 找到被点击的格子
                            				if (pointer.getState() == 0) {  // 如果格子为空
                                					// 根据当前回合设置棋子颜色(1=黑/2=白)
                                					pointer.setState(isBlackTurn ? 1 : 2); 

						//存储棋子顺序（为第几个棋子）
						chessIndexArr[chessSize][0]=pointer.i;
						chessIndexArr[chessSize][1]=pointer.j;
						chessSize++;
                                
                                					if (checkWin(pointer)) {  // 检查是否获胜
                                    					gameFlag = "end";    // 游戏结束
                                    					JOptionPane.showMessageDialog(mainFrame, 
                                        					(isBlackTurn ? "黑方" : "白方") + "获胜!");
                                					} else {
                                    					isBlackTurn = !isBlackTurn; // 切换回合
                                    					// AI模式且轮到AI时自动走棋
                                    					if ("ai".equals(gameMode) && !isBlackTurn) {
                                        						aiMove();
                                    					}
                                					}
                                					repaint();     // 重绘界面
                            				}
                            				return;            // 处理完毕退出
                        			}
                    		}
                		}
            	}
        };
        
        // 注册鼠标监听器
        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);
    }

    /**
     * AI走棋方法
     * 实现多级决策：
     * 1. 检查AI能否直接获胜
     * 2. 阻止玩家即将获胜
     * 3. 寻找最优进攻/防守位置
     * 4. 随机选择合理位置
     */
    private void aiMove() {
        	// 1. 检查AI是否有获胜机会
        	Point winMove = findWinningMove(2); // 2代表白子(AI)
        	if (winMove != null) {
            		placeAIPiece(winMove.x, winMove.y);
            		return;
        	}

        	// 2. 阻止玩家获胜
        	Point blockMove = findWinningMove(1); // 1代表黑子(玩家)
        	if (blockMove != null) {
            		placeAIPiece(blockMove.x, blockMove.y);
            		return;
        	}

        	// 3. 寻找最优策略位置
        	Point bestMove = findStrategicMove();
        	if (bestMove != null) {
            		placeAIPiece(bestMove.x, bestMove.y);
            		return;
        	}

        	// 4. 随机选择合理位置
        	placeRandomMove();
    }

    /**
     * 寻找获胜机会（对指定棋子类型）
     * @param pieceType 1=黑子(玩家) 2=白子(AI)
     * @return 能形成五连的位置，没有则返回null
     */
    private Point findWinningMove(int pieceType) {
        	for (int i = 0; i < ROWS; i++) {
            		for (int j = 0; j < COLS; j++) {
                			if (pointers[i][j].getState() == 0) { // 空位置
                    			pointers[i][j].setState(pieceType); // 模拟落子
                    			if (checkWin(pointers[i][j])) {
                        				pointers[i][j].setState(0); // 恢复空位置
                        				return new Point(i, j);
                    			}
                    			pointers[i][j].setState(0); // 恢复空位置
                			}
            		}
        	}
        	return null;
    }

    /**
     * 寻找战略位置（评估函数）
     * @return 最佳落子位置
     */
    private Point findStrategicMove() {
        	int[][] scoreMap = new int[ROWS][COLS]; // 评分矩阵
        
        	// 评估每个空位的价值
        	for (int i = 0; i < ROWS; i++) {
            		for (int j = 0; j < COLS; j++) {
                			if (pointers[i][j].getState() == 0) {
                    			// 进攻价值（AI的白子）
                    			int attackScore = evaluatePosition(i, j, 2); 
                    			// 防守价值（玩家的黑子）
                    			int defendScore = evaluatePosition(i, j, 1);
                    			// 综合评分（防守权重稍高）
                    			scoreMap[i][j] = attackScore * 3 + defendScore * 4;
                			}
            		}
        	}
        
        	// 找出最高分位置
        	return findBestPosition(scoreMap);
    }

    /**
     * 位置评估函数
     * @param x 行坐标
     * @param y 列坐标
     * @param pieceType 棋子类型
     * @return 该位置的价值评分
     */
    private int evaluatePosition(int x, int y, int pieceType) {
        	int score = 0;
        	int[][] directions = {{1,0}, {0,1}, {1,1}, {1,-1}}; // 四个方向
        
        	for (int[] dir : directions) {
            		int count = 1; // 当前连续棋子数
            		int empty = 0;  // 两端空位
            
            		// 正向检测
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
            
            		// 反向检测
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
            
            		// 根据连子数和空位计算分数
            		if (count >= 5) score += 100000; // 能赢
            		else if (count == 4 && empty >= 1) score += 10000; // 活四
            		else if (count == 3 && empty >= 1) score += 1000;  // 活三
            		else if (count == 2 && empty >= 1) score += 100;   // 活二
            		else if (count == 1 && empty >= 1) score += 10;    // 活一
        	}
        
        	return score;
    }

    /**
     * 从评分矩阵找出最佳位置
     * @param scoreMap 评分矩阵
     * @return 最佳位置坐标
     */
    private Point findBestPosition(int[][] scoreMap) {
        	int maxScore = 0;
        	List<Point> bestPoints = new ArrayList<>();
        
        	// 找出所有最高分位置
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
        
        	// 随机选择一个最高分位置（避免固定模式）
        	return bestPoints.isEmpty() ? null : 
               	bestPoints.get(new Random().nextInt(bestPoints.size()));
    }

    /**
     * AI落子并检查胜负
     */
    private void placeAIPiece(int x, int y) {
        	pointers[x][y].setState(2); // 2代表白子(AI)

	//保存AI下棋的位置
	chessIndexArr[chessSize][0]=x;	//记录行坐标
	chessIndexArr[chessSize][1]=y;	//记录列坐标
	chessSize++;	//棋子总数增加

        	if (checkWin(pointers[x][y])) {
            		gameFlag = "end";
            		JOptionPane.showMessageDialog(mainFrame, "白方(AI)获胜!");
        	}
        	isBlackTurn = true; // 切换回合
        	repaint();
    }

    /**
     * 随机选择合理位置（周围有棋子的位置）
     */
    private void placeRandomMove() {
        	List<Point> validPoints = new ArrayList<>();
        
        	// 收集所有合理位置（周围3x3范围内有棋子）
        	for (int i = 0; i < ROWS; i++) {
            		for (int j = 0; j < COLS; j++) {
                			if (pointers[i][j].getState() == 0 && hasNeighbor(i, j, 2)) {
                    			validPoints.add(new Point(i, j));
                			}
            		}
        	}
        
        	// 如果没有合理位置，选择任意空位
        	if (validPoints.isEmpty()) {
            		for (int i = 0; i < ROWS; i++) {
                			for (int j = 0; j < COLS; j++) {
                    			if (pointers[i][j].getState() == 0) {
                        				validPoints.add(new Point(i, j));
                    			}
                			}
            		}
        	}
        
        	// 随机选择并落子
        	if (!validPoints.isEmpty()) {
            		Point p = validPoints.get(new Random().nextInt(validPoints.size()));
            		placeAIPiece(p.x, p.y);
        	}
    }

    /**
     * 检查指定位置周围是否有棋子
     * @param range 检查范围（1=相邻，2=周围两格等）
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
     * 检查是否获胜
     * @param pointer 当前落子的格子
     * @return 是否获胜
     */
    private boolean checkWin(Pointer pointer) {
        	int i = pointer.getI();                // 获取行索引
        	int j = pointer.getJ();               // 获取列索引
        	int state = pointer.getState();        // 获取棋子状态
        	if (state == 0) return false;          // 空位置直接返回false

        	// 检查四个方向是否连成五子
        	return countConsecutive(i, j, 1, 0) >= 5 ||   // 水平方向
               	countConsecutive(i, j, 0, 1) >= 5 ||   // 垂直方向
               	countConsecutive(i, j, 1, 1) >= 5 ||   // 对角线(\)
               	countConsecutive(i, j, 1, -1) >= 5;    // 对角线(/)
    }

    /**
     * 统计连续相同棋子的数量
     * @param i 起始行
     * @param j 起始列
     * @param di 行方向(1/0/-1)
     * @param dj 列方向(1/0/-1)
     * @return 连续棋子数量
     */
    private int countConsecutive(int i, int j, int di, int dj) {
        	int state = pointers[i][j].getState();  // 当前棋子状态
        	int count = 1;                         // 计数器(包含自己)

        	// 正向检查
        	for (int step = 1; step < 5; step++) {
            		int ni = i + di * step;           // 计算新行
            		int nj = j + dj * step;           // 计算新列
            		// 超出边界或棋子不匹配则停止
            		if (ni < 0 || ni >= ROWS || nj < 0 || nj >= COLS || 
                			pointers[ni][nj].getState() != state) {
                			break;
            		}
            		count++;                          // 增加计数
        	}

        	// 反向检查
        	for (int step = 1; step < 5; step++) {
            		int ni = i - di * step;           // 计算新行(反方向)
            		int nj = j - dj * step;           // 计算新列(反方向)
            		// 超出边界或棋子不匹配则停止
            		if (ni < 0 || ni >= ROWS || nj < 0 || nj >= COLS || 
                			pointers[ni][nj].getState() != state) {
                			break;
            		}
            		count++;                          // 增加计数
        	}

        	return count;                         // 返回总数
    }

    /**
     * 绘制游戏界面
     */
    public void paint(Graphics g) {
        	super.paint(g);                      // 调用父类绘制方法
        	drawGrid(g);                         // 绘制棋盘网格
        	draw5point(g);                       // 绘制五个定位点
        	drawPointer(g);                      // 绘制落子指示器
        	drawPieces(g);                       // 绘制所有棋子
    }

    /**
     * 绘制所有棋子
     */
    public void drawPieces(Graphics g) {
        	for (Pointer[] row : pointers) {
            		for (Pointer pointer : row) {
                			if (pointer.getState() == 1) {  // 黑棋
                    			g.setColor(Color.BLACK);
                    			g.fillOval(pointer.getX() - 15, pointer.getY() - 15, 30, 30);
                			} else if (pointer.getState() == 2) {  // 白棋
                    			g.setColor(Color.WHITE);
                    			g.fillOval(pointer.getX() - 15, pointer.getY() - 15, 30, 30);
                    			g.setColor(Color.BLACK);   // 白棋边框
                    			g.drawOval(pointer.getX() - 15, pointer.getY() - 15, 30, 30);
                			}
            		}
        	}
    }

    /**
     * 绘制落子指示器，人人模式
     */
    private void drawPointer(Graphics g) {
        	for (Pointer[] row : pointers) {
            		for (Pointer pointer : row) {
                			pointer.draw(g);             // 调用每个格子的绘制方法
            		}
        	}
    }

    /**
     * 绘制五个定位点(天元和星位)
     */ private void draw5point(Graphics g) {
        	g.setColor(Color.BLACK);
        	int[] xs = {142, 462, 142, 462, 302};  // 五个点的x坐标
        	int[] ys = {142, 142, 462, 462, 302};  // 五个点的y坐标
        	for (int i = 0; i < 5; i++) {
           		g.fillArc(xs[i], ys[i], 8, 8, 0, 360); // 绘制实心圆点
        	}
    }

   /**
 * 绘制棋盘网格
 */
private void drawGrid(Graphics g) {
    	// 创建渐变背景（从亮部到暗部）
    	GradientPaint gradient = new GradientPaint(
        		0, 0, currentBoardColor.brighter(), // 起始颜色（更亮）
        		getWidth(), getHeight(), currentBoardColor.darker()); // 结束颜色（更暗）
        
    	// 设置渐变画笔
    	((Graphics2D)g).setPaint(gradient);
    	// 绘制棋盘背景（留出2像素边框）
    	g.fillRect(25, 25, 562, 562);
    
    	// 启用抗锯齿使线条更平滑
    	Graphics2D g2d = (Graphics2D)g;
    	g2d.setRenderingHint(
        	RenderingHints.KEY_ANTIALIASING, 
        	RenderingHints.VALUE_ANTIALIAS_ON);
    
    	// 设置半透明黑色网格线
    	g2d.setColor(new Color(0, 0, 0, 150)); // 透明度150/255
    	g2d.setStroke(new BasicStroke(1.5f));  // 线宽1.5像素
    
    	int start = 26;  // 起始坐标
    	int end = 586;   // 结束坐标
    	int dis = 40;    // 格子间距
    
    	// 绘制横线
    	for (int i = 0; i <= ROWS; i++) {
        		int y = start + i * dis;
        		g2d.drawLine(start, y, end, y);
    	}
    
    	// 绘制竖线
    	for (int i = 0; i <= COLS; i++) {
        		int x = start + i * dis;
        		g2d.drawLine(x, start, x, end);
    	}
}

/**
 * 创建菜单字体
 */
private Font createFont() {
    	return new Font("宋体", Font.BOLD, 18); // 使用宋体，加粗，18号
}

/**
 * 创建游戏菜单
 */
private void createMenu() {
    	jmb = new JMenuBar();  // 创建菜单栏
    	Font tFont = createFont();  // 获取菜单字体

    	// 1. 创建"游戏"菜单
    	JMenu gameMenu = new JMenu("游戏");
    	gameMenu.setFont(tFont);
    
    	// 2. 创建"棋盘风格"子菜单
    	JMenu colorMenu = new JMenu("棋盘风格");
    	colorMenu.setFont(new Font("宋体", Font.BOLD, 18));
    	colorMenu.setIcon(new ColorIcon(Color.GRAY, 16, 16));
    
    	String[] colorNames = {"经典原木","浅色木质","深棕复古","米白简约","黄褐典雅",
                          	"蓝色海洋","清新绿意","浪漫粉彩","现代灰调","奢华金色","神秘紫韵"};
    
    	for (int i = 0; i < BOARD_COLORS.length; i++) {
        	JMenuItem colorItem = new JMenuItem(colorNames[i]);
        	colorItem.setFont(new Font("宋体", Font.BOLD, 18));
        	colorItem.setIcon(new ColorIcon(BOARD_COLORS[i], 12, 12));
        	colorItem.setActionCommand("color_" + i);
        	colorItem.addActionListener(this);
        	colorMenu.add(colorItem);
        
        	if (i == 4 || i == 7) {
            		colorMenu.addSeparator();
        	}
    }
    
    // 3. 添加游戏菜单项
    JMenuItem newHumanGame = new JMenuItem("人人对战");
    newHumanGame.setFont(tFont);
    newHumanGame.setActionCommand("human");
    
    JMenuItem newAIGame = new JMenuItem("人机对战");
    newAIGame.setFont(tFont);
    newAIGame.setActionCommand("ai");
    
    JMenuItem restart = new JMenuItem("重新开始");
    restart.setFont(tFont);
    restart.setActionCommand("restart");

    JMenuItem Save=new JMenuItem("保存");
    Save.setFont(tFont);
    Save.setActionCommand("Save");
    
    JMenuItem Continue=new JMenuItem("继续");
    Continue.setFont(tFont);
    Continue.setActionCommand("Continue");
	
    JMenuItem Back=new JMenuItem("悔棋");
    Back.setFont(tFont);
    Back.setActionCommand("Back");

    JMenuItem exit = new JMenuItem("退出");
    exit.setFont(tFont);
    exit.setActionCommand("exit");
    
    gameMenu.add(newHumanGame);
    gameMenu.add(newAIGame);
    gameMenu.add(restart);
    gameMenu.add(Save);
    gameMenu.add(Continue);
    gameMenu.add(Back);
    gameMenu.add(exit);
    
    // 4. 创建"帮助"菜单
    JMenu helpMenu = new JMenu("帮助");
    helpMenu.setFont(tFont);
    
    JMenuItem help = new JMenuItem("操作帮助");
    help.setFont(tFont);
    help.setActionCommand("help");
    
    JMenuItem about = new JMenuItem("关于");
    about.setFont(tFont);
    about.setActionCommand("about");
    
    helpMenu.add(help);
    helpMenu.add(about);
    
    // 组装菜单栏
    jmb.add(gameMenu);
    jmb.add(colorMenu);
    jmb.add(helpMenu);
    
    mainFrame.setJMenuBar(jmb);
    
    // 为菜单项添加事件监听
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

// 颜色图标内部类
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
     * 动作事件处理
     * 功能：处理菜单项点击事件
     */
    @Override
    public void actionPerformed(ActionEvent e) {
	 String command = e.getActionCommand();// 获取命令字符串
        
        // 处理颜色选择命令
        if (command.startsWith("color_")) {
            // 从命令中提取颜色索引
            int colorIndex = Integer.parseInt(command.substring(6));
            currentBoardColor = BOARD_COLORS[colorIndex]; // 更新当前颜色
            
            // 更新界面背景
            mainFrame.getContentPane().setBackground(currentBoardColor);
            // 强制刷新UI组件
            SwingUtilities.updateComponentTreeUI(mainFrame); 
            
            // 显示操作提示
            JOptionPane.showMessageDialog(mainFrame, 
                "已切换为 " + ((JMenuItem)e.getSource()).getText() + " 风格",
                "提示", 
                JOptionPane.INFORMATION_MESSAGE);
                
            repaint(); // 重绘棋盘
            return;
        }
                switch (command) {
            	case "human":                    // 人人对战模式
                		gameMode = "human";
                		resetGame();
		JOptionPane.showMessageDialog(mainFrame,
			"已切换为人人对战模式\n两位玩家轮流点击棋盘下棋","模式切换",
			JOptionPane.INFORMATION_MESSAGE);
                		break;
            	case "ai":                       // 人机对战模式
                		gameMode = "ai";
                		resetGame();
		JOptionPane.showMessageDialog(mainFrame,
			"已切换为人机对战模式\n您将执黑先行，AI执白","模式切换",
			JOptionPane.INFORMATION_MESSAGE);
                		break;
            	case "restart":                  // 重新开始游戏
                		resetGame();
                		break;
	case "Save":	//保存游戏
		saveGame();
		break;
	case "Continue":	//继续游戏
		readGame();
		repaint();
		break;
	case "Back":	//悔棋
		undoMove();
		break;
            	case "exit":                     // 退出游戏
                		System.exit(0);
                		break;
            	case "help":                     // 显示帮助信息
                		JOptionPane.showMessageDialog(mainFrame, 
                    	"操作说明:\n" +
                    	"1. 点击棋盘交叉点放置棋子\n" +
                    	"2. 黑方先行，轮流下棋\n" +
                    	"3. 先在横、竖、斜方向连成五子者获胜\n"+
		"4.使用菜单可以保存/加载游戏、悔棋、更换棋盘风格");
                		break;
            	case "about":                   // 显示关于信息
                		JOptionPane.showMessageDialog(mainFrame, 
                    	"五子棋游戏 v1.0\n" +
                    	"实现人人对战和人机对战两种模式");
                		break;
        	}
    }
	public static void saveGame(){
		JFileChooser jfc=new JFileChooser();//用于让用户在文件系统中选择文件或目录
		int saveDialog=jfc.showSaveDialog(null);
		if(saveDialog==JFileChooser.APPROVE_OPTION){//判断用户是否点击了对话框中的“保存”按钮
			File file=jfc.getSelectedFile();//返回用户在文件选择对话框选中的文件
			try{
				FileWriter fileWriter=new FileWriter(file);//用于把字符流数据写入到文件中
				BufferedWriter bufferedWriter=new BufferedWriter(fileWriter);//通过在内部维护一个缓冲区来提高字符写入的效率。
//它可以将多个字符先暂存到缓冲区中，当缓冲区满了或者手动刷新缓冲区（调用flush方法）时，再将缓冲区中的字符一次性写入到目标输出流（比如文件对应的输出流）中。
//相比于直接使用底层的字符输出流（如FileWriter)逐个字符或逐行写入，在大量字符写入的场景下，使用BufferWriter能够减少实际的物理写入操作次数，从而提升性能

				//保存基本游戏状态
				bufferedWriter.write(chessSize+"#"+isBlackTurn);//写入chessSize和isBlackTurn中间用#隔开
				bufferedWriter.newLine();//换行

				//写入chessIndexArr的内容
				//保存棋子位置
				for(int i=0;i<chessSize;i++){
					String str=chessIndexArr[i][0]+"#"+chessIndexArr[i][1];
					bufferedWriter.write(str);
					bufferedWriter.newLine();
				}

				bufferedWriter.close();//关闭文件
				System.out.println("已保存"+file.getPath());
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
		//打开文件
		JFileChooser jfc=new JFileChooser();
		int openDialog=jfc.showOpenDialog(null);
		if(openDialog==JFileChooser.APPROVE_OPTION){
			File file=jfc.getSelectedFile();
			try{
				FileReader fileReader=new FileReader(file);
				BufferedReader bufferedReader=new BufferedReader(fileReader);

				//重置棋盘
				resetGame();
				//读第一行
				String str=bufferedReader.readLine();
				String[] gameState=str.split("#");//把字符串分隔开，以#为分隔符分割
				//将第一行数据赋给chessSize和isBlackTurn
				chessSize=Integer.parseInt(gameState[0]);
				isBlackTurn=Boolean.parseBoolean(gameState[1]);

				//读出chessIndexArr数组中的数据
				for(int i=0;i<chessSize;i++){
					str=bufferedReader.readLine();
					String[]pos=str.split("#");
					int row=Integer.parseInt(pos[0]);
					int col=Integer.parseInt(pos[1]);
					pointers[row][col].setState((i%2==0)?1:2);
					chessIndexArr[i][0]=row;
					chessIndexArr[i][1]=col;
				}

				bufferedReader.close();//关闭文件
				System.out.println("已读出"+file.getPath());
				gameFlag="start";//恢复游戏状态
			}catch(IOException e){
				throw new RuntimeException(e);
			}
		}
	}
	/**
     * 重置游戏状态
     */
    private void resetGame() {
        	// 清空所有棋子
        	for (Pointer[] row : pointers) {
            		for (Pointer pointer : row) {
                			pointer.setState(0);          // 0表示空位置
            		}
        	}
        	isBlackTurn = true;                  // 重置为黑方先行
        	gameFlag = "start";                  // 设置游戏状态为开始
        	repaint();                           // 重绘界面
    }
	private void undoMove(){
		//棋盘内无棋子可悔
		if(chessSize==0){
			JOptionPane.showMessageDialog(mainFrame,"无棋可悔!");
			return;
		}
		//获取最后一步棋的位置
		int lastRow=chessIndexArr[chessSize-1][0];
		int lastCol=chessIndexArr[chessSize-1][1];

		//清除该位置的棋子
		pointers[lastRow][lastCol].setState(0);
		chessSize--;

		//切换回合
		isBlackTurn=!isBlackTurn;

		//如果是人机模式且当前是AI回合（白方），需要再悔一步（移除AI的上一步）
		if("ai".equals(gameMode)&&!isBlackTurn&&chessSize>0){
			lastRow=chessIndexArr[chessSize-1][0];
			lastCol=chessIndexArr[chessSize-1][1];
			pointers[lastRow][lastCol].setState(0);
			chessSize--;
			isBlackTurn=true;//切换回玩家回合
		}
		
		//重置游戏状态为进行中
		gameFlag="start";

		repaint();
	}
}

/**
 * 棋盘格子类 - 表示棋盘上的每个交叉点
 */
class Pointer {
    	public int i;                          // 行索引
    	public int j;                          // 列索引
    	private int x;                          // 屏幕x坐标
    	private int y;                          // 屏幕y坐标
    	private int h = 36;                     // 指示器大小
    	private boolean show = false;           // 是否显示指示器
    	private int state = 0;                  // 状态(0=空/1=黑/2=白)

    /**
     * 构造函数
     * @param i 行索引
     * @param j 列索引
     * @param x 屏幕x坐标
     * @param y 屏幕y坐标
     */
    public Pointer(int i, int j, int x, int y) {
        	this.i = i;
        	this.j = j;
        	this.x = x;
        	this.y = y;
    }

    /**
     * 绘制指示器(鼠标悬停时显示的红色方框)
     */
    public void draw(Graphics g) {

        	if (show) {                         // 如果需要显示
            		g.setColor(Color.RED);          // 设置红色
            		Graphics2D g2d = (Graphics2D) g; // 转换为2D绘图
            		g2d.setStroke(new BasicStroke(2.0f)); // 设置线条粗细
            
            		// 四个角坐标
            		int[] xPoints = {x-h/2, x+h/2, x+h/2, x-h/2};
            		int[] yPoints = {y-h/2, y-h/2, y+h/2, y+h/2};
            
            		// 绘制四个角的短线条
            		for (int k = 0; k < 4; k++) {
                			int next = (k + 1) % 4;     // 下一个角索引
                			g2d.drawLine(xPoints[k], yPoints[k], 
                             		xPoints[k] + (xPoints[next]-xPoints[k])/4, 
                             		yPoints[k] + (yPoints[next]-yPoints[k])/4);
            		}
        	}
    }

    /**
     * 判断点是否在格子范围内
     * @param x 点x坐标
     * @param y 点y坐标
     * @return 是否在范围内
     */
    public boolean isPoint(int x, int y) {
        	// 检查坐标是否在格子矩形范围内
        	return x > this.x - h/2 && y > this.y - h/2 && 
               		x < this.x + h/2 && y < this.y + h/2;
    }

    // ===== Getter和Setter方法 =====
    public boolean isShow() { return show; }
    public void setShow(boolean show) { this.show = show; }
    public int getState() { return state; }
    public void setState(int state) { this.state = state; }
    public int getI() { return i; }
    public int getJ() { return j; }
    public int getX() { return x; }
    public int getY() { return y; }
}