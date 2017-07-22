package exceptions;

public class ItemContainerException extends RuntimeException
{
	public ItemContainerException()
	{
		super();
	}
	
	public ItemContainerException(String message)
	{
		super(message);
	}

	private static final long serialVersionUID = -2913458606548320739L;

}
