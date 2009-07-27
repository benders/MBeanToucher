package com.vocel.jmx.exception;

public class MBTException extends RuntimeException
{
    private static final long serialVersionUID = -5894416762240541469L;

    private String key;
    private Object[] formatArguments;
    
    public MBTException()
    {
        setKey(MBTExceptionConstants.DEFAULT_ERROR);
    }

    public MBTException(String key)
    {
        setKey(key);
    }
    
    public MBTException(String key, Object formatArguments[])
    {
        this(key);
        setFormatArguments(formatArguments);
    }

    public MBTException(String key, Throwable t)
    {
        super(t);
        
        setKey(key);
    }

    public MBTException(String key, Object formatArguments[], Throwable t)
    {
        this(key, t);
        
        setFormatArguments(formatArguments);
    }

    public String getKey()
    {
        return key;
    }
    
    protected void setKey(String key)
    {
        this.key = key;
    }

    public Object[] getFormatArguments()
    {
        return formatArguments;
    }

    protected void setFormatArguments(Object[] formatArguments)
    {
        this.formatArguments = formatArguments;
    }
    
    
}
