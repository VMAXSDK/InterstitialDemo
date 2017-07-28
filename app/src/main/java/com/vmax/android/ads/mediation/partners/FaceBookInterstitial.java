package com.vmax.android.ads.mediation.partners;


import android.content.Context;
import android.util.Log;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdSettings;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.vmax.android.ads.api.VmaxAdPartner;
import com.vmax.android.ads.util.Constants;

import java.util.Map;

/*
 * Tested with facebook SDK 4.24.0
 */

public class FaceBookInterstitial extends VmaxCustomAd implements
        InterstitialAdListener {
    private static final String PLACEMENT_ID_KEY = "placementid";

    private InterstitialAd mFacebookInterstitial;
    private VmaxCustomAdListener mInterstitialListener;
    public boolean LOGS_ENABLED = true;
    private VmaxAdPartner vmaxAdPartner;


    @Override
    public void loadAd(final Context context,
                       final VmaxCustomAdListener vmaxCustomAdListener,
                       final Map<String, Object> localExtras,
                       final Map<String, Object> serverExtras) {
        try {
            mInterstitialListener = vmaxCustomAdListener;

            final String placementId;
            if (extrasAreValid(serverExtras)) {
                placementId = serverExtras.get(PLACEMENT_ID_KEY).toString();
            } else {
                if(mInterstitialListener!=null)
                  mInterstitialListener.onAdFailed(Constants.AdError.ERROR_MANDATORY_PARAM_MISSING,"FaceBookInterstitial Mandatory parameters missing");
                return;
            }
            if (localExtras != null) {

                if (localExtras.containsKey("vmaxAdPartner"))
                {
                    vmaxAdPartner = (VmaxAdPartner)localExtras.get("vmaxAdPartner");
                    Log.d("vmax","VmaxAdPartnerName "+ "FaceBook");
                    vmaxAdPartner.setPartnerName("FaceBook");
                    Log.d("vmax","VmaxAdPartnerSDKVersion "+ "4.24.0");
                    vmaxAdPartner.setPartnerSDKVersion("4.24.0");
                }

                if (localExtras.containsKey("test")) {

                    String[] mTestAvdIds = (String[]) localExtras
                            .get("test");
                    if (mTestAvdIds != null) {
                        for (int i = 0; i < mTestAvdIds.length; i++) {
                            if (LOGS_ENABLED) {
                                Log.i("vmax",
                                        "test devices: "
                                                + mTestAvdIds[i]);
                            }
                            AdSettings.addTestDevice(mTestAvdIds[i]);
                            if (LOGS_ENABLED) {
                                Log.i("vmax",
                                        "Test mode: "
                                                + AdSettings.isTestMode(context));
                            }
                        }
                    }
                }
            }

            mFacebookInterstitial = new InterstitialAd(context, placementId);
            AdSettings.setMediationService("VMAX");
            mFacebookInterstitial.setAdListener(this);
            mFacebookInterstitial.loadAd();
        } catch (Exception e) {
            if (mInterstitialListener != null) {
                mInterstitialListener.onAdFailed(Constants.AdError.ERROR_UNKNOWN,"FaceBookInterstitial "+e.getMessage());
            }
            e.printStackTrace();
            return;
        }
    }

    @Override
    public void showAd() {
        try {
            if (mFacebookInterstitial != null
                    && mFacebookInterstitial.isAdLoaded()) {

                mFacebookInterstitial.show();
            } else {
                if (LOGS_ENABLED) {
                    Log.d("vmax",
                            "Tried to show a Facebook interstitial ad before it finished loading. Please try again.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            mInterstitialListener.onAdFailed(Constants.AdError.ERROR_RENDITION_ERROR, "FaceBookInterstitial "+e.getMessage());
        }
    }

    @Override
    public void onInvalidate() {
        try {
            if (mFacebookInterstitial != null) {
                if (LOGS_ENABLED) {
                    Log.d("vmax", "Facebook Interstitial ad onInvalidate.");
                }
                mFacebookInterstitial.setAdListener(null);
                mFacebookInterstitial.destroy();
                mFacebookInterstitial = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLoggingImpression(Ad ad) {
        Log.d("vmax","facebook: onLoggingImpression()");
        if(mInterstitialListener!=null){
            mInterstitialListener.logMediationImpression();
        }
    }

    @Override
    public void onAdLoaded(final Ad ad) {
        if (LOGS_ENABLED) {
            Log.d("vmax", "Facebook interstitial ad loaded successfully.");

        }
        mInterstitialListener.onAdLoaded();
    }

    @Override
    public void onError(final Ad ad, final AdError error) {
        if (LOGS_ENABLED) {
            Log.d("vmax", "Facebook interstitial ad failed to load. error: "
                    + error.getErrorCode());
        }
        if(error.getErrorCode() == 1000)
            mInterstitialListener.onAdFailed(Constants.AdError.ERROR_NETWORK_ERROR, "FaceBookInterstitial "+error.getErrorMessage());
        else if (error.getErrorCode() == 1001)
            mInterstitialListener.onAdFailed(Constants.AdError.ERROR_NOFILL, "FaceBookInterstitial "+error.getErrorMessage());
        else if (error.getErrorCode() == 1002)
            mInterstitialListener.onAdFailed(Constants.AdError.ERROR_ADREQUEST_NOT_ALLOWED, "FaceBookInterstitial "+error.getErrorMessage());
        else if (error.getErrorCode() == 2000)
            mInterstitialListener.onAdFailed(Constants.AdError.ERROR_INTERNAL_SERVER, "FaceBookInterstitial "+error.getErrorMessage());
        else if (error.getErrorCode() == 2001)
            mInterstitialListener.onAdFailed(Constants.AdError.ERROR_UNKNOWN, "FaceBookInterstitial "+error.getErrorMessage());
        else if (error.getErrorCode() == 3001)
            mInterstitialListener.onAdFailed(Constants.AdError.ERROR_UNKNOWN, "FaceBookInterstitial "+error.getErrorMessage());
        else
            mInterstitialListener.onAdFailed(Constants.AdError.ERROR_UNKNOWN, "FaceBookInterstitial "+error.getErrorMessage());
        }


    @Override
    public void onInterstitialDisplayed(final Ad ad) {
        if (LOGS_ENABLED) {
            Log.d("vmax", "Showing Facebook interstitial ad.");
        }
        mInterstitialListener.onAdShown();
    }

    @Override
    public void onAdClicked(final Ad ad) {
        if (LOGS_ENABLED) {
            Log.d("vmax", "Facebook interstitial ad clicked.");
        }
        mInterstitialListener.onAdClicked();
    }

    @Override
    public void onInterstitialDismissed(final Ad ad) {
        if (LOGS_ENABLED) {
            Log.d("vmax", "Facebook interstitial ad dismissed.");
        }
        mInterstitialListener.onAdDismissed();
        onDestroy();
    }

    private boolean extrasAreValid(final Map<String, Object> serverExtras) {
        final String placementId = serverExtras.get(PLACEMENT_ID_KEY)
                .toString();
        return (placementId != null && placementId.length() > 0);
    }

    public void onPause() {

    }

    public void onResume() {

    }

    public void onDestroy() {
        if (mFacebookInterstitial != null) {
            if (LOGS_ENABLED) {
                Log.d("vmax", "Facebook Interstitial ad onDestroy.");
            }
            mFacebookInterstitial.destroy();
            mFacebookInterstitial = null;
        }
    }
}
