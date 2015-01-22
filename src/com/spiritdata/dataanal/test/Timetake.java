package com.spiritdata.dataanal.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

import org.junit.Test;
public class Timetake {
/**
* 获取文件创建时间
* @param _file _file 要获取创建时间的文件对象
* @return datetime datetime 创建时间
*/
	public static String getFileCreateDate(File _file) {
		File file = _file;
		try {
			Process ls_proc = Runtime.getRuntime().exec(
			"cmd.exe /c dir " + file.getAbsolutePath() + " /tc");
			BufferedReader br = new BufferedReader(new InputStreamReader(ls_proc.getInputStream()));
			for (int i = 0; i < 5; i++) {
				br.readLine();
			}
			String stuff = br.readLine();
			StringTokenizer st = new StringTokenizer(stuff);
			String dateC = st.nextToken();
			String time = st.nextToken();
			String datetime = dateC.concat(time);
			br.close();
			return datetime;
		} catch (Exception e) {
			return null;
		}
	}
	/**
	 * 这个方法支持jpg、doct、zip、
	 */
	@Test
	public void test(){
		//文件类型login问题记录.txt，LOGO-050113.jpg
		File file = new File("C:\\Users\\Administrator\\Desktop\\文档\\logo备份.rar");
		String time = Timetake.getFileCreateDate(file);
		System.out.println(time);
	}
	
}
