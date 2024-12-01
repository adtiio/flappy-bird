import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;
public class FlappyBird extends JPanel implements ActionListener, KeyListener{
    int boardWidth=360;
    int boardHeight=640;

    Image backGroundImg;
    Image birdImg;
    Image topPipeImg;
    Image bottomPipeImg;

    //bird
    int birdX = boardWidth/8;
    int birdY=boardHeight/2;
    int birdWidth = 34;
    int birdHeight = 32;

    class Bird{
        int x=birdX;
        int y=birdY;
        int width=birdWidth;
        int height=birdHeight;
        Image img;
        
        Bird(Image img){
            this.img=img;
        }
    }
    int pipeX=boardHeight;
    int pipeY=0;
    int pipeWidth = 64;
    int pipeHeight = 512;

    class Pipe{
        int x=pipeX;
        int y=pipeY;
        int width = pipeWidth;
        int height=pipeHeight;
        Image img;
        boolean passed=false;

        Pipe(Image img){
            this.img=img;
        }
    }


    //game logic
    Bird bird;
    int velocityX=-4;
    int velocityY=0;
    int gravity=1;

    ArrayList<Pipe> pipes = new ArrayList<>();


    Timer gameLoop;
    Timer placePipesTimer;
    boolean gameOver=false;
    double score = 0;

    FlappyBird(){
        setPreferredSize(new Dimension(boardWidth,boardHeight));
        setFocusable(true);
        addKeyListener(this);

        backGroundImg = new ImageIcon(getClass().getResource("./flappybirdbg.png")).getImage();
        birdImg = new ImageIcon(getClass().getResource("./flappybird.png")).getImage();
        topPipeImg = new ImageIcon(getClass().getResource("./toppipe.png")).getImage();
        bottomPipeImg = new ImageIcon(getClass().getResource("./bottompipe.png")).getImage();

        bird=new Bird(birdImg);
        pipes = new ArrayList<>();

        placePipesTimer = new Timer(1500,new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                placePipes();
            }
        });
        placePipesTimer.start();

        gameLoop=new Timer(1000/60, this);
        gameLoop.start();
    }

    public void placePipes(){
        int randomPipeY =(int) (pipeY - pipeHeight/4 - Math.random()*(pipeHeight/2));
        int openingSpace = boardHeight/4;

        Pipe topPipe = new Pipe(topPipeImg);
        topPipe.y=randomPipeY;
        pipes.add(topPipe);   

        Pipe bottomPipe= new Pipe(bottomPipeImg);
        bottomPipe.y = topPipe.y+pipeHeight+openingSpace; 
        pipes.add(bottomPipe);             
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }
    public void draw(Graphics g){
        g.drawImage(backGroundImg, 0, 0,boardWidth,boardHeight,null);

        g.drawImage(bird.img, bird.x, bird.y, bird.width, bird.height, null);

        for(int i=0;i<pipes.size();i++){
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }
         if(pipes.size()>10) pipes.remove(0);

        g.setColor(Color.white);
        g.setFont(new Font("Areal",Font.PLAIN, 32));
        if(gameOver){
            g.drawString("Game Over: "+String.valueOf((int)score),10,35);
        }else{
            g.drawString(String.valueOf((int)score), 10, 30);
        }
    }
    public void move(){
        velocityY+=gravity;
        bird.y +=velocityY;
        bird.y=Math.max(bird.y,0);

        for(int i=0;i<pipes.size();i++){
            Pipe pipe = pipes.get(i);
            pipe.x +=velocityX;

            if(!pipe.passed && bird.x > pipe.x + pipe.width){
                pipe.passed=true;
                score+=0.5;
            }

            if(collision(bird, pipe)) gameOver=true;      
        }

        if(bird.y > boardHeight) gameOver = true; 
    }
    public boolean collision(Bird a, Pipe b){
        return a.x < b.x + b.width && a.x + a.width > b.x && a.y < b.y + b.height && a.y+ a.height > b.y;
    }



    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if(gameOver){
            placePipesTimer.stop();
            gameLoop.stop();
        }
    }


    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode()== KeyEvent.VK_SPACE){
            velocityY = -9;
            if(gameOver){
                bird.y=birdY;
                velocityY=0;
                pipes.clear();;
                score=0;
                gameOver=false;
                gameLoop.start();
                placePipesTimer.start();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e){}
    @Override
    public void keyTyped(KeyEvent e){}
}
