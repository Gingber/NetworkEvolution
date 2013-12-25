/**
 * 
 */
package util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * @author Gingber
 *
 */
public class Matrix {
	
	private final static int row = 1000;		//矩阵行数
	private final static int column = 1000; 	//矩阵列数
	private static int[][] matrix = new int[row][column];
	private final static String matrixWriter = "file/matrix.dat";
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static String createMatrix(String filename) throws IOException {
		// TODO Auto-generated method stub
 
		List<String> list = TxtReader.loadVectorFromFile(new File(filename), "UTF-8");
		for (int i = 0; i < list.size(); i++) {
			String[] doublevetrix = list.get(i).split(",");
			int r = Integer.parseInt(doublevetrix[0]);
			int c = Integer.parseInt(doublevetrix[1]);
			matrix[r][c] = 1;
		}
		
		StringBuilder sb = new StringBuilder();
		for(int i =0; i < row; i++) {
            String sc ="";
            for (int j=0; j < column; j++){
                    sb.append(matrix[i][j]);
                    sb.append("\t");
            }
            sb.append("\n");
        }
		
		TxtWriter.saveToFile(sb.toString(), new File(matrixWriter), "UTF-8");
		
		return matrixWriter;
	}

}
