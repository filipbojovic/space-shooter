package model;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.text.Font;

public class InfoLabel extends Label
{
	private static final String FONT_PATH = "src/model/resources/kenvector_future.ttf";
	private static final String IMG_PATH = "view/resources/infoPanel.png";
	
	public InfoLabel(String text)
	{
		setText(text);
		setFont();
		createBackground();
		setAlignment(Pos.CENTER);
		setPrefHeight(49);
		setPrefWidth(350);
	}

	private void createBackground()
	{
		BackgroundImage image = new BackgroundImage(new Image(IMG_PATH,350,49,false,true), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, null);
		setBackground(new Background(image));
	}

	private void setFont()
	{
		try
		{
			setFont(Font.loadFont(new FileInputStream(FONT_PATH), 23));
		} 
		catch (FileNotFoundException e)
		{
			setFont(Font.font("Verdana",15));
		}
	}
}
