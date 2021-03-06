package com.finalyearproject.ecc.core;

import java.math.BigInteger;
import java.util.Random;

public class ECC {
	public static final long AUXILIARY_CONSTANT_LONG = 1000;
	public static final BigInteger AUXILIARY_CONSTANT = BigInteger.valueOf(AUXILIARY_CONSTANT_LONG);

	private static long executionTime = -1;
	private static long startExecutionTime;

	public static byte[] encrypt(byte[] plainText, PublicKey key) throws Exception {
		initializeExecutionTime();

		EllipticCurve c = key.getCurve();
		ECPoint g = c.getBasePoint();
		ECPoint publicKey = key.getKey();
		BigInteger p = c.getP();
		int numBits = p.bitLength();
		int blockSize = getBlockSize(c);
		int cipherTextBlockSize = getCipherTextBlockSize(c);

		byte[] padded = pad(plainText, blockSize);

		byte[][] block = new byte[padded.length / blockSize][blockSize];
		for (int i = 0; i < block.length; ++i) {
			for (int j = 0; j < blockSize; ++j) {
				block[i][j] = padded[i * blockSize + j];
			}
		}

		ECPoint[] encoded = new ECPoint[block.length];
		for (int i = 0; i < encoded.length; ++i) {
			encoded[i] = encode(block[i], c);
		}

		// [C_1, C_2] = [kG, P_m + kP_G], where:
		ECPoint[][] encrypted = new ECPoint[block.length][2];
		Random rnd = new Random(System.currentTimeMillis());
		for (int i = 0; i < encrypted.length; ++i) {
			BigInteger k;
			do {
				k = new BigInteger(numBits, rnd);
			} while (k.mod(p).compareTo(BigInteger.ZERO) == 0);
			encrypted[i][0] = c.multiply(g, k);
			encrypted[i][1] = c.add(encoded[i], c.multiply(publicKey, k));
		}

		byte[] cipherText = new byte[encrypted.length * cipherTextBlockSize * 4];
		for (int i = 0; i < encrypted.length; ++i) {
			byte[] cipher = encrypted[i][0].x.toByteArray();
			int offset = i * cipherTextBlockSize * 4 + cipherTextBlockSize * 0 + (cipherTextBlockSize - cipher.length);
			for (int j = 0; j < cipher.length; ++j) {
				cipherText[j + offset] = cipher[j];
			}
			cipher = encrypted[i][0].y.toByteArray();
			offset = i * cipherTextBlockSize * 4 + cipherTextBlockSize * 1 + (cipherTextBlockSize - cipher.length);
			for (int j = 0; j < cipher.length; ++j) {
				cipherText[j + offset] = cipher[j];
			}
			cipher = encrypted[i][1].x.toByteArray();
			offset = i * cipherTextBlockSize * 4 + cipherTextBlockSize * 2 + (cipherTextBlockSize - cipher.length);
			for (int j = 0; j < cipher.length; ++j) {
				cipherText[j + offset] = cipher[j];
			}
			cipher = encrypted[i][1].y.toByteArray();
			offset = i * cipherTextBlockSize * 4 + cipherTextBlockSize * 3 + (cipherTextBlockSize - cipher.length);
			for (int j = 0; j < cipher.length; ++j) {
				cipherText[j + offset] = cipher[j];
			}
		}

		finalizeExecutionTime();

		return cipherText;
	}

	public static byte[] decrypt(byte[] cipherText, PrivateKey key) throws Exception {
		initializeExecutionTime();

		EllipticCurve c = key.getCurve();
		ECPoint g = c.getBasePoint();
		BigInteger privateKey = key.getKey();
		BigInteger p = c.getP();
		int numBits = p.bitLength();
		int blockSize = getBlockSize(c);
		int cipherTextBlockSize = getCipherTextBlockSize(c);

		if (cipherText.length % cipherTextBlockSize != 0 || (cipherText.length / cipherTextBlockSize) % 4 != 0) {
			throw new Exception("The length of the cipher text is not valid");
		}
		byte block[][] = new byte[cipherText.length / cipherTextBlockSize][cipherTextBlockSize];
		for (int i = 0; i < block.length; ++i) {
			for (int j = 0; j < cipherTextBlockSize; ++j) {
				block[i][j] = cipherText[i * cipherTextBlockSize + j];
			}
		}

		// P_m = C_2 - kC_1, where:
		// [C_1, C_2] is the ciphertext,
		// k is the private key.
		ECPoint encoded[] = new ECPoint[block.length / 4];
		for (int i = 0; i < block.length; i += 4) {
			ECPoint c1 = new ECPoint(new BigInteger(block[i]), new BigInteger(block[i + 1]));
			ECPoint c2 = new ECPoint(new BigInteger(block[i + 2]), new BigInteger(block[i + 3]));
			encoded[i / 4] = c.subtract(c2, c.multiply(c1, privateKey));
		}

		byte plainText[] = new byte[encoded.length * blockSize];
		for (int i = 0; i < encoded.length; ++i) {
			byte decoded[] = decode(encoded[i], c);
			for (int j = Math.max(blockSize - decoded.length, 0); j < blockSize; ++j) {
				plainText[i * blockSize + j] = decoded[j + decoded.length - blockSize];
			}
		}
		plainText = unpad(plainText, blockSize);

		finalizeExecutionTime();
		return plainText;
	}

