package constant;

public interface RegExConstant {

	public static final String IPv4REGEXsimple = "^\\d{1,3}\\.\\d{1,3\\.\\d{1,3}\\.\\d{1,3}$";
	// public static final String IPv4REGEXchinz =
	// "^(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)$";
	public static final String IPv4REGEXjb = "^(?:(?:1[0-9][0-9]\\.)|(?:2[0-4][0-9]\\.)|(?:25[0-5]\\.)|(?:[1-9][0-9]\\.)|(?:[0-9]\\.)){3}(?:(?:1[0-9][0-9])|(?:2[0-4][0-9])|(?:25[0-5])|(?:[1-9][0-9])|(?:[0-9]))$";
	public static final String IPv6REGEX1 = "^\\s*((([0-9A-Fa-f]{1,4}:){7}([0-9A-Fa-f]{1,4}|:))|(([0-9A-Fa-f]{1,4}:){6}(:[0-9A-Fa-f]{1,4}|((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){5}(((:[0-9A-Fa-f]{1,4}){1,2})|:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){4}(((:[0-9A-Fa-f]{1,4}){1,3})|((:[0-9A-Fa-f]{1,4})?:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){3}(((:[0-9A-Fa-f]{1,4}){1,4})|((:[0-9A-Fa-f]{1,4}){0,2}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){2}(((:[0-9A-Fa-f]{1,4}){1,5})|((:[0-9A-Fa-f]{1,4}){0,3}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){1}(((:[0-9A-Fa-f]{1,4}){1,6})|((:[0-9A-Fa-f]{1,4}){0,4}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(:(((:[0-9A-Fa-f]{1,4}){1,7})|((:[0-9A-Fa-f]{1,4}){0,5}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:)))(%.+)?\\s*$";
	public static final String IPv6REGEX2 = "^([\\da-fA-F]{1,4}:){6}((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)|::([\\da−fA−F]1,4:)0,4((25[0−5]|2[0−4]\\d|[01]?\\d\\d?)\\.)3(25[0−5]|2[0−4]\\d|[01]?\\d\\d?)|::([\\da−fA−F]1,4:)0,4((25[0−5]|2[0−4]\\d|[01]?\\d\\d?)\\.)3(25[0−5]|2[0−4]\\d|[01]?\\d\\d?)|^([\\da-fA-F]{1,4}:):([\\da-fA-F]{1,4}:){0,3}((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)|([\\da−fA−F]1,4:)2:([\\da−fA−F]1,4:)0,2((25[0−5]|2[0−4]\\d|[01]?\\d\\d?)\\.)3(25[0−5]|2[0−4]\\d|[01]?\\d\\d?)|([\\da−fA−F]1,4:)2:([\\da−fA−F]1,4:)0,2((25[0−5]|2[0−4]\\d|[01]?\\d\\d?)\\.)3(25[0−5]|2[0−4]\\d|[01]?\\d\\d?)|^([\\da-fA-F]{1,4}:){3}:([\\da-fA-F]{1,4}:){0,1}((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)|([\\da−fA−F]1,4:)4:((25[0−5]|2[0−4]\\d|[01]?\\d\\d?)\\.)3(25[0−5]|2[0−4]\\d|[01]?\\d\\d?)|([\\da−fA−F]1,4:)4:((25[0−5]|2[0−4]\\d|[01]?\\d\\d?)\\.)3(25[0−5]|2[0−4]\\d|[01]?\\d\\d?)|^([\\da-fA-F]{1,4}:){7}[\\da-fA-F]{1,4}|:((:[\\da−fA−F]1,4)1,6|:)|:((:[\\da−fA−F]1,4)1,6|:)|^[\\da-fA-F]{1,4}:((:[\\da-fA-F]{1,4}){1,5}|:)|([\\da−fA−F]1,4:)2((:[\\da−fA−F]1,4)1,4|:)|([\\da−fA−F]1,4:)2((:[\\da−fA−F]1,4)1,4|:)|^([\\da-fA-F]{1,4}:){3}((:[\\da-fA-F]{1,4}){1,3}|:)|([\\da−fA−F]1,4:)4((:[\\da−fA−F]1,4)1,2|:)|([\\da−fA−F]1,4:)4((:[\\da−fA−F]1,4)1,2|:)|^([\\da-fA-F]{1,4}:){5}:([\\da-fA-F]{1,4})?|([\\da−fA−F]1,4:)6:";
	public static final String MACREGEX = "^[\\da-fA-F]{2}-[\\da-fA-F]{2}-[\\da-fA-F]{2}-[\\da-fA-F]{2}-[\\da-fA-F]{2}-[\\da-fA-F]{2}$";

	String DIGIT_REGEX = "^[^0]\\d+$";

	String DOMAIN_NAME_REGEX = "^[a-zA-Z0-9][-a-zA-Z0-9]{0,62}(\\.[a-zA-Z0-9][-a-zA-Z0-9]{0,62})+\\.?$";
	String URL_HTTP_REGEX = "^(?=^.{3,255}$)(http(s)?:\\/\\/)?(www\\.)?[a-zA-Z0-9][-a-zA-Z0-9]{0,62}(\\.[a-zA-Z0-9][-a-zA-Z0-9]{0,62})+(:\\d+)*(\\/\\w+\\.\\w+)*$";
	String URL_HTTP_ARGUEMENT_REGEX = "^(?=^.{3,255}$)(http(s)?:\\/\\/)?(www\\.)?[a-zA-Z0-9][-a-zA-Z0-9]{0,62}(\\.[a-zA-Z0-9][-a-zA-Z0-9]{0,62})+(:\\d+)*(\\/\\w+\\.\\w+)*([\\?&]\\w+=\\w*)*$";
}
