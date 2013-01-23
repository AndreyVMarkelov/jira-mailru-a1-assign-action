package ru.mail.jira.plugins.common;


public class Constants
{
    public final static String PROJECT_NAME_ALLODS = "ALLODS";
    public final static String PROJECT_NAME_VACATIONS = "VCN";

    /**
     * id for issue type "Feature"
     */
    public final static String ISSUE_TYPE_FEATURE_ID = "9";

    /**
     * id for issue type "Team"
     */
    public final static String ISSUE_TYPE_TEAM_ID = "8";

    /**
     * id for issue type "Sprint"
     */
    public final static String ISSUE_TYPE_SPRINT_ID = "7";

    /**
     * id for issue type "Version"
     */
    public final static String ISSUE_TYPE_VERSION_ID = "6";

    /**
     * id for issue type "Task"
     */
    public final static String ISSUE_TYPE_TASK_ID = "3";

    /**
     * id for custom field "Versions"
     */
    public final static Long CF_FEATURE_VERSIONS_ID = Long.valueOf(10305);

    /**
     * id for custom field "Design"
     */
    public final static Long CF_FEATURE_DESIGN_ID = Long.valueOf(10201);

    /**
     * id for custom field "Driver"
     */
    public final static Long CF_FEATURE_DRIVER_ID = Long.valueOf(10202);

    /**
     * id for custom field "QA"
     */
    public final static Long CF_FEATURE_QA_ID = Long.valueOf(10203);

    /**
     * id for custom field "Feature"
     */
    public final static Long CF_TASK_FEATURE_ID = Long.valueOf(10501);

    /**
     * id for custom field "Date from"
     */
    public final static Long CF_VACATION_DATE_FROM_ID = Long.valueOf(10311);

    /**
     * id for custom field "Date to"
     */
    public final static Long CF_VACATION_DATE_TO_ID = Long.valueOf(10312);

    private Constants()
    {
    };
}
