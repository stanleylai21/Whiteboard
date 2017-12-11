import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Canvas extends JPanel implements ModelListener{
	private JTextField tf;
	private JComboBox<Font> cb;
	private ShapeTable st;
	private ArrayList<DShape> shapes = new ArrayList<DShape>();
	private DShape selectedShape;
	boolean isKnobActivated= false;
	boolean isRepositionActivated = false;
	private int dragDeltaX = -1000;
	private int dragDeltaY = -1000;
	public Canvas()
	{
		this.addMouseListener(new MouseAdapter(){
            public void mousePressed(MouseEvent e) {
              int[] bounds;
              boolean foundShape = false;
                for(int i = shapes.size()-1;i>=0;i--)
                {
                	if (selectedShape == shapes.get(i))
                	{
                		bounds = selectedShape.getBigBounds();
                	}
                	else bounds = shapes.get(i).getBounds();
                    if((e.getX() >= bounds[0] && e.getX() <= (bounds[0] + bounds[2]))
                            && e.getY() >= bounds[1] && e.getY() <= (bounds[1] + bounds[3])){
                        selectedShape = shapes.get(i);
                        foundShape = true;
                        break;
                    }
                }
                if (!foundShape)
                {
                    selectedShape = null;
                    isKnobActivated = false;
                    tf.setEditable(false);
                }
                repaint();
                if (selectedShape != null)
                {
                	for(int j = 0; j<selectedShape.getKnobs().size(); j++)
                	{
                		Point p = selectedShape.getKnobs().get(j);
                		if(e.getX() >= p.x && e.getX() <= (p.x + selectedShape.PSIZE))
                		{
                			if(e.getY() >= p.y && e.getY() <= (p.y + selectedShape.PSIZE))
                			{
                				isKnobActivated = true;
                				designateKnobs(p.x, p.y);
                			}
                		}
                	}
                }
                if (selectedShape instanceof DText)
                {
                	tf.setEditable(true);
                	tf.setText(((DTextModel)selectedShape.getDShapeModel()).getText());
                	Font font = new Font(((DTextModel)selectedShape.getDShapeModel()).getFontName(), Font.PLAIN, 1);
                	cb.setSelectedItem(font);
                }
                }
            public void mouseReleased(MouseEvent me) {  
        	  	if(isRepositionActivated)
        	  	{
        	  		isRepositionActivated = false;
            	  	dragDeltaX = -1000;
            	  	dragDeltaY = -1000;
        	  	}
        	  	if(isKnobActivated)
        	  	{
        	  		isKnobActivated = false;
        	  		selectedShape.getDShapeModel().updateModelAfterMouseRelease();
        	  	}
          }
        });
		this.addMouseMotionListener(new MouseAdapter() {
			public void mouseDragged(MouseEvent e) {
				if(isKnobActivated)
				{
					updateSelectedObject(e.getX(), e.getY());
				}	
				else if(selectedShape != null)
				{
					isRepositionActivated = true;
					if(dragDeltaX == -1000 && !isKnobActivated)
					{
						dragDeltaX = e.getX() - selectedShape.getDShapeModel().getX();
						dragDeltaY = e.getY() - selectedShape.getDShapeModel().getY();
					}
					
					updateSelectedObject(e.getX(), e.getY());
				}
	          }		
		});
	}
	public JTextField getTf() {
		return tf;
	}

	public void setTf(JTextField tf) {
		this.tf = tf;
	}
	public JComboBox getCb() {
		return cb;
	}
	public void setCb(JComboBox cb) {
		this.cb = cb;
	}
	public ShapeTable getSt() {
		return st;
	}
	public void setSt(ShapeTable st) {
		this.st = st;
	}
	private void designateKnobs(int x, int y)
	{
			for(int j = 0; j<selectedShape.getKnobs().size(); j++)
  			{
				Point p = selectedShape.getKnobs().get(j);
				if(p.x == x && p.y == y)
				{
					selectedShape.setMovingKnob(p);
				}
				else if(p.x != x && p.y != y)
				{
					selectedShape.setAnchorKnob(p);
				}
  			}
	}
	private void updateSelectedObject(int x, int y)
	{
		if(isKnobActivated)
		{
			selectedShape.getDShapeModel().resizeModel(x, y, selectedShape.getMovingKnob().x, selectedShape.getMovingKnob().y);
		}
		else if(isRepositionActivated)
		{
			selectedShape.getDShapeModel().repositionModel(x, y, dragDeltaX, dragDeltaY);
		}
	}
	public ArrayList<DShape> getShapes()
	{
		return shapes;
	}
	public DShape getSelectedShape()
	{
		return selectedShape;
	}
	public void setSelectedShape(DShape selectedShape) {
		this.selectedShape = selectedShape;
	}
	public void addShape(DShapeModel d)
	{
		d.addModelListener(this);
		if (d instanceof DRectModel)
		{
			DRect rect = new DRect();
			rect.setDShapeModel(d);
			setSelectedShape(rect);
			shapes.add(rect);
			d.setIndex(shapes.size()-1);
			st.refigureRows();
		}
		else if (d instanceof DOvalModel)
		{
			DOval oval = new DOval();
			oval.setDShapeModel(d);
			setSelectedShape(oval);
			shapes.add(oval);
			d.setIndex(shapes.size()-1);
			st.refigureRows();
		}
		else if (d instanceof DTextModel)
		{
			DText text = new DText();
			text.setDShapeModel(d);
			setSelectedShape(text);
			shapes.add(text);
        	d.setIndex(shapes.size()-1);
        	st.refigureRows();
		}
		else if (d instanceof DLineModel)
		{
			DLine line = new DLine();
			line.setDShapeModel(d);
			setSelectedShape(line);
			shapes.add(line);
			d.setIndex(shapes.size()-1);
			st.refigureRows();
		}
			
	}
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		for(int i = 0; i < shapes.size(); i++)
		{
			shapes.get(i).draw(g);
		}
		if (selectedShape != null)
		{
			selectedShape.drawKnobs(g);
		}
	}
	public void modelChanged(DShapeModel model) {
		repaint();
	}
	

}
