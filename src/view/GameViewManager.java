package view;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.animation.AnimationTimer;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Alien;
import model.BigMeteor;
import model.LevelLabel;
import model.SHIP;
import model.SmallInfoLabel;
import model.SmallMeteor;
import model.SpaceShooterButton;
import model.TheEndLabel;
import model.TheEndTextField;
import model.TheEndWindow;

public class GameViewManager
{
	private static final int GAME_WIDTH = 600;
	private static final int GAME_HEIGHT = 800;
	private static final String BACKGROUND_IMAGE = "view/resources/universe.png";
	
	private Stage menuStage;
	private AnchorPane gamePane;
	private Stage gameStage;
	private Scene gameScene;
	
	private GridPane gridPane1;
	private GridPane gridPane2;
	
	private ImageView ship;
	private AnimationTimer gameTimer;
	private SmallInfoLabel scoreLabel;
	
	private ImageView lifes[];
	private int numberOfLifes;
	private HBox lifeBox;
	
	private boolean isLeftKeyPressed;
	private boolean isRightKeyPressed;
	private int angle;
	
	private int numberOfSmallMeteorsToCreate; //broj se menja po levelu
	private int numberOfBigMeteorsToCreate;
	private double freeFallSpeed;
	private double backgroundSpeed = 0.5;
	
	private SmallMeteor smallBrownMeteors[];
	private SmallMeteor smallGrayMeteors[];
	private BigMeteor bigBrownMeteors[];
	private BigMeteor bigGrayMeteors[];
	
	private static final String SMALL_BROWN_METEOR = "view/resources/meteor_brown.png";
	private static final String SMALL_GRAY_METEOR = "view/resources/meteor_gray.png";
	private static final String BIG_BROWN_METEOR = "view/resources/meteor_brown_big.png";
	private static final String BIG_GRAY_METEOR = "view/resources/meteor_gray_big.png";
	
	private Random random;
	private PauseTransition delay; 
	private LevelLabel levelLabel; //izlazi pre pocetka nekog levela
	
	private int scoreGoal; //potreban skor za prelazak na sl level
	private int score; //trenutni skor
	private static final int smallMeteorKillPoints = 5;
	private static final int bigMeteorKillPoints = 20;
	private static final int alienKillPoints = 500;
	private int gameLevel; // level koji trenutno korisnik igra
	
	private TheEndWindow theEndWindow;
	private TheEndLabel msgLabel;
	List<ImageView> lasers;
	
	private ImageView doubleLaser;
	private ImageView life;
	private ImageView doubleSpeed;
	private boolean doubleLaserPresence; //prisutnost na sceni
	private boolean lifePresence;
	private boolean doubleSpeedPresence;
	private boolean isDoubleLaserPickedUp;
	private int movingSpeed;
	private boolean passedLevelAnimation;
	
	private final static double SHIP_RADIUS = 49;
	private final static double DOUBLE_LASER_RADIUS = 16;
	private final static double DOUBLE_SPEED_RADIUS = 15;
	private final static double LIFE_RADIUS = 11;
	private final static double LASER_RADIUS = 4; 
	private final static double SMALL_METEOR_RADIUS = 22;
	private final static double BIG_METEOR_RADIUS = 50;
	private final static double ALIEN_RADIUS = 100;
	
	private double playingTime;
	private AnimationTimer oneSecondTimer; //timer koji okida na jednu sekundu
	private SmallInfoLabel timeLabel;
	
	private static final String LASER_SOUND_PATH = "src/model/resources/laser_sound.mp3";
	private static final String POWERUP_SOUND_PATH = "src/model/resources/powerUpTaking.mp3";
	private static final String EXPLOSION_PATH = "src/model/resources/explosion.mp3";
	private MediaPlayer mediaPlayer;
	
	private Alien alien;
	private static final String ALIEN_LASER_PATH = "view/resources/alienlaser.png";
	private List<ImageView> alienLasers;
	private boolean moveToLeft;//pomeranje aliena
	private boolean moveToRight; 
	private boolean moveDown;
	private boolean hesoyam; //neunistiv 2 sekunde
	
	private ViewManager vm;
	
	public GameViewManager()
	{
		initializeStage();
	}
	
	private void initializeStage()
	{
		gamePane = new AnchorPane();
		gameScene = new Scene(gamePane, GAME_WIDTH, GAME_HEIGHT);
		gameStage = new Stage();
		gameStage.setResizable(false);
		gameStage.setTitle("SpaceShooter");
		gameStage.sizeToScene();
		gameStage.setScene(gameScene);
	}
	
