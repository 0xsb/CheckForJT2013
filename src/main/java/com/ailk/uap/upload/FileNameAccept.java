package com.ailk.uap.upload;

import java.io.File;
import java.io.FilenameFilter;

public class FileNameAccept
  implements FilenameFilter
{
  String extension = ".";

  public FileNameAccept(String fileExtensionNoDot) {
    extension = extension.concat(fileExtensionNoDot);
  }

  public boolean accept(File dir, String name) {
    return name.endsWith(extension);
  }
}