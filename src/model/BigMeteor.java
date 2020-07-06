package model;

import javafx.scene.image.ImageView;

public class BigMeteor extends ImageView
{
	private int HP;
	private int damage;
	
	public BigMeteor(String url)
	{
		super(url);
		HP = 200;
		damage = 20;
	}
	
	public int getHP()
	{
		return HP;
	}
	
	public void setHP()
	{
		HP = 200;
	}
	
	public void substractHP()
	{
		HP -= damage;
	}
}