	public void createNewGame(Stage menuStage , SHIP choosenShip , ViewManager vm)
	{
		this.vm = vm;
		gameLevel = 1; //koji se trenutno level igra
		movingSpeed = 4;
		hesoyam = false;
		this.menuStage = menuStage;
		this.menuStage.hide();
		gameStage.show();
		random = new Random();
		lasers = new ArrayList<ImageView>();
		alienLasers = new ArrayList<ImageView>();
		doubleLaser = new ImageView("view/resources/2xlaser.png");
		life = new ImageView("view/resources/life.png");
		doubleSpeed = new ImageView("view/resources/speed.png");
		doubleLaserPresence = doubleSpeedPresence = lifePresence = false;
		isDoubleLaserPickedUp = false;
		passedLevelAnimation = false;
		
		createBackground();
		createGameLoop();
		createGameElements(choosenShip);
		initializeKeyListeners();
		createPowerUps();
		createHalfSecondTimer();
		startLevel(gameLevel);
	}

	private void createBackground()
	{
		gridPane1 = new GridPane();
		gridPane2 = new GridPane();
		
		for(int i=0;i<GAME_WIDTH/256+1;i++)
		{
			for(int j=0;j<GAME_HEIGHT/256+1;j++)
			{
				ImageView background1 = new ImageView(BACKGROUND_IMAGE);
				ImageView background2 = new ImageView(BACKGROUND_IMAGE);
				
				GridPane.setConstraints(background1, i, j); //gde ce slike biti ubacene
				GridPane.setConstraints(background2, i, j);
				
				gridPane1.getChildren().add(background1);
				gridPane2.getChildren().add(background2);
			}
		}
		gridPane2.setLayoutY(-800);
		gamePane.getChildren().addAll(gridPane1,gridPane2);
	}
	
	private void createGameLoop()
	{
		gameTimer = new AnimationTimer()
		{
			public void handle(long now)
			{
				moveBackground();
				moveShip();
				moveGameElements();
				refreshScoreLabel();
				checkIfElementsColide();
				isLevelPassed();
				moveAlien();
			}
		};
		gameTimer.start();
	}
	
	private void moveBackground()
	{
		gridPane1.setLayoutY(gridPane1.getLayoutY() + backgroundSpeed);
		gridPane2.setLayoutY(gridPane2.getLayoutY() + backgroundSpeed);
		
		if(gridPane1.getLayoutY() > 800)
			gridPane1.setLayoutY(-800);
		
		if(gridPane2.getLayoutY() > 800)
			gridPane2.setLayoutY(-800);
	}
	
	private void createShip(SHIP choosenShip)
	{
		ship = new ImageView(choosenShip.getShipURL());
		ship.setLayoutX(gamePane.getWidth()/2 - ship.prefWidth(-1)/2);
		ship.setLayoutY(gamePane.getHeight() - ship.prefHeight(-1) - 10);
		gamePane.getChildren().add(ship);
	}
	
	private void createScoreInfoLabel()
	{
		scoreLabel = new SmallInfoLabel("POINTS: 0");
		scoreLabel.setLayoutX(gamePane.getWidth() - scoreLabel.prefWidth(-1) - 5);
		scoreLabel.setLayoutY(5);
		gamePane.getChildren().add(scoreLabel);
	}
	
	private void createTimeLabel()
	{
		playingTime = 0;
		timeLabel = new SmallInfoLabel("0 s");
		
		timeLabel.setLayoutY(5);
		timeLabel.setLayoutX(5);
		
		gamePane.getChildren().add(timeLabel);
		
	}
	
	private void refreshTimeLabel()
	{
		playingTime += 0.1;
		timeLabel.setText((int)playingTime +" s");
	}
	
	private void createHalfSecondTimer()
	{
		oneSecondTimer = new AnimationTimer()
		{
			long lastTime = 0;
			@Override
			public void handle(long now)
			{
				if(lastTime != 0)
				{
					if(now > lastTime + 100_000_000)
					{
						refreshTimeLabel();
						makeAlienToFire();
						
						lastTime = now;
					}
				}
				else
					lastTime = now;
			}
		};
		oneSecondTimer.start();
	}
	
	private void createLifes(SHIP choosen)
	{
		lifeBox = new HBox();
		lifes = new ImageView[3];
		numberOfLifes = 3;
		
		for (int i = 0; i < lifes.length; i++)
		{
			lifes[i] = new ImageView(choosen.getLifeURL());
			lifeBox.getChildren().add(lifes[i]);
		}
		lifeBox.setLayoutX(scoreLabel.getLayoutX() + (scoreLabel.prefWidth(-1) - lifeBox.prefWidth(-1))/2);
		lifeBox.setLayoutY(scoreLabel.prefHeight(-1) + 10);
		lifeBox.setSpacing(10);
		gamePane.getChildren().add(lifeBox);
	}
	
	private void removeLife()
	{
		lifeBox.getChildren().remove(lifes[numberOfLifes-1]);
		--numberOfLifes;
		hesoyam = true;
		
		movingSpeed = 4;
		isDoubleLaserPickedUp = false;
		
		delay = new PauseTransition(Duration.seconds(2));
		delay.setOnFinished(new EventHandler<ActionEvent>()
		{
			public void handle(ActionEvent event)
			{
				hesoyam = false;
			}
		});
		delay.play();
		
		if(numberOfLifes == 0)
		{
			gameTimer.stop();
			oneSecondTimer.stop();
			
			createTheEndWindow();
			msgLabel.setText("\t\t\tGAME OVER\n\t\tScore: " +score +"  time: " +playingTime +" s\n   Enter your name bellow:");
			theEndWindow.moveSubScene();
		}
	}
	
