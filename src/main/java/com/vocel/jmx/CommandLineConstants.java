package com.vocel.jmx;

public interface CommandLineConstants
{
    public static final String PROPERTIES              = "com.vocel.jmx.resource.commandline";

    public static final String SEPARATOR               = ".";

    public static final String OPTION_PREFIX_HOST      = "host";
    public static final String OPTION_PREFIX_PORT      = "port";
    public static final String OPTION_PREFIX_ATTRIBUTE = "attribute";
    public static final String OPTION_PREFIX_INVOKE    = "invoke";
    public static final String OPTION_PREFIX_HELP      = "help";
    public static final String OPTION_PREFIX_OBJECT    = "object";
    public static final String OPTION_PREFIX_ARGUMENTS = "arguments";

    public static final String SUFFIX_OPTION           = SEPARATOR + "option";
    public static final String SUFFIX_LONG_OPTION      = SEPARATOR + "longOption";
    public static final String SUFFIX_HAS_ARGUMENT     = SEPARATOR + "hasArgument";
    public static final String SUFFIX_DESCRIPTION      = SEPARATOR + "description";

}
