import java.awt.Graphics;

public class DOval extends DShape {
	public DOval()
	{
		model = new DOvalModel();
	}
	public void draw(Graphics g)
	{
		g.setColor(model.getColor());
		g.fillOval(model.getX(), model.getY(), model.getWidth(), model.getHeight());
	}

}
