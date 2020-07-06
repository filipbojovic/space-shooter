package model;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class SmallInfoLabel extends Label
{
	private static final String FONT_PATH = "src/model/resources/sansation.ttf";
	private static final String IMG_PATH = "view/resources/small_info_panel.png";
	
	public SmallInfoLabel(String text)
	{
		setText(text);
		setFont();
		setTextFill(Color.WHITE);
		//createBackground();
		setAlignment(Pos.CENTER_LEFT);
		setPrefHeight(50);
		setPrefWidth(230);
		setPadding(new Insets(10,10,10,10));
	}

	private void createBackground()
	{
		BackgroundImage image = new BackgroundImage(new Image(IMG_PATH,230,50,false,true), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, null);
		setBackground(new Background(image));
	}

	private void setFont()
	{
		try
		{
			setFont(Font.loadFont(new FileInputStream(FONT_PATH), 20));
		} 
		catch (FileNotFoundException e)
		{
			setFont(Font.font("Verdana",15));
		}
	}
}
