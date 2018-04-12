package piuk.blockchain.android.injection;

import android.content.Context;

import piuk.blockchain.androidcore.injection.ApiModule;
import piuk.blockchain.androidcoreui.injector.ContextModule;

/**
 * Created by adambennett on 08/08/2016.
 */

public enum Injector {

    INSTANCE;

    private ApplicationComponent applicationComponent;
    private PresenterComponent presenterComponent;

    public static Injector getInstance() {
        return INSTANCE;
    }

    public void init(Context applicationContext) {
        ApplicationModule applicationModule = new ApplicationModule();
        ApiModule apiModule = new ApiModule();
        ContextModule contextModule = new ContextModule(applicationContext);

        initAppComponent(applicationModule, apiModule, contextModule);
    }

    protected void initAppComponent(ApplicationModule applicationModule,
                                    ApiModule apiModule,
                                    ContextModule contextModule) {
        applicationComponent = DaggerApplicationComponent.builder()
                .contextModule(contextModule)
                .applicationModule(applicationModule)
                .apiModule(apiModule)
                .build();

        getPresenterComponent();
    }

    public ApplicationComponent getAppComponent() {
        return applicationComponent;
    }

    public PresenterComponent getPresenterComponent() {
        if (presenterComponent == null) {
            presenterComponent = applicationComponent.presenterComponent();
        }
        return presenterComponent;
    }

}
