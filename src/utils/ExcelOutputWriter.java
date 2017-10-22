package utils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import model.Entity;
import model.Site;

public class ExcelOutputWriter {

	public static boolean ExcelOutputWriter(List<Entity> entities,String user) {
		if(entities != null && entities.size() != 0) {
			try {
//				File file = new File("Signals.xlsx");
//				if(file.exists()) {
//					file.delete();
//					file.createNewFile();
//				} else {
//					file.createNewFile();
//				}
				XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream("Signals.xlsx"));
				XSSFSheet sheet = workbook.createSheet("User"+user);
		        String lastNameInserted = "";
		        int rowCount = 0;
		         
		        for (Entity e: entities) {
		            Row row = sheet.createRow(++rowCount);
		             
		            int columnCount = 0;
		            
		            Cell cell_Name = row.createCell(++columnCount);
		            cell_Name.setCellValue(e.getName());
		            Cell cell_Date = row.createCell(++columnCount);
		            cell_Date.setCellValue(e.getDate());
		            Cell cell_TFIDF = row.createCell(++columnCount);
		            cell_TFIDF.setCellValue(e.getTf_idf());
		            
		            
//		            for (Object field : aBook) {
//		                Cell cell = row.createCell(++columnCount);
//		                if (field instanceof String) {
//		                    cell.setCellValue((String) field);
//		                } else if (field instanceof Integer) {
//		                    cell.setCellValue((Integer) field);
//		                }
//		            }
		             
		        }
		        try (FileOutputStream outputStream = new FileOutputStream("Signals.xlsx")) {
		            workbook.write(outputStream);
		        }catch(IOException ex) {
					System.out.println("Error in ExcelOutputWriter");
					System.out.println(ex.toString());
					return false;
				}		
				return true;
			
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				return false;
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				return false;
			}
			finally {
				return true;
			}
			
	
	}
		return false;
	}
	
	
	public static boolean WriteRecommendation(List<Site> urls,String thisUser, String userToCompare) {
		if(urls != null && urls.size() > 0) {
			try {
				XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream("Signals.xlsx"));
				XSSFSheet sheet = workbook.createSheet("REC-"+thisUser+"-"+userToCompare);
		        int rowCount = 0;
		        
		        for (Site u: urls) {
		            Row row = sheet.createRow(++rowCount);
		             
		            int columnCount = 0;
		            
		            Cell cell_Name = row.createCell(++columnCount);
		            cell_Name.setCellValue(u.getUrl());     
		        }
		        try (FileOutputStream outputStream = new FileOutputStream("Signals.xlsx")) {
		            workbook.write(outputStream);
		        }catch(IOException ex) {
					System.out.println("Error in ExcelOutputWriter");
					System.out.println(ex.toString());
					return false;
				}		
				return true;
			
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				return false;
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				return false;
			}
			finally {
				return true;
			}
			
	
	}
		return false;
	}
	
}
