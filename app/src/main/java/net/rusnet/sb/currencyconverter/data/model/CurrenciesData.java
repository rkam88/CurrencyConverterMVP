package net.rusnet.sb.currencyconverter.data.model;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;
import java.util.List;

/**
 * Информация о валютах и курсах конвертации
 *
 * @author Evgeny Chumak
 **/
@Root(name = "ValCurs", strict = false)
public class CurrenciesData {

    @ElementList(inline = true)
    private List<Currency> mCurrencies;

    public List<Currency> getCurrencies() {
        return new ArrayList<>(mCurrencies);
    }
}
