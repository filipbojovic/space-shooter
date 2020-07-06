package model;

import javafx.scene.image.ImageView;

public class SmallMeteor extends ImageView
{
	private int HP;
	private int damage;
	
	public SmallMeteor(String url)
	{
		super(url);
		HP = 100;
		damage = 50;
	}
	
	public int getHP()
	{
		return HP;
	}
	
	public void setHP()
	{
		HP = 100;
	}
	
	public void substractHP()
	{
		HP -= damage;
	}
}
