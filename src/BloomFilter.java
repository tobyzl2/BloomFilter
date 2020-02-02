import java.util.BitSet;

public class BloomFilter {
    private BitSet data;
    private int m;
    private int k;

    public BloomFilter(int n, double fpr) {
        m = (int) ((-n * Math.log(fpr)) / Math.pow(Math.log(fpr), 2));
        k = (int) ((m / n) * Math.log(2));
        data = new BitSet(m);
    }
}
