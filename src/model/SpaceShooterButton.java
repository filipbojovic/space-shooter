package model;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class SpaceShooterButton extends Button
{
	private static final String FONT_PATH = "src/model/resources/kenvector_future.ttf";
	private static final String BUTTON_PRESSED_STYLE = "-fx-background-color: transparent; -fx-background-image: url('model/resources/button.png');";
	private static final String BUTTON_FREE_STYLE = "-fx-background-color: transparent; -fx-background-image: url('model/resources/button.png');";
	
	public SpaceShooterButton(String text)
	{
		setText(text);
		setFont();
		setPrefHeight(45);
		setPrefWidth(190);
		setStyle(BUTTON_FREE_STYLE);
		initializeListeners();
	}
	
	private void initializeListeners()
	{
		setOnMousePressed(new EventHandler<MouseEvent>()
		{
			public void handle(MouseEvent event)
			{
				if(event.getButton().equals(MouseButton.PRIMARY))
					setButtonPressedStyle();
			}
		});
		
		setOnMouseReleased(new EventHandler<MouseEvent>()
		{
			public void handle(MouseEvent event)
			{
				if(event.getButton().equals(MouseButton.PRIMARY))
					setButtonFreeStyle();
			}
		});
		
		setOnMouseEntered(new EventHandler<MouseEvent>()
		{
			public void handle(MouseEvent event)
			{
				setEffect(new DropShadow(10,Color.WHITE));
			}
		});
		
		setOnMouseExited(new EventHandler<MouseEvent>()
		{
			public void handle(MouseEvent event)
			{
				setEffect(null);
			}
		});
	}

	private void setFont()
	{
		try
		{
			setFont(Font.loadFont(new FileInputStream(FONT_PATH),23));
		} 
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
	}
	
	private void setButtonPressedStyle()
	{
		setStyle(BUTTON_PRESSED_STYLE);
		setLayoutY(getLayoutY() + 4); 
	}
	
	private void setButtonFreeStyle()
	{
		setStyle(BUTTON_FREE_STYLE);
		setLayoutY(getLayoutY() - 4); 
	}
}
