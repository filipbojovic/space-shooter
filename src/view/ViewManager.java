package view;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.InfoLabel;
import model.SHIP;
import model.ShipPicker;
import model.SmallInfoLabel;
import model.SpaceShooterButton;
import model.SpaceShooterSubScene;

public class ViewManager
{
	private AnchorPane mainPane;
	private Scene mainScene;
	private Stage mainStage;
	
	private static final int WIDTH = 1024;
	private static final int HEIGHT = 500;
	
	private GridPane gridPane1;
	private GridPane gridPane2;
	
	private static final String BACKGROUND_IMG = "view/resources/universe.png";
	
	private AnimationTimer gameTimer;
	
	private List<SpaceShooterButton> menuButtons;
	
	private static final int MENU_BUTTON_X = 100;
	private static final int MENU_BUTTON_Y = 100;
	
	private static final String LOGO_IMAGE = "view/resources/logo.png";
	private static final String INSCRIPTION_IMAGE = "view/resources/text.png";
	private ImageView logo;
	private ImageView inscription;
	
	private int logoMover;
	
	private SpaceShooterSubScene playSubScene;
	private SpaceShooterSubScene scoresSubScene;
	private SpaceShooterSubScene sceneToHide;
	
	private List<ShipPicker> pickedShips;
	private InfoLabel label;
	private HBox box;
	private SHIP choosenShip;
	private GameViewManager gameManager;
	private boolean isGamePlayed;
	
	SmallInfoLabel scoresLabel; //labela koja se prikazuje u scores pod-sceni
	private VBox scoresContainer; //kontejner za rezultate igre
	
	private Connection con = null;
	private ResultSet rs = null;
	
	public ViewManager()
	{
		mainPane = new AnchorPane();
		mainScene = new Scene(mainPane,WIDTH,HEIGHT);
		mainStage = new Stage();
		//postoji bag koji dodaje dodatni padding kada se fixira prozor
		mainStage.setResizable(false);
		mainStage.sizeToScene();
		mainStage.setTitle("SpaceShooter");
		mainStage.setScene(mainScene);
		menuButtons = new ArrayList<SpaceShooterButton>();
		sceneToHide = null;
		isGamePlayed = false;
		
		createBackground();
		createButtons();
		createLogo();
		createInscription();
		createGameLoop();
		createSubScenes();
		createConnection();
	}
	
	private void createButtons()
	{
		createPlayButton();
		createScoresButton();
		createExitButton();
	}

	public Stage getStage()
	{
		return mainStage;
	}
	
	private void createBackground()
	{
		gridPane1 = new GridPane();
		gridPane2 = new GridPane();
		
		for(int i=0;i<WIDTH/256;i++)
		{
			for(int j=0;j<HEIGHT/256 + 1;j++)
			{
				ImageView backgroundImage1 = new ImageView(BACKGROUND_IMG);
				ImageView backgroundImage2 = new ImageView(BACKGROUND_IMG);
				
				//ovom opcijom se podesava gde ce data slika biti ubacena u grid panelu
				GridPane.setConstraints(backgroundImage1, i, j);
				GridPane.setConstraints(backgroundImage2, i, j);
				
				gridPane1.getChildren().add(backgroundImage1);
				gridPane2.getChildren().add(backgroundImage2);
			}
		}
		//ideja je da se dodaju 2 pane-a , i kako se jedan bude pomerao na dole,stvarace se prazan prostor,i taj prostor
		//ce da popuni pane iznad njega
		gridPane2.setLayoutY(-500);
		mainPane.getChildren().addAll(gridPane1,gridPane2);
	}
	
	private void moveBackground()
	{
		gridPane1.setLayoutY(gridPane1.getLayoutY() + 0.5);
		gridPane2.setLayoutY(gridPane2.getLayoutY() + 0.5);
		
		if(gridPane1.getLayoutY() > 500)
			gridPane1.setLayoutY(-500);
		
		if(gridPane2.getLayoutY() > 500)
			gridPane2.setLayoutY(-500);
	}
	
	private void createGameLoop()
	{
		gameTimer = new AnimationTimer()
		{
			public void handle(long arg0)
			{
				//moveBackground();
				//moveLogo();
			}
		};
		gameTimer.start();
	}
	//kreiranje dugmica
	
	private void addMenuButton(SpaceShooterButton button)
	{
		button.setLayoutX(MENU_BUTTON_X);
		button.setLayoutY(MENU_BUTTON_Y + menuButtons.size() * 100);
		menuButtons.add(button);
		mainPane.getChildren().add(button);
	}
	
	private void createSubScenes()
	{
		createPlaySubScene();
		createScoresSubScene();
	}
	