	public static KeyPair generateKeyPair(EllipticCurve c, Random rnd) throws Exception {
		initializeExecutionTime();

		BigInteger p = c.getP();
		BigInteger privateKey;
		do {
			privateKey = new BigInteger(p.bitLength(), rnd);
		} while (privateKey.mod(p).compareTo(BigInteger.ZERO) == 0);

		// Calculate the public key, k * g.
		ECPoint g = c.getBasePoint();
		
		ECPoint publicKey = c.multiply(g, privateKey);

		KeyPair result = new KeyPair(new PublicKey(c, publicKey), new PrivateKey(c, privateKey));

		finalizeExecutionTime();
		return result;
	}

	
	public static KeyPair generateKeyPair2(EllipticCurve c, BigInteger b1, BigInteger b2) throws Exception {
		initializeExecutionTime();

		BigInteger p = c.getP();
		BigInteger privateKey;
		do {
			privateKey = b1;
		} while (privateKey.mod(p).compareTo(BigInteger.ZERO) == 0);

		ECPoint g = c.getBasePoint();
		ECPoint publicKey = c.multiply(g, privateKey);

		KeyPair result = new KeyPair(new PublicKey(c, publicKey), new PrivateKey(c, privateKey));

		finalizeExecutionTime();
		return result;
	}

	public static long getLastExecutionTime() {
		return executionTime;
	}

	private static ECPoint encode(byte[] block, EllipticCurve c) throws Exception {
		byte[] paddedBlock = new byte[block.length + 2];
		for (int i = 0; i < block.length; ++i) {
			paddedBlock[i + 2] = block[i];
		}
		return koblitzProbabilistic(c, new BigInteger(paddedBlock));
	}

	private static byte[] decode(ECPoint point, EllipticCurve c) {
		return point.x.divide(AUXILIARY_CONSTANT).toByteArray();
	}

	private static int getBlockSize(EllipticCurve c) {
		return Math.max(c.getP().bitLength() / 8 - 5, 1);
	}

	private static int getCipherTextBlockSize(EllipticCurve c) {
		return c.getP().bitLength() / 8 + 5;
	}

	private static byte[] pad(byte[] b, int blockSize) {
		int paddedLength = blockSize - (b.length % blockSize);
		byte[] padded = new byte[b.length + paddedLength];
		for (int i = 0; i < b.length; ++i) {
			padded[i] = b[i];
		}
		for (int i = 0; i < paddedLength - 1; ++i) {
			padded[b.length + i] = 0;
		}
		padded[padded.length - 1] = (byte) paddedLength;

		return padded;
	}

	private static byte[] unpad(byte[] b, int blockSize) {
		int paddedLength = b[b.length - 1];
		byte[] unpadded = new byte[b.length - paddedLength];
		for (int i = 0; i < unpadded.length; ++i) {
			unpadded[i] = b[i];
		}
		return unpadded;
	}

	private static ECPoint koblitzProbabilistic(EllipticCurve c, BigInteger x) throws Exception {
		BigInteger p = c.getP();

		// throw an exception if p != 3 (mod 4)
		if (!p.testBit(0) || !p.testBit(1)) {
			throw new Exception("P should be 3 (mod 4)");
		}
		BigInteger pMinusOnePerTwo = p.subtract(BigInteger.ONE).shiftRight(1);

		BigInteger tempX = x.multiply(AUXILIARY_CONSTANT).mod(p);
		for (long k = 0; k < AUXILIARY_CONSTANT_LONG; ++k) {
			BigInteger newX = tempX.add(BigInteger.valueOf(k));

			// Calculates the rhs of the elliptic curve equation, call it a
			BigInteger a = c.calculateRhs(newX);

			// Determine whether this value is a quadratic residue modulo p
			// It is if and only if a ^ ((p - 1) / 2) = 1 (mod p)
			if (a.modPow(pMinusOnePerTwo, p).compareTo(BigInteger.ONE) == 0) {
				// We found it! Now, the solution is y = a ^ ((p + 1) / 4)
				BigInteger y = a.modPow(p.add(BigInteger.ONE).shiftRight(2), p);
				return new ECPoint(newX.mod(p), y);
			}
		}

		// If we reach this point, then no point are found within the limit.
		throw new Exception("No point found within the auxiliary constant");
	}

	private static void initializeExecutionTime() {
		startExecutionTime = System.currentTimeMillis();
	}

	private static void finalizeExecutionTime() {
		executionTime = System.currentTimeMillis() - startExecutionTime;
	}

	public static void main(String arg[]) throws Exception {
		EllipticCurve ecc = EllipticCurve.NIST_P_192;
		KeyPair keys = generateKeyPair(ecc, new Random(System.currentTimeMillis()));
		String pt = "Anand Kumar";
		System.out.println("Plain text .. " + pt);
		keys.getPublicKey().saveToFile("file.txt");
		PublicKey pk = new PublicKey("file.txt");
		byte[] ct = encrypt(pt.getBytes(), pk);
		String s1 = new String(ct);
		System.out.println("Cipher text .. " + s1);
		byte[] decrypted = decrypt(ct, keys.getPrivateKey());
		String s2 = new String(decrypted);
		System.out.println("Decrypted text .. " + s2);
	}
}
