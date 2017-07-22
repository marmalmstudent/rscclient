package exceptions;

public class ItemException extends RuntimeException
{

	public ItemException()
	{
		super();
	}
	
	public ItemException(String message)
	{
		super(message);
	}

	private static final long serialVersionUID = -2528467059906045576L;
}