	private void createScoresSubScene()
	{
		scoresSubScene = new SpaceShooterSubScene();
		mainPane.getChildren().add(scoresSubScene);
		
		scoresLabel = new SmallInfoLabel("HIGH SCORES");
		try
		{
			scoresLabel.setFont(Font.loadFont(new FileInputStream("src/model/resources/kenvector_future.ttf"), 35));
			scoresLabel.setPrefWidth(400);
			scoresLabel.setAlignment(Pos.CENTER);
		} catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		scoresLabel.setLayoutX(scoresSubScene.prefWidth(-1)/2 - scoresLabel.prefWidth(-1)/2);
		scoresSubScene.getPane().getChildren().add(scoresLabel);
		
		//-------kreiranje kontejnera za rezultate
		
		scoresContainer = new VBox();
		scoresContainer.setAlignment(Pos.CENTER_LEFT);
		scoresContainer.setPrefWidth(580);
		scoresContainer.setLayoutX(scoresSubScene.prefWidth(-1)/2 - scoresContainer.prefWidth(-1)/2);
		scoresContainer.setLayoutY(scoresLabel.getLayoutY() + scoresLabel.prefHeight(-1) + 5);
		
		scoresSubScene.getPane().getChildren().add(scoresContainer);
		SmallInfoLabel l = new SmallInfoLabel("NAME: \t\t\t\t\t SCORE: \t\t\t\t\tTIME:");
		l.setPrefWidth(600);
		scoresContainer.getChildren().add(l);
	}

	private void showSubScenes(SpaceShooterSubScene sceneToShow)
	{
		if(sceneToHide != null)
		{
			sceneToHide.moveSubScene();
		}
		
		sceneToShow.moveSubScene();
		sceneToHide = sceneToShow;
	}
	
	private void createPlayButton()
	{
		SpaceShooterButton play = new SpaceShooterButton("PLAY");
		addMenuButton(play);
		
		play.setOnAction(new EventHandler<ActionEvent>()
		{
			public void handle(ActionEvent event)
			{
				showSubScenes(playSubScene);
			}
		});
		
	}
	
	private void createScoresButton()
	{
		SpaceShooterButton scores = new SpaceShooterButton("SCORES");
		addMenuButton(scores);
		
		scores.setOnAction(new EventHandler<ActionEvent>()
		{
			public void handle(ActionEvent event)
			{
				showSubScenes(scoresSubScene);
				readFromDBAndInsertInScoreTable();
			}
		});
		
	}
	
	//------------------------connection to database-----------------------------
	private void createConnection()
	{
		String user = "root";
		String password = "root";
		String dataBase = "db";
		//String serverName = "DESKTOP-83D1L4F\\SQLEXPRESS";
		try
		{
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			con = DriverManager.getConnection("jdbc:sqlserver://DESKTOP-83D1L4F\\SQLEXPRESS:50539;user="+user+";password="+ password + ";databasename="+dataBase+""); 
			
		} catch (ClassNotFoundException | SQLException e)
		{
			System.out.println("Bad connection with server.");
		}
	}
	
