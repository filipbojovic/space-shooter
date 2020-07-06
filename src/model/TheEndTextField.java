package model;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javafx.scene.control.TextField;
import javafx.scene.text.Font;

public class TheEndTextField extends TextField
{
	private static final String FONT_PATH = "src/model/resources/kenvector_future.ttf";
	
	public TheEndTextField()
	{
		setPrefWidth(350);
		setPrefHeight(60);
		setTextFieldFont();
	}
	
	private void setTextFieldFont()
	{
		try
		{
			this.setFont(Font.loadFont(new FileInputStream(FONT_PATH),25));
		} catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
