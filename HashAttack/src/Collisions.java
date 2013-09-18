import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.security.*;
import java.util.HashSet;
import java.util.Random;


public class Collisions {
	
	static final int NUM_TEST = 10;
	static final long MAX_TIME = 10000000000L;
	

	public static void main(String[] args) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt"));
		Random rand = new Random();
		writer.write("Completely random");// Type of test.
		writer.newLine();
		
		for(int i = 1; ; i++) {
			writer.write(i * 8 + "-bit digest");
			writer.newLine();
			
			// Collision
			writer.write("Collision attack: ");
			long start = System.nanoTime();
			for(int j = 0; j < NUM_TEST; j++) {
				HashSet<byte[]> map = new HashSet<byte[]>();
				byte[] bytes = new byte[i];
				do { rand.nextBytes(bytes); }
				while(map.add(bytes));
			}
			long duration = System.nanoTime() - start;
			writer.write((duration / NUM_TEST) + " nanoseconds");
			writer.newLine();
			
			// Pre-image
			writer.write("Pre-image attack: ");
			start = System.nanoTime();
			for(int j = 0; j < NUM_TEST; j++) {
				byte[] bytes = new byte[i], fixed = new byte[i];
				rand.nextBytes(fixed);
				do { rand.nextBytes(bytes); }
				while(fixed != bytes);
			}
			duration = System.nanoTime() - start;
			writer.write((duration / NUM_TEST) + " nanoseconds");
			writer.newLine();
			writer.newLine();
			
			if(duration > MAX_TIME) break;// That's long enough
		}
		
		writer.close();
	}

	public static byte[] encrypt(String s) throws NoSuchAlgorithmException {
		MessageDigest d = MessageDigest.getInstance("SHA-1");
		d.reset();
		d.update(s.getBytes());
		return d.digest();
	}
}
