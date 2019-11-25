package vn.elite.fundamental.java.reflect;

import lombok.val;

import java.lang.ref.*;

public class ReferenceExample {
    private final String name;

    private ReferenceExample(String name) {
        this.name = name;
    }

    private static ReferenceExample getReference(String name) {
        return new ReferenceExample(name);
    }

    public static void main(String[] args) {
        val queue = new ReferenceQueue<>();
        val soft = new SoftReference<>(getReference(SoftReference.class.getName()));
        val weak = new WeakReference<>(getReference(WeakReference.class.getName()));
        val phantom = new PhantomReference<>(getReference(PhantomReference.class.getName()), queue);

        System.out.println(soft.getClass());

        try {
            boolean allRemoved;
            int counter = 0;
            do {
                counter++;
                System.gc();
                System.out.format("Loop number %d%n", counter);

                boolean softDead = checkReference(soft);
                boolean weakDead = checkReference(weak);
                boolean phantomDead = checkReference(phantom);
                allRemoved = softDead && weakDead && phantomDead;
                System.out.format("All removed -> %s%n", allRemoved);
            } while (!allRemoved && counter < 100);
            queue.remove();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static boolean checkReference(Reference<ReferenceExample> r) {
        val value = r.get();
        if (value != null) {
            System.out.format("Reference is %s%n", value);
            return false;
        } else {
            System.out.format("%s deleted!%n", r.getClass());
            return true;
        }
    }

    @Override
    public String toString() {
        return String.format("%s [status=active]", name);
    }
}
