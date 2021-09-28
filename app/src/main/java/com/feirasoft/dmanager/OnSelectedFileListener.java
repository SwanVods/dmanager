package com.feirasoft.dmanager;

import java.io.File;

public interface OnSelectedFileListener {
    void onFileClicked(File file);
    void onFileLongClicked(File file, int position);
}
