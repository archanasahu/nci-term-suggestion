/*L
 * Copyright Northrop Grumman Information Technology.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/nci-term-suggestion/LICENSE.txt for details.
 */

package gov.nih.nci.evs.browser.webapp;

import java.util.*;
import javax.servlet.http.*;
import gov.nih.nci.evs.browser.properties.*;
import gov.nih.nci.evs.utils.*;
import gov.nih.nci.evs.browser.utils.*;

/**
 * 
 */

/**
 * @author EVS Team (David Yee, Kim Ong)
 * @version 1.0
 */

public class SuggestionRequest extends FormRequest {
    public static final HashMap<String, String> LABELS_HASHMAP = getLabelsHashMap();

    // List of parameter/attribute name(s):
    public static final String EMAIL = "email";
    public static final String OTHER = "other";
    public static final String VOCABULARY = "vocabulary";
    public static final String TERM = "term";
    public static final String SYNONYMS = "synonyms";
    public static final String NEAREST_CODE = "code";
    public static final String DEFINITION = "definition";
    public static final String PROJECT = "project";
    public static final String REASON = "reason";
    public static final String CADSR_SOURCE = "cadsrSource";
    public static final String CADSR_TYPE = "cadsrType";
    public static final String ANSWER = "answer";

    public static final String VERSION = "version";

    // List of field label(s):
    public static final String EMAIL_LABEL = "Business Email";
    public static final String OTHER_LABEL = "Other";
    public static final String VOCABULARY_LABEL = "Vocabulary";
    public static final String TERM_LABEL = "Term";
    public static final String SYNONYMS_LABEL = "Synonym(s)";
    public static final String NEAREST_CODE_LABEL = "Nearest Code/CUI";
    public static final String DEFINITION_LABEL = "Definition/Other";
    public static final String PROJECT_LABEL = "Project/Product term needed for";
    public static final String REASON_LABEL = "Reason for suggestion plus any" +
        " other additional information";
    public static final String CADSR_SOURCE_LABEL = "Source";
    public static final String CADSR_TYPE_LABEL = "caDSR Type";

    public static final String ANSWER_LABEL = "Answer";


    // Parameter list(s):
    private static final String[] ALL_PARAMETERS = new String[] {
        EMAIL, OTHER, VOCABULARY, TERM, SYNONYMS, NEAREST_CODE,
        DEFINITION, PROJECT, REASON, CADSR_SOURCE, CADSR_TYPE, ANSWER };


    private static final String[] REQUIRED_FIELDS = new String[] {EMAIL, TERM, ANSWER };

    private static final String[] MOST_PARAMETERS = new String[] {
        /* EMAIL, OTHER, VOCABULARY, */ TERM, SYNONYMS, NEAREST_CODE,
        DEFINITION, PROJECT, REASON, CADSR_SOURCE, CADSR_TYPE, ANSWER };
    private static final String[] SESSION_ATTRIBUTES = new String[] {
        EMAIL, OTHER, VOCABULARY };

    public SuggestionRequest() {
        super(VOCABULARY);
        setParameters(ALL_PARAMETERS);
    }

    public static String[] get_REQUIRED_FIELDS() {
		return Arrays.copyOf(REQUIRED_FIELDS, REQUIRED_FIELDS.length);
	}

    public static String[] get_MOST_PARAMETERS() {
		return Arrays.copyOf(MOST_PARAMETERS, MOST_PARAMETERS.length);
	}

    private static HashMap<String, String> getLabelsHashMap() {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put(EMAIL, EMAIL_LABEL);
        hashMap.put(OTHER, OTHER_LABEL);
        hashMap.put(VOCABULARY, VOCABULARY_LABEL);
        hashMap.put(TERM, TERM_LABEL);
        hashMap.put(SYNONYMS, SYNONYMS_LABEL);
        hashMap.put(NEAREST_CODE, NEAREST_CODE_LABEL);
        hashMap.put(DEFINITION, DEFINITION_LABEL);
        hashMap.put(PROJECT, PROJECT_LABEL);
        hashMap.put(REASON, REASON_LABEL);
        hashMap.put(CADSR_SOURCE, CADSR_SOURCE_LABEL);
        hashMap.put(CADSR_TYPE, CADSR_TYPE_LABEL);

        hashMap.put(ANSWER, ANSWER_LABEL);

        return hashMap;
    }

    public void clear() {
        super.clear();
        clearSessionAttributes(SESSION_ATTRIBUTES);
    }


    public void updateFormAttributes() {
        clearAttributes(FormRequest.get_ALL_PARAMETERS());
        updateAttributes();
        updateSessionAttributes(SESSION_ATTRIBUTES);
        _parametersHashMap.put(EMAIL,
            MailUtils.cleanAddresses(_parametersHashMap.get(EMAIL)));
	}



    public String submitForm() throws Exception {
        //clearAttributes(FormRequest.ALL_PARAMETERS);
        updateAttributes();
        updateSessionAttributes(SESSION_ATTRIBUTES);
        _parametersHashMap.put(EMAIL,
            MailUtils.cleanAddresses(_parametersHashMap.get(EMAIL)));

        String warnings = validate();
        if (warnings.length() > 0)
            throw new Exception(warnings);

        AppProperties appProperties = AppProperties.getInstance();
        String mailServer = appProperties.getMailSmtpServer();
        String from = _parametersHashMap.get(EMAIL);
        String recipients = getRecipients();
        String subject = getSubject();
        String emailMsg = getEmailMessage();

        if (_isSendEmail)
            MailUtils.postMail(mailServer, from, recipients, subject, emailMsg);

        clearAttributes(MOST_PARAMETERS);
        String msg = "FYI: The following request has been sent:\n";
        msg += "    * " + StringUtils.wrap(80, getSubject());
        _request.setAttribute(MESSAGE, msg);
        printSendEmailWarning();
        return SUCCESSFUL_STATE;
    }