	public void writeToDB(String name , int score , int time)
	{
		String sql = "INSERT INTO Scores Values('"+name +"'," +score +"," +time +")";
		try
		{
			Statement state = con.createStatement();
			state.executeUpdate(sql);
		} 
		catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void clearHighScoreTable()
	{
		String sql = "delete from Scores where 1=1";
		try
		{
			Statement state = con.createStatement();
			state.executeUpdate(sql);
		} 
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	private void readFromDBAndInsertInScoreTable()
	{
		int count = 6; //samo prvih 6 ucitaj iz baze
		String sql = "select s.nickName , s.score , s.playTime , \r\n" + 
				"							(select count(*) + 1\r\n" + 
				"							from Scores s2\r\n" + 
				"							where (s.score < s2.score or (s.score = s2.score and s.playTime > s2.playTime)) and s.id != s2.id) as Rang\r\n" + 
				"from Scores s\r\n" + 
				"order by Rang";
		try
		{
			//PreparedStatement pst = con.prepareStatement("select * from Scores");
			PreparedStatement pst = con.prepareStatement(sql);
			ResultSet rs = pst.executeQuery();
			int id = 1;
			scoresContainer.getChildren().clear(); //brise sve iz ovog kontejnera
			SmallInfoLabel l = new SmallInfoLabel("NAME: \t\t\t\t\t SCORE: \t\t\t\t\tTIME:");
			l.setPrefWidth(600);
			scoresContainer.getChildren().add(l);
			
			while(rs.next() && count-- > 0)
			{
				String name = id++ + ". " +rs.getString("nickName");
				int score = rs.getInt("score");
				int playTime = rs.getInt("playTime");
				
				l = new SmallInfoLabel(name + "\t\t\t\t " +score + "\t\t\t\t\t " +playTime);
				l.setPrefWidth(600);
				scoresContainer.getChildren().add(l);
			}
		} 
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	
	
	private void createExitButton()
	{
		SpaceShooterButton exit = new SpaceShooterButton("EXIT");
		addMenuButton(exit);
		
		exit.setOnAction(new EventHandler<ActionEvent>()
		{
			public void handle(ActionEvent event)
			{
				mainStage.close();
			}
		});
	}
	
	private void createInscription()
	{
		inscription = new ImageView(INSCRIPTION_IMAGE);
		inscription.setPreserveRatio(true);
		inscription.setFitWidth(600);
		inscription.setLayoutY(50);
		inscription.setLayoutX(350);
		
		initializeInscriptionListeners();
		
		mainPane.getChildren().add(inscription);
	}
	
	private void initializeInscriptionListeners()
	{
		inscription.setOnMouseEntered(new EventHandler<MouseEvent>()
		{
			public void handle(MouseEvent event)
			{
				inscription.setEffect(new DropShadow(10, Color.WHITE));
			}
		});
		
		inscription.setOnMouseExited(new EventHandler<MouseEvent>()
		{
			public void handle(MouseEvent event)
			{
				inscription.setEffect(null);
			}
		});
	}

	private void createLogo()
	{
		logo = new ImageView(LOGO_IMAGE);
		logo.setLayoutX(350);
		logo.setLayoutY(200);
		logo.setPreserveRatio(true);
		logo.setFitWidth(600);
		
		initializeLogoListeners();
		
		mainPane.getChildren().add(logo);
	}

	private void initializeLogoListeners()
	{
		logo.setOnMouseEntered(new EventHandler<MouseEvent>()
		{
			public void handle(MouseEvent event)
			{
				logo.setEffect(new DropShadow(10, Color.WHITE));
			}
		});
		
		logo.setOnMouseExited(new EventHandler<MouseEvent>()
		{
			public void handle(MouseEvent event)
			{
				logo.setEffect(null);
			}
		});
	}
	
	private void createInfoLabel()
	{
		label = new InfoLabel("CHOOSE YOUR SHIP");
		label.setLayoutX(playSubScene.getWidth()/2 - label.prefWidth(-1)/2); // -1 vraca sirinu labele
		label.setLayoutY(25);
		
		playSubScene.getPane().getChildren().add(label);
	}
	
	private HBox createShipsToChoose()
	{
		box = new HBox();
		pickedShips = new ArrayList<ShipPicker>();
		box.setSpacing(20);
		for (SHIP ship : SHIP.values())
		{
			ShipPicker shipToPick = new ShipPicker(ship); //ovo je jedan VBox u koji je sada smesten krug i brod
			pickedShips.add(shipToPick); //dodajemo u listu brodova VBox
			
			shipToPick.setOnMousePressed(new EventHandler<MouseEvent>() //kada se klikne na avion ili kruzic
			{
				public void handle(MouseEvent event)
				{
					for (ShipPicker shippicker : pickedShips)//svima postavi prazan kruzic
					{
						shippicker.setCircleState(false);
					}
					shipToPick.setCircleState(true); //postavi pun izabranom 
					choosenShip = shipToPick.getShip();
				}
			});
			
			shipToPick.setOnMouseEntered(new EventHandler<MouseEvent>()
			{
				@Override
				public void handle(MouseEvent event)
				{
					shipToPick.setEffect(new DropShadow(10,Color.BLACK));
				}
			});
			
			shipToPick.setOnMouseExited(new EventHandler<MouseEvent>()
			{
				public void handle(MouseEvent event)
				{
					shipToPick.setEffect(null);
				}
			});
			
			box.getChildren().add(shipToPick);
		}
		
		box.setLayoutX(playSubScene.getWidth()/2 - box.prefWidth(-1)/2);
		box.setLayoutY(25 + label.prefHeight(-1) + 15);
		return box;
	}
	
	private SpaceShooterButton createStartButton()
	{
		SpaceShooterButton start = new SpaceShooterButton("START");
		start.setLayoutX(playSubScene.getWidth()/2 - start.prefWidth(-1)/2);
		start.setLayoutY(playSubScene.getLayoutY() + 300);
		start.setAlignment(Pos.CENTER);
		ViewManager vm = this;
		
		start.setOnAction(new EventHandler<ActionEvent>()
		{
			public void handle(ActionEvent event)
			{
				isGamePlayed = true;
				gameManager = new GameViewManager();
				if(choosenShip != null)
					gameManager.createNewGame(mainStage,choosenShip,vm);
			}
			
		});
		
		return start;
	}
	
	private void createPlaySubScene()
	{
		playSubScene = new SpaceShooterSubScene();
		mainPane.getChildren().add(playSubScene);
		
		createInfoLabel();
		playSubScene.getPane().getChildren().add(createShipsToChoose());
		playSubScene.getPane().getChildren().add(createStartButton());
		
	}
	
	/*private void moveLogo()
	{
		switch (logoMover)
		{
		case 0:
		{
			logo.setLayoutX(logo.getLayoutX()+0.5);
			logo.setLayoutY(logo.getLayoutY()+0.5);
		}break;
		case 1:
		{
			logo.setLayoutY(logo.getLayoutY()-0.5);
			logo.setLayoutX(logo.getLayoutX()+0.5);
		}break;
		case 2:
		{
			logo.setLayoutY(logo.getLayoutY()-0.5);
			logo.setLayoutX(logo.getLayoutX()-0.5);
		}break;
		case 3:
		{
			logo.setLayoutY(logo.getLayoutY()+0.5);
			logo.setLayoutX(logo.getLayoutX()-0.5);
		}break;
		default:
			break;
		}
		++logoMover;
		logoMover = logoMover % 4;
	}*/
}
