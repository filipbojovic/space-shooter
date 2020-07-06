package model;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;

public class LevelLabel extends Label
{
	private static final String FONT_PATH = "src/model/resources/kenvector_future.ttf";
	private TranslateTransition transition;
	
	public LevelLabel(String text)
	{
		setText(text);
		setLabelFont();
		this.setLayoutY(200);
		this.setLayoutX(600);
		setPrefWidth(190);
		setPrefHeight(40);
		setBackground(new Background(new BackgroundFill(Color.RED,CornerRadii.EMPTY,Insets.EMPTY)));
		setAlignment(Pos.CENTER);
		this.setTextFill(Color.YELLOW);
	}
	
	public void showLabel()
	{
		transition = new TranslateTransition();
		transition.setDuration(Duration.seconds(0.3));
		transition.setNode(this);
		transition.setToX(-600 + (300 - prefWidth(-1)/2));
		transition.play();
	}
	
	public void hideLabel()
	{
		transition = new TranslateTransition();
		transition.setDuration(Duration.seconds(0.1));
		transition.setNode(this);
		
		transition.setToX(0);
		transition.play();
	}
	
	
	private void setLabelFont()
	{
		try
		{
			setFont(Font.loadFont(new FileInputStream(FONT_PATH), 35));
		} 
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
	}
}
