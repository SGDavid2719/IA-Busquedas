package ia.agafam;

// Simplement per mirar si un número és parell o senar (comprobant el bit menys significatiu)
// operador "&" (and bit a bit)

public class Util {
    public static boolean isEven(int number) {
        return (number & 1) == 0;
    }
    public static boolean isOdd(int number) {
        return (number & 1) == 1;
    }
}
