import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;

public abstract class DShape {
	protected DShapeModel model;
	private Point movingKnob;
	private Point anchorKnob;
	public static final int PSIZE = 9;
	public static final int PADJUST = 4;
	public abstract void draw(Graphics g);
	public void drawKnobs(Graphics g)
	{
		ArrayList<Point> knobs = getKnobs();
		for(int i = 0; i<knobs.size(); i++)
		{
			Point p = knobs.get(i);
			g.setColor(Color.BLACK);
			g.fillRect(p.x, p.y, PSIZE, PSIZE);
		}
	}
	public DShapeModel getDShapeModel()
	{
		return model;
	}
	public void setDShapeModel(DShapeModel d)
	{
		model = d;
	}
	
	public Point getMovingKnob() {
		return movingKnob;
	}
	public void setMovingKnob(Point movingKnob) {
		this.movingKnob = movingKnob;
	}
	public Point getAnchorKnob() {
		return anchorKnob;
	}
	public void setAnchorKnob(Point anchorKnob) {
		this.anchorKnob = anchorKnob;
	}
	public int[] getBounds()
	{
		return model.getBounds();
	}
	public int[] getBigBounds()
	{
		int[] bounds = model.getBounds();
		bounds[0] -= PADJUST;
		bounds[1] -= PADJUST;
		bounds[2] += 2 * PADJUST;
		bounds[3] += 2 * PADJUST;
		return bounds;
	}
	public ArrayList<Point> getKnobs()
	{
		ArrayList<Point> knobs = new ArrayList<Point>();
		Point p1 = new Point(model.getX() - PADJUST, model.getY() - PADJUST);
		Point p2 = new Point(model.getX() + model.getWidth() - PSIZE + PADJUST, model.getY() - PADJUST);
		Point p3 = new Point(model.getX() - PADJUST, model.getY() + model.getHeight() - PSIZE + PADJUST);
		Point p4 = new Point(model.getX() + model.getWidth() - PSIZE + PADJUST, model.getY() + model.getHeight() - PSIZE + PADJUST);
		knobs.add(p1);
		knobs.add(p2);
		knobs.add(p3);
		knobs.add(p4);
		return knobs;
	}
}
