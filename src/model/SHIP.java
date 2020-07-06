package model;

public enum SHIP
{
	BLUE("view/resources/shipchooser/blue_ship.png","view/resources/shipchooser/blue_life.png"),
	ORANGE("view/resources/shipchooser/orange_ship.png","view/resources/shipchooser/orange_life.png"),
	RED("view/resources/shipchooser/red_ship.png","view/resources/shipchooser/red_life.png"),
	GREEN("view/resources/shipchooser/green_ship.png","view/resources/shipchooser/green_life.png");
	
	private String shipURL;
	private String lifeURL;
	
	private SHIP(String shipURL , String lifeURL)
	{
		this.shipURL = shipURL;
		this.lifeURL = lifeURL;
	}

	public String getShipURL()
	{
		return this.shipURL;
	}

	public String getLifeURL()
	{
		return this.lifeURL;
	}
}
