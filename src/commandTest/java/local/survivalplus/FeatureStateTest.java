package local.survivalplus;

/** Exhaustive command-state checks without launching Minecraft. */
public final class FeatureStateTest {
    public static void main(String[] args) {
        for (boolean flight : new boolean[]{false, true}) {
            for (boolean inventory : new boolean[]{false, true}) {
                FeatureState state = new FeatureState(flight, inventory);
                check(state.change("flight", null), !flight, inventory);
                check(state.change("inventory", null), flight, !inventory);
                check(state.change(null, null), !(flight && inventory), !(flight && inventory));
                for (boolean enabled : new boolean[]{false, true}) {
                    check(state.change("flight", enabled), enabled, inventory);
                    check(state.change("inventory", enabled), flight, enabled);
                    check(state.change(null, enabled), enabled, enabled);
                }
            }
        }
        System.out.println("All 36 command-state combinations passed.");
    }

    private static void check(FeatureState actual, boolean flight, boolean inventory) {
        FeatureState expected = new FeatureState(flight, inventory);
        if (!actual.equals(expected)) throw new AssertionError("Expected " + expected + ", got " + actual);
    }
}