	private void addLife()
	{
		++numberOfLifes;
		lifeBox.getChildren().add(lifes[numberOfLifes - 1]);
	}
	
	private void createGameElements(SHIP choosenShip)
	{
		createTimeLabel();
		createShip(choosenShip);
		createScoreInfoLabel();
		createLifes(choosenShip);
	}
	
	private void initializeKeyListeners()
	{
		
		gameScene.setOnKeyPressed(new EventHandler<KeyEvent>()
		{
			public void handle(KeyEvent event)
			{
				if(event.getCode() == KeyCode.LEFT)
				{
					isLeftKeyPressed = true;
				}
				else if(event.getCode() == KeyCode.RIGHT)
				{
					isRightKeyPressed = true;
				}
				else if(event.getCode() == KeyCode.SPACE)
				{
					if(isDoubleLaserPickedUp)
						createDoubleLaser();
					else
						createLaser();
				}
					
			}
			
		});
		
		gameScene.setOnKeyReleased(new EventHandler<KeyEvent>()
		{
			public void handle(KeyEvent event)
			{
				if(event.getCode() == KeyCode.LEFT)
				{
					isLeftKeyPressed = false;
				}
				else if(event.getCode() == KeyCode.RIGHT)
				{
					isRightKeyPressed = false;
				}
			}
		});
	}
	
	private void moveShip()
	{
		if(isLeftKeyPressed && !isRightKeyPressed)
		{
			if(angle > -30)
				angle -= 5;
			ship.setRotate(angle);
			
			if(ship.getLayoutX() > -20)
				ship.setLayoutX(ship.getLayoutX() - movingSpeed);
		}
		if(!isLeftKeyPressed && isRightKeyPressed)
		{
			if(angle < 30)
				angle += 5;
			ship.setRotate(angle);
			
			if(ship.getLayoutX() < 510)
				ship.setLayoutX(ship.getLayoutX() + movingSpeed);
		}
		if(!isLeftKeyPressed && !isRightKeyPressed)
		{
			if(angle < 0) //gledamo da ispravimo brod
				angle += 5;
			else if(angle > 0)
				angle -= 5;
			ship.setRotate(angle);
		}
		if(isLeftKeyPressed && isRightKeyPressed)
		{
			if(angle < 0)
				angle += 5;
			else if(angle > 0)
				angle -= 5;
			ship.setRotate(angle);
		}
	}
	
	private void createMeteors()
	{
		smallBrownMeteors = new SmallMeteor[numberOfSmallMeteorsToCreate];
		smallGrayMeteors = new SmallMeteor[numberOfSmallMeteorsToCreate];
		bigBrownMeteors = new BigMeteor[numberOfBigMeteorsToCreate];
		bigGrayMeteors = new BigMeteor[numberOfBigMeteorsToCreate];
		
		for(int i=0;i<numberOfSmallMeteorsToCreate;i++)
		{
			smallBrownMeteors[i] = new SmallMeteor(SMALL_BROWN_METEOR);
			smallGrayMeteors[i] = new SmallMeteor(SMALL_GRAY_METEOR);
			
			setRandomPosition(smallBrownMeteors[i]);
			setRandomPosition(smallGrayMeteors[i]);
			
			gamePane.getChildren().addAll(smallBrownMeteors[i],smallGrayMeteors[i]);
		}
		for(int i=0;i<numberOfBigMeteorsToCreate;i++)
		{
			bigBrownMeteors[i] = new BigMeteor(BIG_BROWN_METEOR);
			bigGrayMeteors[i] = new BigMeteor(BIG_GRAY_METEOR);
			
			setRandomPosition(bigGrayMeteors[i]);
			setRandomPosition(bigBrownMeteors[i]);
			
			gamePane.getChildren().addAll(bigBrownMeteors[i],bigGrayMeteors[i]);
		}
	}
	
	private void setRandomPosition(ImageView object)
	{
		object.setLayoutX(random.nextInt(500));
		object.setLayoutY(-(random.nextInt(1024) + 600));
	}
	
