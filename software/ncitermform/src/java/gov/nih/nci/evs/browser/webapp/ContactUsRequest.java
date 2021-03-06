/*L
 * Copyright Northrop Grumman Information Technology.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/nci-term-suggestion/LICENSE.txt for details.
 */

package gov.nih.nci.evs.browser.webapp;

import gov.nih.nci.evs.browser.properties.*;
import gov.nih.nci.evs.utils.*;

/**
 * 
 */

/**
 * @author EVS Team (David Yee)
 * @version 1.0
 */

public class ContactUsRequest extends FormRequest {
    // List of attribute name(s):
    public static final String SUBJECT = "subject";
    public static final String EMAIL_MSG = "email_msg";
    public static final String EMAIL_ADDRESS = "email_address";
    public static final String WARNING_TYPE = "warning_type";

    public static final String WARNINGS = "warnings";
    public static final String ANSWER = "answer";

    private static final String[] ALL_PARAMETERS = new String[] {
        SUBJECT, EMAIL_MSG, EMAIL_ADDRESS };
    private static final String[] MOST_PARAMETERS = new String[] {
        SUBJECT, EMAIL_MSG };
    private static final String[] SESSION_ATTRIBUTES = new String[] {
        EMAIL_ADDRESS };

    public ContactUsRequest() {
        setParameters(ALL_PARAMETERS);
    }

    public void clear() {
        super.clear();
        clearAttributes(new String[] { WARNING_TYPE });
        clearSessionAttributes(SESSION_ATTRIBUTES);
    }

    public String submitForm() {
        clearAttributes(FormRequest.get_ALL_PARAMETERS());
        clearAttributes(new String[] { WARNING_TYPE });
        updateAttributes();
        updateSessionAttributes(SESSION_ATTRIBUTES);
        AppProperties appProperties = AppProperties.getInstance();

        try {
            String mailServer = appProperties.getMailSmtpServer();
            String subject = HTTPUtils.cleanXSS((String)_request.getParameter(SUBJECT));
            String emailMsg = HTTPUtils.cleanXSS((String)_request.getParameter(EMAIL_MSG));
            String from = HTTPUtils.cleanXSS((String)_request.getParameter(EMAIL_ADDRESS));
            String recipients = appProperties.getContactUrl();
            MailUtils.postMail(mailServer, from, recipients, subject, emailMsg, _isSendEmail);
        } catch (UserInputException e) {
            String warnings = e.getMessage();
            _request.setAttribute(WARNINGS, StringUtils.toHtml(warnings));
            _request.setAttribute(WARNING_TYPE, Prop.WarningType.User.name());
            return WARNING_STATE;
        } catch (Exception e) {
            String warnings = "System Error: Your message was not sent.\n";
            warnings += "    (If possible, please contact NCI systems team.)\n";
            warnings += "\n";
            warnings += e.getMessage();
            _request.setAttribute(WARNINGS, StringUtils.toHtml(warnings));
            _request.setAttribute(WARNING_TYPE, Prop.WarningType.System.name());
            e.printStackTrace();
            return WARNING_STATE;
        }

        clearAttributes(MOST_PARAMETERS);
        String msg = "Your message was successfully sent.";
        _request.setAttribute(MESSAGE, StringUtils.toHtml(msg));
        printSendEmailWarning();
        return SUCCESSFUL_STATE;
    }

    protected String printSendEmailWarning() {
        if (_isSendEmail)
            return "";

        String warning = super.printSendEmailWarning();
        StringBuffer buffer = new StringBuffer(warning);
        AppProperties appProperties = AppProperties.getInstance();
        String[] recipients = appProperties.getContactUsRecipients();
        String from = MailUtils.cleanAddresses(HTTPUtils.cleanXSS((String)_request.getParameter(EMAIL_ADDRESS)));
        buffer.append("Debug:\n");
        buffer.append("    * recipient(s): " + StringUtils.toString(recipients, " ; ") + "\n");

        String subject = HTTPUtils.cleanXSS((String)_request.getParameter(SUBJECT));


        buffer.append("    * Subject: " + subject + "\n");
        buffer.append("    * Message: ");
        String emailMsg = HTTPUtils.cleanXSS((String)_request.getParameter(EMAIL_MSG));
        emailMsg = INDENT + emailMsg.replaceAll("\\\n", "\n" + INDENT);
        buffer.append(emailMsg + "\n");
        buffer.append("    * Email: " + from + "\n");

        _request.setAttribute(WARNINGS, buffer.toString());
        return buffer.toString();
    }
}

