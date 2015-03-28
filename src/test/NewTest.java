package test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.BreakType;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.junit.Test;

public class NewTest {
	/**
	 * 创建表格,并修改表格样式
	 * @throws IOException
	 */
	@Test
	public void test1() throws IOException{
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
        
        //操作图片
        XWPFRun run = p.createRun();
        String pictureName = "";
        for(String imgFile : args) {
            int format;
            if(imgFile.endsWith(".emf")) format = XWPFDocument.PICTURE_TYPE_EMF;
            else if(imgFile.endsWith(".wmf")) format = XWPFDocument.PICTURE_TYPE_WMF;
            else if(imgFile.endsWith(".pict")) format = XWPFDocument.PICTURE_TYPE_PICT;
            else if(imgFile.endsWith(".jpeg") || imgFile.endsWith(".jpg")) format = XWPFDocument.PICTURE_TYPE_JPEG;
            else if(imgFile.endsWith(".png")) format = XWPFDocument.PICTURE_TYPE_PNG;
            else if(imgFile.endsWith(".dib")) format = XWPFDocument.PICTURE_TYPE_DIB;
            else if(imgFile.endsWith(".gif")) format = XWPFDocument.PICTURE_TYPE_GIF;
            else if(imgFile.endsWith(".tiff")) format = XWPFDocument.PICTURE_TYPE_TIFF;
            else if(imgFile.endsWith(".eps")) format = XWPFDocument.PICTURE_TYPE_EPS;
            else if(imgFile.endsWith(".bmp")) format = XWPFDocument.PICTURE_TYPE_BMP;
            else if(imgFile.endsWith(".wpg")) format = XWPFDocument.PICTURE_TYPE_WPG;
            else {
                System.err.println("Unsupported picture: " + imgFile +
                        ". Expected emf|wmf|pict|jpeg|png|dib|gif|tiff|eps|bmp|wpg");
                continue;
            }

            run.setText(imgFile);
            run.addBreak();
            run.addPicture(new FileInputStream(imgFile), format, imgFile, Units.toEMU(200), Units.toEMU(200)); // 200x200 pixels
            run.addBreak(BreakType.PAGE);
        }
        //文件不存在时会自动创建  
	    OutputStream os = new FileOutputStream("D:\\word\\table.docx");
	    //写入文件  
	    docx.write(os);
	    this.close(os);
	}
	private void juage(){
		
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
