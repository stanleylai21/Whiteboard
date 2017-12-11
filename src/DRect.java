import java.awt.Graphics;

public class DRect extends DShape {
	public DRect()
	{
		model = new DRectModel();
	}
	public void draw(Graphics g)
	{
		g.setColor(model.getColor());
		g.fillRect(model.getX(), model.getY(), model.getWidth(), model.getHeight());
	}

}
