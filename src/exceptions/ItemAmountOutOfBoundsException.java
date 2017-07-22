package exceptions;

public class ItemAmountOutOfBoundsException extends ItemException
{
	public ItemAmountOutOfBoundsException()
	{
		super();
	}
	
	public ItemAmountOutOfBoundsException(String message)
	{
		super(message);
	}

	private static final long serialVersionUID = 2248087026594879983L;

}
