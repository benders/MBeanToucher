package com.vocel.jmx;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.vocel.jmx.exception.MBTException;
import com.vocel.jmx.exception.MBTExceptionConstants;

/**
 * Call a remote JMX server and perform a variety of options against it.
 * Specifically, we need to be able to retrieve any parameter name that is
 * defined. We also need to be able to call any defined function.
 * 
 * Arguments passed on the command line will be used to dictact the following.
 * 
 * <ul>
 * <li>host</li>
 * <li>port</li>
 * <li>object</li>
 * <li>attribute to retrieve</li>
 * <li>method to invoke</li>
 * <li>attributes for the method (comma separated)
 * </ul>
 * 
 * We will use the Apache Commons CLI project to parse the command line options
 * for us. This should simplify the amount of code we need to write just to
 * handle the options.
 * 
 * @author josh
 * 
 */
public class MBeanToucher
{
    private static final Log           LOGGER = LogFactory.getLog(MBeanToucher.class);

    public static final ResourceBundle BUNDLE = ResourceBundle.getBundle(MBeanToucherConstants.PROPERTIES);

    /**
     * @param args
     */
    public static void main(String[] args)
    {
        try
        {
            CommandLine commandLine = new CommandLine();

            commandLine.parse(args);
            debugArguments(commandLine);

            /*
             * If they asked for help or they did not supply an attribute or
             * invoke, print the help
             */
            if (commandLine.hasOption(commandLine.getHelpOption())
                    || (!commandLine.hasOption(commandLine.getAttributeOption()) && !commandLine.hasOption(commandLine.getInvokeOption())))
            {
                commandLine.printHelp();
                System.exit(0);
            }

            /*
             * Construct the JMX base objects
             */
            MBeanServerConnection server = openConnectionToServer(commandLine);
            ObjectName objName = constructObjectName(commandLine.getObject());

            String output = null;

            /*
             * Process the correct option
             */
            if (commandLine.hasOption(commandLine.getAttributeOption()))
            {
                output = retrieveAttributeValue(server, objName, commandLine);
            }
            else if (commandLine.hasOption(commandLine.getInvokeOption()))
            {
                output = invokeMethod(server, objName, commandLine);

            }
            else
            // We don't know what they wanted, give them help
            {
                commandLine.printHelp();
                System.exit(0);
            }

            System.out.println(output);
        }
        catch (MBTException e)
        {
            LOGGER.error(e);
            
            
            ResourceBundle bundle = ResourceBundle.getBundle(MBTExceptionConstants.PROPERTIES);
            String output = bundle.getString(e.getKey());
            
            if (null != e.getFormatArguments())
            {
                output = MessageFormat.format(output, e.getFormatArguments());
            }

            System.out.println(output);
        }
        catch (Throwable e)
        {
            LOGGER.error(e);
            
            ResourceBundle bundle = ResourceBundle.getBundle(MBTExceptionConstants.PROPERTIES);
            
            System.out.println(bundle.getString(MBTExceptionConstants.DEFAULT_ERROR));
        }
    }

    private static ObjectName constructObjectName(String object)
    {
        try
        {
            return new ObjectName(object);
        }
        catch (MalformedObjectNameException e)
        {
            Object args[] = { object };
            throw new MBTException(MBTExceptionConstants.MALFORMED_OBJECT_NAME, args, e);
        }
        catch (NullPointerException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static String invokeMethod(MBeanServerConnection server, ObjectName objName, CommandLine commandLine)
    {
        try
        {
            String arguments[] = null;

            if (commandLine.hasOption(commandLine.getArgumentsOption()))
            {
                arguments = commandLine.getArguments();
            }

            Object result = server.invoke(objName, commandLine.getInvoke(), arguments, null);

            String format = BUNDLE.getString(MBeanToucherConstants.RESULT_OF_INVOKE);
            Object temp[] = { commandLine.getObject(), commandLine.getInvoke(), result };

            return MessageFormat.format(format, temp);
        }
        catch (InstanceNotFoundException e)
        {
            Object args[] = { commandLine.getInvoke() };
            throw new MBTException(MBTExceptionConstants.METHOD_NOT_FOUND, args, e);
        }
        catch (MBeanException e)
        {
            throw new RuntimeException(e);
        }
        catch (ReflectionException e)
        {
            Object args[] = { commandLine.getInvoke() };
            throw new MBTException(MBTExceptionConstants.METHOD_NOT_FOUND, args, e);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static String retrieveAttributeValue(MBeanServerConnection server, ObjectName objName, CommandLine commandLine)
    {
        try
        {
            Object value = server.getAttribute(objName, commandLine.getAttribute());

            String format = BUNDLE.getString(MBeanToucherConstants.RESULT_OF_ATTRIBUTE);
            Object temp[] = { commandLine.getObject(), commandLine.getAttribute(), value };

            return MessageFormat.format(format, temp);
        }
        catch (AttributeNotFoundException e)
        {
            Object args[] = { commandLine.getAttribute() };
            throw new MBTException(MBTExceptionConstants.ATTRIBUTE_NOT_FOUND, args, e);
        }
        catch (InstanceNotFoundException e)
        {
            throw new RuntimeException(e);
        }
        catch (MBeanException e)
        {
            throw new RuntimeException(e);
        }
        catch (ReflectionException e)
        {
            throw new RuntimeException(e);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static MBeanServerConnection openConnectionToServer(CommandLine commandLine)
    {
        Object hostArgs[] = { commandLine.getHost(), String.valueOf(commandLine.getPort()) };
        String format = BUNDLE.getString(MBeanToucherConstants.SERVICE_URI);
        String remoteAddress = MessageFormat.format(format, hostArgs);

        LOGGER.debug("Attempting to connect to " + remoteAddress);

        try
        {
            JMXServiceURL address = new JMXServiceURL("rmi", "", 0, remoteAddress);
            JMXConnector connector = JMXConnectorFactory.connect(address);
            return connector.getMBeanServerConnection();
        }
        catch (MalformedURLException e)
        {
            Object args[] = { remoteAddress };
            throw new MBTException(MBTExceptionConstants.MALFORMED_URL, args, e);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static void debugArguments(CommandLine commandLine)
    {
        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug("Host: " + (commandLine.hasOption(commandLine.getHostOption()) ? commandLine.getHost() : "none"));
            LOGGER.debug("Port: " + (commandLine.hasOption(commandLine.getPortOption()) ? commandLine.getPort() : "none"));
            LOGGER.debug("Attribute: " + (commandLine.hasOption(commandLine.getAttributeOption()) ? commandLine.getAttribute() : "none"));
            LOGGER.debug("Invoke: " + (commandLine.hasOption(commandLine.getInvokeOption()) ? commandLine.getInvoke() : "none"));
            LOGGER.debug("Invoke Args: " + (commandLine.hasOption(commandLine.getArgumentsOption()) ? commandLine.getArguments() : "none"));
            LOGGER.debug("Object: " + (commandLine.hasOption(commandLine.getObjectOption()) ? commandLine.getObject() : "none"));
            LOGGER.debug("Help: " + (commandLine.hasOption(commandLine.getHelpOption()) ? "supplied" : "not supplied"));
        }
    }
}
