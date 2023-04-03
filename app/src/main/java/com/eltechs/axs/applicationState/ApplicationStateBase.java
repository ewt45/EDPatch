package com.eltechs.axs.applicationState;

public interface ApplicationStateBase<StateClass extends ApplicationStateBase<StateClass>>
        extends StartupActionsCollectionAware<StateClass>, EnvironmentAware, CurrentActivityAware, DroidApplicationContextAware {
}
