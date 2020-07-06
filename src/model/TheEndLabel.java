package model;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javafx.scene.control.Label;
import javafx.scene.text.Font;

public class TheEndLabel extends Label
{
	private static final String FONT_PATH = "src/model/resources/kenvector_future.ttf";
	
	public TheEndLabel(String text)
	{
		setText(text);
		setPrefWidth(380);
		setMinHeight(50);
		setLabelFont();
	}
	
	private void setLabelFont()
	{
		try
		{
			setFont(Font.loadFont(new FileInputStream(FONT_PATH), 20));
		} 
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
	}
}
