import java.util.ArrayList;
import java.util.List;


/**
 * A spectacularly slow fibonacci calculator.  
 * @author andrew
 *
 */
public class Fibonacci {
    public static long fib(int n) {
        if (n <= 1) return n;
        else return fib(n-1) + fib(n-2);
    }

    // compute the first K fibonacci numbers
    public static List<Long> fibSeries (int k) {
       List<Long> list = new ArrayList<Long>();
        for (int i = 1; i <= k; i++)
            list.add(fib(i));
        
        return list;
    }

}