	private void moveGameElements()
	{
		//-----------------------pomeranje meteora---------------------------
		SmallMeteor meteor1;
		for(int i=0;i<numberOfSmallMeteorsToCreate;i++)
		{
			if(smallBrownMeteors[i] != null)
			{
				meteor1 = smallBrownMeteors[i];
				meteor1.setLayoutY(meteor1.getLayoutY() + freeFallSpeed);
				meteor1.setRotate(meteor1.getRotate() + 3);
				
				if(meteor1.getLayoutY() > 1024)
				{
					setRandomPosition(meteor1);
					meteor1.setHP();
				}
			}
			
			if(smallGrayMeteors[i] != null)
			{
				meteor1 = smallGrayMeteors[i];
				meteor1.setLayoutY(meteor1.getLayoutY() + freeFallSpeed);
				meteor1.setRotate(meteor1.getRotate() + 3);
				
				if(meteor1.getLayoutY() > 1024)
				{
					setRandomPosition(meteor1);
					meteor1.setHP();
				}
			}
		}
		
		BigMeteor meteor2;
		for(int i=0;i<numberOfBigMeteorsToCreate;i++)
		{
			if(bigBrownMeteors[i] != null)
			{
				meteor2 = bigBrownMeteors[i];
				meteor2.setLayoutY(meteor2.getLayoutY() + freeFallSpeed + 1);
				
				if(meteor2.getLayoutY() > 1024)
				{
					setRandomPosition(meteor2);
					meteor2.setHP();
				}
			}
			
			if(bigGrayMeteors[i] != null)
			{
				meteor2 = bigGrayMeteors[i];
				meteor2.setLayoutY(meteor2.getLayoutY() + freeFallSpeed + 1);
				
				if(meteor2.getLayoutY() > 1024)
				{
					setRandomPosition(meteor2);
					meteor2.setHP();
				}
			}
		}
		
		//----------------------pomeranje lasera-----------------------------
		ImageView laser;
		for(int i=0;i<lasers.size();i++)
		{
			laser = lasers.get(i);
			
			laser.setLayoutY(laser.getLayoutY() - 3);
			
			if(laser.getLayoutY() < -10)
			{
				gamePane.getChildren().remove(lasers.get(i)); //brise se laser sa ekrana
				lasers.remove(i); //brise se laser iz liste lasera
			}
		}
		
		//------------------pomeranje lasera aliena------------------------------
		
		for(int i=0;i<alienLasers.size();i++)
		{
			laser = alienLasers.get(i);
			
			laser.setLayoutY(laser.getLayoutY() + 3);
			
			if(laser.getLayoutY() > gamePane.getHeight() + 10)
			{
				gamePane.getChildren().remove(alienLasers.get(i)); //brise se laser sa ekrana
				alienLasers.remove(i); //brise se laser iz liste lasera
			}
		}
		
		//-----------------pomeranje powerUP-ova-----------------------------------
		if(doubleLaserPresence)
		{
			doubleLaser.setLayoutY(doubleLaser.getLayoutY() + 3);
			
			if(doubleLaser.getLayoutY() > 800)
			{
				doubleLaserPresence = false;
				gamePane.getChildren().remove(doubleLaser);
				createPowerUps();
			}
		}
		else if(doubleSpeedPresence)
		{
			doubleSpeed.setLayoutY(doubleSpeed.getLayoutY() + 3);
			
			if(doubleSpeed.getLayoutY() > 800)
			{
				doubleSpeedPresence = false;
				gamePane.getChildren().remove(doubleSpeed);
				createPowerUps();
			}
		}
		else if(lifePresence)
		{
			life.setLayoutY(life.getLayoutY() + 3);
			
			if(life.getLayoutY() > 800)
			{
				lifePresence = false;
				gamePane.getChildren().remove(life);
				createPowerUps();
			}
		}
	}
	
	private void removeGameElements()
	{
		for (ImageView meteor : smallBrownMeteors)
			gamePane.getChildren().remove(meteor);
		for (ImageView meteor : smallGrayMeteors)
			gamePane.getChildren().remove(meteor);
		for (ImageView meteor : bigBrownMeteors)
			gamePane.getChildren().remove(meteor);
		for (ImageView meteor : bigGrayMeteors)
			gamePane.getChildren().remove(meteor);
	}
	
	private void startLevel1()
	{
		levelLabel = new LevelLabel("LEVEL 1");
		
		gamePane.getChildren().add(levelLabel);
		levelLabel.showLabel();
		
		delay = new PauseTransition(Duration.seconds(3));
		delay.setOnFinished(new EventHandler<ActionEvent>()
		{
			public void handle(ActionEvent arg0)
			{
				levelLabel.hideLabel();
				delay.stop();
			}
			
		});
		delay.play();
		
		score = 0;
		numberOfSmallMeteorsToCreate = 1; //10
		numberOfBigMeteorsToCreate = 0;
		scoreGoal = numberOfSmallMeteorsToCreate * 2 * smallMeteorKillPoints + numberOfBigMeteorsToCreate * 2 * bigMeteorKillPoints;
		freeFallSpeed = 3.5;
		scoreLabel.setText("SCORE: " +score +"/" +scoreGoal);
		
		createMeteors();
		
	}
	
	private void startLevel2()
	{
		levelLabel.setText("LEVEL 2");
		
		levelLabel.showLabel();
		
		delay = new PauseTransition(Duration.seconds(3));
		delay.setOnFinished(new EventHandler<ActionEvent>()
		{
			public void handle(ActionEvent arg0)
			{
				levelLabel.hideLabel();
				delay.stop();
			}
			
		});
		delay.play();
		
		numberOfSmallMeteorsToCreate = 1; //12
		numberOfBigMeteorsToCreate = 0; //1
		scoreGoal += (numberOfSmallMeteorsToCreate * 2 * smallMeteorKillPoints + numberOfBigMeteorsToCreate * 2 * bigMeteorKillPoints);
		freeFallSpeed = 4;
		
		createMeteors();
		
	}
	
