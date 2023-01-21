package com.eltechs.axs.activities;

import com.eltechs.axs.applicationState.ApplicationStateBase;
import com.eltechs.axs.applicationState.PurchasableComponentsCollectionAware;
import com.eltechs.axs.applicationState.SelectedExecutableFileAware;
import com.eltechs.axs.applicationState.XServerDisplayActivityConfigurationAware;
import com.eltechs.axs.widgets.actions.AbstractAction;

import java.util.List;

public class XServerDisplayActivity <StateClass extends ApplicationStateBase<StateClass> & PurchasableComponentsCollectionAware & XServerDisplayActivityConfigurationAware & SelectedExecutableFileAware<StateClass>> extends FrameworkActivity<StateClass> {
    public void addDefaultPopupMenu(List<AbstractAction> asList) {
    }
}
