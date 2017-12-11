import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

public class ShapeTable extends AbstractTableModel implements ModelListener {
	private Canvas canvas;
	private final int COLUMN_COUNT = 4;
	private ArrayList<DShape> shapes;
	public Canvas getCanvas() {
		return canvas;
	}
	public void setCanvas(Canvas canvas) {
		this.canvas = canvas;
	}
	public ArrayList<DShape> getShapes() {
		return shapes;
	}
	public void setShapes(ArrayList<DShape> shapes) {
		this.shapes = shapes;
	}
	public void modelChanged(DShapeModel model) {
		int i = model.getIndex();
		fireTableRowsUpdated(i, i);
	}
	public int getColumnCount() {
		return COLUMN_COUNT;
	}
	public int getRowCount() {
		return canvas.getShapes().size();
	}
	public Object getValueAt(int row, int column) {
		int[] rowOutput = new int[getRowCount()];
		for (int i = 0;i<rowOutput.length;i++)
		{
			rowOutput[i] = shapes.get(i).getDShapeModel().getIndex();
		}
		int[] columnOutput = new int[getColumnCount()];
		columnOutput[0] = shapes.get(rowOutput[row]).getDShapeModel().getX();
		columnOutput[1] = shapes.get(rowOutput[row]).getDShapeModel().getY();
		columnOutput[2] = shapes.get(rowOutput[row]).getDShapeModel().getWidth();
		columnOutput[3] = shapes.get(rowOutput[row]).getDShapeModel().getHeight();
		return Math.abs(columnOutput[column]);
		
	}
	public String getColumnName(int index)
	{
		String[] output = {"X", "Y", "Width", "Height"};
		return output[index];
	}
	public void refigureRows()
	{
		fireTableDataChanged();
	}
}