	private void startLevel3()
	{
		levelLabel.setText("LEVEL 3");
		
		levelLabel.showLabel();
		
		delay = new PauseTransition(Duration.seconds(3));
		delay.setOnFinished(new EventHandler<ActionEvent>()
		{
			public void handle(ActionEvent arg0)
			{
				levelLabel.hideLabel();
				delay.stop();
			}
			
		});
		delay.play();
		
		numberOfSmallMeteorsToCreate = 1; //15
		numberOfBigMeteorsToCreate = 0; //2 i dodati poene za aliena
		scoreGoal += (numberOfSmallMeteorsToCreate * 2 * smallMeteorKillPoints + numberOfBigMeteorsToCreate * 2 * bigMeteorKillPoints + 0 * alienKillPoints);
		freeFallSpeed = 5;
		//createAlien();
		
		createMeteors();
	}
	
	public void startLevel(int level)
	{
		switch (level)
		{
		case 1:startLevel1();break;
		case 2:startLevel2();break;
		case 3:startLevel3();break;
		default:break;
		}
	}
	private void refreshScoreLabel()
	{
		scoreLabel.setText("SCORE: " +score +"/" +scoreGoal);
	}
	
	private void passedLevelAnimation()
	{
		removeGameElements();
		backgroundSpeed = 4;
		passedLevelAnimation = true;
		
		delay = new PauseTransition(Duration.seconds(3));
		delay.setOnFinished(new EventHandler<ActionEvent>()
		{
			public void handle(ActionEvent arg0)
			{
				backgroundSpeed = 0.5;
				delay.stop();
				passedLevelAnimation = false;
				++gameLevel;
				startLevel(gameLevel);
			}
			
		});
		delay.play();
	}
	
	private void isLevelPassed()
	{
		if(score >= scoreGoal && !passedLevelAnimation) //i ukoliko se trenutno ne odigrava tranzicija prelaza levela
		{
			isItTheEnd();
			removeGameElements();
			passedLevelAnimation();
		}
	}
	
	private void isItTheEnd() //provera za kraj2
	{
		if(gameLevel == 3)
		{
			createTheEndWindow();
			theEndWindow.moveSubScene();
			gameTimer.stop();
			oneSecondTimer.stop();
		}
	}
	
	private void createTheEndWindow()
	{
		theEndWindow = new TheEndWindow();
		gamePane.getChildren().add(theEndWindow);
		
		
		msgLabel = new TheEndLabel("Congrats.You have passed\n the game!\n\nEnter your name bellow:");
		msgLabel.setLayoutY(10);
		msgLabel.setLayoutX(theEndWindow.prefWidth(-1)/2 - msgLabel.prefWidth(-1)/2);
		
		TheEndTextField tb = new TheEndTextField();
		tb.setLayoutY(120);
		tb.setLayoutX(theEndWindow.prefWidth(-1)/2 - tb.prefWidth(-1)/2);
		
		SpaceShooterButton mainMenu = new SpaceShooterButton("Menu");
		
		mainMenu.setOnAction(new EventHandler<ActionEvent>()
		{
			public void handle(ActionEvent event)
			{
				if(tb.getText().length() > 0 && !(tb.getText().equals("Enter your name!")))
				{
					vm.writeToDB(tb.getText(), score, (int)playingTime);
					gameStage.close();
					menuStage.show();
				}
				else
				{
					tb.setText("Enter your name!");
				}
			}
		});
		
		tb.setOnMouseClicked(new EventHandler<MouseEvent>()
		{

			public void handle(MouseEvent event)
			{
				if(tb.getText().equals("Enter your name!"))
					tb.setText("");
			}
		});
		
		mainMenu.setLayoutX(theEndWindow.prefWidth(-1)/2 - mainMenu.prefWidth(-1)/2);
		mainMenu.setLayoutY(theEndWindow.prefHeight(-1) - mainMenu.prefHeight(-1) - 10);
		
		theEndWindow.getPane().getChildren().addAll(msgLabel,tb,mainMenu);
	}
	
	private void createLaser()
	{
		createLaserSound();
		ImageView laser = new ImageView("view/resources/laser.png");
		laser.prefHeight(30);
		laser.prefWidth(5);
		
		lasers.add(laser);
		
		laser.setLayoutX(ship.getLayoutX() + ship.prefWidth(-1)/2 - laser.prefWidth(-1)/2);
		laser.setLayoutY(ship.getLayoutY() - laser.prefHeight(-1) - 2);
		
		gamePane.getChildren().add(laser);
	}
	
