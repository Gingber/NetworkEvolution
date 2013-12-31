/**
 * 
 */
package main;

/**
 * @author Gingber
 *
 */
import java.security.SecureRandom;
import java.util.Random;
/**
 * aiguoxin 2010-10-20下午01:35:13
 * 
 * 随机数产生总结
 */
public class Radom {
	public static void main(String[] args) {
		// Math.random()方法
		for (int i = 0; i < 10; i++) {
			System.out.print((int)(Math.random()*100) + " ");
		}		
		System.out.println("\n-----------------------------");
		// 不带种子，每次的随机数不同
		Random rand = new Random();
		for (int i = 0; i < 10; i++) {
			System.out.print(rand.nextInt(10) + " ");
		}
		System.out.println("\n-----------------------------");
		// 带种子，每次的随机数相同
		Random rand2 = new Random(10);
		for (int i = 0; i < 10; i++) {
			System.out.print(rand2.nextInt(10) + " ");
		}
		System.out.println("\n-----------------------------");
		// 生成完全不重复的随机数
		SecureRandom random = new SecureRandom();
		for (int i = 0; i < 10; i++) {
			System.out.print(random.nextInt(10) + " ");
		}
		
		
		System.out.println("\n-----------------------------");
		Random rand1 = new Random();
		int degree = 10;
		int maxdegree = (int)(1.5*degree);
		int mindegree = (int)(0.5*degree);
		int interval = maxdegree - mindegree;
		for (int i = 0; i < 10; i++) {
	        System.out.print(rand1.nextInt(interval+1) + mindegree + "\t");
		}	
	}
}