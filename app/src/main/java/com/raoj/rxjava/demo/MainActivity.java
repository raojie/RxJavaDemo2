package com.raoj.rxjava.demo;

import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "RxJava2.0-Demo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /************************简单基础使用********************************/
//        //创建一个上游Observable
//        Observable<Integer> observable = new Observable.create(new ObservableOnSubscribe<Integer>() {
//            @Override
//            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
//                e.onNext(1);
//                e.onNext(2);
//                e.onNext(3);
//                e.onComplete();
//            }
//        });
//        //创建一个下游Observer
//        Observer<Integer> observer = new Observer<Integer>() {
//            @Override
//            public void onSubscribe(Disposable d) {
//                Log.d(TAG,"onSubscribe");
//            }
//
//            @Override
//            public void onNext(Integer value) {
//                Log.d(TAG, ""+ value);
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                Log.d(TAG, "onError:"+ e);
//            }
//
//            @Override
//            public void onComplete() {
//                Log.d(TAG, "onComplete");
//            }
//        };
//        //建立连接
//        observable.subscribe(observer);
        /****************************************************************/

        /************************简单操作********************************/
//        Observable.create(new ObservableOnSubscribe<Integer>() {
//            @Override
//            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
//                e.onNext(1);
//                e.onNext(2);
//                e.onNext(3);
//                e.onComplete();
//            }
//        }).subscribe(new Observer<Integer>() {
//            @Override
//            public void onSubscribe(Disposable d) {
//                Log.d(TAG,"onSubscribe");
//            }
//
//            @Override
//            public void onNext(Integer value) {
//                Log.d(TAG, ""+ value);
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                Log.d(TAG, "onError:"+ e);
//            }
//
//            @Override
//            public void onComplete() {
//                Log.d(TAG, "onComplete");
//            }
//        });
        /****************************************************************/

        /**
         * 接下来解释一下其中两个陌生的玩意：ObservableEmitter和Disposable.
         * ObservableEmitter： Emitter是发射器的意思，那就很好猜了，这个就是用来发出事件的，它可以发出三种类型的事件，
         * 通过调用emitter的onNext(T value)、onComplete()和onError(Throwable error)就可以分别发出next事件、complete事件和error事件。
         * 但是，请注意，并不意味着你可以随意乱七八糟发射事件，需要满足一定的规则：
         * 上游可以发送无限个onNext, 下游也可以接收无限个onNext.
         * 当上游发送了一个onComplete后, 上游onComplete之后的事件将会继续发送, 而下游收到onComplete事件之后将不再继续接收事件.
         * 当上游发送了一个onError后, 上游onError之后的事件将继续发送, 而下游收到onError事件之后将不再继续接收事件.
         * 上游可以不发送onComplete或onError.
         * 最为关键的是onComplete和onError必须唯一并且互斥, 即不能发多个onComplete, 也不能发多个onError, 也不能先发一个onComplete, 然后再发一个onError, 反之亦然
         * 注: 关于onComplete和onError唯一并且互斥这一点, 是需要自行在代码中进行控制, 如果你的代码逻辑中违背了这个规则, 并不一定会导致程序崩溃.
         * 比如发送多个onComplete是可以正常运行的, 依然是收到第一个onComplete就不再接收了, 但若是发送多个onError, 则收到第二个onError事件会导致程序会崩溃.
         */

        /*来看个例子, 我们让上游依次发送1,2,3,complete,4,在下游收到第二个事件之后, 切断水管, 看看运行结果:*/
//        Observable.create(new ObservableOnSubscribe<Integer>() {
//            @Override
//            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
//                Log.d(TAG, "emitter 1");
//                emitter.onNext(1);
//                Log.d(TAG, "emitter 2");
//                emitter.onNext(2);
//                Log.d(TAG, "emitter 3");
//                emitter.onNext(3);
//                Log.d(TAG, "emitter complete");
//                emitter.onComplete();
//                Log.d(TAG, "emitter 4");
//                emitter.onNext(4);
//            }
//        }).subscribe(new Observer<Integer>() {
//            private Disposable mDisposable;
//            private int i;
//
//            @Override
//            public void onSubscribe(Disposable d) {
//                Log.d(TAG, "onSubscribe");
//                mDisposable = d;
//            }
//
//            @Override
//            public void onNext(Integer value) {
//                Log.d(TAG, "" + value);
//                i++;
//                if (i==2){
//                    Log.d(TAG,"disposable");
//                    mDisposable.dispose();
//                    Log.d(TAG, "isDisposable:" + mDisposable.isDisposed());
//                }
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                Log.d(TAG, "onError:" + e);
//            }
//
//            @Override
//            public void onComplete() {
//                Log.d(TAG, "onComplete");
//            }
//        });

        /**
         * 从运行结果我们看到, 在收到onNext 2这个事件后, 切断了水管, 但是上游仍然发送了3, complete, 4这几个事件,
         * 而且上游并没有因为发送了onComplete而停止. 同时可以看到下游的onSubscribe()方法是最先调用的.
         * Disposable的用处不止这些, 后面讲解到了线程的调度之后, 我们会发现它的重要性. 随着后续深入的讲解, 我们会在更多的地方发现它的身影.
         *
         */

        /**
         * 不带任何参数的subscribe() 表示下游不关心任何事件,你上游尽管发你的数据去吧, 老子可不管你发什么.
         * 带有一个Consumer参数的方法表示下游只关心onNext事件, 其他的事件我假装没看见, 因此我们如果只需要onNext事件可以这么写:
         */
