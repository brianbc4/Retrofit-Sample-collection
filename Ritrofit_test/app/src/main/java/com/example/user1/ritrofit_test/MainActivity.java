package com.example.user1.ritrofit_test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.List;

import retrofit2.Call;
import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class MainActivity extends AppCompatActivity {
    ApiStores apiStores = AppClient.retrofit().create(ApiStores.class);
    CompositeSubscription mCompositeSubscription;
    List<Call> calls;
    TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        text = (TextView) findViewById(R.id.text);

        String cityId = "101190201";

        addSubscription(apiStores.loadDataByRetrofitRxjava(cityId),
                new Subscriber<MainModel>() {
                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        if (e instanceof HttpException) {
                            HttpException httpException = (HttpException) e;
                            //httpException.response().errorBody().string()
                            int code = httpException.code();
                            String msg = httpException.getMessage();
//                            LogUtil.d("code=" + code);
                            if (code == 504) {
                                msg = "网络不给力";
                            }
                            if (code == 502 || code == 404) {
                                msg = "服务器异常，请稍后再试";
                            }
//                            onFailure(msg);
                        } else {
//                            onFailure(e.getMessage());
                        }
//                        onFinish();
                    }

                    @Override
                    public void onNext(MainModel model) {
                        dataSuccess(model);
                    }

                    @Override
                    public void onCompleted() {
//                        onFinish();
                    }
                });


    }


    public void addSubscription(Observable observable, Subscriber subscriber) {
        if (mCompositeSubscription == null) {
            mCompositeSubscription = new CompositeSubscription();
        }
        mCompositeSubscription.add(observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber));
    }

    private void dataSuccess(MainModel model) {
        MainModel.WeatherinfoBean weatherinfo = model.getWeatherinfo();
        String showData = "城市: " + weatherinfo.getCity()
                + "風向: " + weatherinfo.getWD()
                + "風級: " + weatherinfo.getWS()
                + "發布時間: " + weatherinfo.getTime();
        text.setText(showData);
    }
}
