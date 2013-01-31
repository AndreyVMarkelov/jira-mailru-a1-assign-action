package ru.mail.jira.plugins.webwork;


import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.mail.jira.plugins.common.Constants;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.ComponentManager;
import com.atlassian.jira.bc.issue.comment.CommentService;
import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.config.SubTaskManager;
import com.atlassian.jira.event.type.EventDispatchOption;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.screen.FieldScreenRendererFactory;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.issue.search.SearchResults;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.util.UserUtil;
import com.atlassian.jira.util.SimpleErrorCollection;
import com.atlassian.jira.web.action.issue.AbstractCommentableAssignableIssue;
import com.atlassian.jira.web.bean.PagerFilter;
import com.atlassian.jira.webtests.Permissions;
import com.atlassian.query.Query;


@SuppressWarnings("unchecked")
public class AssignWebworkAction extends AbstractCommentableAssignableIssue
{
    private static final long serialVersionUID = -2431252751604507130L;

    private static final Logger log = LoggerFactory
        .getLogger(AssignWebworkAction.class);

    private static final String ASSIGN_ANYWAY_FALSE = "false";

    private ArrayList<User> assignableUsers;

    private SearchService searchService;

    private CommentService commentService;

    private String assignanyway;

    protected AssignWebworkAction(SubTaskManager subTaskManager,
        FieldScreenRendererFactory fieldScreenRendererFactory,
        CommentService commentService, UserUtil userUtil,
        SearchService searchService)
    {
        super(subTaskManager, fieldScreenRendererFactory, commentService,
            userUtil);
        this.searchService = searchService;
        this.commentService = commentService;
    }

    @Override
    public String doDefault() throws Exception
    {
        String result = super.doDefault();

        String projectKey = (String) getProject().get("key");
        Project currentProject = getProjectManager().getProjectObjByKey(
            projectKey);
        Set<User> usersList = getUserManager().getAllUsers();
        assignableUsers = new ArrayList<User>();
        for (User user : usersList)
        {
            if (getPermissionManager().hasPermission(
                Permissions.ASSIGNABLE_USER, currentProject, user))
            {
                assignableUsers.add(user);
            }
        }

        return result;
    }

    @Override
    public void doValidation()
    {
        MutableIssue issue = getIssueObject();
        if (getAssignee().equals(issue.getAssigneeId()))
        {
            addErrorMessage(getText("assign-webwork.assign.error.assigned")
                + getAssignee());
            return;
        }

        if (ASSIGN_ANYWAY_FALSE.equals(assignanyway))
        {
            // Validating vacations
            final String jqlQuery = "project = "
                + Constants.PROJECT_NAME_VACATIONS + " and assignee = "
                + getAssignee() + " and cf["
                + Constants.CF_VACATION_DATE_FROM_ID + "] <= now() and cf["
                + Constants.CF_VACATION_DATE_TO_ID + "] >= now()";

            List<Issue> issues = executeJQLQuery(jqlQuery);

            if (!issues.isEmpty())
            {
                CustomFieldManager customFieldManager = ComponentManager
                    .getInstance().getCustomFieldManager();

                CustomField cfDateFrom = customFieldManager
                    .getCustomFieldObject(Constants.CF_VACATION_DATE_FROM_ID);
                CustomField cfDateTo = customFieldManager
                    .getCustomFieldObject(Constants.CF_VACATION_DATE_TO_ID);

                Issue anIssue = issues.get(0);
                Date dateFrom = (Date) anIssue.getCustomFieldValue(cfDateFrom);
                Date dateTo = (Date) anIssue.getCustomFieldValue(cfDateTo);
                DateFormat dateFormat = DateFormat
                    .getDateInstance(DateFormat.SHORT);

                addErrorMessage(getText(
                    "assign-webwork.assign.error.onvacation", getAssignee(),
                    dateFormat.format(dateFrom), dateFormat.format(dateTo)));
            }
        }
    }

    @Override
    protected String doExecute() throws Exception
    {
        // assigning and sending comment
        final String comment = getComment();
        MutableIssue issue = getIssueObject();
        User currentUser = getCurrentUser();

        if (currentUser != null)
        {
            if (comment != null && comment != "")
            {
                this.commentService.create(currentUser, issue, comment, true,
                    new SimpleErrorCollection());
            }
            issue.setAssigneeId(getAssignee());
            getIssueManager().updateIssue(currentUser, issue,
                EventDispatchOption.ISSUE_UPDATED, false);
        }

        return SUCCESS;
    }

    public User getCurrentUser()
    {
        JiraAuthenticationContext authCtx = ComponentManager.getInstance()
            .getJiraAuthenticationContext();
        return authCtx.getLoggedInUser();
    }

    private List<Issue> executeJQLQuery(String jqlQuery)
    {
        List<Issue> result = null;

        User user = ComponentManager.getInstance()
            .getJiraAuthenticationContext().getLoggedInUser();
        SearchService.ParseResult parseResult = searchService.parseQuery(user,
            jqlQuery);
        if (parseResult.isValid())
        {
            Query query = parseResult.getQuery();
            try
            {
                SearchResults results = searchService.search(user, query,
                    PagerFilter.getUnlimitedFilter());
                result = results.getIssues();
            }
            catch (SearchException e)
            {
                log.error(
                    "AssignWebworkAction::search exception during executing JQL",
                    e);
            }
        }

        return result;
    }

    public ArrayList<User> getAssignableUsers()
    {
        return assignableUsers;
    }

    public void setAssignableUsers(ArrayList<User> assignableUsers)
    {
        this.assignableUsers = assignableUsers;
    }

    public String getAssignanyway()
    {
        return assignanyway;
    }

    public void setAssignanyway(String assignanyway)
    {
        this.assignanyway = assignanyway;
    }

}
