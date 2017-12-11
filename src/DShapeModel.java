import java.awt.Color;
import java.util.ArrayList;

public class DShapeModel {
	protected int x;
	protected int y;
	protected int width;
	protected int height;
	protected int xO;
	protected int yO;
	protected int hO;
	protected int wO;
	protected int selectedP;
	private Color color;
	private int index;
	private ArrayList<ModelListener> listeners = new ArrayList<ModelListener>();
	public DShapeModel()
	{
		x = 10;
		y = 10;
		width = 20;
		height = 20;
		xO = x;
		yO = y;
		hO = height;
		wO = width;
		color = Color.GRAY;
	}
	
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
		notifyListeners();
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
		notifyListeners();
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
		notifyListeners();
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
		notifyListeners();
	}
	public Color getColor() {
		return color;
	}
	public void setColor(Color color) {
		this.color = color;
		notifyListeners();
	}
	public int getSelectedP() {
		return selectedP;
	}
	public void setSelectedP(int selectedP) {
		this.selectedP = selectedP;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public int[] getBounds()
	{
		int[] bounds = new int[4];
        bounds[0] = getX();
        bounds[1] = getY();
        bounds[2] = getWidth();
        bounds[3] = getHeight();
        return bounds;
	}
	public void addModelListener(ModelListener m)
	{
		listeners.add(m);
	}
	protected void notifyListeners()
	{
		for (ModelListener m:listeners)
		{
			m.modelChanged(this);
		}
	}
	public void updateModelAfterMouseRelease()
	{
		yO = y;
		xO = x;
		hO = height;
		wO = width;
		selectedP = 0;
	}
	public void repositionModel(int xReleased, int yReleased, int xDelta, int yDelta)
	{
		setX(xReleased - xDelta);
		setY(yReleased - yDelta);
		xO = x;
		yO = y;
	}
	public void resizeModel(int xDragged, int yDragged, int xKnob, int yKnob)
	{
		if(selectedP == 0)
		{
			if(xKnob == x - DShape.PADJUST && yKnob == y - DShape.PADJUST)
				selectedP = 1;
			else if(xKnob == (x + width - DShape.PSIZE + DShape.PADJUST) && yKnob == y - DShape.PADJUST)
				selectedP = 2;
			else if(xKnob == x - DShape.PADJUST && yKnob == (y + height - DShape.PSIZE + DShape.PADJUST))
				selectedP = 3;
			else if(xKnob == (x + width - DShape.PSIZE + DShape.PADJUST) && yKnob == (y + height - DShape.PSIZE + DShape.PADJUST))
				selectedP = 4;
		}
		if(selectedP == 1)
		{								
			setWidth(Math.abs(xO + wO - xDragged));
			setX(Math.min(xO + wO, xDragged));
			setHeight(Math.abs(yO + hO - yDragged));
			setY(Math.min(yO + hO, yDragged));
		}
		else if(selectedP == 2)
		{						
			setWidth(Math.abs(xDragged - xO));
			setX(Math.min(xO, xDragged));
			setHeight(Math.abs(yO + hO - yDragged));
			setY(Math.min(yO + hO, yDragged));
		}
		else if(selectedP == 3)
		{
			setWidth(Math.abs(xO + wO - xDragged));
			setX(Math.min(xO + wO, xDragged));
			setHeight(Math.abs(yDragged - yO));
			setY(Math.min(yO, yDragged));
		
		}
		else if(selectedP == 4)
		{
			setWidth(Math.abs(xDragged - xO));
			setX(Math.min(xO, xDragged));
			setHeight(Math.abs(yDragged - yO));
			setY(Math.min(yO, yDragged));
		}
	}
	public void mimic(DShapeModel other)
	{
		setX(other.getX());
		setY(other.getY());
		setWidth(other.getWidth());
		setHeight(other.getHeight());
		setColor(other.getColor());
	}
}