    protected String getRecipients() {
        AppProperties appProperties = AppProperties.getInstance();
        String vocabulary = _parametersHashMap.get(VOCABULARY);
        String version = (String)
            _request.getSession().getAttribute(VERSION);

        String cadsrEmails = appProperties.getCADSREmailString();
        if (version != null && version.compareToIgnoreCase("CADSR") == 0 && cadsrEmails.length() > 0)
            return cadsrEmails;
        return appProperties.getVocabularyEmailsString(version, vocabulary);
    }

    private String validate() {
        StringBuffer buffer = new StringBuffer();
        String email = _parametersHashMap.get(EMAIL);
        validate(buffer, email != null && email.trim().length() > 0 &&
            MailUtils.isValidEmailAddresses(email),
            "* Please enter a valid email address.");

        String vocabulary = _parametersHashMap.get(VOCABULARY);
        validate(buffer, vocabulary != null && vocabulary.length() > 0,
            "* Please select a vocabulary.");

        String term = _parametersHashMap.get(TERM);
        validate(buffer, term != null && term.trim().length() > 0,
            "* Please enter a term.");
        return buffer.toString();
    }

    private String getSubject() {
        String term = _parametersHashMap.get(TERM);
        String vocabulary = _parametersHashMap.get(VOCABULARY);
        String value = "Term Suggestion for";
        if (term.length() > 0)
            value += " " + vocabulary + ": " + term;
        return value;
    }

    private String getEmailMessage() {
        String version = (String)
            _request.getSession().getAttribute(VERSION);

        StringBuffer buffer = new StringBuffer();
        buffer.append(getSubject() + "\n\n");
        buffer.append("Contact information:\n");
        buffer_append(buffer, EMAIL_LABEL, EMAIL);
        buffer_append(buffer, OTHER_LABEL, OTHER);
        buffer.append("\n");
        buffer.append("Term Information:\n");
        buffer_append(buffer, VOCABULARY_LABEL, VOCABULARY);
        buffer_append(buffer, TERM_LABEL, TERM);
        buffer_append(buffer, SYNONYMS_LABEL, SYNONYMS);
        buffer_append(buffer, NEAREST_CODE_LABEL, NEAREST_CODE);
        buffer_append(buffer, DEFINITION_LABEL, DEFINITION);
        //if (version == Prop.Version.CADSR) {
		if (version != null && version.compareToIgnoreCase("CADSR") == 0) {
            buffer_append(buffer, CADSR_SOURCE_LABEL, CADSR_SOURCE);
            buffer_append(buffer, CADSR_TYPE_LABEL, CADSR_TYPE);
        }
        buffer.append("\n");
        buffer.append("Additional Information:\n");
        buffer_append(buffer, PROJECT_LABEL, PROJECT);
        buffer_append(buffer, REASON_LABEL, REASON);
        String retstr = buffer.toString();
        retstr = StringUtil.replaceSpecialCharacters(retstr);
        return retstr;
    }

    protected String printSendEmailWarning() {
        if (_isSendEmail)
            return "";

        String warning = super.printSendEmailWarning();
        StringBuffer buffer = new StringBuffer(warning);
        buffer.append("Subject: " + getSubject() + "\n");
        buffer.append("Message:\n");
        String emailMsg = getEmailMessage();
        emailMsg = INDENT + emailMsg.replaceAll("\\\n", "\n" + INDENT);
        buffer.append(emailMsg);

        _request.setAttribute(WARNINGS, buffer.toString());
        return buffer.toString();
    }

    public static void setupTestData() {
        boolean useTestData = false;
        if (! useTestData)
            return;

        HttpServletRequest request = HTTPUtils.getRequest();
        HTTPUtils.setDefaulSessiontAttribute(request, EMAIL, "John.Doe@abc.com");
        HTTPUtils.setDefaulSessiontAttribute(request, OTHER, "Phone: 987-654-3210");
        HTTPUtils.setDefaulSessiontAttribute(request, VOCABULARY, "NCI Thesaurus");
        HTTPUtils.setDefaultAttribute(request, TERM, "Ultra Murine Cell Types");
        HTTPUtils.setDefaultAttribute(request, SYNONYMS,
            "Cell Types; Cell; Murine Cell Types");
        HTTPUtils.setDefaultAttribute(request, NEAREST_CODE, "C23442");
        HTTPUtils.setDefaultAttribute(request, DEFINITION,
            "The smallest units of living structure capable of independent" +
            " existence, composed of a membrane-enclosed mass of protoplasm" +
            " and containing a nucleus or nucleoid. Cells are highly variable" +
            " and specialized in both structure and function, though all must" +
            " at some stage replicate proteins and nucleic acids, utilize" +
            " energy, and reproduce themselves.");
        HTTPUtils.setDefaultAttribute(request, CADSR_SOURCE,
            AppProperties.getInstance().getCADSRSourceList()[1]);
        HTTPUtils.setDefaultAttribute(request, CADSR_TYPE,
            AppProperties.getInstance().getCADSRTypeList()[1]);
        HTTPUtils.setDefaultAttribute(request, PROJECT,
            "NIH NCI CBIIT");
        HTTPUtils.setDefaultAttribute(request, REASON,
            "New improved version of the previous type.");
    }
}
