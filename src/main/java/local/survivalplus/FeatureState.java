package local.survivalplus;

/** Independent saved settings; the root toggle enables both unless both are already enabled. */
public record FeatureState(boolean flight, boolean inventory) {
    public FeatureState change(String feature, Boolean requested) {
        if ("flight".equals(feature)) {
            return new FeatureState(requested == null ? !flight : requested, inventory);
        }
        if ("inventory".equals(feature)) {
            return new FeatureState(flight, requested == null ? !inventory : requested);
        }
        boolean enabled = requested == null ? !(flight && inventory) : requested;
        return new FeatureState(enabled, enabled);
    }
}
