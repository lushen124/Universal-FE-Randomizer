package ui.common;

import util.Bundle;
import util.OptionRecorder;

/**
 * Interface for any type of view class that can be initialized from an OptionBundle
 */
public interface Preloadable {

    default void preloadOptions(Bundle bundle) {
        if (bundle instanceof OptionRecorder.GBAOptionBundle) {
            preloadOptions((OptionRecorder.GBAOptionBundle) bundle);
        } else if (bundle instanceof OptionRecorder.FE9OptionBundle) {
            preloadOptions((OptionRecorder.FE9OptionBundle) bundle);
        } else if (bundle instanceof OptionRecorder.FE4OptionBundle) {
            preloadOptions((OptionRecorder.FE4OptionBundle) bundle);
        }
    }

    default void updateOptionBundle(Bundle bundle) {
        if (bundle instanceof OptionRecorder.GBAOptionBundle) {
            updateOptionBundle((OptionRecorder.GBAOptionBundle) bundle);
        } else if (bundle instanceof OptionRecorder.FE9OptionBundle) {
            updateOptionBundle((OptionRecorder.FE9OptionBundle) bundle);
        } else if (bundle instanceof OptionRecorder.FE4OptionBundle) {
            updateOptionBundle((OptionRecorder.FE4OptionBundle) bundle);
        }
    }

    /**
     * Called to preload the options for GBAFE Tabs
     */
    default void preloadOptions(OptionRecorder.GBAOptionBundle bundle) {
        throw new UnsupportedOperationException();
    }

    default void updateOptionBundle(OptionRecorder.GBAOptionBundle bundle) {
        throw new UnsupportedOperationException();
    }

    /**
     * Called to preload the options for FE4 Tabs
     */
    default void preloadOptions(OptionRecorder.FE4OptionBundle bundle) {
        throw new UnsupportedOperationException();
    }

    default void updateOptionBundle(OptionRecorder.FE4OptionBundle bundle) {
        throw new UnsupportedOperationException();
    }

    /**
     * Called to preload the options for FE9 Tabs
     */
    default void preloadOptions(OptionRecorder.FE9OptionBundle bundle) {
        throw new UnsupportedOperationException();
    }

    default void updateOptionBundle(OptionRecorder.FE9OptionBundle bundle) {
        throw new UnsupportedOperationException();
    }
}
