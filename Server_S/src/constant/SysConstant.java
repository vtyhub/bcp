package constant;

import java.io.File;

import javax.swing.filechooser.FileSystemView;

public interface SysConstant {
	FileSystemView FSV = FileSystemView.getFileSystemView();

	File DESKTOP_PATH = FSV.getHomeDirectory();

	File DOCUMENT_PATH = FSV.getDefaultDirectory();

	String OS_NAME = System.getProperty("os.name");

	String USER_NAME = System.getProperty("user.name");

	String DEFAULT_CHARSET = "UTF-8";

	int BYTE_TO_BIT = 8;

	int CARRY = 1024;

	int KB = CARRY;

	int MB = KB * CARRY;

	int GB = MB * CARRY;

	int TB = GB * CARRY;

	int DEFAULT_BUFFER_SIZE = MB;

}
