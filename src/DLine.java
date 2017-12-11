import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;

public class DLine extends DShape {
		public DLine()
		{
			model = new DLineModel();
		}
		public void draw(Graphics g)
		{
			g.setColor(model.getColor());
			g.drawLine(((DLineModel)model).getP1().x, ((DLineModel)model).getP1().y, ((DLineModel)model).getP2().x, ((DLineModel)model).getP2().y);
		}
		public ArrayList<Point> getKnobs()
		{
			ArrayList<Point> knobs = new ArrayList<Point>();
			Point p1 = new Point(model.getX() - PADJUST, model.getY() - PADJUST);
			Point p4 = new Point(model.getX() + model.getWidth() - PSIZE + PADJUST, model.getY() + model.getHeight() - PSIZE + PADJUST);
			knobs.add(p1);
			knobs.add(p4);
			return knobs;
		}
		public void drawKnobs(Graphics g)
		{
			ArrayList<Point> knobs = getKnobs();
			Point p = knobs.get(0);
			g.setColor(Color.BLACK);
			g.fillRect(p.x, p.y, PSIZE, PSIZE);
			p = knobs.get(1);
			g.setColor(Color.BLACK);
			g.fillRect(p.x, p.y, PSIZE, PSIZE);
				
		}
		
	}
