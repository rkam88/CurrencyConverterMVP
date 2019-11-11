package net.rusnet.sb.currencyconverter.data;

import android.os.AsyncTask;

import androidx.annotation.NonNull;

import net.rusnet.sb.currencyconverter.data.model.CurrenciesData;
import net.rusnet.sb.currencyconverter.data.model.Currency;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.convert.AnnotationStrategy;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.strategy.Strategy;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class CurrenciesRepository {

    private static final String BASE_URL = "http://www.cbr.ru";
    private final IRatesService mRatesApi;

    public CurrenciesRepository() {
        Strategy strategy = new AnnotationStrategy();
        Serializer serializer = new Persister(strategy);
        // noinspection deprecation
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(SimpleXmlConverterFactory.create(serializer))
                .build();
        mRatesApi = retrofit.create(IRatesService.class);
    }

    public void loadCurrencies(@NonNull OnLoadingFinishListener onLoadingFinishListener) {
        LoadingCurrenciesAsyncTask loadingCurrenciesAsyncTask =
                new LoadingCurrenciesAsyncTask(onLoadingFinishListener);
        loadingCurrenciesAsyncTask.execute();
    }

    private List<Currency> loadCurrencies() {
        Call<CurrenciesData> listCall = mRatesApi.loadCurrencies();
        Response<CurrenciesData> response = null;
        try {
            response = listCall.execute();
            if (response.body() == null || response.errorBody() != null) {
                return null;
            }
            List<Currency> currencies = response.body().getCurrencies();
            return currencies;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private class LoadingCurrenciesAsyncTask extends AsyncTask<Void, Void, List<Currency>> {

        private final OnLoadingFinishListener mOnLoadingFinishListener;

        public LoadingCurrenciesAsyncTask(OnLoadingFinishListener onLoadingFinishListener) {
            mOnLoadingFinishListener = onLoadingFinishListener;
        }

        @Override
        protected void onPostExecute(List<Currency> currencyList) {
            super.onPostExecute(currencyList);

            mOnLoadingFinishListener.onFinish(currencyList);
        }

        @Override
        protected List<Currency> doInBackground(Void... voids) {
            return loadCurrencies();
        }
    }

    public interface OnLoadingFinishListener {
        void onFinish(List<Currency> currencyList);
    }
}
