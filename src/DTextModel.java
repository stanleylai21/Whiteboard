import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;


public class DTextModel extends DShapeModel {
	public DTextModel()
	{
		text = "Hello";
		fontName = "Dialog.plain";
		
	}
	private String fontName;
	private String text;
	public String getFontName() {
		return fontName;
	}
	public void setFontName(String fontName) {
		this.fontName = fontName;
		notifyListeners();
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
		notifyListeners();
	}
	public Font computeFont(Graphics g)
	{
		double size = 1.0;
		Font font = new Font(fontName, Font.PLAIN, (int)size);
		FontMetrics metrics = g.getFontMetrics(font);
		while (metrics.getHeight() < getHeight())
		{
			size = (size*1.10)+1;
			metrics = g.getFontMetrics(font.deriveFont((float)size));
		}
		return font.deriveFont((float)size);
	}
	public void mimic(DShapeModel other)
	{
		super.mimic(other);
		setFontName(((DTextModel)other).getFontName());
		setText(((DTextModel)other).getText());
	}
}
