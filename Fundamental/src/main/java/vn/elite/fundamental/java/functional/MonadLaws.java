package vn.elite.fundamental.java.functional;

import java.util.Optional;
import java.util.function.Function;

class MonadLaws {
    // Input values for the monad law tests below
    private static int value = 42;
    private static Optional<Integer> monadicValue = Optional.of(value);
    private static Function<Integer, Optional<Integer>> optionalOf = Optional::of;
    private static Function<Integer, Optional<Integer>> f = n -> Optional.of(n * 2);
    private static Function<Integer, Optional<Integer>> g = n -> Optional.of(n * 5);
    private static Function<Integer, Optional<Integer>> f_flatMap_g = n -> f.apply(n).flatMap(g);

    public static void main(String[] args) {
        boolean law1 = satisfiesLaw1LeftIdentity();
        boolean law2 = satisfiesLaw2RightIdentity();
        boolean law3 = satisfiesLaw3Associativity();

        System.out.println("Does JDK8's Optional class satisfy the Monad laws?");
        System.out.println("=================================================");
        System.out.println("  1.  Left identity:  " + law1);
        System.out.println("  2.  Right identity: " + law2);
        System.out.println("  3.  Associativity:  " + law3);
        System.out.println();
        System.out.println(law1 && law2 && law3
            ? "Yes, it does."
            : "No, it doesn't.");
    }

    /**
     * Monad law 1, Left Identity From LYAHFGG [1] above: The first monad law states that if we take a value, put it in
     * a default context with return and then feed it to a function by using >>=, it’s the same as just taking the value
     * and applying the function to it
     */
    private static boolean satisfiesLaw1LeftIdentity() {
        return Optional.of(value).flatMap(f).equals(f.apply(value));
    }

    /**
     * Monad law 2, Right Identity
     * <p>
     * From LYAHFGG [1] above: The second law states that if we have a monadic value and we use >>= to feed it to
     * return, the result is our original monadic value.
     */
    private static boolean satisfiesLaw2RightIdentity() {
        return monadicValue.flatMap(optionalOf).equals(monadicValue);
    }

    /**
     * Monad law 3, Associativity
     * <p>
     * From LYAHFGG [1] above: The final monad law says that when we have a chain of monadic function applications with
     * >>=, it shouldn’t matter how they’re nested.
     */
    private static boolean satisfiesLaw3Associativity() {
        return monadicValue.flatMap(f).flatMap(g).equals(monadicValue.flatMap(f_flatMap_g));
    }
}
