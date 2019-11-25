package vn.elite.fundamental.gui.tristate;

import javax.swing.JToggleButton.ToggleButtonModel;
import java.awt.event.ItemEvent;

import static vn.elite.fundamental.gui.tristate.TristateState.DESELECTED;
import static vn.elite.fundamental.gui.tristate.TristateState.INDETERMINATE;

public class TristateButtonModel extends ToggleButtonModel {
    private TristateState state = DESELECTED;

    public TristateButtonModel(TristateState state) {
        setState(state);
    }

    public TristateButtonModel() {
        this(DESELECTED);
    }

    public void setIndeterminate() {
        setState(INDETERMINATE);
    }

    public boolean isIndeterminate() {
        return state == INDETERMINATE;
    }

    // Overrides of superclass methods
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        // Restore state display
        displayState();
    }

    @Override
    public void setSelected(boolean selected) {
        setState(selected ? TristateState.SELECTED : DESELECTED);
    }

    // Empty overrides of superclass methods
    @Override
    public void setArmed(boolean b) {
    }

    @Override
    public void setPressed(boolean b) {
    }

    void iterateState() {
        setState(state.next());
    }

    private void displayState() {
        super.setSelected(state != DESELECTED);
        super.setArmed(state == INDETERMINATE);
        super.setPressed(state == INDETERMINATE);
    }

    public TristateState getState() {
        return state;
    }

    private void setState(TristateState state) {
        //Set internal state
        this.state = state;
        displayState();
        if (state == INDETERMINATE && isEnabled()) {
            // force the events to fire
            // Send ChangeEvent
            fireStateChanged();

            // Send ItemEvent
            int indeterminate = 3;
            fireItemStateChanged(new ItemEvent(this, ItemEvent.ITEM_STATE_CHANGED, this, indeterminate));
        }
    }
}
