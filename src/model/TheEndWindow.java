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

public class TheEndWindow extends SubScene
{
	private final static String BACKGROUND_IMAGE = "model/resources/yellow_pane.png";
	
	public TheEndWindow()
	{
		super(new AnchorPane() , 400 , 300);
		setBackground();
		setLayoutX(610);
		setLayoutY(100);
		setEffect(new DropShadow(10,Color.WHITE));
	}
	
	public AnchorPane getPane()
	{
		return (AnchorPane)this.getRoot();
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
		transition.setNode(this);
		
		transition.setToX(-505);
		transition.play();
	}
}
