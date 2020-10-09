package com.example.mvvmappapplication.utils;

import androidx.annotation.MainThread;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.util.concurrent.atomic.AtomicBoolean;

import timber.log.Timber;

/**
 * 생명주기에 안전하게 이벤트를 처리
 */
public class SingleLiveEvent<T> extends MutableLiveData<T> {

    private final AtomicBoolean mPending = new AtomicBoolean(false);

    @MainThread
    public void observe(LifecycleOwner owner, final Observer<? super T> observer) {

        if (hasActiveObservers()) {
            Timber.w("여러 Observer가 존재하지만, 단 하나만 알림을 받을 수 있다.");
        }

        super.observe(owner, t -> {
            if (mPending.compareAndSet(true, false)) {
                observer.onChanged(t);
            }
        });
    }
    /**
     * LiveData로써 들고있는 데이터의 값을 변경하는 함수.
     * 여기서는 pending(AtomicBoolean)의 변수는 true로 바꾸어
     * observe내의 if문을 처리할 수 있도록 하였음.
     */
    @MainThread
    public void setValue(@Nullable T t) {
        mPending.set(true);
        super.setValue(t);
    }

    /**
     * 즉 postValue나 setValue, call등을 통하여 setValue 함수를 거쳐야만이 Observer을 통하여 데이터를 전달 할 수 있으며,
     * 이는 Configuration Changed 등의 LifeCycleOwner의 재활성화 상태가 와도 원치 않는 이벤트가 일어나는 것을 방지할 수 있도록 해줍니다.
     */
    @MainThread
    public void call() {
        setValue(null);
    }
}