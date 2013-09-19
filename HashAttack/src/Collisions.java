import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.security.*;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Random;


public class Collisions {
	
	static final int NUM_TEST = 100;
	static final int MIN_BIT_LENGTH = 8;
	static final int MAX_BIT_LENGTH = 64;
	static final long MAX_TIME = 220000L;
	

	public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
		BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt"));
		Random rand = new Random();// Assume ciphertext is digested
		writer.write("Completely random");// Type of test.
		writer.newLine();
		writer.newLine();
		
		for(int i = 32, z = MIN_BIT_LENGTH; z < MAX_BIT_LENGTH + 1 ; z++) {
			System.out.println(i + "-bit digest");
			writer.write(i + "-bit digest");
			writer.newLine();
			
			// Collision
			writer.write("Collision attack: ");
			long start = System.nanoTime();
			for(int j = 0; j < NUM_TEST; j++) {
				HashSet<BitSet> map = new HashSet<BitSet>();
				byte[] bytes = new byte[i];
				do { rand.nextBytes(bytes); }
				while(map.add(encrypt(new String(bytes), i)));
			}
			long duration = System.nanoTime() - start;
			writer.write((duration / NUM_TEST) + " ns");
			writer.newLine();
			System.out.println("Duration: " + (duration / NUM_TEST) + " ns");
			
			// Pre-image
			writer.write("Pre-image attack: ");
			start = System.nanoTime();
			for(int j = 0; j < NUM_TEST; j++) {
				byte[] bytes = new byte[i];
				BitSet fixed = new BitSet(i);
				rand.nextBytes(bytes);
				fixed = encrypt(new String(bytes), 8);
				do { rand.nextBytes(bytes); }
				while(fixed == encrypt(new String(bytes), i));
			}
			duration = System.nanoTime() - start;
			writer.write((duration / NUM_TEST) + " ns");
			writer.newLine();
			writer.newLine();
			System.out.println("Duration: " + (duration / NUM_TEST) + " ns");
			
			if((duration / NUM_TEST) > MAX_TIME) break;// That's long enough
		}
		
		writer.close();
	}

	public static BitSet encrypt(String s, int length) throws NoSuchAlgorithmException {
		MessageDigest d = MessageDigest.getInstance("SHA-1");
		d.reset();
		d.update(s.getBytes());
		BitSet bits = new BitSet(length);
		byte[] bytes = d.digest();
		for(int i = 0; i * 8 < length; i++) {
			for(int j = 0x8; j > 0; j = j >> 1) {
				int index = (i + 1) * 8 - (int)(Math.log(j) / Math.log(2));
				if(index == length) break;
				bits.set(index, (bytes[i] & j) > 0);
			}
		}
		return bits;
	}
}
