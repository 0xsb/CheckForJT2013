package com.ailk.uap.util;

import java.io.File;
import java.io.IOException;

public class FileProUtil extends File
{
  private static final long serialVersionUID = -8937203555692866933L;

  private FileProUtil(String pathname)
  {
    super(pathname);
  }

  private static FileProUtil getInstance(String pathname) {
    return new FileProUtil(pathname);
  }

  public static boolean createDir(String dir)
  {
    if (!getInstance(dir).exists()) {
      return getInstance(dir).mkdir();
    }
    return true;
  }

  public static boolean createFile(String dir)
    throws IOException
  {
    if (getInstance(dir).exists()) {
      getInstance(dir).delete();
      return getInstance(dir).createNewFile();
    }
    return true;
  }
}