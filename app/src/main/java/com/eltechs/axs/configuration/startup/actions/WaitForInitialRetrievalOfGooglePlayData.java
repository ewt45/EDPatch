package com.eltechs.axs.configuration.startup.actions;

import com.eltechs.axs.applicationState.PurchasableComponentsCollectionAware;
import com.eltechs.axs.helpers.Assert;
import com.eltechs.axs.payments.GooglePlayInteractionCompletionCallback;
import com.eltechs.axs.payments.PurchasableComponentsCollection;
import com.eltechs.axs.payments.impl.GooglePlayInteractionState;

public class WaitForInitialRetrievalOfGooglePlayData<StateClass extends PurchasableComponentsCollectionAware> extends AbstractStartupAction<StateClass> {
    @Override // com.eltechs.axs.configuration.startup.StartupAction
    public void execute() {
        final PurchasableComponentsCollection coll = getApplicationState().getPurchasableComponentsCollection();
        coll.addGooglePlayInteractionCompletionCallback(() -> {
            GooglePlayInteractionState state = coll.getGooglePlayInteractionState();
            if (state == GooglePlayInteractionState.HAVE_DATA_LOCALLY) {
                sendDone();
            } else if (state == GooglePlayInteractionState.ERROR_OCCURRED) {
                sendError(coll.getErrorMsg());
            } else {
                Assert.state(false, "Unexpected GooglePlayInteractionState" + state);
            }
        });
    }
}