	private void createDoubleLaser()
	{
		createLaserSound();
		ImageView laser1 = new ImageView("view/resources/laser.png");
		ImageView laser2 = new ImageView("view/resources/laser.png");
		
		laser1.prefHeight(30);
		laser1.prefWidth(5);
		
		laser2.prefHeight(30);
		laser2.prefWidth(5);
		
		lasers.add(laser1);
		lasers.add(laser2);
		
		laser1.setLayoutX(ship.getLayoutX() + ship.prefWidth(-1)/2 - laser1.prefWidth(-1) - 2);
		laser1.setLayoutY(ship.getLayoutY() - laser1.prefHeight(-1) - 2);
		
		laser2.setLayoutX(ship.getLayoutX() + ship.prefWidth(-1)/2 + laser2.prefWidth(-1) + 2);
		laser2.setLayoutY(ship.getLayoutY() - laser2.prefHeight(-1) - 2);
		
		gamePane.getChildren().add(laser1);
		gamePane.getChildren().add(laser2);
	}
	
	private void createPowerUps() //od 3 poverUP-a , bira jedan
	{
		int choice;
		choice = random.nextInt(3) + 1;
		
		switch (choice)
		{
		case 1:
		{
			setRandomPosition(doubleLaser);
			doubleLaser.setLayoutY(doubleLaser.getLayoutY() - 1000);
			gamePane.getChildren().add(doubleLaser);
			doubleLaserPresence = true;
			doubleSpeedPresence = false; //u jednom padu samo je jedan prisutan
			lifePresence = false;
		}break;
		case 2:
		{
			setRandomPosition(doubleSpeed);
			doubleSpeed.setLayoutY(doubleSpeed.getLayoutY() - 1000);
			gamePane.getChildren().add(doubleSpeed);
			doubleLaserPresence = false;
			doubleSpeedPresence = true;
			lifePresence = false;
		}break;
		case 3:
		{
			setRandomPosition(life);
			life.setLayoutY(life.getLayoutY() - 1000);
			gamePane.getChildren().add(life);
			doubleLaserPresence = false;
			doubleSpeedPresence = false;
			lifePresence = true;
		}break;
		default:break;
		
		}
	}
	
	private double getDistance(double x1 , double y1 , double x2 , double y2)
	{
		return Math.sqrt(Math.pow(x1-x2, 2) + Math.pow(y1-y2, 2));
	}
	
