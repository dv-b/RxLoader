package ru.arturvasilov.rxloader;

import android.content.Context;
import android.support.v4.app.LoaderManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

import rx.Observable;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;

/**
 * @author Artur Vasilov
 */
@RunWith(JUnit4.class)
public class RxLoaderTest {

    private LifecycleHandler mLifecycleHandler;

    @Before
    public void setUp() throws Exception {
        Context context = Mockito.mock(Context.class);
        LoaderManager loaderManager = new MockedLoaderManager();
        mLifecycleHandler = LoaderLifecycleHandler.create(context, loaderManager);
    }

    @Test
    public void testObservableExecuted() throws Exception {
        TestSubscriber<Integer> subscriber = new TestSubscriber<>();
        Observable.just(1, 2, 3)
                .compose(RxSchedulers.<Integer>async(Schedulers.io(), Schedulers.computation()))
                .compose(mLifecycleHandler.<Integer>load(1))
                .subscribe(subscriber);

        subscriber.assertValues(1, 2, 3);
    }

    @Test
    public void testObservableEmpty() throws Exception {
        TestSubscriber<Object> subscriber = new TestSubscriber<>();
        Observable.empty()
                .compose(mLifecycleHandler.load(1))
                .subscribe(subscriber);

        subscriber.assertCompleted();
    }
}