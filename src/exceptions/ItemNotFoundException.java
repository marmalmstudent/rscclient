package exceptions;

public class ItemNotFoundException extends ItemContainerException
{
	public ItemNotFoundException()
	{
		super();
	}
	
	public ItemNotFoundException(String message)
	{
		super(message);
	}

	private static final long serialVersionUID = 4007835284890021465L;

}
