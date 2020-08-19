package eu.uniga.core.styles;

public enum TextStyle
{
	Bold			(1     ),
	Italic			(1 << 1),
	Underlined		(1 << 2),
	StrikeThrough	(1 << 3),
	Obfuscated		(1 << 4),
	BlockQuote		(1 << 5);
	

	private final int _value;
	
	TextStyle(int value)
	{
		_value = value;
	}
	
	public int GetValue()
	{
		return _value;
	}
}