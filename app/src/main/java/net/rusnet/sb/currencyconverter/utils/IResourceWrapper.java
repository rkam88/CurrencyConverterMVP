package net.rusnet.sb.currencyconverter.utils;

import androidx.annotation.StringRes;

/**
 * @author Evgeny Chumak
 **/
public interface IResourceWrapper {

    String getString(@StringRes int resId);

    String getString(@StringRes int resId, Object... formatArgs);
}
