package net.rusnet.sb.currencyconverter.converter;

import java.util.List;

public interface ConverterViewContract {

    void updateSpinners(List<String> currencyNames);

    void updateConversionRate(String conversionRate,
                              String currencyCodeFrom,
                              String currencyCodeTo
    );

    void showResult(String result, String currencyNameTo);

    void showLoadErrorMessage();

    void showInputErrorMessage();

}