	private void checkIfElementsColide()
	{
		//----------------provera lasera i meteora , aliena------------------------------------
		for(int i=0;i<lasers.size();i++)
		{
			ImageView laser = lasers.get(i);
			double x = laser.getLayoutX();
			double y = laser.getLayoutY();
			
			for(int j=0;j<smallBrownMeteors.length;j++)
			{
				if(smallBrownMeteors[j] != null && !passedLevelAnimation) //samo ako postoji
				{
					double xMeteor = smallBrownMeteors[j].getLayoutX();
					double yMeteor = smallBrownMeteors[j].getLayoutY();
					
					if(LASER_RADIUS + SMALL_METEOR_RADIUS > getDistance(x + 3.5, y + 3.5, xMeteor + 22, yMeteor + 22))
					{
						gamePane.getChildren().remove(lasers.get(i));
						lasers.remove(i);
						
						smallBrownMeteors[j].substractHP(); //oduzmi hp za pogodak
						
						if(smallBrownMeteors[j].getHP() <= 0) //obrisi ga
						{
							score += smallMeteorKillPoints; //uvecaj poene
							gamePane.getChildren().remove(smallBrownMeteors[j]);
							smallBrownMeteors[j] = null;
						}
					}
				}
			}
			
			for(int j=0;j<smallGrayMeteors.length;j++)
			{
				if(smallGrayMeteors[j] != null && !passedLevelAnimation) //samo ako postoji
				{
					double xMeteor = smallGrayMeteors[j].getLayoutX();
					double yMeteor = smallGrayMeteors[j].getLayoutY();
					
					if(LASER_RADIUS + SMALL_METEOR_RADIUS > getDistance(x + 3.5, y + 3.5, xMeteor + 22, yMeteor + 22))
					{
						gamePane.getChildren().remove(lasers.get(i));
						lasers.remove(i);
						
						smallGrayMeteors[j].substractHP(); //oduzmi hp za pogodak
						
						if(smallGrayMeteors[j].getHP() <= 0) //obrisi ga
						{
							score += smallMeteorKillPoints; //uvecaj poene
							gamePane.getChildren().remove(smallGrayMeteors[j]);
							smallGrayMeteors[j] = null;
						}
					}
				}
			}
			
			for(int j=0;j<bigBrownMeteors.length;j++)
			{
				if(bigBrownMeteors[j] != null && !passedLevelAnimation) //samo ako postoji
				{
					double xMeteor = bigBrownMeteors[j].getLayoutX();
					double yMeteor = bigBrownMeteors[j].getLayoutY();
					
					if(LASER_RADIUS + BIG_METEOR_RADIUS > getDistance(x + 3.5 , y + 3.5, xMeteor + 49, yMeteor + 49))
					{
						gamePane.getChildren().remove(lasers.get(i));
						lasers.remove(i);
						
						bigBrownMeteors[j].substractHP(); //oduzmi hp za pogodak
						
						if(bigBrownMeteors[j].getHP() <= 0) //obrisi ga
						{
							score += bigMeteorKillPoints; //uvecaj poene
							gamePane.getChildren().remove(bigBrownMeteors[j]);
							bigBrownMeteors[j] = null;
						}
					}
				}
			}
			
			for(int j=0;j<bigGrayMeteors.length;j++)
			{
				if(bigGrayMeteors[j] != null && !passedLevelAnimation) //samo ako postoji
				{
					double xMeteor = bigGrayMeteors[j].getLayoutX();
					double yMeteor = bigGrayMeteors[j].getLayoutY();
					
					if(LASER_RADIUS + BIG_METEOR_RADIUS > getDistance(x + 3.5, y + 3.5, xMeteor + 49, yMeteor + 49))
					{
						gamePane.getChildren().remove(lasers.get(i));
						lasers.remove(i);
						
						bigGrayMeteors[j].substractHP(); //oduzmi hp za pogodak
						
						if(bigGrayMeteors[j].getHP() <= 0) //obrisi ga
						{
							score += bigMeteorKillPoints; //uvecaj poene
							gamePane.getChildren().remove(bigGrayMeteors[j]);
							bigGrayMeteors[j] = null;
						}
					}
				}
			}
			
			//---------------------za aliena---------------------------
			
			if(alien != null && ((LASER_RADIUS + ALIEN_RADIUS) > getDistance(x + 3.5 , y + 3.5 , alien.getLayoutX() + 100 , alien.getLayoutY() + 76)))
			{
				gamePane.getChildren().remove(lasers.get(i));
				lasers.remove(i);
				
				alien.substractHP();
				if(alien.getHP() <= 0)
				{
					score += alienKillPoints;
					gamePane.getChildren().remove(alien);
					alien = null;
					createExplosionSound();
				}
			}
		}
		//-----------------------alien's lasers i brod---------------------------
		for(int i=0;i<alienLasers.size();i++)
		{
			double x = alienLasers.get(i).getLayoutX();
			double y = alienLasers.get(i).getLayoutY();
			if((LASER_RADIUS + SHIP_RADIUS > getDistance(x + 3.5 , y + 3.5 , ship.getLayoutX() + 49, ship.getLayoutY() + 37.5)) && !hesoyam)
			{
				gamePane.getChildren().remove(alienLasers.get(i));
				alienLasers.remove(i);
				removeLife();
				createExplosionSound();
			}
		}
		
		//---------------------brod i doublelaser-------------------------------
		if(doubleLaserPresence) //ako je laser na sceni
		{
			if(DOUBLE_LASER_RADIUS + SHIP_RADIUS > getDistance(ship.getLayoutX() + 49, ship.getLayoutY() + 37.5, doubleLaser.getLayoutX() + 16, doubleLaser.getLayoutY() + 16))
			{
				isDoubleLaserPickedUp = true;
				doubleLaserPresence = false;
				gamePane.getChildren().remove(doubleLaser);
				createPowerUpSound();
				createPowerUps();
			}
		}
		//------------------brod i 2x speed-------------------------------------
		if(doubleSpeedPresence) //ako je 2xspeed na sceni
		{
			if(DOUBLE_SPEED_RADIUS + SHIP_RADIUS > getDistance(ship.getLayoutX() + 49, ship.getLayoutY() + 37.5, doubleSpeed.getLayoutX() + 10, doubleSpeed.getLayoutY() + 15))
			{
				movingSpeed = 6;
				doubleSpeedPresence = false;
				gamePane.getChildren().remove(doubleSpeed);
				createPowerUpSound();
				createPowerUps();
			}
		}
		//---------------brod i life----------------------------------
		if(lifePresence) 
		{
			if(LIFE_RADIUS + SHIP_RADIUS > getDistance(ship.getLayoutX() + 49, ship.getLayoutY() + 37.5, life.getLayoutX() + 11 , life.getLayoutY() + 10.5))
			{
				if(numberOfLifes < 3)
					addLife();
				gamePane.getChildren().remove(life);
				createPowerUpSound();
				createPowerUps();
			}
		}
		//-------------------------brod i meteori----------------------------------
		
		for(int j=0;j<smallBrownMeteors.length;j++)
		{
			if(smallBrownMeteors[j] != null && !passedLevelAnimation) //samo ako postoji
			{
				double xMeteor = smallBrownMeteors[j].getLayoutX();
				double yMeteor = smallBrownMeteors[j].getLayoutY();
				
				if((SHIP_RADIUS + SMALL_METEOR_RADIUS > getDistance(ship.getLayoutX() + 49, ship.getLayoutY() + 37.5, xMeteor + 22, yMeteor + 22)) && !hesoyam)
				{
					removeLife();
					createExplosionSound();
					setRandomPosition(smallBrownMeteors[j]);
					smallBrownMeteors[j].setHP();
				}
			}
		}
		
		for(int j=0;j<smallGrayMeteors.length;j++)
		{
			if(smallGrayMeteors[j] != null && !passedLevelAnimation) //samo ako postoji
			{
				double xMeteor = smallGrayMeteors[j].getLayoutX();
				double yMeteor = smallGrayMeteors[j].getLayoutY();
				
				if((SHIP_RADIUS + SMALL_METEOR_RADIUS > getDistance(ship.getLayoutX() + 49, ship.getLayoutY() + 37.5, xMeteor + 22, yMeteor + 22)) && !hesoyam)
				{
					removeLife();
					createExplosionSound();
					setRandomPosition(smallGrayMeteors[j]);
					smallGrayMeteors[j].setHP();
				}
			}
		}
		
		for(int j=0;j<bigBrownMeteors.length;j++)
		{
			if(bigBrownMeteors[j] != null && !passedLevelAnimation) //samo ako postoji
			{
				double xMeteor = bigBrownMeteors[j].getLayoutX();
				double yMeteor = bigBrownMeteors[j].getLayoutY();
				
				if((SHIP_RADIUS + SMALL_METEOR_RADIUS > getDistance(ship.getLayoutX() + 49, ship.getLayoutY() + 37.5, xMeteor + 22, yMeteor + 22)) && !hesoyam)
				{
					removeLife();
					createExplosionSound();
					setRandomPosition(bigBrownMeteors[j]);
					bigBrownMeteors[j].setHP();
				}
			}
		}
		
		for(int j=0;j<bigGrayMeteors.length;j++)
		{
			if(bigGrayMeteors[j] != null && !passedLevelAnimation) //samo ako postoji
			{
				double xMeteor = bigGrayMeteors[j].getLayoutX();
				double yMeteor = bigGrayMeteors[j].getLayoutY();
				
				if((SHIP_RADIUS + SMALL_METEOR_RADIUS > getDistance(ship.getLayoutX() + 49, ship.getLayoutY() + 37.5, xMeteor + 22, yMeteor + 22)) && !hesoyam)
				{
					removeLife();
					createExplosionSound();
					setRandomPosition(bigGrayMeteors[j]);
					bigGrayMeteors[j].setHP();
				}
			}
		}	
	}
	
