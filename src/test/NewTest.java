package test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.BreakType;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.junit.Test;

import test.CustomXWPFDocument;
/**
 * 1、创建表格,并修改表格样式
 * 2、插入图片
 * 3、套用模板
 * @author Administrator
 *
 */
public class NewTest {
	/**
	 * 1、测试表格
	 * @throws IOException
	 * @throws InvalidFormatException 
	 */
	@Test
	public void test1() throws IOException, InvalidFormatException{
		XWPFDocument docx = new XWPFDocument();
        XWPFParagraph p = docx.createParagraph();
        
        //操作表格
        XWPFTable table = docx.createTable(2,2);
		//top, left, bottom, right,用于设置边距()
        table.setCellMargins(50, 0, 50,3000);
        //table.setInsideHBorder(XWPFBorderType.NONE, 0, 0, "");//去除单元格间的横线
        int rowSize = table.getRows().size();
        for(int i=0;i<rowSize;i++){
        	XWPFTableRow row =table.getRow(i);
        	int cellSize = row.getTableCells().size();
        	for(int k=0;k<cellSize;k++){
        		//只能通过run的方式来修改cell的样式，
        		//否则在cell中是无法修改颜色的
        		XWPFTableCell cell = row.getCell(k);
        		XWPFRun run = p.createRun();
    			run.setBold(true);
    			run.setText("测试不同样式:i*k="+i*k);
    			run.setColor("DC1E25");
    			cell.setColor("5490AA");
        		cell.setParagraph(p);
        		//加完后删除多余的为了确保正常
        		p.removeRun(0);
        	}
        }
        String picture = "D:\\word\\p1.png";
        if(picture.endsWith(".png")){
        	int format = XWPFDocument.PICTURE_TYPE_PNG;
        	XWPFRun run = p.createRun();
        	run.setText(picture);
        	run.addBreak();
        	run.addPicture(new FileInputStream(picture), format, picture, Units.toEMU(800), Units.toEMU(800));
        	run.addBreak(BreakType.PAGE);
        	p.removeRun(0);
        }
        // TODO以下为找到对应的图片格式"format"
//        //操作图片
//        XWPFRun run = p.createRun();
//        String pictureNames [] = {"D:\\word\\p1.png","D:\\word\\p2.png"};
//        for(String imgFile : pictureNames) {
//            int format;
//            if(imgFile.endsWith(".emf")) format = XWPFDocument.PICTURE_TYPE_EMF;
//            else if(imgFile.endsWith(".wmf")) format = XWPFDocument.PICTURE_TYPE_WMF;
//            else if(imgFile.endsWith(".pict")) format = XWPFDocument.PICTURE_TYPE_PICT;
//            else if(imgFile.endsWith(".jpeg") || imgFile.endsWith(".jpg")) format = XWPFDocument.PICTURE_TYPE_JPEG;
//            else if(imgFile.endsWith(".png")) format = XWPFDocument.PICTURE_TYPE_PNG;
//            else if(imgFile.endsWith(".dib")) format = XWPFDocument.PICTURE_TYPE_DIB;
//            else if(imgFile.endsWith(".gif")) format = XWPFDocument.PICTURE_TYPE_GIF;
//            else if(imgFile.endsWith(".tiff")) format = XWPFDocument.PICTURE_TYPE_TIFF;
//            else if(imgFile.endsWith(".eps")) format = XWPFDocument.PICTURE_TYPE_EPS;
//            else if(imgFile.endsWith(".bmp")) format = XWPFDocument.PICTURE_TYPE_BMP;
//            else if(imgFile.endsWith(".wpg")) format = XWPFDocument.PICTURE_TYPE_WPG;
//            else {
//                System.err.println("Unsupported picture: " + imgFile +
//                        ". Expected emf|wmf|pict|jpeg|png|dib|gif|tiff|eps|bmp|wpg");
//                continue;
//            }
//
//            run.setText(imgFile);
//            run.addBreak();
//            run.addPicture(new FileInputStream(imgFile), format, imgFile, Units.toEMU(200), Units.toEMU(200)); // 200x200 pixels
//            run.addBreak(BreakType.PAGE);
//            p.removeRun(1);
//        }
        //文件不存在时会自动创建  
	    OutputStream os = new FileOutputStream("D:\\word\\table.docx");
	    //写入文件  
	    docx.write(os);
	    this.close(os);
	}
	/**
	 * 2、测试图片的插入
	 */
	@Test
	public void testImg(){
//		CustomXWPFDocument document = new CustomXWPFDocument();
//		try {
//			String picId1 = document.addPictureData(new FileInputStream("D:\\word\\p1.png"), XWPFDocument.PICTURE_TYPE_PNG);
//			document.createPicture(picId1, document.getNextPicNameNumber(XWPFDocument.PICTURE_TYPE_PNG), 200, 150);
//			String picId2 = document.addPictureData(new FileInputStream("D:\\word\\p2.png"), XWPFDocument.PICTURE_TYPE_PNG);
//			document.createPicture(picId2, document.getNextPicNameNumber(XWPFDocument.PICTURE_TYPE_PNG), 200, 150);
//			FileOutputStream fos = new FileOutputStream(new File("D:\\word\\table.docx"));
//			document.write(fos);
//			fos.close();
//		} catch (InvalidFormatException e) {
//			e.printStackTrace();
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}
	/**
	 * 3、测试模板替换
	 * @throws IOException 
	 */
	@Test
	public void templet() throws IOException{
		Map<String, Object> param = new HashMap<String, Object>();  
        param.put("${name}", "huangqiqing");  
        param.put("${zhuanye}", "信息管理与信息系统");  
        param.put("${sex}", "男");  
        param.put("${school_name}", "山东财经大学");  
        param.put("${date}", new Date().toString());  
          
        Map<String,Object> header = new HashMap<String, Object>();  
        header.put("width", 100);  
        header.put("height", 150);  
        header.put("type", "jpg");  
        header.put("content", WordUtil.inputStream2ByteArray(new FileInputStream("D:\\word\\p1.jpg"), true));  
        param.put("${header}",header);  
          
//        Map<String,Object> twocode = new HashMap<String, Object>();  
//        twocode.put("width", 100);  
//        twocode.put("height", 100);  
//        twocode.put("type", "png");  
//        twocode.put("content", ZxingEncoderHandler.getTwoCodeByteArray("测试二维码,huangqiqing", 100,100));  
//        param.put("${twocode}",twocode);  
        CustomXWPFDocument doc = WordUtil.generateWord(param, "D:\\word\\template.docx");  
        FileOutputStream fopts = new FileOutputStream("D:\\word\\template2.docx");  
        doc.write(fopts);  
        fopts.close();  
	}
	private void close(OutputStream os) {
		if(os!=null){
			try {
				os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
