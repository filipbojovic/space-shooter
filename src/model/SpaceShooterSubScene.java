package model;

import javafx.animation.TranslateTransition;
import javafx.scene.SubScene;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class SpaceShooterSubScene extends SubScene
{
	private final static String BACKGROUND_IMAGE = "model/resources/yellow_pane.png";
	private boolean isHidden;
	
	public SpaceShooterSubScene()
	{
		super(new AnchorPane() , 650 , 400);
		setBackground();
		setLayoutX(1030);
		setLayoutY(50);
		setEffect(new DropShadow(10,Color.WHITE));
		isHidden = true;
		
	}
	
	private void setBackground()
	{
		BackgroundImage image = new BackgroundImage(new Image(BACKGROUND_IMAGE,650,400,false,true), 
				BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT,BackgroundPosition.DEFAULT,null);
		
		((AnchorPane)this.getRoot()).setBackground(new Background(image));
	}
	
	public void moveSubScene()
	{
		TranslateTransition transition = new TranslateTransition();
		transition.setDuration(Duration.seconds(0.3));
		transition.setNode(this); //nad kojim elementom se vrsi tranzicija
		
		if(isHidden)
		{
			transition.setToX(-680); //pomeri je za *** unazad
			isHidden = false;
		}
		else
		{
			transition.setToX(0);
			isHidden = true;
		}
		transition.play();
	}
	
	public AnchorPane getPane()
	{
		return (AnchorPane)this.getRoot();
	}

}