//        Observable.create(new ObservableOnSubscribe<Integer>() {
//            @Override
//            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
//                Log.d(TAG, "emitter 1");
//                emitter.onNext(1);
//                Log.d(TAG, "emitter 2");
//                emitter.onNext(2);
//                Log.d(TAG, "emitter 3");
//                emitter.onNext(3);
//                Log.d(TAG, "emitter complete");
//                emitter.onComplete();
//                Log.d(TAG, "emitter 4");
//                emitter.onNext(4);
//            }
//        }).subscribe(new Consumer<Integer>() {
//            @Override
//            public void accept(Integer integer) throws Exception {
//                Log.d(TAG, "onNext:" + integer);
//            }
//        });


        /**************************Android线程正常工作********************************/
//        Log.d(TAG, Thread.currentThread().getName());
        //结果是----D/RxJava2.0-Demo: main
        /**************************Rxjava线程正常工作********************************/
//        /**
//         * 回到RxJava中, 当我们在主线程中去创建一个上游Observable来发送事件, 则这个上游默认就在主线程发送事件.
//         * 当我们在主线程去创建一个下游Observer来接收事件, 则这个下游默认就在主线程中接收事件, 来看段代码:
//         */
//        Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
//            @Override
//            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
//                Log.d(TAG, "Observable thread is:" + Thread.currentThread().getName());
//                Log.d(TAG, "emit 1");
//                e.onNext(1);
//            }
//        });
//
//        Consumer<Integer> consumer = new Consumer<Integer>() {
//            @Override
//            public void accept(Integer integer) throws Exception {
//                Log.d(TAG, "Observer thread is:" + Thread.currentThread().getName());
//                Log.d(TAG, "onNext:" + integer);
//            }
//        };
//        observable.subscribe(consumer);
//
//        //结果是
//        // D/RxJava2.0-Demo: Observable thread is:main
//        // D/RxJava2.0-Demo: emit 1
//        // D/RxJava2.0-Demo: Observer thread is:main
//        // D/RxJava2.0-Demo: onNext:1
//        //这就验证了刚才所说, 上下游默认是在同一个线程工作.

        //这样肯定是满足不了我们的需求的, 我们更多想要的是这么一种情况, 在子线程中做耗时的操作, 然后回到主线程中来操作UI
        //要达到这个目的, 我们需要先改变上游发送事件的线程, 让它去子线程中发送事件, 然后再改变下游的线程,
        // 让它去主线程接收事件. 通过RxJava内置的线程调度器可以很轻松的做到这一点. 接下来看一段代码:
        Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                Log.d(TAG, "Observable thread is:" + Thread.currentThread().getName());
                Log.d(TAG, "emit 1");
                e.onNext(1);
            }
        });

        Consumer<Integer> consumer = new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.d(TAG, "Observer thread is:" + Thread.currentThread().getName());
                Log.d(TAG, "onNext:" + integer);
            }
        };
        observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(consumer);
        //运行结果---
        // D/RxJava2.0-Demo: Observable thread is:RxNewThreadScheduler-1
        // D/RxJava2.0-Demo: emit 1
        // D/RxJava2.0-Demo: Observer thread is:main
        // D/RxJava2.0-Demo: onNext:1
        //可以看到, 上游发送事件的线程的确改变了, 是在一个叫RxNewThreadScheduler-2的线程中发送的事件,
        // 而下游仍然在主线程中接收事件, 这说明我们的目的达成了, 接下来看看是如何做到的.
        //作为一个初学者的入门教程, 并不会贴出一大堆源码来分析, 因此只需要让大家记住几个要点, 已达到如何正确的去使用这个目的才是我们的目标.
        //简单的来说, subscribeOn() 指定的是上游发送事件的线程, observeOn() 指定的是下游接收事件的线程.
        //多次指定上游的线程只有第一次指定的有效, 也就是说多次调用subscribeOn() 只有第一次的有效, 其余的会被忽略.
        //多次指定下游的线程是可以的, 也就是说每调用一次observeOn() , 下游的线程就会切换一次.
    }
}
