package jpatterns.singleton;

/**
 * NaiveSingleton — thread-safe Singleton via Double-Checked Locking (DCL).
 *
 * WHY volatile?
 * Without volatile, the JVM and CPU are free to reorder the instructions that
 * make up `new NaiveSingleton()`. Object creation is not atomic; it involves
 * three steps:
 *   1. Allocate memory for the object.
 *   2. Call the constructor to initialise fields.
 *   3. Assign the reference to `instance`.
 * The JVM may reorder steps 2 and 3, so another thread could read a non-null
 * `instance` that points to a partially-constructed object and use it before
 * the constructor has finished.
 *
 * `volatile` prevents this reordering. It establishes a happens-before
 * relationship: the write to `instance` (step 3) is guaranteed to be visible
 * to any thread that subsequently reads it, and the constructor (step 2) is
 * guaranteed to complete before that write.
 *
 * WHY not AtomicReference?
 * AtomicReference wraps a volatile field and adds CAS (compare-and-set) on top.
 * CAS is useful for lock-free patterns where you never want to block. DCL
 * deliberately takes a lock during initialisation, so the CAS machinery would
 * add overhead without providing any benefit.
 *
 * HOW the two null checks work:
 *   1st check (outside synchronized): the common case — instance already exists,
 *     return it immediately without ever acquiring the lock.
 *   2nd check (inside synchronized): guards against two threads both passing the
 *     1st check before either has initialised the instance. Without it, both
 *     would create a new object and one would overwrite the other.
 *
 * Ref: Goetz et al., "Java Concurrency in Practice", §16.2
 */
public class NaiveSingleton {

    // volatile ensures the fully-constructed object is visible to all threads
    // before the reference is published (see class-level Javadoc).
    private static volatile NaiveSingleton instance;

    // Private constructor prevents instantiation from outside this class.
    private NaiveSingleton() { }

    /**
     * Returns the single instance of NaiveSingleton, creating it on the first
     * call. Thread-safe via Double-Checked Locking.
     */
    public static NaiveSingleton getInstance() {

        // 1st check — no lock acquired; fast path for every call after init.
        if (instance != null) {
            return instance;
        }

        synchronized (NaiveSingleton.class) {
            // 2nd check — we now hold the lock, but another thread may have
            // initialised the instance between our 1st check and here.
            if (instance == null) {
                instance = new NaiveSingleton();
            }
            return instance;
        }

    }

    /**
     * Returns a greeting string.
     */
    public String greet(String name) {
        return "Hello, " + name + "! Welcome to Design Patterns in Java.";
    }

    public static void main(String[] args) {
        System.out.println(NaiveSingleton.getInstance().greet("World"));
    }

}
