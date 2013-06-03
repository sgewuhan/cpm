package com.sg.widget.component.fileupload;

import java.io.File;

public interface IUploadListener {

	void event(String stateCode, int newState, int oldState, File uploadedFile, String selectedFileName, UploadPanel uploadPanel);

}
