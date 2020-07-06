package model;

import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import model.SHIP;

public class ShipPicker extends VBox
{
	private ImageView circleImage;
	private ImageView shipImage;
	
	private static final String choosenCircle = "view/resources/shipchooser/choosen_circle.png";
	private static final String emptyCircle = "view/resources/shipchooser/empty_circle.png";
	private SHIP ship;
	private boolean isCircleChoosen; //da li je kruzic cekiran
	
	public ShipPicker(SHIP ship)
	{
		this.ship = ship;
		circleImage = new ImageView(emptyCircle);
		shipImage = new ImageView(ship.getShipURL());
		isCircleChoosen = false; //na pocetku kruzic nije cekiran
		this.setAlignment(Pos.CENTER);
		this.setSpacing(20);
		this.getChildren().addAll(circleImage,shipImage);
	}
	
	public SHIP getShip()
	{
		return ship;
	}
	
	public void setCircleState(boolean isCircleChoosen)
	{
		this.isCircleChoosen = isCircleChoosen;
		
		if(isCircleChoosen)
			circleImage.setImage(new Image(choosenCircle));
		else
			circleImage.setImage(new Image(emptyCircle));
	}
	
	public boolean getIsCircleChoosen()
	 {
		 return isCircleChoosen;
	 }
}
