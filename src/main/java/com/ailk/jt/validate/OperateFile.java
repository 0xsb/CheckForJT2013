package com.ailk.jt.validate;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class OperateFile {

	// 复制文件
	public static void copyFile(File sourceFile, File targetFile) throws Exception {
		InputStreamReader inBuff = null;
		OutputStreamWriter outBuff = null;
		BufferedReader bReader = null;
		BufferedWriter bWriter = null;

		try {
			if (!targetFile.exists()) {
				targetFile.createNewFile();
			} else {
				targetFile.delete();
			}
			// 新建文件输入流并对它进行缓冲
			bReader = new BufferedReader(new InputStreamReader(new FileInputStream(sourceFile), "UTF-8"));

			// 新建文件输出流并对它进行缓冲
			bWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(targetFile), "UTF-8"));
			String tempString = "";
			while ((tempString = bReader.readLine()) != null) {
				bWriter.write(tempString);
				bWriter.newLine();// 换行
				bWriter.flush(); // 刷新此缓冲的输出流
			}
		} finally {
			// 关闭流
			if (bWriter != null)
				bWriter.close();
			if (bReader != null)
				bReader.close();
			if (inBuff != null)
				inBuff.close();
			if (outBuff != null)
				outBuff.close();
		}
	}

	// 复制文件
	public static void copyFile_old(File sourceFile, File targetFile) throws Exception {
		BufferedInputStream inBuff = null;
		BufferedOutputStream outBuff = null;
		try {
			// 新建文件输入流并对它进行缓冲
			inBuff = new BufferedInputStream(new FileInputStream(sourceFile));

			// 新建文件输出流并对它进行缓冲
			outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));

			// 缓冲数组
			byte[] b = new byte[1024 * 5];
			int len;
			while ((len = inBuff.read(b)) != -1) {
				outBuff.write(b, 0, len);
			}
			// 刷新此缓冲的输出流
			outBuff.flush();
		} finally {
			// 关闭流
			if (inBuff != null)
				inBuff.close();
			if (outBuff != null)
				outBuff.close();
		}
	}

	// public static void copyFile(File srcFileName, File destFileName, String
	// srcCoding, String destCoding)
	// throws Exception {// 把文件转换为GBK文件
	// BufferedReader br = null;
	// BufferedWriter bw = null;
	// try {
	// br = new BufferedReader(new InputStreamReader(new
	// FileInputStream(srcFileName), srcCoding));
	// bw = new BufferedWriter(new OutputStreamWriter(new
	// FileOutputStream(destFileName), destCoding));
	// char[] cbuf = new char[1024 * 5];
	// int len = cbuf.length;
	// int off = 0;
	// int ret = 0;
	// while ((ret = br.read(cbuf, off, len)) > 0) {
	// off += ret;
	// len -= ret;
	// }
	// bw.write(cbuf, 0, off);
	// bw.flush();
	// } finally {
	// if (br != null)
	// br.close();
	// if (bw != null)
	// bw.close();
	// }
	// }

	// TABLENAME.OWNER_YYYYMMDDHH24.End TABLENAME.OWNER_YYYYMMDDHH24.txt
	// （导入数据文件）
	// 文件目录下面 所有.End 后缀的文件名 20120413 ()
	public static String[] searchEndFileName(String filePath, String type) throws Exception {
		return new File(filePath).list(filter("." + type));
	}

	public static String searchEndFile(String filePath, String type) throws Exception {
		return new File(filePath).listFiles(filter("." + type))[0].getName();
	}

	// type：必须声明为final类型，作为一个匿名内部类，访问的变量都必须声明为final类型
	private static FilenameFilter filter(final String type) throws Exception {
		return new FilenameFilter() {
			public boolean accept(File file, String path) {
				String filename = new File(path).getName();
				return filename.indexOf(type) != (-1);
			}
		};
	}

	// 移动文件到相应的文件夹 ${HOME}/data ${HOME}/data/err/ ${HOME}/data/his/${YYYYMM}/
	// public static void moveRenameFile(String fileName, String newPath, String
	// dateYM) throws Exception {
	// String rootPath = "";
	//
	// String newFileAllPath = "";
	// if ("err".equals(newPath)) {
	// newFileAllPath = rootPath + "/err/" + fileName;
	// } else if ("his".equals(newPath)) {
	// newFileAllPath = rootPath + "/his/" + dateYM + "/" + fileName;
	// } else {
	// return;
	// }
	// File newFile = new File(newFileAllPath);
	// if (!newFile.getParentFile().exists()) {
	// System.out.println("目标文件所在路径不存在，准备创建:" + newFileAllPath);
	// if (!newFile.getParentFile().mkdirs()) {
	// System.out.println("创建目录文件所在的目录失败！");
	// }
	// } else {
	// System.out.println("目标文件所在路径已经存在，不需要创建:" + newFileAllPath);
	// }
	// OperateFile.deleteFileOrDir(newFileAllPath);// 删除以前存在的文件，一般不用执行
	//
	// String oldFileAllPath = rootPath + "/" + fileName;
	// File oldFile = new File(oldFileAllPath);
	// oldFile.renameTo(new File(newFileAllPath));
	// System.out.println("文件搬移成功!文件名：" + fileName);
	// }

	// 因为英协环境没有准备好，导入完成后搬移导出数据，而不是删除 cfg_file_host
	public static void moveRenameFileForYX(String fileName, String saveRootPath) throws Exception {
		String newFileAllPath = saveRootPath + fileName;// 文件路径前面 再加一个 根目录
		File newFile = new File(newFileAllPath);
		if (!newFile.getParentFile().exists()) {
			System.out.println("目标文件所在路径不存在，准备创建:" + newFileAllPath);
			if (!newFile.getParentFile().mkdirs()) {
				System.out.println("创建目录文件所在的目录失败！");
			}
		} else {
			System.out.println("目标文件所在路径已经存在，不需要创建:" + newFileAllPath);
		}
		OperateFile.deleteFileOrDir(newFileAllPath);// 删除以前存在的文件，一般不用执行

		String oldFileAllPath = fileName;
		File oldFile = new File(oldFileAllPath);
		oldFile.renameTo(new File(newFileAllPath));
		System.out.println("文件搬移成功!文件名：" + fileName);
	}

	// 根据文件 全路径，创建文件目录以及文件
	public static boolean creatTxtFile(String destFileName) throws Exception {
		File file = new File(destFileName);
		if (file.exists()) {
			System.out.println("创建单个文件" + destFileName + "失败，目标文件已存在！");
			return false;
		}
		if (destFileName.endsWith(File.separator)) {
			System.out.println("创建单个文件" + destFileName + "失败，目标不能是目录！");
			return false;
		}
		if (!file.getParentFile().exists()) {
			System.out.println("目标文件所在路径不存在，准备创建。。。");
			if (!file.getParentFile().mkdirs()) {
				System.out.println("创建目录文件所在的目录失败！");
				return false;
			}
		}
		if (file.createNewFile()) {
			System.out.println("创建单个文件" + destFileName + "成功！");
			return true;
		} else {
			System.out.println("创建单个文件" + destFileName + "失败！");
			return false;
		}
	}

	// 追加文件信息
	public static void appendStrToFile(String fileName, String newStr) throws Exception {
		// 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
		BufferedWriter bfw = null;
		FileWriter fw = null;
		try {
			fw = new FileWriter(fileName, true);
			bfw = new BufferedWriter(fw);
			bfw.write(newStr);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			bfw.close();
			fw.close();
		}
	}

	// 清除空的文件夹
	public static void deleteEmptyFolder(String path) {
		File folder = new File(path);
		if (folder == null || !folder.exists()) {
			return;
		}
		if (folder.isFile()) {
			return;
		}
		if (folder.list().length == 0) {
			folder.delete();
			return;
		}
		File[] files = folder.listFiles();
		for (File file : files) {
			if (file.exists()) {
				OperateFile.deleteEmptyFolder(file.getAbsolutePath());// 递归删除
			}
		}
	}

	// 级联删除文件夹，以及下面的说有文件及文件夹 s fileName 待删除的文件名 文件删除成功返回true,否则返回false
	public static boolean deleteFileOrDir(String fileName) {
		File file = new File(fileName);
		if (!file.exists()) {
			System.out.println("" + fileName + "文件不存在,不需要删除！");
			return false;
		} else {
			if (file.isFile()) {
				return deleteFile(fileName);
			} else {
				return deleteDirectory(fileName);
			}
		}
	}

	// 删除单个文件
	public static boolean deleteFile(String fileName) {
		File file = new File(fileName);
		if (file.isFile() && file.exists()) {
			file.delete();
			System.out.println("删除单个文件" + fileName + "成功！");
			return true;
		} else {
			System.out.println("删除单个文件" + fileName + "失败！");
			return false;
		}
	}

	// 删除目录（文件夹）以及目录下的文件
	private static boolean deleteDirectory(String dir) {
		// 如果dir不以文件分隔符结尾，自动添加文件分隔符
		if (!dir.endsWith(File.separator)) {
			dir = dir + File.separator;
		}
		File dirFile = new File(dir);

		// 如果dir对应的文件不存在，或者不是一个目录，则退出
		if (!dirFile.exists() || !dirFile.isDirectory()) {
			System.out.println("删除目录失败" + dir + "目录不存在！");
			return false;
		}

		boolean flag = true;
		// 删除文件夹下的所有文件(包括子目录)
		File[] files = dirFile.listFiles();
		for (int i = 0; i < files.length; i++) {
			// 删除子文件
			if (files[i].isFile()) {
				flag = deleteFile(files[i].getAbsolutePath());
				if (!flag) {
					break;
				}
			} else {// 删除子目录
				flag = deleteDirectory(files[i].getAbsolutePath());
				if (!flag) {
					break;
				}
			}
		}
		if (!flag) {
			System.out.println("删除目录失败");
			return false;
		}

		// 删除当前目录
		if (dirFile.delete()) {
			System.out.println("删除目录" + dir + "成功！");
			return true;
		} else {
			System.out.println("删除目录" + dir + "失败！");
			return false;
		}
	}

	// 写TXT
	public static boolean writeTxtFile(String fileName, String newStr) throws Exception {
		// 先读取原有文件内容，然后进行写入操作
		boolean flag = false;
		String filein = newStr;
		String temp = "";

		FileInputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader br = null;

		FileOutputStream fos = null;
		PrintWriter pw = null;
		try {
			// 文件路径
			File file = new File(fileName);
			// 将文件读入输入流
			fis = new FileInputStream(file);
			isr = new InputStreamReader(fis);
			br = new BufferedReader(isr);
			StringBuffer buf = new StringBuffer();

			// 保存该文件原有的内容
			for (int j = 1; (temp = br.readLine()) != null; j++) {
				buf = buf.append(temp);
				// System.getProperty("line.separator")
				// 行与行之间的分隔符 相当于“\n”
				buf = buf.append(System.getProperty("line.separator"));
			}
			buf.append(filein);

			fos = new FileOutputStream(file);
			pw = new PrintWriter(fos);
			pw.write(buf.toString().toCharArray());
			pw.flush();
			flag = true;
		} catch (Exception e) {
			throw e;
		} finally {
			if (pw != null) {
				pw.close();
			}
			if (fos != null) {
				fos.close();
			}
			if (br != null) {
				br.close();
			}
			if (isr != null) {
				isr.close();
			}
			if (fis != null) {
				fis.close();
			}
		}
		return flag;
	}

}