	private void createLaserSound()
	{
		Media laserSound = new Media(new File(LASER_SOUND_PATH).toURI().toString());
		mediaPlayer = new MediaPlayer(laserSound);
		mediaPlayer.play();
	}
	
	private void createPowerUpSound()
	{
		Media powerUpSound = new Media(new File(POWERUP_SOUND_PATH).toURI().toString());
		mediaPlayer = new MediaPlayer(powerUpSound);
		mediaPlayer.play();
	}
	
	private void createExplosionSound()
	{
		Media explosionSound = new Media(new File(EXPLOSION_PATH).toURI().toString());
		mediaPlayer = new MediaPlayer(explosionSound);
		mediaPlayer.play();
	}
	
	private void createAlien()
	{
		moveToRight = false;
		moveToLeft = false;
		moveDown = true;
		alien = new Alien();
		alien.setLayoutY(-155);
		alien.setLayoutX(gamePane.getWidth()/2 - alien.prefWidth(-1)/2);
		gamePane.getChildren().add(alien);
	}
	
	private void createAlienLasers()
	{
		ImageView laser1 = new ImageView(ALIEN_LASER_PATH);
		ImageView laser2 = new ImageView(ALIEN_LASER_PATH);
		
		laser1.setLayoutY(alien.getLayoutY() + alien.prefHeight(-1) + 2);
		laser1.setLayoutX(alien.getLayoutX() + 15);
		
		laser2.setLayoutY(alien.getLayoutY() + alien.prefHeight(-1) + 2);
		laser2.setLayoutX(alien.getLayoutX() + alien.prefWidth(-1) - 20);
		
		alienLasers.add(laser1);
		alienLasers.add(laser2);
		
		gamePane.getChildren().addAll(laser1,laser2);
	}
	
	private void makeAlienToFire()
	{
		if(alien != null)
		{
			double left = alien.getLayoutX();
			double right = alien.getLayoutX() + alien.prefWidth(-1);
			
			if((left >= 25 && right <= 275) || (left >= 300 && right <= 575))
				createAlienLasers();
		}
	}
	
	private void moveAlien()
	{
		if(alien != null && moveDown)
		{
			alien.setLayoutY(alien.getLayoutY() + 3);
			
			if(alien.getLayoutY() == 100)
			{
				moveToLeft = true;
				moveDown = false;
			}
		}
		else if(moveToLeft && alien != null) //ako treba da ide u levo
		{
			alien.setLayoutX(alien.getLayoutX() - 1.5);
			
			if(alien.getLayoutX() <= 0)
			{
				moveToLeft = false;
				moveToRight = true;
			}
		}
		else if(alien != null)
		{
			alien.setLayoutX(alien.getLayoutX() + 1.5);
			
			if(alien.getLayoutX() + alien.prefWidth(-1) >= 600)
			{
				moveToLeft = true;
				moveToRight = false;
			}
		}
	}
}
