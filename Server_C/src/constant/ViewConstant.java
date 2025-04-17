package constant;

import javax.swing.JOptionPane;

public interface ViewConstant {

	int DEFAULT_PORT_MAXLEN = 10;

	// =============================messageDialog================================

	String ERROR_TITLE = "Error";
	String WARNING_TITLE = "Warning";
	String PROMPT_TITLE = "Prompt";
	String SUCCESS_TITLE = "Success";
	String FAILED_TITLE = "Failed";
	String QUESTION_TITLE = "Doubt";

	int ERROR_MESSAGE_JOPT = JOptionPane.ERROR_MESSAGE;
	int WARNING_MESSAGE_JOPT = JOptionPane.WARNING_MESSAGE;
	int QUESTION_MESSAGE_JOPT = JOptionPane.QUESTION_MESSAGE;
	int PLAIN_MESSAGE_JOPT = JOptionPane.PLAIN_MESSAGE;
	int INFO_MESSAGE_JOPT = JOptionPane.INFORMATION_MESSAGE;

	int DEFAULT_OPT = JOptionPane.DEFAULT_OPTION;
	int YES_NO_OPT = JOptionPane.YES_NO_OPTION;
	int YE_NO_CANCEL_OPT = JOptionPane.YES_NO_CANCEL_OPTION;
	int OK_CANCEL_OPT = JOptionPane.OK_CANCEL_OPTION;

	int YES_RESULT = JOptionPane.YES_OPTION;
	int OK_RESULT = JOptionPane.OK_OPTION;
	int NO_RESULT = JOptionPane.NO_OPTION;
	int CANCEL_RESULT = JOptionPane.CANCEL_OPTION;
	int CLOSE_RESULT = JOptionPane.CLOSED_OPTION;

	String BCP_SET = "Set";
	String BCP_UNSET = "Unset";

	String UNSET_NOTIFICATION = "was not set!";
	String PP_UNSET_NOTIFICATION = "Public parameters were not set!";
	String BCP_UNSET_NOTIFICATION = "BCP's instance was not set!";
	String KEYPAIR_UNSET_NOTIFICATION = "Key pair was not set!";

	int FILE_WARNING_SIZE_MB = 1;// mb

}
