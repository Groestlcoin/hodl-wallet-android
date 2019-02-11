package co.hodlwallet.tools.threads;

import android.app.Activity;
import android.os.AsyncTask;

import co.hodlwallet.HodlApp;
import co.hodlwallet.BuildConfig;
import co.hodlwallet.R;
import co.hodlwallet.presenter.customviews.BRDialogView;
import co.hodlwallet.presenter.entities.ImportPrivKeyEntity;
import co.hodlwallet.tools.animation.BRDialog;
import co.hodlwallet.tools.manager.BRSharedPrefs;
import co.hodlwallet.tools.util.BRCurrency;
import co.hodlwallet.tools.util.BRExchange;
import co.hodlwallet.wallet.BRWalletManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;

/**
 * BreadWallet
 * <p/>
 * Created by Mihail Gutan <mihail@breadwallet.com> on 6/2/16.
 * Copyright (c) 2016 breadwallet LLC
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

public class ImportPrivKeyTask extends AsyncTask<String, String, String> {
    public static final String TAG = ImportPrivKeyTask.class.getName();
    public static String UNSPENT_URL;
    private Activity app;
    private String key;
    private ImportPrivKeyEntity importPrivKeyEntity;

    public ImportPrivKeyTask(Activity activity) {
        app = activity;

        UNSPENT_URL = BuildConfig.BITCOIN_TESTNET ? "https://bitcore.hodlwallet.com/api/BTC/testnet/wallet/" : "https://bitcore.hodlwallet.com/api/BTC/mainnet/wallet/";
    }

    @Override
    protected String doInBackground(String... params) {
        if (params.length == 0) return null;
        key = params[0];
        if (key == null || key.isEmpty() || app == null) return null;
        String tmpAddrs = BRWalletManager.getInstance().getAddressFromPrivKey(key);
        String url = UNSPENT_URL + tmpAddrs + "/transactions";
        importPrivKeyEntity = createTx(url);
        if (importPrivKeyEntity == null) {
            app.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    BRDialog.showCustomDialog(app, app.getString(R.string.JailbreakWarnings_title),
                            app.getString(R.string.Import_Error_empty), app.getString(R.string.Button_ok), null, new BRDialogView.BROnClickListener() {
                                @Override
                                public void onClick(BRDialogView brDialogView) {
                                    brDialogView.dismissWithAnimation();
                                }
                            }, null, null, 0);
                }
            });
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        if (importPrivKeyEntity == null) {
            return;
        }

//        String iso = BRSharedPrefs.getIso(app);

        String sentBits = BRCurrency.getFormattedCurrencyString(app, "BTC", BRExchange.getAmountFromSatoshis(app, "BTC", new BigDecimal(importPrivKeyEntity.getAmount())));
//        String sentExchange = BRCurrency.getFormattedCurrencyString(app, iso, BRExchange.getAmountFromSatoshis(app, iso, new BigDecimal(importPrivKeyEntity.getAmount())));

        String feeBits = BRCurrency.getFormattedCurrencyString(app, "BTC", BRExchange.getAmountFromSatoshis(app, "BTC", new BigDecimal(importPrivKeyEntity.getFee())));
//        String feeExchange = BRCurrency.getFormattedCurrencyString(app, iso, BRExchange.getAmountFromSatoshis(app, iso, new BigDecimal(importPrivKeyEntity.getFee())));

        if (app == null || importPrivKeyEntity == null) return;
        String message = String.format(app.getString(R.string.Import_confirm), sentBits, feeBits);
        String posButton = String.format("%s (%s)", sentBits, feeBits);
        BRDialog.showCustomDialog(app, "", message, posButton, app.getString(R.string.Button_cancel), new BRDialogView.BROnClickListener() {
            @Override
            public void onClick(BRDialogView brDialogView) {
                BRExecutor.getInstance().forLightWeightBackgroundTasks().execute(new Runnable() {
                    @Override
                    public void run() {
                        boolean result = BRWalletManager.getInstance().confirmKeySweep(importPrivKeyEntity.getTx(), key);
                        if (!result) {
                            app.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    BRDialog.showCustomDialog(app, app.getString(R.string.JailbreakWarnings_title),
                                            app.getString(R.string.Import_Error_notValid), app.getString(R.string.Button_ok), null, new BRDialogView.BROnClickListener() {
                                                @Override
                                                public void onClick(BRDialogView brDialogView) {
                                                    brDialogView.dismissWithAnimation();
                                                }
                                            }, null, null, 0);
                                }
                            });

                        }
                    }
                });

                brDialogView.dismissWithAnimation();

            }
        }, new BRDialogView.BROnClickListener() {
            @Override
            public void onClick(BRDialogView brDialogView) {
                brDialogView.dismissWithAnimation();
            }
        }, null, 0);
        super.onPostExecute(s);
    }

    public static ImportPrivKeyEntity createTx(String url) {
        if (url == null || url.isEmpty()) return null;
        String jsonString = callURL(url);
        if (jsonString == null || jsonString.isEmpty()) return null;
        ImportPrivKeyEntity result = null;
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(jsonString);
            int length = jsonArray.length();
            if (length > 0)
                BRWalletManager.getInstance().createInputArray();

            for (int i = 0; i < length; i++) {
                JSONObject obj = jsonArray.getJSONObject(i);

                // Parsing according to: https://github.com/bitpay/bitcore/blob/master/packages/bitcore-node/docs/api-documentation.md#get-wallet-transactions
                // We should get an array with these attributes:
                //
                // [
                //  {
                //      "id":"5c34b35d69d5562c2fc43e8c",
                //      "txid":"0e3e2357e806b6cdb1f70b54c3a3a17b6714ee1f0e68bebb44a74b1efd512098",
                //      "fee":0,"size":134,
                //      "category":"receive",
                //      "satoshis":5000000000,
                //      "height":1,
                //      "address":"12c6DSiU4Rq3P4ZxziKxzrL5LmMBrzjrJX",
                //      "outputIndex":0,
                //      "blockTime":"2009-01-09T02:54:25.000Z"
                //  }
                //]
                String txid = obj.getString("txid");
                int vout = obj.getInt("outputIndex");
                // FIXME Breadwallet core API should have been renamed from the C project now this is complicated to use hodlwallet namespace.
                // FIXME cont... For example the namespace cannot be imported at the top of this file.
                //com.breadwallet.core.BRCoreAddress address = new com.breadwallet.core.BRCoreAddress(obj.getString("address"));
                //byte[] scriptPubKey = address.getPubKeyScript();
                long amount = obj.getLong("satoshis");
                byte[] txidBytes = hexStringToByteArray(txid);
                BRWalletManager.getInstance().addInputToPrivKeyTx(txidBytes, vout, null, amount);
            }

            result = BRWalletManager.getInstance().getPrivKeyObject();

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    private static String callURL(String myURL) {
//        System.out.println("Requested URL_EA:" + myURL);
        StringBuilder sb = new StringBuilder();
        URLConnection urlConn = null;
        InputStreamReader in = null;
        try {
            URL url = new URL(myURL);
            urlConn = url.openConnection();
            if (urlConn != null)
                urlConn.setReadTimeout(60 * 1000);
            if (urlConn != null && urlConn.getInputStream() != null) {
                in = new InputStreamReader(urlConn.getInputStream(),
                        Charset.defaultCharset());
                BufferedReader bufferedReader = new BufferedReader(in);

                int cp;
                while ((cp = bufferedReader.read()) != -1) {
                    sb.append((char) cp);
                }
                bufferedReader.close();
            }
            assert in != null;
            in.close();
        } catch (Exception e) {
            return null;
        }

        return sb.toString();
    }
}
