package com.vocel.jmx;

import java.util.ResourceBundle;

import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Contains the parsing logic for the command line options. The parsing is
 * delegated to the Apache Commons CLI project. The results of the parse are
 * contained as variables.
 * 
 * @author josh
 * 
 */
public class CommandLine
{
    private static final Log                   LOGGER = LogFactory.getLog(CommandLine.class);

    private Options                            options;
    private org.apache.commons.cli.CommandLine commandLine;
    private ResourceBundle                     bundle;

    public CommandLine()
    {
        options = new Options();
        constructParser();
    }

    public void parse(String arguments[])
    {
        CommandLineParser parser = new PosixParser();

        try
        {
            commandLine = parser.parse(this.options, arguments);
        }
        catch (ParseException e)
        {
            LOGGER.debug(e);
        }
    }

    public boolean hasOption(String option)
    {
        return getCommandLine().hasOption(option);
    }

    public String getHost()
    {
        String property = CommandLineConstants.OPTION_PREFIX_HOST + CommandLineConstants.SUFFIX_OPTION;

        return getCommandLineValue(property);
    }

    public int getPort()
    {
        String property = CommandLineConstants.OPTION_PREFIX_PORT + CommandLineConstants.SUFFIX_OPTION;
        String value = getCommandLineValue(property);

        return (null != value) ? Integer.parseInt(value) : -1;
    }

    public String getAttribute()
    {
        String property = CommandLineConstants.OPTION_PREFIX_ATTRIBUTE + CommandLineConstants.SUFFIX_OPTION;

        return getCommandLineValue(property);
    }

    public String getInvoke()
    {
        String property = CommandLineConstants.OPTION_PREFIX_INVOKE + CommandLineConstants.SUFFIX_OPTION;

        return getCommandLineValue(property);
    }

    public String getObject()
    {
        String property = CommandLineConstants.OPTION_PREFIX_OBJECT + CommandLineConstants.SUFFIX_OPTION;

        return getCommandLineValue(property);
    }

    public String[] getArguments()
    {
        String property = CommandLineConstants.OPTION_PREFIX_ARGUMENTS + CommandLineConstants.SUFFIX_OPTION;
        String value = getCommandLineValue(property);
        String arguments[] = value.split(",");

        for (int i = 0; i < arguments.length; i++)
        {
            arguments[i] = arguments[i].trim(); // get rid of extra whitespace
        }

        return arguments;
    }

    public void printHelp()
    {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(MBeanToucher.BUNDLE.getString(MBeanToucherConstants.APPLICATION_NAME), getOptions());
    }

    public ResourceBundle getBundle()
    {
        return bundle;
    }

    public String getHostOption()
    {
        return getBundle().getString(CommandLineConstants.OPTION_PREFIX_HOST + CommandLineConstants.SUFFIX_OPTION);
    }

    public String getPortOption()
    {
        return getBundle().getString(CommandLineConstants.OPTION_PREFIX_PORT + CommandLineConstants.SUFFIX_OPTION);
    }

    public String getAttributeOption()
    {
        return getBundle().getString(CommandLineConstants.OPTION_PREFIX_ATTRIBUTE + CommandLineConstants.SUFFIX_OPTION);
    }

    public String getInvokeOption()
    {
        return getBundle().getString(CommandLineConstants.OPTION_PREFIX_INVOKE + CommandLineConstants.SUFFIX_OPTION);
    }

    public String getHelpOption()
    {
        return getBundle().getString(CommandLineConstants.OPTION_PREFIX_HELP + CommandLineConstants.SUFFIX_OPTION);
    }

    public String getObjectOption()
    {
        return getBundle().getString(CommandLineConstants.OPTION_PREFIX_OBJECT + CommandLineConstants.SUFFIX_OPTION);
    }

    public String getArgumentsOption()
    {
        return getBundle().getString(CommandLineConstants.OPTION_PREFIX_ARGUMENTS + CommandLineConstants.SUFFIX_OPTION);
    }

    private org.apache.commons.cli.CommandLine getCommandLine()
    {
        return commandLine;
    }

    private Options getOptions()
    {
        return options;
    }

    private void constructParser()
    {
        options.addOption(getOptionForPrefix(CommandLineConstants.OPTION_PREFIX_HOST));
        options.addOption(getOptionForPrefix(CommandLineConstants.OPTION_PREFIX_PORT));
        options.addOption(getOptionForPrefix(CommandLineConstants.OPTION_PREFIX_HELP));
        options.addOption(getOptionForPrefix(CommandLineConstants.OPTION_PREFIX_OBJECT));
        options.addOption(getOptionForPrefix(CommandLineConstants.OPTION_PREFIX_ARGUMENTS));

        OptionGroup group = new OptionGroup();

        group.addOption(getOptionForPrefix(CommandLineConstants.OPTION_PREFIX_ATTRIBUTE));
        group.addOption(getOptionForPrefix(CommandLineConstants.OPTION_PREFIX_INVOKE));

        options.addOptionGroup(group);
    }

    private Option getOptionForPrefix(String prefix)
    {
        bundle = ResourceBundle.getBundle(CommandLineConstants.PROPERTIES);

        String option = bundle.getString(prefix + CommandLineConstants.SUFFIX_OPTION);
        String longOption = bundle.getString(prefix + CommandLineConstants.SUFFIX_LONG_OPTION);
        String hasArgument = bundle.getString(prefix + CommandLineConstants.SUFFIX_HAS_ARGUMENT);
        String description = bundle.getString(prefix + CommandLineConstants.SUFFIX_DESCRIPTION);

        return new Option(option, longOption, Boolean.parseBoolean(hasArgument), description);
    }

    private String getCommandLineValue(String property)
    {
        return getCommandLine().getOptionValue(getBundle().getString(property));
    }

}
