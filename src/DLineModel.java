import java.awt.Point;

public class DLineModel extends DShapeModel {
	private Point p1;
	private Point p2;
	public DLineModel()
	{
		p1 = new Point(getX(), getY());
		p2 = new Point(getX() + getWidth(), getY() + getHeight());
	}
	public Point getP1() {
		return p1;
	}
	public void setP1(Point p1) {
		this.p1 = p1;
		notifyListeners();
	}
	public Point getP2() {
		return p2;
	}
	public void setP2(Point p2) {
		this.p2 = p2;
		notifyListeners();
	}
	
	public void repositionModel(int xReleased, int yReleased, int xDelta, int yDelta)
	{
		setX(xReleased - xDelta);
		setY(yReleased - yDelta);
		setP1(new Point(xReleased - xDelta, yReleased - yDelta));
		setP2(new Point(p1.x + getWidth(), p1.y + getHeight()));
		xO = x;
		yO = y;
	}
	public void resizeModel(int xDragged, int yDragged, int xKnob, int yKnob)
	{
		if(selectedP == 0)
		{
			if(xKnob == x - DShape.PADJUST && yKnob == y - DShape.PADJUST)
				selectedP = 1;
			else if(xKnob == (x + width - DShape.PSIZE + DShape.PADJUST) && yKnob == (y + height - DShape.PSIZE + DShape.PADJUST))
				selectedP = 4;
		}
		if(selectedP == 1)
		{								
			setWidth(xO + wO - xDragged);
			setX(xDragged);
			setHeight(yO + hO - yDragged);
			setY(yDragged);
			setP1(new Point(getX(), getY()));
			setP2(new Point(p1.x + getWidth(), p1.y + getHeight()));
		}
		else if(selectedP == 4)
		{
			setWidth(xDragged - xO);
			setX(xO);
			setHeight(yDragged - yO);
			setY(yO);
			setP1(new Point(getX(), getY()));
			setP2(new Point(p1.x + getWidth(), p1.y + getHeight()));
		}
	}
	public int[] getBounds()
	{
		int[] bounds = new int[4];
        bounds[0] = Math.min(getP1().x,getP2().x);
        bounds[1] = Math.min(getP1().y,getP2().y);
        bounds[2] = Math.abs(getP2().x - getP1().x);
        bounds[3] = Math.abs(getP2().y - getP1().y);
        return bounds;
	}
	public void mimic(DShapeModel other)
	{
		super.mimic(other);
		setP1(((DLineModel)other).getP1());
		setP2(((DLineModel)other).getP2());
	}
}
