package model;

import javafx.animation.TranslateTransition;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class Alien extends ImageView
{
	private int HP;
	private int damage;
	private static final String ALIEN_PATH = "view/resources/alien.png";
	
	public Alien()
	{
		super(ALIEN_PATH);
		HP = 1500;
		damage = 30;
		setEffect(new DropShadow(10,Color.WHITE));
	}
	
	public int getHP()
	{
		return HP;
	}
	
	public void setHP()
	{
		HP = 1000;
	}
	
	public void substractHP()
	{
		HP -= damage;
	}
}
