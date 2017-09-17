package com.finalyearproject.ecc.core;

import java.math.BigInteger;

public class EllipticCurve {
    
    private BigInteger a;
    private BigInteger b;
    private BigInteger p;
    
    private ECPoint g = null;
    
    // some BigInteger constants that might help us in some calculations
    private static BigInteger THREE = new BigInteger("3");
    
    public EllipticCurve(BigInteger a, BigInteger b, BigInteger p) {
        this.a = a;
        this.b = b;
        this.p = p;
    }
    
    public EllipticCurve(BigInteger a, BigInteger b, BigInteger p, ECPoint g) {
        this.a = a;
        this.b = b;
        this.p = p;
        this.g = g;
    }
    
    public EllipticCurve(long a, long b, long p) {
        this.a = BigInteger.valueOf(a);
        this.b = BigInteger.valueOf(b);
        this.p = BigInteger.valueOf(p);
    }

    public EllipticCurve(long a, long b, long p, ECPoint g) {
        this.a = BigInteger.valueOf(a);
        this.b = BigInteger.valueOf(b);
        this.p = BigInteger.valueOf(p);
        this.g = g;
    }
    
    public ECPoint getBasePoint() {
        return g;
    }
    
    public void setBasePoint(ECPoint g) {
        this.g = g;
    }
    
    public BigInteger getA() {
        return a;
    }
    
    public BigInteger getB() {
        return b;
    }
    
    public BigInteger getP() {
        return p;
    }
        
    public static final EllipticCurve NIST_P_192 = new EllipticCurve(
            new BigInteger("-3"),
            new BigInteger("64210519e59c80e70fa7e9ab72243049feb8deecc146b9b1", 16),
            new BigInteger("6277101735386680763835789423207666416083908700390324961279"),
            new ECPoint(
                    new BigInteger("188da80eb03090f67cbf20eb43a18800f4ff0afd82ff1012", 16),
                    new BigInteger("07192b95ffc8da78631011ed6b24cdd573f977a11e794811", 16)
            )
    );

    public boolean isPointInsideCurve(ECPoint point) {
        if (point.isPointOfInfinity()) return true;
        
        return point.x.multiply(point.x).mod(p).add(a).multiply(point.x).add(b)
                .mod(p).subtract(point.y.multiply(point.y)).mod(p)
                .compareTo(BigInteger.ZERO) == 0;
    }
    
    public ECPoint add(ECPoint p1, ECPoint p2) {
        if (p1 == null || p2 == null) return null;
        
        if (p1.isPointOfInfinity()) {
            return new ECPoint(p2);
        } else if (p2.isPointOfInfinity()) {
            return new ECPoint(p1);
        }
        
        BigInteger lambda;
        if (p1.x.subtract(p2.x).mod(p).compareTo(BigInteger.ZERO) == 0) {
            if (p1.y.subtract(p2.y).mod(p).compareTo(BigInteger.ZERO) == 0) {
                // lambda = (3x1^2 + a) / (2y1)
                BigInteger nom = p1.x.multiply(p1.x).multiply(THREE).add(a);
                BigInteger den = p1.y.add(p1.y);
                lambda = nom.multiply(den.modInverse(p));
            } else {
                // lambda = infinity
                return ECPoint.INFINTIY;
            }
        } else {
            // lambda = (y2 - y1) / (x2 - x1)
            BigInteger nom = p2.y.subtract(p1.y);
            BigInteger den = p2.x.subtract(p1.x);
            lambda = nom.multiply(den.modInverse(p));
        }
        
        // The result is (lambda^2 - x1 - y1, lambda(x2 - xr) - yp)
        BigInteger xr = lambda.multiply(lambda).subtract(p1.x).subtract(p2.x).mod(p);
        BigInteger yr = lambda.multiply(p1.x.subtract(xr)).subtract(p1.y).mod(p);
        return new ECPoint(xr, yr);
    }
    
    public ECPoint subtract(ECPoint p1, ECPoint p2) {
        if (p1 == null || p2 == null) return null;
        
        return add(p1, p2.negate());
    }

    public ECPoint multiply(ECPoint p1, BigInteger n) {
        if (p1.isPointOfInfinity()) {
            return ECPoint.INFINTIY;
        }
        
        ECPoint result = ECPoint.INFINTIY;
        int bitLength = n.bitLength();
        for (int i = bitLength - 1; i >= 0; --i) {
            result = add(result, result);
            if (n.testBit(i)) {
                result = add(result, p1);
            }
        }
        
        return result;
    }
    
    public ECPoint multiply(ECPoint p1, long n) {
        return multiply(p1, BigInteger.valueOf(n));
    }

    public BigInteger calculateRhs(BigInteger x) {
        return x.multiply(x).mod(p).add(a).multiply(x).add(b).mod(p);
    }
    
    public static void main(String[] args) {
        // Check whether the standard curves' base points lie on the curve
        System.out.println("NIST_P_192: " + EllipticCurve.NIST_P_192.isPointInsideCurve(EllipticCurve.NIST_P_192.getBasePoint()));
    }

}
