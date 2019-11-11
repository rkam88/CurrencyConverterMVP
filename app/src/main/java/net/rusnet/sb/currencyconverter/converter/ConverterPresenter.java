package net.rusnet.sb.currencyconverter.converter;

import net.rusnet.sb.currencyconverter.R;
import net.rusnet.sb.currencyconverter.data.CurrenciesRepository;
import net.rusnet.sb.currencyconverter.data.model.Currency;
import net.rusnet.sb.currencyconverter.utils.IResourceWrapper;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ConverterPresenter {

    private static final int SCALE = 2;

    private WeakReference<ConverterViewContract> mConverterActivity;
    private CurrenciesRepository mCurrenciesRepository;
    private IResourceWrapper mResourceWrapper;

    private List<Currency> mCurrencyList;
    private final Currency mRub;

    private int fromCurrencyPosition;
    private int toCurrencyPosition;

    public ConverterPresenter(ConverterViewContract converterActivity, IResourceWrapper resourceWrapper) {
        mConverterActivity = new WeakReference<>(converterActivity);
        mResourceWrapper = resourceWrapper;
        mCurrenciesRepository = new CurrenciesRepository();
        mRub = getRubCurrency();
    }

    public void loadCurrencies() {
        CurrenciesRepository.OnLoadingFinishListener onLoadingFinishListener =
                new CurrenciesRepository.OnLoadingFinishListener() {
                    @Override
                    public void onFinish(List<Currency> currencyList) {
                        if (mConverterActivity.get() != null) {
                            if (currencyList != null) {
                                if (!currencyList.contains(mRub)) {
                                    currencyList.add(0, mRub);
                                }
                                mCurrencyList = new ArrayList<>(currencyList);

                                List<String> currencyNames = new ArrayList<>();
                                for (Currency currency : mCurrencyList) {
                                    currencyNames.add(currency.getName());
                                }
                                mConverterActivity.get().updateSpinners(currencyNames);
                            } else {
                                mConverterActivity.get().showLoadErrorMessage();
                            }

                        }
                    }
                };
        mCurrenciesRepository.loadCurrencies(onLoadingFinishListener);
    }

    public void setFromCurrencyPosition(int fromCurrencyPosition) {
        this.fromCurrencyPosition = fromCurrencyPosition;
        updateViewConversionRate();
    }

    public void setToCurrencyPosition(int toCurrencyPosition) {
        this.toCurrencyPosition = toCurrencyPosition;
        updateViewConversionRate();
    }

    public void calculateResult(String amount) {
        if (mConverterActivity.get() == null) return;

        BigDecimal amountToConvert = parseInput(amount);

        if (amountToConvert == null) {
            mConverterActivity.get().showInputErrorMessage();
        } else {
            Currency fromCurrency = mCurrencyList.get(fromCurrencyPosition);
            Currency toCurrency = mCurrencyList.get(toCurrencyPosition);
            BigDecimal rate = calculateRate(fromCurrency.getValue(), toCurrency.getValue());
            BigDecimal result = amountToConvert.multiply(rate).setScale(SCALE, BigDecimal.ROUND_HALF_UP);
            mConverterActivity.get().showResult(result.toString(), toCurrency.getName());
        }

    }

    public void detachView() {
        mConverterActivity.clear();
    }

    private BigDecimal parseInput(String amount) {
        BigDecimal result;
        if (amount == null) {
            result = null;
        } else {
            try {
                result = new BigDecimal(amount);
            } catch (NumberFormatException e) {
                result = null;
            }
        }
        return result;
    }

    private Currency getRubCurrency() {
        return new Currency(
                "rub_id",
                643,
                "RUB",
                1,
                mResourceWrapper.getString(R.string.russian_ruble),
                BigDecimal.ONE
        );
    }

    private void updateViewConversionRate() {
        if (mConverterActivity.get() != null) {
            Currency fromCurrency = mCurrencyList.get(fromCurrencyPosition);
            Currency toCurrency = mCurrencyList.get(toCurrencyPosition);
            mConverterActivity.get().updateConversionRate(
                    calculateRate(fromCurrency.getValue(), toCurrency.getValue()).toString(),
                    fromCurrency.getCharCode(),
                    toCurrency.getCharCode()
            );
            fromCurrency.getValue();
        }
    }

    private BigDecimal calculateRate(BigDecimal from, BigDecimal to) {
        BigDecimal result;
        result = from.divide(to, SCALE, BigDecimal.ROUND_HALF_UP);
        return result;

    }

}