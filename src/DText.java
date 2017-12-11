import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;

public class DText extends DShape {
	public DText()
	{
		model = new DTextModel();
	}
	public void draw(Graphics g)
	{
		Shape clip = g.getClip();
		g.setClip(clip.getBounds().createIntersection(new Rectangle(model.getX(), model.getY(), model.getWidth(), model.getHeight())));
		g.setFont(((DTextModel)model).computeFont(g));
		g.setColor(model.getColor());
		g.drawString(((DTextModel)model).getText(), model.getX(), model.getY() + (3 * model.getHeight()/4));
		g.setClip(clip);
	}
}
