package exceptions;

public class ItemContainerFullException extends ItemContainerException
{
	public ItemContainerFullException()
	{
		super();
	}
	
	public ItemContainerFullException(String message)
	{
		super(message);
	}

	private static final long serialVersionUID = 2248087026594879983L;

}
